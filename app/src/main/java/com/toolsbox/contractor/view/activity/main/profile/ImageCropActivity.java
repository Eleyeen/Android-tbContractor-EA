package com.toolsbox.contractor.view.activity.main.profile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.utils.BitmapHelper;
import com.toolsbox.contractor.view.activity.basic.BaseActivity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

public class ImageCropActivity extends BaseActivity implements CropImageView.OnSetImageUriCompleteListener,
        CropImageView.OnCropImageCompleteListener{
    private static String TAG = "ImageCropActivity";
    private Toolbar toolbar;
    private CropImageView civPhoto;
    private Uri imageUri;
    private Bitmap cropedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crop);
        initVariable();
        initUI();
    }

    void initVariable(){
        imageUri = getIntent().getParcelableExtra("uri");
    }

    void initUI(){
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorWhite), 0);
        setupToolbar();
        civPhoto = findViewById(R.id.crop_image_view);
        civPhoto.setImageUriAsync(imageUri);
        civPhoto.setOnCropImageCompleteListener(this);
    }

    void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.crop_image);
        } catch (Exception e) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_industry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_done:
                civPhoto.getCroppedImageAsync();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void goToFirstScreen(Uri savedUri){
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        intent.putExtra("cropUri", savedUri);
        finish();
    }

    @Override
    public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
        handleCropResult(result);
    }

    @Override
    public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {
        if (error == null) {
            Toast.makeText(this, "Image load successful", Toast.LENGTH_SHORT).show();
        } else {
            Log.e("AIC", "Failed to load image by URI", error);
            Toast.makeText(this, "Image load failed: " + error.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void handleCropResult(CropImageView.CropResult result) {
        if (result.getError() == null) {
            cropedImage = result.getBitmap();
            Log.e(TAG, "Crop Bitmap Size width=" + cropedImage.getWidth() + "  Height=" + cropedImage.getHeight());
            Uri savedUri = BitmapHelper.savePicture(this, cropedImage, Environment.getExternalStorageDirectory().getPath() + "/" + "temp.jpg");
            Log.e(TAG, "Saved Uri" + savedUri.toString());
            goToFirstScreen(savedUri);
        } else {
            Log.e("AIC", "Failed to crop image", result.getError());
            Toast.makeText(
                    this,
                    "Image crop failed: " + result.getError().getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }
}
