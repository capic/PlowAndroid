package plow_android.capic.com.plowandroid.services;

import com.loopj.android.http.*;

/**
 * Created by Vincent on 26/01/2016.
 */
public class RestService {
    private static final String BASE_URL = "http://capic.hd.free.fr:3000/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }
}
