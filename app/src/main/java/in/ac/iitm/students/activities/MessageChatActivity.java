package in.ac.iitm.students.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.util.HashMap;
import java.util.Map;

import in.ac.iitm.students.R;
import in.ac.iitm.students.adapters.ChatAdapter;
import in.ac.iitm.students.objects.ChatObject;
import in.ac.iitm.students.others.MySingleton;
import in.ac.iitm.students.others.UtilStrings;
import in.ac.iitm.students.others.Utils;

/**
 * Created by sai_praneeth7777 on 26-Jun-16.
 */
public class MessageChatActivity extends AppCompatActivity {

    ImageView send_message;
    TextView tvMessage;

    JSONObject obj;
    JSONArray arr;
    ChatAdapter content_adapter;
    ListView listView;
    CoordinatorLayout coordinatorLayout;
    private String threadId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_chat);

        Intent i = getIntent();
        final String thread_id = i.getStringExtra("thread_id");
        String subject = i.getStringExtra("subject");

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.cl_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar6);
        toolbar.setTitle(subject);
        Log.d("chatMessage", "Subject = " + subject);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String solved = i.getStringExtra("solved");
        final String solvedBy = i.getStringExtra("solvedBy");

        TextView complaint = (TextView) findViewById(R.id.complaint_resolved);
        LinearLayout textLayout = (LinearLayout) findViewById(R.id.listFooter);
        if (Integer.valueOf(solved) == 0) {
            complaint.setVisibility(View.GONE);
        } else {
            textLayout.setVisibility(View.GONE);
            complaint.setText("Complaint is resolved by - " + solvedBy);
        }
        setThreadId(thread_id);
        fetchMessages(thread_id);

        send_message = (ImageView) findViewById(R.id.sendButton);
        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvMessage = (TextView) findViewById(R.id.messageInput);

                String message = tvMessage.getText().toString().trim();
                if (message.length() != 0) {
//                    InputMethodManager inputManager = (InputMethodManager)
//                            getSystemService(Context.INPUT_METHOD_SERVICE);
//
//                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
//                            InputMethodManager.HIDE_NOT_ALWAYS);
                    sendMessage(tvMessage.getText().toString(), thread_id);
                }
                tvMessage.setText("");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.complaint_resolve: {

                final String url = getString(R.string.url_complaint_resolve);
                final String roll_no = Utils.getprefString(UtilStrings.ROLLNO, this);
                final String thread_id = getThreadId();
//                Toast.makeText(MessageChatActivity.this, thread_id, Toast.LENGTH_SHORT).show();
                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.
//                                Toast.makeText(MessageChatActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                                Log.d("complaint", response);
                                Intent intent = new Intent(MessageChatActivity.this, MyComplaintsActivity.class);
                                startActivity(intent);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, getString(R.string.error_connection), Snackbar.LENGTH_LONG);
                        snackbar.show();
//                        Toast.makeText(MessageChatActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("thread_id", thread_id);
                        params.put("roll_no", roll_no);
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

                finish();
                break;
            }
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }

    private void fetchMessages(final String thread_id) {
        // Instantiate the RequestQueue.
        final String url = getString(R.string.url_get_messages);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
//                        Toast.makeText(MessageChatActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                        content_adapter = new ChatAdapter(MessageChatActivity.this, R.layout.activity_message_chat);
                        listView = (ListView) findViewById(R.id.list_message);
                        //  listView.setOnItemClickListener(new ListAction());
                        listView.setAdapter(content_adapter);

                        try {
                            arr = new JSONArray(response.toString());
                            String subject, user, date, time, body, thread_id;
                            for (int i = 0; i < arr.length(); i++) {
                                obj = arr.getJSONObject(i);
                                body = obj.getString("body");
                                date = obj.getString("date");
                                time = obj.getString("time");
                                user = obj.getString("user");
                                ChatObject content = new ChatObject(body, time, date, user);
                                content_adapter.add(content);
                            }
                            int position = listView.getBottom();
                            listView.smoothScrollToPosition(position);

                        } catch (JSONException e) {
                            Snackbar snackbar = Snackbar
                                    .make(coordinatorLayout, getString(R.string.error_parsing), Snackbar.LENGTH_LONG);
                            snackbar.show();
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, getString(R.string.error_connection), Snackbar.LENGTH_LONG);
                snackbar.show();
//                Toast.makeText(MessageChatActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("thread_id", thread_id);
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


    private void sendMessage(final String message, final String thread_id) {
        //   getActionBar().hide();
//        Toast.makeText(MessageChatActivity.this, message, Toast.LENGTH_SHORT).show();
        // Instantiate the RequestQueue.

        final String url = getString(R.string.url_send_message);
        final String roll_no = Utils.getprefString(UtilStrings.ROLLNO, this);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //Toast.makeText(MessageChatActivity.this,response.toString(),Toast.LENGTH_SHORT).show();
                        fetchMessages(thread_id);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, getString(R.string.error_connection), Snackbar.LENGTH_LONG);
                snackbar.show();
//                Toast.makeText(MessageChatActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("thread_id", thread_id);
                params.put("message", message);
                params.put("roll_no", roll_no);
                return params;
            }
        };
        // Add the request to the RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
//
//        int MY_SOCKET_TIMEOUT_MS = 10000;
//        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
//                MY_SOCKET_TIMEOUT_MS,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }
}
