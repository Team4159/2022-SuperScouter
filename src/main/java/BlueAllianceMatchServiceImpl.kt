import com.google.gson.Gson
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
        eventKey:String =""

): BlueAllianceMatchService {
    private val client:HttpClient = HttpClient.newBuilder().build()
    private val url:String = "https://www.thebluealliance.com/api/v3/event/$eventKey/matches"
    private lateinit var currentHeaders:String
    private val gson:Gson = Gson()
    companion object {
        val EVENT_KEYS:Array<String> = arrayOf("2022casj", "2016nytr")
    }
    @Throws(HttpTimeoutException::class)
    override fun getMatches(): List<Any>{
        //TODO("Not yet implemented")
        var matches:List<Any> = mutableListOf()
        val request:HttpRequest = HttpRequest.newBuilder(URI.create(url))
                .version(HttpClient.Version.HTTP_2)
                .headers("X-TBA-Auth-Key", authKey)
                //.timeout(Duration.ofSeconds(15L))
                .GET()
                .build()
        val response: CompletableFuture<Void>? = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply{
                    matches = listOf(it.body())
                    currentHeaders = it.headers().toString()
                    println(it.body() + "bruh")
                }
                .thenAccept(System.out::println)

        val r = client.send(request, HttpResponse.BodyHandlers.ofString())
        println(r.body())
        //Use Google Gson or Jackson later
        return matches
    }

    override fun getMatchesByTeamNumber(): List<Any> {
        TODO("Not yet implemented")
    }

    override fun getMatchByQualificationNumber(): List<Any> {
        TODO("Not yet implemented")
    }


    fun getCurrentHeaders(): String? = currentHeaders
}