//Might have to make the properties nullable
//yeah yeah nested hashmap later
data class BlueAllianceMatch(
        @JvmField val compLevel:String,
        @JvmField val winningAlliance:String,

        @JvmField val blueRobot1Climb:String,
        @JvmField val blueRobot2Climb:String,
        @JvmField val blueRobot3Climb:String,

        @JvmField val redRobot1Climb:String,
        @JvmField val redRobot2Climb:String,
        @JvmField val redRobot3Climb:String,

        @JvmField val blueRobot1IsTaxi:String,
        @JvmField val blueRobot2IsTaxi:String,
        @JvmField val blueRobot3IsTaxi:String,

        @JvmField val redRobot1IsTaxi:String,
        @JvmField val redRobot2IsTaxi:String,
        @JvmField val redRobot3IsTaxi:String,

        @JvmField val blueRP:Int,
        @JvmField val redRP:Int,
        @JvmField val blueQuintetAchieved:Boolean,
        @JvmField val redQuintetAchieved:Boolean,
        @JvmField val blueHangarBonusRP:Boolean,
        @JvmField val redHangarBonusRP:Boolean,
        @JvmField val blueCargoBonusRP:Boolean,
        @JvmField val redCargoBonusRP:Boolean,

        @JvmField val blueFoulCount:Int,
        @JvmField val redFoulCount:Int,
        @JvmField val blueTechFoulCount:Int,
        @JvmField val redTechFoulCount:Int,

        //From Red and Blue alliances
        @JvmField val redTotalPoints:Int,
        @JvmField val blueTotalPoints:Int

        //Maybe add YT vid field

)

