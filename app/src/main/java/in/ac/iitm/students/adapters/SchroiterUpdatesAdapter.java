package in.ac.iitm.students.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.ac.iitm.students.R;

/**
 * Created by Sathwik on 20-12-2016.
 */
public class SchroiterUpdatesAdapter extends RecyclerView.Adapter<SchroiterUpdatesAdapter.ViewHolder> {

    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<String> desc = new ArrayList<>();
    private ArrayList<String> times = new ArrayList<>();
    private Context context;
    private String response;

    public SchroiterUpdatesAdapter(String response, Context context) {
        this.response = response;
        this.context = context;
        setUpData();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schroeter_updates, parent, false);
        return new ViewHolder(view);
    }

    private void setUpData() {

        try {
            JSONArray jsonArray = new JSONArray(response);
            JSONObject jsonObject;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                names.add(jsonObject.getString("title"));
                desc.add(jsonObject.getString("description"));
                times.add(jsonObject.getString("time"));
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    @Override
    public void onBindViewHolder(final SchroiterUpdatesAdapter.ViewHolder holder, final int position) {

        String name = names.get(position);
        String des = desc.get(position);
        String time = times.get(position);

        if (name.equals("null")) holder.tvTitle.setVisibility(View.GONE);
        else
            holder.tvTitle.setText(name);
        if (des.equals("null")) holder.tvDesc.setVisibility(View.GONE);
        else
            holder.tvDesc.setText(des);
        if (time.equals("null")) holder.tvTime.setVisibility(View.GONE);
        else
            holder.tvTime.setText(time);

    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvDesc, tvTime;

        ViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.text_title_su);
            tvDesc = (TextView) itemView.findViewById(R.id.text_desc_su);
            tvTime = (TextView) itemView.findViewById(R.id.text_time_su);
        }


    }

}
