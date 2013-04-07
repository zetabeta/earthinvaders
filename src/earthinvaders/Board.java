package earthinvaders;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JPanel;


public class Board extends JPanel implements Runnable, Commons { 


	private static final long serialVersionUID = 1L;
	private Dimension d;
    private ArrayList<DarkNinja> ninjas;
    private Player player;
    private Kunai kunai;

    private int ninjaX = 150;
    private int ninjaY = 5;
    private int direction = -1;
    private int deaths = 0;

    private boolean ingame = true;
    private final String kaboom = "../pix/explosion.png";
    private final String ninjapix = "../pix/ninja.png";
    private String message = "Game Over";

    private Thread animator;

    public Board() 
    {

        addKeyListener(new TAdapter());
        setFocusable(true);
        d = new Dimension(BOARD_WIDTH, BOARD_HEIGTH);
        setBackground(Color.black);

        gameInit();
        setDoubleBuffered(true);
    }

    public void addNotify() {
        super.addNotify();
        gameInit();
    }

    public void gameInit() {

        ninjas = new ArrayList<DarkNinja>();

        ImageIcon ii = new ImageIcon(this.getClass().getResource(ninjapix));

        for (int i=0; i < 4; i++) {
            for (int j=0; j < 6; j++) {
                DarkNinja ninja = new DarkNinja(ninjaX + NINJA_WIDTH*j, ninjaY + NINJA_HEIGHT*i);
                ninja.setImage(ii.getImage());
                ninjas.add(ninja);
            }
        }

        player = new Player();
        kunai = new Kunai();

        if (animator == null || !ingame) {
            animator = new Thread(this);
            animator.start();
        }
    }

    public void drawNinjas(Graphics g) 
    {
        Iterator<DarkNinja> it = ninjas.iterator();

        while (it.hasNext()) {
            DarkNinja ninja = (DarkNinja) it.next();

            if (ninja.isVisible()) {
                g.drawImage(ninja.getImage(), ninja.getX(), ninja.getY(), this);
            }

            if (ninja.isDying()) {
                ninja.die();
            }
        }
    }

    public void drawPlayer(Graphics g) {

        if (player.isVisible()) {
            g.drawImage(player.getImage(), player.getX(), player.getY(), this);
        }

        if (player.isDying()) {
            player.die();
            ingame = false;
        }
    }

    public void drawShot(Graphics g) {
        if (kunai.isVisible())
            g.drawImage(kunai.getImage(), kunai.getX(), kunai.getY(), this);
    }

    public void drawBombing(Graphics g) {

        Iterator<DarkNinja> i3 = ninjas.iterator();

        while (i3.hasNext()) {
            DarkNinja a = (DarkNinja) i3.next();

            DarkNinja.Star b = a.getStar();

            if (!b.isDestroyed()) {
                g.drawImage(b.getImage(), b.getX(), b.getY(), this); 
            }
        }
    }

    public void paint(Graphics g)
    {
      super.paint(g);

      g.setColor(Color.darkGray);
      g.fillRect(0, 0, d.width, d.height);
      g.setColor(Color.green);   

      if (ingame) {

        g.drawLine(0, GROUND, BOARD_WIDTH, GROUND);
        drawNinjas(g);
        drawPlayer(g);
        drawShot(g);
        drawBombing(g);
      }

      Toolkit.getDefaultToolkit().sync();
      g.dispose();
    }

    public void gameOver()
    {

        Graphics g = this.getGraphics();

        g.setColor(Color.black);
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGTH);

        Font small = new Font("Helvetica", Font.BOLD, 24);
        FontMetrics metr = this.getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(message, (BOARD_WIDTH - metr.stringWidth(message))/2, 
            BOARD_HEIGTH/2);
    }

    public void animationCycle()  {

        if (deaths == NUMBER_OF_NINJAS_TO_DESTROY) {
            ingame = false;
            message = "Game won!";
        }

        // player

        player.act();

        // shot
        if (kunai.isVisible()) {
            Iterator<DarkNinja> it = ninjas.iterator();
            int shotX = kunai.getX();
            int shotY = kunai.getY();

            while (it.hasNext()) {
                DarkNinja ninja = (DarkNinja) it.next();
                int ninjaX = ninja.getX();
                int ninjaY = ninja.getY();

                if (ninja.isVisible() && kunai.isVisible()) {
                    if (shotX >= (ninjaX) && 
                        shotX <= (ninjaX + NINJA_WIDTH) &&
                        shotY >= (ninjaY) &&
                        shotY <= (ninjaY+NINJA_HEIGHT) ) {
                            ImageIcon ii = 
                                new ImageIcon(getClass().getResource(kaboom));
                            ninja.setImage(ii.getImage());
                            ninja.setDying(true);
                            deaths++;
                            kunai.die();
                        }
                }
            }

            int y = kunai.getY();
            y -= 4;
            if (y < 0)
                kunai.die();
            else kunai.setY(y);
        }

        // ninjas

         Iterator<DarkNinja> it1 = ninjas.iterator();

         while (it1.hasNext()) {
             DarkNinja a1 = (DarkNinja) it1.next();
             int x = a1.getX();

             if (x  >= BOARD_WIDTH - BORDER_RIGHT && direction != -1) {
                 direction = -1;
                 Iterator<DarkNinja> i1 = ninjas.iterator();
                 while (i1.hasNext()) {
                     DarkNinja a2 = (DarkNinja) i1.next();
                     a2.setY(a2.getY() + GO_DOWN);
                 }
             }

            if (x <= BORDER_LEFT && direction != 1) {
                direction = 1;

                Iterator<DarkNinja> i2 = ninjas.iterator();
                while (i2.hasNext()) {
                    DarkNinja a = (DarkNinja)i2.next();
                    a.setY(a.getY() + GO_DOWN);
                }
            }
        }


        Iterator<DarkNinja> it = ninjas.iterator();

        while (it.hasNext()) {
            DarkNinja ninja = (DarkNinja) it.next();
            if (ninja.isVisible()) {

                int y = ninja.getY();

                if (y > GROUND - NINJA_HEIGHT) {
                    ingame = false;
                    message = "Invasion!";
                }

                ninja.act(direction);
            }
        }

        // dragon stars

        Iterator<DarkNinja> i3 = ninjas.iterator();
        Random generator = new Random();

        while (i3.hasNext()) {
            int shot = generator.nextInt(15);
            DarkNinja a = (DarkNinja) i3.next();
            DarkNinja.Star b = a.getStar();
            if (shot == CHANCE && a.isVisible() && b.isDestroyed()) {

                b.setDestroyed(false);
                b.setX(a.getX());
                b.setY(a.getY());   
            }

            int starX = b.getX();
            int starY = b.getY();
            int playerX = player.getX();
            int playerY = player.getY();

            if (player.isVisible() && !b.isDestroyed()) {
                if ( starX >= (playerX) && 
                    starX <= (playerX+PLAYER_WIDTH) &&
                    starY >= (playerY) && 
                    starY <= (playerY+PLAYER_HEIGHT) ) {
                        ImageIcon ii = 
                            new ImageIcon(this.getClass().getResource(kaboom));
                        player.setImage(ii.getImage());
                        player.setDying(true);
                        b.setDestroyed(true);;
                    }
            }

            if (!b.isDestroyed()) {
                b.setY(b.getY() + 1);   
                if (b.getY() >= GROUND - BOMB_HEIGHT) {
                    b.setDestroyed(true);
                }
            }
        }
    }

    public void run() {

        long beforeTime, timeDiff, sleep;

        beforeTime = System.currentTimeMillis();

        while (ingame) {
            repaint();
            animationCycle();

            timeDiff = System.currentTimeMillis() - beforeTime;
            sleep = DELAY - timeDiff;

            if (sleep < 0) 
                sleep = 2;
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                System.out.println("interrupted");
            }
            beforeTime = System.currentTimeMillis();
        }
        gameOver();
    }

    private class TAdapter extends KeyAdapter {

        public void keyReleased(KeyEvent e) {
            player.keyReleased(e);
        }

        public void keyPressed(KeyEvent e) {

          player.keyPressed(e);

          int x = player.getX();
          int y = player.getY();

          if (ingame)
          {
            if (e.isAltDown()) {
                if (!kunai.isVisible())
                    kunai = new Kunai(x, y);
            }
          }
        }
    }
}