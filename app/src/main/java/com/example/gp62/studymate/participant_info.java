package com.example.gp62.studymate;

import java.io.Serializable;

/**
 * Created by GP62 on 2018-05-31.
 */

public class participant_info implements Serializable{
    String participant_image;
  //  String participant_crown;
    String participant_nick;
    String participant_gender;
    String participant_age;
    String participant_no;

    public participant_info(String participant_no, String participant_image, String participant_nick, String participant_gender, String participant_age) {
        this.participant_image = participant_image;
       // this.participant_crown = crown;
        this.participant_nick = participant_nick;
        this.participant_no = participant_no;
        this.participant_gender = participant_gender;
        this.participant_age = participant_age;
    }

//    public String getCrown() {
//        return participant_crown;
//    }

    public String getParticipant_no() {
        return participant_no;
    }

    public String getParticipant_image() {
        return participant_image;
    }

    public String getParticipant_nick() {
        return participant_nick;
    }

    public String getParticipant_gender() {
        return participant_gender;
    }

    public String getParticipant_age() {
        return participant_age;
    }
}
