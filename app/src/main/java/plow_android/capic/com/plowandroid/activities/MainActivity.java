package plow_android.capic.com.plowandroid.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import plow_android.capic.com.plowandroid.R;
import plow_android.capic.com.plowandroid.adapters.DownloadsAdapter;
import plow_android.capic.com.plowandroid.beans.Download;
import plow_android.capic.com.plowandroid.listeners.EndlessScrollListener;
import plow_android.capic.com.plowandroid.services.RestService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int NUMBER_OF_ITEMS = 15;

    private DownloadsAdapter adapter;
    private List<Download> listDownloads;
    private Integer mCurrentStatus = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final HashMap<Long, Bitmap> hashDownloadHostPictures = new HashMap<>();
        RestService.get(this, "downloadHostPictures/", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("onCreate", "Number of pictures: " + response.length());
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject o = response.getJSONObject(i);
                        Log.d("onCreate", "Object: " + o.toString());

                        Long id = o.getLong("id");

                        byte[] decodedString = Base64.decode(o.getString("picture"), Base64.DEFAULT);
                        Bitmap image = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        hashDownloadHostPictures.put(id, image);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        ListView listview = (ListView) findViewById(R.id.listview);
        listDownloads = new LinkedList<>();
        adapter = new DownloadsAdapter(this, R.layout.item_download, listDownloads, hashDownloadHostPictures);
        listview.setAdapter(adapter);

        loadDatas(0, null);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),
                        "Click ListItem Number " + position, Toast.LENGTH_LONG)
                        .show();
            }
        });

        listview.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                Log.d("onLoadMore", "+++++***********+++++++");
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                customLoadMoreDataFromApi(page);
                // or customLoadMoreDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });
    }

    // Append more data into the adapter
    public void customLoadMoreDataFromApi(int offset) {
        // This method probably sends out a network request and appends new data items to your adapter.
        // Use the offset value and add it as a parameter to your API request to retrieve paginated data.
        // Deserialize API response and then construct new objects to append to the adapter
        Log.d("customLoadMore", "offset: " + offset);
        loadDatas((offset - 1) * NUMBER_OF_ITEMS, mCurrentStatus);
    }

    private void loadDatasWithClear(Integer status) {
        adapter.clear();
        loadDatas(0, status);
    }
    private void loadDatas(int offset, Integer status) {
        //adapter.clear();
        RequestParams params = new RequestParams();
        params.add("_offset", String.valueOf(offset));
        params.add("_limit", String.valueOf(NUMBER_OF_ITEMS));

        if (status != null) {
            params.add("status", String.valueOf(status));
        }

        RestService.get(this, "downloads", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("loadDatas", "Number of downloads got: " + response.length());
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject o = response.getJSONObject(i);

                        adapter.add(new Download(o.getLong("id"), o.getString("name"), o.getString("link"), o.getLong("size_file"), o.getLong("size_part"), o.getLong("size_file_downloaded"), o.getLong("size_part_downloaded"), (byte) o.getInt("status"), (byte) o.getInt("progress_part"), (byte) o.getInt("progress_file"), o.getLong("average_speed"), o.getLong("current_speed"), o.getLong("time_spent"), o.getLong("time_left"), o.getInt("pid_plowdown"), o.getInt("pid_python"), o.getString("file_path"), (byte) o.getInt("priority"), o.getLong("host_id")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                if (throwable.getMessage() != null) {
                    Log.d("loadDatas", throwable.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                if (throwable.getMessage() != null) {
                    Log.d("loadDatas", throwable.getMessage());
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
        if (id == R.id.action_refresh) {
            loadDatasWithClear(mCurrentStatus);
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
            loadDatasWithClear(null);
        } else if (id == R.id.download_in_progress) {
            loadDatasWithClear(Download.STATUS_IN_PROGRESS);
            mCurrentStatus = Download.STATUS_IN_PROGRESS;
        } else if (id == R.id.download_waiting) {
            loadDatasWithClear(Download.STATUS_WAITING);
            mCurrentStatus = Download.STATUS_WAITING;
        } else if (id == R.id.download_finished) {
            loadDatasWithClear(Download.STATUS_FINISHED);
            mCurrentStatus = Download.STATUS_FINISHED;
        } else if (id == R.id.nav_preferences) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
