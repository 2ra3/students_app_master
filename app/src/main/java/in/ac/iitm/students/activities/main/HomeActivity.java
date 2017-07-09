package in.ac.iitm.students.activities.main;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;

import in.ac.iitm.students.R;
import in.ac.iitm.students.activities.AboutUsActivity;
import in.ac.iitm.students.activities.ContactUsActivity;
import in.ac.iitm.students.activities.SubscriptionActivity;
import in.ac.iitm.students.fragments.ForceUpdateDialogFragment;
import in.ac.iitm.students.fragments.OptionalUpdateDialogFragment;
import in.ac.iitm.students.others.LogOutAlertClass;
import in.ac.iitm.students.others.MySingleton;
import in.ac.iitm.students.others.UtilStrings;
import in.ac.iitm.students.others.Utils;

import static in.ac.iitm.students.activities.SubscriptionActivity.MY_PREFS_NAME;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {

    private static int optionalUpdateDialogCount = 0;
    private static Context mContext;
    String url = "https://students.iitm.ac.in/studentsapp/general/subs.php";
    private Toolbar toolbar;
    private ProgressBar pbar;
    private Snackbar snackbar;
    private FragmentManager fm;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static Context getContext() {
        return mContext;
    }

    public static void showAlert(Activity activity, String title, String message) {

        Drawable dialog_icon;
        dialog_icon = ContextCompat.getDrawable(activity, R.drawable.app_logo);


        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setIcon(dialog_icon);
        builder.setTitle(title);
        builder.setMessage(message)
                .setNeutralButton(R.string.dismiss_home_dialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showAlert(Activity activity, String title, String message, final String link) {

        Drawable dialog_icon;
        dialog_icon = ContextCompat.getDrawable(activity, R.drawable.app_logo);


        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setIcon(dialog_icon);
        builder.setTitle(title);
        builder.setMessage(message)
                .setNegativeButton(R.string.dismiss_home_dialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.go_to_link, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        openWebPage(link);
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private static void openWebPage(String url) {
        Toast.makeText(mContext, "Getting data...", Toast.LENGTH_SHORT).show();
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(mContext.getPackageManager()) != null) {
            mContext.startActivity(intent);
        } else {
            Toast.makeText(mContext, "Error getting data, try again later...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        PackageInfo pInfo;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            //Toast.makeText(this,version, Toast.LENGTH_SHORT).show();
            checkVersionMatch(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mContext = getBaseContext();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        updatePreferences();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefreshhome);
        swipeRefreshLayout.setOnRefreshListener(HomeActivity.this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        pbar = (ProgressBar) findViewById(R.id.pb_home);

        snackbar = Snackbar
                .make(drawer, R.string.error_connection, Snackbar.LENGTH_LONG);
        getData();


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        String roll_no = Utils.getprefString(UtilStrings.ROLLNO, this);
        String name = Utils.getprefString(UtilStrings.NAME, this);

        String firebaseToken = FirebaseInstanceId.getInstance().getToken();
        //Log.d("tada", firebaseToken.toString());
        sendRegistrationToServer(firebaseToken, name, roll_no);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(getResources().getInteger(R.integer.nav_index_home)).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        TextView username = (TextView) header.findViewById(R.id.tv_username);
        TextView userrollNumber = (TextView) header.findViewById(R.id.tv_roll_number);

        username.setText(name);
        userrollNumber.setText(roll_no);
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

    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        refreshList();
    }

    public void refreshList() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 3000);

    }

    private void getData() {

        final Context context = HomeActivity.this;

        pbar.setVisibility(View.VISIBLE);
        String url = getString(R.string.url_home);

        // Request a string response from the provided URL.
        StringRequest jsonObjReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    response = jsonArray.toString();
                    Log.d("response", "home " + response);
                    Utils.saveprefString(UtilStrings.homeData, response, getBaseContext());
                    goToAdapter(response);

                } catch (JSONException e) {

                    TextView tvError = (TextView) findViewById(R.id.tv_error_home);
                    tvError.setText(R.string.error_parsing);
                    pbar.setVisibility(View.GONE);
                    tvError.setVisibility(View.VISIBLE);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                try {
                    String dataBuffer = Utils.getprefString(UtilStrings.homeData, HomeActivity.this);
                    if (!dataBuffer.equals("")) {
                        String response = dataBuffer;
                        pbar.setVisibility(View.GONE);
                        snackbar.show();
                        goToAdapter(response);
                    } else {
                        error.printStackTrace();
                        pbar.setVisibility(View.GONE);
                        snackbar.show();

                        TextView tvError = (TextView) findViewById(R.id.tv_error_home);
                        tvError.setVisibility(View.VISIBLE);
                    }
                } catch (EmptyStackException e) {
                    error.printStackTrace();
                    pbar.setVisibility(View.GONE);
                    snackbar.show();

                    TextView tvError = (TextView) findViewById(R.id.tv_error_home);
                    tvError.setVisibility(View.VISIBLE);
                }
            }
        });
        MySingleton.getInstance(context).addToRequestQueue(jsonObjReq);
    }

    private void goToAdapter(String response) {

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.content_home);
        recyclerView.setAdapter(new HomeAdapter(response, this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        pbar.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void checkVersionMatch(final String version) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")//https://students.iitm.ac.in/studentsapp/general/version_check.php

                .authority("students.iitm.ac.in")
                .appendPath("studentsapp")
                .appendPath("general")
                .appendPath("version_check.php");
        final String url = builder.build().toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsArray = new JSONArray(response);
                            JSONObject jsObject = jsArray.getJSONObject(0);
                            String ver = jsObject.getString("version");
                            fm = getSupportFragmentManager();
                            if (Integer.parseInt(ver) == 1) {
                                DialogFragment optionalUpdateDialogFragment = new OptionalUpdateDialogFragment();
                                optionalUpdateDialogFragment.setCancelable(false);
                                if (optionalUpdateDialogCount == 0) {
                                    optionalUpdateDialogFragment.show(fm, "OptionalUpdateDialogFragment");
                                    optionalUpdateDialogCount++;
                                }


                            } else if (Integer.parseInt(ver) == 2) {

                                DialogFragment forceUpdateDialogFragment = new ForceUpdateDialogFragment();
                                forceUpdateDialogFragment.setCancelable(false);
                                forceUpdateDialogFragment.show(fm, "ForceUpdateDialogFragment");

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                //Snackbar snackbar = Snackbar.make("Internet Connection Failed.", Snackbar.LENGTH_SHORT);
                //snackbar.show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("version", version);
                return params;
            }
        };
// Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void sendRegistrationToServer(final String refreshedToken, final String name, final String roll_no) {
        // Instantiate the RequestQueue.
        //Toast.makeText(this,refreshedToken, Toast.LENGTH_SHORT).show();
        final String url = getString(R.string.url_register_fcm);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(HomeActivity.this, response, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(HomeActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", refreshedToken);
                params.put("rollno", roll_no);
                params.put("name", name);
                return params;
            }
        };
        // Add the request to the RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);

        int MY_SOCKET_TIMEOUT_MS = 10000;
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
            Intent intent = new Intent(HomeActivity.this, AboutUsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_contact) {
            Intent intent = new Intent(HomeActivity.this, ContactUsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_log_out) {
            LogOutAlertClass lg = new LogOutAlertClass();
            lg.isSure(HomeActivity.this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updatePreferences() {
        final ArrayList<HashMap<String, String>> database_topics = new ArrayList<>();

        final SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        final SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with the response
                        try {
                            JSONArray jsArray = new JSONArray(response);
                            for (int i = 0; i < jsArray.length(); i++) {
                                JSONObject jsObject = jsArray.getJSONObject(i);
                                HashMap<String, String> hashMap = new HashMap<>();

                                hashMap.put("topic", jsObject.getString("topic"));
                                hashMap.put("value", jsObject.getString("value"));

                                database_topics.add(hashMap);
                            }

                            for (HashMap<String, String> hashMap : database_topics) {

                                if (Integer.parseInt(hashMap.get("value")) == 0) {

                                    FirebaseMessaging.getInstance().unsubscribeFromTopic(hashMap.get("topic"));
                                    editor.remove(hashMap.get("topic"));
                                    editor.apply();
                                } else if (Integer.parseInt(hashMap.get("value")) == 1 && !prefs.contains(hashMap.get("topic"))) {

                                    FirebaseMessaging.getInstance().subscribeToTopic(hashMap.get("topic"));
                                    editor.putBoolean(hashMap.get("topic"), true);
                                    editor.apply();
                                }


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // Handle error
                        //Toast.makeText(context, "Couldn't connect to internet.", Toast.LENGTH_SHORT).show();
                    }
                });
        MySingleton.getInstance(HomeActivity.this).addToRequestQueue(stringRequest);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent = new Intent();
        boolean flag = false;
        final Context context = HomeActivity.this;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (id == R.id.nav_home) {
            // Handle the camera action
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
            intent.putExtra("knock_knock", "home");
            flag = true;

        } else if (id == R.id.nav_about) {
            intent = new Intent(context, AboutUsActivity.class);
            flag = true;

        } else if (id == R.id.nav_subscriptions) {
            intent = new Intent(context, SubscriptionActivity.class);
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
                            lg.isSure(HomeActivity.this);
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

    public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

        Context context;
        String response;

        private ArrayList<String> titles = new ArrayList<>();
        private ArrayList<String> details = new ArrayList<>();
        private ArrayList<String> image_urls = new ArrayList<>();
        private ArrayList<String> cats = new ArrayList<>();
        private ArrayList<String> createdat = new ArrayList<>();
        private ArrayList<String> subscribed = new ArrayList<>();
        private ArrayList<String> links = new ArrayList<>();
        private ImageLoader imageLoader;


        public HomeAdapter(String response, Context _context) {

            this.response = response;
            context = _context;

            imageLoader = MySingleton.getInstance(context).getImageLoader();
            SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            Map<String, ?> allEntries = prefs.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                if (entry.getValue().toString().equals("true")) {
                    subscribed.add(entry.getKey());
                }
            }

            setUpData();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_feed, parent, false);


            return new ViewHolder(view);
        }

        private void setUpData() {

            try {

                JSONArray jsonArray = new JSONArray(response);
                JSONObject jsonObject;
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    String cat = jsonObject.getString("topic");

                    Log.d("tada", subscribed.toString());

                    if (subscribed.size() != 0) {
                        if (subscribed.contains(cat)) {
                            titles.add(jsonObject.getString("title"));
                            details.add(jsonObject.getString("description"));
                            image_urls.add(jsonObject.getString("url"));
                            cats.add(cat);
                            links.add(jsonObject.getString("link"));
                            createdat.add(jsonObject.getString("created_at"));
                        }

                    } else {
                        links.add(jsonObject.getString("link"));
                        titles.add(jsonObject.getString("title"));
                        details.add(jsonObject.getString("description"));
                        cats.add(cat);
                        createdat.add(jsonObject.getString("created_at"));
                        image_urls.add(jsonObject.getString("url"));
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            final String title = titles.get(position);
            final String detail = details.get(position);
            final String image_url = image_urls.get(position);
            final String link = links.get(position);
            final String topic = cats.get(position);
            Log.d("pani", title + " : " + link);

            holder.tvTitle.setText(title);
            holder.tvDetails.setText(detail);
            holder.ivHomeFeed.setImageUrl(image_url, imageLoader);
            holder.ivHomeFeed.setDefaultImageResId(R.mipmap.ic_launcher);
            holder.ivHomeFeed.setErrorImageResId(R.mipmap.ic_launcher);

            if (link.equals("nada")) {
                holder.iv_link.setVisibility(View.INVISIBLE);

                holder.rlHomeFeed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (topic) {
                            case "eml": {
                                Intent intent = new Intent(context, EMLActivity.class);
                                context.startActivity(intent);
                                break;
                            }
                            case "t5e": {
                                Intent intent = new Intent(context, T5EActivity.class);
                                context.startActivity(intent);
                                break;
                            }
                            case "schroeter": {
                                Intent intent = new Intent(context, SchroeterActivity.class);
                                context.startActivity(intent);
                                break;
                            }
                            default: {
                                try {
                                    HomeActivity.showAlert(HomeActivity.this, title, detail);
                                } catch (Exception e) {
                                    Log.e("tada", "home fragment", e);
                                }
                                break;
                            }
                        }

                    }
                });
            } else {

                Log.d("pika", title + " : link image visible");
                holder.iv_link.setVisibility(View.VISIBLE);

                holder.rlHomeFeed.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        try {
                            HomeActivity.showAlert(HomeActivity.this, title, detail, link);
                        } catch (Exception e) {
                            Log.e("tada", "home fragment", e);
                        }

                    }
                });
            }


        }

        @Override
        public int getItemCount() {
            return titles.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvTitle, tvDetails;
            RelativeLayout rlHomeFeed;
            NetworkImageView ivHomeFeed;
            CardView cvhome;
            ImageView iv_link;

            ViewHolder(View itemView) {
                super(itemView);

                tvTitle = (TextView) itemView.findViewById(R.id.text_title_home_feed);
                tvDetails = (TextView) itemView.findViewById(R.id.tv_home_details);
                rlHomeFeed = (RelativeLayout) itemView.findViewById(R.id.rl_home_feed);
                ivHomeFeed = (NetworkImageView) itemView.findViewById(R.id.iv_home_feed_icon);
                cvhome = (CardView) itemView.findViewById(R.id.cl_home_feed);
                iv_link = (ImageView) itemView.findViewById(R.id.link_image_view);

            }


        }

    }

}
