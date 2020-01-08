import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;

public class View extends Application {

    public static String[] args;

    protected static final int DEPTH = 50;
    protected static final int HEIGHT = 800;
    protected static final int WIDTH = 1000;
    protected static final int BOARD_SIZE = 700;
    protected static final int POPUP_SIZE = 400;

    protected Controller controller;
    protected GridPane grid;
    protected int n = 8;
    protected Pane surfacePane;
    protected RotateTransition surfacePaneRotation;
    protected Text displayTurn;
    protected Stage primaryStage;

    public double getSize() {
        return ((double) View.BOARD_SIZE) / this.n;
    }

    public static void highlightPane(StackPane pane) {
        pane.setStyle("-fx-background-image: url(/assets/dark_Wood_Texture.jpg); -fx-border-color: green; -fx-border-width: 5;");
    }

    public static void normalizePane(StackPane pane) {
        pane.setStyle("-fx-background-image: url(/assets/dark_Wood_Texture.jpg)");
    }

    protected void setupField(int i, int j) {
        StackPane field = new StackPane();
        field.setStyle("-fx-background-image: url(/assets/dark_Wood_Texture.jpg)");

        field.setPrefSize(this.getSize(), this.getSize());

        this.grid.add(field, i, j);
        field.setTranslateZ(0.01); // Bring field background to front

        this.controller.addField(new Point(i + 1, j + 1), field);
    }

    protected void setupFields() {
        for (int i = 0; i < n; i++) {
            for (int j = i % 2; j < n; j += 2) {
                this.setupField(i, j);
            }
        }
    }

    protected void setupGrid() {
        this.grid = new GridPane();

        this.grid.setMinHeight(View.BOARD_SIZE);
        this.grid.setMinWidth(View.BOARD_SIZE);
        this.grid.setMaxHeight(View.BOARD_SIZE);
        this.grid.setMaxWidth(View.BOARD_SIZE);
        this.grid.setTranslateZ(-View.DEPTH / 2.0);

        this.grid.setRotationAxis(Rotate.X_AXIS);
        this.grid.setRotate(180);

        this.grid.setStyle("-fx-background-image: url(/assets/light_Marble_Texture.jpg); -fx-background-size: cover;");

        this.grid.setPickOnBounds(false);

        this.surfacePane.getChildren().add(this.grid);

    }

    protected void setupSurface() {
        this.surfacePane = new StackPane();
        this.surfacePane.setPickOnBounds(false);

        // Setup board surface
        Box box = new Box();
        box.setWidth(View.BOARD_SIZE);
        box.setHeight(View.BOARD_SIZE);
        box.setDepth(View.DEPTH);
        box.setPickOnBounds(false);

        //texture
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(new Image(getClass().getResourceAsStream("/assets/light_Marble_Texture.jpg")));
        box.setMaterial(material);

        StackPane.setAlignment(box, Pos.CENTER);

        // Setup rotation
        this.surfacePaneRotation = new RotateTransition();
        this.surfacePaneRotation.setAxis(Rotate.Z_AXIS);
        this.surfacePaneRotation.setByAngle(180);
        this.surfacePaneRotation.setCycleCount(1);
        this.surfacePaneRotation.setDuration(Duration.millis(1000));
        this.surfacePaneRotation.setAutoReverse(false);
        this.surfacePaneRotation.setNode(this.surfacePane);

        this.surfacePane.getChildren().add(box);

        this.setupGrid();
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        // Handle n-argument
        if (View.args.length == 1) {
            int newN = Integer.parseInt(View.args[0]);

            if (newN >= 3 && newN <= 100) {
                this.n = newN;
            }
        }

        StackPane root = new StackPane();
        root.setMinSize(View.WIDTH, View.HEIGHT);
        root.setMaxSize(View.WIDTH, View.HEIGHT);


        this.displayTurn = new Text("whites turn");
        this.displayTurn.setStyle("-fx-font: 50 Arial;");
        this.displayTurn.setFill(Color.BLACK);

        StackPane textbox = new StackPane();
        textbox.setStyle("-fx-background-color: burlywood;");
        textbox.setMinHeight(80);
        textbox.setMinWidth(20);
        textbox.setMaxHeight(20);
        textbox.setMaxWidth(300);
        textbox.setStyle("-fx-border-color: gray; -fx-border-width: 4;");
        textbox.getChildren().add(this.displayTurn);

        primaryStage.setTitle("Checkers");

        Rectangle background = new Rectangle(View.WIDTH * 2, View.HEIGHT * 2);
        background.setFill(Color.web("antiquewhite"));
        background.setTranslateZ(500);

        StackPane boardContainer = new StackPane();
        boardContainer.setPrefSize(View.WIDTH, View.HEIGHT);

        boardContainer.setRotationAxis(Rotate.X_AXIS);
        boardContainer.setRotate(-50);
        boardContainer.setPickOnBounds(false);

        this.setupSurface();

        boardContainer.getChildren().add(this.surfacePane);

        root.getChildren().addAll(background, boardContainer, textbox);
        root.setPickOnBounds(false); // Pass through click events

        StackPane.setAlignment(background, Pos.CENTER);
        StackPane.setAlignment(textbox, Pos.TOP_CENTER);
        StackPane.setAlignment(this.surfacePane, Pos.CENTER);
        StackPane.setAlignment(this.displayTurn, Pos.CENTER);

        this.controller = new Controller(this, this.n, this.grid);

        this.setupFields();

        this.controller.setupPieces();

        Scene scene = new Scene(root, View.WIDTH, View.HEIGHT, true, null);
        scene.setCamera(new PerspectiveCamera());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void setupDisplayTurn(boolean isWhiteTurn) {
        this.displayTurn.setText(isWhiteTurn ? "Whites turn" : "Blacks turn");
    }

    public void displayWin(String whoWon) {
        this.primaryStage.setTitle("You won!");

        StackPane root = new StackPane();

        Button button = new Button("Close");
        button.setOnMouseClicked(e -> {
            this.primaryStage.close();
        });

        StackPane pane = new StackPane();
        pane.setStyle("-fx-background-image: url(/assets/confetti_Texture.jpg); -fx-background-size: cover; -fx-border-color: black; -fx-border-width: 5px;");
        pane.setMinSize(View.POPUP_SIZE, View.POPUP_SIZE);
        pane.setMaxSize(View.POPUP_SIZE, View.POPUP_SIZE);

        Label label = new Label(whoWon);
        label.setAlignment(Pos.BASELINE_CENTER);
        label.setMinWidth(300);
        label.setMinHeight(150);
        label.setStyle("-fx-font: 50 Arial");

        StackPane.setAlignment(pane, Pos.CENTER);
        StackPane.setAlignment(label, Pos.TOP_CENTER);
        StackPane.setAlignment(button, Pos.BOTTOM_CENTER);

        pane.getChildren().addAll(label, button);
        root.getChildren().add(pane);

        Scene scene = new Scene(root, View.WIDTH, View.HEIGHT);
        this.primaryStage.setScene(scene);
        this.primaryStage.show();
    }

    public void rotate() {
        this.surfacePaneRotation.play();
    }

    public static void main(String[] args) {
        View.args = args;

        launch(args);
    }

}
