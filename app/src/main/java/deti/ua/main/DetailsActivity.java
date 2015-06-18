package deti.ua.main;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import addProject.LoadProjectCamera;
import webentities.Image;
import webentities.ListImages;
import webentities.Project;


public class DetailsActivity extends AppCompatActivity {
    String projectName="";
    static int projectID;
    static List<Image> lista = new ArrayList<>();

    static SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        FrameLayout frame = (FrameLayout) findViewById(R.id.frame_container);
        Bundle extras = getIntent().getExtras();

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        projectID = getIntent().getIntExtra("projID", -1);
        String title = getIntent().getStringExtra("title");
        String user = getIntent().getStringExtra("user");
        String categoria = getIntent().getStringExtra("cat");
        String descrip = getIntent().getStringExtra("descrp");
        String views = getIntent().getStringExtra("views");
        String ownerID = getIntent().getStringExtra("ownerID");

        try {
            getInfoProject();
        } catch (Exception e) {
            e.printStackTrace();
        }


        TextView userTextView = (TextView) findViewById(R.id.userNameProj);
        userTextView.setText(user);

        TextView cat = (TextView) findViewById(R.id.categoria);
        cat.setText(categoria);


        RelativeLayout layout = (RelativeLayout)findViewById(R.id.gallery_layout);
        TextView description = new TextView(this);
        description.setTextSize(25);
        description.setGravity(Gravity.CENTER);
        description.setText(descrip);

        RelativeLayout.LayoutParams paramsDescr = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsDescr.topMargin = 20;
        paramsDescr.bottomMargin = 15;
        layout.addView(description,paramsDescr);

        ImageView[] iv_album = new ImageView[lista.size()];
        TextView[] tv_album = new TextView[lista.size()];
        for (int i = 0; i < lista.size(); i++) {
            Image img = lista.get(i);

            byte[] decodedString = Base64.decode(img.getImageData(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            iv_album[i] = new ImageView(this);
            iv_album[i].setImageBitmap(bitmap);
            iv_album[i].setId(i + 1);
            iv_album[i].setAdjustViewBounds(true);

            if(i == 0){
                params2.topMargin = 120;
            }else {

                params2.topMargin = 5;
            }

            params2.leftMargin = 16;
            params2.rightMargin = 16;

            tv_album[i] = new TextView(this);
            tv_album[i].setText(img.getDescription());
            tv_album[i].setId(i + 2);
            tv_album[i].setTextSize(25);
            tv_album[i].setGravity(Gravity.CENTER);
            params.topMargin = 20;
            params.bottomMargin = 15;
            params.leftMargin = 16;
            params.rightMargin = 16;
            params.addRule(RelativeLayout.CENTER_IN_PARENT);


            if (i > 0) {
                params2.addRule(RelativeLayout.BELOW, tv_album[i - 1].getId());
                iv_album[i].setId(tv_album[i - 1].getId()+1);
                tv_album[i].setId(tv_album[i - 1].getId() + 2);
            }
            params.addRule(RelativeLayout.BELOW, iv_album[i].getId());

            layout.addView(iv_album[i],params2);
            layout.addView(tv_album[i],params);

        }



        //set the back arrow in the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle(title);
        //getSupportActionBar().setHomeButtonEnabled(false);


        if (savedInstanceState == null) {

            String nMec = sp.getString("NMec","Not Found");
            if(ownerID.equals(nMec)) {
                createButton();
            }
            projectName=title;
        }
    }


    private void getInfoProject () throws Exception {


        (new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Log.i("SendAnswer", "Estou aqui pah");
                Project proj = new Project(projectID);


                Gson gson = new GsonBuilder().create();

                URL url;
                HttpURLConnection urlConnection;

                String message = gson.toJson(proj);

                Log.i("SendAnswer", message);

                try {

                    StringBuilder output = new StringBuilder();

                    url = new URL("http://192.168.160.32:8080/cmAndroid/webresources/loadImages");

                    urlConnection = (HttpURLConnection) url.openConnection();

                    urlConnection.setDoOutput(true);
                    urlConnection.setDoInput(true);
                    urlConnection.setConnectTimeout(20000);
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setRequestProperty("charset", "utf-8");
                    //urlConnection.setRequestProperty("Content-Length", "" + Integer.toString(message.getBytes().length));

                    urlConnection.connect();

                    OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                    out.write(message);
                    out.close();

                    Log.i("SendAnswer", "Mensagem enviada");

                    InputStream is;

                    if (urlConnection.getResponseCode() == 200) {

                        is = urlConnection.getInputStream();

    					/* obtem a resposta do pedido */
                        int n = 1;
                        while (n > 0) {

                            byte[] b = new byte[4096];

                            n = is.read(b);

                            if (n > 0)
                                //ListProjects
                                output.append(new String(b, 0, n));
                        }
                    }
                    ListImages listImages = gson.fromJson(output.toString(), ListImages.class);
                    Log.i("SendAnswer", output.toString());

                    lista = listImages.getListImages();

                    Log.i("SIZE", String.valueOf(lista.size()));

                    urlConnection.disconnect();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }).execute().get(7000, TimeUnit.MILLISECONDS);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.home){
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void buttonClicked(int x) {

        Intent intent = new Intent(this, LoadProjectCamera.class);
        intent.putExtra("Option",x);
        intent.putExtra("Name",projectName);

        startActivity(intent);
    }
    private void createButton() {
        // Set up the white button on the lower right corner
        // more or less with default parameter
        final ImageView fabIconNew = new ImageView(this);
        fabIconNew.setImageDrawable(getResources().getDrawable(R.drawable.ic_action));
        final FloatingActionButton rightLowerButton = new FloatingActionButton.Builder(this)
                .setContentView(fabIconNew)
                .setBackgroundDrawable(R.drawable.button_action_red_selector)
                .build();

        SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(this);
        ImageView rlIcon1 = new ImageView(this);
        ImageView rlIcon2 = new ImageView(this);
        ImageView rlIcon3 = new ImageView(this);

        rlIcon1.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_photo181));
        rlIcon2.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_video163));
        rlIcon3.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_upload));

        // Build the menu with default options: light theme, 90 degrees, 72dp radius.
        // Set 4 default SubActionButtons
        int subActionButtonSize = 225;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(subActionButtonSize, subActionButtonSize);
        rLSubBuilder.setLayoutParams(params);
        final FloatingActionMenu rightLowerMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(rLSubBuilder.setContentView(rlIcon1).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon2).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon3).build())
                .attachTo(rightLowerButton)
                .build();


        rlIcon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClicked(1);        }
        });
        rlIcon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClicked(2);        }
        });
        rlIcon3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClicked(3);        }
        });
        // Listen menu open and close events to animate the button content view
        rightLowerMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees clockwise
                fabIconNew.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees counter-clockwise
                fabIconNew.setRotation(45);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }
        });
    }

}
