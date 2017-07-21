package com.example.au.imageeditor;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ImageView Image;
    private Button choose;
    private GridView EffectGrid;
    private HorizontalGridView horizontalGridView;
    private static int RESULT_LOAD_IMAGE = 1;
    ArrayList<String> effect;
    effectgridadapter adapter;
    Bitmap muBitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    private Bitmap bitmap;
    Mat src;

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.d("tag1", "OpenCV not loaded");
        } else {
            Log.d("tag2", "OpenCV loaded");
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.loadLibrary("NativeImageProcessor");
        setContentView(R.layout.activity_main);
        Image= (ImageView) findViewById(R.id.imageView);
        Bitmap image1 = BitmapFactory.decodeResource(getResources(),R.drawable.images);
        muBitmap= BitmapFactory.decodeResource(getResources(),R.drawable.images);
        Image.setImageBitmap(image1);
        bitmap=((BitmapDrawable)Image.getDrawable()).getBitmap();
        choose= (Button) findViewById(R.id.button);
       // EffectGrid= (GridView) findViewById(R.id.editgrid);
        verifyStoragePermissions(this);



        horizontalGridView = (HorizontalGridView) findViewById(R.id.editgrid);
        effect=new ArrayList<String>();
        effect.add("Greyscale");
        effect.add("Sharpen");
        effect.add("Highlight");
        effect.add("MeanRemoval");
        effect.add("Brightness");
        effect.add("Sepia");
        effect.add("Sobelh");
        effect.add("Sobelv");
        effect.add("Flip");
        effect.add("Invert");
        adapter = new effectgridadapter(this,effect,Image);

        horizontalGridView.setAdapter(adapter);





    }

    public void Loadimage(View view) {
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();


            Image.setImageBitmap(BitmapFactory.decodeFile(picturePath));
             muBitmap = BitmapFactory.decodeFile(picturePath);
            adapter = new effectgridadapter(this,effect,Image);

            horizontalGridView.setAdapter(adapter);




        }


    }
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
    private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {

                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    public void Undo(View view) {
        Image.setImageBitmap(muBitmap);
    }

    public void Saveimage(View view) {
        BitmapDrawable draw = (BitmapDrawable) Image.getDrawable();
        Bitmap bitmap = draw.getBitmap();

        FileOutputStream outStream = null;
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + "/imageproject");
        dir.mkdirs();
        String fileName = String.format("%d.jpg", System.currentTimeMillis());
        File outFile = new File(dir, fileName);
        try {
            outStream = new FileOutputStream(outFile);

        } catch (FileNotFoundException mE) {
            mE.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
        String path = sdCard.getAbsolutePath();
        Toast.makeText(this,"imaged saved in "+path+"/imageproject",Toast.LENGTH_LONG).show();
        try {
            outStream.flush();
        } catch (IOException mE) {
            mE.printStackTrace();
        }
        try {
            outStream.close();
        } catch (IOException mE) {
            mE.printStackTrace();
        }
    }
}

