package com.example.gp62.studymate;

import android.content.Context;
import android.content.Intent;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

/**
 * Created by GP62 on 2018-05-30.
 */

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.ViewHolder> {

    private List<participant_info> participantList;
    private int participant_itemLayout;
    Context context;
    String Tag = "ParticipantAdapter";

    public ParticipantAdapter(Context context, List<participant_info> participantList, int participant_itemLayout) {
        this.context = context;
        this.participantList = participantList;
        this.participant_itemLayout = participant_itemLayout;
    }

    // 아이템xml과 java연결
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(participant_itemLayout,parent,false);
        return new ViewHolder(view);
    }

    // listView getView 를 대체, 넘겨 받은 데이터를 화면에 출력하는 역할
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        participant_info participantInfo = participantList.get(position);
        //holder.participant_image.setImageResource(participantInfo.getParticipant_image());
//        if(participantInfo.getCrown().equals("GONE")){
//            holder.participant_crown.setVisibility(View.GONE);
//        }else if(participantInfo.getCrown().equals("VISIBLE")){
//            holder.participant_crown.setVisibility(View.VISIBLE);
//        }
        String URL = new Variable().getURL();

         /*프사 원형으로 나오게 하기*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            holder.participant_image.setBackground(new ShapeDrawable(new OvalShape()));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.participant_image.setClipToOutline(true);
        }

        Glide
                .with(context)
//                    .load(mapList.get(position).getPlace_image())
                .load(URL+participantInfo.getParticipant_image())
                .centerCrop()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
//                .override(100,100) // 이미지 크기 변경
                .placeholder(R.drawable.ic_launcher_background) //이미지 불러오기 전까지 보여질 이미지
                .error(R.drawable.ic_launcher_background) // 이미지로딩이 실패했을시 보여질 이미지
//                .bitmapTransform(new CropCircleTransformation(context))
                .into(holder.participant_image);

        //.e(Tag,"참여자이미지주소 : "+URL+participantInfo.getParticipant_image());

        holder.participant_nick.setText(participantInfo.getParticipant_nick());
        holder.participant_gender.setText(participantInfo.getParticipant_gender());
        holder.participant_age.setText(participantInfo.getParticipant_age());

        // 참여자리스트에서 한 명의 유저를 클릭하면, 유저프로필화면으로 이동한다.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go_user_profile = new Intent(context,UserProfileActivity.class);
                // 유저의 사진주소, 닉네임, 성별/나이대를 보낸다.
                go_user_profile.putExtra("user_image",participantList.get(position).getParticipant_image());
                go_user_profile.putExtra("user_nick",participantList.get(position).getParticipant_nick());
                go_user_profile.putExtra("user_gender",participantList.get(position).getParticipant_gender());
                go_user_profile.putExtra("user_age",participantList.get(position).getParticipant_age());
                go_user_profile.putExtra("user_no",participantList.get(position).getParticipant_no());

                Log.e(Tag,"position : "+position);
                Log.e(Tag,"participantList.size() : "+participantList.size());

                Log.e(Tag,"user_image"+participantList.get(position).getParticipant_image());
                Log.e(Tag,"user_nick"+participantList.get(position).getParticipant_nick());
                Log.e(Tag,"user_gender"+participantList.get(position).getParticipant_gender());
                Log.e(Tag,"user_age"+participantList.get(position).getParticipant_age());
                Log.e(Tag,"user_no"+participantList.get(position).getParticipant_no());

                context.startActivity(go_user_profile);
            }
        });
    }

    @Override
    public int getItemCount() {
        return participantList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView participant_image;
       // public ImageView participant_crown;
        public TextView participant_nick;
        public TextView participant_gender;
        public TextView participant_age;

        public ViewHolder(View itemView){
            super(itemView);
            participant_image = (ImageView)itemView.findViewById(R.id.participant_image);
           // participant_crown = (ImageView)itemView.findViewById(R.id.crown);
            participant_nick = (TextView)itemView.findViewById(R.id.participant_nick);
            participant_gender = (TextView)itemView.findViewById(R.id.participant_gender);
            participant_age = (TextView)itemView.findViewById(R.id.participant_age);
        }
    }
}
