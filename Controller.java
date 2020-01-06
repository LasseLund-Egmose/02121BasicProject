import javafx.scene.layout.GridPane;

public class Controller {

    protected int boardSize;
    protected GridPane grid;
    protected View view;

    enum Team {
        BLACK,
        WHITE
    }

    protected void setupPiece(int i, int j, Team team) {
        this.grid.add(null, i, j);
    }

    protected void setupPieces() {
        this.setupPiece(0, 0, Team.WHITE);
        this.setupPiece(this.boardSize - 1, this.boardSize - 1, Team.BLACK);
    }

    public Controller(View view, int boardSize, GridPane grid) {
        this.boardSize = boardSize;
        this.grid = grid;
        this.view = view;

        this.setupPieces();
    }


}
