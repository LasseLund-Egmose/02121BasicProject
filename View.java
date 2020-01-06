import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

public class View extends Application {

    protected Pane surfacePane;
    protected RotateTransition surfacePaneRotation;

    protected void setupSurface() {
        this.surfacePane = new StackPane();

        // Setup board surface
        Box box = new Box();
        box.setWidth(700.0);
        box.setHeight(700.0);
        box.setDepth(10.0);
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
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Checkers");
        StackPane root = new StackPane();
        root.setPrefSize(1000, 800);

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
    }

    public void rotate() {
        this.surfacePaneRotation.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
