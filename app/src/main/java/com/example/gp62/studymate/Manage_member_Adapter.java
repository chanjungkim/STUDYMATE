package com.example.gp62.studymate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by GP62 on 2018-06-29.
 */

public class Manage_member_Adapter extends RecyclerView.Adapter<Manage_member_Adapter.ViewHolder> {

    private int member_list_item; // 멤버아이템 레이아웃
    String Tag = "Manage_member_Adapter";
    private ArrayList<participant_info> memberList; // 멤버리스트
    Context context;

    public Manage_member_Adapter(int member_list_item, ArrayList<participant_info> memberList, Context context) {
        this.member_list_item = member_list_item;
        this.memberList = memberList;
        this.context = context;
    }

    // 아이템 xml과 java연결
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(member_list_item, parent, false);
        return new ViewHolder(view);
    }

    // listView getView 를 대체, 넘겨 받은 데이터를 화면에 출력하는 역할
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }


    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_member_image;
        public TextView tv_member_nick;
        public CheckBox cb_member_check;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_member_image = (ImageView) itemView.findViewById(R.id.iv_member_image);
            tv_member_nick = (TextView) itemView.findViewById(R.id.tv_member_nick);
            cb_member_check = (CheckBox) itemView.findViewById(R.id.cb_member_check);
        }
    }
}
