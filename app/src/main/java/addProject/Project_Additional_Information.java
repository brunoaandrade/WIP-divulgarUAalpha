package addProject;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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

import deti.ua.main.R;
import webentities.ListUsers;
import webentities.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class Project_Additional_Information extends Fragment {

    protected TextView selectUsersButton;
    private CharSequence[] users ;
    private ArrayList<CharSequence> selectedUsers = new ArrayList<>();


    public Project_Additional_Information() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_project__additional__information, container, false);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int idOwner=Integer.parseInt(sp.getString("NMec",null));
        User user=new User(idOwner);
        new RetrieveFeedTask().execute(user);
        selectUsersButton = (TextView) rootView.findViewById(R.id.select_colours);
        selectUsersButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showSelectUsersDialog();
            }
        });

        addItemsOnSpinner(rootView);
        // Inflate the layout for this fragment
        EditText myTextBox = (EditText) rootView.findViewById(R.id.editText2);
        myTextBox.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                ((AddToProject) getActivity()).descricao=s.toString();
            }
        });

        return rootView;
    }

    public void addItemsOnSpinner(View rootView) {

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }
                return v;
            }
            @Override
            public int getCount() {
                return super.getCount()-1; // you dont display last item. It is used as hint.
            }
        };

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Load courses list
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if( sp.getInt("uc_size", 0)!=0) {
            int size = sp.getInt("uc_size", 0);
            for (int i = 0; i < size; i++) {
                Log.i("log", sp.getString("uc_" + i, null));
                dataAdapter.add(sp.getString("uc_" + i, null));
            }
        }
        dataAdapter.add("Selecionar Disciplina");
        Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner);
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(dataAdapter.getCount());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                AddToProject a=(AddToProject)getActivity();
                a.disciplina=parentView.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }




    protected void showSelectUsersDialog() {
        boolean[] checkedUsers = new boolean[users.length];
        AddToProject a=(AddToProject)getActivity();
        for(int i = 0; i < users.length; i++)
            checkedUsers[i] = a.selectedUsersFinal.contains(users[i]);
        DialogInterface.OnMultiChoiceClickListener coloursDialogListener = new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if(isChecked)
                    selectedUsers.add(users[which]);
                else
                    selectedUsers.remove(users[which]);
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                AddToProject a=(AddToProject)getActivity();
                a.selectedUsersFinal= new ArrayList<>();
                a.selectedUsersFinal.addAll(selectedUsers);
                onChangeSelectedUsers();
            } });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                AddToProject a=(AddToProject)getActivity();
                selectedUsers = new ArrayList<>();
                selectedUsers.addAll(a.selectedUsersFinal);
                onChangeSelectedUsers();
            }
        });
        builder.setTitle("Select Users");
        builder.setMultiChoiceItems(users, checkedUsers, coloursDialogListener);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    protected void onChangeSelectedUsers() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CooWorkers :  ");
        int i=0;
        AddToProject a=(AddToProject)getActivity();
        for(CharSequence colour : a.selectedUsersFinal) {
            i++;
            stringBuilder.append(colour);
            if(i!= a.selectedUsersFinal.size())
                stringBuilder.append(" , ");
        }
        selectUsersButton.setText(stringBuilder.toString());
    }



    class RetrieveFeedTask extends AsyncTask<User, Void, List<User>> {

        private Exception exception;

        protected List<User> doInBackground(User... user) {
            Gson gson = new GsonBuilder().create();
            String message = gson.toJson(user);
            message = message.substring(1,message.length()-1);

            URL url;
            HttpURLConnection urlConnection;
            try {
                StringBuilder output = new StringBuilder();
                url = new URL("http://192.168.160.32:8080/cmAndroid/webresources/listUsers");
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
                ListUsers prr = gson.fromJson(output.toString(), ListUsers.class);

                urlConnection.disconnect();
                return prr.getListUser();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(List<User> feed) {
            myMethod(feed);
        }
    }

    private void  myMethod(List<User> feed) {
        if(feed!=null) {
            AddToProject a=(AddToProject)getActivity();
            a.userList.addAll(feed);

            users =new String[feed.size()];
            int i=0;
            for (User project : feed) {
                users[i]=project.getName();
                i++;
            }
        }
    }
}
