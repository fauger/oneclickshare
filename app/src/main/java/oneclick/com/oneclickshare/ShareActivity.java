package oneclick.com.oneclickshare;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import oneclick.com.oneclickshare.service.ShareService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShareActivity extends AppCompatActivity {

    ImageView imageView;
    ShareService mShareService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        imageView =(ImageView) findViewById(R.id.share_img);

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

        void handleSendImage(Intent intent, Context context) {
            Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
            Log.i("Upload","img " +imageUri );
            if (imageUri != null) {

                //Glide.with(context)
                  //      .load(imageUri) // Uri of the picture
                    //    .into(imageView);

              ShareService.ApiInterface api = ShareService.servicesInterface;
                File file = new File(getPath(imageUri));
                MultipartBody.Part fbody = MultipartBody.Part.createFormData("test",file.getName(), RequestBody.create(MediaType.parse(getContentResolver().getType(imageUri)), file));
                Call<ResponseBody> call = api.upload(fbody);
                Log.i("Upload","start upload" );
                call.enqueue(new Callback<ResponseBody>() {


                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.i("Upload","success : " + response.message()  + " / code :" + response.code() );
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.i("Upload","failure",t);
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
