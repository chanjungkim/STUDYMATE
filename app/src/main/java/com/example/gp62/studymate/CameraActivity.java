package com.example.gp62.studymate;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.SensorManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class CameraActivity extends AppCompatActivity
        implements CameraBridgeViewBase.CvCameraViewListener2{

    private static final String TAG = "CameraActivity";
    private CameraBridgeViewBase mOpenCvCameraView;
    private Mat matInput;
    private Mat matResult;
    Button button;
    public OrientationEventListener morientEventListener;

    //public native void ConvertRGBtoGray(long matAddrInput, long matAddrResult);
    public native long loadCascade(String cascadeFileName);

    public native int detect(long cascadeClassifier_face,

                             long cascadeClassifier_eye, long matAddrInput, long matAddrResult);

    public long cascadeClassifier_face = 0;
    public long cascadeClassifier_eye = 0;

    private final Semaphore writeLock = new Semaphore(1);

    public void getWriteLock() throws InterruptedException {
        writeLock.acquire();
    }

    public void releaseWriteLock() {
        writeLock.release();
    }

    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("native-lib");
    }

    private void copyFile(String filename) {
        String baseDir = Environment.getExternalStorageDirectory().getPath();
        String pathDir = baseDir + File.separator + filename;

        AssetManager assetManager = this.getAssets();

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            Log.e(TAG, "copyFile :: 다음 경로로 파일복사 " + pathDir);
            inputStream = assetManager.open(filename);
            outputStream = new FileOutputStream(pathDir);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            inputStream.close();
            inputStream = null;
            outputStream.flush();
            outputStream.close();
            outputStream = null;
        } catch (Exception e) {
            Log.e(TAG, "copyFile :: 파일 복사 중 예외 발생 " + e.toString());
        }

    }

    private void read_cascade_file() {
        copyFile("haarcascade_frontalface_alt.xml");
        copyFile("haarcascade_eye_tree_eyeglasses.xml");

        Log.e(TAG, "read_cascade_file:");

        cascadeClassifier_face = loadCascade("haarcascade_frontalface_alt.xml");
        Log.e(TAG, "read_cascade_file:");

        cascadeClassifier_eye = loadCascade("haarcascade_eye_tree_eyeglasses.xml");
    }


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_camera);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //퍼미션 상태 확인
            if (!hasPermissions(PERMISSIONS)) {

                //퍼미션 허가 안되어있다면 사용자에게 요청
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            } else read_cascade_file(); //추가
        } else read_cascade_file(); //추가

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        // mOpenCvCameraView.setCameraIndex(0); // front-camera(1),  back-camera(0)
        mOpenCvCameraView.setCameraIndex(1); // front-camera(1),  back-camera(0)


        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);

        button = (Button) findViewById(R.id.button);

        if(morientEventListener == null){
            morientEventListener = new OrientationEventListener(getApplicationContext(),
                    SensorManager.SENSOR_DELAY_NORMAL) {

                @Override
                public void onOrientationChanged(int arg0) {

                    if(arg0 >= 315 || arg0 < 45) {
                        Log.e(TAG,"세로화면");
                        //exifOrientationToDegrees(90);
                    }

                    // 90˚
                    else if(arg0 >= 45 && arg0 < 135) {
                        Log.e(TAG,"가로인데 얼굴검출 잘 안되는 가로화면");
                        //exifOrientationToDegrees(180);
                    }

                    // 180˚
                    else if(arg0 >= 135 && arg0 < 225) {
                        Log.e(TAG,"거꾸로 세로화면");
                        //                               exifOrientationToDegrees(270);
                    }

                    // 270˚ (landscape)
                    else if(arg0 >= 225 && arg0 < 315)
                    {
                        Log.e(TAG,"얼굴검출 잘 되는 가로화면 : "+mOpenCvCameraView.getRotation());
                        //                             int orientation = 0;
                        //                           exifOrientationToDegrees(orientation);
                    }
                }
            };
        }

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                try {

                    //인식 오류 시, Toast 메시지 출력
                    if (!morientEventListener.canDetectOrientation()) {
                        Log.e(TAG,"Can't DetectOrientation");
                    }

                    getWriteLock();

                    File path = new File(Environment.getExternalStorageDirectory() + "/Images/");
                    path.mkdirs();
                    Random rnd = new Random();
                    int p = rnd.nextInt(500);
                    File file = new File(path, p+".jpg");

                    String filename = file.toString();
                    Log.e(TAG, "폰에 저장하려는 파일경로+파일명 : "+filename);

                    Imgproc.cvtColor(matInput, matInput, Imgproc.COLOR_BGR2RGB, 4);
                    boolean ret = Imgcodecs.imwrite(filename, matInput);
                    if (ret) Log.e(TAG, "SUCESS : "+filename);
                    else Log.e(TAG, "FAIL");

                    Bitmap image = BitmapFactory.decodeFile(filename);

//                    // 이미지를 상황에 맞게 회전시킨다
//                    ExifInterface exif = new ExifInterface(filename);
//                    int exifOrientation = exif.getAttributeInt(
//                            ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//                    Log.e(TAG, "방향 : "+exifOrientation);
//                    int exifDegree = exifOrientationToDegrees(exifOrientation);
//                    image = rotate(image, exifDegree);
//
//                    saveBitmaptoJpeg(image,filename);

                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    //Uri mImageCaptureUri = Uri.fromFile(file);
                    mediaScanIntent.setData(Uri.fromFile(file));
                    sendBroadcast(mediaScanIntent);


                    Intent returnIntent = new Intent();
//                    //String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
//                    //Uri uriTarget = getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,new ContentValues());
//                    //Uri mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
//                    returnIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriTarget);
//                    Log.e(TAG, "보낼 uri : " + uriTarget);
////                    returnIntent.putExtra("gogo", mImageCaptureUri);
//                    //returnIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
//                    //Log.e(TAG,"보낼 MediaStore.EXTRA_OUTPUT : "+MediaStore.EXTRA_OUTPUT);
                    returnIntent.putExtra("filename",filename);
                    setResult(RESULT_OK, returnIntent);
                    finish();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                releaseWriteLock();

            }
        });
    }


    /**
     * Image SDCard Save (input Bitmap -> saved file JPEG)
     * Writer intruder(Kwangseob Kim)
     * @param bitmap : input bitmap file
     * @param filename : folder name + file name
     */

    public static void saveBitmaptoJpeg(Bitmap bitmap, String filename){
        //String ex_storage =Environment.getExternalStorageDirectory().getAbsolutePath();
        // Get Absolute Path in External Sdcard

        File file_path;
        try{
            file_path = new File(filename);
            if(!file_path.isDirectory()){
                file_path.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(filename);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();

        }catch(FileNotFoundException exception){
            Log.e("FileNotFoundException", exception.getMessage());
        }catch(IOException exception){
            Log.e("IOException", exception.getMessage());
        }
    }


    /**
     * EXIF정보를 회전각도로 변환하는 메서드
     *
     * @param exifOrientation EXIF 회전각
     * @return 실제 각도
     */
    public int exifOrientationToDegrees(int exifOrientation)
    {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
        {
            return 90;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180)
        {
            return 180;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)
        {
            return 270;
        }
        return 0;
    }

    /**
     * 이미지를 회전시킵니다.
     *
     * @param bitmap 비트맵 이미지
     * @param degrees 회전 각도
     * @return 회전된 이미지
     */
    public Bitmap rotate(Bitmap bitmap, int degrees)
    {
        if(degrees != 0 && bitmap != null)
        {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);

            try
            {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if(bitmap != converted)
                {
                    bitmap.recycle();
                    bitmap = converted;
                }
            }
            catch(OutOfMemoryError ex)
            {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }
        }
        return bitmap;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        morientEventListener.disable();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "onResume :: Internal OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
        } else {
            Log.e(TAG, "onResum :: OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        morientEventListener.enable();
    }

    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();

    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        try {
            getWriteLock();

            matInput = inputFrame.rgba();


            //if ( matResult != null ) matResult.release(); fix 2018. 8. 18

            if (matResult == null)

                matResult = new Mat(matInput.rows(), matInput.cols(), matInput.type());

            //ConvertRGBtoGray(matInput.getNativeObjAddr(), matResult.getNativeObjAddr());
            Core.flip(matInput, matInput, 1);

            int ret = detect(cascadeClassifier_face, cascadeClassifier_eye, matInput.getNativeObjAddr(),
                    matResult.getNativeObjAddr());

            if (ret != 0) {
                Log.e(TAG, "face " + ret + " found");
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        button.setBackgroundColor(Color.YELLOW);
                    }
                });
            } else {
                button.setBackgroundColor(Color.WHITE);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        releaseWriteLock();
        return matResult;
    }


    //여기서부턴 퍼미션 관련 메소드
    static final int PERMISSIONS_REQUEST_CODE = 1000;
    //String[] PERMISSIONS  = {"android.permission.CAMERA"};
    String[] PERMISSIONS = {"android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    private boolean hasPermissions(String[] permissions) {
        int result;

        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (String perms : permissions) {

            result = ContextCompat.checkSelfPermission(this, perms);

            if (result == PackageManager.PERMISSION_DENIED) {
                //허가 안된 퍼미션 발견
                return false;
            }
        }

        //모든 퍼미션이 허가되었음
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraPermissionAccepted = grantResults[0]
                            == PackageManager.PERMISSION_GRANTED;

//                    if (!cameraPermissionAccepted)
//                        showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");

                    boolean writePermissionAccepted = grantResults[1]
                            == PackageManager.PERMISSION_GRANTED;

                    if (!cameraPermissionAccepted || !writePermissionAccepted) {
                        showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                        return;
                    } else {
                        read_cascade_file();
                    }
                }
                break;
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
    }

}
