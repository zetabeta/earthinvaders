
package earthinvaders;

import javax.swing.JFrame;

public class EarthInvaders extends JFrame implements Commons {

	private static final long serialVersionUID = 1L;

	public EarthInvaders()
    {
        add(new Board());
        setTitle("Earth Invaders");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(BOARD_WIDTH, BOARD_HEIGTH);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
    }

    public static void main(String[] args) {
        new EarthInvaders();
    }
}
