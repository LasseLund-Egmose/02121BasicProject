import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;

import java.awt.*;
import java.util.HashMap;

public class CheckerPiece {

    protected StackPane pane;
    protected Point position;
    protected double size;
    protected Controller.Team team;
    protected Cylinder cylinder;

    public CheckerPiece(double size, Controller.Team team) {
        this.size = size;
        this.team = team;

        this.setupPiece();
    }

    public void attachToGrid(GridPane pane) {
        pane.add(this.getPane(), this.position.x - 1, this.position.y - 1);
    }

    public void setPosition(Point position, HashMap<Point, CheckerPiece> occupiedPositions) {
        occupiedPositions.remove(this.position);

        this.position = position;

        occupiedPositions.put(this.position, this);
    }

    public void setupEvent(Controller controller) {
        this.pane.setOnMouseClicked( e -> {
            controller.setSelectedPiece(this);
        });
    }

    protected void setupPiece() {
        this.cylinder = new Cylinder((this.size * 2) / 5, 8);
        this.cylinder.setMaterial(new PhongMaterial(this.getColor()));
        this.cylinder.setRotationAxis(Rotate.X_AXIS);
        this.cylinder.setRotate(90);
        this.cylinder.setTranslateZ(4);

        this.pane = new StackPane();
        this.pane.getChildren().add(this.cylinder);
    }

    protected Point getPosition() {
        return this.position;
    }

    protected Pane getPane() {
        return this.pane;
    }

    public Color getColor() {
        return team == Controller.Team.BLACK ? Color.BLACK : Color.WHITE;
    }

    public void changePieceColor(Color color) {
        this.cylinder.setMaterial(new PhongMaterial(color));
    }

}