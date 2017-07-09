package in.ac.iitm.students.adapters;

/**
 * Created by sai_praneeth7777 on 18-Jun-16.
 */

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.ac.iitm.students.R;
import in.ac.iitm.students.activities.MessageChatActivity;
import in.ac.iitm.students.objects.ThreadObject;

/**
 * Created by sai_praneeth7777 on 13-Jun-16.
 */
public class ThreadAdapter extends ArrayAdapter {
    List list = new ArrayList<ThreadObject>();

    public ThreadAdapter(Context context, int resource) {
        super(context, resource);
    }

    public void add(ThreadObject object) {
        super.add(object);
        list.add(object);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        row = convertView;
        final contentHolder contentHolder;

        if (row == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.list_row_thread, parent, false);
            contentHolder = new contentHolder();
            contentHolder.txsubject = (TextView) row.findViewById(R.id.threadSubject);
            contentHolder.txbody = (TextView) row.findViewById(R.id.threadBody);
            contentHolder.txdate = (TextView) row.findViewById(R.id.threadDate);
            contentHolder.txthread_id = (TextView) row.findViewById(R.id.thread_id);
            contentHolder.txmessname = (TextView) row.findViewById(R.id.threadName);
            contentHolder.txuser = (TextView) row.findViewById(R.id.threadUser);
            contentHolder.tvComCategory = (TextView) row.findViewById(R.id.tv_complaint_category);
            contentHolder.tvComInformed = (TextView) row.findViewById(R.id.tv_complaint_informed);
            row.setTag(contentHolder);
            row.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), MessageChatActivity.class);
                    i.putExtra("thread_id", contentHolder.txthread_id.getText().toString());
                    i.putExtra("solved", contentHolder.txsolved);
                    i.putExtra("solvedBy", contentHolder.txsolvedBy);
                    v.getContext().startActivity(i);
                    //Toast.makeText(v.getContext(), btn.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            contentHolder = (contentHolder) row.getTag();
        }

        final ThreadObject content = (ThreadObject) this.getItem(position);
        contentHolder.txsubject.setText(content.getSubject());
        contentHolder.txbody.setText(content.getBody());
        contentHolder.txdate.setText(content.getDate() + ", " + content.getTime());
        //contentHolder.txuser.setText(content.getUser());
        contentHolder.txthread_id.setText(content.getId());
        contentHolder.txmessname.setText(content.getMessName());
        contentHolder.txuser.setText(content.getUser());
        contentHolder.tvComCategory.setText(content.getComplaintcategory());
        contentHolder.tvComInformed.setText(content.getInformed());
        contentHolder.txsolved = content.getSolved();
        contentHolder.txsolvedBy = content.getSolved_by();
        return row;
    }

    static class contentHolder {
        TextView txsubject;
        TextView txbody;
        TextView tvComInformed;
        TextView tvComCategory;
        TextView txdate;
        TextView txthread_id;
        TextView txmessname;
        TextView txuser;
        String txsolved;
        String txsolvedBy;
    }
}
