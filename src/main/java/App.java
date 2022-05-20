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
        //settingsConfig.createFormatSettingsSheet("Format Settings");


        var service = new BlueAllianceMatchServiceImpl(PropReader.getProperty("AUTH_KEY"), "2022carv");
        List<Map<String, Object>> matches;
        //System.out.println(service.getMatchesByTeamNumber(4159).get(0).getClass());

        try {
            matches = service.getMatches2();  //size 105
        } catch(Exception e){
            matches = Collections.emptyList();
            e.printStackTrace();
        }
        //System.out.println(service.getAllMatchJsonKeys(matches.get(1),false));
        //System.out.println(service.getMatchJsonValueByKey(matches.get(1),service.getAllMatchJsonKeys(matches.get(1),false)));
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


            List<List<Object>> values = new ArrayList<>();
            //System.out.println(values);

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
            var vals = new ArrayList<List<Object>>();
            service.getAllMatchJsonKeys(matches.get(1),true).forEach(
                item -> {
                    vals.add(Collections.singletonList(item));
                }
            );
            Spreadsheet.createCheckbox("Format Settings", 0,vals.size(),1,2);
            Spreadsheet.insertData(vals, "Format Settings"+createA1Range("A1", 1, vals.size()));
            Spreadsheet.resizeRange("Format Settings", 0, vals.size());
            System.out.println(Spreadsheet.getTabNames());
            //System.out.println(service.getAllMatchJsonKeys(matches.get(0), true));
            //System.out.println(service.getMatchJsonValueByKey(matches.get(0), service.getAllMatchJsonKeys(matches.get(0), true)));
            var dataAs2DList = Spreadsheet.getData("Format Settings"+createA1Range("A1",1,vals.size()));
            var selectedEntries = new ArrayList<String>(Collections.emptyList());
            System.out.println(dataAs2DList);
            dataAs2DList.forEach(pair -> {
                if(Boolean.parseBoolean(pair.get(1))){
                    selectedEntries.add(pair.get(0));
                }
            });

            System.out.println("IE "+selectedEntries.size()); //size 102
            System.out.println(service.getMatchJsonValueByKey(matches.get(0),selectedEntries)); //size 96
            System.out.println(Spreadsheet.getTabNames());
            //For every team tab, write in included keys. For every match for a team write in data corresponding to included key
            selectedEntries.add(0," "); //A1 must be empty
            for(String tabName : Spreadsheet.getTabNames()){
                var tabVals = new ArrayList<List<Object>>();
                tabVals.add(Collections.singletonList(selectedEntries));
                if(isInt(tabName)) {
                    System.out.println("Pri");
                    var teamMatches = service.getMatchesByTeamNumber(Integer.parseInt(tabName));
                    teamMatches.forEach(match -> {
                        var matchRowData = service.getMatchJsonValueByKey(match, selectedEntries);
                        tabVals.add(Collections.singletonList(matchRowData));
                    });
                    //inserting into spreadsheet not working
                    //Spreadsheet.insertData(tabVals, tabName+createA1Range("A1", selectedEntries.size(), tabVals.size()));
                }
                //parse matches list data as one array and write req all at  once
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

