package com.example.gp62.studymate;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * Created by GP62 on 2018-05-30.
 */

class MyGroupSection extends StatelessSection {
    String title;
    List<GroupItem> itemList_member;
    Context context;

    String Tag = "MyGroupSection";

    public MyGroupSection(Context context, String title, List<GroupItem> list) {
        // call constructor with layout resources for this Section header and items
        super(SectionParameters.builder()
                .itemResourceId(R.layout.my_group_item)
                .headerResourceId(R.layout.section_header_member)
                .build());

        this.context = context;
        this.title = title;
        this.itemList_member = list;
    }

    @Override
    public int getContentItemsTotal() {
        return itemList_member.size(); // number of items of this section
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        // return a custom instance of ViewHolder for the items of this section
        return new MyItemViewHolder(view);
    }

    public List<GroupItem> getList() {
        return itemList_member;
    }


    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {
        MyItemViewHolder itemHolder = (MyItemViewHolder) holder;

        // bind your view here
        //itemHolder.tvItem.setText(itemList_member.get(position));
        GroupItem groupItem = itemList_member.get(position);
        //itemHolder..setText(itemList_member.get(position));
        itemHolder.my_group_title.setText(groupItem.getGroup_title());
        itemHolder.my_group_cate.setText(groupItem.getGroup_cate());
        itemHolder.my_group_region.setText(groupItem.getGroup_region());
        itemHolder.my_group_introduce.setText(groupItem.getGroup_introduce());
        itemHolder.my_group_cur_num.setText("" + groupItem.getGroup_cur_people());
        itemHolder.my_group_max_num.setText("" + groupItem.getGroup_num_people());
        /*이미지 잠시만 빈값으로 */
        String URL = new Variable().getURL();
        //Log.e(Tag,"URL : "+URL);
        //Log.e(Tag,"image : "+groupItem.getGroup_image());
        Glide
                .with(context)
//                    .load(mapList.get(position).getPlace_image())
                .load(URL+groupItem.getGroup_image())
                .override(100,100) // 이미지 크기 변경
                .placeholder(R.drawable.ic_launcher_background) //이미지 불러오기 전까지 보여질 이미지
                .error(R.drawable.ic_launcher_background) // 이미지로딩이 실패했을시 보여질 이미지
                .into(itemHolder.my_group_image);
//        }
//        itemHolder.my_group_image.setBackgroundResource(groupItem.getGroup_image());
//        Log.e(Tag,""+itemHolder);
        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go_group_view = new Intent(context, GroupViewActivity.class);
                // 리스트뷰에서 선택된 아이템 위치를 가져온다(get(position)), 그리고 그 아이템의 group_no을 가져온다 -> 공부 더 필요!
                Log.e(Tag,"position : "+position);
                Log.e(Tag,"itemList_member.size() : "+itemList_member.size());

                //go_group_view.putExtra("group_no", itemList_member.get(position).getGroup_no());
                Log.e(Tag,"itemList_member.get(position).getGroup_no() : "+itemList_member.get(position).getGroup_no());
                go_group_view.putExtra("group_no", itemList_member.get(position).getGroup_no());
                context.startActivity(go_group_view);
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

        headerHolder.tvTitle.setText(title);
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitle;

        HeaderViewHolder(View view) {
            super(view);

            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        }
    }

    class MyItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView my_group_image;
        public TextView my_group_title;
        public TextView my_group_introduce;
        public TextView my_group_region;
        public TextView my_group_cate;
        public TextView my_group_cur_num;
        public TextView my_group_max_num;

        public MyItemViewHolder(View itemView) {
            super(itemView);

            my_group_image = (ImageView) itemView.findViewById(R.id.my_group_image);
            my_group_title = (TextView) itemView.findViewById(R.id.my_group_title);
            my_group_introduce = (TextView) itemView.findViewById(R.id.my_group_introduce);
            my_group_region = (TextView) itemView.findViewById(R.id.my_group_region);
            my_group_cate = (TextView) itemView.findViewById(R.id.my_group_cate);
            my_group_cur_num = (TextView) itemView.findViewById(R.id.my_group_cur_num);
            my_group_max_num = (TextView) itemView.findViewById(R.id.my_group_max_num);
        }
    }
}
