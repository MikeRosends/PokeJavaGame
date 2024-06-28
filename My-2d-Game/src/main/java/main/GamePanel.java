package main;

import entity.Player;
import object.SuperObject;
import tile.TileManager;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {

    // SCREEN SETTINGS
    final int originalTileSze = 16; // 16x16 tile - every tile is made with this tile size

    // SCALE IT
    final int scale = 3;
    public final int tileSize = originalTileSze * scale; // now looks like a 48x48 tile

    // SCREEN SIZE 4:3
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; // 768 Pixels
    public final int screenHigth = tileSize * maxScreenRow; // 576 Pixels

    // WORLD SETTINGS
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    public final int worldWidth = tileSize * maxWorldCol;
    public final int worldHeight = tileSize * maxWorldRow;


    //FPS
    int FPS = 60;


    TileManager tileM = new TileManager(this);
    KeyHandler keyH = new KeyHandler();
    Thread gameThread;
    public CollisionChecker collisionChecker = new CollisionChecker(this);
    public AssetSetter aSetter = new AssetSetter(this);
    public Player player = new Player(this,keyH);
    public SuperObject obj[] = new SuperObject[10]; // This means we can only display up to 10 objects on the screen at the same time - 10 is so the game is not too slow

    public GamePanel() {

        this.setPreferredSize(new Dimension(screenWidth, screenHigth));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
    }

    public void setUpGame() {

        aSetter.setObject();
    }

    public void startGameThread() {

        gameThread = new Thread(this); // Passing the main.GamePanel to the thread constructor - how to instantiate a thread
        gameThread.start(); // calling the run method automatically
    }

    // GAME LOOP
    @Override
    /*public void run() {

        // 1sec - drawing the creen 60 times per sec
        double drawInterval = 1000000000 / FPS; // 0.01666 secs
        double nextDrawTime = System.nanoTime() + drawInterval; // when the internal system time hits the nextDrawTime it draws the screen again

        // Repeats the process that is written in this while loop as long as this gameThread exists
        while (gameThread != null) {

            long currentTime = System.nanoTime();
            System.out.println("current time:" + currentTime);

            // 1. UPDATE: update information such as character position - 60 times per second (60FPS)
            update();

            // 2. Draw the screen with the updated information
            repaint();

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime / 1000000;

                *//*
                  If this update() and repaint() take more than this
                  drawInterval then no time is left. this Thread
                  doesn't need to sleep since we already used the
                  allocated time
                 *//*
                if (remainingTime < 0) {
                    remainingTime = 0;
                }

                Thread.sleep((long) remainingTime);

                nextDrawTime += drawInterval;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

}
            */

    public void run() {

        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;


        while (gameThread != null) {

            currentTime = System.nanoTime(); // Check the current time

            delta += (currentTime - lastTime) / drawInterval; // How much time has passed devided by this drawInterval
            timer += (currentTime - lastTime);
            lastTime = currentTime;


            // 1 = drawInterval
            // Every loop we add the passed time, divided by drawInterval to delta.
            // When this delta reaches this drawInterval then we update() and repaint()
            // Then reset this delta
            if (delta >= 1) {
                update();
                repaint();
                delta--;
                drawCount++;
            }

            if (timer >= 1000000000) {
                // System.out.println("FPS: " + drawCount);
                drawCount = 0;
                timer = 0;
            }
        }
    }

    public void update() {

        player.update();
    }

    // this is one of the standard methods to draw thing on JPanel - its built-in
    public void paintComponent(Graphics g) {

        // The parent class is JPanel - main.GamePanel is a subclass of JPanel / it is called with "repaint()"
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g; // Graphics2D has more functions than Graphics

        // TILE
            // Draw by layers
        tileM.draw(g2);

        // OBJECT
        for (int i = 0; i < obj.length; i++) {
            if (obj[i] != null) {
                obj[i].draw(g2, this);
            }
        }

        // PLAYER
        player.draw(g2);


        g2.dispose(); // to save memory
    }
}
