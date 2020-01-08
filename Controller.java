import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.Popup;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Controller {

    protected ArrayList<CheckerPiece> checkerPieces = new ArrayList<>(); // A list of all pieces
    protected HashMap<Integer, HashMap<Integer, StackPane>> fields = new HashMap<>(); // A map (x -> y -> pane) of all dark fields (StackPanes)
    protected HashMap<StackPane, Point> possibleJumpMoves = new HashMap<>(); // A map (pane -> jumped position) of all possible jump moves
    protected ArrayList<StackPane> possibleRegularMoves = new ArrayList<>(); // A list of all possible regular moves

    protected GridPane grid;
    protected EventHandler<MouseEvent> moveClickEventHandler;
    protected int n;
    protected CheckerPiece selectedPiece = null;
    protected View view;
    protected boolean isWhiteTurn = true;

    enum Team {
        BLACK,
        WHITE
    }

    protected void doJumpMove(StackPane toPane, Point jumpedPosition) {
        for(CheckerPiece piece : this.checkerPieces) {
            if(!piece.getPosition().equals(jumpedPosition)) {
                continue;
            }
            piece.detach();
            this.checkerPieces.remove(piece);

            break;
        }

        this.doRegularMove(toPane);
    }

    protected void doRegularMove(StackPane toPane) {
        this.getSelectedPiece().attachToFieldByPane(this.fields, toPane);

        this.selectedPiece.assertHighlight(false);

        this.normalizeFields();

        this.selectedPiece = null;
        this.possibleJumpMoves.clear();
        this.possibleRegularMoves.clear();

        this.finishTurn();
    }

    protected void finishTurn() {
        this.isWhiteTurn = !this.isWhiteTurn;
        checkForWin();
        this.view.setupDisplayTurn(this.isWhiteTurn);
        this.view.rotate();
    }

    protected void checkForWin(){
        int white = 0;
        int black = 0;

        for(int i = 0; i<this.checkerPieces.size(); i++) {
            if (this.checkerPieces.get(i).team == Team.WHITE) {
                white++;
            } else {
                black++;
            }
        }
        System.out.println("white:=" + white + "  " + "black:=" + black );
         if (white==0) {
             this.view.displayWin("black won!!");
         } else if (black==0) {
             this.view.displayWin("white won!!");
         }
    }

    protected Object eligibleJumpMoveOrNull(CheckerPiece thisPiece, Point opponentPosition) {
        Point thisPos = thisPiece.getPosition();
        Point diff = new Point(opponentPosition.x - thisPos.x, opponentPosition.y - thisPos.y);

        Point newPos = (Point) opponentPosition.clone();
        newPos.translate(diff.x, diff.y);

        return this.isPositionValid(newPos) ? fields.get(newPos.x).get(newPos.y) : null;
    }

    protected void highlightEligibleFields(CheckerPiece piece) {
        for(Point p : this.surroundingFields(piece.getPosition())) {
            StackPane pane = this.fields.get(p.x).get(p.y);

            if(pane.getChildren().size() > 0) {
                Object eligibleJumpMove = this.eligibleJumpMoveOrNull(piece, p);

                if(eligibleJumpMove instanceof StackPane) {
                    StackPane eligibleJumpMovePane = (StackPane) eligibleJumpMove;
                    this.possibleJumpMoves.put(eligibleJumpMovePane, p);
                    View.highlightPane(eligibleJumpMovePane);
                }
            } else {
                this.possibleRegularMoves.add(pane);
                View.highlightPane(pane);
            }
        }
    }


    protected boolean isPositionValid(Point p) {
        return p.x >= 1 && p.y >= 1 && p.x <= this.n && p.y <= this.n;
    }

    protected void normalizeFields() {
        ArrayList<StackPane> allHighlightedPanes = new ArrayList<>();
        allHighlightedPanes.addAll(this.possibleJumpMoves.keySet());
        allHighlightedPanes.addAll(this.possibleRegularMoves);

        for(StackPane p : allHighlightedPanes) {
            View.normalizePane(p);
        }
    }

    protected void onFieldClick(Object clickedElement) {
        if(!(clickedElement instanceof StackPane) || this.getSelectedPiece() == null) {
            return;
        }

        StackPane clickedElementPane = (StackPane) clickedElement;


        if(this.possibleJumpMoves.containsKey(clickedElement)) {
            this.doJumpMove(clickedElementPane, this.possibleJumpMoves.get(clickedElement));
            return;
        }

        if(this.possibleRegularMoves.contains(clickedElement)) {
            this.doRegularMove(clickedElementPane);
        }
    }

    protected void setupPiece(int i, int j, Team team) {
        CheckerPiece piece = new CheckerPiece(this.view.getSize(), team);

        piece.attachToFieldByPosition(this.fields, new Point(i + 1, j + 1));
        piece.setupEvent(this);

        this.checkerPieces.add(piece);
    }

    protected void setupPieces() {
        this.setupPiece(3, 3, Team.WHITE);
        this.setupPiece(this.n - 4, this.n - 4, Team.BLACK);
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

    public Controller(View view, int n, GridPane grid) {
        this.grid = grid;
        this.moveClickEventHandler = mouseEvent -> this.onFieldClick(mouseEvent.getSource());
        this.n = n;
        this.view = view;
    }

    public void addField(Point p, StackPane pane) {
        pane.addEventFilter(MouseEvent.MOUSE_PRESSED, this.moveClickEventHandler);

        if(!this.fields.containsKey(p.x)) {
            this.fields.put(p.x, new HashMap<>());
        }

        this.fields.get(p.x).put(p.y, pane);
    }

    public void setSelectedPiece(CheckerPiece piece) {
        if (this.selectedPiece != null) {
            this.selectedPiece.assertHighlight(false);
        }

        if (this.selectedPiece != piece && ((isWhiteTurn==true && piece.team==Team.WHITE) || (isWhiteTurn==false && piece.team==Team.BLACK))) {
            this.selectedPiece = piece;
            this.selectedPiece.assertHighlight(true);

            this.highlightEligibleFields(this.selectedPiece);
            return;
        }

        this.normalizeFields();
        this.selectedPiece = null;
    }

    public CheckerPiece getSelectedPiece() {
        return this.selectedPiece;
    }

    public boolean getisWhiteturn() {
        return this.isWhiteTurn;
    }

}
