package game;

import  javax.swing.JFrame;

public class GameWindow extends JFrame{
    public GameWindow(){
        setTitle("Flappy Arnav");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 700);
        setLocationRelativeTo(null);
        add(new GamePanel());

        setVisible(true);
        setResizable(false);
    }
}