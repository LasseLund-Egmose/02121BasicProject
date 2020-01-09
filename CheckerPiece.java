import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;

import java.awt.*;
import java.util.HashMap;

public class CheckerPiece {

    protected Cylinder cylinder; // Cylinder shape
    protected StackPane cylinderContainer = new StackPane(); // Cylinder container
    protected boolean isActive = false; // Is this piece added to board?
    protected PhongMaterial material; // Cylinder texture
    protected Field parent = null; // Parent field containing cylinderContainer
    protected double size; // Size of one field
    protected Controller.Team team; // Team of this piece

    // Setup pane, shape and material
    protected void setupPiece() {
        double radius = (this.size * 2) / 5;
        double height = radius / 1.5;

        this.material = new PhongMaterial();
        this.material.setDiffuseMap(
            new Image(getClass().getResourceAsStream(
                team == Controller.Team.BLACK ? "/assets/piece_black.jpg" : "/assets/piece_white.jpg"
            ))
        );

        this.cylinder = new Cylinder(radius, height);
        this.cylinder.setMaterial(this.getMaterial());
        this.cylinder.setRotationAxis(Rotate.X_AXIS);
        this.cylinder.setRotate(90);
        this.cylinder.setTranslateZ(height / 2);

        this.cylinderContainer.getChildren().add(this.cylinder);
    }

    // Construct
    public CheckerPiece(double size, Controller.Team team) {
        this.size = size;
        this.team = team;

        this.setupPiece();
    }

    // Make sure piece is either highlighted or not
    public void assertHighlight(boolean shouldHighlight) {
        if (shouldHighlight) {
            this.cylinder.setMaterial(new PhongMaterial(Color.LIMEGREEN));
            return;
        }

        this.cylinder.setMaterial(this.getMaterial());
    }

    // Detach and afterwards attach piece to given pane (black field)
    public void attachToField(Field field, HashMap<Controller.Team, Integer> activeCount) {
        // Detach
        this.detach(activeCount);

        // Add to field
        field.getChildren().add(this.getPane());

        // Setup references between piece and field
        this.parent = field;
        field.setAttachedPiece(this);

        // Justify activeCount if applicable
        if (!this.isActive) {
            int activeCountInt = activeCount.get(this.team);
            activeCountInt++;
            activeCount.put(this.team, activeCountInt);
        }

        // Set active
        this.isActive = true;
    }

    // Detach from current field and set activeCount accordingly
    public void detach(HashMap<Controller.Team, Integer> activeCount) {
        if (this.parent != null) {
            this.parent.getChildren().clear();
            this.parent.setAttachedPiece(null);
            this.parent = null;
        }

        if (this.isActive) {
            int activeCountInt = activeCount.get(this.team);
            activeCountInt--;
            activeCount.put(this.team, activeCountInt);
        }

        this.isActive = false;
    }

    public PhongMaterial getMaterial() {
        return this.material;
    }

    public Pane getPane() {
        return this.cylinderContainer;
    }

    public Field getParent() {
        return this.parent;
    }

    public Point getPosition() {
        if(this.getParent() == null) {
            return null;
        }

        return this.getParent().getPosition();
    }

    public Controller.Team getTeam() {
        return this.team;
    }

    // Setup click event on piece
    public void setupEvent(Controller controller) {
        this.cylinderContainer.setOnMouseClicked(e -> controller.setSelectedPiece(this));
    }
}