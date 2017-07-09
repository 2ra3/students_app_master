package in.ac.iitm.students.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;

import in.ac.iitm.students.R;
import in.ac.iitm.students.others.MySingleton;
import in.ac.iitm.students.others.TouchImageView;

/**
 * Created by admin on 29-10-2016.
 */
public class CalendarFragment extends Fragment {

    private static final String CAL_URL = "blah";

    public static CalendarFragment newInstance(String url) {
        Bundle args = new Bundle();
        args.putString(CAL_URL, url);

        CalendarFragment fragment = new CalendarFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_calender, container, false);
        String url = getArguments().getString(CAL_URL);
//        Toast.makeText(getActivity(), url, Toast.LENGTH_SHORT).show();
        TouchImageView mTouchImageView = (TouchImageView) v.findViewById(R.id.calendar_image);
        // Get the ImageLoader through your singleton class.
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Loading image");
        progressDialog.show();
        progressDialog.setCancelable(false);
        ImageLoader mImageLoader = MySingleton.getInstance(getActivity()).getImageLoader();
        mImageLoader.get(url, ImageLoader.getImageListener(mTouchImageView, 0, R.drawable.image_load_error));
        progressDialog.dismiss();
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}


