package in.ac.iitm.students.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import in.ac.iitm.students.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        int SPLASH_TIME_OUT = 800;
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                String cls = checkIntent(getIntent());
                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                i.putExtra("class", cls);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkIntent(intent);
    }

    public String checkIntent(Intent intent) {
        String cls = "HomeActivity";
        if (intent.hasExtra("activity")) {
            cls = intent.getExtras().get("activity").toString();
        }
        return cls;
    }

}