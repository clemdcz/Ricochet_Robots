package isep.jfx;

import isep.ricrob.Game;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PlayerController {

    @FXML
    TextField nameTextField;

    @FXML
    public void initialize() {

        // "Binding JFX" - Synchronisation du "Label" avec l'état du jeu
        nameTextField.textProperty()
                .bindBidirectional(Game.context.playerNameProperty);

    }

    @FXML
    public void confirm(ActionEvent actionEvent) {
        if (
                ( nameTextField.getText() != null )
            &&
                ( ! nameTextField.getText().isBlank() )
            ) {
            Game.context.processSelectPlayer();
            // Fermeture de la boite de dialogue
            Node node = (Node) actionEvent.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            stage.close();
        }
    }

}
