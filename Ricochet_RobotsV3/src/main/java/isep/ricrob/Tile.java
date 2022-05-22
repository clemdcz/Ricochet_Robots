package isep.ricrob;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class Tile {

    Pane pane;
    int row, col;
    ImageView wallLeft,wallRight,wallUp,wallDown;

    public Tile(Pane pane, int row, int col, ImageView wallLeft, ImageView wallRight, ImageView wallUp, ImageView wallDown) {
        this.pane = pane;
        this.row = row;
        this.col = col;
        this.wallLeft = wallLeft;
        this.wallRight = wallRight;
        this.wallUp = wallUp;
        this.wallDown = wallDown;

        wallLeft.setVisible(false);
        wallRight.setVisible(false);
        wallUp.setVisible(false);
        wallDown.setVisible(false);
    }

    public void setWall(boolean left,boolean right,boolean up,boolean down){
        wallLeft.setVisible(left);
        wallRight.setVisible(right);
        wallUp.setVisible(up);
        wallDown.setVisible(down);
    }
}
