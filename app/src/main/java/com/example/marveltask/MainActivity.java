package com.example.marveltask;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marveltask.database.constraint;
import com.example.marveltask.database.firstmanager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    TextView add;
    EditText ed_name,ed_details;
    ImageView ed_img;
    Button btn_save;
    GridView gridview;

    public static final int CAMERA_DOC = 1;
    public static final int GALLERY_DOC = 2;

    android.app.AlertDialog alertDialog1;

    Bitmap bmc,bitmapG;
    byte[] imgar;
    String saveimg="";


    bind_data vw;
    ArrayList<bind_data> viewlist;


    //************************ Sqlite Database ************************//
    SQLiteDatabase sb;
    firstmanager cm=new firstmanager(this);




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.CAMERA,android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                1);





        add= findViewById(R.id.add);
        gridview= findViewById(R.id.gridview);
        sb=cm.openDB();



        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                add_details();

            }
        });


        get_data();


        // ****************************************   oncreate closing here      ***************************************
    }




    public void get_data()
    {

        viewlist =new ArrayList<>();
        filllist();
        myadapter md=new myadapter(this,viewlist);
        gridview.setAdapter(md);
    }






    private void filllist() {


        viewlist.clear();

        Cursor c=sb.query(constraint.TBL_NAME,null,null,null,null,null,null);
        if(c!=null  && c.moveToFirst()) {
            do {
                String id = c.getString(c.getColumnIndex(constraint.COL_ID));
                String name = c.getString(c.getColumnIndex(constraint.COL_NAME));
                String detail = c.getString(c.getColumnIndex(constraint.COL_DETAIL));
                String img = c.getString(c.getColumnIndex(constraint.COL_IMAGE));

                vw = new bind_data();
                vw.setId(id);
                vw.setName(name);
                vw.setDetails(detail);
                vw.setImg(img);

                viewlist.add(vw);
            } while (c.moveToNext());
            c.close();
        }
        else
        {
            //   Toast.makeText(this,"Empty Table",Toast.LENGTH_SHORT).show();
        }
    }

    public void add_details(){



        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        final View promptsView = li.inflate(R.layout.add_details,null);

        android.app.AlertDialog.Builder alertDialogBuilder1 = new android.app.AlertDialog.Builder(MainActivity.this);

        alertDialogBuilder1.setView(promptsView);

        ed_img=promptsView.findViewById(R.id.ed_img);
        ed_name = promptsView.findViewById(R.id.ed_name);
        ed_details = promptsView.findViewById(R.id.ed_detail);
        btn_save=promptsView.findViewById(R.id.btn_save);




        ed_img.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {


                take_photo();



            }
        });



        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if(saveimg.equals(""))
                {

                    Toast.makeText(MainActivity.this, "Please Add Photo", Toast.LENGTH_SHORT).show();
                }


                else {


                    save_details();



                }


            }
        });


        alertDialog1 = alertDialogBuilder1.create();

        alertDialog1.show();



    }




    private void take_photo() {


        final CharSequence[] options = { "Take Photo","Choose from Gallery","Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {

//                        Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        Intent intent= new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
//                        startActivityForResult(intent, CAMERA_DOC);



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
                        Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
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


        try {

            ContentValues cv = new ContentValues();
            cv.put(constraint.COL_NAME, ed_name.getText().toString());
            cv.put(constraint.COL_DETAIL, ed_details.getText().toString());
            cv.put(constraint.COL_IMAGE, saveimg);


            long l = sb.insert(constraint.TBL_NAME, null, cv);
            System.out.println("save sqlite"+l);

            if (l>0)
            {

                Toast.makeText(this, "Sucessfully Saved", Toast.LENGTH_SHORT).show();
                alertDialog1.cancel();
                saveimg="";

                get_data();
            }


        } catch (Exception e) {


            e.printStackTrace();
        } finally {


        }




    }





    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {





                } else {


                }
                return;
            }






        }

    }






}
