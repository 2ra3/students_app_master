package in.ac.iitm.students.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.ac.iitm.students.R;
import in.ac.iitm.students.adapters.LecturesAdapter;


/**
 * Created by Sathwik on 20-12-2016.
 */

public class UpcomingLectures extends Fragment {

    String response;

    public UpcomingLectures() {
        // Required empty public constructor
    }

    public void setResponse(String response) {
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
        View view = inflater.inflate(R.layout.fragment_upcoming_lectures, container, false);

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new LecturesAdapter(response, context, true));
        return view;
    }

}
