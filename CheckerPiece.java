import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Cylinder;

public class CheckerPiece {
    protected StackPane pane;
    protected double size;

    public CheckerPiece(double size){
        this.size=size;
        this.pane = new StackPane();
        setupPiece();
    }

    protected void setupPiece(){
        Cylinder cylinder = new Cylinder((this.size*2)/5, 8);
        this.pane.getChildren().add(cylinder);
    }

    public Pane getPane(){
        return this.pane;
    }
}