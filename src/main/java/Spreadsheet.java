import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.json.GoogleJsonError;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public class Spreadsheet {
    private static final String APPLICATION_NAME = "Cardinalbotics Scouting App";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String spreadsheetId = PropReader.getProperty("spreadSheetId");

    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = Spreadsheet.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline")
            .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    private static Sheets service = null;
    private static Sheets getSheetsService() throws GeneralSecurityException, IOException {
        if (service == null) {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        }
        return service;
    }

    /**
     * Prints the names and majors of students in a sample spreadsheet:
     * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
     */
    public static void runTest() throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final String spreadsheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms";
        final String range = "Class Data!A2:E";
        Sheets service = getSheetsService();
        ValueRange response = service.spreadsheets().values()
            .get(spreadsheetId, range)
            .execute();
        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            System.out.println("Name, Major");
            for (List row : values) {
                // Print columns A and E, which correspond to indices 0 and 4.
                System.out.printf("%s, %s\n", row.get(0), row.get(4));
            }
        }
    }

    public static void insertData(List<List<Object>> values, String range) throws IOException, GeneralSecurityException {
        Sheets service = getSheetsService();
        ValueRange body = new ValueRange()
            .setValues(values);
        UpdateValuesResponse result =
            service.spreadsheets().values().update(spreadsheetId, range, body)
            .setValueInputOption("RAW")
            .execute();
        System.out.printf("%d cells updated.\n", result.getUpdatedCells());
    }

    public static boolean checkIfExists(String sheetName) throws IOException, GeneralSecurityException {
        Sheets service = getSheetsService();
        String range = sheetName + "!A1:A1";
        try {
            ValueRange response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        } catch (IOException exception) {
            if (exception instanceof GoogleJsonResponseException) {
               GoogleJsonError details = ((GoogleJsonResponseException) exception).getDetails();
                if (details.getCode() == 400) {
                     System.out.println("Tab does not exist");
                }
            }
            return false;
        }
        return true;
    }

    public static void createTab(String title) throws IOException, GeneralSecurityException {
        Sheets service = getSheetsService();
        List<Request> requests = new ArrayList<>();
        requests.add(new Request().setAddSheet(new AddSheetRequest().setProperties(new SheetProperties()
                        .setTitle(title))));
        BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest().setRequests(requests);
        try {
            service.spreadsheets().batchUpdate(spreadsheetId, body).execute();
        } catch (IOException exception) {
            if (exception instanceof GoogleJsonResponseException) {
               GoogleJsonError details = ((GoogleJsonResponseException) exception).getDetails();
                if (details.getCode() == 400) {
                     System.out.println("Tab already exists");
                }
            }
        }
    }

    public static void resizeRange(String sheetName, int startRow, int endRow) throws IOException, GeneralSecurityException {
        Sheets service = getSheetsService();
        List<Request> requests = new ArrayList<>();
        System.out.println(getSheetId(sheetName));
        requests.add(new Request().setAutoResizeDimensions(
                    new AutoResizeDimensionsRequest()
                        .setDimensions(
                            new DimensionRange()
                                .setSheetId(getSheetId(sheetName))
                                .setDimension("ROWS")
                                .setStartIndex(startRow)
                                .setEndIndex(endRow)
                        )
                    )
                );
        BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest().setRequests(requests);
        service.spreadsheets().batchUpdate(spreadsheetId, body).execute();
    }

    //Might need to edit later
    public static void createCheckbox(String sheetName, int startRowIndex, int endRowIndex, int startColIndex, int endColIndex) throws IOException, GeneralSecurityException {
        Sheets service = getSheetsService();
        List<Request> requests = Collections.emptyList();
        requests.add(
            new Request().setRepeatCell(
                new RepeatCellRequest().setCell(
                    new CellData().setDataValidation(
                        new DataValidationRule().setCondition(new BooleanCondition().setType("BOOLEAN")))
                )
                    .setRange(
                        new GridRange()
                            .setSheetId(getSheetId(sheetName))
                            .setStartRowIndex(startRowIndex)
                            .setEndRowIndex(endRowIndex)
                            .setStartColumnIndex(startColIndex)
                            .setEndColumnIndex(endColIndex)
                    )
                    .setFields("DataValidation")
            )
        );
        BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest().setRequests(requests);
        service.spreadsheets().batchUpdate(spreadsheetId, body).execute();
        System.out.println("Completed");
    }

    public static int getSheetId(String sheetName) throws IOException, GeneralSecurityException {
        Sheets service = getSheetsService();
        Sheets.Spreadsheets.Get response = service.spreadsheets().get(spreadsheetId);
        response.setIncludeGridData(false);
        ArrayList<Sheet> sheets = (ArrayList<Sheet>) response.execute().getSheets();
        for (Sheet sheet : sheets) {
            if (sheet.getProperties().getTitle().equals(sheetName)) {
                return sheet.getProperties().getSheetId();
            }
        }
        return 0;
        // Error handling is for weak programmers that cant write code that works
    }

    // Function to get data as 2d arrays from a sheet using a range
    public static ArrayList<ArrayList<String>> getData(String range) throws IOException, GeneralSecurityException {
        Sheets service = getSheetsService();
        ValueRange response = service.spreadsheets().values()
            .get(spreadsheetId, range)
            .execute();
        List<List<Object>> values = response.getValues();
        ArrayList<ArrayList<String>> data = new ArrayList<>();
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            for (List row : values) {
                ArrayList<String> rowData = new ArrayList<>();
                for (Object cell : row) {
                    rowData.add(cell.toString());
                }
                data.add(rowData);
            }
        }
        return data;
    }
}
