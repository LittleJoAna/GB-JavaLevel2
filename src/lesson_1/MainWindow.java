package lesson_1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainWindow extends JFrame {
    private static final int POS_X = 600;
    private static final int POS_Y = 200;
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;

    private static int countBall = 0;
    private static int countPress = 0;

    Sprite[] sprites = new Sprite[10];
    Background bg = new Background();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainWindow();
            }
        });
    }

    private MainWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocation(POS_X, POS_Y);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setResizable(false);
        setTitle("Circles");
        GameCanvas gameCanvas = new GameCanvas(this);
        add(gameCanvas);
        initGame();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if(sprites.length <= countBall + 3) {
                    Sprite[] temp = new Sprite[sprites.length + 10];
                    for(int i = 0; i < countBall; i++) {
                        temp[i] = sprites[i];
                    }
                    sprites = temp;
                }
                onClick(e);
            }
        });
        setVisible(true);
    }

    private void onClick(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            countPress += 3;
            System.out.println(countPress);
            pressedLeftMouseButton();
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            pressedRightMouseButton();
        } else {
            throw new RuntimeException("Нажата неизвестная клавиша!");
        }

    }

    private void pressedLeftMouseButton () {
        if (countBall == countPress) return;
        int temp = countBall;
        for(int i = temp; i < countPress; i++) {
            sprites[i] = new Ball();
            countBall++;
        }
        System.out.println(countBall);
    }

    private void pressedRightMouseButton () {

    }

    private void initGame() {
        countPress += 3;
        for (int i = 0; i < 3; i++) {
            sprites[i] = new Ball();
            countBall++;
        }
    }

    void onDrawFrame(GameCanvas gameCanvas, Graphics g, float deltaTime) {
        update(gameCanvas, deltaTime);
        render(gameCanvas, g, deltaTime);

    }

    private void update(GameCanvas gameCanvas, float deltaTime) {
        for (int i = 0; i < countBall; i++) {
            sprites[i].update(gameCanvas, deltaTime);
        }
    }

    private void render(GameCanvas gameCanvas, Graphics g, float deltaTime) {
        bg.render(gameCanvas, g, deltaTime);
        for (int i = 0; i < countBall; i++) {
            sprites[i].render(gameCanvas, g);
        }
    }
}
