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
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

public class View extends Application {

    protected static Pane getSurface() {
        StackPane pane = new StackPane();

        Box box = new Box();
        box.setWidth(700.0);
        box.setHeight(700.0);
        box.setDepth(10.0);
        box.setMaterial(new PhongMaterial(Color.RED));

        StackPane.setAlignment(box, Pos.CENTER);

        //Instantiating RotateTransition class
        RotateTransition rotate = new RotateTransition();

        //Setting Axis of rotation
        rotate.setAxis(Rotate.Z_AXIS);

        // setting the angle of rotation
        rotate.setByAngle(180);

        pane.setOnMouseClicked( e ->{
            rotate.play();
        });
        //setting cycle count of the rotation
        rotate.setCycleCount(1);

        //Setting duration of the transition
        rotate.setDuration(Duration.millis(1000));

        //the transition will be auto reversed by setting this to true
        rotate.setAutoReverse(false);

        //setting Rectangle as the node onto which the
// transition will be applied
        rotate.setNode(pane);

        pane.getChildren().add(box);
        return pane;
    }

    protected static void setupRotation(Pane pane) {


    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Checkers");
        StackPane root = new StackPane();
        root.setPrefSize(1000, 800);

        root.setRotationAxis(Rotate.X_AXIS);
        root.setRotate(-50);

        Pane surface = View.getSurface();
        root.getChildren().add(surface);
        StackPane.setAlignment(surface, Pos.CENTER);

        Scene scene = new Scene(root);
        scene.setCamera(new PerspectiveCamera());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
