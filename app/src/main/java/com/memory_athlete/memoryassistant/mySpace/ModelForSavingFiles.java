package com.memory_athlete.memoryassistant.mySpace;

import androidx.annotation.Keep;

@Keep
public class ModelForSavingFiles {

    public String url_file;

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
