package com.jlcsoftware.sampleapp;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jlcsoftware.api.ImgurGallery;
import com.jlcsoftware.api.ImgurImage;
import com.jlcsoftware.api.ImgurSyncronzier;
import com.jlcsoftware.services.SynchronizeIntentService;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ImgurFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ImgurFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImgurFragment extends Fragment implements ImgurRecyclerViewAdapter.OnImgurRecyclerViewAdapterItemClickListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_VIEW_TYPE = "ARG_VIEW_TYPE";
    private static final String ARG_GALLERY = "ARG_GALLERY";
    public static String GRID_VIEW = "GRID_VIEW";
    public static String LIST_VIEW = "LIST_VIEW";

    ImgurRecyclerViewAdapter adapter; // Adapter we are using
    AsyncImgurTask imgurTask; // our Imgur Image downloading task
    RecyclerView recyclerView;
    ProgressBar progressBar;
    BroadcastReceiver broadcastReceiver;
    TextView textView;

    private OnFragmentInteractionListener mListener;
    private String viewTypeArg = LIST_VIEW;  // controls the layout
    private String galleryArg = "top"; // the Imgur Gallery we are displaying

    public ImgurFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param viewType Type of View - List or Grid
     * @return A new instance of fragment ImgurFragment.
     */
    public static ImgurFragment newInstance(String viewType, String galleryArg) {
        ImgurFragment fragment = new ImgurFragment();
        Bundle args = new Bundle();
        args.putString(ARG_VIEW_TYPE, viewType);
        args.putString(ARG_GALLERY, galleryArg);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Tell the listener someone selected a Image
     *
     * @param image
     */

    @Override
    public void onSelectedImage(ImgurImage image) {
        mListener.onFragmentInteraction(image); // Tell the activity
    }

    @Override
    public void onClick(View v) {
        // do nothing
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Get the gallery we are displaying
     *
     * @return
     */

    public String getGallery() {
        return galleryArg;
    }

    /**
     * Get the type of layout we are displaying
     *
     * @return
     */
    public String getViewType() {
        return viewTypeArg;
    }

    /**
     * Update our display and gallery
     *
     * @param viewType layout
     * @param gallery  Imgur Gallery Id
     */
    public void setDisplay(String viewType, String gallery) {
        if (null != recyclerView && null != adapter) {
            if (!viewTypeArg.equals(viewType)) {
                if (viewType.equals(GRID_VIEW)) {
                    recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
                    adapter.setItemViewType(R.layout.grid_card);
                } else {
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    adapter.setItemViewType(R.layout.list_row);
                }
            }

            if (!gallery.equals(galleryArg)) {
                this.galleryArg = gallery;
                refresh(true);
            }

        }
        this.viewTypeArg = viewType;
        this.galleryArg = gallery;
        setTitle();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            viewTypeArg = getArguments().getString(ARG_VIEW_TYPE);
            galleryArg = getArguments().getString(ARG_GALLERY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_imgur, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        textView = (TextView) view.findViewById(R.id.textView);

        setTitle();

        if (viewTypeArg.equals(GRID_VIEW))
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        else
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ImgurRecyclerViewAdapter(getContext(), viewTypeArg.equals(GRID_VIEW) ? R.layout.grid_card : R.layout.list_row);
        adapter.setOnItemClickListener(new ImgurRecyclerViewAdapter.OnImgurRecyclerViewAdapterItemClickListener() {

            @Override
            public void onClick(View v) {

            }

            @Override
            public void onSelectedImage(ImgurImage image) {
                if (null != mListener) mListener.onFragmentInteraction(image);
            }
        });
        recyclerView.setAdapter(adapter);

        if (null != savedInstanceState) { // somebody flipped the device, etc...
            try {
                String str = savedInstanceState.getString("gallery");
                if (null != str && !str.isEmpty()) { // fast twisting will cause gallery not to be set
                    final ImgurGallery imgurGallery = new ImgurGallery(new JSONObject(str));
                    adapter.setImgurGallery(imgurGallery);
                }else {
                    refresh(true);
                }
            } catch (JSONException e) {
                refresh(true);
            }
        } else {
            refresh(true);
        }


        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO: The cache has been refreshed... do something?
            }
        };
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver,
                new IntentFilter(SynchronizeIntentService.BROADCAST_GALLERIES_SYNCHRONIZED));

        return view;
    }

    private void setTitle() {
        if (null != textView) {
            // TODO: support more choices
            if (galleryArg.equals("hot")) {
                textView.setText(R.string.imgur_hot);
            }
            if (galleryArg.equals("top")) {
                textView.setText(R.string.imgur_top);
            }
        }
    }

    private void refresh(boolean doProgress) {
        if (null != imgurTask) {
            imgurTask.cancel(true);
            imgurTask = null;
        }
        if (null == imgurTask) {
            imgurTask = new AsyncImgurTask();
            imgurTask.setSilent(!doProgress);
            imgurTask.execute(galleryArg);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != broadcastReceiver)
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != adapter) {
            // not really necessary because we are pulling the gallery from the cache, but what the heck, maybe the cache was
            // cleaned in the middle by android or us....
            ImgurGallery gallery = adapter.getImgurGallery();
            if (null != gallery) // Fast twisting of device
                outState.putString("gallery", gallery.toString());
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(ImgurImage selected);
    }

    /**
     * Handles background downloading for Imgar Images and loading and parsing cached responses
     */

    public class AsyncImgurTask extends AsyncTask<String, Void, ImgurGallery> {

        ProgressDialog progressDialog;
        boolean silent = false;

        public boolean isSilent() {
            return silent;
        }

        public void setSilent(boolean silent) {
            this.silent = silent;
        }

        @Override
        protected void onPreExecute() {
            if (!isSilent()) {
                recyclerView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected ImgurGallery doInBackground(String... params) {
            ImgurGallery images = ImgurSyncronzier.getGalleryImages(getContext(), params[0]);
            return images;
        }

        @Override
        protected void onPostExecute(ImgurGallery result) {

            if (!isSilent()) {
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }

            if (null != result) {
                adapter.setImgurGallery(result);
            } else {
                Toast.makeText(getContext(), "Failed to get the images from Imgur", Toast.LENGTH_SHORT).show();
            }
            imgurTask = null;
        }
    }

}
