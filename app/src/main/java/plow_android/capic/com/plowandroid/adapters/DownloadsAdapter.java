package plow_android.capic.com.plowandroid.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import plow_android.capic.com.plowandroid.R;
import plow_android.capic.com.plowandroid.beans.Download;

/**
 * Created by Vincent on 27/01/2016.
 */
public class DownloadsAdapter extends ArrayAdapter<Download> {
    public static final int VIEW_TYPE_LOADING = 0;
    public static final int VIEW_TYPE_ACTIVITY = 1;

    private HashMap<Long, Bitmap> hashDownloadHostPictures;
    private List<Download> listDownloads;

    public DownloadsAdapter(Context context, int resource, List<Download> downloads, HashMap<Long, Bitmap> hashDownloadHostPictures) {
        super(context, resource, downloads);
        this.hashDownloadHostPictures = hashDownloadHostPictures;
        this.listDownloads = downloads;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("getView", "position: " + position + " | taille: " + listDownloads.size());
        /*if (getItemViewType(position) == VIEW_TYPE_LOADING) {
            return getFooterView(position, convertView, parent);
        } else {*/
            Download download = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_download, parent, false);
            }

            TextView downloadName = (TextView) convertView.findViewById(R.id.downloadName);
            TextView downloadLink = (TextView) convertView.findViewById(R.id.downloadLink);
            TextView downloadPercent = (TextView) convertView.findViewById(R.id.downloadPercent);
            ImageView icon = (ImageView) convertView.findViewById(R.id.downloadStatus);
            ImageView host = (ImageView) convertView.findViewById(R.id.downloadHost);

            switch (((Byte) download.getStatus()).intValue()) {
                case Download.STATUS_WAITING:
                    icon.setImageResource(R.drawable.ic_pause_black_24dp);
                    break;
                case Download.STATUS_IN_PROGRESS:
                    icon.setImageResource(R.drawable.ic_play_black_24dp);
                    break;
                case Download.STATUS_FINISHED:
                    icon.setImageResource(R.drawable.ic_check_black_24dp);
                    break;
            }

            Bitmap image = hashDownloadHostPictures.get(download.getHostId());

            downloadName.setText(download.getName());
            downloadLink.setText(download.getLink());
            downloadPercent.setText(String.valueOf(download.getProgressFile()));
            host.setImageBitmap(image);

            Log.d("getView", "<=== download adapter ===> ");
            Log.d("getView", "downloadName => text: " + downloadName.getText());
            Log.d("getView", "downloadLink => text: " + downloadLink.getText());
            Log.d("getView", "downloadHost => src: " + host.getDrawable().toString());

            return convertView;
//        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == listDownloads.size()) ? VIEW_TYPE_LOADING : VIEW_TYPE_ACTIVITY;
    }

    private View getFooterView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            row = LayoutInflater.from(getContext()).inflate(R.layout.footer_list_downloads, parent, false);
        }

        return row;
    }
}
