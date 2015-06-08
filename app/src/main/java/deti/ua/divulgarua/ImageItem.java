package deti.ua.divulgarua;

/**
 * Created by LuisAfonso on 13-05-2015.
 */
import android.graphics.Bitmap;

import java.util.Date;

public class ImageItem {
    private Bitmap image;
    private String title;
    private Date date;
    private int views;
    private String user;
    private int projectID;

    public ImageItem(Bitmap image, String title, Date date, int views, String user, int projectID) {
        super();
        this.image = image;
        this.title = title;
        this.date = date;
        this.views = views;
        this.user = user;
        this.projectID = projectID;
    }

    public int getProjectID() {
        return projectID;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}