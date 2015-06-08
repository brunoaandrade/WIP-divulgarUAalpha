package deti.ua.divulgarua;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import login.IdentityUAApi;
import webentities.UserCourse;


public class ActivateAccountActivity extends AppCompatActivity {
    static SharedPreferences sp;
    static String androidID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_account);
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        androidID = Secure.getString(getContentResolver(), Secure.ANDROID_ID);

        try {
            getUAInfo("name");
            getUAInfo("uu");
            getUAInfo("student_info");
            getUAInfo("student_courses");
        }catch (Exception e) {
            e.printStackTrace();
        }

        String fullname = sp.getString("Nome", "Not Found");
        final EditText crtName = (EditText)findViewById(R.id.crtName);
        crtName.setText(fullname);

        String photo = sp.getString("Foto", "Not Found");
        ImageView profilePic = (ImageView)findViewById(R.id.editPic);
        byte[] decodedString = Base64.decode(photo, Base64.DEFAULT);
        Bitmap image = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        profilePic.setImageBitmap(image);




        Button save = (Button) findViewById(R.id.saveProfile);

        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final SharedPreferences.Editor editor = sp.edit();
                //String[] prefKeys = {"Nome","Foto","Localidade", "Interesses"};
                String[] prefKeys = {"Nome", "Foto"};
                EditText[] editT = {};

                editor.putString("Nome", crtName.getText().toString());

                try {
                    sendUAInfo();
                }catch (Exception e) {
                    e.printStackTrace();
                }
                editor.putBoolean("isLogin", true);
                editor.commit();
                Intent intent = new Intent(ActivateAccountActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activate_account, menu);
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

        return super.onOptionsItemSelected(item);
    }

    private void sendUAInfo () throws Exception {

        final String fullname = sp.getString("Nome", "");
        final String email = sp.getString("Email", "");
        final String photo = sp.getString("Foto", "");
        final String nMec = sp.getString("NMec", "");
        final String curso = sp.getString("Curso", "");
        final String anoCurr = sp.getString("AnoCurricular", "");



        (new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Log.i("SendAnswer","Estou aqui pah");

                //Criacao do utilizador
                webentities.User user = new webentities.User(Integer.parseInt(nMec),fullname,email, anoCurr, photo, curso);

                //Lista das cadeiras do utilizador
                List<UserCourse> userCourseList = new ArrayList<UserCourse>();


                if( sp.getInt("uc_size", 0)!=0) {
                    int size = sp.getInt("uc_size", 0);
                    for (int i = 0; i < size; i++) {
                        Log.i("log", sp.getString("uc_" + i, null));
                        webentities.UserCourse userCourse = new webentities.UserCourse(sp.getString("uc_" + i, null));
                        userCourseList.add(userCourse);
                    }
                }



                //Unique android identifier (esta assim para fase teste alterar mais tarde)
                webentities.MobileUser mobileUser = new webentities.MobileUser(androidID);

                //Criacao da mensagem a enviar
               webentities.UserInfo userInfo = new webentities.UserInfo(user, userCourseList, mobileUser);

                Gson gson = new GsonBuilder().create();

                URL url;
                HttpURLConnection urlConnection = null;

                String message = gson.toJson(userInfo);

                Log.i("SendAnswer",message);

                try {

                    StringBuilder output = new StringBuilder();

                    url = new URL("http://192.168.160.32:8080/cmAndroid/webresources/userCreation");

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

                    Log.i("SendAnswer","Mensagem enviada");

                    InputStream is;

                    if (urlConnection.getResponseCode() == 200) {

                        is = urlConnection.getInputStream();

    					/* obtem a resposta do pedido */
                        int n = 1;
                        while (n > 0) {

                            byte[] b = new byte[4096];

                            n =  is.read(b);

                            if (n > 0)
                                output.append(new String(b, 0, n));
                        }
                    }

                    Log.i("SendAnswer", output.toString());

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

    @Override
    public void onBackPressed() {
    }

    private void getUAInfo(String scope) throws Exception {


        final String scopeUA = scope;

        final OAuthService service= new ServiceBuilder()
                .provider(IdentityUAApi.class)
                .apiKey(IdentityUAApi.getConsumerKey())
                .apiSecret(IdentityUAApi.getConsumerSecret())
                .build();

        String token = sp.getString("ua_access_token", "Not Found");
        String secret = sp.getString("ua_access_secret","Not Found");
        final Token accessToken=new Token(token,secret);
        String url = IdentityUAApi.getDataUrl(scope);
        final OAuthRequest request = new OAuthRequest(Verb.GET,url);
        Log.i("CENAS0", request.toString());


        (new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                service.signRequest(accessToken, request);
                Response resp = request.send();
                Log.i("CENAS1", resp.getBody());
                Log.i("CENAS2", resp.getMessage());
                String result = resp.getBody();
                JSONObject jObj = null;
                JSONObject results = null;
                SharedPreferences.Editor editor = sp.edit();
                try {
                    jObj = new JSONObject(result);
                    if (scopeUA == "student_info") {
                        results = jObj.getJSONObject("NewDataSet").getJSONObject("ObterDadosAluno");

                        Log.i("CENAS1", results.getString("NMec"));
                        editor.putString("NMec",(results.getString("NMec")));

                        Log.i("CENAS1", results.getString("Curso"));
                        editor.putString("Curso",results.getString("Curso"));

                        Log.i("CENAS1", results.getString("AnoCurricular"));
                        editor.putString("AnoCurricular",results.getString("AnoCurricular"));

                        Log.i("CENAS1", results.getString("Foto"));
                        editor.putString("Foto",results.getString("Foto"));
                    } else if (scopeUA == "uu") {
                        editor.putString("Email",jObj.getString("email"));
                    } else if (scopeUA == "name") {
                        String fullname = jObj.getString("name") + " " + jObj.getString("surname");
                        editor.putString("Nome",fullname);

                    }else if (scopeUA == "student_courses") {

                        JSONArray courses = jObj.getJSONObject("NewDataSet").getJSONArray("ObterListaDisciplinasAluno");
                        /*String fullname = jObj.getString("name") + " " + jObj.getString("surname");
                        editor.putString("Nome",fullname);*/
                        editor.putInt("uc_size", courses.length());

                        for (int i = 0; i < courses.length(); ++i) {
                            results = courses.getJSONObject(i);
                            Log.i("CENAS1", results.getString("NomeDisciplina"));
                            editor.putString("uc_" + i, results.getString("NomeDisciplina"));
                        }

                    }
                    editor.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

         /*  @Override
            protected void onPostExecute(String result) {



            }*/
        }).execute().get(1000, TimeUnit.MILLISECONDS);

    }
}
