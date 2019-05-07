package com.example.gp62.studymate;

/**
 * Created by GP62 on 2018-05-11.
 */

public class Variable {
    // URL
    private String SERVER_URL;
    private String IP;

    public String getURL(){
        //SERVER_URL = "http://ec2-13-209-65-255.ap-northeast-2.compute.amazonaws.com/";
        SERVER_URL = "http://ec2-13-124-89-170.ap-northeast-2.compute.amazonaws.com/";
        return SERVER_URL;
    }

    public String getIP(){
        IP = "192.168.43.74";
        return IP;
    }

    // 쓰는 법
//    //get
//    String s =  variable.getInstance().getURL();

}
