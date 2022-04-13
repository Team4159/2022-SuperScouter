import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

public class PropReader {
    public static void printProps() {
        Properties properties = new Properties();
        java.net.URL url = ClassLoader.getSystemResource("config.properties");

        try  {
            properties.load(url.openStream());
        } catch (FileNotFoundException fie) {
            fie.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        Set<String> keys = properties.stringPropertyNames();
        for (String key : keys) {
            System.out.println(key + " - " + properties.getProperty(key));
        }
    }

    public static String getProperty(String key) {
        Properties properties = new Properties();
        java.net.URL url = ClassLoader.getSystemResource("config.properties");

        try  {
            properties.load(url.openStream());
        } catch (FileNotFoundException fie) {
            fie.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return properties.getProperty(key);
    }
}
