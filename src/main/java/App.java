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
        System.out.println(service.getMatchesByTeamNumber(4159).get(0).getClass());
        Spreadsheet.getTabNames().forEach(
            tabName -> {
                try {
                    var tabNameAsInt = Integer.parseInt(tabName);
                    var teamMatches = service.getMatchesByTeamNumber(tabNameAsInt);
                    var alliancePoints = new ArrayList<List<Object>>(Collections.emptyList());
                    var climbData = new ArrayList<List<Object>>(Collections.emptyList());
                    var taxiData = new ArrayList<List<Object>>(Collections.emptyList());
                    var rpData = new ArrayList<List<Object>>(Collections.emptyList());
                    var cargoRPData = new ArrayList<List<Object>>(Collections.emptyList());
                    var hangarRPData = new ArrayList<List<Object>>(Collections.emptyList());


                    //Link teamKeys & climbData to taxiRobot1,2,3 etc
                    teamMatches.forEach(match -> {
                        var blueAlliance = ((LinkedTreeMap<String,Object>)match.get("alliances")).get("red");
                        var redAlliance = ((LinkedTreeMap<String,Object>)match.get("alliances")).get("red");
                        var blueScoreBreakdown = ((LinkedTreeMap<String,Object>)match.get("score_breakdown")).get("blue");
                        var redScoreBreakdown = ((LinkedTreeMap<String,Object>)match.get("score_breakdown")).get("red");
                        ArrayList<String> blueTeams = (ArrayList<String>) ((LinkedTreeMap<String,Object>)blueAlliance).get("team_keys");
                        ArrayList<String> redTeams = (ArrayList<String>) ((LinkedTreeMap<String,Object>)redAlliance).get("team_keys");
                        //Remove strings and convert to ints, check which list they're in, then get position in list
                        if(blueTeams.contains("frc"+tabNameAsInt)) {
                            int teamPosition = (blueTeams.indexOf("frc"+tabNameAsInt)+1);
                            var taxi = ((LinkedTreeMap)blueScoreBreakdown).get("taxiRobot"+teamPosition);
                            var climb = ((LinkedTreeMap)blueScoreBreakdown).get("endgameRobot"+teamPosition);
                            var totalPoints = ((LinkedTreeMap)blueScoreBreakdown).get("totalPoints");
                            var rp = ((LinkedTreeMap)blueScoreBreakdown).get("rp");
                            var hangarBonusRP = ((LinkedTreeMap)blueScoreBreakdown).get("hangarBonusRankingPoint");
                            var cargoBonusRP = ((LinkedTreeMap)blueScoreBreakdown).get("cargoBonusRankingPoint");
                            alliancePoints.add(Collections.singletonList(totalPoints));
                            climbData.add(Collections.singletonList(climb));
                            taxiData.add(Collections.singletonList(taxi));
                            rpData.add(Collections.singletonList(rp));
                            cargoRPData.add(Collections.singletonList(cargoBonusRP));
                            hangarRPData.add(Collections.singletonList(hangarRPData));
                            try {
                                Spreadsheet.insertData(taxiData, tabName+createA1Range("C8",taxiData.size(),1));
                                Spreadsheet.insertData(alliancePoints,tabName+createA1Range("C9",alliancePoints.size(),1));
                                Spreadsheet.insertData(climbData,tabName+createA1Range("C18",climbData.size(),1));
                                Spreadsheet.insertData(rpData,tabName+createA1Range("C27",rpData.size(),1));
                                Spreadsheet.insertData(cargoRPData,tabName+createA1Range("C26",cargoRPData.size(),1));
                                Spreadsheet.insertData(hangarRPData,tabName+createA1Range("C25",hangarRPData.size(),1));
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (GeneralSecurityException e) {
                                e.printStackTrace();
                            }
                            System.out.println(climb);
                        }
                        if(redTeams.contains("frc"+tabNameAsInt)) {
                            //System.out.println("red");
                        }
                    });

                    //Spreadsheet.insertData(alliancePoints, tabName+createA1Range("A1",12,1));
                    //Repeat for other values

                } catch (NumberFormatException e){
                    System.out.println("Non team number sheet name");
                } catch (InterruptedException /*| GeneralSecurityException*/ | IOException e) {
                    e.printStackTrace();
                }
            }
        );

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

            matches.forEach(match -> {
                //One day we will make a gui that lets one choose fields to include and exlude from the json so no one
                //has to type things like this ever
                var matchData = new Vector<Object>();
                matchData.add(match.get("comp_level"));
                matchData.add(match.get("match_number"));
                matchData.add(match.get("winning_alliance"));

                LinkedTreeMap blueScoreBreakdown = ((LinkedTreeMap)((LinkedTreeMap)(match.get("score_breakdown"))).get("blue"));

                matchData.add("Blue Teams " + ((LinkedTreeMap)((LinkedTreeMap)(match.get("alliances"))).get("blue")).get("team_keys").toString() );
                matchData.add("Blue Alliance Score " + blueScoreBreakdown.get("totalPoints"));
                matchData.add(
                    "Blue Taxi Status " +
                    Arrays.asList(
                        blueScoreBreakdown.get("taxiRobot1"),
                        blueScoreBreakdown.get("taxiRobot2"),
                        blueScoreBreakdown.get("taxiRobot3")
                    ).toString()
                );
                matchData.add(
                    "Blue Climb Status " +
                    Arrays.asList(
                        blueScoreBreakdown.get("endgameRobot1"),
                        blueScoreBreakdown.get("endgameRobot2"),
                        blueScoreBreakdown.get("endgameRobot3")
                    ).toString()
                );
                matchData.add("Blue Match RP " + blueScoreBreakdown.get("rp"));
                matchData.add("Blue Hangar Bonus RP " + blueScoreBreakdown.get("hangarBonusRankingPoint"));
                matchData.add("Blue Cargo Bonus RP " + blueScoreBreakdown.get("cargoBonusRankingPoint"));
                matchData.add("Blue Foul Count " + blueScoreBreakdown.get("foulCount"));
                matchData.add("Blue Tech Foul Count " + blueScoreBreakdown.get("techFoulCount"));

                //Red
                LinkedTreeMap redScoreBreakdown = ((LinkedTreeMap)((LinkedTreeMap)(match.get("score_breakdown"))).get("red"));

                matchData.add("Red Teams " + ((LinkedTreeMap)((LinkedTreeMap)(match.get("alliances"))).get("red")).get("team_keys").toString() );
                matchData.add("Red Alliance Score " + redScoreBreakdown.get("totalPoints"));
                matchData.add(
                    "Red Taxi Status " +
                        Arrays.asList(
                            redScoreBreakdown.get("taxiRobot1"),
                            redScoreBreakdown.get("taxiRobot2"),
                            redScoreBreakdown.get("taxiRobot3")
                        ).toString()
                );
                matchData.add(
                    "Red Climb Status " +
                        Arrays.asList(
                            redScoreBreakdown.get("endgameRobot1"),
                            redScoreBreakdown.get("endgameRobot2"),
                            redScoreBreakdown.get("endgameRobot3")
                        ).toString()
                );
                matchData.add("Red Match RP " + redScoreBreakdown.get("rp"));
                matchData.add("Red Hangar Bonus RP " + redScoreBreakdown.get("hangarBonusRankingPoint"));
                matchData.add("Red Cargo Bonus RP " + redScoreBreakdown.get("cargoBonusRankingPoint"));
                matchData.add("Red Foul Count " + redScoreBreakdown.get("foulCount"));
                matchData.add("Red Tech Foul Count " + redScoreBreakdown.get("techFoulCount"));

                values.add(matchData);
            });
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
            System.out.println(Spreadsheet.getTabNames());
            StatWheel.generateRobot(4159).saveTeam(4159);
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
}

