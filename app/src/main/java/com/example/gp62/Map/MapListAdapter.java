package com.example.gp62.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gp62.studymate.R;

import java.util.ArrayList;

/**
 * Created by GP62 on 2018-05-30.
 */

public class MapListAdapter extends RecyclerView.Adapter<MapListAdapter.ViewHolder> {

    private ArrayList<MapItem> mapList;
    private int map_list_item;
    String Tag = "MapListAdapter";
    Handler handler = new Handler();
    Context context;

    public MapListAdapter(Context context, ArrayList<MapItem> mapList, int map_list_item) {
        this.context = context;
        this.mapList = mapList;
        if (this.mapList == null) {
            Log.e("@@@@", "맵리스트 널임 ");
        }
        this.map_list_item = map_list_item;
    }

    // 아이템xml과 java연결
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(map_list_item, parent, false);
        return new ViewHolder(view);
    }

    // listView getView 를 대체, 넘겨 받은 데이터를 화면에 출력하는 역할
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final long pos=holder.getItemId();

        Log.e(Tag, ""+pos);
        // 핸드폰 가로세로 길이에 따라 그리드뷰 아이템 크기가 달라짐
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) holder.itemView.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int deviceWidth = displayMetrics.widthPixels;  // 핸드폰의 가로 해상도를 구함.
// int deviceHeight = displayMetrics.heightPixels;  // 핸드폰의 세로 해상도를 구함.
        deviceWidth = deviceWidth / 2;
        int deviceHeight = (int) (deviceWidth * 1.5);  // 세로의 길이를 가로의 길이의 1.5배로 하고 싶었다.
        holder.itemView.getLayoutParams().height = deviceHeight;  // 아이템 뷰의 세로 길이를 구한 길이로 변경
        holder.itemView.requestLayout(); // 변경 사항 적용

        final MapItem mapItem = mapList.get(position);

        // 스터디룸사진이 있다면, // url주소를 통해 이미지 가져오기
        //Log.e(Tag,"이미지 받아오나 : "+ mapItem.getPlace_image());
//        if (!mapItem.getPlace_image().equals("")) {
//            Log.e(Tag, "스터디룸 사진 있음");
//            Thread image_thread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    URL url = null;
//                    try {
//                        Log.e(Tag, "아이템이미지 : " + mapItem.getPlace_image());
//                        // url = new URL(mapItem.getPlace_image());
//
//                        url = new URL(mapList.get(position).getPlace_image());
//                        InputStream inputStream = url.openStream();
//                        final Bitmap bm = BitmapFactory.decodeStream(inputStream);
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (holder.getItemId()==pos){
//                                    holder.room_image.setImageBitmap(bm);
//                                }
//
///*                                else {
//                                    holder.room_image.setBackgroundResource(0);
//                                }*/
//                            }
//                        });
//                    } catch (MalformedURLException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//            image_thread.start();
//        }
        //Log.e(Tag,"이미지가져와라!"+mapItem.getPlace_image());
/*        if (!mapItem.getPlace_image().equals("")) {
            Glide
                    .with(context)
//                    .load(mapList.get(position).getPlace_image())
                    .load(mapItem.getPlace_image())
                    .override(100,100) // 이미지 크기 변경
                    .placeholder(R.mipmap.ic_launcher_round) //이미지 불러오기 전까지 보여질 이미지
                    .error(R.mipmap.ic_launcher) // 이미지로딩이 실패했을시 보여질 이미지
                    .into(holder.room_image);
        }
        else { //스터디룸 사진이 없다면, 기본 이미지가 나오게 하기
            holder.room_image.setBackgroundResource(0);
            Log.e(Tag, "스터디룸 사진X");
        }*/

//        if(mapItem.getPlace_image().equals("")){
//            holder.room_image.setBackgroundResource(R.drawable.ic_launcher_background);
//            Log.e(Tag, "스터디룸 사진X");
//        }else {
            Glide
                    .with(context)
//                    .load(mapList.get(position).getPlace_image())
                    .load(mapItem.getPlace_image())
                    .override(100,100) // 이미지 크기 변경
//                    .placeholder(R.mipmap.ic_launcher_round) //이미지 불러오기 전까지 보여질 이미지
//                    .error(R.mipmap.ic_launcher) // 이미지로딩이 실패했을시 보여질 이미지
                    .into(holder.room_image);
//        }

/*        // 스트링을 비트맵으로 바꾸기
        byte[] decodedByteArray = Base64.decode(mapItem.getPlace_image(), Base64.NO_WRAP);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);*/

       /* holder.room_image.setImageBitmap(decodedBitmap);*/


        Log.e(Tag, "이미지사진" + "(" + position + ") : " + mapItem.getPlace_image());
        holder.room_title.setText(mapItem.getPlace_name());
        holder.room_address.setText(mapItem.getAddress_name());
        holder.room_phone.setText(mapItem.getPhone());
        holder.room_review_num.setText("0");
        double distance_m = mapItem.getDistance() * 0.001;
        double distance_km = Math.round(distance_m * 10d) / 10d;
        holder.room_distance.setText("" + distance_km);
    }

    @Override
    public int getItemCount() {
        if (mapList != null) {
            return mapList.size();
        }
        Log.e("맵리스트어댑터", "맵리스트 null임");
        return -1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView room_image;
        //public TextView room_star;
        public TextView room_title;
        public TextView room_address;
        public TextView room_phone;
        public TextView room_review_num;
        public TextView room_distance;
        // public LinearLayout group_layout;

        public ViewHolder(View itemView) {
            super(itemView);
            room_image = (ImageView) itemView.findViewById(R.id.room_image);
            //room_star = (TextView)itemView.findViewById(R.id.room_star);
            room_title = (TextView) itemView.findViewById(R.id.room_title);
            room_address = (TextView) itemView.findViewById(R.id.room_address);
            room_phone = (TextView) itemView.findViewById(R.id.room_phone);
            room_review_num = (TextView) itemView.findViewById(R.id.room_review_num);
            room_distance = (TextView) itemView.findViewById(R.id.room_distance);
            // group_layout = itemView.findViewById(R.id.group_layout);
        }
    }
}
