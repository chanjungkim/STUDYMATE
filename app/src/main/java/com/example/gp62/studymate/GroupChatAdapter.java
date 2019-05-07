package com.example.gp62.studymate;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by GP62 on 2018-05-30.
 */

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.ViewHolder> {

    private List<ChatItem_ui> GroupChatList;
    private int group_chat_itemLayout;
    Context context;
    String Tag = "GroupChatAdapter";
    String user_no;
    int right = 1;
    int left = 2;

    public GroupChatAdapter(Context context, List<ChatItem_ui> GroupChatList, int group_chat_itemLayout, String user_no) {
        this.context = context;
        this.GroupChatList = GroupChatList;
        this.group_chat_itemLayout = group_chat_itemLayout;
        this.user_no = user_no;
    }

    //viewType별로 위치 바꿀 수 있음 // 내가 보낸 메세지와 남이 보낸 메세지 구분하기
    @Override
    public int getItemViewType(int position) {
        //Log.e(Tag, "GroupChatList.get(position).getChat_user_no() : " + GroupChatList.get(position).getChat_user_no());
        //Log.e(Tag, "user_no : " + user_no);
        if (GroupChatList.get(position).getChat_user_no().equals(user_no)) {
            return right;
        } else {
            return left;
        }
        //return super.getItemViewType(position);
    }

    // 아이템xml과 java연결
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == right) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.right_chat_item, parent, false);
            Log.e(Tag, "오른쪽이당");
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.left_chat_item, parent, false);
            Log.e(Tag, "왼쪽이당");
        }

        return new ViewHolder(view);
    }

    // listView getView 를 대체, 넘겨 받은 데이터를 화면에 출력하는 역할
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChatItem_ui chatItem_ui = GroupChatList.get(position);
        // 텍스트가 있는 경우
        if(chatItem_ui.getMessage()!=null){
            //Log.e(Tag,"텍스트 있다.");
            holder.group_chat_context.setVisibility(View.VISIBLE);
            holder.group_chat_image_talk.setVisibility(View.GONE);
            // 내가 방금 올린 텍스트
            if(GroupChatList.get(position).isWriteOrRead()){
                holder.group_chat_context.setText(chatItem_ui.getMessage());
            }else{
                holder.group_chat_context.setText(chatItem_ui.getMessage());
            }
            //holder.group_chat_image_talk.setVisibility(View.GONE);
        }else{
            holder.group_chat_context.setVisibility(View.GONE);
            holder.group_chat_image_talk.setVisibility(View.VISIBLE);
        }

/*        // 메세지 이미지가 있는 경우 // URL을 통해서 가져오는 경우,
        if(!chatItem_ui.getMessage_image().isEmpty()){
            Log.e(Tag,"이미지 있다");
        }else{ //이미지가 없는 경우
            Log.e(Tag,"이미지 없다.");
        }*/

        // 메세지 이미지가 있는 경우 // 앨범에서 방금 가져온 이미지인 경우,
        if(chatItem_ui.getMessage_image_path()!=null){
            holder.group_chat_context.setVisibility(View.GONE);
            holder.group_chat_image_talk.setVisibility(View.VISIBLE);
            //Log.e(Tag,"이미지 있다 : "+GroupChatList.get(position).getMessage_image());
            String SERVER_URL = new Variable().getURL();
            String image_path = null;
            //Uri myUri = Uri.parse(GroupChatList.get(position).getMessage_image());
            //Log.e(Tag,"이미지 절대경로 : "+RealPathUtil.getRealPath(context,myUri));
            //Log.e(Tag,"이미지 절대경로 : "+media.getImagePath());

            // 내가 방금 올린 이미지
            if(GroupChatList.get(position).isWriteOrRead()){
                image_path = GroupChatList.get(position).getMessage_image_path();
                Glide
                        .with(context)
                        .load(image_path)
                        .placeholder(R.drawable.ic_launcher_background) //이미지 불러오기 전까지 보여질 이미지
                        .error(R.drawable.ic_launcher_background) // 이미지로딩이 실패했을시 보여질 이미지
                        .into(holder.group_chat_image_talk);
            }else{
                image_path = SERVER_URL+ GroupChatList.get(position).getMessage_image_path();
                Glide
                        .with(context)
                        .load(image_path)
                        .placeholder(R.drawable.ic_launcher_background) //이미지 불러오기 전까지 보여질 이미지
                        .error(R.drawable.ic_launcher_background) // 이미지로딩이 실패했을시 보여질 이미지
                        .into(holder.group_chat_image_talk);
            }
//            .override(100, 100) // 이미지 크기 변경

        }else{ //이미지가 없는 경우
            //Log.e(Tag,"이미지 없다.");
        }

        // 나인 경우, 닉네임과 프사 안나오게 하기
        if(GroupChatList.get(position).getChat_user_no().equals(user_no)){
            // 닉네임
            holder.group_chat_nick.setVisibility(View.GONE);
            // 프사
            holder.group_chat_image.setVisibility(View.GONE);

        }else{ // 내가 아닌 경우는 나오게 하기
            holder.group_chat_nick.setText(chatItem_ui.getChat_user_nick());
//            /*프사 원모양으로 나오게 하기*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.group_chat_image.setBackground(new ShapeDrawable(new OvalShape()));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.group_chat_image.setClipToOutline(true);
            }
            String URL = new Variable().getURL();

            Glide
                    .with(context)
                    .load(URL + GroupChatList.get(position).getChat_user_pic())
                    //.override(100, 100) // 이미지 크기 변경
                    .placeholder(R.drawable.ic_launcher_background) //이미지 불러오기 전까지 보여질 이미지
                    .error(R.drawable.ic_launcher_background) // 이미지로딩이 실패했을시 보여질 이미지
                    .into(holder.group_chat_image);
        }
    }

    @Override
    public int getItemCount() {
        return GroupChatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView group_chat_image;
        public ImageView group_chat_image_talk;
        public TextView group_chat_nick;
        public TextView group_chat_context;


        public ViewHolder(View itemView) {
            super(itemView);
            group_chat_image = (ImageView) itemView.findViewById(R.id.group_chat_image);
            group_chat_image_talk = (ImageView) itemView.findViewById(R.id.group_chat_image_talk);
            group_chat_nick = (TextView) itemView.findViewById(R.id.group_chat_nick);
            group_chat_context = (TextView) itemView.findViewById(R.id.group_chat_context);
            //Log.e(Tag,"그룹어댑터_user_no : "+user_no);
        }
    }
}
