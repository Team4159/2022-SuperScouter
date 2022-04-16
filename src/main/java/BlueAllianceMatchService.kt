interface BlueAllianceMatchService {
    fun getMatches():List<Any>
    fun getMatchesByTeamNumber(teamNumber:Int):List<Any>
    fun getMatchByQualificationNumber(qualNumber:Int, matchType:String):List<Any>
}
