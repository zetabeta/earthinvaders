package earthinvaders;

import javax.swing.ImageIcon;


public class DarkNinja extends Sprite {

    private Star star;
    private final String ninja = "../pix/ninja.png";

    public DarkNinja(int x, int y) {
        this.x = x;
        this.y = y;

        star = new Star(x, y);
        ImageIcon ii = new ImageIcon(this.getClass().getResource(ninja));
        setImage(ii.getImage());

    }

    public void act(int direction) {
        this.x += direction;
    }

    public Star getStar() {
        return star;
    }

    public class Star extends Sprite {

        private final String star = "../pix/star.png";
        private boolean destroyed;

        public Star(int x, int y) {
            setDestroyed(true);
            this.x = x;
            this.y = y;
            ImageIcon ii = new ImageIcon(this.getClass().getResource(star));
            setImage(ii.getImage());
        }

        public void setDestroyed(boolean destroyed) {
            this.destroyed = destroyed;
        }

        public boolean isDestroyed() {
            return destroyed;
        }
    }
}