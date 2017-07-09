package in.ac.iitm.students.others;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by admin on 27-11-2016.
 */

public class ClusterMarkerLocation implements ClusterItem {
    private LatLng position;

    public ClusterMarkerLocation(LatLng latLng) {
        position = latLng;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }
}
