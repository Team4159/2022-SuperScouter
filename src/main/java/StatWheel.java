import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Font;
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
    public StatWheel(int width, int height, double[] statArray, String[] statLabels) {
        assert statArray.length == statLabels.length;
        int statLength = statArray.length;
        double center = height/2;
        double radius = height/2.5;

        image = new BufferedImage(height, width, BufferedImage.TYPE_INT_ARGB);
        graphics = image.createGraphics();
        graphics.setStroke(new BasicStroke(5));

        graphics.setPaint(new Color(0, 0, 0, 120));
        graphics.fillPolygon(drawRegularPolygon(center, radius*1.25, statLength));
        // graphics.fillRect(0, 0, width, height);

        Polygon backgroundPolygon = drawRegularPolygon(center, radius, statLength);
        graphics.setPaint(new Color(25, 25, 25));
        graphics.fillPolygon(backgroundPolygon);
        graphics.setPaint(new Color(255, 0, 0));
        graphics.drawPolygon(backgroundPolygon);

        graphics.setFont(new Font(graphics.getFont().toString(), Font.PLAIN, 30));
        for (int i = 1; i < 4; i++) {
            graphics.setPaint(new Color((int) (51 * (i+1)), 0, 0));
            graphics.drawPolygon(drawRegularPolygon(center, (int) Math.round(radius*i*.25), statLength));
            // graphics.setPaint(new Color(255, 0, 0));
            graphics.drawString(Integer.toString(i*25)+"%", (int) (width/2), (int) (radius-radius*(i-1)*.25));

        }
        graphics.drawOval((int) (center-1), (int) (center-1), 2, 2);
        for (int i = 0; i < statLength; i++) {
            graphics.drawLine((int) ((center) - (radius) * Math.sin(i * 2 * Math.PI / statLength)), (int) ((center) - (radius) * Math.cos(i * 2 * Math.PI / statLength)), (int) width/2, (int) center);
        }

        graphics.setPaint(new Color(255, 0, 0, 180));
        graphics.fillPolygon(drawStats(center, radius, statLength, statArray));

        graphics.setPaint(new Color(255, 255, 255));
        graphics.setFont(new Font(graphics.getFont().toString(), Font.PLAIN, 45));
        for (int i = 0; i < statLength; i++) {
            graphics.drawString(String.format("%s %02d%%", statLabels[i], (int) (statArray[i]*100)), (int) ((center) - (radius*.8) * Math.sin(i * 2 * Math.PI / statLength) - width/8), (int) ((center) - (radius*1.05) * Math.cos(i * 2 * Math.PI / statLength) + height/60));
        }

    }
    public StatWheel(double[] statArray, String[] statLabels) {
        this(1000, 1000, statArray, statLabels);
    }

    public void saveToFile(String filename) {
        try {
            ImageIO.write(image, "PNG", new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void runTest() {
        String stat5label[] = {"power", "accuracy", "win rate", "speed", "defense"};
        String stat6label[] = {"power", "accuracy", "win rate", "speed", "defense", "spirit"};
        double stats5[] = {.5, .5, .5, .5, .5};
        StatWheel testWheel5 = new StatWheel(stats5, stat5label);
        testWheel5.saveToFile("./test5.png");
        double stats6[] = {.5, .4, .3, .4, .5, .6};
        StatWheel testWheel6 = new StatWheel(stats6, stat6label);
        testWheel6.saveToFile("./test6.png");

        double[] stats5Rand = new double[5];
        for (int i=0; i<5; i++) {
            stats5Rand[i] = Math.round(Math.random()*100.0)/100.0;
        }
        StatWheel testWheel5rand = new StatWheel(stats5Rand, stat5label);
        testWheel5rand.saveToFile("./test5Rand.png");

        double[] stats6Rand = new double[6];
        for (int i=0; i<6; i++) {
            stats6Rand[i] = Math.round(Math.random()*100.0)/100.0;
        }
        StatWheel testWheel6rand = new StatWheel(stats6Rand, stat6label);
        testWheel6rand.saveToFile("./test6Rand.png");
        
        //below stuff pointless to test until I'm able to get all from one team
        var b = new BlueAllianceMatchServiceImpl(PropReader.getProperty("AUTH_KEY"), "2022casj");
        try {
            var matchFour = b.getMatches().get(4);
            System.out.println(matchFour);
            String statDataLabel[] = {"Points(/100)", "test", "test"};
            // System.out.println(Math.min(1.0, matchFour.redTotalPoints/100.0));
            double statsData[] = {Math.min(1.0, matchFour.getRedTotalPoints()/100.0), .5, .5};
            StatWheel statData = new StatWheel(statsData, statDataLabel);
            statData.saveToFile("./testFour.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
        

    }
}
