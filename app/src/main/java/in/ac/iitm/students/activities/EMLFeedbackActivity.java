package in.ac.iitm.students.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import in.ac.iitm.students.R;
import in.ac.iitm.students.others.MySingleton;
import in.ac.iitm.students.others.UtilStrings;
import in.ac.iitm.students.others.Utils;

public class EMLFeedbackActivity extends AppCompatActivity {
    CoordinatorLayout coordinatorLayout;
    String id;
    EditText etFeedback;
    int isUpcoming;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emlfeedback);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.cl_eml_feedback);

        Intent intent = getIntent();
        String name = intent.getStringExtra("speaker_name");
        String bio = intent.getStringExtra("speaker_bio");
        String photo = intent.getStringExtra("photo_url");
        String topic = intent.getStringExtra("topic");
        id = intent.getStringExtra("ID");
        isUpcoming = intent.getIntExtra("isUpcoming", 0);
        int color = intent.getIntExtra("color", R.color.lightGreen);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        TextView tvTitle = (TextView) findViewById(R.id.tv_title_feedback);
        TextView tvTopic = (TextView) findViewById(R.id.tv_topic_feedback);
        etFeedback = (EditText) findViewById(R.id.et_feedback);
        Button button = (Button) findViewById(R.id.b_submit_feedback);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String feedback = etFeedback.getText().toString();
                if (feedback.trim().length() == 0) {
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Field cannot be blank.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else submitFeedback(feedback);
            }
        });

        if (isUpcoming == 1) {
            toolbar.setTitle("Questionnaire");
            etFeedback.setHint("Ask your question here");
        } else {
            toolbar.setTitle("Feedback");
            etFeedback.setHint("Your feedback here");
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvTitle.setText(name);
        tvTopic.setText(topic);

    }

    private void submitFeedback(final String feedback) {
        String url = "";
        if (isUpcoming == 1) {
            // questionnaire
            url = "https://students.iitm.ac.in/studentsapp/eml/questionaire.php";
        } else {
            url = "https://students.iitm.ac.in/studentsapp/eml/feedback.php";
        }

        final ProgressDialog pDialog = new ProgressDialog(EMLFeedbackActivity.this);
        pDialog.setMessage("Sending data...");
        pDialog.show();
        pDialog.setCancelable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if (response.equals("success")) {
                            pDialog.dismiss();

                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(etFeedback.getWindowToken(), 0);

                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("showSnackbar", 1);
                            setResult(Activity.RESULT_OK, resultIntent);
                            finish();

                        } else {
                            pDialog.dismiss();
                            Snackbar snackbar = Snackbar
                                    .make(coordinatorLayout, "Error sending data, please try again later...", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, getString(R.string.error_connection), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                String roll_no = Utils.getprefString(UtilStrings.ROLLNO, EMLFeedbackActivity.this);
                params.put("roll", roll_no);
                params.put("id", id);
                if (isUpcoming == 1) {
                    params.put("question", feedback);
                } else
                    params.put("feedback", feedback);
                return params;
            }

        };
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
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
}
