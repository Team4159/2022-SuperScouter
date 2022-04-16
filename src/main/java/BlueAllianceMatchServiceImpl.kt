import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.net.MalformedURLException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpTimeoutException
import java.time.Duration
import java.util.concurrent.CompletableFuture
import kotlin.jvm.Throws

class BlueAllianceMatchServiceImpl(
    private val authKey:String ="", //X-TBA-Auth-Key
    private val eventKey:String =""

): BlueAllianceMatchService {

    private val client:HttpClient = HttpClient.newBuilder().build()
    private val url:String = "https://www.thebluealliance.com/api/v3"
    private lateinit var currentHeaders:String
    private val gson:Gson = GsonBuilder().setPrettyPrinting().create()
    companion object {
        //Event keys: https://docs.google.com/spreadsheets/d/1HqsReMjr5uBuyZjqv14t6bQF2n038GfMmWi3B6vFGiA/edit
        val EVENT_KEYS:Map<String,String> = HashMap<String,String>().apply {
            put("SVR", "2022casj")
            put("SFR", "2022casf")
        }
    }

    @Throws(HttpTimeoutException::class, InterruptedException::class, MalformedURLException::class)
    override fun getMatches(): List<BlueAllianceMatch>{
        //TODO("Not yet implemented")
        val matches:MutableList<BlueAllianceMatch> = mutableListOf()
        matches.clear()
        val request:HttpRequest = createRequest("$url/event/$eventKey/matches")
        val response: CompletableFuture<Any>? = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            //Do nested hashmap later
            .thenApply(HttpResponse<String>::body)
            .thenApply { it ->
                //println(it)
                //Serialization
                //matches = gson.fromJson(it, object: TypeToken<BlueAllianceMatch>(){}.type)
                val matchesJSON:JsonArray = JsonParser.parseString(it) as JsonArray
                matchesJSON.map {
                    //From match object

                    val winningAlliance:String = it.asJsonObject.get("winning_alliance").asString
                    val scoreBreakdown:JsonObject = it.asJsonObject.get("score_breakdown").asJsonObject
                    val alliances:JsonObject = it.asJsonObject.get("alliances").asJsonObject
                    val compLevel:String = it.asJsonObject.get("comp_level").asString
                    //From scoreBreakdown
                    val red:JsonObject = scoreBreakdown.get("red").asJsonObject
                    val blue:JsonObject = scoreBreakdown.get("blue").asJsonObject

                    //From blue and red
                    val blueRobot1Climb:String = blue.get("endgameRobot1").asString
                    val blueRobot2Climb:String = blue.get("endgameRobot2").asString
                    val blueRobot3Climb:String = blue.get("endgameRobot3").asString

                    val redRobot1Climb:String = red.get("endgameRobot1").asString
                    val redRobot2Climb:String = red.get("endgameRobot2").asString
                    val redRobot3Climb:String = red.get("endgameRobot3").asString

                    val blueRobot1IsTaxi:String = blue.get("taxiRobot1").asString
                    val blueRobot2IsTaxi:String = blue.get("taxiRobot2").asString
                    val blueRobot3IsTaxi:String = blue.get("taxiRobot3").asString

                    val redRobot1IsTaxi:String = red.get("taxiRobot1").asString
                    val redRobot2IsTaxi:String = red.get("taxiRobot2").asString
                    val redRobot3IsTaxi:String = red.get("taxiRobot3").asString

                    val blueRP = blue.get("rp").asInt
                    val redRP = red.get("rp").asInt
                    val blueQuintetAchieved:Boolean = blue.get("quintetAchieved").asBoolean
                    val redQuintetAchieved:Boolean = red.get("quintetAchieved").asBoolean
                    val blueHangarBonusRP:Boolean = blue.get("hangarBonusRankingPoint").asBoolean
                    val redHangarBonusRP:Boolean = red.get("hangarBonusRankingPoint").asBoolean
                    val blueCargoBonusRP:Boolean = blue.get("cargoBonusRankingPoint").asBoolean
                    val redCargoBonusRP:Boolean = red.get("cargoBonusRankingPoint").asBoolean

                    val blueFoulCount = blue.get("foulCount").asInt
                    val redFoulCount = red.get("foulCount").asInt
                    val blueTechFoulCount = blue.get("techFoulCount").asInt
                    val redTechFoulCount = red.get("techFoulCount").asInt

                    //From Red and Blue alliances
                    val redTotalPoints = red.get("totalPoints").asInt
                    val blueTotalPoints = blue.get("totalPoints").asInt

                    val redAlliance:JsonObject = alliances.get("red").asJsonObject
                    val blueAlliance:JsonObject = alliances.get("blue").asJsonObject

                    //From the alliance objects; Figure out ordering later based on comp_level
                    val redTeams:JsonArray = redAlliance.get("team_keys").asJsonArray
                    val blueTeams:JsonArray = blueAlliance.get("team_keys").asJsonArray
                    matches.add(BlueAllianceMatch(
                        compLevel,
                        winningAlliance,
                        blueRobot1Climb, blueRobot2Climb, blueRobot3Climb,
                        redRobot1Climb, redRobot2Climb, redRobot3Climb,
                        blueRobot1IsTaxi, blueRobot2IsTaxi, blueRobot3IsTaxi,
                        redRobot1IsTaxi, redRobot2IsTaxi, redRobot3IsTaxi,
                        blueRP, redRP,
                        blueQuintetAchieved, redQuintetAchieved,
                        blueHangarBonusRP, redHangarBonusRP,
                        blueCargoBonusRP, redCargoBonusRP, blueFoulCount, redFoulCount, blueTechFoulCount, redTechFoulCount,
                        redTotalPoints, blueTotalPoints
                    ))
                }
                return@thenApply matches
            }
        val result:List<BlueAllianceMatch> = response?.get() as List<BlueAllianceMatch>
        return matches
    }

    //Can be used on non Rapid React games
    @Throws(HttpTimeoutException::class, InterruptedException::class, MalformedURLException::class)
    fun getMatches2(): List<Map<String,Any>>{
        //TODO("Not yet implemented")
        val matches:MutableList<Map<String,Any>> = mutableListOf()
        matches.clear()
        val request:HttpRequest = createRequest("$url/event/$eventKey/matches")
        val response: CompletableFuture<Any>? = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            //Do nested hashmap later
            .thenApply(HttpResponse<String>::body)
            .thenApply { it ->
                //println(it)
                //Serialization
                //matches = gson.fromJson(it, object: TypeToken<BlueAllianceMatch>(){}.type)
                val matchesJSON:JsonArray = JsonParser.parseString(it) as JsonArray
                matchesJSON.map {
                    //From matchJSON object
                    val match = gson.fromJson<HashMap<String,Any>>(it, object :TypeToken<HashMap<String,Any>>(){}.type)
                    matches.add(match)
                }
                return@thenApply matches
            }
        val result:List<Map<String,Any>> = response?.get() as List<Map<String, Any>>
        return result
    }

    @Throws(HttpTimeoutException::class, InterruptedException::class, MalformedURLException::class)
    override fun getMatchesByTeamNumber(teamNumber:Int): List<Map<String,Any>> {
        //TODO("Not yet implemented")
        val matches:MutableList<Map<String,Any>> = mutableListOf()
        matches.clear()
        val request:HttpRequest = createRequest("$url/team/frc$teamNumber/event/$eventKey/matches")
        val response:CompletableFuture<Any>? = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse<String>::body)
            .thenApply {
                val matchesJSON:JsonArray = JsonParser.parseString(it) as JsonArray
                matchesJSON.map {
                    //From matchJSON object
                    val match = gson.fromJson<HashMap<String,Any>>(it, object :TypeToken<HashMap<String,Any>>(){}.type)
                    matches.add(match)
                }
                return@thenApply matches

            }
        val result:List<Map<String,Any>> = response?.get() as List<Map<String,Any>>
        return result
    }

    @Throws(java.lang.Exception::class)
    override fun getMatchByQualificationNumber(matchNumber:Int, matchType:String): List<Any> {
        //TODO("Not yet implemented")
        if(
            matchType.toLowerCase() != "qm" ||
            matchType.toLowerCase() != "ef" ||
            matchType.toLowerCase() != "sf" ||
            matchType.toLowerCase() != "qf" ||
            matchType.toLowerCase() != "f"
        ) throw Exception("Invalid match type")
        val matches:MutableList<Map<String,Any>> = mutableListOf()
        matches.clear()
        //Match key format: yyyy[EVENT_CODE]_[COMP_LEVEL]m[MATCH_NUMBER]
        val request:HttpRequest = createRequest("$url/match/${eventKey}_${matchType}m$matchNumber")
        val response:CompletableFuture<Any>? = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse<String>::body)
            .thenApply {
                val matchesJSON:JsonArray = JsonParser.parseString(it) as JsonArray
                matchesJSON.map {
                    //From matchJSON object
                    val match = gson.fromJson<HashMap<String,Any>>(it, object :TypeToken<HashMap<String,Any>>(){}.type)
                    matches.add(match)
                }
                return@thenApply matches

            }
        val result:List<Map<String,Any>> = response?.get() as List<Map<String,Any>>
        return result

    }

    fun getCurrentHeaders(): String? = currentHeaders

    private fun createRequest(url:String):HttpRequest {
        try {
            return HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .headers("X-TBA-Auth-Key", authKey)
                .timeout(Duration.ofSeconds(15L))
                .uri(URI.create(url))
                .GET()
                .build()
        } catch(e:MalformedURLException) {
            e.printStackTrace()
            return HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .headers("X-TBA-Auth-Key", authKey)
                .timeout(Duration.ofSeconds(15L))
                .uri(URI.create(this.url))
                .GET()
                .build()
        }
    }
}