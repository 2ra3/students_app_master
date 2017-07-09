package in.ac.iitm.students.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import in.ac.iitm.students.R;
import in.ac.iitm.students.adapters.SchroiterUpdatesAdapter;


public class SchroiterUpdatesFragment extends Fragment {

    String response;
    DrawerLayout drawerLayout;

    public SchroiterUpdatesFragment() {
        // Required empty public constructor
    }

    public void setResponse(String response, DrawerLayout drawerLayout) {
        this.response = response;
        this.drawerLayout = drawerLayout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schroeter, container, false);

        if (response.equals("No results")) {
            TextView tvNoUpdate = (TextView) view.findViewById(R.id.tv_error_updates);
            tvNoUpdate.setText("No updates for now");
            tvNoUpdate.setVisibility(View.VISIBLE);
        } else {
            Context context = getActivity();
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv_schroeter_updates);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new SchroiterUpdatesAdapter(response, context));
        }
        return view;

    }
}
