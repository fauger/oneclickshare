package com.zeroetun.oneclickshare;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import com.zeroetun.oneclickshare.service.FireBaseService;
import com.zeroetun.oneclickshare.service.ShareService;
import com.zeroetun.oneclickshare.service.UploadListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShareActivity extends AppCompatActivity {

    ImageView imageView;
    ProgressBar progressBar;
    ShareService mShareService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        imageView =(ImageView) findViewById(R.id.share_img);
        progressBar = (ProgressBar) findViewById(R.id.progress);

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendImage(intent, this); // Handle single image being sent
            }


        }
    }

        void handleSendImage(Intent intent, final Context context) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.bringToFront();
            Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
            Log.i("Upload","img " +imageUri );
            if (imageUri != null) {

                //Glide.with(context)
                //      .load(imageUri) // Uri of the picture
                //    .into(imageView);

                FireBaseService fireBaseService = new FireBaseService();
                fireBaseService.uploadFromLocalFile(imageUri, new UploadListener() {
                    @Override
                    public void onSuccess(Uri uri) {
                        final TextView textView = new TextView(context);
                        textView.setText(uri.toString());
                        textView.setTextIsSelectable(true);

                        new AlertDialog.Builder(context)
                                .setTitle("Your image has been uploaded successfully :-)")
                                .setView(textView)
                                .setPositiveButton(android.R.string.ok, null)
                                .setNegativeButton("Copy to clipboard", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ClipboardManager manager =
                                                (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                        manager.setText( textView.getText() );
                                        // Show a message:
                                        Toast.makeText(context, "Text in clipboard",
                                                Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                })
                                .setCancelable(false)
                                .show();
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Toast.makeText(context, "Erreur", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }



        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_share, menu);
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

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
