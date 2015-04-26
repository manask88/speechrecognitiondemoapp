package com.example.manuel.cameraexample;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements SpeechRecognizerManager.OnResultListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    private Camera mCamera;
    private CameraPreview mCameraPreview;
    private FrameLayout mPreviewFrameLayout;
    private Button mCaptureButton;
    private Camera.PictureCallback mPictureCallback;
    private SpeechRecognizerManager mSpeechRecognizerManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSpeechRecognizerManager =new SpeechRecognizerManager(this);
        mSpeechRecognizerManager.setOnResultListner(this);

        mPreviewFrameLayout = (FrameLayout) findViewById(R.id.camera_preview);

        mPictureCallback= new PictureCallBack();

        // Add a listener to the Capture button
        mCaptureButton = (Button) findViewById(R.id.button_capture);
        mCaptureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera

                        if (mCamera != null) {
                            mCamera.takePicture(null, null, mPictureCallback);

                        }

                    }
                }
        );

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Create an instance of Camera
        mCamera = getCameraInstance();

        if (mCamera !=null) {
            mCameraPreview = new CameraPreview(MainActivity.this, mCamera);
            mPreviewFrameLayout.addView(mCameraPreview);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        stopPreviewAndFreeCamera();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            Log.e(TAG,e.getMessage());
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    /**
     * When this function returns, mCamera will be null.
     */
    private void stopPreviewAndFreeCamera() {

        if (mCamera != null) {
            // Call stopPreview() to stop updating the mPreviewFrameLayout surface.
            mCamera.stopPreview();

            // Important: Call release() to release the camera for use by other
            // applications. Applications should release the camera immediately
            // during onPause() and re-open() it during onResume()).
            mCamera.release();
            mCamera = null;
            mPreviewFrameLayout.removeAllViews();
            mCameraPreview =null;
        }


    }

    private class PictureCallBack implements Camera.PictureCallback {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.w(TAG, "onPictureTaken.");
            File pictureFile = CameraUtil.getOutputMediaFile(CameraUtil.MEDIA_TYPE_IMAGE);
            if (pictureFile == null){
                Log.d(TAG, "Error creating media file, check storage permissions: "
                );
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }



            camera.startPreview();
        }
    }

    @Override
    public void OnResult(ArrayList<String> commands) {

        for(String command:commands)
        {
            if (command.equals("take picture")){
                Toast.makeText(this,"You said: take picture", Toast.LENGTH_SHORT).show();
                mCamera.takePicture(null, null, mPictureCallback);
                return;
            }

        }
    }



}
