import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;

public class CheckerPiece {

    protected Color color;
    protected StackPane pane;
    protected double size;

    public CheckerPiece(double size, Color color) {
        this.color = color;
        this.size = size;

        this.setupPiece();
    }

    protected void setupPiece() {
        Cylinder cylinder = new Cylinder((this.size * 2) / 5, 8);
        cylinder.setMaterial(new PhongMaterial(this.color));
        cylinder.setRotationAxis(Rotate.X_AXIS);
        cylinder.setRotate(90);

        this.pane = new StackPane();
        this.pane.getChildren().add(cylinder);
    }

    public Pane getPane() {
        return this.pane;
    }
}