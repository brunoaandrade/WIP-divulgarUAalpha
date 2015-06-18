package addProject;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.media.effect.EffectFactory;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLException;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import deti.ua.main.R;


public class PhotoFilter extends Activity  implements GLSurfaceView.Renderer {

    final String[] names = {"Normal", "Auto Fix ", "Color Intensity", "Cross Process ", " Documentary ", " Duo Tone ", "Fill Light ", "Fish Eye ", " Grain ", "Grayscale ", "Lomoish ", "Negative ", " Posterize", " Saturate ", " Sepia ", " Sharpen ", " Temperature ", " Tint ", " Vignett"};
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private TextureRenderer mTexRenderer = new TextureRenderer();
    private String phtoToEdit = null;
    private GLSurfaceView mEffectView;
    private int[] mTextures = new int[2];
    private EffectContext mEffectContext;
    private boolean mInitialized = false;
    private FilterGrid gridAdapter;
    private GridView gridView;
    private Effect mEffect;
    private int mImageWidth;
    private int mImageHeight;
    int mCurrentEffect;
    Bitmap bitmap;
    Bitmap bitmapOriginal;
    Bitmap photo;
    File imgFile;
    int deformation=0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_filter);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            phtoToEdit = extras.getString("Message");
        }
        imgFile = new File(phtoToEdit);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        bitmap = BitmapFactory.decodeFile(phtoToEdit, options);


        mEffectView = (GLSurfaceView) findViewById(R.id.effectsview1);
        mEffectView.setEGLContextClientVersion(2);
        mEffectView.setRenderer(this);
        mEffectView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);


        gridView = (GridView) findViewById(R.id.gridView);
        gridAdapter = new FilterGrid(this, R.layout.activity_filter_grid, getData());
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                FilterItem item = (FilterItem) parent.getItemAtPosition(position);
                int pos = 0;
                for (int i = 0; i < names.length; i++) {
                    if (names[i] == item.getTitle())
                        pos = i;
                }
                setCurrentEffect(pos);
                mEffectView.requestRender();
            }
        });
    }

    @Override
    public void onBackPressed() {
        handleBack();
    }

    private void loadTextures() {
        // Generate textures
        GLES20.glGenTextures(2, mTextures, 0);

        // Load input bitmap
        bitmapOriginal = bitmap;
        if (bitmap.getWidth() > 5000 || bitmap.getHeight() > 2900) {
            mImageWidth = bitmap.getWidth() / 4;
            mImageHeight = bitmap.getHeight() / 4;
            deformation=4;
        } else if (bitmap.getWidth() > 2500 || bitmap.getHeight() > 1400) {
            mImageWidth = bitmap.getWidth() / 2;
            mImageHeight = bitmap.getHeight() / 2;
            deformation=2;
        } else {
            mImageWidth = bitmap.getWidth();
            mImageHeight = bitmap.getHeight();
            deformation=1;
        }
        mTexRenderer.updateTextureSize(mImageWidth, mImageHeight);

        // Upload to texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0]);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        // Set texture parameters
        GLToolbox.initTexParams();
    }

    public void setCurrentEffect(int effect) {
        mCurrentEffect = effect;
    }

    private void initEffect() {
        EffectFactory effectFactory = mEffectContext.getFactory();
        if (mEffect != null) {
            mEffect.release();
        }
        /**
         * Initialize the correct effect based on the selected menu/action item
         */
        switch (mCurrentEffect) {

            case 0:
                break;

            case 1:
                mEffect = effectFactory.createEffect(
                        EffectFactory.EFFECT_AUTOFIX);
                mEffect.setParameter("scale", 0.5f);
                break;

            case 2:
                mEffect = effectFactory.createEffect(
                        EffectFactory.EFFECT_BLACKWHITE);
                mEffect.setParameter("black", .1f);
                mEffect.setParameter("white", .7f);
                break;


            case 3:
                mEffect = effectFactory.createEffect(
                        EffectFactory.EFFECT_CROSSPROCESS);
                break;

            case 4:
                mEffect = effectFactory.createEffect(
                        EffectFactory.EFFECT_DOCUMENTARY);
                break;

            case 5:
                mEffect = effectFactory.createEffect(
                        EffectFactory.EFFECT_DUOTONE);
                mEffect.setParameter("first_color", Color.YELLOW);
                mEffect.setParameter("second_color", Color.DKGRAY);
                break;

            case 6:
                mEffect = effectFactory.createEffect(
                        EffectFactory.EFFECT_FILLLIGHT);
                mEffect.setParameter("strength", .8f);
                break;

            case 7:
                mEffect = effectFactory.createEffect(
                        EffectFactory.EFFECT_FISHEYE);
                mEffect.setParameter("scale", .5f);
                break;

            case 8:
                mEffect = effectFactory.createEffect(
                        EffectFactory.EFFECT_GRAIN);
                mEffect.setParameter("strength", 1.0f);
                break;

            case 9:
                mEffect = effectFactory.createEffect(
                        EffectFactory.EFFECT_GRAYSCALE);
                break;

            case 10:
                mEffect = effectFactory.createEffect(
                        EffectFactory.EFFECT_LOMOISH);
                break;

            case 11:
                mEffect = effectFactory.createEffect(
                        EffectFactory.EFFECT_NEGATIVE);
                break;

            case 12:
                mEffect = effectFactory.createEffect(
                        EffectFactory.EFFECT_POSTERIZE);
                break;

            case 13:
                mEffect = effectFactory.createEffect(
                        EffectFactory.EFFECT_SATURATE);
                mEffect.setParameter("scale", .5f);
                break;

            case 14:
                mEffect = effectFactory.createEffect(
                        EffectFactory.EFFECT_SEPIA);
                break;

            case 15:
                mEffect = effectFactory.createEffect(
                        EffectFactory.EFFECT_SHARPEN);
                break;

            case 16:
                mEffect = effectFactory.createEffect(
                        EffectFactory.EFFECT_TEMPERATURE);
                mEffect.setParameter("scale", .9f);
                break;

            case 17:
                mEffect = effectFactory.createEffect(
                        EffectFactory.EFFECT_TINT);
                mEffect.setParameter("tint", Color.MAGENTA);
                break;

            case 18:
                mEffect = effectFactory.createEffect(
                        EffectFactory.EFFECT_VIGNETTE);
                mEffect.setParameter("scale", .5f);
                break;

            default:
                break;

        }
    }

    private void applyEffect() {
        mEffect.apply(mTextures[0], mImageWidth, mImageHeight, mTextures[1]);
    }

    private void renderResult() {
        if (mCurrentEffect != 0) {
            // if no effect is chosen, just render the original bitmap
            mTexRenderer.renderTexture(mTextures[1]);
        } else {
            // render the result of applyEffect()
            mTexRenderer.renderTexture(mTextures[0]);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (!mInitialized) {
            //Only need to do this once
            mEffectContext = EffectContext.createWithCurrentGlContext();
            mTexRenderer.init();
            loadTextures();
            mInitialized = true;
            renderResult();
        } else if (mCurrentEffect != 0) {
            //if an effect is chosen initialize it and apply it to the texture
            initEffect();
            applyEffect();
            renderResult();
            createBitmapFromGLSurface(mImageHeight, mImageWidth, gl);
        } else if (mCurrentEffect == 0) {
            bitmap = bitmapOriginal;
            renderResult();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (mTexRenderer != null) {
            mTexRenderer.updateViewSize(width, height);
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    }

    private ArrayList<FilterItem> getData() {
        final ArrayList<FilterItem> imageItems = new ArrayList<>();
        TypedArray imgs = getResources().obtainTypedArray(R.array.image_ids1);
        for (int i = 0; i < names.length; i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgs.getResourceId(i, -1));
            imageItems.add(new FilterItem(bitmap, names[i]));
        }
        return imageItems;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void handleContinue() {
        if (mCurrentEffect != 0)
            galleryAddPic();

        Intent returnIntent = new Intent();
        returnIntent.putExtra("path", phtoToEdit);
        returnIntent.putExtra("nextFilter", true);
        recycle();
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

    //converte GLSURFACE para bitmap
    private void createBitmapFromGLSurface(int h, int w, GL10 gl) throws OutOfMemoryError {
        h*=deformation;
        w*=deformation;
        System.gc();
        int bitmapBuffer[] = new int[w * h];
        int bitmapSource[] = new int[w * h];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);

        try {
            double tmp;
            int extra;
            int differenceWidth = Math.abs(mEffectView.getWidth() - mImageWidth);
            int differenceHeight = Math.abs(mEffectView.getHeight() - mImageHeight);
            if (differenceWidth < differenceHeight) {
                tmp = (double) mEffectView.getWidth() / mImageWidth;
                tmp = mImageHeight * tmp;
                extra = (int) ((mEffectView.getHeight() - tmp) / 2);
                gl.glReadPixels(0, extra, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer);
            } else {
                tmp = (double) mEffectView.getHeight() / mImageHeight;
                tmp = mImageWidth * tmp;
                extra = (int) ((mEffectView.getWidth() - tmp) / 2);
                gl.glReadPixels(extra, 0, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer);
            }

            int offset1, offset2;
            for (int i = 0; i < h; i++) {
                offset1 = i * w;
                offset2 = (h - i - 1) * w;
                for (int j = 0; j < w; j++) {
                    int texturePixel = bitmapBuffer[offset1 + j];
                    int blue = (texturePixel >> 16) & 0xff;
                    int red = (texturePixel << 16) & 0x00ff0000;
                    int pixel = (texturePixel & 0xff00ff00) | red | blue;
                    bitmapSource[offset2 + j] = pixel;
                }
            }
        } catch (GLException e) {
            return;
        }
        photo = Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
    }

    // Salvar imagem no album
    private void galleryAddPic() {
        phtoToEdit = phtoToEdit.replaceFirst(JPEG_FILE_SUFFIX, "_edited" + JPEG_FILE_SUFFIX);

        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(phtoToEdit);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
        try {
            FileOutputStream fOut = new FileOutputStream(f);
            photo.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void recycle() {
        phtoToEdit = null;
        mEffectView = null;
        mEffectContext = null;
        gridAdapter = null;
        gridView.setAdapter(new ArrayAdapter(this, R.layout.activity_filter_grid, new ArrayList()));

        gridView = null;
        if (bitmap != null)
            bitmap.recycle();
        if (bitmapOriginal != null)
            bitmapOriginal.recycle();
        if (photo != null)
            photo.recycle();
        System.gc();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == this.RESULT_OK) {
                recycle();

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

    private void handleBack() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("path", phtoToEdit);
        returnIntent.putExtra("nextFilter", false);
        setResult(RESULT_OK, returnIntent);
        recycle();
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}