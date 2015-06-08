package addProject;

/**
 * Created by Bruno on 15/05/2015.
 */

import android.graphics.Bitmap;

public class FilterItem {
    private Bitmap image;
    private String title;

    public FilterItem(Bitmap image, String title) {
        super();
        this.image = image;
        this.title = title;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
