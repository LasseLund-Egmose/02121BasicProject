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
    protected ArrayList<StackPane> possibleJumpMoves = new ArrayList<>();
    protected ArrayList<StackPane> possibleRegularMoves = new ArrayList<>();
    protected CheckerPiece selectedPiece = null;
    protected View view;

    enum Team {
        BLACK,
        WHITE
    }

    protected boolean checkJumpMove(CheckerPiece thisPiece, Point opponentPosition) {
        Point thisPos = thisPiece.getPosition();
        Point diff = new Point(opponentPosition.x - thisPos.x, opponentPosition.y - thisPos.y);

        Point newPos = ((Point) opponentPosition.clone());
        newPos.translate(diff.x, diff.y);

        return this.isPositionValid(newPos);
    }

    protected void highlightEligibleFields(CheckerPiece piece) {
        for(Point p : this.surroundingFields(piece.getPosition())) {
            String stringPoint = p.toString();
            StackPane pane = this.fields.get(stringPoint);

            if(pane.getChildren().size() > 0) {
                boolean jumpMoveEligible = this.checkJumpMove(piece, p);

                if(jumpMoveEligible) {
                    this.possibleJumpMoves.add(pane);
                }
            } else {
                this.possibleRegularMoves.add(pane);
            }
        }

        // TODO: Highlight possibleJumpMoves & possibleRegularMoves
        for(int i = 0; i < this.possibleRegularMoves.size(); i++) {
            View.highlightPane(this.possibleRegularMoves.get(i));
        }
    }

    protected boolean isPositionValid(Point p) {
        return p.x >= 1 && p.y >= 1 && p.x <= this.n && p.y <= this.n;
    }

    protected void normalizeEligibleFields() {
        // TODO: Normalize possibleJumpMoves & possibleRegularMoves
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
            if(this.isPositionValid(ip)) {
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
