package isep.jfx;

import isep.ricrob.Game;
import isep.ricrob.Symbol;
import isep.ricrob.Tile;
import isep.ricrob.Token;
import isep.utility.SymbolsRessources;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;
import java.util.List;

import static isep.ricrob.Token.Color.*;

public class MainController {

    public final int TILE_SIZE = 40;


    @FXML
    public GridPane boardPane;

    @FXML
    private StackPane targetDisplayed;

    @FXML
    public Label statusLabel;

    @FXML
    public Label timeLabel;

    @FXML
    public Button rejouerBtn;

    @FXML
    public Label steps;

    List<Tile> tiles;

    //Dans cette classe MainController, on retrouve les méthodes suivantes :
    // - Initialisation qui va créer le board en utilisant les méthodes nécessaires (createWalls, addSymbols, refreshTarget)
    // - Une méthode pour récupérer le nom du joueur
    // - Des méthodes pour reset et actualiser la position du robot
    // - Tout ce qui tourne autour du timer
    // - Restart le jeu

    // "initialize()" est appelé par JavaFX à l'affichage de la fenêtre
    @FXML
    public void initialize() {

        steps.textProperty().bind(Game.context.getNumberOfSteps());
        timeLabel.textProperty().bind(Game.context.TIME_TO_CATCH.asString());

        // Affichage un message "bloquant"
        showWarning("Bienvenue sur le jeu Ricochet Robots");


        // Construction du plateau
        Image tile = new Image("cell.png", TILE_SIZE, TILE_SIZE, false, true);
        Image wl = new Image("WL.png", TILE_SIZE, TILE_SIZE, false, true);
        Image wr = new Image("WR.png", TILE_SIZE, TILE_SIZE, false, true);
        Image wu = new Image("WU.png", TILE_SIZE, TILE_SIZE, false, true);
        Image wd = new Image("WD.png", TILE_SIZE, TILE_SIZE, false, true);

        // ... "cell.png" doit être placé à la racine de "resources/" (sinon PB)
        boardPane.setPadding(new Insets(2));

        int rows = Game.SIZE;
        int cols = Game.SIZE;
        //rows = cols = 4;
        tiles = Game.context.getBoard();

        //Début du timer
        startTimer();

        //On crée les cellules du plateau
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {

                ImageView tileGui = new ImageView(tile);
                final int finalRow = row;
                final int finalCol = col;
                tileGui.setOnMouseClicked
                        (event -> {
                            String status = Game.context.processSelectTile(finalCol, finalRow);

                            if ("MOVE".equals(status)) {
                                updateSelectedRobotPosition();
                            } else if (status != null) {
                                if(Game.context.getStatus() == Game.Status.PLAYER_WIN_TOKEN){
                                    updateSelectedRobotPosition();
                                    showWarning(Game.Status.PLAYER_WIN_TOKEN.getToolTip() + ", temps de jeu " +
                                            (120 - (Game.context.TIME_TO_CATCH.getValue())) + "sec , nombre de " +
                                        Game.context.getNumberOfSteps().getValue());
                                    Game.context.setStatus(Game.Status.CHOOSE_PLAYER);
                                    restartGame();

                                }
                                showWarning(status);
                            }
                        });

                ImageView wlUL = new ImageView(wl);
                ImageView wrUL = new ImageView(wr);
                ImageView wuUL = new ImageView(wu);
                ImageView wdUL = new ImageView(wd);




                Pane pane = new Pane();
                pane.setPrefWidth(40);
                pane.setPrefHeight(40);
                tileGui.fitWidthProperty().bind(pane.widthProperty());
                pane.setLayoutX(0);
                pane.setLayoutY(0);
                wdUL.fitWidthProperty().bind(pane.widthProperty());
                wrUL.setX(0);


                pane.getChildren().addAll(tileGui, wlUL, wrUL, wuUL, wdUL);
                tiles.add(new Tile(pane, row, col, wlUL, wrUL, wuUL, wdUL));

                boardPane.add(pane, col, row);
            }
        }

        //On fixe les robots comme étant non disponibles
        for (var color : Token.Color.values()) {
            var robot = Game.context.getRobots().get(color);
            getTileAt(robot.getLig(), robot.getCol()).setAvailable(false);
        }

        // Setup des murs ici
        createWalls();


        //Ajout des symboles
        addSymbols();

        // Ajout des pièces
        addRobot(RED);
        addRobot(GREEN);
        addRobot(BLUE);
        addRobot(YELLOW);


        //Ajout de l'objectif
        refreshTarget();

        // "Binding JFX" - Synchronisation du "Label" avec l'état du jeu
        statusLabel.textProperty().bind(Game.context.statusToolTipProperty);
        Game.context.setStatus(Game.Status.CHOOSE_PLAYER);
    }

    //Méthode pour aller chercher les coordonnées du celulle
    private Tile getTileAt(int row, int col) {
        int index = row * Game.SIZE + col;
        return tiles.get(index);
    }

    // Affiche une boite de dialogue construite avec "SceneBuilder"
    public void showPlayerView(ActionEvent actionEvent) throws IOException {
        if (Game.context.getStatus() == Game.Status.CHOOSE_PLAYER) {
            FXMLLoader fxmlLoader = new FXMLLoader
                    (MainApplication.class.getResource("player-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        }
    }

    //Ajout des robots
    private void addRobot(Token.Color color) {
        Token robot = Game.context.getRobots().get(color);
        ImageView robotGui = new ImageView(new Image(
                color.name() + "_robot.png",
                TILE_SIZE, TILE_SIZE, false, true
        ));
        robotGui.setOnMouseClicked
                (event -> Game.context.processSelectRobot(color));
        boardPane.add(robotGui, robot.getCol(), robot.getLig());
        // Association de l' "ImageView" avec le robot stocké dans le jeu
        robot.setGui(robotGui);
    }

    // Update la position des robots
    private void updateSelectedRobotPosition() {

        Token robot = Game.context.getSelectedRobot();
        GridPane.setConstraints(robot.getGui(), robot.getCol(), robot.getLig());
    }

    private void showWarning(String message) {
        Alert startMessage
                = new Alert(Alert.AlertType.INFORMATION, message);
        startMessage.setHeaderText(null);
        startMessage.setGraphic(null);
        startMessage.showAndWait();
    }

    //Ajout de tous les murs
    private void createWalls() {
        // Murs extérieurs
        getTileAt(0, 0).setWall(true, false, true, false);
        getTileAt(1, 0).setWall(true, false, false, false);
        getTileAt(2, 0).setWall(true, false, false, false);
        getTileAt(3, 0).setWall(true, false, false, false);
        getTileAt(4, 0).setWall(true, false, false, false);
        getTileAt(6, 0).setWall(true, false, false, false);
        getTileAt(7, 0).setWall(true, false, false, false);
        getTileAt(8, 0).setWall(true, false, false, false);
        getTileAt(9, 0).setWall(true, false, false, false);
        getTileAt(10, 0).setWall(true, false, false, false);
        getTileAt(11, 0).setWall(true, false, false, false);
        getTileAt(12, 0).setWall(true, false, false, false);
        getTileAt(13, 0).setWall(true, false, false, false);
        getTileAt(14, 0).setWall(true, false, false, false);
        getTileAt(15, 0).setWall(true, false, false, true);

        getTileAt(0, 1).setWall(false, false, true, false);
        getTileAt(0, 2).setWall(false, false, true, false);
        getTileAt(0, 3).setWall(false, false, true, false);
        getTileAt(0, 4).setWall(true, false, true, false);
        getTileAt(0, 5).setWall(false, false, true, false);
        getTileAt(0, 6).setWall(false, false, true, false);
        getTileAt(0, 7).setWall(false, false, true, false);
        getTileAt(0, 8).setWall(false, false, true, false);
        getTileAt(0, 9).setWall(false, false, true, false);
        getTileAt(0, 10).setWall(true, false, true, false);
        getTileAt(0, 11).setWall(false, false, true, false);
        getTileAt(0, 12).setWall(false, false, true, false);
        getTileAt(0, 13).setWall(false, false, true, false);
        getTileAt(0, 14).setWall(false, false, true, false);
        getTileAt(0, 15).setWall(false, true, true, false);

        getTileAt(15, 1).setWall(false, false, false, true);
        getTileAt(15, 2).setWall(false, false, false, true);
        getTileAt(15, 3).setWall(false, false, true, true);
        getTileAt(15, 4).setWall(false, false, false, true);
        getTileAt(15, 5).setWall(true, false, false, true);
        getTileAt(15, 6).setWall(false, false, false, true);
        getTileAt(15, 7).setWall(false, false, false, true);
        getTileAt(15, 8).setWall(false, false, false, true);
        getTileAt(15, 9).setWall(false, false, false, true);
        getTileAt(15, 10).setWall(false, false, false, true);
        getTileAt(15, 11).setWall(false, false, false, true);
        getTileAt(15, 12).setWall(true, false, false, true);
        getTileAt(15, 13).setWall(false, false, false, true);
        getTileAt(15, 14).setWall(false, false, false, true);
        getTileAt(15, 15).setWall(false, true, false, true);

        getTileAt(1, 15).setWall(false, true, false, false);
        getTileAt(2, 15).setWall(false, true, true, false);
        getTileAt(3, 15).setWall(false, true, false, false);
        getTileAt(4, 15).setWall(true, true, false, false);
        getTileAt(5, 15).setWall(false, true, false, false);
        getTileAt(6, 15).setWall(false, true, false, false);
        getTileAt(7, 15).setWall(false, true, false, false);
        getTileAt(8, 15).setWall(false, true, false, false);
        getTileAt(9, 15).setWall(false, true, false, false);
        getTileAt(10, 15).setWall(false, true, false, false);
        getTileAt(11, 15).setWall(false, true, false, false);
        getTileAt(12, 15).setWall(false, true, true, false);
        getTileAt(13, 15).setWall(true, true, false, false);
        getTileAt(14, 15).setWall(false, true, false, false);


        // Murs intérieurs

        getTileAt(1, 14).setWall(true, false, false, false);

        getTileAt(2, 13).setWall(false, false, true, false);
        getTileAt(2, 6).setWall(true, false, false, false);

        getTileAt(3, 5).setWall(false, false, true, false);
        getTileAt(3, 9).setWall(true, false, true, false);

        getTileAt(4, 2).setWall(false, false, true, false);
        getTileAt(4, 3).setWall(true, false, false, false);
        getTileAt(4, 14).setWall(false, false, true, false);

        getTileAt(5, 0).setWall(true, false, true, false);
        getTileAt(5, 7).setWall(true, false, false, false);

        getTileAt(6, 1).setWall(true, false, true, false);
        getTileAt(6, 7).setWall(false, false, true, true);
        getTileAt(6, 8).setWall(false, false, false, true);
        getTileAt(6, 12).setWall(true, false, false, false);

        //Center
        getTileAt(7, 6).setWall(false, true, false, false);
        getTileAt(7, 9).setWall(true, false, false, false);
        getTileAt(7, 12).setWall(false, false, true, false);


        getTileAt(8, 6).setWall(false, true, false, false);
        getTileAt(8, 9).setWall(true, false, false, false);
        //end center
        getTileAt(9, 4).setWall(true, false, false, false);
        getTileAt(9, 7).setWall(false, false, true, false);
        getTileAt(9, 8).setWall(false, false, true, false);
        getTileAt(9, 13).setWall(true, false, false, false);

        getTileAt(10, 4).setWall(false, false, true, false);
        getTileAt(10, 6).setWall(true, false, true, false);
        getTileAt(10, 13).setWall(false, false, true, false);

        getTileAt(11, 0).setWall(true, false, true, false);
        getTileAt(11, 10).setWall(true, false, false, false);

        getTileAt(12, 7).setWall(false, false, true, false);
        getTileAt(12, 8).setWall(true, false, false, false);
        getTileAt(12, 9).setWall(false, false, true, false);

        getTileAt(13, 1).setWall(false, false, true, false);
        getTileAt(13, 2).setWall(true, false, false, false);
        getTileAt(13, 14).setWall(false, false, true, false);

        getTileAt(14, 4).setWall(true, false, false, false);
        getTileAt(14, 10).setWall(true, false, true, false);

    }

    //Ajout des symbol a des position static
    private void addSymbols() {
        //Ajout des symboles rouges
        addSymbol(0, 4, SymbolsRessources.redSymbols[0], RED);
        addSymbol(15, 5, SymbolsRessources.redSymbols[1], RED);
        addSymbol(11, 0, SymbolsRessources.redSymbols[2], RED);
        addSymbol(2, 5, SymbolsRessources.redSymbols[3], RED);
        //Ajout des symboles blues
        addSymbol(5, 7, SymbolsRessources.blueSymbols[0], BLUE);
        addSymbol(14, 3, SymbolsRessources.blueSymbols[1], BLUE);
        addSymbol(10, 6, SymbolsRessources.blueSymbols[2], BLUE);
        addSymbol(1, 13, SymbolsRessources.blueSymbols[3], BLUE);

        //Ajout des symboles jaunes
        addSymbol(3, 9, SymbolsRessources.yellowSymbols[0], YELLOW);
        addSymbol(6, 12, SymbolsRessources.yellowSymbols[1], YELLOW);
        addSymbol(4, 2, SymbolsRessources.yellowSymbols[2], YELLOW);
        addSymbol(9, 4, SymbolsRessources.yellowSymbols[3], YELLOW);

        //Ajout des symboles verts
        addSymbol(13, 14, SymbolsRessources.greenSymbols[0], GREEN);
        addSymbol(14, 10, SymbolsRessources.greenSymbols[1], GREEN);
        addSymbol(4, 14, SymbolsRessources.greenSymbols[2], GREEN);
        addSymbol(6, 1, SymbolsRessources.greenSymbols[3], GREEN);
    }
    //Ajout des symboles
    public void addSymbol(int row, int col, String image, Token.Color color) {
        Token symbol = new Symbol(color);
        symbol.setPosition(col, row);
        var symbolGui = new ImageView(new Image(
                image,
                TILE_SIZE, TILE_SIZE, false, true
        ));

        boardPane.add(symbolGui, symbol.getCol(), symbol.getLig());
        // Association de l' "ImageView" avec le robot stocké dans le jeu
        symbol.setGui(symbolGui);

        Game.context.getSymbols().add(symbol);
    }

    //Actualiser l'objectif
    private void refreshTarget() {
        //Enlever "l'enfant" précèdent
        targetDisplayed.getChildren().clear();
        //Nouvelle objectif
        var random = new Random();
        var index = random.nextInt(Game.context.getSymbols().size());
        var newTarget = Game.context.getSymbols().get(index);

        var targetGui = new ImageView(new Image(
                ((ImageView) newTarget.getGui()).getImage().getUrl(),
                TILE_SIZE * 2, TILE_SIZE * 2, false, true
        ));
        Image centerImage = new Image("center.png", TILE_SIZE * 2, TILE_SIZE * 2, false, false);
        ImageView tileBackground = new ImageView(centerImage);
        targetDisplayed.getChildren().add(tileBackground);
        targetDisplayed.getChildren().add(targetGui);

        Game.context.setTarget((Symbol) newTarget);

    }

    //Méthode du timer
    private void startTimer() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (Game.context.getStatus() != Game.Status.CHOOSE_PLAYER) {
                    Platform.runLater(()-> rejouerBtn.setDisable(false));
                    var newValue = Game.context.TIME_TO_CATCH.getValue() - 1;
                    if (newValue == 0) {

                        Platform.runLater(() -> {
                            Alert startMessage
                                    = new Alert(Alert.AlertType.INFORMATION, "PERDU !!");
                            startMessage.setHeaderText(null);
                            startMessage.setGraphic(null);
                            startMessage.show();
                            restartGame();
                            Game.context.setStatus(Game.Status.CHOOSE_PLAYER);
                        });

                    }
                    if (newValue > 0) {
                        Platform.runLater(() -> Game.context.TIME_TO_CATCH.setValue(newValue));

                    }

                } else {
                    //Cacher le bouton restart
                    Platform.runLater(()-> rejouerBtn.setDisable(true));
                }

            }

        }, 0, 1000);
    }

    //Méthode pour reset la position des persos
    public void resetRobotsPositions(){
        Game.context.getRobots().forEach((color,robot) ->{
            getTileAt(robot.getLig() ,robot.getCol()).setAvailable(true);
            Random random = new Random();
            var row = random.nextInt(Game.SIZE);
            var col = random.nextInt(Game.SIZE);
            robot.setPosition(col, row);
            getTileAt(row ,col).setAvailable(false);
            GridPane.setConstraints(robot.getGui(), robot.getCol(), robot.getLig());
        });
    }

    //Méthode pour recommencer le jeu
    public void restartGame() {
        this.refreshTarget();
        Game.context.resetTimer();
        Game.context.resetSteps();
        resetRobotsPositions();
    }

    //Méthode pour restart le jeu
    public void restartGameAction(ActionEvent actionEvent) {
        //TODO
        System.out.println("Le jeu restart");
        Platform.runLater(this::restartGame);
    }
}
