import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;

import java.awt.*;

public class CheckerPiece {

    protected Color color;
    protected StackPane pane;
    protected Point position;
    protected double size;
    protected Cylinder cylinder;

    public CheckerPiece(double size, Color color) {
        this.color = color;
        this.size = size;

        this.setupPiece();
    }

    public void attachToGrid(GridPane pane, Point position) {
        this.position = position;

        pane.add(this.getPane(), position.x - 1, position.y - 1);
    }

    public void setupEvent(Controller controller) {
        this.pane.setOnMouseClicked( e -> {
            controller.setSelectedPiece(this);
        });
    }

    protected void setupPiece() {
        this.cylinder = new Cylinder((this.size * 2) / 5, 8);
        this.cylinder.setMaterial(new PhongMaterial(this.color));
        this.cylinder.setRotationAxis(Rotate.X_AXIS);
        this.cylinder.setRotate(90);
        this.cylinder.setTranslateZ(4);

        this.pane = new StackPane();
        this.pane.getChildren().add(this.cylinder);
    }

    protected Pane getPane() {
        return this.pane;
    }

    public Color getColor() {
        return this.color;
    }

    public void changePieceColor(Color color) {
        this.cylinder.setMaterial(new PhongMaterial(color));
    }

}