package in.ac.iitm.students.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import in.ac.iitm.students.R;

/**
 * Created by admin on 27-10-2016.
 */
public class StudentDetailsActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView name = (TextView) findViewById(R.id.student_name);
        TextView rollno = (TextView) findViewById(R.id.student_roll_no);
        TextView address = (TextView) findViewById(R.id.student_address);
        TextView mail = (TextView) findViewById(R.id.student_email_id);
        ImageView photo = (ImageView) findViewById(R.id.student_photo);

        Intent intent = getIntent();

        String nameString = intent.getStringExtra("studName");
        if (nameString.equals("null")) name.setText("Name unavailable");
        else name.setText(nameString);

        String roll = intent.getStringExtra("studRoll");
        if (roll.equals("null")) rollno.setText("Roll number unavailable");
        else rollno.setText(roll);

        String hostel = intent.getStringExtra("hostel");
        String room = intent.getStringExtra("roomNo");

        if (hostel.equals("null")) address.setText("Address unavailable");
        else {
            if (room.equals("null")) address.setText(hostel);
            else address.setText(hostel + ", " + room);
        }

        if (roll.equals("null")) mail.setText("Email ID unavailable");
        else {
            roll = roll.toLowerCase();
            String sMail = roll + "@smail.iitm.ac.in";
            mail.setText(sMail);
        }

        Picasso.with(this)
                .load(intent.getStringExtra("photo"))
                .placeholder(R.drawable.ic_menu_camera)
                .error(R.drawable.ic_menu_camera)
                .fit()
                .centerCrop()
                .into(photo);

    }

    //Setting up back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
