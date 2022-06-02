module isep.jfx {
    requires javafx.controls;
    requires javafx.fxml;
    opens isep.jfx to javafx.fxml;
    exports isep.jfx;

    requires transitive javafx.base;
    requires transitive javafx.graphics;
    requires transitive java.desktop;
}