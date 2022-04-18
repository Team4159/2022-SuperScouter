interface BlueAllianceMatchService {
    fun getMatches():List<Any>
    fun getMatches2():List<Any>
    fun getMatchesByTeamNumber(teamNumber:Int):List<Any>
    fun getMatchByQualificationNumber(matchNumber:Int, matchType:String):List<Any>
    fun getTeams(): List<Map<String,Any>>
}
