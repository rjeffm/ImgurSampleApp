package com.jlcsoftware.sampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jlcsoftware.api.ImgurImage;
import com.jlcsoftware.helpers.MyLogger;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.future.ImageViewFuture;

import org.json.JSONException;

/**
 * A simple Activity to show the details of the Imgur Image (no Fragments)
 */

public class ImgurDetailActivity extends AppCompatActivity {
    private static final String TAG = "ImgurDetailActivity";

    private ImgurImage imgurImage;
    private ImageView imageView;
    private TextView textView;
    private TextView textView2;
    private ImageViewFuture imageViewFuture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imgur_detail);

        Intent intent = getIntent();
        String imugurImageStr = intent.getExtras().getString("ImgurImage");
        try {
            imgurImage = new ImgurImage(imugurImageStr);
        } catch (JSONException e) {
            MyLogger.e(TAG, "protected void onCreate(Bundle savedInstanceState)", e);
        }

        imageView = (ImageView) findViewById(R.id.imageView);
        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);

        if (null != imgurImage) {
            imageViewFuture = Ion.with(imageView).load(imgurImage.getLink());
            //.withBitmap()
            //        .error(R.drawable.link_break).animateLoad(R.animator.rotate_around_center_point)
              //      .placeholder(R.drawable.progress);//.intoImageView(imageView);
            textView.setText(Html.fromHtml(imgurImage.getTitle()));
            final String description = imgurImage.getDescription();
            if (null != description && !description.equals("null"))
                textView2.setText(description);
        } else {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.link_break));
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, imgurImage.getTitle() + " " + imgurImage.getLink());
                    intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.imgur_check_out));
                    startActivity(Intent.createChooser(intent, getString(R.string.str_share)));
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


}
