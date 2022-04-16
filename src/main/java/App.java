import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.http.HttpTimeoutException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;

public class App {
    public static void main(String[] args){
        var b = new BlueAllianceMatchServiceImpl(PropReader.getProperty("AUTH_KEY"), "2022casj");
        try {
            System.out.println(b.getMatches());
        } catch(Exception e){
            e.printStackTrace();
        }



        System.out.println("Hello World!");
        try {
            StatWheel.runTest();
        } catch (Exception e) {
            System.out.println("something broke in statwheel");
        }
        try {
            // Spreadsheet.runTest();
            List<List<Object>> values = Arrays.asList(
                    Arrays.asList(
                        "Tim", "Joe"
                        ),
                    Arrays.asList(
                        "Linda", "Bob"
                        )
                    );
            Spreadsheet.insertData(values, "Test!A1:B2" );
            Spreadsheet.createTab("Test2");
            System.out.println(Spreadsheet.checkIfExists("idk"));
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

