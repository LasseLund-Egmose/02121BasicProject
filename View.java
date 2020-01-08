import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.FontWeight;
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

    protected static final String BACKGROUND_FIELD = "-fx-background-image: url(/assets/dark_Wood_Texture.jpg);";
    protected static final String BACKGROUND_GRID = "-fx-background-image: url(/assets/light_Marble_Texture.jpg);";
    protected static final String BACKGROUND_WIN = "-fx-background-image: url(/assets/confetti_Texture.jpg);";

    protected Controller controller;
    protected int dimension = 8;
    protected GridPane grid;
    protected Pane surfacePane;
    protected RotateTransition surfacePaneRotation;
    protected Text displayTurn;
    protected Stage primaryStage;

    public double getSize() {
        return ((double) View.BOARD_SIZE) / this.dimension;
    }

    public void highlightPane(StackPane pane) {
        int borderWidth = this.getSize() < 20 ? 2 : 5;
        pane.setStyle(View.BACKGROUND_FIELD + " -fx-border-color: green; -fx-border-width: " + borderWidth + ";");
    }

    public void normalizePane(StackPane pane) {
        pane.setStyle(View.BACKGROUND_FIELD);
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

        this.grid.setStyle(View.BACKGROUND_GRID + " -fx-background-size: cover;");

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

    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

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

        this.controller = new Controller(this, this.dimension, this.grid);

        this.setupFields();

        this.controller.setupPieces();

        Scene scene = new Scene(root, View.WIDTH, View.HEIGHT, true, null);
        scene.setCamera(new PerspectiveCamera());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void setupDisplayTurn(boolean isWhiteTurn) {
        this.displayTurn.setText(isWhiteTurn ? "White's turn" : "Black's turn");
    }

    public void displayWin(String whoWon) {
        this.primaryStage.setTitle("You won!");

        StackPane root = new StackPane();

        Button button = new Button("Close");
        button.setStyle("-fx-padding: 8 15 15 15;\n" +
                "    -fx-background-insets: 0,0 0 5 0, 0 0 6 0, 0 0 7 0;\n" +
                "    -fx-background-radius: 8;\n" +
                "    -fx-background-color: \n" +
                "        linear-gradient(from 0% 93% to 0% 100%, #a34313 0%, #903b12 100%),\n" +
                "        #9d4024,\n" +
                "        #d86e3a,\n" +
                "        radial-gradient(center 50% 50%, radius 100%, #d86e3a, #c54e2c);\n" +
                "    -fx-effect: dropshadow( gaussian , rgba(0,0,0,0.75) , 4,0,0,1 );\n" +
                "    -fx-font-weight: bold;\n" +
                "    -fx-font-size: 60px;");
        button.setOnMouseClicked(e -> this.primaryStage.close());

        StackPane pane = new StackPane();
        pane.setStyle(View.BACKGROUND_WIN + " -fx-background-size: cover; -fx-border-color: black; -fx-border-width: 5px;");
        pane.setMinSize(View.POPUP_SIZE, View.POPUP_SIZE);
        pane.setMaxSize(View.POPUP_SIZE, View.POPUP_SIZE);

        DropShadow ds = new DropShadow();
        ds.setOffsetY(3.0f);
        ds.setColor(Color.color(0.4f, 0.4f, 0.4f));

        Text text = new Text();
        text.setEffect(ds);
        text.setFill(Color.GOLDENROD);
        text.setText(whoWon);
        text.setStyle("-fx-font: 70px Arial");

        StackPane.setAlignment(pane, Pos.CENTER);
        StackPane.setAlignment(text, Pos.CENTER);
        StackPane.setAlignment(button, Pos.BOTTOM_CENTER);

        pane.getChildren().addAll(text, button);
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
