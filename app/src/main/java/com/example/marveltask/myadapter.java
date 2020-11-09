package com.example.marveltask;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marveltask.database.constraint;
import com.example.marveltask.database.firstmanager;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class myadapter extends BaseAdapter {

    private List<bind_data> viewlist;

    private LayoutInflater lf=null;
    // Context ctx=null;
    AlertDialog alertDialog;

    //************************ Sqlite Database ************************//
    SQLiteDatabase sb;
    firstmanager cm;


    public myadapter(Activity activity, List<bind_data>viewlist)
    {
        //ctx= activity.getApplicationContext();
        this.viewlist=viewlist;
        lf=LayoutInflater.from(activity);

        cm=new firstmanager(activity);
        sb=cm.openDB();
    }



    @Override
    public int getCount() {
        return viewlist.size();
    }

    @Override
    public Object getItem(int position) {
        return viewlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Toast.makeText(ctx,"view created",Toast.LENGTH_LONG).show();

        if(convertView==null)
        {
            convertView = lf.inflate(R.layout.mycustom,parent,false);
        }


        LinearLayout lnr_call=convertView.findViewById(R.id.lnr_cal);

        CircleImageView img1=convertView.findViewById(R.id.view_img);
        TextView tv1=convertView.findViewById(R.id.view_name);
        TextView tv2=convertView.findViewById(R.id.view_details);
        ImageView tv3=convertView.findViewById(R.id.view_delete);
        ImageView tv4=convertView.findViewById(R.id.view_edit);




        final bind_data d=viewlist.get(position);
        tv1.setText(d.getName());
        tv2.setText(d.getDetails());

        String abc=d.getImg();


        byte[] decodedString = Base64.decode(abc, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        img1.setImageBitmap(decodedByte);


        tv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
                alertDialogBuilder.setMessage("Are you sure want to delete?");
                        alertDialogBuilder.setPositiveButton("yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {

                                        sb.execSQL("DELETE FROM " + constraint.TBL_NAME + " WHERE " + constraint.COL_ID + "= '" + d.getId() + "'");
                                        sb.close();

                                        Toast.makeText(v.getContext(),"Marvel Info has been deleted...",Toast.LENGTH_LONG).show();
                                        Intent i = new Intent(v.getContext(),MainActivity.class);
                                        v.getContext().startActivity(i);


                                    }
                                });

                alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });

        tv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent i= new Intent(v.getContext(),update.class);
                i.putExtra("key_id",d.getId());
                i.putExtra("name",d.getName());
                i.putExtra("details",d.getDetails());
                i.putExtra("img",d.getImg());
                v.getContext().startActivity(i);

            }
        });


        return convertView;

    }


}

