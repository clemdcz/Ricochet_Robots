package isep.ricrob;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.image.ImageView;

import java.util.Random;


public class Token {

    public Token(Color color) {
        this.color = color;
        Random random = new Random();
        setPosition( random.nextInt(Game.SIZE), random.nextInt(Game.SIZE) );
    }

    private Color color;
    public Color getColor() { return this.color; }

    // * Position

    public int col;
    public int lig;

    public void setPosition(int col, int lig) {
        this.col = col;
        this.lig = lig;
    }
    public int getCol() { return col; }
    public int getLig() { return lig; }

    // Composant "JFX" associé
    Node gui;
    public void setGui(ImageView gui) { this.gui = gui; }
    public Node getGui() { return gui; }

    // * ---

    public enum Color {RED, GREEN, BLUE, YELLOW}

}
