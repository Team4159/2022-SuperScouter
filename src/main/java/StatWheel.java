package statwheel;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.BasicStroke;
import javax.imageio.ImageIO;
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
        graphics.setPaint(Color.white);
        graphics.fillPolygon(backgroundPolygon);
        graphics.setPaint(Color.black);
        graphics.drawPolygon(backgroundPolygon);

        graphics.setPaint(new Color(0, 0, 0, 127));
        graphics.drawPolygon(drawRegularPolygon(height/2, height/3*.75, statLength));
        graphics.drawPolygon(drawRegularPolygon(height/2, height/3*.5, statLength));
        graphics.drawPolygon(drawRegularPolygon(height/2, height/3*.25, statLength));
        graphics.drawOval(height/2-1, height/2-1, 2, 2);

        graphics.setPaint(new Color(255, 0, 0, 200));
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

    public static void test() {
        double stats[] = {1.0, .5, .7, .3, .8};
        StatWheel testWheel = new StatWheel(stats);
        testWheel.saveToFile("./test.png");
    }

    // public static void main(String[] args) {
    //     BufferedImage image = new BufferedImage(300, 300, BufferedImage.TYPE_INT_ARGB);
    //     Graphics2D graphics = image.createGraphics();
    //     graphics.setBackground(Color.white);
    //     graphics.setStroke(new BasicStroke(2));

    //     graphics.setPaint(Color.white);
    //     graphics.fillRect(0, 0, 300, 300);

    //     graphics.setPaint(Color.black);
    //     graphics.drawPolygon(drawRegularPolygon(150, 100));

    //     graphics.setPaint(new Color(0, 0, 0, 127));
    //     graphics.drawPolygon(drawRegularPolygon(150, 75));
    //     graphics.drawPolygon(drawRegularPolygon(150, 50));
    //     graphics.drawPolygon(drawRegularPolygon(150, 25));
    //     graphics.drawOval(150-1, 150-1, 2, 2);

    //     graphics.setPaint(new Color(255, 0, 0, 200));
    //     double statArray[] = {1.0, .5, .7, .3, .8};
    //     graphics.fillPolygon(drawStats(150, 100, statArray));

    //     try {
    //         if (ImageIO.write(image, "PNG", new File(System.getProperty("user.home")+"/javatest.png")))
    //         {
    //             System.out.println("-- saved");
    //         }
    //     } catch (IOException e) {
    //             e.printStackTrace();
    //     }
    // }
}