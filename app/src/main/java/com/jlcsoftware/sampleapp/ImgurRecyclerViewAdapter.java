package com.jlcsoftware.sampleapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jlcsoftware.api.ImgurGallery;
import com.jlcsoftware.api.ImgurImage;
import com.jlcsoftware.api.ImgurSyncronzier;
import com.jlcsoftware.helpers.MyLogger;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.future.ImageViewFuture;


/**
 * Created by Jeff on 12-Jul-16.
 */
public class ImgurRecyclerViewAdapter extends RecyclerView.Adapter {
    private final static String TAG = "ImgurRecyclerViewAdapter";

    interface OnImgurRecyclerViewAdapterItemClickListener extends View.OnClickListener {
        public void onSelectedImage(ImgurImage image);
    }


    OnImgurRecyclerViewAdapterItemClickListener listener;

    public void setOnItemClickListener(OnImgurRecyclerViewAdapterItemClickListener listener) {
        this.listener = listener;
    }


    protected int selectedItem = -1;

    public class ImgurRecyclerViewHolder extends RecyclerView.ViewHolder {
        protected TextView textView;
        protected ImageView imageView;
        protected ImageViewFuture imageViewFuture; // Ion handle for canceling etc.


        public ImgurRecyclerViewHolder(View itemView) {
            super(itemView);
            View view = (View) itemView.findViewById(R.id.linearLayout);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedItem = getLayoutPosition();
                    if (null != listener) {
                        listener.onClick(v);
                        listener.onSelectedImage(gallery.get(selectedItem));
                    }
                }
            });
            this.textView = (TextView) itemView.findViewById(R.id.textView);
            this.imageView = (ImageView) itemView.findViewById(R.id.imageView);
            this.imageView.setScaleType(ImageView.ScaleType.CENTER); //TODO: move to xml
        }
    }

    @Override
    public int getItemViewType(int position) {
        return layoutId;
    }

    public void setItemViewType(int viewType){
        layoutId = viewType; // View Type and Layout Id are the same. Change this when changing layout manager
    }

    Context context;
    int layoutId;

    public ImgurGallery getImgurGallery() {
        return gallery;
    }

    public void setImgurGallery(ImgurGallery gallery) {
        if (null == gallery) return;
        this.gallery = gallery;
        // TODO: do more clever things here... diff the difference and call notifyItemRangeInserted(int positionStart, int itemCount) for example
        notifyDataSetChanged();
    }

    ImgurGallery gallery;

    public ImgurRecyclerViewAdapter(Context context, int layoutId) {
        this.context = context;
        this.layoutId = layoutId;
    }

    public ImgurRecyclerViewAdapter(Context context, int layoutId, ImgurGallery gallery) {
        this.context = context;
        this.layoutId = layoutId;
        setImgurGallery(gallery);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        ImgurRecyclerViewHolder viewHolder = new ImgurRecyclerViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        holder.itemView.setSelected(selectedItem == position);

        ImgurImage image = gallery.get(position);
        ImgurRecyclerViewHolder thisHolder = (ImgurRecyclerViewHolder) holder;
        if (image.isAlbum()) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.limeA200));
            new AlbumImageTask(thisHolder).execute(image.getCover());
        } else {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.cardview_light_background));
            try {
                if (null != thisHolder.imageViewFuture) thisHolder.imageViewFuture.cancel(true);

                thisHolder.imageViewFuture = Ion.with(context).load(image.getLink())
                        .withBitmap().fitCenter()
                        .error(R.drawable.link_break)
                        .placeholder(R.drawable.progress).intoImageView(thisHolder.imageView);

            } catch (Exception e) {
                MyLogger.d(TAG, "onBindViewHolder(RecyclerView.ViewHolder holder, int position)", e);
            }

        }
        ((ImgurRecyclerViewHolder) holder).textView.setText(Html.fromHtml(image.getTitle()));
    }

    @Override
    public int getItemCount() {
        if (null == gallery) return 0;
        return gallery.length();
    }


    ImgurImage getSelected() {
        if (selectedItem != -1) {
            return gallery.get(selectedItem);
        }
        return null;
    }


    public class AlbumImageTask extends AsyncTask<String, Void, ImgurImage> {
        final ImgurRecyclerViewHolder holder;

        AlbumImageTask(ImgurRecyclerViewHolder holder) {
            this.holder = holder;
            this.holder.imageView.setImageResource(R.drawable.gallery);

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected ImgurImage doInBackground(String... params) {
            ImgurImage image = ImgurSyncronzier.getImage(context, params[0]);
            return image;
        }

        @Override
        protected void onPostExecute(ImgurImage result) {
            if (null == result) {
                holder.imageView.setImageResource(R.drawable.link_break);
                return;
            }
            String link;
            if (null != holder.imageViewFuture) holder.imageViewFuture.cancel();

            holder.imageViewFuture = Ion.with(context).load(result.getLink()).withBitmap().fitCenter()
                    .error(R.drawable.link_break).animateLoad(R.animator.rotate_around_center_point)
                    .placeholder(R.drawable.progress).intoImageView(holder.imageView);
        }
    }


}
