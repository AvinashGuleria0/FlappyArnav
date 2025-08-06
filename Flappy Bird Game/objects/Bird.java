package objects;

import java.awt.*;
import javax.swing.ImageIcon;

public class Bird {

    private int x, y;
    private int width = 50;
    private int height = 50;

    private float velocity = 0;
    private final float gravity = 0.5f;
    private final float jumpStrength = -8;
    private Image birdImage;

    public Bird(int x, int y) {
        this.x = x;
        this.y = y;
        // birdImage = new ImageIcon(getClass().getResource("/bird.png")).getImage();
        new ImageIcon("res/bird.png");
    }

    public void update() {
        velocity += gravity;
        y += velocity;

        if (y + height >= 600) {
            y = 600 - height;
        }
    }

    public void jump() {
        velocity = jumpStrength;
    }

    public void draw(Graphics g) {
        // g.setColor(Color.RED);
        // g.fillOval(x, y, width, height);
        g.drawImage(birdImage, x, y, width, height, null);
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
