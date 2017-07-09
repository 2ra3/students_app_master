package in.ac.iitm.students.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.ac.iitm.students.R;
import in.ac.iitm.students.activities.main.MessAndFacilitiesActivity;
import in.ac.iitm.students.adapters.AdapterClass;
import in.ac.iitm.students.objects.ObjectClass;
import in.ac.iitm.students.others.MySingleton;

/**
 * Created by sai_praneeth7777 on 11-Jun-16.
 */
public class MessOrFacilitiesListActivity extends AppCompatActivity {
    ListView listView1;
    JSONObject obj;
    JSONArray arr;
    AdapterClass content_adapter;
    ListView listView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess_or_facilities_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        String type = i.getStringExtra("type");
        fetchData(type);

        listView1 = (ListView) findViewById(R.id.listView);
        //  listView1.setOnItemClickListener(new ListAction());
    }

    private void fetchData(final String type) {

        final ProgressDialog pDialog = new ProgressDialog(MessOrFacilitiesListActivity.this);
        pDialog.setMessage("Getting data...");
        pDialog.show();
        pDialog.setCancelable(false);

        final TextView message = (TextView) findViewById(R.id.tv_mess_list_msg);
        message.setVisibility(View.GONE);
        // Instantiate the RequestQueue.
        final String url = getString(R.string.url_fetch_list);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + type,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        content_adapter = new AdapterClass(MessOrFacilitiesListActivity.this, R.layout.activity_mess_or_facilities_list);
                        listView = (ListView) findViewById(R.id.listView);
                        //  listView.setOnItemClickListener(new ListAction());
                        listView.setAdapter(content_adapter);
                        try {
                            arr = new JSONArray(response.toString());
                            String name, id;
                            for (int i = 0; i < arr.length(); i++) {
                                obj = arr.getJSONObject(i);
                                name = obj.getString("name");
                                id = obj.getString("id");
                                ObjectClass content = new ObjectClass(name, id, type);
                                content_adapter.add(content);
                                pDialog.dismiss();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            message.setText(getString(R.string.error_parsing));
                            message.setVisibility(View.VISIBLE);
                            pDialog.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                message.setText(getString(R.string.error_connection));
                message.setVisibility(View.VISIBLE);
                pDialog.dismiss();
            }
        });
        // Add the request to the RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    //
//    class ListAction implements AdapterView.OnItemClickListener{
//      @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
//          ViewGroup vg =(ViewGroup)view;
//          Button bt = (Button) findViewById(R.id.mess);      }
//    };
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
        Intent intent = new Intent(MessOrFacilitiesListActivity.this, MessAndFacilitiesActivity.class);
        startActivity(intent);
    }

}
