package com.example.gp62.studymate;

import java.util.ArrayList;

/**
 * Created by GP62 on 2018-07-10.
 */

public class ChatItem_ToServer {
    String chat_group_no; // 스터디그룹번호
    String chat_user_nick;
    ArrayList<String> member_no; // 그룹원들번호
    String chat_user_no; // 유저번호
    String message; // 메세지 내용

    public ChatItem_ToServer(String chat_group_no, String chat_user_nick, ArrayList<String> member_no, String chat_user_no, String message) {
        this.chat_group_no = chat_group_no;
        this.chat_user_nick = chat_user_nick;
        this.member_no = member_no;
        this.chat_user_no = chat_user_no;
        this.message = message;
    }

    public String getChat_group_no() {
        return chat_group_no;
    }

    public String getChat_user_nick() {
        return chat_user_nick;
    }

    public ArrayList<String> getMember_no() {
        return member_no;
    }

    public String getChat_user_no() {
        return chat_user_no;
    }

    public String getMessage() {
        return message;
    }
}
