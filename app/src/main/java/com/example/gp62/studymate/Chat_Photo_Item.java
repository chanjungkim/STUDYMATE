package com.example.gp62.studymate;

import org.json.JSONArray;

/**
 * Created by GP62 on 2018-08-11.
 */

public class Chat_Photo_Item {
    String message_Type;
    JSONArray jsonArray;

    public Chat_Photo_Item(JSONArray jsonArray) {

        this.jsonArray = jsonArray;
    }



    public JSONArray getJsonArray() {
        return jsonArray;
    }
}
