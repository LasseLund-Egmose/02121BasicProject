import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class Controller {

    protected GridPane grid;
    protected int n;
    protected View view;
    protected CheckerPiece selectedPiece = null;

    enum Team {
        BLACK,
        WHITE
    }

    protected void setupPiece(int i, int j, Team team) {
        Color pieceColor = team == Team.BLACK ? Color.BLACK : Color.WHITE;
        CheckerPiece piece = new CheckerPiece(this.view.getSize(), pieceColor);
        piece.setupEvent(this);

        this.grid.add(piece.getPane(), i, j);
    }

    protected void setupPieces() {
        this.setupPiece(0, 0, Team.WHITE);
        this.setupPiece(this.n - 1, this.n - 1, Team.BLACK);
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
    }

    public CheckerPiece getSelectedPiece() {
        return this.selectedPiece;
    }


}
