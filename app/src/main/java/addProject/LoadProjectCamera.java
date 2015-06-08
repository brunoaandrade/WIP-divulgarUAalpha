package addProject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import deti.ua.divulgarua.R;
import webentities.Project;


public class LoadProjectCamera extends Activity {
    private static final int ACTION_TAKE_PHOTO_B = 2;
    private static final int ACTION_TAKE_VIDEO = 3;
    private static final int CONTINUE_TO_PHOTO_LIST = 4;
    private static final int RESULT_LOAD_IMAGE = 5;
    private static final int CONTINUE_TO_PHOTO_FILTER = 6;
    private static final int SHARE_PHOTO=9;
    private final static String EXTRA_MESSAGE = "Message";
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    private Bitmap mImageBitmap;
    private Uri mVideoUri;
    private String mCurrentPhotoPath;
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private String projectName = "";
    private boolean newProject=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mImageBitmap = null;
        mVideoUri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }

        Bundle b = getIntent().getExtras();
        projectName = b.getString("Name");
        if(!projectName.equals(""))
            newProject=false;
        int value = b.getInt("Option");
        addAction(value);
    }

    @Override
    public void onBackPressed() {
        mImageBitmap.recycle();
        mImageBitmap = null;
        Intent returnIntent = new Intent();
        setResult(this.RESULT_CANCELED, returnIntent);
        finish();
    }

    private void addAction(int option) {

        switch (option) {
            case 1: {
                dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
                break;
            }
            case 2: {
                dispatchTakeVideoIntent();
                break;
            }
            case 3: {
                handleGalleryResult();
                break;
            }
        }
    }

    private void handleGalleryResult() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    /* Photo album for this application */
    private String getAlbumName() {
        return getString(R.string.app_name);
    }

    private File getAlbumDir() {
        File storageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());
            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }
        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }
        return storageDir;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }

    private void dispatchTakePictureIntent(int actionCode) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = null;

        try {
            f = setUpPhotoFile();
            mCurrentPhotoPath = f.getAbsolutePath();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        } catch (IOException e) {
            e.printStackTrace();
            f = null;
            mCurrentPhotoPath = null;
        }


        startActivityForResult(takePictureIntent, actionCode);
    }

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(takeVideoIntent, ACTION_TAKE_VIDEO);
    }

    private void handleBigCameraPhoto() {

        if (mCurrentPhotoPath != null) {
            //setPic();
            Intent intent = new Intent(this, PhotoEdit.class);
            intent.putExtra(EXTRA_MESSAGE, mCurrentPhotoPath);
            startActivityForResult(intent, CONTINUE_TO_PHOTO_FILTER);
            mCurrentPhotoPath = null;
        }
    }

    private void handleCameraVideo(Intent intent) {
        mVideoUri = intent.getData();
        mImageBitmap = null;


        if(projectName.equals("")) {
            Intent intentVideo = new Intent(this, AddToProject.class);
            ArrayList<String> elements = new ArrayList<>();
            elements.add(getRealPathFromURI(mVideoUri));
            intentVideo.putExtra(EXTRA_MESSAGE, elements);
            startActivityForResult(intentVideo, SHARE_PHOTO);
        }else{
            Intent intentVideo = new Intent(this, APartilharProjecto.class);
            ArrayList<String> elements = new ArrayList<>();
            elements.add(getRealPathFromURI(mVideoUri));
            intentVideo.putExtra("Name", projectName);
            intentVideo.putExtra(EXTRA_MESSAGE, elements);
            startActivityForResult(intentVideo, 1);
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getApplicationContext().getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private String getPathFromContentDocument(Uri path) {
        String wholeID = DocumentsContract.getDocumentId(path);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = {MediaStore.Images.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{id}, null);

        String filePath = "";

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            return filePath = cursor.getString(columnIndex);
        }

        cursor.close();
        return "";
    }

    private String getPathFromContent(Uri path) {
        if (path.toString().contains("media"))
            return getPathFromContentDocument(path);
        else if (path.toString().contains("google.android"))
            return getPathFromContentGooleDrive(path);
        else if (path.toString().contains("skydrive"))
            return getPathFromContentOneDrive(path);
        else if (path.toString().contains("file:") || path.toString().contains("dropbox"))
            return path.toString();
        return null;
    }

    private String getPathFromContentGooleDrive(Uri path) {
        //fazer
        return null;
    }

    private String getPathFromContentOneDrive(Uri path) {
        //fazer
        return null;
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1: {
                if (resultCode == this.RESULT_OK) {
                    Intent returnIntent = new Intent();
                    setResult(this.RESULT_OK, returnIntent);
                    finish();
                }
                if (resultCode == this.RESULT_CANCELED) {
                    Intent returnIntent = new Intent();
                    setResult(this.RESULT_CANCELED, returnIntent);
                    finish();
                }
                break;
            }
            case ACTION_TAKE_PHOTO_B: {
                if (resultCode == RESULT_OK) {
                    handleBigCameraPhoto();
                } else {
                    Intent returnIntent = new Intent();
                    setResult(this.RESULT_CANCELED, returnIntent);
                    finish();
                }
                break;
            } // ACTION_TAKE_PHOTO_B

            case ACTION_TAKE_VIDEO: {
                if (resultCode == RESULT_OK) {
                    handleCameraVideo(data);
                } else {
                    Intent returnIntent = new Intent();
                    setResult(this.RESULT_CANCELED, returnIntent);
                    finish();
                }
                break;
            } // ACTION_TAKE_VIDEO
            case CONTINUE_TO_PHOTO_FILTER: {
                if (resultCode == RESULT_OK && null != data) {

                    String path = data.getStringExtra("path");
                    Intent intent = new Intent(this, PhotoFilter.class);
                    intent.putExtra(EXTRA_MESSAGE, path);
                    startActivityForResult(intent, CONTINUE_TO_PHOTO_LIST);
                } else {
                    Intent returnIntent = new Intent();
                    setResult(this.RESULT_CANCELED, returnIntent);
                    finish();
                }
                break;
            } //
            case CONTINUE_TO_PHOTO_LIST: {
                if (resultCode == RESULT_OK && null != data) {
                    String path = data.getStringExtra("path");
                    Boolean nextFilter = data.getBooleanExtra("nextFilter", false);

                    if(nextFilter) {
                        if(projectName.equals("")) {
                            Intent intent = new Intent(this, AddToProject.class);
                            ArrayList<String> elements = new ArrayList<>();
                            elements.add(path);
                            intent.putExtra(EXTRA_MESSAGE, elements);
                            startActivityForResult(intent, SHARE_PHOTO);
                        }else{
                            Intent intentVideo = new Intent(this, APartilharProjecto.class);
                            ArrayList<String> elements = new ArrayList<>();
                            elements.add(path);
                            intentVideo.putExtra("Name", projectName);
                            intentVideo.putExtra(EXTRA_MESSAGE, elements);
                            startActivityForResult(intentVideo, 1);
                        }
                    }
                    else{
                        Intent intent = new Intent(this, PhotoEdit.class);
                        intent.putExtra(EXTRA_MESSAGE, path);
                        startActivityForResult(intent, CONTINUE_TO_PHOTO_FILTER);
                    }
                } else {
                    Intent returnIntent = new Intent();
                    setResult(this.RESULT_CANCELED, returnIntent);
                    finish();
                }
                break;
            } //
            case RESULT_LOAD_IMAGE: {
                if (resultCode == RESULT_OK && (null != data || null != data.getClipData())) {
                    ArrayList<String> elements = new ArrayList<>();
                    if (data.getData() != null) {
                        //If uploaded with Android Gallery (max 1 image)
                        Uri selectedImage = data.getData();
                        String temp = getPathFromContent(selectedImage);
                        if (temp != null)
                            elements.add(temp);
                    } else {
                        //If uploaded with the new Android Photos gallery
                        ClipData clipData = data.getClipData();
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            ClipData.Item item = clipData.getItemAt(i);
                            Uri uri = item.getUri();
                            String temp = getPathFromContent(uri);
                            if (temp != null)
                                elements.add(temp);
                        }
                    }

                    if(projectName.equals("")) {
                        Intent intent1 = new Intent(this, AddToProject.class);
                        intent1.putExtra(EXTRA_MESSAGE, elements);
                        startActivityForResult(intent1, SHARE_PHOTO);
                    }else{
                        Intent intent = new Intent(this, APartilharProjecto.class);
                        intent.putExtra("Name", projectName);
                        intent.putExtra(EXTRA_MESSAGE, elements);
                        startActivityForResult(intent, 1);
                    }
                } else {
                    Intent returnIntent = new Intent();
                    setResult(this.RESULT_CANCELED, returnIntent);
                    finish();
                }
                break;
            }
            case SHARE_PHOTO: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> elements  = data.getStringArrayListExtra(EXTRA_MESSAGE);
                    projectName = data.getStringExtra("Name");
                    String descricao=data.getStringExtra("Descricao");
                    String disciplina=data.getStringExtra("Disciplina");
                    int idProjecto=data.getIntExtra("idProjecto", 1);
                    newProject=data.getBooleanExtra("newProject", false);
                    ArrayList<Integer> cooworker=data.getIntegerArrayListExtra("cooworker");

                    Intent intent = new Intent(this, APartilharProjecto.class);
                    intent.putExtra(EXTRA_MESSAGE, elements);
                    intent.putExtra("Descricao", descricao);
                    intent.putExtra("Disciplina", disciplina);
                    intent.putExtra("Name", projectName);
                    intent.putExtra("cooworker", cooworker);

                    intent.putExtra("newProject", newProject);
                    intent.putExtra("idProjecto", idProjecto);
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                    intent.putExtra("idOwner",Integer.parseInt(sp.getString("NMec",null)));

                    startActivityForResult(intent, 1);

                } else {
                    Intent returnIntent = new Intent();
                    setResult(this.RESULT_CANCELED, returnIntent);
                    finish();
                }
                break;
            } //
        }
        System.gc();
    }
}

