import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Controller {

    protected HashMap<String, StackPane> fields = new HashMap<>();
    protected GridPane grid;
    protected int n;
    protected CheckerPiece selectedPiece = null;
    protected View view;

    enum Team {
        BLACK,
        WHITE
    }

    protected void highlightEligibleFields(CheckerPiece piece) {
        ArrayList<Point> possibleJumpMoves = new ArrayList<>();
        ArrayList<Point> possibleRegularMoves = new ArrayList<>();

        for(Point p : this.surroundingFields(piece.getPosition())) {
            String stringPoint = p.toString();
            StackPane pane = this.fields.get(stringPoint);

            if(pane.getChildren().size() > 0) {
                System.out.println(stringPoint + " occupied!");
            } else {
                System.out.println(stringPoint + " free!");
            }
        }
    }

    protected ArrayList<Point> surroundingFields(Point p) {
        ArrayList<Point> eligiblePoints = new ArrayList<>();
        Point[] points = new Point[] {
            new Point(p.x - 1, p.y + 1),
            new Point(p.x + 1, p.y + 1),
            new Point(p.x - 1, p.y - 1),
            new Point(p.x + 1, p.y - 1)
        };

        for(int i = 0; i < 4; i++) {
            Point ip = points[i];
            if(ip.x >= 1 && ip.y >= 1 && ip.x <= this.n && ip.y <= this.n) {
                eligiblePoints.add(ip);
            }
        }

        return eligiblePoints;
    }

    public void setupPiece(int i, int j, Team team) {
        CheckerPiece piece = new CheckerPiece(this.view.getSize(), team);

        piece.attachToField(this.fields, new Point(i + 1, j + 1));
        piece.setupEvent(this);
    }

    protected void setupPieces() {
        this.setupPiece(3, 3, Team.WHITE);
        this.setupPiece(this.n - 4, this.n - 4, Team.BLACK);
    }

    public Controller(View view, int n, GridPane grid) {
        this.grid = grid;
        this.n = n;
        this.view = view;
    }

    public void addField(Point p, StackPane pane) {
        this.fields.put(p.toString(), pane);
    }

    public void setSelectedPiece(CheckerPiece piece) {
        // TODO: de-highlight fields here

        if (this.selectedPiece != null) {
            this.selectedPiece.changePieceColor(this.selectedPiece.getColor());
        }

        if (this.selectedPiece != piece) {
            this.selectedPiece = piece;
            this.selectedPiece.changePieceColor(Color.LIMEGREEN);

            this.highlightEligibleFields(this.selectedPiece);
            return;
        }

        this.selectedPiece = null;
    }

    public CheckerPiece getSelectedPiece() {
        return this.selectedPiece;
    }


}
