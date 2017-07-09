package in.ac.iitm.students.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import in.ac.iitm.students.R;

/**
 * Created by Omkar on 27-01-2017.
 */

public class HomeDialogFragment extends DialogFragment {
    private String dialog_title = "Students App";
    private String dialog_message = "Created By Institute Mobops";
    private Drawable dialog_icon;

    public void setDialogMessage(String dialog_message) {
        this.dialog_message = dialog_message;
    }

    public void setDialogTitle(String dialog_title) {
        this.dialog_title = dialog_title;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        dialog_icon = ContextCompat.getDrawable(getActivity(), R.drawable.app_logo);
        builder.setIcon(dialog_icon);
        builder.setTitle(dialog_title);
        builder.setMessage(dialog_message)
                .setNeutralButton(R.string.dismiss_home_dialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}

