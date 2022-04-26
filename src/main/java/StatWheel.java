//Image editing classes
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Font;
import java.awt.BasicStroke;
import javax.imageio.ImageIO;

//Exceptions
import javax.management.RuntimeErrorException;
import java.io.UncheckedIOException;
import java.io.FileNotFoundException;
import java.io.IOException;

//Writing image to file
import java.io.ByteArrayOutputStream;
import java.io.File;

//Prob unused: saving image as base64 string
import java.io.PrintWriter;
import java.util.Base64;
import java.util.stream.Stream;

//Read data from bluealliance
import com.google.gson.Gson; //testing
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

//Not sure where used
import java.util.ArrayList;
import java.util.Arrays;

public class StatWheel{
    public BufferedImage image;
    public Graphics2D graphics;

    public static Polygon drawRegularPolygon(double center, double radius, int vertices) {
        Polygon output = new Polygon();
        //basically draw a circle, divide the circumference of that circle by the amount of verticies, then draw a shape using the coordinates of the verticies
        for (int i = 0; i < vertices; i++) {
            output.addPoint((int) (center - radius * Math.sin(i * 2 * Math.PI / vertices)),
                    (int) (center - radius * Math.cos(i * 2 * Math.PI / vertices)));
        }
        return output;
    }
    public static Polygon drawStats(double center, double radius, int vertices, double[] percentages) {
        Polygon output = new Polygon();
        //Same as drawRegularPolygon but changes the radius according to the value in percentages
        for (int i = 0; i < vertices; i++) {
            output.addPoint((int) (center - (radius * percentages[i]) * Math.sin(i * 2 * Math.PI / vertices)),
                    (int) (center - (radius * percentages[i]) * Math.cos(i * 2 * Math.PI / vertices)));
        }

        return output;
    }
    public StatWheel(int width, int height, double[] statArray, String[] statLabels, String[] statSuffix) {
        assert statArray.length == statLabels.length;
        assert statArray.length == statSuffix.length;
        int statLength = statArray.length;
        //in seperate variables for easy modification
        double center = height/2;
        double radius = height/2.5;

        image = new BufferedImage(height, width, BufferedImage.TYPE_INT_ARGB);
        graphics = image.createGraphics();
        graphics.setStroke(new BasicStroke(5)); //line width

        graphics.setPaint(Color.black);
        graphics.fillRect(0, 0, width, height); //fill background with black, makes stuff easier to read

        graphics.setPaint(new Color(0, 0, 0, 120));
        graphics.fillPolygon(drawRegularPolygon(center, radius*1.25, statLength)); //draw background's background

        //draw background
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
        graphics.setPaint(new Color(255, 0, 0));
        for (int i = 0; i < statLength; i++) {
            graphics.drawLine((int) ((center) - (radius) * Math.sin(i * 2 * Math.PI / statLength)), (int) ((center) - (radius) * Math.cos(i * 2 * Math.PI / statLength)), (int) width/2, (int) center);
        }

        graphics.setPaint(new Color(255, 0, 0, 180));
        graphics.fillPolygon(drawStats(center, radius, statLength, statArray));

        graphics.setPaint(new Color(255, 255, 255));
        graphics.setFont(new Font(graphics.getFont().toString(), Font.PLAIN, 40));
        String suffix = "";
        String displayString = "";
        int stringWidth = 0;
        int stringHeight = 0;
        for (int i = 0; i < statLength; i++) {
            if (statSuffix[i] == null) {
                suffix = "%";
            } else {
                suffix = statSuffix[i];
            }
            displayString = String.format("%s %02d", statLabels[i], (int) (statArray[i]*100))+suffix;
            stringWidth = graphics.getFontMetrics().stringWidth(displayString);
            stringHeight = graphics.getFontMetrics().getHeight();
            graphics.drawString(displayString, (int) ((center) - (radius*.8) * Math.sin(i * 2 * Math.PI / statLength) - stringWidth / 2.0), (int) ((center) - (radius*1.08) * Math.cos(i * 2 * Math.PI / statLength) + stringHeight/2.0));
        }

    }
    public StatWheel(double[] statArray, String[] statLabels, String[] statSuffix) {
        this(1000, 1000, statArray, statLabels, statSuffix);
    }
    public StatWheel(double[] statArray, String[] statLabels) {
        this(1000, 1000, statArray, statLabels, new String[statArray.length]);
    }
    
    public String saveTeam(int teamNumber) {
        try {
            // Check if folder exists and create if not
            File folder = new File(PropReader.getProperty("imageCache"));
            if (!folder.exists()) {
                folder.mkdir();
            }
            ImageIO.write(image, "PNG", new File(PropReader.getProperty("imageCache") + "/" +Integer.toString(teamNumber)+".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (String) "https://"+PropReader.getProperty("serverUrl")+"/"+Integer.toString(teamNumber)+".png";
    }

    public void saveToFile(String filename) {
        try {
            ImageIO.write(image, "PNG", new File(PropReader.getProperty("imageCache")+ "/" + filename+".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String toBase64() {
        //doesn't work with google sheets ;-;
        //quarter-size image to fit in google sheets
        int width = 500;//image.getWidth();
        int height = 500;//image.getHeight();
        BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D scaledGraphics = scaledImage.createGraphics();
        scaledGraphics.setPaint(Color.white);
        // scaledGraphics.drawRect(0, 0, width, height);
        scaledGraphics.drawImage(image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        try
        {
            Boolean output = ImageIO.write(scaledImage, "png", os);
            if (!output) {
                throw new RuntimeException("Image format is invalid");
            }
            // ImageIO.write(scaledImage, "JPEG2000", os);
            return Base64.getEncoder().encodeToString(os.toByteArray());
        }
        catch (final IOException ioe)
        {
            throw new UncheckedIOException(ioe);
        }
    }
    private static int indexOf(String input, String[] list) {
        for (int i=0;i<list.length;i++) {
            if (input.equals(list[i])) {
                return i;
            }
        }
        return -1;
    }
    public static StatWheel generateRobot(Integer teamNumber) {
        String teamString = "frc"+Integer.toString(teamNumber);
        var b = new BlueAllianceMatchServiceImpl(PropReader.getProperty("AUTH_KEY"), PropReader.getProperty("EVENT_KEY"));
        try {
            var teamMatches = b.getMatchesByTeamNumber(teamNumber);
            double totalPoints = 0;
            double climbs = 0;
            double taxis = 0;
            double wins = 0;
            double autoPoints = 0;
            for (int i=0;i<teamMatches.size();i++) {//teamMatches.size()
                var currentMatch = teamMatches.get(i);
                var blueAlliance = (LinkedTreeMap) (((LinkedTreeMap) currentMatch.get("alliances")).get("blue"));
                var redAlliance = (LinkedTreeMap) (((LinkedTreeMap) currentMatch.get("alliances")).get("red"));

                //defaults
                String allianceName = "blue";
                int teamNo = 0;
                var allianceObject = blueAlliance;

                ArrayList<String> teamList = (ArrayList<String>) blueAlliance.get("team_keys");
                teamList.addAll((ArrayList<String>) redAlliance.get("team_keys"));

                switch (indexOf(teamString, (String[]) teamList.toArray(new String[teamList.size()]))) {
                    case 0:
                        teamNo = 0;
                        allianceObject = blueAlliance;
                        allianceName = "blue";
                        break;
                    case 1:
                        teamNo = 1;
                        allianceObject = blueAlliance;
                        allianceName = "blue";
                        break;
                    case 2:
                        teamNo = 2;
                        allianceObject = blueAlliance;
                        allianceName = "blue";
                        break;
                    case 3:
                        teamNo = 0;
                        allianceObject = redAlliance;
                        allianceName = "red";
                        break;
                    case 4:
                        teamNo = 1;
                        allianceObject = redAlliance;
                        allianceName = "red";
                        break;
                    case 5:
                        teamNo = 2;
                        allianceObject = redAlliance;
                        allianceName = "red";
                        break;
                }
                var allianceData = (LinkedTreeMap) ((LinkedTreeMap) currentMatch.get("score_breakdown")).get(allianceName);
                
                totalPoints += (double) allianceObject.get("score");
                taxis += allianceData.get("taxiRobot"+Integer.toString(teamNo+1)).equals("Yes") ? 1.0 : 0.0;
                climbs += !allianceData.get("endgameRobot"+Integer.toString(teamNo+1)).equals("None") ? 1.0 : 0.0;
                wins += currentMatch.get("winning_alliance").equals(allianceName) ? 1.0 : 0.0;
                autoPoints += (double) allianceData.get("autoPoints");
            }
            double avgPoints = totalPoints / teamMatches.size() / 100;
            double avgAutoPoints = autoPoints / teamMatches.size() / 100;
            double taxiPercent = taxis / teamMatches.size();
            double climbPercent = climbs / teamMatches.size();
            double winPercent = wins / teamMatches.size();

            double[] statArray = {avgPoints, avgAutoPoints, taxiPercent, climbPercent, winPercent};
            String[] statLabels = {"Ally Avg", "Ally Auto Avg", "Taxi", "Climb", "Win"};
            String[] statSuffix = {" pts", " pts", "%", "%", "%"};

            return new StatWheel(statArray, statLabels, statSuffix);
            // System.out.println(avgPoints);
            // System.out.println(taxiPercent);
            // System.out.println(climbPercent);
            // System.out.println(winPercent);
            // System.out.println(avgAutoPoints);
        } catch (Exception e) {
            e.printStackTrace();
            double[] statArray = {0, 0, 0, 0, 0};
            String[] statLabels = {"Error", "Error", "Error", "Error", "Error"};
            String[] statSuffix = {"", "", "", "", ""};
            return new StatWheel(statArray, statLabels);
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
        
        System.out.println(StatWheel.generateRobot(4159).saveTeam(4159));
    }
}
