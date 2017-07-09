package in.ac.iitm.students.objects;

import java.util.ArrayList;
import java.util.List;

import in.ac.iitm.students.R;

/**
 * Created by Rahil Shajahan on 6/13/2017.
 */

public class GroupsAndClubsObject {
    private int imageID;

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }

    public static List<GroupsAndClubsObject> getObjectList(){

        List<GroupsAndClubsObject> dataList = new ArrayList<>();
        int[] images = getImages();
        for(int i = 0;i<images.length;i++)
        {
            GroupsAndClubsObject groupsAndClubsObject = new GroupsAndClubsObject();
            groupsAndClubsObject.setImageID(images[i]);
            dataList.add(groupsAndClubsObject);
        }
        return dataList;
    }

    private static int[] getImages(){
        int[] images = {
                R.drawable.changed_eml, R.drawable.logot5e
        };
        return images;
    }
}

