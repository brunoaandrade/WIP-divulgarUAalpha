package deti.ua.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import webentities.ListProjects;
import webentities.Project;
import webentities.User;


public class ProfileActivity extends AppCompatActivity {

    static SharedPreferences sp;
    ImageButton imageButton;
    private GridView gridView;
    private GridViewAdapter gridAdapter;
    static List<Project> lista = new ArrayList<>();
    static int nmec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        sp = PreferenceManager.getDefaultSharedPreferences(this);

        Intent intent = getIntent();

        String profileID = intent.getStringExtra("profileID");
        Log.i("E", profileID);

        if(profileID.contains("self")){
            Log.i("E", "now");
            String fullname = sp.getString("Nome","Not Found");
            TextView name = (TextView) findViewById(R.id.profile_name);
            TextView yStudies = (TextView) findViewById(R.id.profile_yearStudies);
            Log.i("E", fullname);
            name.setText(fullname);
            yStudies.setText("Ano " + sp.getString("AnoCurricular", "year not found"));
            Log.i("E", sp.getString("NMec", "Not Found"));
            nmec = Integer.parseInt(sp.getString("NMec", "-1"));

            if(fullname != null) {
                getSupportActionBar().setTitle(fullname);
            }

        }

        try {
            getListProjects();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ImageView profImgAct  = (ImageView) findViewById(R.id.profImgAct);

        String photo = sp.getString("Foto", "Not Found");


        // decode user image
        byte[] decodedString = Base64.decode(photo, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Log.i("SIZE",String.valueOf(bitmap.getWidth()));
        Log.i("SIZE", String.valueOf(bitmap.getHeight()));


                BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setShader(shader);


        Canvas c = new Canvas(circleBitmap);
        c.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth()/2, paint);



        profImgAct.setImageBitmap(circleBitmap);

        imageButton = (ImageButton) findViewById(R.id.editProfile);



        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        gridView = (GridView) findViewById(R.id.gridView);

        gridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, getData());
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ImageItem item = (ImageItem) parent.getItemAtPosition(position);
                //Create intent
                Intent intent = new Intent(ProfileActivity.this, DetailsActivity.class);
                intent.putExtra("projID", item.getProjectID());
                intent.putExtra("title", item.getTitle());
                intent.putExtra("user", item.getUser());
                intent.putExtra("cat", item.getProjectCat());
                intent.putExtra("descrp", item.getProjectDescrp());
                intent.putExtra("views", item.getViews());


                //Start details activity
                startActivity(intent);
            }
        });


        //set the back arrow in the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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

    /**
     * Prepare some dummy data for gridview
     */
    private ArrayList<ImageItem> getData() {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();

        for (int i = 0; i < lista.size(); i++) {
            Project proj = lista.get(i);
            proj.getProjectID();
            Log.i("SIZE", proj.getName());
            byte[] decodedString = Base64.decode(proj.getCapeImage(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            imageItems.add(new ImageItem(bitmap, proj.getName(),proj.getCreatedDate(),proj.getnViews(),proj.getAuthorName(),proj.getProjectID(),proj.getCourseName(), proj.getDescription(),String.valueOf(proj.getOwnerID())));
        }
        return imageItems;
    }

    private void getListProjects () throws Exception {


        (new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Log.i("SendAnswer", "Estou aqui pah");
                User user = new User(nmec);



                Gson gson = new GsonBuilder().create();

                URL url;
                HttpURLConnection urlConnection;

                String message = gson.toJson(user);

                Log.i("SendAnswer", message);

                try {

                    StringBuilder output = new StringBuilder();

                    url = new URL("http://192.168.160.32:8080/cmAndroid/webresources/generic");

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
                    ListProjects listProjects = gson.fromJson(output.toString(), ListProjects.class);
                    Log.i("SendAnswer", output.toString());

                    lista = listProjects.getListProject();

                    Log.i("SIZE", String.valueOf(lista.size()));


                    Log.i("SIZE", String.valueOf(lista.size()));

                    urlConnection.disconnect();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }).execute().get(3000, TimeUnit.MILLISECONDS);



    }


}
