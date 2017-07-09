package in.ac.iitm.students.activities;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import in.ac.iitm.students.R;
import in.ac.iitm.students.activities.main.HomeActivity;
import in.ac.iitm.students.others.MySingleton;

/**
 * Created by admin on 30-01-2017.
 */

public class SubscriptionActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    private Context context;
    private HashMap<String, Boolean> subscriptionPref = new HashMap<>();

    private ArrayList<HashMap<String, String>> user_topics = new ArrayList<>();
    private int fromhome;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        fromhome = intent.getIntExtra("fromhome", 0);

        context = this;
        String url = "https://students.iitm.ac.in/studentsapp/general/subs.php";
        final ArrayList<HashMap<String, String>> database_topics = new ArrayList<>();

        final SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

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

                                hashMap.put("name", jsObject.getString("name"));
                                hashMap.put("topic", jsObject.getString("topic"));
                                hashMap.put("value", jsObject.getString("value"));

                                database_topics.add(hashMap);
                            }

                            for (HashMap<String, String> hashMap : database_topics) {
                                if (Integer.parseInt(hashMap.get("value")) == 1) {

                                    subscriptionPref.put(hashMap.get("topic"), prefs.getBoolean(hashMap.get("topic"), true));
                                    user_topics.add(hashMap);
                                } else if (Integer.parseInt(hashMap.get("value")) == 0) {

                                    FirebaseMessaging.getInstance().unsubscribeFromTopic(hashMap.get("topic"));
                                    editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                    editor.remove(hashMap.get("topic"));
                                    editor.apply();
                                }


                            }

                            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll_subscribe_list);
                            LinearLayout.LayoutParams rlParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            rlParams.setMargins(8, 0, 8, 48);

                            RelativeLayout.LayoutParams textView_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            textView_params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                            textView_params.addRule(RelativeLayout.ALIGN_PARENT_START);

                            RelativeLayout.LayoutParams switchCompat_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            switchCompat_params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

                            for (int i = 0; i < user_topics.size(); i++) {
                                HashMap<String, String> hashMap = user_topics.get(i);

                                RelativeLayout rl = new RelativeLayout(context);
                                rl.setLayoutParams(rlParams);
                                rl.setPadding(8, 16, 8, 16);

                                TextView textView = new TextView(context);
                                textView.setText(hashMap.get("name"));

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    textView.setTextAppearance(R.style.TextAppearance_AppCompat_Caption);
                                    textView.setTextSize(14);
                                }

                                if (hashMap.get("topic").equals("general")) {
                                    textView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                                }
                                textView.setPadding(8, 0, 0, 0);
                                textView.setLayoutParams(textView_params);
                                rl.addView(textView);


                                SwitchCompat switchCompat = new SwitchCompat(context);
                                switchCompat.setOnCheckedChangeListener(SubscriptionActivity.this);
                                switchCompat.setTag(hashMap.get("topic"));
                                switchCompat.setChecked(subscriptionPref.get(hashMap.get("topic")));
                                switchCompat.setLayoutParams(switchCompat_params);
                                rl.addView(switchCompat);

                                linearLayout.addView(rl);

                            }
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll_subscribe_list);
                            LinearLayout.LayoutParams tv_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            TextView tv = new TextView(SubscriptionActivity.this);
                            tv.setText("Error loading this page.");
                            tv.setLayoutParams(tv_params);
                            linearLayout.addView(tv);
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll_subscribe_list);
                        LinearLayout.LayoutParams tv_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        TextView tv = new TextView(SubscriptionActivity.this);
                        tv.setText("No Internet Connection :(");
                        tv.setLayoutParams(tv_params);
                        linearLayout.addView(tv);
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }


                        // Handle error
                        //Toast.makeText(context, "Couldn't connect to internet.", Toast.LENGTH_SHORT).show();
                    }
                });
        MySingleton.getInstance(context).addToRequestQueue(stringRequest);


    }

    @Override
    public void onPause() {
        super.onPause();

        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        for (int i = 0; i < user_topics.size(); i++) {
            HashMap<String, String> hashMap = user_topics.get(i);
            editor.putBoolean(hashMap.get("topic"), subscriptionPref.get(hashMap.get("topic")));

            Log.d("tada", hashMap.get("topic") + ":" + subscriptionPref.get(hashMap.get("topic")));
        }
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        if (getIntent().hasExtra("knock_knock")) {
            Intent intent = new Intent(SubscriptionActivity.this, HomeActivity.class);
            startActivity(intent);
        } else super.onBackPressed();  // optional depending on your needs
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!isChecked) {
            if (buttonView.getTag().toString().equals("general")) {
                buttonView.setChecked(true);
                Snackbar.make(findViewById(R.id.coordinator_layout_subscribe), "Cannot Un-subscribe :|", Snackbar.LENGTH_LONG).show();
            } else {
                buttonView.setChecked(false);
                subscriptionPref.remove(buttonView.getTag().toString());
                subscriptionPref.put(buttonView.getTag().toString(), false);
                FirebaseMessaging.getInstance().unsubscribeFromTopic(buttonView.getTag().toString());
            }

        } else {
            buttonView.setChecked(true);
            subscriptionPref.remove(buttonView.getTag().toString());
            subscriptionPref.put(buttonView.getTag().toString(), true);
            FirebaseMessaging.getInstance().subscribeToTopic(buttonView.getTag().toString());
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                if (getIntent().hasExtra("knock_knock")) {
                    Intent intent = new Intent(SubscriptionActivity.this, HomeActivity.class);
                    startActivity(intent);
                } else onBackPressed();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
