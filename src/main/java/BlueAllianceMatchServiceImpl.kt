import com.google.gson.*
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import java.lang.NumberFormatException
import java.net.MalformedURLException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpTimeoutException
import java.time.Duration
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.stream.Collectors
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap
import kotlin.jvm.Throws
import kotlin.math.ceil
import kotlin.math.floor

class BlueAllianceMatchServiceImpl(
    private val authKey:String ="", //X-TBA-Auth-Key
    private val eventKey:String =""

): BlueAllianceMatchService {

    private val client:HttpClient = HttpClient.newBuilder().build()
    private val url:String = "https://www.thebluealliance.com/api/v3"
    private lateinit var currentHeaders:String
    private val lastHttpStatus:Int = 300
    private val keyList:ArrayList<String> = ArrayList()
    private val valuesList:ArrayList<String> = ArrayList()

    private val gson:Gson = GsonBuilder().setPrettyPrinting().create()
    companion object {
        //Event keys: https://docs.google.com/spreadsheets/d/1HqsReMjr5uBuyZjqv14t6bQF2n038GfMmWi3B6vFGiA/edit
        val EVENT_KEYS:Map<String,String> = HashMap<String,String>().apply {
            put("SVR", "2022casj")
            put("SFR", "2022casf")
        }

        fun validateEventKey(eventKey:String):String{
            try {
                eventKey.substring(0,4).toInt()
            } catch(e:NumberFormatException){
                throw Exception("eventKey does not start with a year. Ex: 2022")
            }
            val characters = eventKey.toCharArray()
            characters.forEach { if(!it.isLowerCase()) throw Exception("Key should be lowercase") }
            if(eventKey.toLowerCase().substring(4).length != 4) throw Exception("Improperly formatted key. Ex: casf")
            if(eventKey.toLowerCase().length != 8) throw Exception("Key is not 8 characters")
            return eventKey
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
    override fun getMatches2(): List<Map<String,Any>>{
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

    // Get all the teams in the event
    @Throws(HttpTimeoutException::class, InterruptedException::class, MalformedURLException::class)
    override fun getTeams(): List<Map<String,Any>> {
        //TODO("Not yet implemented")
        val teams:MutableList<Map<String,Any>> = mutableListOf()
        teams.clear()
        val request:HttpRequest = createRequest("$url/event/$eventKey/teams")
        val response:CompletableFuture<Any>? = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse<String>::body)
            .thenApply {
                val teamsJSON:JsonArray = JsonParser.parseString(it) as JsonArray
                teamsJSON.forEach {
                    //From matchJSON object
                    val team = gson.fromJson<HashMap<String,Any>>(it, object :TypeToken<HashMap<String,Any>>(){}.type)
                    teams.add(team)
                }
                return@thenApply teams
            }
        val result:List<Map<String,Any>> = response?.get() as List<Map<String,Any>>
        return result
    }

    //If it contains just the value,  add the key to my list.
    //If it is an object, store the key and send the value recursively to the same function.
    //If it is an array, check whether it contains an object, if so I store the key and send the value recursively to the same function.
    fun getAllMatchJsonKeys(serializedJson: Map<String, Any>, includeOuterKey:Boolean):List<String> {
        val keySet:Set<String> = (serializedJson).keys
        keySet.forEach { it ->
            if(serializedJson.get(it) ?: error("serializedJson may be null.") is LinkedTreeMap<*, *>){
                if(includeOuterKey) keyList.add(it)
                getAllMatchJsonKeys((serializedJson.get(it) as Map<String, Any>), includeOuterKey)
            } else if(serializedJson.get(it) ?: error("serializedJson may be null.") is List<*>){
                for(i in 0 until (serializedJson.get(it) as List<*>).size) {
                    if((serializedJson.get(it) as List<*>).get(i) ?: error("serializedJson may be null.") is LinkedTreeMap<*, *>){
                        if(includeOuterKey) keyList.add(it)
                        //println(it)
                        getAllMatchJsonKeys((((serializedJson.get(it) as List<*>).get(i) as Map<*, *>)) as Map<String, Any>, includeOuterKey)
                    } else {
                        if(!(keyList.contains(it))) keyList.add(it)
                    }
                }
            }else
                keyList.add(it)
            }
        return keyList
    }

    fun getMatchJsonValueByKey(serializedJson: Map<String,Any>, keys:List<String>):List<Any>{
        val keySet:Collection<Any> = (serializedJson).values
        keySet.forEach { it ->
            if (it is LinkedTreeMap<*, *>) {
                //valuesList.add(it.toString())
                getMatchJsonValueByKey(it as Map<String, Any>, keys)
            } else if (it is List<*>) {
                for (i in 0 until it.size) {
                    if (it.get(i) is LinkedTreeMap<*, *>) {
                        //valuesList.add(it.toString())
                        getMatchJsonValueByKey(((it.get(i) as Map<*, *>)) as Map<String, Any>, keys)
                    } else {
                        //Idk why the team key lists are strings
                        if(!(valuesList.contains(it.toString()))) valuesList.add(it.toString())
                    }
                }
            } else {
                //Check if decimal is int
                //if(it is Double && ceil(it.toDouble()) == floor(it.toDouble())) valuesList.add(it.toInt().toString())
                valuesList.add(
                    if(
                        it is Double &&
                        ceil(it.toDouble()) == floor(it.toDouble())
                    ) it.toInt().toString()
                    else it.toString()
                )
            }
        }
        return valuesList
    }

    //To avoid duplicates and displacement issues in Format Settings keys and the 1st row of keys on team tab sheets
    //compLevel, winningAlliance, etc are common keys in match json objects for any game at the moment
    fun getCrucialMatchesInfo(teamNumber:Int):List<Map<String,Any>>{ //address null key issue later
        //Search for alliance color for each match. Get important outer keys,
        // then get all keys in score breakdown;Note:Prior to api/v3, a lot of keys have null values
        val matches = mutableListOf<Map<String,Any>>()
        if (teamNumber <= 0) return matches
        var allianceColor = ""
        val request:HttpRequest = createRequest("$url/team/frc$teamNumber/event/$eventKey/matches")
        val response: CompletableFuture<Any>? = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse<String>::body)
            .thenApply { it ->
                val matchesJSON: JsonArray = JsonParser.parseString(it) as JsonArray
                matchesJSON.map {
                    //Important outer keys
                    val match = TreeMap<String,Any>()
                    val compLevel:String = it.asJsonObject.get("comp_level").asString
                    val matchNumber:Int = it.asJsonObject.get("match_number").asInt
                    val setNumber:Int = it.asJsonObject.get("set_number").asInt
                    val winningAlliance: String = it.asJsonObject.get("winning_alliance").asString

                    //Find alliance color
                    val blueTeamKeys:JsonArray = it.asJsonObject.get("alliances").asJsonObject.get("blue").asJsonObject.get("team_keys").asJsonArray
                    val redTeamKeys:JsonArray = it.asJsonObject.get("alliances").asJsonObject.get("red").asJsonObject.get("team_keys").asJsonArray
                    if(TextConverter.stringToList(blueTeamKeys.toList().toString()).contains("\"frc$teamNumber\""))
                        allianceColor = "blue"
                    if(TextConverter.stringToList(redTeamKeys.toList().toString()).contains("\"frc$teamNumber\""))
                        allianceColor = "red"

                    //Score breakdown KV pairs
                    val scoreBreakdown: JsonObject? = it.asJsonObject.get("score_breakdown").asJsonObject.get(allianceColor).asJsonObject
                    val scoreBreakdownMap = gson.fromJson<HashMap<String,Any>>(scoreBreakdown, object :TypeToken<HashMap<String,Any>>(){}.type)
                    val scoreBreakdownKeys = scoreBreakdownMap.keys.stream().collect(Collectors.toList())
                    val scoreBreakdownVals = scoreBreakdownMap.values.stream().collect(Collectors.toList())
                    match.apply {
                        put("AllianceColor", allianceColor)
                        put("comp_level", compLevel)
                        put("match_number", matchNumber)
                        put("set_number", setNumber)
                        put("winning_alliance", winningAlliance)
                    }
                    if(scoreBreakdown != null){
                        for(i in 0 until scoreBreakdownKeys.size) match.put(scoreBreakdownKeys[i], scoreBreakdownVals[i])
                        matches.add(match)
                    }
                }
            }
        response?.get() as List<Map<String,Any>>
        return matches
    }

    fun getCurrentHeaders(): String? = currentHeaders
    fun getLastHttpStatus():Int = lastHttpStatus

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
