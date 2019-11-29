package com.example.smartattendence;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartattendence.Helper.GraphicOverlay;
import com.example.smartattendence.Helper.RectOverlay;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.util.List;

import dmax.dialog.SpotsDialog;
import eu.livotov.labs.android.camview.CameraLiveView;
public class MainActivity extends AppCompatActivity {

    private Button FDButton;
    private CameraLiveView cameraLiveView;
    private GraphicOverlay graphicOverlay;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FDButton = findViewById(R.id.DFButton);
        graphicOverlay = findViewById(R.id.graphic_overlay);
        cameraLiveView = findViewById(R.id.camView);
        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Please Wait....")
                .setCancelable(false)
                .build();

        FDButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraLiveView.startCamera();
                cameraLiveView.performClick();
                graphicOverlay.clear();
            }
        });


        cameraLiveView.CameraLiveViewEventsListener(new CameraLiveView.CameraLiveViewEventsListener() {
            @Override
            public void onCameraStarted(CameraLiveView camera) {
                alertDialog.show();

                processFaceDetection(bitmap);
            }

            @Override
            public void onCameraStopped(CameraLiveView camera) {

            }

            @Override
            public void onCameraError(Throwable err) {

            }
        });
//        FDButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                camView.onStart();
//
//                camView.captureImage(new CameraKitView.ImageCallback() {
//                    @Override
//                    public void onImage(CameraKitView cameraKitView,final byte[] captureImage) {
//
//                    }
//
//                });
//                graphicOverlay.clear();
//
//            }
//        });


    }

     void processFaceDetection(Bitmap bitmap) {
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionFaceDetectorOptions firebaseVisionFaceDetectorOptions = new FirebaseVisionFaceDetectorOptions.Builder().build();
        FirebaseVisionFaceDetector firebaseVisionFaceDetector = FirebaseVision.getInstance().getVisionFaceDetector(firebaseVisionFaceDetectorOptions);
        firebaseVisionFaceDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(new


                                                                                                   OnSuccessListener<List<FirebaseVisionFace>>() {
                                                                                                       @Override
                                                                                                       public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                                                                                                           getfaceResult(firebaseVisionFaces);

                                                                                                       }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Error"+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getfaceResult(List<FirebaseVisionFace> firebaseVisionFaces) {
        int counter = 0;
        for(FirebaseVisionFace face : firebaseVisionFaces)
        {
            Rect rect = face.getBoundingBox();
            RectOverlay rectOverlay = new RectOverlay(graphicOverlay,rect);
            graphicOverlay.add(rectOverlay);

            counter = counter + 1;
        }
        alertDialog.dismiss();


    }


    @Override
    protected void onResume() {
        super.onResume();
        cameraLiveView.startCamera();
    }

    @Override
    protected void onPause() {
        cameraLiveView.stopCamera();
        super.onPause();
    }
}
