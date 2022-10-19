package de.remadisson.dcfheck.enums;

public enum LogType {

    CMD("[COMMAND]"), INFO("[INFO]"), WARNING("[WARNING]");

    private String prefix;

    LogType(String prefix){
        this.prefix = prefix;
    }

    public String getPrefix(){
        return prefix;
    }

}
