package in.ac.iitm.students.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.ac.iitm.students.R;
import in.ac.iitm.students.others.MySingleton;
import in.ac.iitm.students.others.UtilStrings;
import in.ac.iitm.students.others.Utils;

/**
 * Created by sai_praneeth7777 on 15-Jun-16.
 */
public class ComplaintActivity extends AppCompatActivity {
    final List<String> complaintIdList = new ArrayList<>();
    CoordinatorLayout coordinatorLayout;
    private String id;
    //    private int gotcha=0;
    private CheckBox checkBox;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        checkBox = (CheckBox) findViewById(R.id.checkBoxComplaint);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.cl_add_complaint);

        getComplaintCategories();

        Intent i = getIntent();
        String name = i.getStringExtra("name");
        String type = i.getStringExtra("type");
        String id = i.getStringExtra("id");
        setHead(name, type);
        setId(id);

    }

    private void getComplaintCategories() {
        final List<String> list = new ArrayList<>();

        final Spinner spinner = (Spinner) findViewById(R.id.spinnerComplaint);
        final TextView tvNoConnection = (TextView) findViewById(R.id.tv_no_connection_complaint);
        final Button submitButton = (Button) findViewById(R.id.postComplaint);
        final ProgressBar progressbar = (ProgressBar) findViewById(R.id.pb_complaint_category);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_complaint_spinner, list);

        list.add("Select complaint category");

        String url = getString(R.string.url_complaint_categories);
//        Log.d("URL", url);
        // Request a string response from the provided URL.
        StringRequest jsonObjReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {

                    Log.d("complaint", response);
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject;

                    String category;
                    String complaintId;
                    int i;
                    for (i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        category = jsonObject.getString("name");
                        complaintId = jsonObject.getString("id");

                        list.add(category);//+", "+studRoll
                        complaintIdList.add(complaintId);
                    }

                    progressbar.setVisibility(View.GONE);
                    submitButton.setVisibility(View.VISIBLE);
                    tvNoConnection.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressbar.setVisibility(View.GONE);
                    submitButton.setVisibility(View.GONE);
                    tvNoConnection.setText(R.string.error_parsing);
                    tvNoConnection.setVisibility(View.VISIBLE);
                    spinner.setVisibility(View.GONE);
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, getString(R.string.error_parsing), Snackbar.LENGTH_LONG);
                    snackbar.show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                progressbar.setVisibility(View.GONE);
                submitButton.setVisibility(View.GONE);
                tvNoConnection.setText(R.string.error_connection);
                tvNoConnection.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.GONE);
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, getString(R.string.error_connection), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
        MySingleton.getInstance(this).addToRequestQueue(jsonObjReq);

        assert spinner != null;
        spinner.setAdapter(adapter);
        spinner.setPrompt("Select a category");
    }

    private void setHead(String name, String type) {
        TextView txt = (TextView) findViewById(R.id.typename);
        txt.setText(name);
        TextView head = (TextView) findViewById(R.id.typeType);
        head.setText(type);
    }

    public void onPostClick(View v) {
        EditText subject = (EditText) findViewById(R.id.new_complaint_title);
        EditText body = (EditText) findViewById(R.id.desc);
        TextView header = (TextView) findViewById(R.id.typename);
        Spinner spinner = (Spinner) findViewById(R.id.spinnerComplaint);


        int isEmptyInt = checkIfEmpty(subject.getText().toString(), body.getText().toString());
        if (isEmptyInt == 0) {
            subject.setHintTextColor(Color.parseColor("#F44336"));
            body.setHintTextColor(Color.parseColor("#F44336"));
            makeSnackbar("Subject and description cannot be empty.");
        } else if (isEmptyInt == 1) {
            subject.setHintTextColor(Color.parseColor("#F44336"));
            makeSnackbar("Subject cannot be empty.");
        } else if (isEmptyInt == 2) {
            body.setHintTextColor(Color.parseColor("#F44336"));
            makeSnackbar("Description cannot be empty.");
        } else if (spinner.getSelectedItemPosition() == 0) {
            makeSnackbar("Select complaint category.");
        } else {
            if (checkBox.isChecked()) {
                postMail(subject.getText().toString(), body.getText().toString(), header.getText().toString(), spinner.getSelectedItemPosition());

            } else {
                makeSnackbar("Please select the check box.");
            }
        }
        // To dismiss the dialog
    }

    private void postMail(final String sub, final String body, final String messname, int spinnerSelectedPosition) {

        // Instantiate the RequestQueue.
        final String url = getString(R.string.url_complaint_post);
        // Request a string response from the provided URL.
        final String roll_no = Utils.getprefString(UtilStrings.ROLLNO, this);
        final String name = Utils.getprefString(UtilStrings.NAME, this);
        final String id = getId();
        final String complaintId = complaintIdList.get(spinnerSelectedPosition - 1);

        /*CheckBox checkBox = (CheckBox) findViewById(R.id.checkBoxComplaint);
        final String isChecboxSelected;
        if (checkBox.isChecked()) {
            isChecboxSelected = "1";
        } else {
            isChecboxSelected = "0";
        }
        */

        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Sending data...");
        progress.setCancelable(false);
        progress.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        // Display the first 500 characters of the response string.
                        if (response.equals("ERROR")) {
                            makeSnackbar(getString(R.string.error_sending));

                        } else {
                            Intent i = new Intent(ComplaintActivity.this, MyComplaintsActivity.class);
                            startActivity(i);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
                makeSnackbar(getString(R.string.error_connection));
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("sub", sub);
                params.put("body", body);
                params.put("id", id);
                params.put("name", name);
                params.put("roll_no", roll_no);
                params.put("ccid", complaintId);
                params.put("isinformed", "1"); //1 if informed, 0 if not informed
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private int checkIfEmpty(String subjectString, String bodyString) {
        if (subjectString.length() == 0 && bodyString.length() == 0) return 0;
        if (subjectString.length() == 0) return 1;
        if (bodyString.length() == 0) return 2;
        return 3;
    }

    private void makeSnackbar(String msg) {

        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, msg, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
