package plow_android.capic.com.plowandroid.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by Vincent on 26/01/2016.
 */
public class RestService {
    private static final String BASE_URL = "http://capic.hd.free.fr:3000/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    private static String getAbsoluteUrl(Context context, String relativeUrl) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String url = prefs.getString("server_address", "NULL");

        if (("NULL").equals(url)) {
            url = BASE_URL;
        }

        if (!url.endsWith("/")) {
            url += "/";
        }
        Log.d("getAbsoluteUrl", "Address: " + url + relativeUrl);

        return url + relativeUrl;

    }

    public static void get(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(context, url), params, responseHandler);
    }

    public static void post(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(context, url), params, responseHandler);
    }

    public static void delete(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.delete(getAbsoluteUrl(context, url), params, responseHandler);
    }
}
