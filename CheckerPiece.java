import javafx.scene.image.Image;
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

    public void attachToField(HashMap<String, StackPane> fields, Point position) {
        this.position = position;

        StackPane drop = fields.get(position.toString());
        drop.getChildren().add(this.getPane());
    }

    public void setupEvent(Controller controller) {
        this.pane.setOnMouseClicked( e -> {
            controller.setSelectedPiece(this);
        });
    }

    protected void setupPiece() {
        this.cylinder = new Cylinder((this.size * 2) / 5, 8);
        this.cylinder.setMaterial((this.getMaterial()));
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

    public PhongMaterial getMaterial() {
        PhongMaterial materialDark = new PhongMaterial();
        materialDark.setDiffuseMap(new Image(getClass().getResourceAsStream("/assets/5dark_Marble_Texture.jpg")));
        PhongMaterial materialLight = new PhongMaterial();
        materialLight.setDiffuseMap(new Image(getClass().getResourceAsStream("/assets/2light_Marble_Texture.jpg")));

        return team == Controller.Team.BLACK ? materialDark : materialLight;
    }

    public void changePieceColor(Color color) {
        this.cylinder.setMaterial(new PhongMaterial(color));
    }

    public void changePieceMaterial(PhongMaterial material) {
        this.cylinder.setMaterial(material);
    }


}