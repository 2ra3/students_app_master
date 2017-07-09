package in.ac.iitm.students.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.ac.iitm.students.R;
import in.ac.iitm.students.adapters.LecturesAdapter;


/**
 * Created by Sathwik on 20-12-2016.
 */

public class Year2016_17Fragment extends Fragment {

    String response;

    public Year2016_17Fragment() {
        // Required empty public constructor
    }

    public void setResponse(String response) {
        Log.d("JsonResponse", response);
        this.response = response;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_year2016_17, container, false);

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        Log.d("JsonResponse", "\n\nI am in year 2016\n\n");

        recyclerView.setAdapter(new LecturesAdapter(response, context, false));
        return view;
    }

}
