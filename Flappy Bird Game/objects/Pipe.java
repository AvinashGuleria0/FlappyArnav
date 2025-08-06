package objects;

import java.awt.*;
import  java.util.*;

public class Pipe {
    private int x;
    private int width = 57;
    private int gap = 200;
    public static int speed = 8;

    private int topHeight;
    private int bottomY;

    public static final int SCREEN_HEIGHT = 700;

    public Pipe(int startX) {
        this.x = startX;

        Random rand = new Random();
        topHeight = 70 + rand.nextInt(200);
        bottomY = topHeight + gap;
    }

    public void update() {
        x -= speed;
    }

    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        // Pipe colors
        Color outerGreen = new Color(0, 100, 0); // Dark green border
        Color midGreen = new Color(0, 180, 0); // Pipe body
        Color lightGreen = new Color(80, 255, 80); // Highlight stripe

        g2.setColor(outerGreen);
        g2.fillRect(x - 2, 0, width + 4, topHeight);

        // Inner body
        g2.setColor(midGreen);
        g2.fillRect(x, 0, width, topHeight);

        // Vertical highlight
        g2.setColor(lightGreen);
        g2.fillRect(x + width / 4, 0, 4, topHeight); // small highlight stripe

        // Pipe cap
        g2.setColor(outerGreen);
        g2.fillRect(x - 5, topHeight - 10, width + 10, 15);

        // Outer border
        g2.setColor(outerGreen); 
        g2.fillRect(x - 2, bottomY, width + 4, SCREEN_HEIGHT - bottomY);

        // Inner body
        g2.setColor(midGreen);
        g2.fillRect(x, bottomY, width, SCREEN_HEIGHT - bottomY);

        // Highlight
        g2.setColor(lightGreen);
        g2.fillRect(x + width / 4, bottomY, 4, SCREEN_HEIGHT - bottomY);

        // Bottom cap
        g2.setColor(outerGreen);
        g2.fillRect(x - 5, bottomY, width + 10, 15);
    }

    public boolean isOffScreen() {
        return x + width < 0;
    }

    public int getX() {
        return x;
    }

    public int getWidth() {
        return width;
    }

    public int getTopHeight() {
        return topHeight;
    }

    public int getGap() {
        return gap;
    }

}