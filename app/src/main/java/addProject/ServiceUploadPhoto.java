package addProject;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import webentities.Image;
import webentities.ImageRegister;
import webentities.Project;
import webentities.ProjectRegister;
import webentities.ProjectRegisterResponse;
import webentities.UserCourse;


public class ServiceUploadPhoto extends IntentService {
    private ArrayList<String> descriptionList= new ArrayList<>();
    private ArrayList<String> listPhotosPath= new ArrayList<>();
    private String disciplina = "";
    private String descricao = "";
    private boolean newProject = true;
    private String photoCapaPath = "";
    private int idProjecto = -1;
    private String projectName = "";
    private int idOwner = -1;
    private ArrayList<Integer> cooworker;



    public ServiceUploadPhoto() {
        super("Service upload");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        disciplina = intent.getStringExtra("disciplina");
        descricao = intent.getStringExtra("descricao");
        newProject = intent.getBooleanExtra("newProject", true);
        photoCapaPath = intent.getStringExtra("photoCapa");
        listPhotosPath = intent.getStringArrayListExtra("listPhotos");
        descriptionList = intent.getStringArrayListExtra("descriptionList");
        idProjecto = intent.getIntExtra("idProjecto", 1);
        projectName = intent.getStringExtra("projectName");
        idOwner = intent.getIntExtra("idOwner", 0);
        cooworker=intent.getIntegerArrayListExtra("cooworker");

        if (newProject) {
            ProjectRegisterResponse p = createProject();
            if(p!=null)
                uploadData(p.getProjectid());
        } else {
            uploadData(idProjecto);
        }
    }


    private ProjectRegisterResponse createProject() {

        Project p = new Project(idOwner, projectName, descricao);

        List<String> listTags = new ArrayList<String>();
        listTags.add("UA");


        UserCourse uc = new UserCourse(disciplina);

        ProjectRegister pr = new ProjectRegister(p);
        pr.setListTags(listTags);
        pr.setUserCourse(uc);
        pr.setListCoworker(cooworker);

        Gson gson = new GsonBuilder().create();
        String message = gson.toJson(pr);

        URL url;
        HttpURLConnection urlConnection = null;

        try {

            StringBuilder output = new StringBuilder();

            url = new URL("http://192.168.160.32:8080/cmAndroid/webresources/projectRegister");

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setConnectTimeout(20000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("charset", "utf-8");
            //urlConnection.setRequestProperty("Content-Length", "" + Integer.toString(message.getBytes().length));

            urlConnection.connect();


            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            out.write(message);
            out.close();


            InputStream is;
            if (urlConnection.getResponseCode() == 200) {

                is = urlConnection.getInputStream();

                int n = 1;
                while (n > 0) {

                    byte[] b = new byte[4096];

                    n = is.read(b);

                    if (n > 0)
                        output.append(new String(b, 0, n));
                }
            }

            ProjectRegisterResponse prr = gson.fromJson(output.toString(), ProjectRegisterResponse.class);

            urlConnection.disconnect();
            return prr;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void uploadData(int idProject) {
        String name = "";
        List<Image> imageList = new ArrayList<Image>();
        if (newProject) {
            int i;
            for (i = 0; i < listPhotosPath.size(); i++) {
                name = listPhotosPath.get(i).substring(listPhotosPath.get(i).lastIndexOf("/") + 1);
                if (listPhotosPath.get(i).contains("jpg") || listPhotosPath.get(i).contains("png")) {
                    imageList.add(new Image(idProject, getImageToString(listPhotosPath.get(i)), name, descriptionList.get(i), 2));
                    descriptionList.remove(i);
                    break;
                }
            }
            listPhotosPath.remove(i);
            imageList.add(new Image(idProject, getImageToString(photoCapaPath), name, "", 1));
        }

        for (int i = 0; i < listPhotosPath.size(); i++) {
            String s = listPhotosPath.get(i);
            name = s.substring(s.lastIndexOf("/") + 1);
            if (s.contains("jpg") || s.contains("png")) {
                if(!descriptionList.get(i).equals("Escrever Legenda..."))
                    imageList.add(new Image(idProject, getImageToString(listPhotosPath.get(i)), name, descriptionList.get(i), 0));
                else
                    imageList.add(new Image(idProject, getImageToString(listPhotosPath.get(i)), name, "", 0));
            }
        }

        ImageRegister imageRegister = new ImageRegister(imageList);

        Gson gson = new GsonBuilder().create();
        String message = gson.toJson(imageRegister);


        URL url;
        HttpURLConnection urlConnection = null;

        try {

            StringBuilder output = new StringBuilder();

            url = new URL("http://192.168.160.32:8080/cmAndroid/webresources/imageRegister");

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setConnectTimeout(20000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("charset", "utf-8");
            //urlConnection.setRequestProperty("Content-Length", "" + Integer.toString(message.getBytes().length));

            urlConnection.connect();

            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            out.write(message);
            out.close();


            InputStream is;
            if (urlConnection.getResponseCode() == 200) {

                is = urlConnection.getInputStream();

                int n = 1;
                while (n > 0) {

                    byte[] b = new byte[4096];

                    n = is.read(b);

                    if (n > 0)
                        output.append(new String(b, 0, n));
                }
            }

            ImageRegister prr = gson.fromJson(output.toString(), ImageRegister.class);

            urlConnection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getImageToString(String photoPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
}