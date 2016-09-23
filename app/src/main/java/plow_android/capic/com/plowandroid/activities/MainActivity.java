package plow_android.capic.com.plowandroid.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;
import plow_android.capic.com.plowandroid.R;
import plow_android.capic.com.plowandroid.adapters.DownloadsAdapter;
import plow_android.capic.com.plowandroid.beans.Download;
import plow_android.capic.com.plowandroid.listeners.EndlessScrollListener;
import plow_android.capic.com.plowandroid.services.RestService;
import rx.functions.Action1;
import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.PubSubData;
import ws.wamp.jawampa.WampClient;
import ws.wamp.jawampa.WampClientBuilder;
import ws.wamp.jawampa.connection.IWampConnectorProvider;
import ws.wamp.jawampa.transport.netty.NettyWampClientConnectorProvider;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int NUMBER_OF_ITEMS = 15;

    private DownloadsAdapter adapter;
    private List<Long> listDownloadsId; // liste qui evite d'utiliser une hashmap (pas forcement ordonne dans le meme ordre)
    private List<Download> listDownloads;
    private Integer mCurrentStatus = null;
    private ListView listview;
    private SwipeRefreshLayout swipeContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        websocketManagement();

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                loadDatas(0, mCurrentStatus);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


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

        listview = (ListView) findViewById(R.id.listview);
        listDownloads = new LinkedList<>();
        listDownloadsId = new LinkedList<>();
        adapter = new DownloadsAdapter(this, R.layout.item_download, listDownloads, hashDownloadHostPictures);
        listview.setAdapter(adapter);

        loadDatas(0, null);

       /* listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),
                        "Click ListItem Number " + position, Toast.LENGTH_LONG)
                        .show();
            }
        });

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),
                        "Click ListItem Number " + position, Toast.LENGTH_LONG)
                        .show();
                return true;
            }
        });*/

        listview.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                customLoadMoreDataFromApi(page);
                // or customLoadMoreDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });

        registerForContextMenu(listview);


    }

    private void receiveDownloadsMessage(PubSubData arg0) {
        if(arg0 != null) {
            Log.d("WAMP", "receiveDownloadsMessage");
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("WAMP", "receiveDownloadsMessage run");
                    ArrayNode nodes = arg0.arguments();

                    for (int i = 0; i < nodes.size(); i++) {
                        ObjectNode node = (ObjectNode) nodes.get(i);
                        Log.d("WAMP", node.toString());
                        Download download = new Download();
                        download.fromJson(node);
                        int idx = listDownloadsId.indexOf(download.getId());
                        Log.d("WAMP", "idx " + idx);
                        listDownloads.set(idx, download);
                        adapter.notifyDataSetChanged();
                        Log.d("WAMP", String.valueOf(download.getProgressFile()));
                    }

                    try {
                        JSONObject jsonComment = new JSONObject(nodes.toString());
                        Log.d("WAMP", jsonComment.toString());
                    } catch (JSONException e) {
                    }
                }
            });
        }
    }

    private void websocketManagement() {
        WampClientBuilder builder = new WampClientBuilder();
        final WampClient client;
        try {

            builder.withConnectorProvider(new NettyWampClientConnectorProvider())
                    .withUri("ws://capic.hd.free.fr:8181/ws")
                    .withRealm("realm1")
                    .withInfiniteReconnects()
                    .withReconnectInterval(10, TimeUnit.SECONDS);

            client = builder.build();

            client.statusChanged().subscribe(t1 -> {
                if(t1 instanceof WampClient.ConnectedState) {
                    client.makeSubscription("plow.downloads.downloads").subscribe(arg0 -> {
                        Log.d("WAMP", "publish");
                        receiveDownloadsMessage(arg0);
                    }, arg0 -> {
                        if(arg0 != null) {
                            Log.i("WAMP", " call Throwable response: " + arg0.toString());
                        }
                    });
                }
            });
            client.open();

        } catch (ApplicationError applicationError) {
            applicationError.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, view, menuInfo);
        if (view.getId() == R.id.listview) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(listDownloads.get(info.position).getName());
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.download_context, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        final Download download = listDownloads.get(info.position);

        //  info.position will give the index of selected item intIndexSelected=info.position
        if(item.getItemId() == R.id.download_context_item_delete)
        {
            RequestParams params = new RequestParams();
            params.add("id", String.valueOf(download.getId()));

            Log.d("onContextItemSelected", "Delete id: " + download.getId());
            // Code to execute when clicked on This Item
            RestService.delete(this, "downloads/" + download.getId(), null,  new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                    try {
                        Log.d("onContextItemSelected", "Download id: " + download.getId() + " deletion => " + jsonObject.getBoolean("return"));
                        if (jsonObject.getBoolean("return")) {
                            listDownloads.remove(info.position);
                            listDownloadsId.remove(info.position);
                            adapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                    Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                    if (throwable.getMessage() != null) {
                        Log.d("onContextItemSelected", throwable.getMessage());
                    }
                }
            });
        }

        return true;
    }

    // Append more data into the adapter
    public void customLoadMoreDataFromApi(int offset) {
        /*if (offset >= listDownloads.size()) {
            listview.addFooterView(LayoutInflater.from(MainActivity.this).inflate(R.layout.footer_list_downloads, null, false));
        }
*/
        // This method probably sends out a network request and appends new data items to your adapter.
        // Use the offset value and add it as a parameter to your API request to retrieve paginated data.
        // Deserialize API response and then construct new objects to append to the adapter
        Log.d("customLoadMore", "offset: " + offset);
        swipeContainer.setRefreshing(true);
        loadDatas((offset - 1) * NUMBER_OF_ITEMS, mCurrentStatus);
    }

    private void loadDatasWithClear(Integer status) {
        adapter.clear();
        loadDatas(0, status);
    }
    private void loadDatas(int offset, Integer status) {
        // TODO catch si l'adresse serveur est foireuse
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

//                        adapter.add(new Download(o.getLong("id"), o.getString("name"), o.getString("link"), o.getLong("size_file"), o.getLong("size_part"), o.getLong("size_file_downloaded"), o.getLong("size_part_downloaded"), (byte) o.getInt("status"), (byte) o.getInt("progress_part"), (byte) o.getInt("progress_file"), o.getLong("average_speed"), o.getLong("current_speed"), o.getLong("time_spent"), o.getLong("time_left"), o.getInt("pid_plowdown"), o.getInt("pid_python"), o.getString("file_path"), (byte) o.getInt("priority"), o.getLong("host_id")));
                        listDownloads.add(new Download(o.getLong("id"), o.getString("name"), o.getString("link"), o.getLong("size_file"), o.getLong("size_part"), o.getLong("size_file_downloaded"), o.getLong("size_part_downloaded"), (byte) o.getInt("status"), (byte) o.getInt("progress_part"), (byte) o.getInt("progress_file"), o.getLong("average_speed"), o.getLong("current_speed"), o.getLong("time_spent"), o.getLong("time_left"), o.getInt("pid_plowdown"), o.getInt("pid_python"), o.getString("file_path"), (byte) o.getInt("priority"), o.getLong("host_id")));
                        listDownloadsId.add(o.getLong("id"));
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
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
