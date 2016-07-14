package com.jlcsoftware.sampleapp;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.jlcsoftware.api.ImgurImage;
import com.jlcsoftware.services.SynchronizeIntentService;
import com.koushikdutta.ion.Ion;

/**
 * The main activity, go figure...
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ImgurFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // on create happens before onResume... bad for the fragment we are creating here...
        // so load up the last saved event...
        selectedGallery = AppPreferences.getInstance(this).getString("selectedGallery", "top");
        viewType = AppPreferences.getInstance(this).getString("viewType", ImgurFragment.GRID_VIEW);

        if(null!=savedInstanceState){ // The Activity was fully killed, and we saved temp values...
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // start up Synchronization (fill the cache)
        SynchronizeIntentService.startActionRefresh(this);
        // set clean up and refresh schedules
        SynchronizeIntentService.setSchedules(this);

        if (BuildConfig.DEBUG) { // Android says that deployed application should not contain logging code.
            Ion.getDefault(this).configure().setLogging("MyLogs", Log.DEBUG);
        }

        navigationItemSelected(0); // display something
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Sorry no settings for you!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_about) {
            new AlertDialog.Builder(this)
                    .setCancelable(true)
                    .setTitle(getResources().getString(R.string.action_about))
                    .setMessage(Html.fromHtml(getString(R.string.app_about)))
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }
                    }).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        navigationItemSelected(id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    String selectedGallery = "hot";
    String viewType = ImgurFragment.GRID_VIEW;

    public boolean navigationItemSelected(int id) {
        // Handle navigation view item clicks

        if (id == R.id.nav_send) {
            // yes its hard coded... :)
            Intent myShareIntent = new Intent(Intent.ACTION_SEND);
            myShareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "A Great Example");
            myShareIntent.putExtra(Intent.EXTRA_TEXT, "Look what I found\nA GREAT RecycleView sample app by: Jeff Moll\nhttps://github.com/rjeffm/ImgurSampleApp");
            myShareIntent.setType("text/plain");
            startActivityForResult(Intent.createChooser(myShareIntent, "Send"), 0xdead);
            return true;
        }


        String newGallery = selectedGallery;
        String newViewType = viewType;
        if (R.id.nav_hot == id) {
            newGallery = "hot";
        }
        if (R.id.nav_top == id) {
            newGallery = "top";
        }

        if (R.id.nav_grid == id) {
            newViewType = ImgurFragment.GRID_VIEW;
        }
        if (R.id.nav_list == id) {
            newViewType = ImgurFragment.LIST_VIEW;
        }

        // Support for More Fragment types than this SAMPLE actually handles/creates
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.current_fragment);
        Fragment frag = currentFragment;

        if (!(frag instanceof ImgurFragment)) {
            frag = ImgurFragment.newInstance(newViewType, newGallery);
        } else {
            ((ImgurFragment) frag).setDisplay(newViewType, newGallery);
        }

        if (null != frag) {
            if (frag != currentFragment) {
                FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
                if (null != currentFragment) {
                    trans.detach(currentFragment);
                    trans.remove(currentFragment);
                }
                trans.add(R.id.current_fragment, frag).commit();
            }
        }

        selectedGallery = newGallery;
        viewType = newViewType;

        return true;
    }


    @Override
    protected void onPause() {
        // Save our state for the next run of the app
        AppPreferences.getInstance(this).putString("selectedGallery", selectedGallery);
        AppPreferences.getInstance(this).putString("viewType", viewType);
        super.onPause();
    }

    @Override
    public void onFragmentInteraction(ImgurImage selected) {
        if (null != selected) {
            if(selected.isAlbum()){
                Toast.makeText(this,"Navigate into Album. NOT SUPPORTED YET",Toast.LENGTH_LONG).show();
            }else {
                Intent intent = new Intent(this, ImgurDetailActivity.class);
                intent.putExtra("ImgurImage", selected.toString());
                startActivity(intent);
            }
        }
    }
}
