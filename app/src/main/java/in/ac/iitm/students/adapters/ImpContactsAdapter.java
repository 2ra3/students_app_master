package in.ac.iitm.students.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import in.ac.iitm.students.R;

/**
 * Created by Sathwik on 16-01-2017.
 */

public class ImpContactsAdapter extends RecyclerView.Adapter<ImpContactsAdapter.ViewHolder> {
    public String[] contactNames = {"Medical emergency", "Security", "Tele counselling", "LAN complaints", "Electrical complaints", "CCW office"};
    public String[] contactDetails = {"04422578888", "04422579999", "04422575555", "04422575987", "04422578187", "04422578504"};
    Context context;

    public ImpContactsAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contacts, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.title.setText(contactNames[position]);
        holder.subtitle.setText(contactDetails[position]);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialPhoneNumber(contactDetails[position]);
            }
        });

    }

    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return contactNames.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView title, subtitle;
        LinearLayout linearLayout;


        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            title = (TextView) itemView.findViewById(R.id.text_decibels);
            subtitle = (TextView) itemView.findViewById(R.id.text_event);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.ll_call);
        }
    }

}
