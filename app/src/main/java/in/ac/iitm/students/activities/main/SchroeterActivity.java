package in.ac.iitm.students.activities.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

import in.ac.iitm.students.R;
import in.ac.iitm.students.activities.AboutUsActivity;
import in.ac.iitm.students.activities.ContactUsActivity;
import in.ac.iitm.students.activities.SubscriptionActivity;
import in.ac.iitm.students.fragments.SchroiterScoresFragment;
import in.ac.iitm.students.fragments.SchroiterUpdatesFragment;
import in.ac.iitm.students.others.LogOutAlertClass;
import in.ac.iitm.students.others.MySingleton;
import in.ac.iitm.students.others.UtilStrings;
import in.ac.iitm.students.others.Utils;

public class SchroeterActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    String updates = "No results", scores = "No results";
    DrawerLayout drawer;
    ProgressBar pbar;
    Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schroiter);

        pbar = (ProgressBar) findViewById(R.id.pb_schroiter);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        snackbar = Snackbar
                .make(drawer, R.string.error_connection, Snackbar.LENGTH_LONG);
        getUpdates();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(getResources().getInteger(R.integer.nav_index_schroeter)).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        TextView username = (TextView) header.findViewById(R.id.tv_username);
        TextView rollNumber = (TextView) header.findViewById(R.id.tv_roll_number);

        String roll_no = Utils.getprefString(UtilStrings.ROLLNO, this);
        String name = Utils.getprefString(UtilStrings.NAME, this);

        username.setText(name);
        rollNumber.setText(roll_no);

        ImageView imageView = (ImageView) header.findViewById(R.id.user_pic);
        String urlPic = "https://photos.iitm.ac.in//byroll.php?roll=" + roll_no;
        Picasso.with(this)
                .load(urlPic)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(imageView);
    }

    private void getUpdates() {

        final Context context = SchroeterActivity.this;
        pbar.setVisibility(View.VISIBLE);
        String url = getString(R.string.url_schroiter_updates);

        // Request a string response from the provided URL.
        StringRequest jsonObjReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    updates = jsonArray.toString();
                    Utils.saveprefString(UtilStrings.schroeterUpdates, updates, getBaseContext());
                    getScores();


                } catch (JSONException e) {
                    getScores();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                try {
                    String updateBuffer = Utils.getprefString(UtilStrings.schroeterUpdates, SchroeterActivity.this);
                    if (!updateBuffer.equals("")) {
                        updates = updateBuffer;
                        pbar.setVisibility(View.GONE);
                        snackbar.show();
                        getScores();
                    } else {
                        error.printStackTrace();
                        pbar.setVisibility(View.GONE);
                        snackbar.show();

                        TextView tvError = (TextView) findViewById(R.id.tv_error_schroiter);
                        tvError.setVisibility(View.VISIBLE);
                    }
                } catch (EmptyStackException e) {
                    error.printStackTrace();
                    pbar.setVisibility(View.GONE);
                    snackbar.show();

                    TextView tvError = (TextView) findViewById(R.id.tv_error_schroiter);
                    tvError.setVisibility(View.VISIBLE);
                }
            }
        });
        MySingleton.getInstance(this).addToRequestQueue(jsonObjReq);
    }

    private void getScores() {

        pbar.setVisibility(View.VISIBLE);

        final Context context = SchroeterActivity.this;

        String url = getString(R.string.url_schroiter_scores);

        // Request a string response from the provided URL.
        StringRequest jsonObjReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    scores = jsonArray.toString();
                    ViewPager viewPager = (ViewPager) findViewById(R.id.container);
                    setupViewPager(viewPager);

                    TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
                    tabLayout.setVisibility(View.VISIBLE);
                    tabLayout.setupWithViewPager(viewPager);
                    Utils.saveprefString(UtilStrings.schroeterScores, scores, getBaseContext());
                    pbar.setVisibility(View.GONE);

                } catch (JSONException e) {
                    pbar.setVisibility(View.GONE);
                    ViewPager viewPager = (ViewPager) findViewById(R.id.container);
                    setupViewPager(viewPager);

                    TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
                    tabLayout.setVisibility(View.VISIBLE);
                    tabLayout.setupWithViewPager(viewPager);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    String scoreBuffer = Utils.getprefString(UtilStrings.schroeterScores, SchroeterActivity.this);
                    if (!scoreBuffer.equals("")) {
                        scores = scoreBuffer;
                        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
                        setupViewPager(viewPager);

                        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
                        tabLayout.setVisibility(View.VISIBLE);
                        tabLayout.setupWithViewPager(viewPager);
                        pbar.setVisibility(View.GONE);
                        snackbar.show();
                    } else {
                        error.printStackTrace();

                        pbar.setVisibility(View.GONE);
                        snackbar.show();
                        TextView tvError = (TextView) findViewById(R.id.tv_error_schroiter);
                        tvError.setVisibility(View.VISIBLE);
                    }

                } catch (EmptyStackException e) {
                    error.printStackTrace();

                    pbar.setVisibility(View.GONE);
                    snackbar.show();
                    TextView tvError = (TextView) findViewById(R.id.tv_error_schroiter);
                    tvError.setVisibility(View.VISIBLE);
                }

            }
        }

        );
        MySingleton.getInstance(this).addToRequestQueue(jsonObjReq);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Log.d("schroeter", scores + "\n" + updates);
        SchroiterUpdatesFragment u = new SchroiterUpdatesFragment();
        SchroiterScoresFragment s = new SchroiterScoresFragment();

        u.setResponse(updates, drawer);
        s.setResponse(scores, drawer);

        adapter.addFragment(u, "Updates");
        adapter.addFragment(s, "Score links");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(SchroeterActivity.this, HomeActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent intent = new Intent(SchroeterActivity.this, AboutUsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_contact) {
            Intent intent = new Intent(SchroeterActivity.this, ContactUsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_log_out) {
            LogOutAlertClass lg = new LogOutAlertClass();
            lg.isSure(SchroeterActivity.this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent = new Intent();
        boolean flag = false;
        final Context context = SchroeterActivity.this;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (id == R.id.nav_home) {
            intent = new Intent(context, HomeActivity.class);
            flag = true;
        } else if (id == R.id.nav_search) {

            intent = new Intent(context, StudentSearchActivity.class);
            flag = true;
        } else if (id == R.id.nav_contacts) {
            intent = new Intent(context, ImpContactsActivity.class);
            flag = true;

        } else if (id == R.id.nav_map) {
            intent = new Intent(context, MapActivity.class);
            flag = true;
        } else if (id == R.id.nav_groups_and_clubs) {
            intent = new Intent(context, GroupsAndClubsActivity.class);
            flag = true;
        } else if (id == R.id.nav_mess_and_facilities) {
            intent = new Intent(context, MessAndFacilitiesActivity.class);
            flag = true;
        } else if (id == R.id.nav_calendar) {
            intent = new Intent(context, CalendarActivity.class);
            flag = true;

        } else if (id == R.id.nav_schroeter) {

        } else if (id == R.id.nav_subscriptions) {
            intent = new Intent(context, SubscriptionActivity.class);
            flag = true;

        } else if (id == R.id.nav_about) {

            intent = new Intent(context, AboutUsActivity.class);
            flag = true;
        } else if (id == R.id.nav_contact_us) {
            intent = new Intent(context, ContactUsActivity.class);
            flag = true;

        } else if (id == R.id.nav_log_out) {
            drawer.closeDrawer(GravityCompat.START);
            Handler handler = new Handler();
            handler.postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            LogOutAlertClass lg = new LogOutAlertClass();
                            lg.isSure(SchroeterActivity.this);
                        }
                    }
                    , getResources().getInteger(R.integer.close_nav_drawer_delay)  // it takes around 200 ms for drawer to close
            );
            return true;
        }

        drawer.closeDrawer(GravityCompat.START);

        //Wait till the nav drawer is closed and then start new activity (for smooth animations)
        Handler mHandler = new Handler();
        final boolean finalFlag = flag;
        final Intent finalIntent = intent;
        mHandler.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        if (finalFlag) {
                            context.startActivity(finalIntent);
                        }
                    }
                }
                , getResources().getInteger(R.integer.close_nav_drawer_delay)  // it takes around 200 ms for drawer to close
        );

        return true;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}