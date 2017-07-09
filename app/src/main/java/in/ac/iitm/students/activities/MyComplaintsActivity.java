package in.ac.iitm.students.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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
import java.util.Map;

import in.ac.iitm.students.R;
import in.ac.iitm.students.activities.main.MessAndFacilitiesActivity;
import in.ac.iitm.students.adapters.ThreadAdapter;
import in.ac.iitm.students.objects.ThreadObject;
import in.ac.iitm.students.others.MySingleton;
import in.ac.iitm.students.others.UtilStrings;
import in.ac.iitm.students.others.Utils;

/**
 * Created by sai_praneeth7777 on 18-Jun-16.
 */
public class MyComplaintsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    JSONObject obj;
    JSONArray arr;
    ThreadAdapter content_adapter;
    ListView listView;
    ArrayList<String> titles = new ArrayList<String>();
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_complaints);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar5);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(MyComplaintsActivity.this);

        getContent();
    }

    private void getContent() {
        final String roll_no = Utils.getprefString(UtilStrings.ROLLNO, this);
        // Instantiate the RequestQueue.

        final ProgressDialog pDialog = new ProgressDialog(MyComplaintsActivity.this);
        pDialog.setMessage("Getting data...");
        pDialog.show();
        pDialog.setCancelable(false);

        final TextView tvNoComplaints = (TextView) findViewById(R.id.tv_no_complaints);

        final String url = getString(R.string.url_get_threads);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //Toast.makeText(MyComplaintsActivity.this,response.toString(),Toast.LENGTH_SHORT).show();
                        content_adapter = new ThreadAdapter(MyComplaintsActivity.this, R.layout.list_row_thread);
                        listView = (ListView) findViewById(R.id.threadList12);
//                        Log.d("complaint", response);
                        //  listView.setOnItemClickListener(new ListAction());
                        if (response.equals("null"))
                            tvNoComplaints.setText(R.string.error_no_complaints);
                        try {
                            arr = new JSONArray(response.toString());
                            String subject, user, date, time, body, thread_id, messName, compCategory = "Complaint category appears here", informed = "Informed";
                            String solved, solved_by;
//                            Log.d("complaints", response);
                            for (int i = 0; i < arr.length(); i++) {
                                obj = arr.getJSONObject(i);
                                subject = obj.getString("subject");
                                body = obj.getString("body");
                                user = obj.getString("user");
                                date = obj.getString("date");
                                time = obj.getString("time");
                                thread_id = obj.getString("thread_id");
                                messName = obj.getString("mess_name");
                                solved = obj.getString("solved");
                                solved_by = obj.getString("resolved_by");
                                compCategory = obj.getString("cc");
                                informed = obj.getString("isinformed");
                                if (Integer.parseInt(informed) == 0) {
                                    informed = "Not informed";
                                } else {
                                    informed = "Informed";
                                }
                                titles.add(subject);
//                                solved = obj.getString("is_resolved");
//                                solved_by = obj.getString("resolved_by");
                                ThreadObject content = new ThreadObject(subject, body, date, time, user, thread_id, messName, solved, solved_by, compCategory, informed);
                                content_adapter.add(content);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            tvNoComplaints.setVisibility(View.VISIBLE);
                            if (response.equals("null"))
                                tvNoComplaints.setText(R.string.error_no_complaints);
                            else
                                tvNoComplaints.setText(R.string.error_parsing);
                            pDialog.dismiss();
                        }
                        listView.setAdapter(content_adapter);

                        pDialog.dismiss();
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {
                                TextView textViewId = (TextView) view.findViewById(R.id.thread_id);
                                String thread_id = textViewId.getText().toString();
                                String title = titles.get(position);
                                //Toast.makeText(view.getContext(),text,Toast.LENGTH_SHORT).show();
                                getThreadMessages(thread_id, title);
                            }
                        });


                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                tvNoComplaints.setVisibility(View.VISIBLE);
                tvNoComplaints.setText(R.string.error_connection);
                pDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("rollno", roll_no);
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

    private void getThreadMessages(final String thread_id, String title) {
        Intent i = new Intent(this, MessageChatActivity.class);
        i.putExtra("thread_id", thread_id);
        i.putExtra("subject", title);
        startActivity(i);
    }


    @Override
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MyComplaintsActivity.this, MessAndFacilitiesActivity.class);
        startActivity(intent);
    }
}
