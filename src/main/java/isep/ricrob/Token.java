package isep.ricrob;

import javafx.scene.Node;
import javafx.scene.image.ImageView;

import java.util.Random;


public abstract class Token {

    // Classe avec méthodes et attributs servant pour les robots et les symboles

    public Token(Color color) {
        this.color = color;
        Random random = new Random();
        setPosition( random.nextInt(Game.SIZE), random.nextInt(Game.SIZE) );
    }

    private Color color;
    public Color getColor() { return this.color; }

    // Métohdes concernant la position

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
