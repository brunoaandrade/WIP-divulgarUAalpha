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
    private String projectCat;
    private String projectDescrp;

    public ImageItem(Bitmap image, String title, Date date, int views, String user, int projectID, String projectCat, String projectDescrp) {
        super();
        this.image = image;
        this.title = title;
        this.date = date;
        this.views = views;
        this.user = user;
        this.projectID = projectID;
        this.projectCat = projectCat;
        this.projectDescrp = projectDescrp;
    }

    public String getProjectDescrp() {
        return projectDescrp;
    }

    public void setProjectDescrp(String projectDescrp) {
        this.projectDescrp = projectDescrp;
    }

    public String getProjectCat() {
        return projectCat;
    }

    public void setProjectCat(String projectCat) {
        this.projectCat = projectCat;
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

    public String getViews() {
        return String.valueOf(views);
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