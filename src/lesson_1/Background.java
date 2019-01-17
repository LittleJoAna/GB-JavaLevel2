package lesson_1;

/*
 *Написать класс Background, изменяющий цвет канвы в зависимости от времени
 */

import java.awt.*;


public class Background extends Sprite {
    private static final float colorRed = 128;
    private static final float colorGreen = 128;
    private static final float colorBlue = 128;

    private static float time;

    @Override
    void render(GameCanvas gameCanvas, Graphics g, float deltaTime) {
        time += deltaTime;
        g.setColor(new Color((int)colorChangeRed(time), (int)colorChangeGreen(time), (int)colorChangeBlue(time)));
        g.fillRect(gameCanvas.getX(), gameCanvas.getY(), gameCanvas.getWidth(), gameCanvas.getHeight());
    }

    float colorChangeRed(float deltaTime) {
        float a = (int)(colorRed + colorRed * Math.sin(deltaTime));
        return a;
    }

    float colorChangeGreen(float deltaTime) {
        float a = (int)(colorGreen + colorGreen * Math.sin(deltaTime * 2));
        return a;
    }

    float colorChangeBlue(float deltaTime) {
        float a = (int)(colorBlue + colorBlue * Math.sin(deltaTime * 3));
        return a;
    }
}
