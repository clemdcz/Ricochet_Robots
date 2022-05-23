package isep.jfx;

import isep.ricrob.Game;
import isep.ricrob.Tile;
import isep.ricrob.Token;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static isep.ricrob.Token.Color.*;

public class MainController {

    public final int TILE_SIZE = 40;

    @FXML
    public GridPane boardPane;

    @FXML
    public Label statusLabel;

    List<Tile> tiles;

    // "initialize()" est appelé par JavaFX à l'affichage de la fenêtre
    @FXML
    public void initialize() {

        // Affichage un message "bloquant"
        showWarning("Ricochet Robots");

        // Construction du plateau
        Image tile = new Image("cell.png", TILE_SIZE, TILE_SIZE, false, true);
        Image wl = new Image("WL.png", TILE_SIZE, TILE_SIZE, false, true);
        Image wr = new Image("WR.png", TILE_SIZE, TILE_SIZE, false, true);
        Image wu = new Image("WU.png", TILE_SIZE, TILE_SIZE, false, true);
        Image wd = new Image("WD.png", TILE_SIZE, TILE_SIZE, false, true);


        // ... "cell.png" doit être placé à la racine de "resources/" (sinon PB)
        boardPane.setPadding(new Insets(2));
       // boardPane.setBackground( new Background(new BackgroundFill(Color.BLACK,CornerRadii.EMPTY, Insets.EMPTY)));

        int rows = Game.SIZE;
        int cols = Game.SIZE;
        //rows = cols = 4;
        tiles = new ArrayList<>();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                ImageView tileGui = new ImageView(tile);
                final int finalRow = row;
                final int finalCol = col;
                tileGui.setOnMouseClicked
                        (event -> {
                            String status = Game.context.processSelectTile
                                    (finalRow, finalCol);
                            if ( "MOVE".equals(status)) {
                                updateSelectedRobotPosition();
                            } else if (status != null) {
                                showWarning(status);
                            }
                        });

                ImageView wlUL = new ImageView(wl);
                ImageView wrUL = new ImageView(wr);
                ImageView wuUL = new ImageView(wu);
                ImageView wdUL = new ImageView(wd);

                //wrUL.setX(-5);


                Pane pane = new Pane();
                pane.setPrefWidth(40);
                pane.setPrefHeight(40);
                //System.out.println(pane.widthProperty());
                tileGui.fitWidthProperty().bind(pane.widthProperty());
                pane.setLayoutX(0);
                pane.setLayoutY(0);
                wdUL.fitWidthProperty().bind(pane.widthProperty());
                wrUL.setX(0);


                pane.getChildren().addAll(tileGui,wlUL,wrUL,wuUL,wdUL);
                tiles.add(new Tile(pane,row,col,wlUL,wrUL,wuUL,wdUL));

                boardPane.add(pane, col, row);
            }
        }

        // Setup des murs ici

        // Murs extérieurs
        getTileAt(0,0).setWall(true,false,true,false);
        getTileAt(1,0).setWall(true,false,false,false);
        getTileAt(2,0).setWall(true,false,false,false);
        getTileAt(3,0).setWall(true,false,false,false);
        getTileAt(4,0).setWall(true,false,false,false);
        getTileAt(6,0).setWall(true,false,false,false);
        getTileAt(7,0).setWall(true,false,false,false);
        getTileAt(8,0).setWall(true,false,false,false);
        getTileAt(9,0).setWall(true,false,false,false);
        getTileAt(10,0).setWall(true,false,false,false);
        getTileAt(11,0).setWall(true,false,false,false);
        getTileAt(12,0).setWall(true,false,false,false);
        getTileAt(13,0).setWall(true,false,false,false);
        getTileAt(14,0).setWall(true,false,false,false);
        getTileAt(15,0).setWall(true,false,false,true);

        getTileAt(0,1).setWall(false,false,true,false);
        getTileAt(0,2).setWall(false,false,true,false);
        getTileAt(0,3).setWall(false,false,true,false);
        getTileAt(0,4).setWall(true,false,true,false);
        getTileAt(0,5).setWall(false,false,true,false);
        getTileAt(0,6).setWall(false,false,true,false);
        getTileAt(0,7).setWall(false,false,true,false);
        getTileAt(0,8).setWall(false,false,true,false);
        getTileAt(0,9).setWall(false,false,true,false);
        getTileAt(0,10).setWall(true,false,true,false);
        getTileAt(0,11).setWall(false,false,true,false);
        getTileAt(0,12).setWall(false,false,true,false);
        getTileAt(0,13).setWall(false,false,true,false);
        getTileAt(0,14).setWall(false,false,true,false);
        getTileAt(0,15).setWall(false,true,true,false);

        getTileAt(15,1).setWall(false,false,false,true);
        getTileAt(15,2).setWall(false,false,false,true);
        getTileAt(15,3).setWall(false,false,true,true);
        getTileAt(15,4).setWall(false,false,false,true);
        getTileAt(15,5).setWall(true,false,false,true);
        getTileAt(15,6).setWall(false,false,false,true);
        getTileAt(15,7).setWall(false,false,false,true);
        getTileAt(15,8).setWall(false,false,false,true);
        getTileAt(15,9).setWall(false,false,false,true);
        getTileAt(15,10).setWall(false,false,false,true);
        getTileAt(15,11).setWall(false,false,false,true);
        getTileAt(15,12).setWall(true,false,false,true);
        getTileAt(15,13).setWall(false,false,false,true);
        getTileAt(15,14).setWall(false,false,false,true);
        getTileAt(15,15).setWall(false,true,false,true);

        getTileAt(1,15).setWall(false,true,false,false);
        getTileAt(2,15).setWall(false,true,true,false);
        getTileAt(3,15).setWall(false,true,false,false);
        getTileAt(4,15).setWall(true,true,false,false);
        getTileAt(5,15).setWall(false,true,false,false);
        getTileAt(6,15).setWall(false,true,false,false);
        getTileAt(7,15).setWall(false,true,false,false);
        getTileAt(8,15).setWall(false,true,false,false);
        getTileAt(9,15).setWall(false,true,false,false);
        getTileAt(10,15).setWall(false,true,false,false);
        getTileAt(11,15).setWall(false,true,false,false);
        getTileAt(12,15).setWall(false,true,true,false);
        getTileAt(13,15).setWall(true,true,false,false);
        getTileAt(14,15).setWall(false,true,false,false);


        // Murs intérieurs

        getTileAt(1,14).setWall(true,false,false,false);
        //getTileAt(1,13).setWall(false,false,false,true);
        getTileAt(2,13).setWall(false,false,true,false);

        getTileAt(2,6).setWall(true,false,false,false);

        getTileAt(3,5).setWall(false,false,true,false);
        getTileAt(3,9).setWall(true,false,true,false);

        getTileAt(4,2).setWall(false,false,true,false);
        getTileAt(4,3).setWall(true,false,false,false);
        getTileAt(4,14).setWall(false,false,true,false);

        getTileAt(5,0).setWall(true,false,true,false);
        getTileAt(5,7).setWall(true,false,false,false);

        getTileAt(6,1).setWall(true,false,true,false);
        getTileAt(6,7).setWall(false,false,true,false);
        getTileAt(6,12).setWall(true,false,false,false);

        getTileAt(7,7).setWall(true,false,true,false);
        getTileAt(7,8).setWall(false,false,true,false);
        getTileAt(7,9).setWall(true,false,false,false);
        getTileAt(7,12).setWall(false,false,true,false);


        getTileAt(8,7).setWall(true,false,false,false);
        getTileAt(8,9).setWall(true,false,false,false);

        getTileAt(9,4).setWall(true,false,false,false);
        getTileAt(9,7).setWall(false,false,true,false);
        getTileAt(9,8).setWall(false,false,true,false);
        getTileAt(9,13).setWall(true,false,false,false);

        getTileAt(10,4).setWall(false,false,true,false);
        getTileAt(10,6).setWall(true,false,true,false);
        getTileAt(10,13).setWall(false,false,true,false);

        getTileAt(11,0).setWall(true,false,true,false);
        getTileAt(11,10).setWall(true,false,false,false);

        getTileAt(12,7).setWall(false,false,true,false);
        getTileAt(12,8).setWall(true,false,false,false);
        getTileAt(12,9).setWall(false,false,true,false);

        getTileAt(13,1).setWall(false,false,true,false);
        getTileAt(13,2).setWall(true,false,false,false);
        getTileAt(13,14).setWall(false,false,true,false);

        getTileAt(14,4).setWall(true,false,false,false);
        getTileAt(14,10).setWall(true,false,true,false);



        // Ajout des pièces
        addRobot(RED);
        addRobot(GREEN);
        addRobot(BLUE);
        addRobot(YELLOW);
//
//        boardPane.add(
//                new ImageView( new Image(
//                        Game.context.getTarget().getColor() + "_target.png",
//                        TILE_SIZE, TILE_SIZE, false, true
//                ) ),
//                Game.context.getTarget().getCol(),
//                Game.context.getTarget().getLig()
//        );

        // "Binding JFX" - Synchronisation du "Label" avec l'état du jeu
        statusLabel.textProperty().bind(Game.context.statusToolTipProperty);

    }

    private Tile getTileAt(int row,int col){
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

    private void addRobot(Token.Color color) {
        Token robot = Game.context.getRobots().get(color);
        ImageView robotGui = new ImageView( new Image(
                color.name() + "_robot.png",
                TILE_SIZE, TILE_SIZE, false, true
        ) );
        robotGui.setOnMouseClicked
                (event -> Game.context.processSelectRobot(color));
        boardPane.add(robotGui, robot.getCol(), robot.getLig());
        // Association de l' "ImageView" avec le robot stocké dans le jeu
        robot.setGui(robotGui);
    }

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
}
