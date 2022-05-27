class TextConverter {
    companion object {
        fun isCamelCase(text:String):Boolean = text.matches("([a-z]+[A-Z]+w+)+".toRegex())

        fun isSnakeCase(text:String):Boolean = text.matches("\\b[A-Z]+(_[A-Z]+)*\\b".toRegex())

        fun camelOrSnakeToTitle(text:String):String{
            if(isCamelCase(text)){
                return ""
            }
            if(isSnakeCase(text)){
                return ""
            }
            return text
        }

        fun stringToArray(str:String):Array<String> =str.replace("[","").replace("]","").split(", ").toTypedArray()
        fun stringToList(str:String):List<String> = str.replace("[","").replace("]","").split(", ")

    }
}