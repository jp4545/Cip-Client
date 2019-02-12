package com.example.cipclient;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class NotesUpload extends AppCompatActivity {

    Button uploadnotes, uPloads;

    Uri pdfurl;
    EditText notification;
    TextView sel;
    ProgressDialog progressdialog;
    String sUrl;
    String encodedImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_upload);
        uploadnotes = (Button) findViewById(R.id.UploadnOtes);
        uPloads = (Button) findViewById(R.id.uPloadnOtes);
        notification = (EditText) findViewById(R.id.Notification);
        sel = (TextView) findViewById(R.id.select);
        progressdialog = new ProgressDialog(this);
        sUrl = "http://192.168.43.175:5000/hello/jp";
        uploadnotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v == uploadnotes)
                    Toast.makeText(NotesUpload.this,"Hello jp",Toast.LENGTH_SHORT).show();
                    selectpdf();
            }
        });


    }

    private void selectpdf() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 86);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 86 && resultCode == RESULT_OK && data != null) {
            pdfurl = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), pdfurl);
                getStringImage(bitmap);
            } catch (FileNotFoundException e) {

                e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

            sel.setText("Selected file is");

        } else {
            Toast.makeText(NotesUpload.this, "Please select a file", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ((requestCode == 9) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectpdf();
        } else {
            Toast.makeText(NotesUpload.this, "Please Give permission", Toast.LENGTH_SHORT).show();
        }
    }

    private class hello extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            String dataset= "jp";

            HttpURLConnection conn = null;
            try {
                String tem = "http://192.168.43.175:5000/hello";
                URL url = new URL(tem);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write( encodedImage );
                wr.flush();
                try {

                    InputStreamReader input = new InputStreamReader(conn.getInputStream());
                    int temp = conn.getResponseCode();
                    BufferedReader r = new BufferedReader(input);
                    StringBuilder total = new StringBuilder();
                    for (String line; (line = r.readLine()) != null; ) {
                        total.append(line).append('\n');
                    }
                    notification.setText(total.toString());

                } catch (Exception e) {
                    Toast.makeText(NotesUpload.this,"Inside Exception",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } finally {
                    conn.disconnect();
                }
            } catch (MalformedURLException e) {
                notification.setText("malformed");
            } catch (IOException e) {
                notification.setText("Ioexception");
            }
            return null;

        }
    }
    public void getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        Toast.makeText(NotesUpload.this,encodedImage,Toast.LENGTH_SHORT).show();
        new hello().execute();
    }
    }

