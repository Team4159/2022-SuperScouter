import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.io.FileWriter
import java.io.Writer
import java.nio.file.Paths

class SheetFormatSettingsConfig() {
    private val gson:Gson = GsonBuilder().setPrettyPrinting().create()
    //private val writer:Writer = FileWriter(Paths.get("./resources/settings.json").toString())

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

    fun createFormatSettingsSheet(sheetName:String):Unit {
        //if(Spreadsheet.checkIfExists(sheetName)) return
        //Spreadsheet.createTab(sheetName)
    }
    fun updateSettingsJson():Unit {

    }

    fun readSettingsJson():Unit {

    }
}