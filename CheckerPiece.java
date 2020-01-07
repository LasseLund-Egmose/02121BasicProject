import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CheckerPiece {

    protected StackPane pane;
    protected Point position;
    protected double size;
    protected Controller.Team team;
    protected Cylinder cylinder;
    protected boolean isActive = false;

    public CheckerPiece(double size, Controller.Team team) {
        this.size = size;
        this.team = team;

        this.setupPiece();
    }

    public void attachToField(StackPane pane, Point position) {
        this.detach();

        this.position = position;

        pane.getChildren().add(this.getPane());

        this.isActive = true;
    }

    public void attachToFieldByPane(HashMap<Integer, HashMap<Integer, StackPane>> fields, StackPane pane) {
        this.detach();

        for (Map.Entry<Integer, HashMap<Integer, StackPane>> hmap : fields.entrySet()) {
            int x = hmap.getKey();

            for(Map.Entry<Integer, StackPane> e : hmap.getValue().entrySet()) {
                if(e.getValue() != pane) {
                    continue;
                }

                Point p = new Point(x, e.getKey());
                this.attachToField(pane, p);

                return;
            }
        }
    }

    public void attachToFieldByPosition(HashMap<Integer, HashMap<Integer, StackPane>> fields, Point position) {
        StackPane pane = fields.get(position.x).get(position.y);
        this.attachToField(pane, position);
    }

    public void detach() {
        Pane p = this.getPane();
        Object parent = p.getParent();

        if(parent instanceof StackPane) {
            StackPane parentPane = (StackPane) parent;
            parentPane.getChildren().remove(p);
        }

        this.isActive = false;
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