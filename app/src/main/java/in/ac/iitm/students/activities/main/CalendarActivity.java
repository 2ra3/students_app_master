package in.ac.iitm.students.activities.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.ac.iitm.students.R;
import in.ac.iitm.students.activities.AboutUsActivity;
import in.ac.iitm.students.activities.CalendarDisplayActivity;
import in.ac.iitm.students.activities.ContactUsActivity;
import in.ac.iitm.students.activities.SubscriptionActivity;
import in.ac.iitm.students.others.LogOutAlertClass;
import in.ac.iitm.students.others.MySingleton;
import in.ac.iitm.students.others.UtilStrings;
import in.ac.iitm.students.others.Utils;


public class CalendarActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ArrayList<String> calendarUrlArray = new ArrayList<>();

    Toolbar toolbar;
    DrawerLayout drawerLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final TextView mOddYear1TextView = (TextView) findViewById(R.id.odd_year1_textView);
        final TextView mEvenYear1TextView = (TextView) findViewById(R.id.even_year1_textView);
        final TextView mOddYear2TextView = (TextView) findViewById(R.id.odd_year2_textView);
        final TextView mEvenYear2TextView = (TextView) findViewById(R.id.even_year2_textView);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("students.iitm.ac.in")
                .appendPath("studentsapp")
                .appendPath("calendar")
                .appendPath("cal.php");
        String url = builder.build().toString();

        StringRequest jsonObjReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsArray = new JSONArray(response);

                    JSONObject jsObject0 = jsArray.getJSONObject(0);
                    String year1 = jsObject0.getString("year");
                    mOddYear1TextView.setText("Jul-Nov " + year1);
                    mEvenYear1TextView.setText("Jan-May " + year1);
                    calendarUrlArray.add(jsObject0.getString("even"));
                    calendarUrlArray.add(jsObject0.getString("odd"));

                    JSONObject jsObject1 = jsArray.getJSONObject(1);
                    String year2 = jsObject1.getString("year");
                    mOddYear2TextView.setText("Jul-Nov " + year2);
                    mEvenYear2TextView.setText("Jan-May " + year2);
                    calendarUrlArray.add(jsObject1.getString("even"));
                    calendarUrlArray.add(jsObject1.getString("odd"));


                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar snackbar = Snackbar.make(drawerLayout, R.string.error_parsing, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("VolleyResponseError", error);
                Snackbar snackbar = Snackbar.make(drawerLayout, R.string.error_connection, Snackbar.LENGTH_SHORT);
                snackbar.show();

            }
        });
        MySingleton.getInstance(this).addToRequestQueue(jsonObjReq);

        ImageButton mOddYear1ImageButton = (ImageButton) findViewById(R.id.odd_year1);
        ImageButton mEvenYear1ImageButton = (ImageButton) findViewById(R.id.even_year1);
        ImageButton mOddYear2ImageButton = (ImageButton) findViewById(R.id.odd_year2);
        ImageButton mEvenYear2ImageButton = (ImageButton) findViewById(R.id.even_year2);

        mOddYear1ImageButton.setTag(0);
        mEvenYear1ImageButton.setTag(1);
        mOddYear2ImageButton.setTag(2);
        mEvenYear2ImageButton.setTag(3);


        mOddYear1ImageButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dummy_calendar));
        mEvenYear1ImageButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dummy_calendar));
        mOddYear2ImageButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dummy_calendar));
        mEvenYear2ImageButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dummy_calendar));


        mOddYear1ImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendar((Integer) v.getTag());
            }
        });

        mEvenYear1ImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendar((Integer) v.getTag());
            }
        });

        mOddYear2ImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendar((Integer) v.getTag());
            }
        });

        mEvenYear2ImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendar((Integer) v.getTag());
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(getResources().getInteger(R.integer.nav_index_calendar)).setChecked(true);
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

    public void showCalendar(int c) {
        if (calendarUrlArray != null) {
            Intent intent = new Intent(this, CalendarDisplayActivity.class);
            if (calendarUrlArray.size() > 0) {
                intent.putExtra("calendar_url", calendarUrlArray.get(c));
                startActivity(intent);
            } else {
                Snackbar snackbar = Snackbar.make(drawerLayout, R.string.error_connection, Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(CalendarActivity.this, HomeActivity.class);
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
            Intent intent = new Intent(CalendarActivity.this, AboutUsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_contact) {
            Intent intent = new Intent(CalendarActivity.this, ContactUsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_log_out) {
            LogOutAlertClass lg = new LogOutAlertClass();
            lg.isSure(CalendarActivity.this);
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
        final Context context = CalendarActivity.this;
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
        } else if (id == R.id.nav_schroeter) {
            intent = new Intent(context, SchroeterActivity.class);
            flag = true;

        } else if (id == R.id.nav_calendar) {

        } else if (id == R.id.nav_about) {
            intent = new Intent(context, AboutUsActivity.class);
            flag = true;

        } else if (id == R.id.nav_contact_us) {
            intent = new Intent(context, ContactUsActivity.class);
            flag = true;

        } else if (id == R.id.nav_subscriptions) {
            intent = new Intent(context, SubscriptionActivity.class);
            flag = true;

        } else if (id == R.id.nav_log_out) {
            drawer.closeDrawer(GravityCompat.START);
            Handler handler = new Handler();
            handler.postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            LogOutAlertClass lg = new LogOutAlertClass();
                            lg.isSure(CalendarActivity.this);
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

