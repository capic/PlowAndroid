package plow_android.capic.com.plowandroid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import plow_android.capic.com.plowandroid.R;
import plow_android.capic.com.plowandroid.adapters.DownloadsAdapter;
import plow_android.capic.com.plowandroid.beans.Download;
import plow_android.capic.com.plowandroid.services.RestService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DownloadsAdapter adapter;
    private List<Download> listDownloads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ListView listview = (ListView) findViewById(R.id.listview);
        listDownloads = new LinkedList<>();
        adapter = new DownloadsAdapter(this, R.layout.item_download, listDownloads);
        listview.setAdapter(adapter);

        loadDatas(null);
    }

    private void loadDatas(Integer status) {
        adapter.clear();
        RequestParams params = null;
        if (status != null) {
            params = new RequestParams();
            params.add("status", String.valueOf(status));
        }
        RestService.get("downloads", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("loadDatas", "Number of downloads got: " + response.length());
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject o = response.getJSONObject(i);

                        adapter.add(new Download(o.getLong("id"), o.getString("name"), o.getString("link"), o.getLong("size_file"), o.getLong("size_part"), o.getLong("size_file_downloaded"), o.getLong("size_part_downloaded"), (byte) o.getInt("status"), (byte) o.getInt("progress_part"), (byte) o.getInt("progress_file"), o.getLong("average_speed"), o.getLong("current_speed"), o.getLong("time_spent"), o.getLong("time_left"), o.getInt("pid_plowdown"), o.getInt("pid_python"), o.getString("file_path"), (byte) o.getInt("priority")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.download_all) {
            loadDatas(null);
        } else if (id == R.id.download_in_progress) {
            loadDatas(Download.STATUS_IN_PROGRESS);
        } else if (id == R.id.download_waiting) {
            loadDatas(Download.STATUS_WAITING);
        } else if (id == R.id.download_finished) {
            loadDatas(Download.STATUS_FINISHED);
        } else if (id == R.id.nav_preferences) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
