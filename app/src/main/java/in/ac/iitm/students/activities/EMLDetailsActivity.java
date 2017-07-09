package in.ac.iitm.students.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import in.ac.iitm.students.R;
import in.ac.iitm.students.others.SuccessfullySubmittedPrompt;


public class EMLDetailsActivity extends AppCompatActivity {
    CoordinatorLayout cl;
    int isUpcoming;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emldetails);

        Intent intent = getIntent();
        final String name = intent.getStringExtra("speaker_name");
        final String bio = intent.getStringExtra("speaker_bio");
        final String photo = intent.getStringExtra("photo_url");
        final String topic = intent.getStringExtra("topic");
        final String id = intent.getStringExtra("ID");
        isUpcoming = intent.getIntExtra("isUpcoming", 0);
        int color = intent.getIntExtra("color", R.color.lightGreen);

        cl = (CoordinatorLayout) findViewById(R.id.main_content);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.transparent));
        collapsingToolbar.setTitle(name);

        ImageView ivLecturePic = (ImageView) findViewById(R.id.iv_details_lecture_pic);
        TextView tvName = (TextView) findViewById(R.id.tv_details_lecturer);
        TextView tvBio = (TextView) findViewById(R.id.tv_details_lecture_info);
        TextView tvTopic = (TextView) findViewById(R.id.tv_details_lecture_topic);

        tvName.setText(name);
        tvBio.setText(bio);
        if (topic.equals("Topic unavailable")) {
            tvTopic.setVisibility(View.GONE);
        } else
            tvTopic.setText(topic);
        Picasso.with(this)
                .load(photo)
                .fit()
                .error(R.drawable.changed_eml)
                .centerCrop()
                .into(ivLecturePic);
        ivLecturePic.setBackgroundResource(color);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EMLDetailsActivity.this, EMLFeedbackActivity.class);
                intent.putExtra("speaker_name", name);
                intent.putExtra("feedback", 1);
                intent.putExtra("topic", topic);
                intent.putExtra("ID", id);
                intent.putExtra("isUpcoming", isUpcoming);
                startActivityForResult(intent, 0);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (0): {
                if (resultCode == Activity.RESULT_OK) {
                    int showSnackbar = data.getIntExtra("showSnackbar", 0);
                    String string = "";
                    if (isUpcoming == 1) {
                        string = "question";
                    } else {
                        string = "feedback";
                    }
                    if (showSnackbar == 1) {

                        SuccessfullySubmittedPrompt prompt = new SuccessfullySubmittedPrompt();
                        prompt.setMessage(EMLDetailsActivity.this, string);
                    }
                }
                break;
            }
        }
    }

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
        super.onBackPressed();
    }
}
