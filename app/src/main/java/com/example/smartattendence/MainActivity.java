package com.example.smartattendence;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.controls.Mode;

import java.util.List;

import dmax.dialog.SpotsDialog;
public class MainActivity extends AppCompatActivity {

    private Button FDButton;
    private GraphicOverlay graphicOverlay;
    AlertDialog alertDialog;
    private String TAG = "MainActivity";
    private ImageView imageView;
    private CameraView cameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageview);
        FDButton = findViewById(R.id.DFButton);
        graphicOverlay = findViewById(R.id.graphic_overlay);
        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Please Wait....")
                .setCancelable(false)
                .build();
        cameraView = findViewById(R.id.camView);
        cameraView.setMode(Mode.PICTURE);
        cameraView.setLifecycleOwner(this);

        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(@NonNull PictureResult result) {
                super.onPictureTaken(result);
                Bitmap bitmap = BitmapFactory.decodeByteArray(result.getData(), 0, result.getData().length);
//               imageView.setImageBitmap(bitmap);
                processFaceDetection(bitmap);
                //cameraView.setVisibility(View.GONE);
            }
        });
        FDButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraView.takePicture();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            //           imageView.setImageBitmap(photo);
        }
    }


    void processFaceDetection(Bitmap bitmap) {
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionFaceDetectorOptions firebaseVisionFaceDetectorOptions = new FirebaseVisionFaceDetectorOptions.Builder().build();
        FirebaseVisionFaceDetector firebaseVisionFaceDetector = FirebaseVision.getInstance().getVisionFaceDetector(firebaseVisionFaceDetectorOptions);
        firebaseVisionFaceDetector.detectInImage(firebaseVisionImage)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {

                    @Override
                    public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                        Log.d(TAG, "onSuccess: "+firebaseVisionFaces.size());
//                        Log.d(TAG, "onSuccess: "+firebaseVisionFaces.get(0).toString());
                        getfaceResult(firebaseVisionFaces);
                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getfaceResult(List<FirebaseVisionFace> firebaseVisionFaces) {
        if(firebaseVisionFaces.size()>0){
            Log.d(TAG, "getfaceResult: "+firebaseVisionFaces.size());
            Log.d(TAG, "getfaceResult: bottom "+firebaseVisionFaces.get(0).getBoundingBox().bottom);
            Log.d(TAG, "getfaceResult: top"+firebaseVisionFaces.get(0).getBoundingBox().top);
            Log.d(TAG, "getfaceResult: left"+firebaseVisionFaces.get(0).getBoundingBox().left);
            Log.d(TAG, "getfaceResult: right"+firebaseVisionFaces.get(0).getBoundingBox().right);
//
            int counter = 0;
            Toast.makeText(this, firebaseVisionFaces.size()+" faces detected", Toast.LENGTH_SHORT).show();
            for (FirebaseVisionFace face : firebaseVisionFaces) {
                Rect rect = face.getBoundingBox();
                RectOverlay rectOverlay = new RectOverlay(graphicOverlay, rect);
                graphicOverlay.add(rectOverlay);
                counter = counter + 1;
            }
            alertDialog.dismiss();
        }

        Toast.makeText(this, "No faces detected by Google", Toast.LENGTH_SHORT).show();


    }

}
