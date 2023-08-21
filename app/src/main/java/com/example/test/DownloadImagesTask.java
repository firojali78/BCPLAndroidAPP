package com.example.test;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DownloadImagesTask extends AsyncTask<ArrayList<String>, Void, Void> {

    private ProgressDialog progressDialog;
    private Context context;


    public DownloadImagesTask(Context context)
    {
         this.context= context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Downloading Images");
        progressDialog.setCancelable(false);
        progressDialog.show();


    }

    @Override
    protected Void doInBackground(ArrayList<String>... arrayLists) {
        ArrayList<String> imageUrl = arrayLists[0];
        ContextWrapper cw = new ContextWrapper(context);

        for (String imageUri : imageUrl)
        {
            try
            {
                URL url = new URL(imageUri);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
              //  File directory  = new File(Environment.getExternalStorageDirectory() + "/BCPL");
                File directory = cw.getDir("BCPL", Context.MODE_PRIVATE);
                if(!directory.exists())
                {
                    directory.mkdir();
                }
                String u = url.toString();
                u = u.replace("-","");
                File file = new File(directory,u.substring(u.lastIndexOf("/")+1));
                FileOutputStream fileOutputStream = new FileOutputStream(file);

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();

                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

                System.out.println("Image Downloaded successfully " + u);
            }
            catch (Exception e)
            {
                System.out.println("error in downloading image "+e.toString());
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        progressDialog.dismiss();
        Toast.makeText(context, "Images Downloaded Successfully", Toast.LENGTH_SHORT).show();
    }
}
