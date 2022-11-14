package com.memory_athlete.memoryassistant.mySpace;

public class ModelForSavingFiles {

    private String url_file;

    public ModelForSavingFiles(){

    }

    public ModelForSavingFiles(String url_file) {
        this.url_file = url_file;
    }

    public String getUrl_file() {
        return url_file;
    }

    public void setUrl_file(String url_file) {
        this.url_file = url_file;
    }
}
