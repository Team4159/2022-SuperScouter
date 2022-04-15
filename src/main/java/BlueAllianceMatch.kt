//Might have to make the properties nullable
//yeah yeah nested hashmap later
data class BlueAllianceMatch(
        val compLevel:String,
        val winningAlliance:String,

        val blueRobot1Climb:String,
        val blueRobot2Climb:String,
        val blueRobot3Climb:String,

        val redRobot1Climb:String,
        val redRobot2Climb:String,
        val redRobot3Climb:String,

        val blueRobot1IsTaxi:String,
        val blueRobot2IsTaxi:String,
        val blueRobot3IsTaxi:String,

        val redRobot1IsTaxi:String,
        val redRobot2IsTaxi:String,
        val redRobot3IsTaxi:String,

        val blueRP:Int,
        val redRP:Int,
        val blueQuintetAchieved:Boolean,
        val redQuintetAchieved:Boolean,
        val blueHangarBonusRP:Boolean,
        val redHangarBonusRP:Boolean,
        val blueCargoBonusRP:Boolean,
        val redCargoBonusRP:Boolean,

        val blueFoulCount:Int,
        val redFoulCount:Int,
        val blueTechFoulCount:Int,
        val redTechFoulCount:Int,

        //From Red and Blue alliances
        val redTotalPoints:Int,
        val blueTotalPoints:Int

        //Maybe add YT vid field
)

