package in.ac.iitm.students.activities.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import in.ac.iitm.students.R;
import in.ac.iitm.students.activities.AboutUsActivity;
import in.ac.iitm.students.activities.ContactUsActivity;
import in.ac.iitm.students.activities.SubscriptionActivity;
import in.ac.iitm.students.adapters.NewsAdapter;
import in.ac.iitm.students.objects.News;
import in.ac.iitm.students.others.LogOutAlertClass;
import in.ac.iitm.students.others.MySingleton;
import in.ac.iitm.students.others.UtilStrings;
import in.ac.iitm.students.others.Utils;

public class T5EActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final Gson gson = new Gson();
    public String NEWSSTRING = "fifthestatestring";
    public String TAG = "TheFifthEstateFragment";
    Toolbar toolbar;
    RecyclerView mRecyclerView;
    Context context;
    TextView error_message;
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_t5e);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        error_message = (TextView) findViewById(R.id.tv_error_t5e);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.cl_t5e);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerfifthsetate);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(T5EActivity.this));
        mRecyclerView.setHasFixedSize(true);
        getNews();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

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

    public void getNews() {
        final ProgressDialog progress;

        context = T5EActivity.this;
        final ArrayList<News> newses = new ArrayList<News>();
        String url = getString(R.string.fifthettateurl);

        progress = new ProgressDialog(context);
        progress.setCancelable(false);
        progress.setMessage("Loading T5E...");
        progress.show();

        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (url, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        int id = 0;
                        Long dateINT = null;
                        String title = null, summary = null, content = null;
                        String imgurl = "";

                        for (int i = 0; response.length() > i; i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                id = jsonObject.getInt("id");
                                Date date = null;
                                SimpleDateFormat formatter = new SimpleDateFormat("E, MMM dd, yyyy hh:mm a");
                                try {
                                    date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(jsonObject.getString("modified").replace("T", " "));
                                } catch (java.text.ParseException e) {
                                    e.printStackTrace();
                                }
                                dateINT = date.getTime();
                                title = jsonObject.getJSONObject("title").getString("rendered");
                                summary = jsonObject.getJSONObject("excerpt").getString("rendered");
                                summary = Html.fromHtml(summary).toString();
                                content = jsonObject.getJSONObject("content").getString("rendered");
                                org.jsoup.nodes.Document doc = Jsoup.parse(content);
                                Elements castsImageUrl = doc.getElementsByTag("img");
                                if (castsImageUrl.size() > 0) {
                                    org.jsoup.nodes.Element el = castsImageUrl.get(0);
                                    imgurl = el.attr("src");
//                                    Log.d(TAG, imgurl);
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
//                            Log.d(TAG, title);
                            newses.add(new News(id, title, summary, content, imgurl, dateINT));
                        }
                        String json = gson.toJson(newses);
                        Utils.saveprefString(NEWSSTRING, json, context);
                        mRecyclerView.setAdapter(new NewsAdapter(context, newses));
                        progress.dismiss();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, getString(R.string.error_connection), Snackbar.LENGTH_LONG);
                        snackbar.show();
                        String json = Utils.getprefString(NEWSSTRING, context);

                        if (!json.equals("")) {
                            ArrayList<News> newses = (ArrayList<News>) gson.fromJson(json,
                                    new TypeToken<ArrayList<News>>() {
                                    }.getType());
                            mRecyclerView.setAdapter(new NewsAdapter(context, newses));
                        } else {
                            error_message.setVisibility(View.VISIBLE);
                        }
                        progress.dismiss();
                    }
                });


        MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(T5EActivity.this, HomeActivity.class);
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
            Intent intent = new Intent(context, AboutUsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_contact) {
            Intent intent = new Intent(T5EActivity.this, ContactUsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_log_out) {
            LogOutAlertClass lg = new LogOutAlertClass();
            lg.isSure(T5EActivity.this);
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
        }else if (id == R.id.nav_groups_and_clubs) {
            intent = new Intent(context, GroupsAndClubsActivity.class);
            flag = true;

        } else if (id == R.id.nav_calendar) {
            intent = new Intent(context, CalendarActivity.class);
            flag = true;
        } else if (id == R.id.nav_mess_and_facilities) {
            intent = new Intent(context, MessAndFacilitiesActivity.class);
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
                            lg.isSure(T5EActivity.this);
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
}
