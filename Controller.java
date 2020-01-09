import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Controller {

    protected ArrayList<CheckerPiece> checkerPieces = new ArrayList<>(); // A list of all pieces
    protected HashMap<Integer, HashMap<Integer, Field>> fields = new HashMap<>(); // A map (x -> y -> pane) of all fields

    protected HashMap<Team, Integer> activeCount = new HashMap<>(); // A map (Team -> int) of number of active pieces on each team

    protected HashMap<Field, Field> possibleJumpMoves = new HashMap<>(); // A map (pane -> jumped pane) of all possible jump moves
    protected ArrayList<Field> possibleRegularMoves = new ArrayList<>(); // A list of all possible regular moves

    protected int dimension; // Dimension of board
    protected GridPane grid;
    protected boolean isWhiteTurn = true; // Keep track of turn
    protected EventHandler<MouseEvent> moveClickEventHandler; // EventHandler for click events on black fields
    protected CheckerPiece selectedPiece = null; // Keep track of selected piece
    protected View view; // Reference to view instance

    // Team enum
    public enum Team {
        BLACK,
        WHITE
    }

    // Check if a team has won
    protected void checkForWin() {
        if (this.activeCount.get(Team.BLACK) == 0) {
            this.view.displayWin("White won");
        }

        if (this.activeCount.get(Team.WHITE) == 0) {
            this.view.displayWin("Black won");
        }
    }

    // Handle a jump move
    protected void doJumpMove(Field toField, Field jumpedField) {
        // Detach (remove) jumped CheckerPiece
        jumpedField.getAttachedPiece().detach(this.activeCount);

        // Handle rest of move as a regular move
        this.doRegularMove(toField);
    }

    // Handle a regular move
    protected void doRegularMove(Field toField) {
        // Attach selected piece to chosen field
        this.getSelectedPiece().attachToField(toField, this.activeCount);

        // Remove highlight of piece and fields
        this.selectedPiece.assertHighlight(false);
        this.normalizeFields();

        // Reset highlight-related properties
        this.selectedPiece = null;
        this.possibleJumpMoves.clear();
        this.possibleRegularMoves.clear();

        // Finish turn
        this.finishTurn();
    }

    // Check if a jump move is eligible (e.g. no piece behind jumped piece)
    // Return pane from new position if yes and null if no
    protected Object eligibleJumpMoveOrNull(CheckerPiece thisPiece, Point opponentPosition) {
        Point thisPos = thisPiece.getPosition();
        Point diff = new Point(opponentPosition.x - thisPos.x, opponentPosition.y - thisPos.y);

        Point newPos = (Point) opponentPosition.clone();
        newPos.translate(diff.x, diff.y);

        return this.isPositionValid(newPos) ? fields.get(newPos.x).get(newPos.y) : null;
    }

    // Check if game is over, toggle isWhiteTurn and setup turn for other team
    protected void finishTurn() {
        this.isWhiteTurn = !this.isWhiteTurn;

        checkForWin();

        this.view.setupDisplayTurn(this.isWhiteTurn);
        this.view.rotate();
    }

    // Highlight fields a selected piece can move to
    protected void highlightEligibleFields(CheckerPiece piece) {
        // Iterate surrounding diagonal fields of given piece
        for (Point p : this.surroundingFields(piece.getPosition())) {
            // Get pane of current field
            Field field = this.fields.get(p.x).get(p.y);

            // Is this position occupied - and is it possible to jump it?
            if (field.getChildren().size() > 0) {
                Object eligibleJumpMove = this.eligibleJumpMoveOrNull(piece, p);

                // Check if jump move is eligible - per eligibleJumpMoveOrNull
                if (eligibleJumpMove instanceof Field) {
                    // Handle jump move if not null (e.g. instance of Field)
                    Field eligibleJumpMoveField = (Field) eligibleJumpMove;

                    this.possibleJumpMoves.put(eligibleJumpMoveField, field);
                    this.view.highlightPane(eligibleJumpMoveField);
                }
            } else { // Else allow a regular move
                this.possibleRegularMoves.add(field);
                this.view.highlightPane(field);
            }
        }
    }

    // Check if position is within boundaries of board
    protected boolean isPositionValid(Point p) {
        return p.x >= 1 && p.y >= 1 && p.x <= this.dimension && p.y <= this.dimension;
    }

    // Remove highlights from highlighted fields
    protected void normalizeFields() {
        ArrayList<Field> allHighlightedPanes = new ArrayList<>();
        allHighlightedPanes.addAll(this.possibleJumpMoves.keySet());
        allHighlightedPanes.addAll(this.possibleRegularMoves);

        for (Field field : allHighlightedPanes) {
            this.view.normalizePane(field);
        }
    }

    // Handle click on black field
    protected void onFieldClick(Object clickedElement) {
        // Check if Field is clicked and a selectedPiece is chosen
        if (!(clickedElement instanceof Field) || this.getSelectedPiece() == null) {
            return;
        }

        Field clickedElementField = (Field) clickedElement;

        // Is a jump move chosen?
        if (this.possibleJumpMoves.containsKey(clickedElement)) {
            this.doJumpMove(clickedElementField, this.possibleJumpMoves.get(clickedElement));
            return;
        }

        // Is a regular move chosen?
        if (this.possibleRegularMoves.contains(clickedElement)) {
            this.doRegularMove(clickedElementField);
        }
    }

    // Setup one black field by position
    protected void setupField(Point p) {
        Field field = new Field(p);

        field.addEventFilter(MouseEvent.MOUSE_PRESSED, this.moveClickEventHandler);

        if (!this.fields.containsKey(p.x)) {
            this.fields.put(p.x, new HashMap<>());
        }

        this.fields.get(p.x).put(p.y, field);

        this.view.setupField(field, p);
    }

    // Create a piece by team and attach it to given position
    protected void setupPiece(Point position, Team team) {
        CheckerPiece piece = new CheckerPiece(this.view.getSize(), team);

        Field field = this.fields.get(position.x).get(position.y);

        // Attach to field by position
        piece.attachToField(
            field,
            this.activeCount
        );

        // Setup click event for field
        piece.setupEvent(this);

        // Add to list of pieces
        this.checkerPieces.add(piece);
    }

    // Get diagonally surrounding fields (within board boundaries) from a given position
    protected ArrayList<Point> surroundingFields(Point p) {
        ArrayList<Point> eligiblePoints = new ArrayList<>();
        Point[] points = new Point[]{
            new Point(p.x - 1, p.y + 1),
            new Point(p.x + 1, p.y + 1),
            new Point(p.x - 1, p.y - 1),
            new Point(p.x + 1, p.y - 1)
        };

        for (int i = 0; i < 4; i++) {
            Point ip = points[i];
            if (this.isPositionValid(ip)) {
                eligiblePoints.add(ip);
            }
        }

        return eligiblePoints;
    }

    // Construct controller
    public Controller(View view, int dimension, GridPane grid) {
        this.dimension = dimension;
        this.grid = grid;
        this.moveClickEventHandler = mouseEvent -> this.onFieldClick(mouseEvent.getSource());
        this.view = view;

        this.activeCount.put(Team.BLACK, 0);
        this.activeCount.put(Team.WHITE, 0);
    }

    // Get selected piece
    public CheckerPiece getSelectedPiece() {
        return this.selectedPiece;
    }

    // Set selected piece
    public void setSelectedPiece(CheckerPiece piece) {
        // Remove highlight from currently selected piece
        if (this.selectedPiece != null) {
            this.normalizeFields();
            this.selectedPiece.assertHighlight(false);
        }

        // Select piece if turn matches the piece's team
        if (this.selectedPiece != piece && isWhiteTurn == (piece.getTeam() == Team.WHITE)) {
            this.selectedPiece = piece;
            this.selectedPiece.assertHighlight(true);

            // Highlight fields around selected piece
            this.highlightEligibleFields(this.selectedPiece);
            return;
        }

        // Reset selectedPiece
        this.selectedPiece = null;
    }

    // Setup black fields
    public void setupFields() {
        for (int i = 0; i < this.dimension; i++) {
            for (int j = i % 2; j < this.dimension; j += 2) {
                this.setupField(new Point(j + 1, i + 1));
            }
        }
    }

    // Setup a piece in each corner
    public void setupPieces() {
        this.setupPiece(new Point(1, 1), Team.WHITE);
        this.setupPiece(new Point(this.dimension, this.dimension), Team.BLACK);
    }
}
