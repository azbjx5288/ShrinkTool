package com.shrinktool.component;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Window;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class PicturePicker extends Activity {
    private static final String TAG = PicturePicker.class.getSimpleName();
    
    private static final String KEY_OUTPUT_X = "output-x";
    private static final String KEY_OUTPUT_Y = "output-y";
    private static final String KEY_RESULT_PATH = "result-path";
    private static final String KEY_TYPE = "type";
    
    private static final int STATE_INIT = 0;
    private static final int STATE_PHOTO_PICKED = 1;
    private static final int STATE_PHOTO_CROP = 2;
    private static final int STATE_PHOTO_CAMERA_PICKED = 3;
    
    private int mState = STATE_INIT;
    
    public enum TYPE {
        TYPE_CAMERA,
        TYPE_ALBUM
    }
    
    private String mResultPath;
    private Uri mCropImageUri;
    private TYPE mType;
    private int mOutputX;
    private int mOutputY;
    
    public static void action(Activity activity, int requestCode, TYPE type, int w, int h, String resultPath) {
        Intent intent = new Intent(activity, PicturePicker.class);
        attachParameters(intent, type, w, h, resultPath);
        activity.startActivityForResult(intent, requestCode);
    }
    
    //result需要返回到fragment
    public static void action(Fragment fragment, int requestCode, TYPE type, int w, int h, String resultPath) {
        Intent intent = new Intent(fragment.getActivity(), PicturePicker.class);
        attachParameters(intent, type, w, h, resultPath);
        fragment.startActivityForResult(intent, requestCode);
    }
    
    private static void attachParameters(Intent intent, TYPE type, int w, int h, String resultPath) {
        intent.putExtra(KEY_TYPE, type);
        intent.putExtra(KEY_RESULT_PATH, resultPath);
        intent.putExtra(KEY_OUTPUT_X, w);
        intent.putExtra(KEY_OUTPUT_Y, h);
    }
    
    @Override
    protected void onCreate(Bundle bundle) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(bundle);
        Intent intent = getIntent();
        mType = (TYPE) intent.getSerializableExtra(KEY_TYPE);
        mResultPath = intent.getStringExtra(KEY_RESULT_PATH);
        mOutputX = intent.getIntExtra(KEY_OUTPUT_X, 120);
        mOutputY = intent.getIntExtra(KEY_OUTPUT_Y, 120);
        Log.d(TAG, "onCreate type=" + mType + ", outputX=" + mOutputX
                + ", outputY=" + mOutputY + ", resultPath=" + mResultPath);
        
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        switch (mState) {
            case STATE_INIT: {
                switch (mType) {
                    case TYPE_CAMERA:
                        pickFormCamera();
                        break;
                    case TYPE_ALBUM:
                        pickFormAlbum();
                        break;
                }
                return;
            }
            
            case STATE_PHOTO_CAMERA_PICKED:
            case STATE_PHOTO_PICKED: {
                callCrop();
                break;
            }
        }
    }
    
    private void pickFormCamera() {
        mState = STATE_PHOTO_CAMERA_PICKED;
        mCropImageUri = Uri.fromFile(getTempFile());
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Bundle bundle = new Bundle();
        //兼容性，相机可能从Bundle或Intent读取EXTRA_OUTPUT字段
        bundle.putParcelable(MediaStore.EXTRA_OUTPUT, mCropImageUri);
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, mCropImageUri);
        intentCamera.putExtras(bundle);
        startActivityForResult(intentCamera, mState);
    }
    
    private void callCrop() {
        /*ELog.d(TAG, "callCrop mCropImageUri=" + mCropImageUri);*/
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(mCropImageUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", mOutputX);
        intent.putExtra("aspectY", mOutputY);
        intent.putExtra("outputX", mOutputX);
        intent.putExtra("outputY", mOutputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mResultPath)));
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, STATE_PHOTO_CROP);
    }
    
    private void pickFormAlbum() {
        mState = STATE_PHOTO_PICKED;
        Intent intentPick = new Intent(Intent.ACTION_GET_CONTENT, null);
        intentPick.addCategory(Intent.CATEGORY_OPENABLE);
        intentPick.setType("image/*");
        
        startActivityForResult(intentPick, mState);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*ELog.d(TAG, "onActivityResult requestCode=" + requestCode + ", resultCode=" + resultCode
				+ ", data=" + data);*/
        if (resultCode != RESULT_OK) {
            setResult(resultCode);
            finish();
            return;
        }
        
        mState = requestCode;
        switch (mState) {
            case STATE_PHOTO_CROP:
                if (new File(mResultPath).exists()) {
                    setResult(RESULT_OK);
                    finish();
                    return;
                }
                break;
            case STATE_PHOTO_PICKED:
                Uri uri = data.getData();
                if (uri != null) {
                    transition(uri);
                } else {
                    setResult(RESULT_CANCELED);
                    finish();
                }
                break;
        }
    }

    private File getTempFile() {
        return new File(getExternalCacheDir(),
                "crop_image_" + System.currentTimeMillis() + "" + ".jpg");
    }

    //返回的uri可能是第三方数据库，将数据另存为后再裁剪
    private void transition(Uri uri){
        if (uri.toString().startsWith("file:") || uri.toString().startsWith(Images.Media.EXTERNAL_CONTENT_URI.toString())) {
            mCropImageUri = uri;
            return;
        }
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            //is可能不是jpg的，但skia解码库会处理的
            File file = getTempFile();
            FileOutputStream os = new FileOutputStream(file);
            int bytesRead;
            byte[] buf = new byte[1024];
            while ((bytesRead = is.read(buf)) != -1) {
                os.write(buf, 0, bytesRead);
            }
            os.flush();
            os.close();
            is.close();
            mCropImageUri = Uri.fromFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        String tmp = "file://" + getExternalCacheDir().toString();
        if (mCropImageUri != null && mCropImageUri.toString().startsWith(tmp)) {
            new File(mCropImageUri.getPath()).delete();
        }
    }
}
