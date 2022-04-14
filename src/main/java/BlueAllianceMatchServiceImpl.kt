import java.net.http.HttpClient

class BlueAllianceMatchServiceImpl(): BlueAllianceMatchService {
    val client:HttpClient = HttpClient.newBuilder().build()

    val url:String = "https://www.thebluealliance.com/api/v3"
    val authKey:String ="" //X-TBA-Auth-Key


}