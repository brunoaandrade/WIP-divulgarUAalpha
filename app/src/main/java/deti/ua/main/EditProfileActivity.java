package deti.ua.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


public class EditProfileActivity extends AppCompatActivity {

    static SharedPreferences sp;



    static EditText [] editT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //set the back arrow in the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        final EditText editName = (EditText)findViewById(R.id.editName);
        final TextView editStudys = (TextView)findViewById(R.id.editStudys);
        final TextView edtYear = (TextView)findViewById(R.id.edtYear);
        final EditText editLocation = (EditText)findViewById(R.id.editLocation);
        final EditText editInterests = (EditText)findViewById(R.id.editTag);
        final EditText editAge = (EditText)findViewById(R.id.editAge);

        String fullname = sp.getString("Nome", "Not Found");

        editName.setText(fullname);

        String photo = sp.getString("Foto", "Not Found");
        ImageView profilePic = (ImageView)findViewById(R.id.profilePicture);
        byte[] decodedString = Base64.decode(photo, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);


        Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Log.i("SIZE", String.valueOf(bitmap.getWidth()));
        Log.i("SIZE", String.valueOf(bitmap.getHeight()));


        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setShader(shader);


        Canvas c = new Canvas(circleBitmap);
        c.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth()/2, paint);


        profilePic.setImageBitmap(circleBitmap);

        String studys = sp.getString("Curso", "Not Found");
        editStudys.setText(studys);

        String ano = sp.getString("AnoCurricular", "Not Found");
        edtYear.setText(ano);

        String idade = sp.getString("Idade", "Not Found");
        editAge.setText(idade);

        String localidade = sp.getString("Localidade", "Not Found");
        editLocation.setText(localidade);

        String interesses = sp.getString("Interesses", "Not Found");
        editAge.setText(interesses);

        EditText [] eT = {editName,editAge, editLocation,editInterests};

        editT = eT;


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
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

        if (id == R.id.nextBut){
            final SharedPreferences.Editor editor = sp.edit();
            //String[] prefKeys = {"Nome","Foto","Localidade", "Interesses"};
            String[] prefKeys = {"Nome","Idade","Localidade", "Interesses"};


            for (int i = 0; i < prefKeys.length; i++) {
                editor.putString(prefKeys[i], editT[i].getText().toString());
            }
            editor.commit();
            Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
