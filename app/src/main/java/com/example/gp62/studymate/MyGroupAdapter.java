package com.example.gp62.studymate;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by GP62 on 2018-05-30.
 */

public class MyGroupAdapter extends RecyclerView.Adapter<MyGroupAdapter.ViewHolder> {

    private List<GroupItem> myGroupList;
    private int mygroup_itemLayout;

    public MyGroupAdapter(List<GroupItem> mygroupList, int mygroup_itemLayout) {
        this.myGroupList = mygroupList;
        this.mygroup_itemLayout = mygroup_itemLayout;
    }

    // 아이템xml과 java연결
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mygroup_itemLayout,parent,false);
        return new ViewHolder(view);
    }

    // listView getView 를 대체, 넘겨 받은 데이터를 화면에 출력하는 역할
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GroupItem groupItem = myGroupList.get(position);
        holder.my_group_title.setText(groupItem.getGroup_title());
        holder.my_group_cate.setText(groupItem.getGroup_cate());
        holder.my_group_region.setText(groupItem.getGroup_region());
        holder.my_group_introduce.setText(groupItem.getGroup_introduce());
        holder.my_group_cur_num.setText(""+groupItem.getGroup_cur_people());
        holder.my_group_max_num.setText(""+groupItem.getGroup_num_people());
        /*이미지 잠시 빈값으로 해놓음*/
        //holder.my_group_image.set
    }

    @Override
    public int getItemCount() {
        return myGroupList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView my_group_image;
        public TextView my_group_title;
        public TextView my_group_introduce;
        public TextView my_group_region;
        public TextView my_group_cate;
        public TextView my_group_cur_num;
        public TextView my_group_max_num;

        public ViewHolder(View itemView){
            super(itemView);
            my_group_image = (ImageView)itemView.findViewById(R.id.my_group_image);
            my_group_title = (TextView)itemView.findViewById(R.id.my_group_title);
            my_group_introduce = (TextView)itemView.findViewById(R.id.my_group_introduce);
            my_group_region = (TextView)itemView.findViewById(R.id.my_group_region);
            my_group_cate = (TextView)itemView.findViewById(R.id.my_group_cate);
            my_group_cur_num = (TextView)itemView.findViewById(R.id.my_group_cur_num);
            my_group_max_num = (TextView)itemView.findViewById(R.id.my_group_max_num);
        }
    }
}
