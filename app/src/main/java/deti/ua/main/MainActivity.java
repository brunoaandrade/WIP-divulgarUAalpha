package deti.ua.main;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import addProject.LoadProjectCamera;
import authentication.OauthActivity;



public class MainActivity extends AppCompatActivity  {


    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[]={"Recentes","Populares"};
    int Numboftabs =2;
    private static final String USER_IS_LOGGEDIN = "isLogin";

    private static final int PROFILE_SETTING = 1;
    private AccountHeader.Result headerResult = null;
    private Drawer.Result result = null;
    static SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isLogin = sp.getBoolean(USER_IS_LOGGEDIN, false);//get value of last authentication status


        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the AccountHeader
        headerResult = new AccountHeader()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .withSavedInstance(savedInstanceState)
                .build();



        //Create the drawer
        result = new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.title_projects).withIcon(FontAwesome.Icon.faw_files_o).withIdentifier(1),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.title_about).withIcon(FontAwesome.Icon.faw_info).withIdentifier(2),
                        new SecondaryDrawerItem().withName(R.string.title_help).withIcon(FontAwesome.Icon.faw_question).withIdentifier(3),
                        new SecondaryDrawerItem().withName(R.string.title_login).withIcon(FontAwesome.Icon.faw_sign_in).withIdentifier(18)

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        if (drawerItem != null) {

                            Intent intent = null;
                            if (drawerItem.getIdentifier() == 1) {
                                intent = new Intent(MainActivity.this, MainActivity.class);
                            } else if (drawerItem.getIdentifier() == 2) {
                                intent = new Intent(MainActivity.this, AboutActivity.class);
                            } else if (drawerItem.getIdentifier() == 3) {
                                intent = new Intent(MainActivity.this, HelpActivity.class);
                            }else if (drawerItem.getIdentifier() == 4) {
                                    intent = new Intent(MainActivity.this, ProfileActivity.class);
                                    intent.putExtra("profileID","self");
                            } else if (drawerItem.getIdentifier() == 18) {
                                String token = sp.getString("ua_access_token", "Not Found");
                                if (token.equals("Not Found")) {
                                    intent = new Intent(MainActivity.this, OauthActivity.class);
                                }
                                boolean isLogin = sp.getBoolean(USER_IS_LOGGEDIN, false);
                                if (!isLogin) {
                                    sp.edit().putBoolean(USER_IS_LOGGEDIN, true).commit();
                                    finish();
                                    startActivity(getIntent());
                                }
                            } else if (drawerItem.getIdentifier() == 19) {
                                intent = new Intent(MainActivity.this, SettingsActivity.class);
                            } else if (drawerItem.getIdentifier() == 20) {

                                sp.edit().putBoolean(USER_IS_LOGGEDIN, false).commit();
                                finish();
                                startActivity(getIntent());
                            }
                            if (intent != null) {
                                MainActivity.this.startActivity(intent);
                            }
                        }
                    }
                })
                .build();

        if(isLogin)
        {


            //FAB button
            createButton();


            String fullname = sp.getString("Nome","Not Found");


            String email = sp.getString("Email","Not Found");

            String photo = sp.getString("Foto","Not Found");


            // decode user image
            byte[] decodedString = Base64.decode(photo, Base64.DEFAULT);
            Bitmap image = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);


            headerResult.addProfiles(
                    new ProfileDrawerItem().withName(fullname).withEmail(email).withIcon(new BitmapDrawable(getResources(),image))
            );
            result.removeItem(4);
            result.addItems(
                    new PrimaryDrawerItem().withName(R.string.title_profile).withIcon(FontAwesome.Icon.faw_user).withIdentifier(4),
                    new SecondaryDrawerItem().withName(R.string.title_settings).withIcon(FontAwesome.Icon.faw_cog).withIdentifier(19),
                    new SecondaryDrawerItem().withName(R.string.title_logout).withIcon(FontAwesome.Icon.faw_sign_out).withIdentifier(20)
            );
        }


        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);

        outState = headerResult.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }



    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
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

    private void buttonClicked(int x) {
        Intent intent = new Intent(this, LoadProjectCamera.class);
        intent.putExtra("Option",x);
        intent.putExtra("Name","");

        startActivity(intent);
    }

}
