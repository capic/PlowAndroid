package plow_android.capic.com.plowandroid.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import plow_android.capic.com.plowandroid.R;
import plow_android.capic.com.plowandroid.beans.Download;

/**
 * Created by Vincent on 27/01/2016.
 */
public class DownloadsAdapter extends ArrayAdapter<Download> {

    public DownloadsAdapter(Context context, int resource, List<Download> downloads) {
        super(context, resource, downloads);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Download download = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_download, parent, false);
        }

        RelativeLayout downloadLayout = (RelativeLayout) convertView.findViewById(R.id.downloadLayout);
        TextView downloadName = (TextView) convertView.findViewById(R.id.downloadName);
        TextView downloadLink = (TextView) convertView.findViewById(R.id.downloadLink);
        TextView downloadPercent = (TextView) convertView.findViewById(R.id.downloadPercent);

        switch (((Byte)download.getStatus()).intValue()) {
            case Download.STATUS_WAITING:
                downloadLayout.setBackgroundColor(Color.argb(50, 3, 53, 254)); // bleu
                break;
            case Download.STATUS_IN_PROGRESS:
                downloadLayout.setBackgroundColor(Color.argb(50, 49, 251, 4)); // vert
                break;
            case Download.STATUS_FINISHED:
                downloadLayout.setBackgroundColor(Color.argb(50, 254, 229, 3)); //jaune
                break;
        }

        downloadName.setText(download.getName());
        downloadLink.setText(download.getLink());
        downloadPercent.setText(String.valueOf(download.getProgressFile()));

        Log.d("getView", "<=== download adapter ===> ");
        Log.d("getView","downloadName => text: " + downloadName.getText());
        Log.d("getView","downloadLink => text: " + downloadLink.getText());

        return convertView;
    }
}
