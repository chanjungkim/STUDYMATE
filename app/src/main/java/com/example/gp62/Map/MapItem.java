package com.example.gp62.Map;

import java.io.Serializable;

/**
 * Created by GP62 on 2018-05-25.
 */

public class MapItem implements Serializable { // 주변 스터디룸 정보 가지고 올 내용 (json포맷)
    public String place_image;
    public String place_name;
    public String place_url;
    public String address_name;
    public String road_address_name;
    public String phone;
    public double x;
    public double y;
    public double distance;
    public String category_group_name;
    public String category_group_code;
    public String id;

    public MapItem(String place_image, String place_name, String place_url, String address_name, String road_address_name, String phone, double x, double y, double distance, String category_group_name, String category_group_code, String id) {
        this.place_image = place_image;
        this.place_name = place_name;
        this.place_url = place_url;
        this.address_name = address_name;
        this.road_address_name = road_address_name;
        this.phone = phone;
        this.x = x;
        this.y = y;
        this.distance = distance;
        this.category_group_name = category_group_name;
        this.category_group_code = category_group_code;
        this.id = id;
    }

    public void setPlace_image(String place_image) {
        this.place_image = place_image;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public void setPlace_url(String place_url) {
        this.place_url = place_url;
    }

    public void setAddress_name(String address_name) {
        this.address_name = address_name;
    }

    public void setRoad_address_name(String road_address_name) {
        this.road_address_name = road_address_name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setCategory_group_name(String category_group_name) {
        this.category_group_name = category_group_name;
    }

    public void setCategory_group_code(String category_group_code) {
        this.category_group_code = category_group_code;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlace_image() {
        return place_image;
    }

    public String getPlace_name() {
        return place_name;
    }

    public String getPlace_url() {
        return place_url;
    }

    public String getAddress_name() {
        return address_name;
    }

    public String getRoad_address_name() {
        return road_address_name;
    }

    public String getPhone() {
        return phone;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getDistance() {
        return distance;
    }

    public String getCategory_group_name() {
        return category_group_name;
    }

    public String getCategory_group_code() {
        return category_group_code;
    }

    public String getId() {
        return id;
    }
}
