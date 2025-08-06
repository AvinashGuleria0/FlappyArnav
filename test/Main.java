import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Create frame
        JFrame frame = new JFrame("Frame with Background Image");
        frame.setSize(900, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add custom panel with background
        frame.add(new BackgroundPanel());

        // Show frame
        frame.setVisible(true);
    }
}

// Custom JPanel that draws a background image
class BackgroundPanel extends JPanel {
    private BufferedImage backgroundImage;

    public BackgroundPanel() {
        try {
            backgroundImage = ImageIO.read(new File("bac.png"));
            System.out.println("✅ Image loaded successfully.");
        } catch (IOException e) {
            System.out.println("❌ Failed to load image: " + e.getMessage());
            e.printStackTrace(); // show full error
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
