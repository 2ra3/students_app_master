package in.ac.iitm.students.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.ac.iitm.students.R;
import in.ac.iitm.students.activities.StudentDetailsActivity;
import in.ac.iitm.students.others.MySingleton;


public class RollSearchFragment extends Fragment {
    Context context;
    EditText etRollNoSearch;
    FrameLayout frameLayout;

    public RollSearchFragment() {
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
        View view = inflater.inflate(R.layout.fragment_roll_search, container, false);

        context = view.getContext();
        frameLayout = (FrameLayout) view.findViewById(R.id.frame_layout_roll);
        etRollNoSearch = (EditText) view.findViewById(R.id.et_search_no);
        etRollNoSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                goToDetails(etRollNoSearch.getText().toString());
                return false;
            }
        });

        Button buttonShowDetails = (Button) view.findViewById(R.id.button_show_details);
        buttonShowDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToDetails(etRollNoSearch.getText().toString());
            }
        });

        return view;
    }

    private void goToDetails(String query) {

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etRollNoSearch.getWindowToken(), 0);

        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Getting data...");
        pDialog.show();
        pDialog.setCancelable(false);

        Uri.Builder builder = new Uri.Builder();

        builder.scheme("https")//https://students.iitm.ac.in/studentsapp/map/get_location.php?
                .authority("students.iitm.ac.in")
                .appendPath("studentsapp")
                .appendPath("studentlist")
                .appendPath("getresultbyroll.php")
                .appendQueryParameter("rollno", query);

        String url = builder.build().toString();
        Log.d("searchUrl", url);

        // Request a string response from the provided URL.
        StringRequest jsonObjReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {

                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject;
                    Log.d("JsonResponse", response);
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
                            .make(frameLayout, "No results found!", Snackbar.LENGTH_LONG);
                    snackbar.show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                pDialog.dismiss();
                Snackbar snackbar = Snackbar
                        .make(frameLayout, "Couldn't connect to the server.", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
        MySingleton.getInstance(context).addToRequestQueue(jsonObjReq);
    }
}
