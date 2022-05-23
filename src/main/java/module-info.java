module isep.jfx {
    requires javafx.controls;
    requires javafx.fxml;


    opens isep.jfx to javafx.fxml;
    exports isep.jfx;
}