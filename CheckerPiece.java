import javafx.scene.image.Image;
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

    protected void setupPiece() {
        double radius = (this.size * 2) / 5;

        this.cylinder = new Cylinder(radius, radius / 1.5);
        this.cylinder.setMaterial(this.getMaterial());
        this.cylinder.setRotationAxis(Rotate.X_AXIS);
        this.cylinder.setRotate(90);
        this.cylinder.setTranslateZ(4);

        this.pane = new StackPane();
        this.pane.getChildren().add(this.cylinder);
    }

    public CheckerPiece(double size, Controller.Team team) {
        this.size = size;
        this.team = team;

        this.setupPiece();
    }

    public void assertHighlight(boolean shouldHighlight) {
        if (shouldHighlight) {
            this.cylinder.setMaterial(new PhongMaterial(Color.LIMEGREEN));
            return;
        }

        this.cylinder.setMaterial(this.getMaterial());
    }

    public void attachToField(StackPane pane, Point position, HashMap<Controller.Team, Integer> activeCount) {
        this.detach(activeCount);

        this.position = position;

        pane.getChildren().add(this.getPane());

        if (!this.isActive) {
            int activeCountInt = activeCount.get(this.team);
            activeCountInt++;
            activeCount.put(this.team, activeCountInt);
        }

        this.isActive = true;
    }

    public void attachToFieldByPane(
        HashMap<Integer, HashMap<Integer, StackPane>> fields,
        StackPane pane,
        HashMap<Controller.Team, Integer> activeCount
    ) {
        for (Map.Entry<Integer, HashMap<Integer, StackPane>> hmap : fields.entrySet()) {
            int x = hmap.getKey();

            for (Map.Entry<Integer, StackPane> e : hmap.getValue().entrySet()) {
                if (e.getValue() != pane) {
                    continue;
                }

                Point p = new Point(x, e.getKey());
                this.attachToField(pane, p, activeCount);

                return;
            }
        }
    }

    public void attachToFieldByPosition(
        HashMap<Integer, HashMap<Integer, StackPane>> fields,
        Point position,
        HashMap<Controller.Team, Integer> activeCount
    ) {
        StackPane pane = fields.get(position.x).get(position.y);
        this.attachToField(pane, position, activeCount);
    }

    public void detach(HashMap<Controller.Team, Integer> activeCount) {
        Pane p = this.getPane();
        Object parent = p.getParent();

        if (parent instanceof StackPane) {
            StackPane parentPane = (StackPane) parent;
            parentPane.getChildren().remove(p);
        }

        if (this.isActive) {
            int activeCountInt = activeCount.get(this.team);
            activeCountInt--;
            activeCount.put(this.team, activeCountInt);
        }

        this.isActive = false;
    }

    public PhongMaterial getMaterial() {
        PhongMaterial materialDark = new PhongMaterial();
        materialDark.setDiffuseMap(new Image(getClass().getResourceAsStream("/assets/5dark_Marble_Texture.jpg")));
        PhongMaterial materialLight = new PhongMaterial();
        materialLight.setDiffuseMap(new Image(getClass().getResourceAsStream("/assets/2light_Marble_Texture.jpg")));

        return team == Controller.Team.BLACK ? materialDark : materialLight;
    }

    public Pane getPane() {
        return this.pane;
    }

    public Point getPosition() {
        return this.position;
    }

    public void setupEvent(Controller controller) {
        this.pane.setOnMouseClicked(e -> {
            controller.setSelectedPiece(this);
        });
    }
}