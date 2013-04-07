package earthinvaders;

import javax.swing.ImageIcon;


public class Kunai extends Sprite {

    private String kunai = "../pix/kunai.png";
    private final int H_SPACE = 6;
    private final int V_SPACE = 1;

    public Kunai() {
    }

    public Kunai(int x, int y) {

        ImageIcon ii = new ImageIcon(this.getClass().getResource(kunai));
        setImage(ii.getImage());
        setX(x + H_SPACE);
        setY(y - V_SPACE);
    }
}