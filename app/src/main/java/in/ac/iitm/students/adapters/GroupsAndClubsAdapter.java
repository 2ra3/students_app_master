package in.ac.iitm.students.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import in.ac.iitm.students.R;
import in.ac.iitm.students.activities.main.EMLActivity;
import in.ac.iitm.students.activities.main.GroupsAndClubsActivity;
import in.ac.iitm.students.activities.main.T5EActivity;
import in.ac.iitm.students.objects.GroupsAndClubsObject;

/**
 * Created by Rahil Shajahan on 6/13/2017.
 */

public class GroupsAndClubsAdapter extends RecyclerView.Adapter<GroupsAndClubsAdapter.MyViewHolder>{

    private List<GroupsAndClubsObject> objectList;
    private LayoutInflater  inflater;

    public GroupsAndClubsAdapter(Context context,List<GroupsAndClubsObject> objectList){
        inflater = LayoutInflater.from(context);
        this.objectList = objectList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_groups_and_clubs,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        GroupsAndClubsObject current = objectList.get(position);
        holder.setData(current, position);
    }

    @Override
    public int getItemCount() {
        return objectList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private ImageView imgThumb;
        private int position;
        private GroupsAndClubsObject currentObject;




        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   intentItem(position);

                }
            });
            imgThumb = (ImageView) itemView.findViewById(R.id.image_id);
        }

        public void setData(GroupsAndClubsObject currentObject, int position){
            this.imgThumb.setImageResource(currentObject.getImageID());
            this.position = position;
            this.currentObject = currentObject;
        }
        public void intentItem(int position){
            if(position==1){
                itemView.getContext().startActivity(new Intent(itemView.getContext(),T5EActivity.class));
            }
            else if(position==0){
                itemView.getContext().startActivity(new Intent(itemView.getContext(),EMLActivity.class));
            }
        }


    }
}
