package com.example.b12tdi.l92;

import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.UserHandle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


/** Takes a single photo on service start. */
public class PhotoTakingService extends Service {

    String LOG_TAG = "myLogs";





    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG,"PhotoTakingService onCreate");
        showMessage("PhotoTakingService onCreate");
        takePhoto(this);
        Log.d(LOG_TAG,"PhotoTakingService after takePhoto");

    }

    @SuppressWarnings("deprecation")
    public /*static*/ void takePhoto(final Context context) {
        final SurfaceView preview = new SurfaceView(context);

        showMessage("takePhoto starts");

        SurfaceHolder holder = preview.getHolder();
        // deprecated setting, but required on Android versions prior to 3.0
        showMessage("after SurfaceHolder holder");
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        showMessage("after holder.setType");
        holder.addCallback(new SurfaceHolder.Callback() {
            //@Override
            //The preview must happen at or after this point or takePicture fails
            public void surfaceCreated(SurfaceHolder holder) {
                showMessage("Surface created");

                Camera camera = null;

                try {
                    camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                    showMessage("Opened camera");

                    try {
                        camera.setPreviewDisplay(holder);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Camera.Parameters params= camera.getParameters();

                    params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                    params.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
                    params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
                    params.setExposureCompensation(0);
                    params.setPictureFormat(ImageFormat.JPEG);
                    params.setJpegQuality(100);
                    params.setRotation(90);

                    params.setJpegQuality(100);

                    params.setExposureCompensation(params.getMaxExposureCompensation());

                    if(params.isAutoExposureLockSupported()) {
                        params.setAutoExposureLock(false);
                    }

                    camera.setParameters(params);

                    camera.startPreview();
                    showMessage("Started preview");
                    Thread.sleep(500);
                    camera.takePicture(null, null, new Camera.PictureCallback() {

                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            showMessage("Took picture");
                            File file=new File(Environment.getExternalStorageDirectory()+"/dirr");
                            if (data != null) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                                if(bitmap!=null){


                                    if(!file.isDirectory()){
                                        file.mkdir();
                                    }

                                    file=new File(Environment.getExternalStorageDirectory()+"/dirr",System.currentTimeMillis()+".jpg");
                                    Log.d("myLogs","file " + file.toString());

                                    try
                                    {
                                        FileOutputStream fileOutputStream=new FileOutputStream(file);
                                        bitmap.compress(Bitmap.CompressFormat.JPEG,100, fileOutputStream);

                                        fileOutputStream.flush();
                                        fileOutputStream.close();
                                    }
                                    catch(IOException e){
                                        e.printStackTrace();
                                    }
                                    catch(Exception exception)
                                    {
                                        exception.printStackTrace();
                                    }

                                }
                            }

                            camera.release();

                            try {


                                /*GMailSender sender = new GMailSender("diterentev@gmail.com", "Dimon1982terr#");
                                Log.d("myLogs", "Before sending mail");
                                sender.sendMail("This is Subject",
                                        "This is Body",
                                        "diterentev@gmail.com",
                                        "terdm@yandex.ru");*/
                                startService(new Intent( getApplicationContext(), SendMailIntentService.class).putExtra("file",file.toString()));

                                Intent i = new Intent();
                                i.setAction("MotionDetectorFrontMail");
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                Log.d("myLogs", "");

                                Log.d("myLogs", "After sending mail");
                            } catch (Exception e) {
                                Log.d("myLogs", e.toString());
                            }
/*
                            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                            emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            emailIntent.setType("application/image");
                            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"terdm@yandex.ru"});
                            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Test Subject");
                            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "From My App");
                            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.toString()));

                            //emailIntent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

                            //startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                            try {
                                Log.d("myLogs","Before startActivity");
                                //getApplicationContext().startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                                getApplicationContext().startActivity(emailIntent);
                            } catch (Exception ex) {
                                Log.d("myLogs","error " + ex.toString());
                            }
                            Log.d("myLogs","After startActivity");
*/

                        }
                    });
                } catch (Exception e) {
                    if (camera != null)
                        camera.release();
                    throw new RuntimeException(e);
                }
            }

            //@Override
            public void surfaceDestroyed(SurfaceHolder holder) {}
            //@Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
        }

        );
        showMessage("after holder.addCallback");
        WindowManager wm = (WindowManager)context
                .getSystemService(Context.WINDOW_SERVICE);
        showMessage("after WindowManager wm");
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                1, 1, //Must be at least 1x1
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                0,
                //Don't know if this is a safe default
                PixelFormat.UNKNOWN);
        showMessage("after WindowManager.LayoutParams params");
        //Don't set the preview visibility to GONE or INVISIBLE
        wm.addView(preview, params);
        showMessage("after wm.addView");
    }

    private static void showMessage(String message) {
        Log.d("myLogs", message);
    }

    @Override public IBinder onBind(Intent intent) { return null; }
}