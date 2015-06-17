package deti.ua.divulgarua;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
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
import webentities.ProjectOrderType;
import webentities.RequestListProjects;


public class SearchResultsActivity extends AppCompatActivity {

    private GridView gridView;
    private GridViewAdapter gridAdapter;
    static List<Project> lista = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //set the back arrow in the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        gridView = (GridView) findViewById(R.id.gridViewSearch);

        handleIntent(getIntent());



        gridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, getData());
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ImageItem item = (ImageItem) parent.getItemAtPosition(position);
                //Create intent
                Intent intent = new Intent(SearchResultsActivity.this, DetailsActivity.class);
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




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_results, menu);
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

        if (id == R.id.home){
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.i("SEARCH", query);
            try {
                getListProjects(query);
            } catch (Exception e) {
                e.printStackTrace();
            }



        }
    }

    private void showResults(String query) {
        // Query your data set and show results
        Log.i("SEARCHAA",query);
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

            imageItems.add(new ImageItem(bitmap, proj.getName(),proj.getCreatedDate(),proj.getnViews(),String.valueOf(proj.getOwnerID()),proj.getProjectID(),proj.getCourseName(),proj.getDescription()));
        }
        return imageItems;
    }


    private void getListProjects (String query) throws Exception {

        final String querySearch = query;
        (new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Log.i("SendAnswer", "Estou aqui pah");
                RequestListProjects list = new RequestListProjects(ProjectOrderType.SERCH, querySearch);


                Gson gson = new GsonBuilder().create();

                URL url;
                HttpURLConnection urlConnection = null;

                String message = gson.toJson(list);

                Log.i("SendAnswer", message);

                try {

                    StringBuilder output = new StringBuilder();

                    url = new URL("http://192.168.160.32:8080/cmAndroid/webresources/sendProjectBy");

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

                    urlConnection.disconnect();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }).execute().get(10000, TimeUnit.MILLISECONDS);



    }



}

