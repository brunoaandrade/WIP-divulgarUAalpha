package addProject;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import deti.ua.main.R;


public class PhotoEdit extends Activity  {
    int effectType;
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private TextureRenderer mTexRenderer = new TextureRenderer();
    private String photoToEdit = null;
    int mCurrentEffect;
    Bitmap bitmapOriginal;
    Bitmap temp;
    ImageView imageView;
    final int PIC_CROP = 2;
    Matrix matrix ;
    boolean rotateleft=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_edit);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            photoToEdit = extras.getString("Message");
        }
        addListenerOnButton();
        File imgFile = new File(photoToEdit);
        bitmapOriginal = decodeFile(imgFile);
        imgFile.delete();

        imageView = (ImageView) findViewById(R.id.imageView1);



        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        if(bitmapOriginal.getHeight()<bitmapOriginal.getWidth() && display.getRotation()==Surface.ROTATION_0) {
            matrix = new Matrix();
            matrix.postRotate(90);
            bitmapOriginal = Bitmap.createBitmap(bitmapOriginal, 0, 0, bitmapOriginal.getWidth(), bitmapOriginal.getHeight(), matrix, true);
        }

        imageView.setImageBitmap(bitmapOriginal);

    }

    @Override
    public void onBackPressed() {
        handleBack();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen for landscape and portrait and set portrait mode always
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
            int orientation = display.getRotation();

            if(orientation== Surface.ROTATION_90)
            {
                rotateleft=true;
                matrix = new Matrix();
                matrix.postRotate(-90);
                bitmapOriginal = Bitmap.createBitmap(bitmapOriginal, 0, 0, bitmapOriginal.getWidth(), bitmapOriginal.getHeight(), matrix, true);
                imageView.setImageBitmap(bitmapOriginal);
            }
            else {
                rotateleft=false;
                matrix = new Matrix();
                matrix.postRotate(90);
                bitmapOriginal = Bitmap.createBitmap(bitmapOriginal, 0, 0, bitmapOriginal.getWidth(), bitmapOriginal.getHeight(), matrix, true);
                imageView.setImageBitmap(bitmapOriginal);
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            matrix = new Matrix();
            if (rotateleft)
                matrix.postRotate(90);
            else
                matrix.postRotate(-90);
            bitmapOriginal = Bitmap.createBitmap(bitmapOriginal, 0, 0, bitmapOriginal.getWidth(), bitmapOriginal.getHeight(), matrix, true);
            imageView.setImageBitmap(bitmapOriginal);
        }


    }

    private void handleContinue() {
        galleryAddPic();
        recycle();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("path", photoToEdit);
        setResult(RESULT_OK, returnIntent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_photo_edit, menu);
        menu.findItem(R.id.nextBut).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.nextBut:
                handleContinue();
                return true;
            case android.R.id.home:
                handleBack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void handleBack() {
        recycle();
        Intent returnIntent = new Intent();
        setResult(this.RESULT_CANCELED, returnIntent);
        finish();
    }


    // Salvar imagem no album
    private void galleryAddPic() {
        photoToEdit = photoToEdit.replaceFirst(JPEG_FILE_SUFFIX, "_edited" + JPEG_FILE_SUFFIX);

        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(photoToEdit);
        photoToEdit = f.getAbsolutePath();
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
        try {
            FileOutputStream fOut = new FileOutputStream(f);
            bitmapOriginal.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addListenerOnButton() {
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.barValue);
        mainLayout.setVisibility(LinearLayout.GONE);
        mainLayout = (LinearLayout) findViewById(R.id.acepetCancel);
        mainLayout.setVisibility(LinearLayout.GONE);
        mainLayout = (LinearLayout) findViewById(R.id.rotateImage);
        mainLayout.setVisibility(LinearLayout.GONE);
        ImageButton imageButton;
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                LinearLayout mainLayout = (LinearLayout) findViewById(R.id.graph_fragment_holder);
                mainLayout.setVisibility(LinearLayout.GONE);
                mainLayout = (LinearLayout) findViewById(R.id.barValue);
                mainLayout.setVisibility(LinearLayout.VISIBLE);
                mainLayout = (LinearLayout) findViewById(R.id.acepetCancel);
                mainLayout.setVisibility(LinearLayout.VISIBLE);
                effectType = 3;
                SeekBar sk = (SeekBar) findViewById(R.id.seek1);
                sk.setProgress(50);
            }
        });
        imageButton = (ImageButton) findViewById(R.id.imageButton2);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                LinearLayout mainLayout = (LinearLayout) findViewById(R.id.graph_fragment_holder);
                mainLayout.setVisibility(LinearLayout.GONE);

                ///////rotate
                mainLayout = (LinearLayout) findViewById(R.id.acepetCancel);
                mainLayout.setVisibility(LinearLayout.VISIBLE);
                mainLayout = (LinearLayout) findViewById(R.id.rotateImage);
                mainLayout.setVisibility(LinearLayout.VISIBLE);

            }
        });
        imageButton = (ImageButton) findViewById(R.id.imageButton3);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                LinearLayout mainLayout = (LinearLayout) findViewById(R.id.graph_fragment_holder);
                mainLayout.setVisibility(LinearLayout.GONE);
                mainLayout = (LinearLayout) findViewById(R.id.rotateImage);
                mainLayout.setVisibility(LinearLayout.GONE);
                mainLayout = (LinearLayout) findViewById(R.id.barValue);
                mainLayout.setVisibility(LinearLayout.VISIBLE);
                mainLayout = (LinearLayout) findViewById(R.id.acepetCancel);
                mainLayout.setVisibility(LinearLayout.VISIBLE);
                effectType = 4;
            }
        });
        imageButton = (ImageButton) findViewById(R.id.imageButton4);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                LinearLayout mainLayout = (LinearLayout) findViewById(R.id.graph_fragment_holder);
                mainLayout.setVisibility(LinearLayout.GONE);

                ///////
                performCrop();


            }
        });
        imageButton = (ImageButton) findViewById(R.id.buttonCancel);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                LinearLayout mainLayout = (LinearLayout) findViewById(R.id.barValue);
                mainLayout.setVisibility(LinearLayout.GONE);
                mainLayout = (LinearLayout) findViewById(R.id.acepetCancel);
                mainLayout.setVisibility(LinearLayout.GONE);
                mainLayout = (LinearLayout) findViewById(R.id.rotateImage);
                mainLayout.setVisibility(LinearLayout.GONE);
                mainLayout = (LinearLayout) findViewById(R.id.graph_fragment_holder);
                mainLayout.setVisibility(LinearLayout.VISIBLE);

                //cancel
                imageView.setImageBitmap(bitmapOriginal);
            }
        });
        imageButton = (ImageButton) findViewById(R.id.buttonAcepet);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                LinearLayout mainLayout = (LinearLayout) findViewById(R.id.barValue);
                mainLayout.setVisibility(LinearLayout.GONE);
                mainLayout = (LinearLayout) findViewById(R.id.acepetCancel);
                mainLayout.setVisibility(LinearLayout.GONE);
                mainLayout = (LinearLayout) findViewById(R.id.rotateImage);
                mainLayout.setVisibility(LinearLayout.GONE);
                mainLayout = (LinearLayout) findViewById(R.id.graph_fragment_holder);
                mainLayout.setVisibility(LinearLayout.VISIBLE);
                SeekBar sk = (SeekBar) findViewById(R.id.seek1);

                //Accepet
                bitmapOriginal = temp;
                temp = null;

                sk.setProgress(0);
                System.gc();
            }
        });
        imageButton = (ImageButton) findViewById(R.id.rotateHorizontal);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                effectType = 5;
                temp = null;
                //rotateHorizontal
                flip(true, false);
                imageView.setImageBitmap(temp);
                System.gc();
            }
        });
        imageButton = (ImageButton) findViewById(R.id.rotateVertical);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                temp = null;
                effectType = 6;
                //rotateVertical
                flip(false, false);
                imageView.setImageBitmap(temp);
                System.gc();
            }
        });
        imageButton = (ImageButton) findViewById(R.id.rotateHalf);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                temp = null;
                effectType = 7;
                //rotateHalf
                rotate(90, false);
                imageView.setImageBitmap(temp);
                System.gc();

            }
        });
        final SeekBar sk = (SeekBar) findViewById(R.id.seek1);
        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                temp = null;
                if (effectType == 3)
                    changeBitmapContrastBrightness(bitmapOriginal, 1, (float) (sk.getProgress() - 50) * 5.1f, false);
                else
                    changeBitmapContrastBrightness(bitmapOriginal, (float) sk.getProgress() / 10, 0, false);
                imageView.setImageBitmap(temp);
                System.gc();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void rotate(int degrees, boolean hightQuality) {
        matrix = new Matrix();
        matrix.setRotate(degrees);
        int w = bitmapOriginal.getWidth();
        int h = bitmapOriginal.getHeight();

        temp = Bitmap.createBitmap(bitmapOriginal, 0, 0, bitmapOriginal.getWidth(), bitmapOriginal.getHeight(), matrix, false);
    }


    private void flip(boolean horizontal, boolean hightQuality) {
        matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);
        if (!horizontal) {
            matrix.preScale(1.0f, -1.0f);
        }

        temp = Bitmap.createBitmap(bitmapOriginal, 0, 0, bitmapOriginal.getWidth(), bitmapOriginal.getHeight(), matrix, false);
    }

    private void changeBitmapContrastBrightness(Bitmap bmp, float contrast, float brightness, boolean hightQuality) {
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, brightness,
                        0, contrast, 0, 0, brightness,
                        0, 0, contrast, 0, brightness,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        temp = ret;
        System.gc();
    }

    private Bitmap decodeFile(File f) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE = 1600;

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


    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, "Title", null);
        System.gc();
        return Uri.parse(path);
    }

    private void performCrop() {
        try {
            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(getImageUri(bitmapOriginal), "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", bitmapOriginal.getWidth());
            cropIntent.putExtra("aspectY", bitmapOriginal.getHeight());
            //indicate output X and Y
            cropIntent.putExtra("outputX", bitmapOriginal.getWidth());
            cropIntent.putExtra("outputY", bitmapOriginal.getHeight());

            File f = new File(Environment.getExternalStorageDirectory(),
                    "/temporary_holder.jpg");
            try {
                f.createNewFile();
            } catch (IOException ex) {
                Log.e("io", ex.getMessage());
            }

            Uri uri = Uri.fromFile(f);

            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

            startActivityForResult(cropIntent, PIC_CROP);

        } //respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            //display an error message
            String errorMessage = "Your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PIC_CROP) {
            if(resultCode==-1) {
                String filePath = Environment.getExternalStorageDirectory()
                        + "/temporary_holder.jpg";

                bitmapOriginal = BitmapFactory.decodeFile(filePath);
                File f = new File(filePath);
                f.delete();
                LinearLayout mainLayout = (LinearLayout) findViewById(R.id.graph_fragment_holder);
                mainLayout.setVisibility(LinearLayout.VISIBLE);
                imageView.setImageBitmap(bitmapOriginal);
            }
            else{
                LinearLayout mainLayout = (LinearLayout) findViewById(R.id.graph_fragment_holder);
                mainLayout.setVisibility(LinearLayout.VISIBLE);
            }
        }
        else if (requestCode == 1) {
            if (resultCode == this.RESULT_OK) {
                String result = data.getStringExtra("result");
                Intent returnIntent = new Intent();
                returnIntent.putExtra(result, true);
                setResult(this.RESULT_OK, returnIntent);
                finish();
            }
            if (resultCode == this.RESULT_CANCELED) {
                recycle();
                Intent returnIntent = new Intent();
                setResult(this.RESULT_CANCELED, returnIntent);
                finish();
            }
        }
    }

    private void recycle() {
        if (bitmapOriginal != null)
            bitmapOriginal.recycle();
        ImageView imageView = null;
        mTexRenderer = null;
        System.gc();
    }

    public void onDestroy() {
        super.onDestroy();
    }
}

