package in.ac.iitm.students.activities.main;

import android.app.ProgressDialog;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

import in.ac.iitm.students.R;
import in.ac.iitm.students.activities.AboutUsActivity;
import in.ac.iitm.students.activities.ContactUsActivity;
import in.ac.iitm.students.activities.SubscriptionActivity;
import in.ac.iitm.students.fragments.UpcomingLectures;
import in.ac.iitm.students.fragments.Year2014_15Fragment;
import in.ac.iitm.students.fragments.Year2015_16Fragment;
import in.ac.iitm.students.fragments.Year2016_17Fragment;
import in.ac.iitm.students.others.LogOutAlertClass;
import in.ac.iitm.students.others.MySingleton;
import in.ac.iitm.students.others.UtilStrings;
import in.ac.iitm.students.others.Utils;

public class EMLActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;

    ProgressDialog pDialog;
    TextView tvError;
    DrawerLayout drawer;
    String year2016_17 = "[", year2015_16 = "[", year2014_15 = "[", up_coming = "[";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eml);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvError = (TextView) findViewById(R.id.tv_error_eml);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_eml);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        getEMLData();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(getResources().getInteger(R.integer.nav_index_groups_and_clubs)).setChecked(true);
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

    private void getEMLData() {
        final Context context = EMLActivity.this;
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Loading EML...");
        pDialog.show();
        pDialog.setCancelable(false);


        String url = getString(R.string.url_eml);

        // Request a string response from the provided URL.
        StringRequest jsonObjReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {


                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject;
                    String series;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        series = jsonObject.getString("series");

                        // Do not replace with switch - Sathwik Chadaga :P
                        if (series.equals("2014-15")) {
                            year2014_15 += jsonObject;
                            if (i != jsonArray.length() - 1) year2014_15 += ",";
                        } else if (series.equals("2015-16")) {
                            year2015_16 += jsonObject;
                            if (i != jsonArray.length() - 1) year2015_16 += ",";
                        } else if (series.equals("2016-17")) {
                            year2016_17 += jsonObject;
                            if (i != jsonArray.length() - 1) year2016_17 += ",";
//                            Log.d("PARSMY", year2016_17);
//                            Log.d("PARSMY", "\n\n\n\n\n");
                        }


                    }
                    year2014_15 = year2014_15 + "]";
                    year2015_16 = year2015_16 + "]";
                    year2016_17 = year2016_17 + "]";

                    Utils.saveprefString(UtilStrings.EMLDATA2014, year2014_15, getBaseContext());
                    Utils.saveprefString(UtilStrings.EMLDATA2015, year2015_16, getBaseContext());
                    Utils.saveprefString(UtilStrings.EMLDATA2016, year2016_17, getBaseContext());

                    getUpcomingLectures();
                } catch (JSONException e) {
                    e.printStackTrace();
                    pDialog.dismiss();
                    tvError.setText(R.string.error_parsing);
                    tvError.setVisibility(View.VISIBLE);
                    Snackbar snackbar = Snackbar
                            .make(drawer, getString(R.string.error_parsing), Snackbar.LENGTH_LONG);
                    snackbar.show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                pDialog.dismiss();

                try {
                    String year2014 = Utils.getprefString(UtilStrings.EMLDATA2014, EMLActivity.this);
                    String year2015 = Utils.getprefString(UtilStrings.EMLDATA2015, EMLActivity.this);
                    String year2016 = Utils.getprefString(UtilStrings.EMLDATA2016, EMLActivity.this);

                    if (!year2014.equals("") && !year2015.equals("") && !year2016.equals("")) {
                        year2014_15 = year2014;
                        year2015_16 = year2015;
                        year2016_17 = year2016;

                        pDialog.dismiss();
                        Snackbar snackbar = Snackbar
                                .make(drawer, getString(R.string.error_connection), Snackbar.LENGTH_LONG);
                        snackbar.show();
                        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
                        setupViewPagerNoUpcoming(viewPager);

                        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
                        if (tabLayout != null) {
                            tabLayout.setupWithViewPager(viewPager);
                            tabLayout.setVisibility(View.VISIBLE);
                        }
                    } else {

                        tvError.setText(R.string.error_connection);
                        tvError.setVisibility(View.VISIBLE);
                        pDialog.dismiss();
                        Snackbar snackbar = Snackbar
                                .make(drawer, getString(R.string.error_connection), Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                } catch (EmptyStackException e) {
                    pDialog.dismiss();
                    tvError.setText(R.string.error_connection);
                    tvError.setVisibility(View.VISIBLE);
                    Snackbar snackbar = Snackbar
                            .make(drawer, getString(R.string.error_connection), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }

                pDialog.dismiss();
            }
        });
        MySingleton.getInstance(this).addToRequestQueue(jsonObjReq);

    }

    private void getUpcomingLectures() {
        final Context context = EMLActivity.this;

        String url = getString(R.string.url_eml_upcoming);

        // Request a string response from the provided URL.
        StringRequest jsonObjReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    up_coming = jsonArray.toString();

                    ViewPager viewPager = (ViewPager) findViewById(R.id.container);
                    setupViewPager(viewPager);

                    TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
                    if (tabLayout != null) {
                        tabLayout.setupWithViewPager(viewPager);
                        tabLayout.setVisibility(View.VISIBLE);
                    }

                    pDialog.dismiss();

                } catch (JSONException e) {
                    ViewPager viewPager = (ViewPager) findViewById(R.id.container);
                    setupViewPagerNoUpcoming(viewPager);

                    TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
                    if (tabLayout != null) {
                        tabLayout.setupWithViewPager(viewPager);
                        tabLayout.setVisibility(View.VISIBLE);
                    }

                    pDialog.dismiss();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                pDialog.dismiss();
                tvError.setText(R.string.error_connection);
                tvError.setVisibility(View.VISIBLE);
                pDialog.dismiss();
                Snackbar snackbar = Snackbar
                        .make(drawer, getString(R.string.error_connection), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
        MySingleton.getInstance(this).addToRequestQueue(jsonObjReq);
    }

    private void setupViewPager(ViewPager viewPager) {


        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        UpcomingLectures u = new UpcomingLectures();
        Year2016_17Fragment y1 = new Year2016_17Fragment();
        Year2015_16Fragment y2 = new Year2015_16Fragment();
        Year2014_15Fragment y3 = new Year2014_15Fragment();

        u.setResponse(up_coming);
        y1.setResponse(year2016_17);
        y2.setResponse(year2015_16);
        y3.setResponse(year2014_15);

        adapter.addFragment(u, "Upcoming");
        adapter.addFragment(y1, "2016 - 17");
        adapter.addFragment(y2, "2015 - 16");
        adapter.addFragment(y3, "2014 - 15");
        viewPager.setAdapter(adapter);
    }

    private void setupViewPagerNoUpcoming(ViewPager viewPager) {


        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        Year2016_17Fragment y1 = new Year2016_17Fragment();
        Year2015_16Fragment y2 = new Year2015_16Fragment();
        Year2014_15Fragment y3 = new Year2014_15Fragment();

        y1.setResponse(year2016_17);
        y2.setResponse(year2015_16);
        y3.setResponse(year2014_15);

        adapter.addFragment(y1, "2016 - 17");
        adapter.addFragment(y2, "2015 - 16");
        adapter.addFragment(y3, "2014 - 15");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_eml);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(EMLActivity.this, HomeActivity.class);
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
            Intent intent = new Intent(EMLActivity.this, AboutUsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_contact) {
            Intent intent = new Intent(EMLActivity.this, ContactUsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_log_out) {
            LogOutAlertClass lg = new LogOutAlertClass();
            lg.isSure(EMLActivity.this);
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
        final Context context = EMLActivity.this;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_eml);

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
            intent = new Intent(context, SchroeterActivity.class);
            flag = true;

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
                            lg.isSure(EMLActivity.this);
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
