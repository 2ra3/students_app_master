package in.ac.iitm.students.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.ac.iitm.students.R;
import in.ac.iitm.students.activities.EMLDetailsActivity;

/**
 * Created by Sathwik on 20-12-2016.
 */
public class LecturesAdapter extends RecyclerView.Adapter<LecturesAdapter.ViewHolder> {
    Boolean isUpcoming;
    //
    private Context context;
    private String lecturersString;
    private ArrayList<String> lectureID = new ArrayList<>();
    private ArrayList<String> speakerName = new ArrayList<>();
    private ArrayList<String> speakerBio = new ArrayList<>();
    private ArrayList<String> photoURL = new ArrayList<>();
    private ArrayList<String> topic = new ArrayList<>();

    public LecturesAdapter(String lecturers, Context context, Boolean isUpcoming) {
        this.lecturersString = lecturers;
        this.context = context;
        this.isUpcoming = isUpcoming;
        setUpData();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lecture, parent, false);
        context = parent.getContext();


        return new ViewHolder(view);
    }

    private void setUpData() {

        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Parsing data...");
        pDialog.show();
        pDialog.setCancelable(false);
        try {
            JSONArray jsonArray = new JSONArray(lecturersString);
            JSONObject jsonObject;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                lectureID.add(jsonObject.getString("id"));
                speakerName.add(jsonObject.getString("speaker_name"));
                speakerBio.add(jsonObject.getString("speaker_bio"));
                photoURL.add(jsonObject.getString("photo_url"));
                topic.add(jsonObject.getString("topic"));
            }
            pDialog.dismiss();
        } catch (JSONException e) {
            e.printStackTrace();
            pDialog.dismiss();

        }
    }

    @Override
    public void onBindViewHolder(final LecturesAdapter.ViewHolder holder, final int position) {

        String name = speakerName.get(position);
        String myTopic = topic.get(position);
        String bio = speakerBio.get(position);
        final String photo = photoURL.get(position);
        final String id = lectureID.get(position);

        if (name.equals("null")) {
            holder.tvLecturer.setVisibility(View.GONE);
            name = "Title unavailable";
        } else
            holder.tvLecturer.setText(name);

        if (bio.equals("null")) bio = "Speaker bio unavailable";

        if (myTopic.equals("null")) {
            Log.d("EMLFix", "I am here! " + myTopic);
            holder.tvTime.setVisibility(View.GONE);
            myTopic = "Topic unavailable";
        } else
            holder.tvTime.setText(myTopic);

        Picasso.with(context)
                .load(photo)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.changed_eml)
                .error(R.drawable.changed_eml)
                .into(holder.ivLecturePic);

        final int bgColors[] = {R.color.amber, R.color.cyan, R.color.brown, R.color.lightGreen};
        holder.ivLecturePic.setBackgroundResource(bgColors[(position % 4)]);

        final String finalName = name;
        final String finalBio = bio;
        final String finalMyTopic = myTopic;
        holder.rlLecture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EMLDetailsActivity.class);
                intent.putExtra("ID", id);
                intent.putExtra("speaker_name", finalName);
                intent.putExtra("speaker_bio", finalBio);
                intent.putExtra("photo_url", photo);
                intent.putExtra("topic", finalMyTopic);
                if (isUpcoming) intent.putExtra("isUpcoming", 1);
                else intent.putExtra("isUpcoming", 0);
                intent.putExtra("topic", finalMyTopic);
                intent.putExtra("color", bgColors[(position % 4)]);

                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return speakerName.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvLecturer, tvTime;
        RelativeLayout rlLecture;
        ImageView ivLecturePic;

        ViewHolder(View itemView) {
            super(itemView);

            tvLecturer = (TextView) itemView.findViewById(R.id.tv_lecturer);
            tvTime = (TextView) itemView.findViewById(R.id.tv_topic_eml);
            rlLecture = (RelativeLayout) itemView.findViewById(R.id.rl_item_lecture);
            ivLecturePic = (ImageView) itemView.findViewById(R.id.iv_item_lecture_pic);
        }


    }

}
