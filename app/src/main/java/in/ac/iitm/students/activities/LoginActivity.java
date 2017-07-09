package in.ac.iitm.students.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.ac.iitm.students.R;
import in.ac.iitm.students.activities.main.HomeActivity;
import in.ac.iitm.students.others.MySingleton;
import in.ac.iitm.students.others.UtilStrings;
import in.ac.iitm.students.others.Utils;

/**
 * Created by sai_praneeth7777 on 03-Sep-16.
 */
public class LoginActivity extends AppCompatActivity {
    ProgressDialog progress;
    EditText username, password;
    Button login;
    private Class<?> cls;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        String cls_name = "HomeActivity";
        if (getIntent().hasExtra("class")) {
            cls_name = getIntent().getExtras().get("class").toString();
        }
        cls = HomeActivity.class;
        try {
            cls = Class.forName(cls_name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        username = (EditText) this.findViewById(R.id.rollno);
        password = (EditText) this.findViewById(R.id.password);
        login = (Button) this.findViewById(R.id.loginButton);

        String roll_no = Utils.getprefString(UtilStrings.ROLLNO, this);
        String name = Utils.getprefString(UtilStrings.NAME, this);
        if (!roll_no.equals("") && !name.equals("")) {
            Intent downloadIntent;
            downloadIntent = new Intent(getBaseContext(), cls);
            startActivity(downloadIntent);
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.getText().toString().trim().length() > 0 && password.getText().toString().trim().length() > 0) {
                    if (Utils.isNetworkAvailable(getBaseContext())) {
                        progress = new ProgressDialog(LoginActivity.this);
                        progress.setCancelable(false);
                        progress.setMessage("Logging in...");
                        progress.show();
                        PlacementLdaplogin(getBaseContext());
                    } else {
                        MakeSnSnackbar("No internet connection");
                    }

                } else {
                    MakeSnSnackbar("Enter your username and password");
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void PlacementLdaplogin(final Context context) {

        final String[] message = new String[1];
        final String[] DisplayName = new String[1];
        final String[] Hostel = new String[1];
        final int[] success = new int[1];
        final JSONObject[] responseJson = new JSONObject[1];


        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                getString(R.string.LoginURl),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            responseJson[0] = new JSONObject(response);
                            success[0] = responseJson[0].getInt("success");
                            message[0] = responseJson[0].getString("message");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        if (success[0] == 1) {
                            JSONObject jsonResultObjuct = null;
                            try {
                                jsonResultObjuct = responseJson[0].getJSONArray("result").getJSONObject(0);
                                DisplayName[0] = jsonResultObjuct.getString("fullname");
                                Hostel[0] = jsonResultObjuct.getString("hostel");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // Log.d("valid login", responseBody + "ok");
                            Intent downloadIntent;
                            downloadIntent = new Intent(getBaseContext(), HomeActivity.class);
                            startActivity(downloadIntent);
                            Utils.saveprefString(UtilStrings.NAME, DisplayName[0], getBaseContext());
                            Utils.saveprefString(UtilStrings.HOSTEl, Hostel[0], getBaseContext());
                            Utils.saveprefString(UtilStrings.ROLLNO, username.getText().toString().toUpperCase(), getBaseContext());
                            Utils.saveprefBool(UtilStrings.LOGEDIN, true, context);

                            finish();
                        } else if (success[0] == 0) {
                            MakeSnSnackbar(message[0]);
                            Log.d("invalid login", response + "Error connecting to server !!");
                            Utils.clearpref(context);
                        } else {
                            MakeSnSnackbar(getString(R.string.error_connection));
                            Log.d("invalid login", response + "Error connecting to server !!");
                            Utils.clearpref(context);
                        }
                        progress.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MakeSnSnackbar(getString(R.string.error_connection));
                Log.d("invalid login: ", error.toString() + "; Error connecting to server!");
                Utils.clearpref(context);
                progress.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("roll", username.getText().toString());
                params.put("pass", password.getText().toString());
                return params;
            }

        };
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void MakeSnSnackbar(String text) {
        hideKeyboard();
        Snackbar snack = Snackbar.make((LinearLayout) findViewById(R.id.container), text, Snackbar.LENGTH_LONG);
        ViewGroup group = (ViewGroup) snack.getView();
        for (int i = 0; i < group.getChildCount(); i++) {
            View v = group.getChildAt(i);
            if (v instanceof TextView) {
                TextView t = (TextView) v;
                t.setTextColor(Color.RED);
            }
        }
        snack.show();
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
