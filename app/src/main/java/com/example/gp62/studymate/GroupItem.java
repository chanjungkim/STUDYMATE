package com.example.gp62.studymate;

/**
 * Created by GP62 on 2018-05-11.
 */

public class GroupItem { // 모임리스트에 보여질 내용들 (모임어댑터=>GroupAdapter getView 변수명과 같은지 확인)
//    String ImageView;

    //모임제목  //모임 한줄 소개 // 활동지역 // 모임카테고리 //모임정원 (int) //모임원 수 (int)
    String group_title; //모임제목
    String group_introduce; //모임 한줄 소개
    String group_region;
    String group_cate;
    int group_max_num;
    int group_cur_num;
    String group_image;

    // 모임리스트 아이템에 보여지는 내용은 아님. 하지만, 모임수정,삭제시 필요함 (group_no로, 모임리스트 중 어떤 모임을 수정,삭제할건지 알 수 있음.)
    int group_no;

    public GroupItem(int group_no,String group_cate, String group_title, String group_introduce, String group_region, int group_cur_num, int group_max_num, String group_image) {
        this.group_cate = group_cate;
        this.group_no = group_no;
        this.group_title = group_title;
        this.group_introduce = group_introduce;
        this.group_region = group_region;
        this.group_cur_num = group_cur_num;
        this.group_max_num = group_max_num;
        this.group_image = group_image;
    }

    public String getGroup_image() {
        return group_image;
    }

    public void setGroup_image(String group_image) {
        this.group_image = group_image;
    }

    public int getGroup_no() {
        return group_no;
    }

    public void setGroup_no(int group_no) {
        this.group_no = group_no;
    }

    public String getGroup_cate() {
        return group_cate;
    }

    public void setGroup_cate(String group_cate) {
        this.group_cate = group_cate;
    }

    public String getGroup_title() {
        return group_title;
    }

    public void setGroup_title(String group_title) {
        this.group_title = group_title;
    }

    public String getGroup_introduce() {
        return group_introduce;
    }

    public void setGroup_introduce(String group_introduce) {
        this.group_introduce = group_introduce;
    }

    public String getGroup_region() {
        return group_region;
    }

    public void setGroup_region(String group_region) {
        this.group_region = group_region;
    }

    public int getGroup_cur_people() {
        return group_cur_num;
    }

    public void setGroup_cur_people(int group_cur_num) {
        this.group_cur_num = group_cur_num;
    }

    public int getGroup_num_people() {
        return group_max_num;
    }

    public void setGroup_num_people(int group_max_num) {
        this.group_max_num = group_max_num;
    }
}