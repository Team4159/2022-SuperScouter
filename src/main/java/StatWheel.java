import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.BasicStroke;
import javax.imageio.ImageIO;

import java.io.Console;
import java.io.File;
import java.io.IOException;


public class StatWheel{
    public BufferedImage image;
    public Graphics2D graphics;

    public static Polygon drawRegularPolygon(double center, double radius, int vertices) {
        Polygon output = new Polygon();
        for (int i = 0; i < vertices; i++) {
            output.addPoint((int) (center - radius * Math.sin(i * 2 * Math.PI / vertices)),
            (int) (center - radius * Math.cos(i * 2 * Math.PI / vertices)));
        }

        return output;
    }
    public static Polygon drawStats(double center, double radius, int vertices, double[] percentages) {
        Polygon output = new Polygon();
        for (int i = 0; i < vertices; i++) {
            output.addPoint((int) (center - (radius * percentages[i]) * Math.sin(i * 2 * Math.PI / vertices)),
            (int) (center - (radius * percentages[i]) * Math.cos(i * 2 * Math.PI / vertices)));
        }

        return output;
    }
    public StatWheel(int height, int width, double[] statArray) {
        int statLength = statArray.length;
        image = new BufferedImage(height, width, BufferedImage.TYPE_INT_ARGB);
        graphics = image.createGraphics();
        graphics.setStroke(new BasicStroke(2));

        Polygon backgroundPolygon = drawRegularPolygon(height/2, height/3, statLength);
        graphics.setPaint(new Color(255, 255, 255));
        graphics.fillPolygon(backgroundPolygon);
        graphics.setPaint(Color.black);
        graphics.drawPolygon(backgroundPolygon);

        graphics.setPaint(new Color(0, 0, 0, 200));
        graphics.drawPolygon(drawRegularPolygon(height/2, height/3*.75, statLength));
        graphics.drawPolygon(drawRegularPolygon(height/2, height/3*.5, statLength));
        graphics.drawPolygon(drawRegularPolygon(height/2, height/3*.25, statLength));
        graphics.drawOval(height/2-1, height/2-1, 2, 2);

        graphics.setPaint(new Color(0, 0, 0, 127));
        for (int i = 0; i < statLength; i++) {
            graphics.drawLine((int) ((height/2) - (height/3) * Math.sin(i * 2 * Math.PI / statLength)), (int) ((height/2) - (height/3) * Math.cos(i * 2 * Math.PI / statLength)), (int) width/2, (int) height/2);
        }

        graphics.setPaint(new Color(255, 0, 0, 140));
        graphics.fillPolygon(drawStats(height/2, height/3, statLength, statArray));

    }
    public StatWheel(double[] statArray) {
        this(1000, 1000, statArray);
    }

    public void saveToFile(String filename) {
        try {
            ImageIO.write(image, "PNG", new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void runTest() {
        double stats5[] = {.5, .5, .5, .5, .5};
        StatWheel testWheel5 = new StatWheel(stats5);
        testWheel5.saveToFile("./test5.png");
        double stats6[] = {.5, .4, .3, .4, .5, .6};
        StatWheel testWheel6 = new StatWheel(stats6);
        testWheel6.saveToFile("./test6.png");

        double[] statsRand = new double[5];
        for (int i=0; i<5; i++) {
            statsRand[i] = Math.round(Math.random()*100.0)/100.0;
            System.out.println(statsRand[i]);
        }
        StatWheel testWheel5rand = new StatWheel(statsRand);
        testWheel5rand.saveToFile("./testRand.png");
    }
}