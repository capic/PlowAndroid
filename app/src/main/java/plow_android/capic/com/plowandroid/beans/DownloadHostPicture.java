package plow_android.capic.com.plowandroid.beans;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.Serializable;

/**
 * Created by Vincent on 27/01/2016.
 */
public class DownloadHostPicture implements Serializable {

    private Long id;
    private Bitmap image;

    public DownloadHostPicture(Long id, Bitmap image) {
        this.id = id;
        this.image = image;
    }

    public DownloadHostPicture(Long id, String base64) {
        this.id = id;

        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        this.image = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
