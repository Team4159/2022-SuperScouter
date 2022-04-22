import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.io.FileWriter
import java.io.Writer
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

    fun placeHolder(){
        /*System.out.println(service.getAllMatchJsonKeys(matches.get(1),false));
        System.out.println(service.getMatchJsonValueByKey(matches.get(1),service.getAllMatchJsonKeys(matches.get(1),false)));
        var vals = new ArrayList<List<Object>>();
        service.getAllMatchJsonKeys(matches.get(1),false).forEach(
            item -> {
            vals.add(Collections.singletonList(item));
        }
        );
        var vals2 = new ArrayList<List<Object>>();
        for(int i=0;i<vals.size();i++){
            vals2.add(Collections.singletonList(true));
        }*/
        // Spreadsheet.createCheckbox("Format Settings", 0,vals.size(),1,2);
        // Spreadsheet.insertData(vals, "Format Settings"+createA1Range("A1", 1, vals.size()));
        // Spreadsheet.resizeRange("Format Settings", 0, vals.size());
        // Spreadsheet.insertData(vals2, "Format Settings"+createA1Range("B1", 1, vals.size()));
        // Spreadsheet.resizeRange("Sheet2", 1, 3);
        // var dataAs2DList = Spreadsheet.getData("Format Settings"+createA1Range("A1",1,vals.size()));
        // var includedEntries = new ArrayList<String>(Collections.emptyList());
        // System.out.println(dataAs2DList);
        // dataAs2DList.forEach(pair -> {
        //     if(Boolean.parseBoolean(pair.get(1))){
        //         includedEntries.add(pair.get(0));
        //     }
        // });
        // System.out.println(includedEntries);
        //System.out.println(Spreadsheet.getData());
    }

    fun createFormatSettingsSheet(sheetName:String):Unit {
        if(Spreadsheet.checkIfExists(sheetName)) return
        Spreadsheet.createTab(sheetName)
        val matches = service.getMatches2()
        /*Spreadsheet.insertData(
            listOf(
                    listOf(service.getAllMatchJsonKeys(service.getMatches2()[0], false))
            ), App.createA1Range("A1",105,1)
        )*/
        Spreadsheet.createCheckbox(sheetName, 1,105,2,2)
    }
    fun updateSettingsJson():Unit {

    }

    fun readSettingsJson():Unit {

    }

}