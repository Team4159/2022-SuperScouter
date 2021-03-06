import com.google.gson.internal.LinkedTreeMap;
import org.checkerframework.checker.units.qual.C;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.http.HttpTimeoutException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.stream.Collectors;

public class App {
    public static void main(String... args) throws InterruptedException, IOException, GeneralSecurityException {
        var settingsConfig = SheetFormatSettingsConfig.Companion.getInstance();

        var service = new BlueAllianceMatchServiceImpl(PropReader.getProperty("AUTH_KEY"), "2022casj");
        List<Map<String, Object>> matches;
        try {
            matches = service.getMatches2();  //size 105
        } catch(Exception e){
            matches = Collections.emptyList();
            e.printStackTrace();
        }

        try {
            // Spreadsheet.runTest();

            /*
            List<List<Object>> values = Arrays.asList( //Populate with match values
                Arrays.asList(
                    "Tim", "Joe"
                ),
                Arrays.asList(
                    "Linda", "Bob"
                )
            );
            */

            /*
            System.out.println(createA1Range("A1", values.get(0).size(), values.size()));
            Spreadsheet.insertData(values, createA1Range("A1", values.get(0).size(), values.size()));
            Spreadsheet.resizeRange("Test2", 0, values.size());
            Spreadsheet.createTab("Test2");
            System.out.println(Spreadsheet.checkIfExists("idk"));
            System.out.println(Spreadsheet.getData("Test!A1:C3"));
            System.out.println(Spreadsheet.getData("Test2!A1:C3"));
            */

            //System.out.println(service.getTeams());
            //createSheets("Template" );

            //System.out.println(service.getAllMatchJsonKeys(matches.get(1),false));
            //System.out.println(service.getMatchJsonValueByKey(matches.get(1),service.getAllMatchJsonKeys(matches.get(1),false)));

            //Write JSON keys to Format Settings Sheet
            var vals = new ArrayList<List<Object>>();
            service.getAllMatchJsonKeys(service.getCrucialMatchesInfo(4159).get(1),false).forEach(
                item -> {
                    vals.add(Collections.singletonList(item));
                }
            );

            //Write checkboxes to Format Settings Sheet
            var dataAs2DList = Spreadsheet.getData("Format Settings"+createA1Range("A1",1,vals.size()));
            var isCheckboxChanged = false;
            for(ArrayList<String> pair : dataAs2DList)
                if(Boolean.parseBoolean(pair.get(1))){
                    isCheckboxChanged = true;
                    break;
                }
            if(!isCheckboxChanged){
                Spreadsheet.createCheckbox("Format Settings", 0,vals.size(),1,2);
                Spreadsheet.insertData(vals, "Format Settings"+createA1Range("A1", 1, vals.size()));
                Spreadsheet.resizeRange("Format Settings", 0, vals.size());
            }
            //System.out.println(service.getAllMatchJsonKeys(matches.get(0), true));
            //System.out.println(service.getMatchJsonValueByKey(matches.get(0), service.getAllMatchJsonKeys(matches.get(0), true)));

            //Handle adding checked keys to selectedEntries list
            dataAs2DList = Spreadsheet.getData("Format Settings"+createA1Range("A1",1,vals.size()));
            var selectedEntries = new ArrayList<String>(Collections.emptyList());
            System.out.println(dataAs2DList);
            dataAs2DList.forEach(pair -> {
                if(Boolean.parseBoolean(pair.get(1))){
                    selectedEntries.add(pair.get(0));
                }
            });

            System.out.println("IE "+selectedEntries.size()); //size 102
            System.out.println(service.getMatchJsonValueByKey(matches.get(0),selectedEntries)); //size 96
            //Clear sheet

            //For every team tab, write in included keys in top row.
            // For every match for a team write in data corresponding to
            // included key and form 2d list to insert
            selectedEntries.add(0," "); //A1 must be empty
            var tabVals = new ArrayList<List<Object>>();
            for(String tabName : Spreadsheet.getTabNames()){
                tabVals.clear();
                tabVals.add(new ArrayList<Object>(selectedEntries));
                if(isInt(tabName)) {
                    var teamMatches = service.getCrucialMatchesInfo(Integer.parseInt(tabName));
                    List<Object> matchRowData = Arrays.asList();
                    for(Map<String, Object> match : teamMatches){
                        matchRowData = service.getMatchJsonValueByKey(match, selectedEntries);
                        matchRowData.add(0,"Match # ");
                        tabVals.add(List.copyOf(matchRowData));
                        matchRowData.clear();
                    }
                    if(tabVals.size() > 1) { //Empty size are always size 1 due to adding a space char in selectedEntries
                        Spreadsheet.insertData(tabVals, tabName + createA1Range("A1", 200, tabVals.size()));
                        Spreadsheet.resizeRange(tabName, 0, tabVals.get(0).size());
                        Thread.sleep(500);
                    }
                }
            }
            //StatWheel.generateRobot(4159).saveTeam(4159);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String createA1Range(String startingCell, int matchSize, int initialListSize){
        var letters = "";
        while (matchSize >= 0) {
            letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(matchSize % 26) + letters;
            matchSize = (int) (Math.floor(matchSize / 26F) - 1);
        }
        return ("!"+startingCell+":"+letters+initialListSize);
    }

    private static void createSheets(String template) throws IOException, InterruptedException, GeneralSecurityException {
        var service = new BlueAllianceMatchServiceImpl(PropReader.getProperty("AUTH_KEY"), PropReader.getProperty("EVENT_KEY"));
        // Insert after existing sheets
        int index = Spreadsheet.getNumberOfSheets();
        List<Map<String, Object>> teams = service.getTeams();
        // Sort teams by team number
        teams.sort(Comparator.comparing(team -> Math.round((double) team.get("team_number"))));
        // Reverse teams so that they are inserted in the correct order
        Collections.reverse(teams);
        teams.forEach(team -> {
            System.out.println(team.get("team_number"));
            long teamNumber = Math.round((double) team.get("team_number"));
            try {
                // Hopefully this is ratelimited enough
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            try {
                if(Spreadsheet.checkIfExists(teamNumber + "" /* I hate this*/ )) {
                    System.out.println("Team " + teamNumber + " already exists");
                }
                else {
                    Spreadsheet.copyTab(template, teamNumber + "", index);
                }
            } catch (IOException | GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static boolean isInt(String s){
        try{
            Integer.parseInt(s);
        } catch(NumberFormatException e){
            return false;
        }
        return true;
    }
}

