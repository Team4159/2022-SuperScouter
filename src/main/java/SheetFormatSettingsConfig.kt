
class SheetFormatSettingsConfig() {
    companion object{
        fun getInstance():SheetFormatSettingsConfig{
            var settingsConfig:SheetFormatSettingsConfig? = null
            if(settingsConfig == null){
                settingsConfig = SheetFormatSettingsConfig()
                return settingsConfig
            }
            return settingsConfig
        }
    }


}