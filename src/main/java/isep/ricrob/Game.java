package isep.ricrob;

import isep.utiliy.Directions;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.ImageView;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;

import static isep.ricrob.Game.Status.*;
import static isep.ricrob.Token.Color.*;


public class Game {
    // * Instance globale de gestion du jeu
    public static Game context;
    private final int TIME_PLAY = 120;
    public IntegerProperty TIME_TO_CATCH = new SimpleIntegerProperty(TIME_PLAY);
    private int numberOfSteps ;
    private StringProperty numberOfStepsProperty = new SimpleStringProperty(); ;
    // Le plateau de SIZE x SIZE cases
    private ArrayList<Tile> board;
    // Les 4 robots
    private Map<Token.Color, Token> robots;

    //La liste des symboles dans le board
    private ArrayList<Token> listSymbols;

    // La cible
    private Token target;

    public ObservableValue<? extends String> getNumberOfSteps() {
        return numberOfStepsProperty;
    }

    public void resetSteps(){
        this.numberOfSteps = 0;
        this.numberOfStepsProperty.setValue("Coups -  0");
    }

    public void resetTimer(){
        Game.context.TIME_TO_CATCH.setValue(TIME_PLAY);
    }
    public static void start() {
        if (Game.context != null) {
            throw new RuntimeException
                    ("Impossible de lancer plusieurs fois la partie...");
        }
        Game.context = new Game();
        Game.context.setStatus(CHOOSE_PLAYER);
    }

    // * ---

    // Taille du plateau (SIZE x SIZE)
    public static final int SIZE = 16;

    // Constructeur privé (instance unique créée par "start()" )
    private Game() {

        board = new ArrayList<>();

        robots = new HashMap<>();
        robots.put(RED, new Robot(RED));
        robots.put(GREEN, new Robot(GREEN));
        robots.put(BLUE, new Robot(BLUE));
        robots.put(YELLOW, new Robot(YELLOW));

        Token.Color[] colors = Token.Color.values();
        int randomColorIndex = ( new Random() ).nextInt( colors.length );
        target = new Symbol( colors[randomColorIndex] );
        listSymbols = new ArrayList<>();
        numberOfStepsProperty.setValue("Coups -  0");
    }

    public ArrayList<Tile> getBoard(){return board;}
    // * Gestion des événements du jeu

    public void processSelectPlayer() {
        if (this.status == CHOOSE_PLAYER) {
            // Action suivante attendue : choisir la case cible
            //setStatus(PLAY);
            setStatus(CHOOSE_ROBOT);
        }
    }

    public void processSelectRobot(Token.Color color) {
        if (this.status == CHOOSE_ROBOT) {
            this.selectedRobot = this.robots.get(color);
            // Action suivante attendue : choisir la case cible
            setStatus(CHOOSE_TILE);
        }
    }

    public String processSelectTile(int col, int lig) {
        if (this.status == CHOOSE_TILE) {
            if (
                    (this.selectedRobot.getCol() != col)
                            &&
                  (this.selectedRobot.getLig() != lig)
            ) {
                return "Les déplacements en diagonale sont interdits";
            } else {
                if (col > this.selectedRobot.getCol()) moveRobot(Directions.RIGHT);
                else if (col < this.selectedRobot.getCol()) moveRobot(Directions.LEFT);
                else if (lig > this.selectedRobot.getLig()) moveRobot(Directions.DOWN);
                else if (lig < this.selectedRobot.getLig()) moveRobot(Directions.UP);

                if (checkIfWin()){
                    //System.out.println("Vous avez gagné !");
                    setStatus(PLAYER_WIN_TOKEN);
                    return "Vous avez gagné !";
                }else{
                    //this.selectedRobot.setPosition(col,lig);
                    // Action suivante attendue : choisir un robot
                    setStatus(CHOOSE_ROBOT);
                    return "MOVE";
                }

            }
        }

        return null;
    }

    // * Etat courant du jeu

    public enum Status {
        CHOOSE_PLAYER("Cliquez sur le bouton [Jouer]"),
        PLAY("Etat de jeu"),
        CHOOSE_ROBOT("Cliquez sur le robot à déplacer"),
        CHOOSE_TILE("Cliquez sur la case destination"),
        PLAYER_WIN_TOKEN("Vous avez gagnez");
        Status(String toolTip) { this.toolTip = toolTip; }
        private String toolTip;
        public String getToolTip() { return this.toolTip; }
    }
    public Status getStatus() { return status; }
    public void setStatus(Status status) {

        this.status = status;
        // Mise à jour du libellé d'état sur l'affichage
        StringBuilder statusMessage = new StringBuilder();
        if (playerNameProperty.get() != null) {
            statusMessage.append(playerNameProperty.get());
            statusMessage.append(" : ");
        }
        statusMessage.append( status.getToolTip() );
        this.statusToolTipProperty.set( statusMessage.toString() );

    }
    private Status status;
    // "Binding JFX" - Synchronisation avec "MainController.statusLabel"
    public StringProperty statusToolTipProperty = new SimpleStringProperty();

    // "Binding JFX" - Synchronisation avec "PlayerController.name"
    public StringProperty playerNameProperty = new SimpleStringProperty();

    private Token selectedRobot;
    public Token getSelectedRobot() { return this.selectedRobot; }


    public void updateSelectedRobot(Token robot) { this.selectedRobot = robot; }
    // * ---


    public Map<Token.Color, Token> getRobots() { return this.robots; }


    public Token getTarget() { return this.target; }

    public void setTarget(Symbol target) {  this.target = target; }
    public ArrayList<Token> getSymbols() { return this.listSymbols; }


    public void moveRobot(Directions direction){
        boolean hasBeenMoved = false;
        boolean noObstacle = true;
        switch (direction){
            case UP: {
                var row  = this.selectedRobot.getLig();
                var col  = this.selectedRobot.getCol();
                while(noObstacle && (row - 1) >= 0){
                    //Condition sur les murs
                    if (this.getTileAt(row,col).wallUp.isVisible()
                       || this.getTileAt((row - 1),col).wallDown.isVisible()){
                        noObstacle = false;
                        continue;
                    }
                    //Condition sur la disponibilité des case
                    if (!this.getTileAt((row - 1),col).isAvailable()) break;

                    //Update robot position
                    this.getTileAt(row,col).setAvailable(true);

                    this.selectedRobot.setPosition(col,(row - 1));
                    hasBeenMoved = true;
                    this.getTileAt((row - 1),col).setAvailable(false);

                    row --;
                }
                break;
            }
            case DOWN: {
                var row  = this.selectedRobot.getLig();
                var col  = this.selectedRobot.getCol();
                while(noObstacle && (row + 1) < SIZE){
                    //Condition sur les murs
                    if (this.getTileAt(row,col).wallDown.isVisible()
                            || this.getTileAt((row +1),col).wallUp.isVisible()){
                        noObstacle = false;
                        continue;
                    }
                    //Condition sur la disponibilité des case
                    if (!this.getTileAt((row +1),col).isAvailable()) break;

                    //Update robot position
                    this.getTileAt(row,col).setAvailable(true);

                    this.selectedRobot.setPosition(col,(row + 1));
                    hasBeenMoved = true;
                    this.getTileAt((row + 1),col).setAvailable(false);

                    row ++;
                }
                break;
            }
            case LEFT: {
                var row  = this.selectedRobot.getLig();
                var col  = this.selectedRobot.getCol();
                while(noObstacle && (col - 1) >= 0){
                    //Condition sur les murs
                    if (this.getTileAt(row,col).wallLeft.isVisible() || this.getTileAt(row ,(col - 1)).wallRight.isVisible()){
                        noObstacle = false;
                        continue;
                    }
                    //Condition sur la disponibilité des case
                    if (!this.getTileAt(row,(col-1)).isAvailable()) break;

                    //Update robot position
                    this.getTileAt(row,col).setAvailable(true);

                    this.selectedRobot.setPosition((col-1),row);
                    hasBeenMoved = true;
                    this.getTileAt(row,(col-1)).setAvailable(false);

                    col --;
                }
                break;
            }
            case RIGHT: {
                var row  = this.selectedRobot.getLig();
                var col  = this.selectedRobot.getCol();
                while(noObstacle && (col + 1) < SIZE){
                    //Condition sur les murs
                    if (this.getTileAt(row,col).wallRight.isVisible()
                            || this.getTileAt(row ,col+1).wallLeft.isVisible()){
                        noObstacle = false;
                        continue;
                    }
                    //Condition sur la disponibilité des case
                    if (!this.getTileAt(row,(col+1)).isAvailable()) break;

                    //Update robot position
                    this.getTileAt(row,col).setAvailable(true);

                    this.selectedRobot.setPosition(col + 1,row);
                    hasBeenMoved = true;
                    this.getTileAt(row,(col+1)).setAvailable(false);

                    col ++;
                }
                break;
            }
        }
        if(hasBeenMoved){
            numberOfSteps++;
            numberOfStepsProperty.setValue("coups :  "+numberOfSteps);
        }
    }

    private Tile getTileAt(int row,int col){
        int index = row * Game.SIZE + col;
        return board.get(index);
    }

    private boolean checkIfWin(){
        var row = this.getSelectedRobot().getLig();
        var col = this.getSelectedRobot().getCol();
        var color = this.getSelectedRobot().getColor();
        var filePath = ((ImageView)this.getTarget().getGui()).getImage().getUrl();
//        System.out.println("Player "+row+" "+col+" "+filePath);

        for(var symbol: listSymbols){
//            System.out.println("symbol -" + symbol.getLig() + " " + symbol.getCol()+" "+((ImageView)symbol.getGui()).getImage().getUrl().equals(filePath));
            if (symbol.getLig() == row &&
                    symbol.getCol() == col &&
                    symbol.getColor() == color &&
                    ((ImageView)symbol.getGui()).getImage().getUrl().equals(filePath)
            ){

                return true;
            }
        }
        return false;
    }



}
