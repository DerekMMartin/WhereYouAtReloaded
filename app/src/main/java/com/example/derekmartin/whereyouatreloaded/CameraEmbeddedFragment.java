package com.example.derekmartin.whereyouatreloaded;

import android.Manifest;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;


public class CameraEmbeddedFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private static final int CAMERA_REQUEST_CODE = 200;
    private View MyView;
    private CameraManager cameraManager;
    private int cameraFacing;
    private TextureView.SurfaceTextureListener surfaceTextureListener;
    private Size previewSize;
    private String cameraId;
    private CameraDevice.StateCallback stateCallback;
    private Handler backgroundHandler;
    private HandlerThread backgroundThread;
    private CameraDevice cameraDevice;
    private CaptureRequest.Builder captureRequestBuilder;
    private CaptureRequest captureRequest;
    private CameraCaptureSession cameraCaptureSession;
    private File galleryFolder;
    private android.support.v4.app.FragmentManager fManager;


    public CameraEmbeddedFragment() {
        // Required empty public constructor
    }

    public void onTakePhotoButtonClicked() {
        lock();
        FileOutputStream outputPhoto = null;
        Bitmap photoBitmap=null;
        try {
            outputPhoto = new FileOutputStream(createImageFile(galleryFolder));
            photoBitmap=((TextureView)(MyView.findViewById(R.id.texture_view))).getBitmap();
            photoBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputPhoto);

            MyView.findViewById(R.id.capture_photo_button).setVisibility(View.INVISIBLE);
            MyView.findViewById(R.id.send_photo_button).setVisibility(View.VISIBLE);
            MyView.findViewById(R.id.cancel_photo_button).setVisibility(View.VISIBLE);

            ImageView preview=(ImageView)MyView.findViewById(R.id.picture_preview);
            preview.setVisibility(View.VISIBLE);
            preview.setImageBitmap(photoBitmap);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            unlock();
            try {
                if (outputPhoto != null) {
                    outputPhoto.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void onCancelPhotoButtonClicked(){
        MyView.findViewById(R.id.capture_photo_button).setVisibility(View.VISIBLE);
        MyView.findViewById(R.id.send_photo_button).setVisibility(View.INVISIBLE);
        MyView.findViewById(R.id.cancel_photo_button).setVisibility(View.INVISIBLE);
        MyView.findViewById(R.id.picture_preview).setVisibility(View.INVISIBLE);
    }
    public void onSendButtonClicked(){
        fManager=getActivity().getSupportFragmentManager();
        FragmentTransaction fTrans=fManager.beginTransaction();

        fTrans.replace(R.id.content_frame,new SendingFragment());
        fTrans.addToBackStack(null);
        fTrans.commit();
    }
    private void createImageGallery() {
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        galleryFolder = new File(storageDirectory, "Where You At Reloaded");
        if (!galleryFolder.exists()) {
            boolean wasCreated = galleryFolder.mkdirs();
            if (!wasCreated) {
                Log.e("CapturedImages", "Failed to create directory");
            }
        }
    }
    private File createImageFile(File galleryFolder) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "image_" + timeStamp + "_";
        return File.createTempFile(imageFileName, ".jpg", galleryFolder);
//        return File.createTempFile("image",".png",galleryFolder);
    }
    private void createPreviewSession() {
        try {
            SurfaceTexture surfaceTexture = ((TextureView)(MyView.findViewById(R.id.texture_view))).getSurfaceTexture();
            surfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            Surface previewSurface = new Surface(surfaceTexture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(previewSurface);

            cameraDevice.createCaptureSession(Collections.singletonList(previewSurface),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                            if (cameraDevice == null) {
                                return;
                            }

                            try {
                                captureRequest = captureRequestBuilder.build();
                                CameraEmbeddedFragment.this.cameraCaptureSession = cameraCaptureSession;
                                CameraEmbeddedFragment.this.cameraCaptureSession.setRepeatingRequest(captureRequest,
                                        null, backgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {

                        }
                    }, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private void openBackgroundThread() {
        backgroundThread = new HandlerThread("camera_background_thread");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }
    private void lock() {
        try {
            cameraCaptureSession.capture(captureRequestBuilder.build(),
                    null, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private void unlock() {
        try {
            cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(),
                    null, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private void setUpCamera() {
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics =
                        cameraManager.getCameraCharacteristics(cameraId);
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) ==
                        cameraFacing) {
                    StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(
                            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    previewSize = streamConfigurationMap.getOutputSizes(SurfaceTexture.class)[1];
                    this.cameraId = cameraId;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private void openCamera() {
        try {
            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                cameraManager.openCamera(cameraId, stateCallback, backgroundHandler);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        lockScreenOrientation();

        MyView = inflater.inflate(R.layout.fragment_camera_embedded, container, false);
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_REQUEST_CODE);
        createImageGallery();

        cameraManager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
        cameraFacing = CameraCharacteristics.LENS_FACING_BACK;
        surfaceTextureListener = new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                setUpCamera();
                openCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

            }
        };
        stateCallback = new CameraDevice.StateCallback() {
            @Override
            public void onOpened(CameraDevice cameraDevice) {
                CameraEmbeddedFragment.this.cameraDevice = cameraDevice;
                createPreviewSession();
            }

            @Override
            public void onDisconnected(CameraDevice cameraDevice) {
                cameraDevice.close();
                CameraEmbeddedFragment.this.cameraDevice = null;
            }

            @Override
            public void onError(CameraDevice cameraDevice, int error) {
                cameraDevice.close();
                CameraEmbeddedFragment.this.cameraDevice = null;
            }
        };
        MyView.findViewById(R.id.capture_photo_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTakePhotoButtonClicked();
            }
        });
        MyView.findViewById(R.id.cancel_photo_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelPhotoButtonClicked();
            }
        });
        MyView.findViewById(R.id.send_photo_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendButtonClicked();
            }
        });
        return  MyView;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        lockScreenOrientation();
        super.onCreate(savedInstanceState);


    }
    @Override
    public void onResume() {
        super.onResume();
        openBackgroundThread();
        if (((TextureView)(MyView.findViewById(R.id.texture_view))).isAvailable()) {
            setUpCamera();
            openCamera();
        } else {
            ((TextureView)(MyView.findViewById(R.id.texture_view))).setSurfaceTextureListener(surfaceTextureListener);
        }
    }
    @Override
    public void onStop(){
        backgroundThread.quitSafely();
        super.onStop();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void lockScreenOrientation() {
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    private void unlockScreenOrientation() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

}
