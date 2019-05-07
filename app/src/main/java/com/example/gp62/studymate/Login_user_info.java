package com.example.gp62.studymate;

/**
 * Created by GP62 on 2018-05-23.
 */

public class Login_user_info { // 로그인한 회원정보모음
    String user_no;// 회원의 고유번호
    String user_email;// 이메일
    String user_photo;// 프로필사진
    String user_nick;// 닉네임
    String user_gender;// 성별
    String user_age;// 연령대
    String user_region;// 관심 지역
    String user_cate;// 관심 카테고리

    public Login_user_info(String user_no, String user_email, String user_photo, String user_nick, String user_gender, String user_age, String user_region, String user_cate) {
        this.user_no = user_no;
        this.user_email = user_email;
        this.user_photo = user_photo;
        this.user_nick = user_nick;
        this.user_gender = user_gender;
        this.user_age = user_age;
        this.user_region = user_region;
        this.user_cate = user_cate;
    }

    public String getUser_no() {
        return user_no;
    }

    public String getUser_email() {
        return user_email;
    }

    public String getUser_photo() {
        return user_photo;
    }

    public String getUser_nick() {
        return user_nick;
    }

    public String getUser_gender() {
        return user_gender;
    }

    public String getUser_age() {
        return user_age;
    }

    public String getUser_region() {
        return user_region;
    }

    public String getUser_cate() {
        return user_cate;
    }
}
