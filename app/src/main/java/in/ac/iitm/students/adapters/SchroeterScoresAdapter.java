package in.ac.iitm.students.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.ac.iitm.students.R;

/**
 * Created by Sathwik on 20-12-2016.
 */
public class SchroeterScoresAdapter extends RecyclerView.Adapter<SchroeterScoresAdapter.ViewHolder> {

    DrawerLayout drawerLayout;
    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<String> desc = new ArrayList<>();
    private Context context;
    private String response;

    public SchroeterScoresAdapter(String response, Context context, DrawerLayout drawerLayout) {
        this.response = response;
        this.context = context;
        this.drawerLayout = drawerLayout;
        setUpData();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schroeter_scores, parent, false);
//        context = parent.getContext();


        return new ViewHolder(view);
    }

    private void setUpData() {


        try {
            JSONArray jsonArray = new JSONArray(response);
            JSONObject jsonObject;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                names.add(jsonObject.getString("name"));
                desc.add(jsonObject.getString("link"));
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    @Override
    public void onBindViewHolder(final SchroeterScoresAdapter.ViewHolder holder, final int position) {

        String name = names.get(position);
        final String des = desc.get(position);

        holder.tvTitle.setText(name);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWebPage(des);
            }
        });


    }

    private void openWebPage(String url) {
        Toast.makeText(context, "Getting data...", Toast.LENGTH_SHORT).show();
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            Snackbar snackbar = Snackbar.make(drawerLayout, "Error getting data, try again later...", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        LinearLayout linearLayout;

        ViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.text_title_ss);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.ll_sc_link);
        }


    }

}
