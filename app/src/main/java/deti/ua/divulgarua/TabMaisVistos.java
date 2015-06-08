package deti.ua.divulgarua;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Created by hp1 on 21-01-2015.
 */
public class TabMaisVistos extends Fragment {

    private GridView gridView;
    private GridViewAdapter gridAdapter;
    static List<Project> lista = new ArrayList<Project>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_recentes,container,false);

        gridView = (GridView) v.findViewById(R.id.gridView);

        try {
            getListProjects();
        } catch (Exception e) {
            e.printStackTrace();
        }

        gridAdapter = new GridViewAdapter(getActivity(), R.layout.grid_item_layout, getData());
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ImageItem item = (ImageItem) parent.getItemAtPosition(position);
                //Create intent
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra("title", item.getTitle());
                intent.putExtra("image", item.getImage());
                intent.putExtra("date", item.getDate());
                intent.putExtra("views", item.getViews());
                intent.putExtra("user", item.getUser());

                Log.i("LOG1",intent.getStringExtra("title"));

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
            Log.i("SIZE", proj.getName());
            byte[] decodedString = Base64.decode(proj.getCapeImage(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            imageItems.add(new ImageItem(bitmap, proj.getName(),proj.getCreatedDate(),0,String.valueOf(proj.getOwnerID()),proj.getProjectID()));
        }
        return imageItems;
    }

    private void getListProjects () throws Exception {


        (new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Log.i("SendAnswer", "Estou aqui pah");
                RequestListProjects list = new RequestListProjects(ProjectOrderType.VIEWS, "");


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
        }).execute().get(3000, TimeUnit.MILLISECONDS);



    }


}