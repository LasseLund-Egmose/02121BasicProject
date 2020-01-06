import javafx.scene.layout.GridPane;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Controller {

    protected GridPane grid;
    protected int n;
    protected HashMap<Point, CheckerPiece> occupiedPoints = new HashMap<>();
    protected CheckerPiece selectedPiece;
    protected View view;
    protected CheckerPiece selectedPiece = null;

    enum Team {
        BLACK,
        WHITE
    }

    protected void highlightEligibleFields(Point position) {
        ArrayList<Point> possibleJumpMoves = new ArrayList<>();
        ArrayList<Point> possibleRegularMoves = new ArrayList<>();

        for(Point p : this.surroundingFields(position)) {

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

    protected void setupPiece(int i, int j, Team team) {
        CheckerPiece piece = new CheckerPiece(this.view.getSize(), team);

        piece.setPosition(new Point(i + 1, j + 1), this.occupiedPoints);
        piece.attachToGrid(this.grid);
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

        this.setupPieces();
    }

    public void setSelectedPiece(CheckerPiece piece) {
        if (this.selectedPiece != null) {
            this.selectedPiece.changePieceColor(this.selectedPiece.getColor());
        }
        if (this.selectedPiece == piece) {
            this.selectedPiece = null;
        } else {
            this.selectedPiece = piece;
            this.selectedPiece.changePieceColor(Color.LIMEGREEN);
        }

        this.highlightEligibleFields(this.selectedPiece.getPosition());
    }

    public CheckerPiece getSelectedPiece() {
        return this.selectedPiece;
    }


}
