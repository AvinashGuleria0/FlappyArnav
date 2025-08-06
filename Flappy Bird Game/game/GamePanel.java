package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;
import objects.Bird;
import objects.Pipe;

public class GamePanel extends JPanel implements ActionListener, java.awt.event.KeyListener {

    private Bird bird;
    private Timer timer;
    public static final int WIDTH = 500;
    public static final int HEIGHT = 700;
    private boolean gameOver = false;
    private int score = 0;
    private ArrayList<Pipe> pipes;
    private int pipeSpawnTimer = 0;
    private final int pipeSpawnInterval = 100;
    private int frameCount = 0;
    private final int speedIncreaseInterval = 500; // every 500 frames (10 seconds)
    Set<Pipe> scoredPipes = new HashSet<>();
    private int highScore = 0;
    private boolean gameStarted = false;
    private boolean paused = false;
    private Image background;
    private Image StartScreenImage;
    private int bgX = 0;
    private boolean isJumpSoundMuted = false;
    private boolean isBackgroundSoundMuted = false;
    private Clip backgroundClip;

    private JButton toggleJumpButton;
    private JButton toggleBackButton;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.white);
        bird = new Bird(100, 350);
        background = new ImageIcon(getClass().getResource("/res/bac.png")).getImage();
        StartScreenImage = new ImageIcon(getClass().getResource("/res/startScreen.png")).getImage();
        timer = new Timer(20, this);
        timer.start();
        playSound("background.wav", true);
        setFocusable(true);
        addKeyListener(this);
        pipes = new ArrayList<>();

        // === ADD THE BUTTONS HERE ===

        toggleJumpButton = new JButton("Mute Jump");
        toggleJumpButton.setBounds(WIDTH - 140, 10, 120, 30);
        toggleJumpButton.addActionListener(e -> {
            isJumpSoundMuted = !isJumpSoundMuted;
            toggleJumpButton.setText(isJumpSoundMuted ? "Unmute Jump" : "Mute Jump");
            // 🔧 Return focus to panel
            requestFocusInWindow();
        });

        toggleBackButton = new JButton("Mute BGM");
        toggleBackButton.setBounds(WIDTH - 140, 50, 120, 30);
        toggleBackButton.addActionListener(e -> {
            isBackgroundSoundMuted = !isBackgroundSoundMuted;
            toggleBackButton.setText(isBackgroundSoundMuted ? "Unmute BGM" : "Mute BGM");
            if (backgroundClip != null) {
                if (isBackgroundSoundMuted) {
                    backgroundClip.stop();
                } else {
                    backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
                }
            }
            requestFocusInWindow();
        });

        setLayout(null); // Allows absolute positioning
        add(toggleJumpButton);
        add(toggleBackButton);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!gameStarted) {
            g.drawImage(StartScreenImage, 0, 0, WIDTH, HEIGHT, null);
            return;
        }
        if (paused) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.drawString("Paused", WIDTH / 2 - 60, HEIGHT / 2);
            return;
        }

        g.drawImage(background, bgX, 0, WIDTH, HEIGHT, null);
        g.drawImage(background, bgX + WIDTH, 0, WIDTH, HEIGHT, null);

        g.setColor(Color.ORANGE);
        g.fillRect(0, HEIGHT - 100, WIDTH, 100);
        g.setColor(Color.GREEN);
        g.fillRect(0, HEIGHT - 110, WIDTH, 10);

        for (Pipe pipe : pipes) {
            pipe.draw(g);
        }

        bird.draw(g);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.drawString("Score: " + score, 13, 40);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("High Score: " + highScore, 13, 70);

        if (gameOver) {
            g.setColor(Color.BLACK);
            g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 36));
            g.drawString("Game Over", WIDTH / 2 - 120, HEIGHT / 2);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameStarted || paused) {
            return;
        }
        if (!gameOver) {
            bird.update();

            pipeSpawnTimer++;
            if (pipeSpawnTimer >= pipeSpawnInterval) {
                pipes.add(new Pipe(WIDTH));
                pipeSpawnTimer = 0;
            }
            Iterator<Pipe> it = pipes.iterator();
            while (it.hasNext()) {
                Pipe pipe = it.next();
                pipe.update();
                if (pipe.isOffScreen()) {
                    it.remove();
                }
                if (isCollision(bird, pipe)) {
                    gameOver = true;
                    if (score > highScore) {
                        highScore = score;
                    }
                }
                if (!gameOver && pipe.getX() + pipe.getWidth() < bird.getX() && !scoredPipes.contains(pipe)) {
                    score++;
                    scoredPipes.add(pipe);
                    System.out.println(score);
                }
            }

            if (bird.getY() + bird.getHeight() >= HEIGHT - 100 || bird.getY() < 0) {
                gameOver = true;
            }
            frameCount++;
            if (frameCount % speedIncreaseInterval == 0 && Pipe.speed < 16) {
                Pipe.speed++;
                System.out.println("Pipe speed increased to: " + Pipe.speed);
            }
        }

        if (!gameOver) {
            bgX -= 2;
        }

        if (bgX <= -WIDTH) {
            bgX = 0;
        }
        repaint();
    }

    @Override
    public void keyTyped(java.awt.event.KeyEvent e) {
    }

    @Override
    public void keyReleased(java.awt.event.KeyEvent e) {

    }

    @Override
    public void keyPressed(java.awt.event.KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!gameStarted) {
                gameStarted = true;
            } else if (!gameOver && !paused) {
                bird.jump();
                playSound("jump.wav", false);
            }
        }
        if ((e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) && gameOver) {
            resetGame();
        }
        if (e.getKeyCode() == KeyEvent.VK_P && gameStarted && !gameOver) {
            paused = !paused;
        }
    }

    private boolean isCollision(Bird bird, Pipe pipe) {
        int birdX = bird.getX();
        int birdY = bird.getY();
        int birdW = bird.getWidth();
        int birdH = bird.getHeight();

        int pipeX = pipe.getX();
        int pipeW = pipe.getWidth();
        int topH = pipe.getTopHeight();
        int gap = pipe.getGap();

        // Bird's rectangle
        int birdRight = birdX + birdW;
        int birdBottom = birdY + birdH;

        // Check X-axis overlap
        boolean overlapsX = birdRight >= pipeX && birdX <= pipeX + pipeW;

        // Check Y collision (either with top or bottom pipe)
        boolean hitsTop = birdY <= topH;
        boolean hitsBottom = birdBottom >= topH + gap;

        return overlapsX && (hitsTop || hitsBottom);
    }

    private void resetGame() {
        bird = new Bird(100, 350);
        pipes.clear();
        pipeSpawnTimer = 0;
        scoredPipes.clear();
        score = 0;
        gameOver = false;
        frameCount = 0;
        Pipe.speed = 8;
        pipes.add(new Pipe(WIDTH));
    }

    private void playSound(String filename, boolean loop) {
        try {
            if ((filename.equals("jump.wav") && isJumpSoundMuted) ||
                    (filename.equals("background.wav") && isBackgroundSoundMuted)) {
                return;
            }

            java.net.URL soundURL = getClass().getResource("/res/sound/" + filename);
            if (soundURL == null) {
                System.err.println("Sound file not found: " + filename);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundURL);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);

            // Volume control
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

            // Set volume based on file
            if (filename.equals("jump.wav")) {
                gainControl.setValue(-25.0f); // 🔉 Lower jump sound (range: 0 to -80 dB)
            } else if (filename.equals("background.wav")) {
                backgroundClip = clip;
            }

            // Save reference if it's background sound
            if (filename.equals("background.wav")) {
                backgroundClip = clip;
            }

            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                clip.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
