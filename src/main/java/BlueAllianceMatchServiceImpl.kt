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
    private val url:String = "https://www.thebluealliance.com/api/v3/event/$eventKey/matches/simple"
    private lateinit var currentHeaders:String

    @Throws(HttpTimeoutException::class)
    override fun getMatches(): List<Any>{
        //TODO("Not yet implemented")
        var matches:List<Any> = mutableListOf()
        val request:HttpRequest = HttpRequest.newBuilder(URI(url))
                .version(HttpClient.Version.HTTP_2)
                .headers("X-TBA-Auth-Key", authKey)
                .timeout(Duration.ofSeconds(15L))
                .GET()
                .build()
        val response: CompletableFuture<Void>? = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply{
                    matches = listOf(it.body())
                    currentHeaders = it.headers().toString()
                }
                .thenAccept(System.out::println)
        //Use Google Gson or Jackson later
        return matches
    }
}