import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class App {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        try {
          StatWheel.runTest();
        } catch (Exception e) {
          System.out.println("something broke in statwheel");
        }
        try {
          Spreadsheet.runTest();
        } catch (Exception e) {
          System.out.println("something broke");
        }
        // PropReader.printProps();
    }
}
