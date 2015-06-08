package addProject;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

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

import deti.ua.divulgarua.R;
import webentities.ListProjects;
import webentities.Project;
import webentities.User;

public class AddToProject extends AppCompatActivity implements TextWatcher {
    private static final String EXTRA_MESSAGE = "Message";
    private AutoCompleteTextView textView;
    private Project_Additional_Information hello;
    private boolean oldProject;
    public String disciplina="";
    public String descricao="";
    public ArrayList<CharSequence> selectedUsersFinal = new ArrayList<>();
    public ArrayList<User> userList = new ArrayList<>();
    private boolean showOneTime = false;
    private String projectName="";
    private ArrayList<String> Projectos = new ArrayList<>();
    private ArrayList<Integer> ids=  new ArrayList<>();
    private boolean fragmentDisplay=false;
    private ArrayList<String> listPhotos;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_project);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        listPhotos = intent.getStringArrayListExtra(EXTRA_MESSAGE);
        textView = (AutoCompleteTextView) findViewById(R.id.projectos_list);
        getListProjects();
    }

    private void makeAutoCompleteView() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, Projectos);
        textView = (AutoCompleteTextView) findViewById(R.id.projectos_list);
        textView.setAdapter(adapter);
        textView.addTextChangedListener(this);
        textView.setOnEditorActionListener(
                new AutoCompleteTextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if ((actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                                event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER ) &&projectName.length()>2) {
                            tv=v;
                            onDoneAction(v);
                            return true;
                        }
                        else if(projectName.length()<3)
                            Toast.makeText(getApplicationContext(), "O projecto tem de ter mais de 3 caracteres",Toast.LENGTH_LONG).show();

                        return false;
                    }
                });
        textView.setImeOptions(EditorInfo.IME_ACTION_DONE);
    }

    @Override
    public void onBackPressed() {
        System.gc();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        projectName = s.toString();
    }

    private void onDoneAction(TextView v) {

        oldProject = Projectos.contains(projectName);

        if (!oldProject&&!fragmentDisplay) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            hello = new Project_Additional_Information();
            fragmentTransaction.add(R.id.llFragmentContainer, hello);
            fragmentTransaction.commit();
            showOneTime = true;
            fragmentDisplay=true;
        } else {
            if (showOneTime&&fragmentDisplay&&oldProject) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.remove(hello);
                fragmentTransaction.commit();
                fragmentDisplay=false;
            }
        }
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
        }
    }

    public void handleContinue() {
        oldProject = Projectos.contains(projectName);

        if (!Projectos.contains(projectName)&&!fragmentDisplay)
        {
            onDoneAction(tv);
            return;
        }
        if(disciplina.equals("Selecionar Disciplina"))
        {

            Toast.makeText(getApplicationContext(), "Tem de Selecionar uma disciplina",Toast.LENGTH_LONG).show();
            return;
        }
        if(projectName.equals(""))
        {
            Toast.makeText(getApplicationContext(), "Tem de Escolher um Nome para o Projecto",Toast.LENGTH_LONG).show();
            return;
        }


        System.gc();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("Name", projectName);
        returnIntent.putExtra("newProject", !oldProject);
        if(!oldProject){
            returnIntent.putExtra("Descricao", descricao);
            returnIntent.putExtra("Disciplina", disciplina);

            ArrayList<Integer> idUserList=new ArrayList<>();
            for(int i=0;i<userList.size();i++){
                CharSequence s=userList.get(i).getName();
                if(selectedUsersFinal.contains(s))
                    idUserList.add(userList.get(i).getMec());

            }
            returnIntent.putExtra("cooworker", idUserList);

        }
        else{
            for(int i=0;i<Projectos.size();i++) {
                if(Projectos.get(i).equals(projectName))
                    returnIntent.putExtra("idProjecto", ids.get(i));
            }
        }
        returnIntent.putExtra(EXTRA_MESSAGE, listPhotos);
        setResult(this.RESULT_OK, returnIntent);
        finish();
    }

    public void handleCancel() {
        System.gc();
        Intent returnIntent = new Intent();
        setResult(this.RESULT_CANCELED, returnIntent);
        finish();
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

    private void getListProjects() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        int idOwner=Integer.parseInt(sp.getString("NMec",null));
        User user=new User(idOwner);
        new RetrieveFeedTask().execute(user);
    }

    class RetrieveFeedTask extends AsyncTask<User, Void, List<Project>> {

        private Exception exception;

        protected List<Project> doInBackground(User... user) {
            Gson gson = new GsonBuilder().create();
            String message = gson.toJson(user);
            message = message.substring(1,message.length()-1);

            URL url;
            HttpURLConnection urlConnection;
            try {
                StringBuilder output = new StringBuilder();
                url = new URL("http://192.168.160.32:8080/cmAndroid/webresources/generic");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setConnectTimeout(20000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("charset", "utf-8");
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
                Log.d("Tag Name", output.toString());
                ListProjects prr = gson.fromJson(output.toString(), ListProjects.class);

                urlConnection.disconnect();
                return prr.getListProject();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(List<Project> feed) {
            // TODO: check this.exception
            // TODO: do something with the feed
            myMethod(feed);
        }
    }

    private void  myMethod(List<Project> feed) {
        if(feed!=null) {
            for (Project project : feed) {
                Projectos.add(project.getName());
                ids.add(project.getProjectID());
            }
        }
        makeAutoCompleteView();
    }
}