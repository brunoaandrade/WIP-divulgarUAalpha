/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webentities;

import java.io.Serializable;

/**
 *
 * @author MoLt1eS
 */
public class Image implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    private int projectID;
    
    private String imageData;
    
    private String title;
    
    private String description;
    
    private int pos;

    public Image(int projectID, String imageData, String title, String description, int pos) {
        this.projectID = projectID;
        this.imageData = imageData;
        this.title = title;
        this.description = description;
        this.pos = pos;
    }

    public Image() {
    }

    public int getProjectID() {
        return projectID;
    }

    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
    
    
    
}
