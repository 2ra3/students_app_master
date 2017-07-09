package in.ac.iitm.students.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.ac.iitm.students.R;
import in.ac.iitm.students.activities.StudentDetailsActivity;
import in.ac.iitm.students.others.MySingleton;


public class NameSearchFragment extends Fragment {

    ListView lvSuggestion;
    ArrayAdapter<String> adapter;
    ArrayList<String> listSuggestion = new ArrayList<>(25);
    EditText etSearch;
    ProgressBar progressSearch;
    Context context;
    FrameLayout frameLayout;
    TextView searchMessage;

    public NameSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_name_search, container, false);
        context = view.getContext();
        frameLayout = (FrameLayout) view.findViewById(R.id.frame_layout_name);
        progressSearch = (ProgressBar) view.findViewById(R.id.pb_search);
        etSearch = (EditText) view.findViewById(R.id.et_search_name);
        searchMessage = (TextView) view.findViewById(R.id.tv_search_result_msg);
        lvSuggestion = (ListView) view.findViewById(R.id.lv_suggestion);
        adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_1,
                listSuggestion);
        lvSuggestion.setAdapter(adapter);

        lvSuggestion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = (String) parent.getItemAtPosition(position);
                goToDetails(name);
            }
        });


        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                showSuggestion(s.toString());
            }
        });

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    final Editable selection = etSearch.getText();

                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);

                    showSuggestion(selection.toString());
                    return true;
                }
                return false;
            }
        });

        return view;
    }

    private void showSuggestion(String query) {

        listSuggestion.clear();
        MySingleton.getInstance(context).getRequestQueue().cancelAll("tag");
        if (query.length() <= 2) {
            progressSearch.setVisibility(View.GONE);
            searchMessage.setText(R.string.error_enter_more_characters);
            searchMessage.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
            return;
        }
        searchMessage.setVisibility(View.GONE);
        progressSearch.setVisibility(View.VISIBLE);

        Uri.Builder builder = new Uri.Builder();

        builder.scheme("https")//https://students.iitm.ac.in/studentsapp/map/get_location.php?
                .authority("students.iitm.ac.in")
                .appendPath("studentsapp")
                .appendPath("studentlist")
                .appendPath("getresultbyname.php")
                .appendQueryParameter("name", query);

        String url = builder.build().toString();
//        Log.d("URL", url);
        // Request a string response from the provided URL.
        StringRequest jsonObjReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {

                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject;
                    int i;
                    String studName;
                    listSuggestion.clear();

                    for (i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        studName = jsonObject.getString("fullname");
                        if (!listSuggestion.contains(studName))
                            listSuggestion.add(studName);//+", "+studRoll
                    }
                    adapter.notifyDataSetChanged();
                    searchMessage.setVisibility(View.GONE);
                    progressSearch.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
//                    Log.d("URL", response);
                    if (response.equals("No results") || response.equals("")) {
                        searchMessage.setText(R.string.error_no_result);
                        searchMessage.setVisibility(View.VISIBLE);
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(frameLayout, getString(R.string.error_parsing), Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                    listSuggestion.clear();
                    adapter.notifyDataSetChanged();
                    progressSearch.setVisibility(View.GONE);

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                searchMessage.setText(R.string.error_connection);
                searchMessage.setVisibility(View.VISIBLE);
                listSuggestion.clear();
                adapter.notifyDataSetChanged();
                progressSearch.setVisibility(View.GONE);
            }
        });

        jsonObjReq.setTag("tag");
        MySingleton.getInstance(context).addToRequestQueue(jsonObjReq);
    }

    private void goToDetails(String query) {

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);

        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Getting data...");
        pDialog.show();
        pDialog.setCancelable(false);


        Uri.Builder builder = new Uri.Builder();

        builder.scheme("https")//https://students.iitm.ac.in/studentsapp/map/get_location.php?
                .authority("students.iitm.ac.in")
                .appendPath("studentsapp")
                .appendPath("studentlist")
                .appendPath("getresultbyname.php")
                .appendQueryParameter("name", query);

        String url = builder.build().toString();

        // Request a string response from the provided URL.
        StringRequest jsonObjReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {

                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject;
                    String studName = "Name appears here",
                            studRoll = "Roll number appears here",
                            hostel = "Hostel",
                            roomNo = "room number",
                            photo = "https://photos.iitm.ac.in//byroll.php?roll=wrongSyntax";

                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        studName = jsonObject.getString("fullname");
                        studRoll = jsonObject.getString("username");
                        hostel = jsonObject.getString("hostel");
                        roomNo = jsonObject.getString("roomno");
                        photo = jsonObject.getString("url");
                    }

                    Intent intent = new Intent(context, StudentDetailsActivity.class);
                    intent.putExtra("studName", studName);
                    intent.putExtra("studRoll", studRoll);
                    intent.putExtra("hostel", hostel);
                    intent.putExtra("roomNo", roomNo);
                    intent.putExtra("photo", photo);
                    pDialog.dismiss();
                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                    pDialog.dismiss();
                    Snackbar snackbar = Snackbar
                            .make(frameLayout, getString(R.string.error_parsing), Snackbar.LENGTH_LONG);
                    snackbar.show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                pDialog.dismiss();
                Snackbar snackbar = Snackbar
                        .make(frameLayout, getString(R.string.error_connection), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
        MySingleton.getInstance(context).addToRequestQueue(jsonObjReq);
    }
}
