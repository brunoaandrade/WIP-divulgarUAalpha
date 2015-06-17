package deti.ua.divulgarua;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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


/**
 * Created by LuisAfonso on 13-05-2015.
 */
public class TabRecentes extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private GridView gridView;
    private SwipeRefreshLayout mSwipeLayout;
    private static GridViewAdapter gridAdapter;
    static List<Project> lista = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_recentes,container,false);

        gridView = (GridView) v.findViewById(R.id.gridView);

        try {
            getListProjects();
        } catch (Exception e) {
            e.printStackTrace();
        }


        Log.i("", "'HEY");
        gridAdapter = new GridViewAdapter(getActivity(), R.layout.grid_item_layout, getData());
        gridView.setAdapter(gridAdapter);
        Log.i("", "'DONE");

        mSwipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ImageItem item = (ImageItem) parent.getItemAtPosition(position);
                //Create intent
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
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

        return v;
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
    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i("", "'SUP");
                gridAdapter.clear();
                try {
                    getListProjects();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                gridAdapter.addAll(getData());
                mSwipeLayout.setRefreshing(false);
            }
        }, 5000);
    }

    private void getListProjects () throws Exception {


        (new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Log.i("SendAnswer", "Estou aqui pah");
                RequestListProjects list = new RequestListProjects(ProjectOrderType.DATE, "");


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
