package in.ac.iitm.students.others;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import in.ac.iitm.students.R;
import in.ac.iitm.students.activities.LoginActivity;

/**
 * Created by Sathwik on 14-01-2017.
 */

public class LogOutAlertClass {
    public void isSure(final Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Log out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Utils.clearpref(context);
                        Intent intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);
                        // continue with delete
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(R.drawable.ic_exit_to_app_black_24dp)
                .show();
    }
}
