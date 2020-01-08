import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

    protected static final int BOARD_SIZE = 700;
    protected static final int DEPTH = 50;
    protected static final int HEIGHT = 800;
    protected static final int POPUP_SIZE = 400;
    protected static final int WIDTH = 1000;

    protected static final String ASSET_GRID = "/assets/grid.png";
    protected static final String BACKGROUND_FIELD = "-fx-background-image: url(/assets/dark_wood.jpg);";

    protected Controller controller;
    protected int dimension = 8;
    protected Text displayTurn;
    protected GridPane grid;
    protected Pane surfacePane;
    protected RotateTransition surfacePaneRotation;
    protected Stage primaryStage;

    public static void main(String[] args) {
        View.args = args;

        launch(args);
    }

    protected void setupField(int i, int j) {
        StackPane field = new StackPane();
        field.setStyle(View.BACKGROUND_FIELD);

        field.setPrefSize(this.getSize(), this.getSize());

        this.grid.add(field, i, j);
        field.setTranslateZ(0.01); // Bring field background to front

        this.controller.addField(new Point(i + 1, j + 1), field);
    }

    protected void setupFields() {
        for (int i = 0; i < this.dimension; i++) {
            for (int j = i % 2; j < this.dimension; j += 2) {
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

        this.grid.setPickOnBounds(false);
        this.grid.setStyle("-fx-effect: null;");

        this.surfacePane.getChildren().add(this.grid);
    }

    protected void setupSurface() {
        this.surfacePane = new StackPane();
        this.surfacePane.setPickOnBounds(false);
        this.surfacePane.setStyle("-fx-effect: null;");

        // Setup board surface
        Box box = new Box();
        box.setWidth(View.BOARD_SIZE);
        box.setHeight(View.BOARD_SIZE);
        box.setDepth(View.DEPTH);
        box.setPickOnBounds(false);
        box.setStyle("-fx-effect: null;");

        //texture
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(new Image(getClass().getResourceAsStream(View.ASSET_GRID)));
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

    public double getSize() {
        return ((double) View.BOARD_SIZE) / this.dimension;
    }

    public void displayWin(String whoWon) {
        this.primaryStage.setTitle("You won!");

        StackPane root = new StackPane();

        Button button = new Button("Close");
        button.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #DAA520;");
        button.setOnMouseClicked(e -> this.primaryStage.close());

        StackPane pane = new StackPane();
        pane.setStyle("-fx-background-color: antiquewhite; -fx-border-color: #DAA520; -fx-border-width: 5px;");
        pane.setMinSize(View.POPUP_SIZE, View.POPUP_SIZE);
        pane.setMaxSize(View.POPUP_SIZE, View.POPUP_SIZE);

        Text text = new Text();
        text.setText(whoWon);
        text.setStyle("-fx-font: 70px Arial");

        StackPane.setAlignment(text, Pos.CENTER);
        StackPane.setAlignment(pane, Pos.CENTER);
        StackPane.setAlignment(button, Pos.BOTTOM_CENTER);

        pane.getChildren().addAll(text, button);
        root.getChildren().add(pane);

        Scene scene = new Scene(root, View.WIDTH, View.HEIGHT);
        this.primaryStage.setScene(scene);
        this.primaryStage.show();
    }

    public void highlightPane(StackPane pane) {
        int borderWidth = this.getSize() < 20 ? 2 : 5;
        pane.setStyle(View.BACKGROUND_FIELD + " -fx-border-color: green; -fx-border-width: " + borderWidth + ";");
    }

    public void normalizePane(StackPane pane) {
        pane.setStyle(View.BACKGROUND_FIELD);
    }

    public void setupDisplayTurn(boolean isWhiteTurn) {
        this.displayTurn.setText(isWhiteTurn ? "White's turn" : "Black's turn");
    }

    public void rotate() {
        this.surfacePaneRotation.play();
    }

    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Checkers");

        // Handle n-argument
        if (View.args.length == 1) {
            int newN = Integer.parseInt(View.args[0]);

            if (newN >= 3 && newN <= 100) {
                this.dimension = newN;
            }
        }

        StackPane root = new StackPane();
        root.setMinSize(View.WIDTH, View.HEIGHT);
        root.setMaxSize(View.WIDTH, View.HEIGHT);

        this.displayTurn = new Text();
        this.displayTurn.setStyle("-fx-font: 50 Arial;");
        this.displayTurn.setFill(Color.BLACK);
        this.setupDisplayTurn(true);

        StackPane textbox = new StackPane();
        textbox.setMinHeight(80);
        textbox.setMinWidth(20);
        textbox.setMaxHeight(20);
        textbox.setMaxWidth(300);
        textbox.setStyle("-fx-border-color: gray; -fx-border-width: 4;");
        textbox.getChildren().add(this.displayTurn);

        Rectangle background = new Rectangle(View.WIDTH * 2, View.HEIGHT * 2);
        background.setFill(Color.web("antiquewhite"));
        background.setTranslateZ(500); //TODO: Calculate

        StackPane boardContainer = new StackPane();
        boardContainer.setPrefSize(View.WIDTH, View.HEIGHT);
        boardContainer.setRotationAxis(Rotate.X_AXIS);
        boardContainer.setRotate(-50);
        boardContainer.setPickOnBounds(false);
        boardContainer.setStyle("-fx-effect: null;");

        this.setupSurface();

        boardContainer.getChildren().add(this.surfacePane);

        root.getChildren().addAll(background, boardContainer, textbox);
        root.setPickOnBounds(false); // Pass through click events
        root.setStyle("-fx-effect: null;");

        StackPane.setAlignment(background, Pos.CENTER);
        StackPane.setAlignment(textbox, Pos.TOP_CENTER);
        StackPane.setAlignment(this.surfacePane, Pos.CENTER);
        StackPane.setAlignment(this.displayTurn, Pos.CENTER);

        this.controller = new Controller(this, this.dimension, this.grid);

        this.setupFields();

        this.controller.setupPieces();

        Scene scene = new Scene(root, View.WIDTH, View.HEIGHT, true, null);
        scene.setCamera(new PerspectiveCamera());

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
