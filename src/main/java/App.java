import com.google.gson.internal.LinkedTreeMap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.http.HttpTimeoutException;
import java.security.GeneralSecurityException;
import java.util.*;

public class App {
    public static void main(String... args) throws InterruptedException, MalformedURLException, HttpTimeoutException {

        var service = new BlueAllianceMatchServiceImpl(PropReader.getProperty("AUTH_KEY"), "2022casj");
        List<Map<String, Object>> matches;

        try {
            matches = service.getMatches2();  //size 105
        } catch(Exception e){
            matches = Collections.emptyList();
            e.printStackTrace();
        }
        System.out.println(service.getAllMatchJsonKeys(matches.get(1)));
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
            System.out.println(createA1Range("A1", values.get(0).size(), values.size()));
            Spreadsheet.insertData(values, createA1Range("A1", values.get(0).size(), values.size()));
            Spreadsheet.resizeRange("Test2", 0, values.size());
            Spreadsheet.createTab("Test2");
            System.out.println(Spreadsheet.checkIfExists("idk"));
            System.out.println(Spreadsheet.getData("Test", "A1:C3"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String createA1Range(String startingCell, int matchSize, int initialListSize){
        var letters = "";
        while (matchSize >= 0) {
            letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(matchSize % 26) + letters;
            matchSize = (int) (Math.floor(matchSize / 26F) - 1);
        }
        return ("!"+startingCell+":"+letters+initialListSize);
    }
}
