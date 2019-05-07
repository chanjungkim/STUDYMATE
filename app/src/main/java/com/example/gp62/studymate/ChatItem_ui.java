package com.example.gp62.studymate;

/**
 * Created by GP62 on 2018-07-10.
 */

public class ChatItem_ui {
    String chat_user_pic; // 프사
    String chat_user_nick; // 닉네임
    String message; // 메세지 내용
    String chat_user_no;// 유저넘버
    String message_image_path; // 이미지 메세지 경로
    boolean WriteOrRead;
    // true이면 write
    // false이면 read

//    public ChatItem_ui(String chat_user_photo, String chat_user_nick, String message, String chat_user_no) {
//        this.chat_user_photo = chat_user_photo;
//        this.chat_user_nick = chat_user_nick;
//        this.message = message;
//        this.chat_user_no = chat_user_no;
//    }

    public ChatItem_ui(String message, String chat_user_no, String chat_user_nick, String chat_user_pic, String message_image_path,boolean WriteOrRead) {
        this.message = message;
        this.chat_user_no = chat_user_no;
        this.chat_user_nick = chat_user_nick;
        this.chat_user_pic = chat_user_pic;
        this.message_image_path = message_image_path;
        this.WriteOrRead = WriteOrRead;
    }

//    public ChatItem_ui(String message, String chat_user_no, String chat_user_nick, String chat_user_pic) {
//        this.message = message;
//        this.chat_user_no = chat_user_no;
//        this.chat_user_nick = chat_user_nick;
//        this.chat_user_pic = chat_user_pic;
//    }

    public String getChat_user_pic() {
        return chat_user_pic;
    }
//
    public String getChat_user_nick() {
        return chat_user_nick;
    }

    public String getMessage() {
        return message;
    }

    public String getChat_user_no() {
        return chat_user_no;
    }

    public String getMessage_image_path() {
        return message_image_path;
    }

    public boolean isWriteOrRead() {
        return WriteOrRead;
    }
}
