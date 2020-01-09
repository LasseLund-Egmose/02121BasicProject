import javafx.scene.layout.StackPane;

import java.awt.*;

public class Field extends StackPane {

    protected CheckerPiece attachedPiece = null; // Reference to piece in field (if any)
    protected Point position; // The field's position

    public CheckerPiece getAttachedPiece() {
        return this.attachedPiece;
    }

    public Point getPosition() {
        return position;
    }

    public Field(Point position) {
        this.position = position;
    }

    public void setAttachedPiece(CheckerPiece piece) {
        this.attachedPiece = piece;
    }

}
