import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

public class View extends Application {

    public static String[] args;

    protected static final int DEPTH = 50;
    protected static final int HEIGHT = 800;
    protected static final int WIDTH = 1000;
    protected static final int BOARD_SIZE = 700;

    protected GridPane grid;
    protected int n = 8;
    protected Pane surfacePane;
    protected RotateTransition surfacePaneRotation;

    protected void setupField(int i, int j) {
        StackPane drop = new StackPane();
        drop.setStyle("-fx-background-color: #000");

        double size = ((double) View.BOARD_SIZE) / this.n;
        drop.setPrefSize(size, size);

        this.grid.add(drop, i, j);
    }

    protected void setupFields() {
        for(int i = 0; i < n; i++) {
            for(int j = (i + 1) % 2; j < n; j += 2) {
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

        this.setupFields();

        this.surfacePane.getChildren().add(this.grid);
    }

    protected void setupSurface() {
        this.surfacePane = new StackPane();

        // Setup board surface
        Box box = new Box();
        box.setWidth(View.BOARD_SIZE);
        box.setHeight(View.BOARD_SIZE);
        box.setDepth(View.DEPTH);
        box.setMaterial(new PhongMaterial(Color.GRAY));

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
        // Handle n-argument
        if(View.args.length == 1) {
            int newN = Integer.parseInt(View.args[0]);

            if(newN >= 3 && newN <= 100) {
                this.n = newN;
            }
        }

        primaryStage.setTitle("Checkers");
        StackPane root = new StackPane();
        root.setPrefSize(View.WIDTH, View.HEIGHT);

        root.setRotationAxis(Rotate.X_AXIS);
        root.setRotate(-50);

        this.setupSurface();

        root.getChildren().add(this.surfacePane);
        StackPane.setAlignment(this.surfacePane, Pos.CENTER);

        this.surfacePane.setOnMouseClicked(e -> this.rotate()); // TODO: Remove this

        Scene scene = new Scene(root);
        scene.setCamera(new PerspectiveCamera());

        primaryStage.setScene(scene);
        primaryStage.show();

        new Controller(this, this.n, this.grid);
    }

    public void rotate() {
        this.surfacePaneRotation.play();
    }

    public static void main(String[] args) {
        View.args = args;

        launch(args);
    }
}
