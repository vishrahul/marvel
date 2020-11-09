package com.example.marveltask;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.marveltask.database.constraint;
import com.example.marveltask.database.firstmanager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class update extends AppCompatActivity {

    EditText ed_name,ed_details;
    ImageView ed_img;
    Button btn_save;

    String id;

    public static final int CAMERA_DOC = 1;
    public static final int GALLERY_DOC = 2;

    android.app.AlertDialog alertDialog1;

    Bitmap bmc,bitmapG;
    byte[] imgar;
    String saveimg="";

    //************************ Sqlite Database ************************//
    SQLiteDatabase sb;
    firstmanager cm=new firstmanager(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        ed_img=findViewById(R.id.ed_img);
        ed_name = findViewById(R.id.ed_name);
        ed_details = findViewById(R.id.ed_detail);
        btn_save=findViewById(R.id.btn_save);

        id=getIntent().getStringExtra("key_id");

        ed_name.setText(getIntent().getStringExtra("name"));
        ed_details.setText(getIntent().getStringExtra("details"));

        saveimg =getIntent().getStringExtra("img");

        byte[] decodedString = Base64.decode(saveimg, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        ed_img.setImageBitmap(decodedByte);


        sb=cm.openDB();

        ed_img.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {


                take_photo();



            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(saveimg.equals(""))
                {

                    Toast.makeText(update.this, "Please Add Photo", Toast.LENGTH_LONG).show();
                }


                else {


                    save_details();



                }
            }
        });

    }





    private void take_photo() {


        final CharSequence[] options = { "Take Photo","Choose from Gallery","Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(update.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {

                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    takePictureIntent.setPackage("com.android.camera");
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, CAMERA_DOC);
                    }
                }
                else if (options[item].equals("Choose from Gallery"))
                {


                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, GALLERY_DOC);

                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == this.RESULT_CANCELED) {
            return;
        }


        if (requestCode == GALLERY_DOC) {

            try{

                if (data != null) {
                    Uri contentURI = data.getData();
                    try {
                        bitmapG = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                        String path = save_image_method(bitmapG);
                        ed_img.setImageBitmap(bitmapG);




                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(update.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }



                bitmapG = getResizedBitmap(bitmapG, 500);

                ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
                bitmapG.compress(Bitmap.CompressFormat.PNG, 100, bos1);
                imgar = bos1.toByteArray();
                saveimg = Base64.encodeToString(imgar, Base64.DEFAULT);


            }catch (Exception e){

            }

        }




        else if (requestCode == CAMERA_DOC) {


            try{




                Bundle b=data.getExtras();
                bmc=(Bitmap)b.get("data");
                ed_img.setImageBitmap(bmc);


                bmc = getResizedBitmap(bmc, 500);


                ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
                bmc.compress(Bitmap.CompressFormat.PNG, 100, bos1);
                imgar = bos1.toByteArray();
                saveimg = Base64.encodeToString(imgar, Base64.DEFAULT);


            }catch (Exception e){

            }




        }


    }



    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }



    public String save_image_method(Bitmap myBitmap) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        File wallpaperDirectory = new File(Environment.getExternalStorageDirectory() + "pp");

        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance().getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }






    private void save_details() {


        ContentValues cv = new ContentValues();  //ContentValue is use for set the value of the collom
        String arg[] = {id};
        cv.put(constraint.COL_NAME, ed_name.getText().toString());
        cv.put(constraint.COL_DETAIL,ed_details.getText().toString());
        cv.put(constraint.COL_IMAGE,saveimg);


        int rw= sb.update(constraint.TBL_NAME, cv, constraint.COL_ID + "=?",arg);
        if(rw>0)
        {
            Toast.makeText(update.this,"Marvel Info has been updated...",Toast.LENGTH_SHORT).show();
            Intent i = new Intent(update.this,MainActivity.class);
            startActivity(i);
        }
        else {
        }







    }


}
