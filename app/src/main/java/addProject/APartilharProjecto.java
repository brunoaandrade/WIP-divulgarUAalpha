package addProject;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;

import org.askerov.dynamicgrid.DynamicGridView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

import deti.ua.divulgarua.MainActivity;
import deti.ua.divulgarua.R;

/**
 * Created by bruno on 30/05/2015.
 */
public class APartilharProjecto extends AppCompatActivity implements TextWatcher {
    private final int PIC_CROP=6;
    private final static String EXTRA_MESSAGE = "Message";
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private String mCurrentPhotoPath;
    private ArrayList<String> listPhotos = new ArrayList<>();
    private TextView textView;
    private String projectName = "";
    private ListAdapter gridAdapter;
    private DynamicGridView gridView;
    private ArrayList<String> descriptionList ;
    private ArrayList<Bitmap> pics;
    private int position;
    private boolean newProject=true;
    private String photoCapa;
    private String disciplina="";
    private String descricao="";
    private int idProjecto;
    private int idOwner;
    private ArrayList<Integer> cooworker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partilhar_project);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addListenerOnButton();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }

        Intent intent = getIntent();
        listPhotos = intent.getStringArrayListExtra(EXTRA_MESSAGE);
        projectName = intent.getStringExtra("Name");
        newProject= intent.getBooleanExtra("newProject", true);
        descricao=intent.getStringExtra("Descricao");
        cooworker=intent.getIntegerArrayListExtra("cooworker");
        disciplina=intent.getStringExtra("Disciplina");
        idProjecto=intent.getIntExtra("idProjecto",1);
        idOwner=intent.getIntExtra("idOwner",0);


        Iterator<String> i = listPhotos.iterator();
        while (i.hasNext()) {
            String s = i.next();
            if(!s.contains("jpg")&&!s.contains("png")&&!s.contains("avi")&&!s.contains("mp4"))
                i.remove();
        }

        addButonFacebook();
        textView = (TextView) findViewById(R.id.projectos_list);
        textView.setText(projectName);
        doThumbnail();
        createDynamicGrid();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        projectName = s.toString();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        projectName = s.toString();
    }

    @Override
    public void afterTextChanged(Editable s) {
        projectName = s.toString();

    }

    private void addListenerOnButton() {

        Button imageButton = (Button) findViewById(R.id.imageButtonInsta);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                handleShare("com.instagram.android");
            }
        });

        imageButton = (Button) findViewById(R.id.morePhotos);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                handleMorePhotos();
            }
        });

        imageButton = (Button) findViewById(R.id.twitter);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                handleShare("com.twitter.android");
            }
        });

        imageButton = (Button) findViewById(R.id.flirck);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                handleShare("com.yahoo.mobile.client.android.flickr");
            }
        });

        imageButton = (Button) findViewById(R.id.tumbler);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                handleShare("com.tumblr");
            }
        });

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.gc();
        switch (requestCode) {
            case 1: {
                if (resultCode == this.RESULT_OK) {
                    String result = data.getStringExtra("result");
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(result, true);
                    setResult(this.RESULT_OK, returnIntent);
                    finish();
                }
                if (resultCode == this.RESULT_CANCELED) {
                    Intent returnIntent = new Intent();
                    setResult(this.RESULT_CANCELED, returnIntent);
                    finish();
                }
            }
            case 3: {
                if (resultCode == RESULT_OK) {
                    galleryAddPic();
                    Intent intent = new Intent(this, PhotoEdit.class);
                    intent.putExtra(EXTRA_MESSAGE, mCurrentPhotoPath);
                    startActivityForResult(intent, 4);
                }
                break;
            }
            case 4: {
                if (resultCode == RESULT_OK && null != data) {
                    String path = data.getStringExtra("path");

                    Intent intent = new Intent(this, PhotoFilter.class);
                    intent.putExtra(EXTRA_MESSAGE, path);
                    startActivityForResult(intent, 5);
                }
                break;
            } //
            case 5: {
                if (resultCode == RESULT_OK) {
                    String path = data.getStringExtra("path");
                    Boolean nextFilter = data.getBooleanExtra("nextFilter", false);
                    if(nextFilter) {
                        listPhotos.add(path);
                        descriptionList.add("Escrever Legenda...");
                        pics.add(decodeFile(new File(path)));
                        updateGrid();
                    }
                    else{
                        Intent intent = new Intent(this, PhotoEdit.class);
                        intent.putExtra(EXTRA_MESSAGE, path);
                        startActivityForResult(intent, 4);
                    }
                }else {
                    Intent returnIntent = new Intent();
                    setResult(this.RESULT_CANCELED, returnIntent);
                    finish();
                }
                break;
            }
            case PIC_CROP:{
                endUpload();
            }
        }
    }

    private void handleMorePhotos() {

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
        startActivityForResult(takePictureIntent, 3);
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
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

    private String getAlbumName() {
        return getString(R.string.app_name);
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_to_project, menu);
        menu.findItem(R.id.nextBut).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.nextBut:
                handleContinue();
                return true;
            case R.id.cancelBut:
                handleCancel();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void handleCancel() {
        System.gc();
        Intent returnIntent = new Intent();
        setResult(this.RESULT_CANCELED, returnIntent);
        finish();
    }

    public void handleContinue() {
        boolean image=false;
        if(newProject) {
            Iterator<String> i = listPhotos.iterator();
            while (i.hasNext()) {
                String s = i.next();
                if (s.contains("jpg") || s.contains("png"))
                    image = true;
            }
            if (image) {
                Toast.makeText(getApplicationContext(), "Escolhe Area Para a Capa ", Toast.LENGTH_LONG).show();
                performCrop();
            } else {
                Toast.makeText(getApplicationContext(), "Tens de ter uma imagem para pores de Capa", Toast.LENGTH_LONG).show();
            }
        }
        else{
            endUpload();
        }
    }


    private void handleShare(String pack) {
        try {
            getPackageManager().getApplicationInfo(pack, 0);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            String s = projectName;
            shareIntent.putExtra(Intent.EXTRA_TEXT, s);

            String mediaPath = listPhotos.get(0);
            File media = new File(mediaPath);
            Uri uri = Uri.fromFile(media);

            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.setPackage(pack);
            startActivity(shareIntent);
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(getApplicationContext(), "Tens de ter a aplicacao instalada para partilhares!",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void addButonFacebook() {

        //Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.ic_tick);
        File imageFile = new File(listPhotos.get(0));
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();

        Bitmap image = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), bmOptions);
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        ShareButton shareButton = (ShareButton) findViewById(R.id.fb_share_button);
        shareButton.setShareContent(content);
    }


    private void changeDescription(String value) {
        descriptionList.set(position, value);
        updateGrid();
    }

    @Override
    public void onBackPressed() {
        if (gridView.isEditMode()) {
            gridView.stopEditMode();
        } else {
            System.gc();
        }
    }

    public void showAlert(int position){
        this.position=position;
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Legenda");
        alert.setMessage("Escrever Legenda da foto");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        input.setImeOptions(EditorInfo.IME_ACTION_DONE);
        if(!descriptionList.get(position).equals("Escrever Legenda..."))
            input.setText(descriptionList.get(position));
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                changeDescription(input.getText().toString());
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    private void updateGrid(){
        gridAdapter = null;
        gridAdapter=new CheeseDynamicAdapter(this, listPhotos ,1,descriptionList,pics);
        gridView.setAdapter(gridAdapter);
    }

    private void createDynamicGrid() {
        descriptionList= new ArrayList<String>(Collections.nCopies(listPhotos.size(), "Escrever Legenda..."));

        gridView = (DynamicGridView) findViewById(R.id.dynamic_grid);

        gridView.setNumColumns(1);
        gridAdapter=new CheeseDynamicAdapter(this,listPhotos,1,descriptionList,pics);
        gridView.setAdapter(gridAdapter);
        gridView.setOnDropListener(new DynamicGridView.OnDropListener() {
            @Override
            public void onActionDrop() {
                gridView.stopEditMode();
            }
        });
        gridView.setOnDragListener(new DynamicGridView.OnDragListener() {
            @Override
            public void onDragStarted(int position) {
            }

            @Override
            public void onDragPositionsChanged(int oldPosition, int newPosition) {
                String tmp = listPhotos.get(oldPosition);
                String tmp1 = descriptionList.get(oldPosition);
                Bitmap tmp2 = pics.get(oldPosition);

                listPhotos.set(oldPosition, listPhotos.get(newPosition));
                descriptionList.set(oldPosition, descriptionList.get(newPosition));
                pics.set(oldPosition, pics.get(newPosition));

                listPhotos.set(newPosition, tmp);
                descriptionList.set(newPosition, tmp1);
                pics.set(newPosition, tmp2);
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                gridView.startEditMode(position);
                return true;
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showAlert(position);
            }
        });
    }


    private void doThumbnail(){
        pics=new ArrayList<>();
        for(int i =0;i<listPhotos.size();i++)
            pics.add(decodeFile(new File(listPhotos.get(i))));
    }


    private Bitmap decodeFile(File f) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE = 60;

            // Find the correct scale value. It should be the power of 2.
            int scale = 2;
            while (o.outWidth / scale >= REQUIRED_SIZE &&
                    o.outHeight / scale >= REQUIRED_SIZE) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    private void performCrop() {
        try {
            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            Iterator<String> i = listPhotos.iterator();
            String s="";
            while (i.hasNext()) {
                s = i.next();
                if(s.contains("jpg") || s.contains("png"))
                    break;
            }
            Uri r=Uri.fromFile(new File(s));
            cropIntent.setDataAndType(r, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 500);
            cropIntent.putExtra("outputY", 500);

            File f = new File(Environment.getExternalStorageDirectory(),
                    "/temporary_holder.jpg");
            try {
                f.createNewFile();
            } catch (IOException ex) {
                Log.e("io", ex.getMessage());
            }
            photoCapa=f.getAbsolutePath();
            Uri uriCapa= Uri.fromFile(f);
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriCapa);
            startActivityForResult(cropIntent, PIC_CROP);

        }
        catch (ActivityNotFoundException anfe) {
            String errorMessage = "Your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void endUpload(){
        Intent myIntent = new Intent(Intent.ACTION_SYNC, null,this, ServiceUploadPhoto.class);
        myIntent.putExtra("descriptionList",descriptionList);
        myIntent.putExtra("listPhotos", listPhotos);
        myIntent.putExtra("disciplina",disciplina);
        myIntent.putExtra("descricao", descricao);
        myIntent.putExtra("newProject",newProject);
        myIntent.putExtra("photoCapa", photoCapa);
        myIntent.putExtra("idProjecto", idProjecto);
        myIntent.putExtra("projectName", projectName);
        myIntent.putExtra("idOwner", idOwner);
        myIntent.putExtra("cooworker", cooworker);

        startService(myIntent);


        try {
            Thread.sleep(1000);                 //1000 milliseconds is one second.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
