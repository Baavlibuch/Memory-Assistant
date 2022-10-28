package com.memory_athlete.memoryassistant.language;

public class SettingLanguage {

    public String string_to_locale = null;

    public String setLang(String val){

        switch (val) {
            case "Hindi":
                 string_to_locale = "hi";
                break;
            case "Bengali":
                string_to_locale = "bn";
                break;
            case "Arabic":
                string_to_locale = "ar";
                break;
            case "Czech":
                string_to_locale = "cs";
                break;
            case "German":
                string_to_locale = "de";
                break;
            case "Spanish":
                string_to_locale = "es";
                break;
            case "Filipino":
                string_to_locale = "fil";
                break;
            case "French":
                string_to_locale = "fr";
                break;
            case "Italian":
                string_to_locale = "it";
                break;
            case "Japanese":
                string_to_locale = "ja";
                break;
            case "Korean":
                string_to_locale = "ko";
                break;
            case "Malay":
                string_to_locale = "ms";
                break;
            case "Norwegian":
                string_to_locale = "no";
                break;
            case "Portuguese":
                string_to_locale = "pt";
                break;
            case "Portuguese-Brazil":
                string_to_locale = "pt-rBR";
                break;
            case "Russian":
                string_to_locale = "ru";
                break;
            case "Swahili":
                string_to_locale = "sw";
                break;
            case "Turkish":
                string_to_locale = "tr";
                break;
            case "Chinese":
                string_to_locale = "zh";
                break;
            default:
                string_to_locale = "en";
        }

        return(string_to_locale);

    }

}
