import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.io.Writer
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

class SheetFormatSettingsConfig {
    private val gson:Gson = GsonBuilder().setPrettyPrinting().create()
    private val service = BlueAllianceMatchServiceImpl(PropReader.getProperty("AUTH_KEY"), "2022casj")
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

    fun updateSettingsJson():Unit {

    }

    fun readSettingsJson():Unit {

    }

}