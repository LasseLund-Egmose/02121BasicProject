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

public class View extends Application {

    protected static Pane getSurface() {
        StackPane pane = new StackPane();

        Box box = new Box();
        box.setWidth(700.0);
        box.setHeight(700.0);
        box.setDepth(10.0);
        box.setMaterial(new PhongMaterial(Color.RED));

        StackPane.setAlignment(box, Pos.CENTER);

        pane.getChildren().add(box);
        return pane;
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
