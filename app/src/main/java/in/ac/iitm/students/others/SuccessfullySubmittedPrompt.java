package in.ac.iitm.students.others;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import in.ac.iitm.students.R;

/**
 * Created by Sathwik on 14-01-2017.
 */

public class SuccessfullySubmittedPrompt {
    public void setMessage(final Context context, String feedback) {
        new AlertDialog.Builder(context)
                .setTitle("Successfully submitted")
                .setMessage("Your " + feedback + " has been successfully submitted.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(R.drawable.ic_done_black_24dp)
                .show();
    }
}
