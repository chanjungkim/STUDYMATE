package com.example.gp62.studymate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class GroupAdapter extends BaseAdapter { // 모임리스트 어댑터
    private Context context;
    private ArrayList<GroupItem> group_items_list;
    String Tag = "GroupAdapter";

    public GroupAdapter(Context context, ArrayList<GroupItem> group_items_list) {
        this.context = context;
        this.group_items_list = group_items_list;
    }

    @Override
    public int getCount() {
        return group_items_list.size();
    }

    @Override
    public Object getItem(int position) {
        return group_items_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

//    class ViewHolder {
//        TextView group_title_text_tv;
//        TextView group_max_num_tv;
//        TextView group_introduce_tv;
//        TextView group_region_tv;
//        TextView group_cate_tv;
//        ImageView group_image = null;
//    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.search_group_item, parent, false);

        // 모임리스트에 들어갈 내용
        // 모임리스트에 보여질 내용들
        TextView group_title = (TextView) convertView.findViewById(R.id.group_title); //모임제목
        TextView group_introduce = (TextView) convertView.findViewById(R.id.group_introduce); //모임 한줄 소개
        TextView group_region = (TextView) convertView.findViewById(R.id.group_region); // 활동지역
        TextView group_cate = (TextView) convertView.findViewById(R.id.group_cate); // 모임카테고리
        TextView group_max_num = (TextView) convertView.findViewById(R.id.group_max_num); //모임정원 (int)
        TextView group_cur_num = (TextView) convertView.findViewById(R.id.group_cur_num); //모임원 수 (int)
        ImageView group_image = (ImageView) convertView.findViewById(R.id.group_image); // 모임이미지
        // 이미지 보류
//        ImageView group_image = null;
        String URL = new Variable().getURL();

        if(group_items_list.size()>0){
            Glide
                    .with(context)
//                    .load(mapList.get(position).getPlace_image())
                    .load(URL+group_items_list.get(position).getGroup_image())
                    .override(100,100) // 이미지 크기 변경
                    .placeholder(R.drawable.ic_launcher_background) //이미지 불러오기 전까지 보여질 이미지
                    .error(R.drawable.ic_launcher_background) // 이미지로딩이 실패했을시 보여질 이미지
                    .into(group_image);

            //Log.e(Tag,"이미지 가져오는 주소 : "+URL+group_items_list.get(position).getGroup_image());

            group_title.setText(group_items_list.get(position).getGroup_title());
            group_introduce.setText(group_items_list.get(position).getGroup_introduce());
            group_region.setText(group_items_list.get(position).getGroup_region());
            group_cate.setText(group_items_list.get(position).getGroup_cate());
            group_cur_num.setText("" + group_items_list.get(position).getGroup_cur_people());
            group_max_num.setText("" + group_items_list.get(position).getGroup_num_people());
        }


        // 태그만들기
        convertView.setTag(group_items_list.get(position).getGroup_title());
        return convertView;
    }
}
