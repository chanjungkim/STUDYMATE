package com.example.gp62.Map;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.gp62.studymate.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.Place;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;




/* 장소검색탭을 클릭하면 나오는 화면*/
public class MapList2Activity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        PlacesListener {


    String Tag = "MapList2Activity";
    private GoogleApiClient mGoogleApiClient = null;
    private GoogleMap mGoogleMap = null;
    private Marker currentMarker = null;

    //private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초

    private AppCompatActivity mActivity;
    boolean askPermissionOnceAgain = false;
    boolean mRequestingLocationUpdates = false;
    Location mCurrentLocatiion;
    boolean mMoveMapByUser = true;
    boolean mMoveMapByAPI = true;
    LatLng currentPosition;
    List<Marker> previous_marker = null; //마커리스트
    ArrayList<MapItem> mapItemList = new ArrayList<>(); // 스터디룸목록
    Handler handler;


    LocationRequest locationRequest = new LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL_MS)
            .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

    TextView tv_current_location;

    boolean showCurLocation = true;
    Button studylist_btn; // 목록보기 버튼

    ProgressBar progressBar_map; // 지도 화면 로딩 프로그래스바

/*    // 스터디룸사진 url
    String photo = "";
    // 스터디룸리스트 사이즈만큼 반복문 돌릴 때,
    int j = 0;*/

    /*  켰을 때, 구글api연결하기*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);

//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_map_list2);
        // 지도화면 로딩
        progressBar_map = findViewById(R.id.progressBar_map);
        progressBar_map.setVisibility(View.VISIBLE);


//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
//                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        tv_current_location = findViewById(R.id.current_location);
        //Log.e(TAG, "onCreate");
        //Log.e(TAG, "1");
        mActivity = this;

        //Log.e(TAG, "2");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        //Log.e(TAG, "3");


        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //Log.e(TAG, "4");

        previous_marker = new ArrayList<Marker>();


         /* 목록보기 버튼*/
        studylist_btn = (Button) findViewById(R.id.studylist_btn);
        studylist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go_map_list = new Intent(getApplicationContext(), MapListActivity.class);
                /*go_map_list.putExtra("주변스터디룸", mapItemList);*/ //어레이리스트를 intent로 보냈었음.
                go_map_list.putExtra("현재위치",mCurrentLocatiion);
                //go_map_list.putExtra("주변스터디룸", (Serializable) mapItemList);
                startActivity(go_map_list);
            }
        });

    }

    /*  onResume 때, 구글지도api로부터 위치정보 받아오기
      위치정보권한 허가 안했다면 다시 물어보기*/
    @Override
    public void onResume() {

        super.onResume();
        //Log.e(TAG, "onResume");
        //Log.e(TAG, "5");

        /* 구글지도api와 연결이 되어있다면, 위치업데이트요청을 안하고 있다면, 위치정보업데이트하기*/
        if (mGoogleApiClient.isConnected()) {
            //Log.e(TAG, "6");
            if (!mRequestingLocationUpdates) {
                //Log.e(TAG, "7");
                startLocationUpdates();
                //Log.e(TAG, "8");
            }
        }

       /*  디바이스에 위치서비스(gps)를 켰는지 확인*/
        if (askPermissionOnceAgain) {
            //Log.e(TAG, "9");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                askPermissionOnceAgain = false;
                //Log.e(TAG, "10");
                checkPermissions();
                //Log.e(TAG, "11");
            }
        }
    }


    /*  구글지도api로부터 디바이스가 gps가 onoff인지 확인 후, on이면 현재 위치 받아오기*/
    private void startLocationUpdates() {
        //Log.e(TAG, "12");
       /*  디바이스에 gps가 꺼져있는 경우, gps를 킬 수 있는 세팅창으로 갈지 물어보는 다이얼로그를 띄운다*/
        if (!checkLocationServicesStatus()) { //62
            showDialogForLocationServiceSetting(); //90
            //Log.e(TAG, "13");

           /*  디바이스에 gps가 꺼져있는 경우,*/
        } else {
            //Log.e(TAG, "14");
            /* 현재 앱이 디바이스의 위치권한을 쓸 수 없으므로 현재 위치 못받아옴*/
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //Log.e(TAG, "15");
                return;
            }
            //Log.e(TAG, "16");

            //Log.e(TAG, "startLocationUpdates : call FusedLocationApi.requestLocationUpdates");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
            mRequestingLocationUpdates = true;
            //Log.e(TAG, "17");
            mGoogleMap.setMyLocationEnabled(true);
            //Log.e(TAG, "18");

        }

    }


    /*  위치업데이트 그만 받아오기*/
    private void stopLocationUpdates() {
        //Log.e(TAG, "19");
        //Log.e(TAG, "stopLocationUpdates : LocationServices.FusedLocationApi.removeLocationUpdates");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mRequestingLocationUpdates = false;
        //Log.e(TAG, "20");
    }


    /*  맵을 사용할 준비가 되었을 때,*/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Log.e(TAG, "21");
        //Log.e(TAG, "onMapReady");

        mGoogleMap = googleMap;
        //Log.e(TAG, "22");

        /*런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        지도의 초기위치를 서울로 이동 (일단 화면이 보이긴 해야함)*/
        setDefaultLocation();
        //Log.e(TAG, "23");
        //mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(13));
        //Log.e(TAG, "24");
        mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                //Log.e(TAG, "25");
                //Log.e(TAG, "onMyLocationButtonClick : 위치에 따른 카메라 이동 활성화");
                mMoveMapByAPI = true;
                //Log.e(TAG, "26");
                return true;
            }
        });
        //Log.e(TAG, "27");
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                //Log.e(TAG, "28");
                //Log.e(TAG, "onMapClick :");
            }
        });

        mGoogleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {

            @Override
            public void onCameraMoveStarted(int i) {
                //Log.e(TAG, "29");
                if (mMoveMapByUser == true && mRequestingLocationUpdates) {
                    //Log.e(TAG, "30");
                    //Log.e(TAG, "onCameraMove : 위치에 따른 카메라 이동 비활성화");
                    mMoveMapByAPI = false;
                }
                //Log.e(TAG, "31");
                mMoveMapByUser = true;

            }
        });
// Do other setup activities here too, as described elsewhere in this tutorial.
        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents, null);

                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                title.setText(marker.getTitle());

                TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });

        mGoogleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {

            @Override
            public void onCameraMove() {
                //Log.e(TAG, "32");

            }
        });
    }

    /*  현재위치가 바뀔 때, 바뀐위치 가져옴. 지도에 현재 위치 마커생성함. 마커생성된 곳으로 화면이동*/
    @Override
    public void onLocationChanged(Location location) {
        //Log.e(TAG, "33");
        if (showCurLocation) {
            currentPosition
                    = new LatLng(location.getLatitude(), location.getLongitude());

            Log.e(Tag, "onLocationChanged");

            String markerTitle = getCurrentAddress(currentPosition);
            //Log.e(TAG, "34");
            String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                    + " 경도:" + String.valueOf(location.getLongitude());

            /*현재 위치에 마커 생성하고 이동*/
            setCurrentLocation(location, markerTitle, markerSnippet);
            //Log.e(TAG, "35");
            mCurrentLocatiion = location;

            if (markerTitle == null) {
                tv_current_location.setText("현재 위치를 받아올 수 없습니다.");
            } else {
                tv_current_location.setText(markerTitle);
            }

            kakaoMap(mCurrentLocatiion);
            googleMap(mCurrentLocatiion);
            //showPlaceInformation(currentPosition);

            progressBar_map.setVisibility(View.GONE);


           /*   //중복 스터디룸 리스트 제거
            ArrayList resultList = new ArrayList<MapItem>();
            for (int i = 0; i < mapItemList.size(); i++) {
                if (!resultList.contains(mapItemList.get(i))) {
                    resultList.add(mapItemList.get(i));
                }
            }*/

            showCurLocation = false;
        }
    }


    /*  onStart 때 구글지도api와 연결이 안되었다면, 연결시도하기*/
    @Override
    protected void onStart() {
        //Log.e(TAG, "36");
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
            //Log.e(TAG, "37");
        }
        //Log.e(TAG, "38");
        super.onStart();
    }

    /*  onStop때 지도와 연결끊음*/
    @Override
    protected void onStop() {
        //Log.e(TAG, "39");
        // 위치를 계속 받아오고 있는 중이면 위치 그만 받아오기
        if (mRequestingLocationUpdates) {
            //Log.e(TAG, "40");
            //Log.e(TAG, "onStop : call stopLocationUpdates");
            stopLocationUpdates();
        }

        /* 지도api와 연결중이면 끊기*/
        if (mGoogleApiClient.isConnected()) {
            //Log.e(TAG, "41");

            //Log.e(TAG, "onStop : mGoogleApiClient disconnect");
            mGoogleApiClient.disconnect();
        }
        //Log.e(TAG, "42");
        super.onStop();
    }


    /*  디바이스의 위치서비스권한을 앱이 이용할 수 있는지 확인하기*/
    @Override
    public void onConnected(Bundle connectionHint) {
        //Log.e(TAG, "43");
        // 현재 위치 가져오는 요청을 할 때, 앱이 디바이스의 gps를 사용할 수 있는지 확인
        if (!mRequestingLocationUpdates) {
            //Log.e(TAG, "44");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //Log.e(TAG, "45");
                int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);


                // 만약 앱이 디바이스의 위치서비스를 이용할 수 없다면, 권한요청다이얼로그 띄우기
                if (hasFineLocationPermission == PackageManager.PERMISSION_DENIED) {
                    //Log.e(TAG, "46");
                    ActivityCompat.requestPermissions(mActivity,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                    // 앱이 디바이스의 위치서비스를 이용할 수 있음. 현재 위치 불러오기
                } else {
                    //Log.e(TAG, "47");
                    startLocationUpdates();
                    mGoogleMap.setMyLocationEnabled(true);
                }

                // 구글지도api에 현재 위치 가져오는 요청을 안할 때는 현재 위치 가져오라고 요청하기
            } else {
                //Log.e(TAG, "48");
                //Log.e(TAG, "onConnected - 현재 위치 가져오라고 요청하기");
                startLocationUpdates();
                mGoogleMap.setMyLocationEnabled(true);
            }
        }
    }


    /*  연결실패했을 때, 기본 위치로 이동(서울로 이동)*/
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Log.e(TAG, "49");
        //Log.e(TAG, "onConnectionFailed");
        setDefaultLocation();
    }


    /* 연결중지 (지도화면 끌 때)*/
    @Override
    public void onConnectionSuspended(int cause) {
        //Log.e(TAG, "50");
        //Log.e(TAG, "onConnectionSuspended");
        if (cause == CAUSE_NETWORK_LOST) {
            //Log.e(TAG, "51");
            //Log.e(TAG, "onConnectionSuspended(): Google Play services " +
            //"connection lost.  Cause: network lost.");
        } else if (cause == CAUSE_SERVICE_DISCONNECTED) {
            //Log.e(TAG, "52");
            //Log.e(TAG, "onConnectionSuspended():  Google Play services " +
            //"connection lost.  Cause: service disconnected");
        }
        //Log.e(TAG, "53");
    }

    /*gps좌표를 주소로 변환*/
    public String getCurrentAddress(LatLng latlng) {
        //Log.e(TAG, "54");
        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> gCoder;
        //Log.e(TAG, "55");
        try {
            //Log.e(TAG, "56");
            gCoder = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            //Log.e(TAG, "57");
            //Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            //Log.e(TAG, "58");
            //Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }
        //Log.e(TAG, "59");

        if (gCoder == null || gCoder.size() == 0) {
            //Log.e(TAG, "60");
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            //Log.e(TAG, "현재위치1");
            Address address = gCoder.get(0);
//           // Log.e(TAG, "현재위치2");
//            if(address.getLocality()==null || address.getSubLocality()==null || address.getThoroughfare()==null){
//               // Log.e(TAG, "현재위치3");
//                String address_s = "현재 위치를 받아올 수 없습니다";
//                Log.e(Tag, "현재 위치 주소 : "+address_s);
//                tv_current_location.setText(address_s);
//
//            }else{
//               // Log.e(TAG, "현재위치4");
//                String address_s = address.getLocality()+" "+address.getSubLocality()+" "+address.getThoroughfare();
//               // Log.e(Tag, "현재 위치 주소 : "+address_s);
//                tv_current_location.setText(address_s);
//            }
            // tv_current_location.setText("헤이");
            //tv_current_location.setText();
            //Log.e(Tag, "텍스트뷰에 보이는 주소 : "+tv_current_location.getText());
            //  Log.e(TAG, "현재위치5");
            return address.getAddressLine(0);
        }
    }

    // 디바이스의 gps가 켜져있는지 확인
    public boolean checkLocationServicesStatus() {
        //Log.e(TAG, "62");
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    // 현재 위치 설정
    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {
        //Log.e(TAG, "63");
        mMoveMapByUser = false;

        if (currentMarker != null) currentMarker.remove();
        //Log.e(TAG, "64");

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
//        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
        markerOptions.draggable(true);

        //구글맵의 디폴트 현재 위치는 파란색 동그라미로 표시
        //마커를 원하는 이미지로 변경하여 현재 위치 표시하도록 수정 fix - 2017. 11.27
        //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher));

        currentMarker = mGoogleMap.addMarker(markerOptions);


        if (mMoveMapByAPI) {
            //Log.e(TAG, "65");

            //Log.e(TAG, "setCurrentLocation :  mGoogleMap moveCamera "
            // + location.getLatitude() + " " + location.getLongitude());
            // CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 15);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
            mGoogleMap.moveCamera(cameraUpdate);
        }
        // Log.e(TAG, "66");
    }

    // 기본 위치 설정
    public void setDefaultLocation() {
        //Log.e(TAG, "67");
        mMoveMapByUser = false;


        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 여부 확인하세요";


        if (currentMarker != null) {
            //Log.e(TAG, "68");
            currentMarker.remove();
        }
        //Log.e(TAG, "69");
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mGoogleMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mGoogleMap.moveCamera(cameraUpdate);

    }


    //여기부터는 런타임 퍼미션 처리을 위한 메소드들 (앱 실행 중 필요한 권한 있으면 물어봄)
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        //Log.e(TAG, "70");

        boolean fineLocationRationale = ActivityCompat
                .shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);


        // 앱이 디바이스의 위치서비스 권한 요구함
        if (hasFineLocationPermission == PackageManager
                .PERMISSION_DENIED && fineLocationRationale) {
            showDialogForPermission("앱을 실행하기위해서는 디바이스 위치서비스 권한이 필요합니다. 권한을 허용하시겠습니까?");
            //Log.e(TAG, "71");

        } else if (hasFineLocationPermission
                == PackageManager.PERMISSION_DENIED && !fineLocationRationale) {
            //Log.e(TAG, "72");
            showDialogForPermissionSetting("퍼미션 거부 + Don't ask again(다시 묻지 않음) " +
                    "체크 박스를 설정한 경우로 설정에서 퍼미션 허가해야합니다.");
        } else if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {
            //Log.e(TAG, "73");

            //Log.e(TAG, "checkPermissions : 퍼미션 가지고 있음");

            if (mGoogleApiClient.isConnected() == false) {
                //Log.e(TAG, "74");
                //Log.e(TAG, "checkPermissions : 퍼미션 가지고 있음");
                mGoogleApiClient.connect();
            }
            //Log.e(TAG, "75");
        }
        //Log.e(TAG, "76");
    }

    // 앱이 디바이스의 위치권한을 쓸 수 있게 해달라고 유저에게 요청 후
    // 앱에서 보이는 반응
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        //Log.e(TAG, "77");

        if (permsRequestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION && grantResults.length > 0) {
            //Log.e(TAG, "78");
            boolean permissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

            // 유저가 앱이 디바이스의 위치권한(gps)을 쓸 수 있게 허락한 경우
            if (permissionAccepted) {
                //Log.e(TAG, "79");

                if (!mGoogleApiClient.isConnected()) {
                    //Log.e(TAG, "80");
                    //Log.e(TAG, "onRequestPermissionsResult : mGoogleApiClient connect");
                    mGoogleApiClient.connect();
                }
                //Log.e(TAG, "81");

                // 유저가 앱이 디바이스의 위치권한(gps)을 못쓰게 거부한 경우, 권한체크하러가기
            } else {
                //Log.e(TAG, "82");
                checkPermissions();
            }
            //Log.e(TAG, "83");
        }
        //Log.e(TAG, "84");
    }


    // 앱이 디바이스의 위치서비스를 이용할 수 있도록 권한 요구
    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {
        //Log.e(TAG, "85");

        // 예를 누르면
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                // STUDYMATE의 다음 작업을 허용하시겠습니까? 이 기기의 위치에 액세스하기
                //Log.e(TAG, "86");
            }
        });

        // 아니요를 누르면 지도화면이 꺼짐
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
                //Log.e(TAG, "87");
            }
        });
        builder.create().show();
    }

    // 권한 설정으로 가기 전 다이얼로그
    private void showDialogForPermissionSetting(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                //Log.e(TAG, "88");


                askPermissionOnceAgain = true;

                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + mActivity.getPackageName()));
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(myAppSettings);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
                //Log.e(TAG, "89");
            }
        });
        builder.create().show();
    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {
        //Log.e(TAG, "90");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다. gps를 실행해주세요");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //Log.e(TAG, "91");
                Intent callGPSSettingIntent
                        = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                Toast.makeText(getApplicationContext(), "gps가 꺼져있어, 현재 위치를 받아올 수 없습니다", Toast.LENGTH_SHORT);
                finish();
                //Log.e(TAG, "92");
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            //사용자가 디바이스의 GPS를 켰는지 확인
            case GPS_ENABLE_REQUEST_CODE:
                //Log.e(TAG, "93");
                if (checkLocationServicesStatus()) {
                    //Log.e(TAG, "94");
                    if (checkLocationServicesStatus()) {
                        //Log.e(TAG, "95");

                        // GPS 켰음.
                        if (mGoogleApiClient.isConnected() == false) {
                            //Log.e(TAG, "96");
                            //Log.e(TAG, "onActivityResult : mGoogleApiClient connect ");
                            mGoogleApiClient.connect();
                        }
                        return;
                    }
                }

                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Log.e(TAG, "onPause");
        //Log.e(TAG, "97");
    }

////////////////////////////////////////////////////////////////////////////////////////////////
    // 구글 place api로 주변검색할 때 필요한 메소드

    @Override
    public void onPlacesFailure(PlacesException e) {

    }

    @Override
    public void onPlacesStart() {

    }

    @Override
    public void onPlacesSuccess(final List<Place> places) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 구글맵
                for (Place place : places) {
                    LatLng latLng
                            = new LatLng(place.getLatitude()
                            , place.getLongitude());
                    String markerSnippet = getCurrentAddress(latLng);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(place.getName());
                    markerOptions.snippet("(구글)" + markerSnippet);
                    Marker item = mGoogleMap.addMarker(markerOptions);
                    previous_marker.add(item);
                }

//                // 카카오맵
//                for (){
//                    MapPOIItem marker = new MapPOIItem();
//                    marker.setItemName();
//                    marker.setTag(0);
//                    marker.setMapPoint(MARKER_POINT);
//                    marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
//                    marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
//                    //mapView.addPOIItem(marker);
//                    Marker item = mGoogleMap.addMarker(marker);
//                    previous_marker.add(item);
//                }

                //중복 마커 제거
                HashSet<Marker> hashSet = new HashSet<Marker>();
                hashSet.addAll(previous_marker);
                previous_marker.clear();
                previous_marker.addAll(hashSet);
            }
        });
    }

    @Override
    public void onPlacesFinished() {

    }

    public void showPlaceInformation(LatLng location) {
        mGoogleMap.clear();//지도 클리어

        if (previous_marker != null)
            previous_marker.clear();//지역정보 마커 클리어

        new NRPlaces.Builder()
                .listener(this)
                .key("AIzaSyBtS9p_spGHvZ7-cb7sOy8KAe3__8mH0NQ")
                .latlng(location.latitude, location.longitude)//현재 위치
                .radius(3000) //2000 미터 내에서 검색
                //.type(PlaceType.RESTAURANT) //음식점
                .keyword("스터디룸")
                .language("ko", "KR")
                .build()
                .execute();
    }

    public void kakaoMap(Location location) {
        //////////////////////////////////////////////////////////////////////////////////////////////////////
        // 내 서버를 통해 카카오맵 api로 부터 주변 스터디룸 정보 받아오기

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Log.e(Tag, "response : "+response);

                try {
                    //Log.e(Tag,"response1");
                    JSONObject jsonObject = new JSONObject(response);
                    // Log.e(Tag,"response2");
                    JSONArray jsonResponse = jsonObject.getJSONArray("documents");
                    // Log.e(Tag,"response3");
//                    Log.e(Tag, response);
                    double study_latitude; // 위도
                    double study_longitude; // 경도
                    String place_name; // 이름
                    String address_name; // 주소
                    String phone; // 전화번호
                    String place_url; // url
                    double distance; // 중심좌표까지의 거리
                    String place_id;

                    //Log.e(Tag, "response4");
                    // 만약 success가 되었다면 즉 주변 스터디룸 정보들을 받아올 수 있다면,
                    // 받아온 주변 스터디룸 정보들을 마커에 세팅한다
                    // success : 해당 과정이 정상적으로 수행 되었는지 response 값
                    // boolean success = jsonObject.getBoolean("success");
                    //Log.e(Tag, "response4-1");
                    //if (success) {
                    for (int i = 0; i < response.length(); i++) {
                        // Log.e(Tag,"response5");
                        JSONObject jObject = jsonResponse.getJSONObject(i);
                        place_name = jObject.optString("place_name");
                        address_name = jObject.optString("address_name");
                        phone = jObject.optString("phone");
                        //place_id = jObject.optString("id");
                        //Log.e(Tag,"id"+place_id);
                        study_longitude = Double.valueOf(jObject.optString("x"));
                        study_latitude = Double.valueOf(jObject.optString("y"));
                        place_url = jObject.optString("place_url");
                        distance = Double.valueOf(jObject.optString("distance"));
                        //Log.e(Tag, "경도(x):" + study_longitude + " 위도(y):" + study_latitude + " 이름:" + place_name + " 주소:" + address_name + " 전화번호:" + phone);

                        //지도에 마커표시
                        MarkerOptions markerOptions = new MarkerOptions();
//                        Log.e(Tag,"331");
                        markerOptions.position(new LatLng(study_latitude, study_longitude))
                                .title(place_name)
                                .snippet("(카카오)" + address_name);

//                        Log.e(Tag,"4441");
                        Marker item = mGoogleMap.addMarker(markerOptions);
//                        Log.e(Tag,"3746");
                        previous_marker.add(item);

//                        Log.e(Tag,"344");
/*                            *//*카카오맵 주변 스터디룸 리스트*//*
                        MapItem mapItem = new MapItem("", place_name, place_url, address_name, "", phone, study_longitude, study_latitude, distance, "", "", "");

//                        Log.e(Tag,"35551");
                        mapItemList.add(mapItem);*/
                        //Log.e(Tag,"스터디룸리스트 넣어짐");
                        //Log.e(Tag,"어레이리스트내용 : "+mapItemList);
                    }
                    // }else { // 주변 스터디룸 정보들을 받아올 수 없다면,
                    Log.e(Tag, "카카오 : 주변스터디룸 정보 받아올 수 없음!");
                    // }
                } catch (Exception e) { // 오류가 발생했을 경우
                    e.printStackTrace();
                    Log.e(Tag, "카카오 오류");
                }
            }
        };
        String keyword = "스터디룸";
        int radius = 2000;
        String x = String.valueOf(location.getLongitude());
        String y = String.valueOf(location.getLatitude());
        // 현재위치의 경도, 위도, 검색어, 반경거리를 내 서버로 보내기
        Log.e(Tag, "+11");
        KaKaoMapRequest kaKaoMapRequest = new KaKaoMapRequest(x, y, keyword, radius, responseListener);
        Log.e(Tag, "+12");
        // 요청을 실질적으로 보낼 수 있도록 큐를 만든다
        Log.e(Tag, "+13");
        RequestQueue queue = Volley.newRequestQueue(this);
        // 큐에 사용가능한 이메일인지 검사하는 큐를 등록한다
        Log.e(Tag, "+14");
        queue.add(kaKaoMapRequest);
        Log.e(Tag, "+15");
    }

    public void googleMap(Location location) {
        //////////////////////////////////////////////////////////////////////////////////////////////////////
        // 내 서버를 통해 카카오맵 api로 부터 주변 스터디룸 정보 받아오기

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //logLargeString("google_response : "+response);
/*주석시작*/
                try {
                    Log.e(Tag, "onResponse : 1");
                    //Log.e(Tag,"response1");
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObject1 = jsonObject.getJSONObject("response");
                    //logLargeString("jsonObject : " + jsonObject);

                    /*JSONArray jsonResponse = jsonObject1.getJSONArray("room_photo");*/
                    //Log.e(Tag, "jsonResponse.length() : "+jsonResponse.length());
                    //logLargeString("jsonResponse : "+jsonResponse);
//                    JSONObject jsonObject1 = new JSONObject(response);
//                    logLargeString("jsonObject1 : "+jsonObject);
//                    JSONArray jsonResponse_photo = jsonObject1.getJSONArray("room_photo");
//                    //Log.e(Tag,"results결과 : "+(JSONArray)jsonResponse);
                    /*Log.e(Tag,"onResponse : 2");*/

//                    Log.e(Tag, "여기도 photo : "+photo);

////                    Log.e(Tag, response);
                    String study_latitude; // 위도
                    String study_longitude; // 경도
                    String place_name; // 이름
                    String address_name; // 주소
                    final String phone; // 전화번호
                    String place_url; // url
                    double distance; // 중심좌표까지의 거리
                    String place_id;

                    JSONArray jsonResponse_info = jsonObject1.getJSONArray("info");
                    /*logLargeString("jsonResponse_info : "+jsonResponse_info);*/
                    // 만약 success가 되었다면 즉 주변 스터디룸 정보들을 받아올 수 있다면,
                    // 받아온 주변 스터디룸 정보들을 마커에 세팅한다
                    // success : 해당 과정이 정상적으로 수행 되었는지 response 값
                    // boolean success = jsonObject.getBoolean("success");
                    //if (success) {
                    Log.e(Tag, "jsonResponse_info.length : " + jsonResponse_info.length());
                    Log.e(Tag, "구글1");
//                    /*장소 기본 정보 가져오기*/
                    for (int i = 0; i < jsonResponse_info.length(); i++) {
                        // Log.e(Tag,"response5");
                        JSONObject Object = jsonResponse_info.getJSONObject(i);
                        //Log.e(Tag, "jsonResponse_info: 1");
                        JSONObject geometry = Object.getJSONObject("geometry");
                        //Log.e(Tag, "geometry: " + geometry);
                        JSONObject location = geometry.getJSONObject("location");
                        //Log.e(Tag, "location: " + location);
                        study_latitude = location.optString("lat");
                        study_longitude = location.optString("lng");
                        //Log.e(Tag,"study_latitude: "+study_latitude);
                        //Log.e(Tag,"study_longitude: "+study_longitude);

                        place_name = Object.optString("name");
                        address_name = Object.optString("vicinity");
                        //Log.e(Tag, "이름:" + place_name);
                        //Log.e(Tag, "주소:" + address_name);


//                        place_url =
//                        phone = jObject.optString("phone");
//                        //place_id = jObject.optString("id");
//                        //Log.e(Tag,"id"+place_id);
//                        study_longitude = Double.valueOf(jObject.optString("x"));
//                        study_latitude = Double.valueOf(jObject.optString("y"));
//                        place_url = jObject.optString("place_url");
//                        distance = Double.valueOf(jObject.optString("distance"));
//                        //Log.e(Tag, "경도(x):" + study_longitude + " 위도(y):" + study_latitude + " 이름:" + place_name + " 주소:" + address_name + " 전화번호:" + phone);
//
//                        //지도에 마커표시
                        MarkerOptions markerOptions = new MarkerOptions();
//                        Log.e(Tag,"331");
                        double study_latitude_d = Double.valueOf(study_latitude);
                        double study_longitude_d = Double.valueOf(study_longitude);
                        markerOptions.position(new LatLng(study_latitude_d, study_longitude_d))
                                .title(place_name)
                                .snippet("(구글)" + address_name);

//                        Log.e(Tag,"4441");
                        Marker item = mGoogleMap.addMarker(markerOptions);
//                        Log.e(Tag,"3746");
                        previous_marker.add(item);


                        /*// 두 위치 사이 거리 구하기
                        Location location_cur = new Location("pointA");
                        location_cur.setLatitude(mCurrentLocatiion.getLatitude());
                        location_cur.setLongitude(mCurrentLocatiion.getLongitude());

                        Location location_room = new Location("pointB");
                        location_room.setLatitude(study_latitude_d);
                        location_room.setLongitude(study_longitude_d);

                        distance = location_cur.distanceTo(location_room);
                        //Log.e(Tag,"두 지점사이의 거리 : "+distance);

////                        Log.e(Tag,"344");
                            *//*구글맵 주변 스터디룸 리스트*//*
                        MapItem mapItem = new MapItem("", place_name, "", address_name, "", "", study_longitude_d, study_latitude_d, distance, "", "", "");

//                        Log.e(Tag,"35551");
                        mapItemList.add(mapItem);*/
                        //Log.e(Tag,"스터디룸리스트 넣어짐");
                        //Log.e(Tag,"어레이리스트내용 : "+mapItemList);
                    }
                    Log.e(Tag, "구글2");
                    String name;
                    String photo = "";

                    /*JSONArray jsonResponse = jsonObject1.getJSONArray("room_photo");
                    Log.e(Tag, "구글3");
                    // 사진url 가져오기
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        Log.e(Tag, "구글4-1");
                        JSONObject Object = jsonResponse.getJSONObject(i);
                        Log.e(Tag, "구글4-2");
                        name = Object.optString("name");
                        photo = Object.optString("photo");
//                        Log.e(Tag, "name : " + name);
                        //Log.e(Tag, "photo : " + photo);
                        Log.e(Tag, "구글4-3");
                        //Log.e(Tag, "스터디리스트사이즈 : " + mapItemList.size());

                        for (int j = 0; j < mapItemList.size(); j++) {
                            if (mapItemList.get(j).getPlace_name().equals(name)) {
                                mapItemList.get(j).setPlace_image(photo);
//                                Log.e(Tag,"이름가져오기 : "+name);
                                //Log.e(Tag, j + "번 사진가져오기 : " + photo);
                                //Log.e(Tag, "사진_ : " + mapItemList.get(j).getPlace_image());
                                break;
                            } else { //스터디룸 사진이 없다면, 기본 이미지가 나오게 하기
//                                mapItemList.get(j).setPlace_image("");
                                //Log.e(Tag, j + "번 사진없음");
                            }
                        }
                    }
                    Log.e(Tag, "구글5");
                    studylist_btn.setVisibility(View.VISIBLE);

                    for (int j = 0; j < mapItemList.size(); j++) {
                        Log.e(Tag,j+"번 이미지 : "+mapItemList.get(j).getPlace_image());
                    }*/
//                    }
//                     }else { // 주변 스터디룸 정보들을 받아올 수 없다면,
//                    Log.e(Tag,"카카오 : 주변스터디룸 정보 받아올 수 없음!");
//                     }

                } catch (Exception e) { // 오류가 발생했을 경우
                    e.printStackTrace();
                    Log.e(Tag, "구글에서 스터디룸 정보가져오기 실패");
                }
                /*주석끝*/
            }
        };
        String keyword = "스터디룸";
        int radius = 5000;
        String x = String.valueOf(location.getLongitude());
        String y = String.valueOf(location.getLatitude());
        // 현재위치의 경도, 위도, 검색어, 반경거리를 내 서버로 보내기
        // Log.e(Tag, "googleMap_search_1");
        GoogleMapRequest googleMapRequest = new GoogleMapRequest(x, y, keyword, radius, responseListener);
        // 요청을 실질적으로 보낼 수 있도록 큐를 만든다
        RequestQueue queue = Volley.newRequestQueue(this);
        // 큐에 사용가능한 이메일인지 검사하는 큐를 등록한다
        queue.add(googleMapRequest);
        // Log.e(Tag, "googleMap_search_2");

    }

    //    // Request photos and metadata for the specified place.
//    private void getPhotos() {
//        final String placeId = "ChIJa147K9HX3IAR-lwiGIQv9i4";
//        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
//        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
//            @Override
//            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
//                // Get the list of photos.
//                PlacePhotoMetadataResponse photos = task.getResult();
//                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
//                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
//                // Get the first photo in the list.
//                PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
//                // Get the attribution text.
//                CharSequence attribution = photoMetadata.getAttributions();
//                // Get a full-size bitmap for the photo.
//                Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
//                photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
//                    @Override
//                    public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
//                        PlacePhotoResponse photo = task.getResult();
//                        Bitmap bitmap = photo.getBitmap();
//                    }
//                });
//            }
//        });s
//    }
    public void logLargeString(String str) {
        if (str.length() > 3000) {
            Log.e("e", str.substring(0, 3000));
            logLargeString(str.substring(3000));
        } else {
            Log.e("e", str); // continuation
        }
    }

//    public double distanceTo(Location location){
//        synchronized (mResults){
//            if(mLatitude ! = )
//        }
//    }

}
