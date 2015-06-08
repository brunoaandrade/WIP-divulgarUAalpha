/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webentities;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 *
 * @author MoLt1eS
 */
public class Project implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    private int projectID;
    
    private int ownerID;
   
    private String name;
    
    private String description;
    
    private int capeID;

    private Timestamp createdDate;
    
    private String courseName; 
    
    private String capeImage;
    
    private String authorName;
    
    private int nViews;
    
    public Project() {
    }

    public Project(int projectID) {
        this.projectID = projectID;
    }
    
    public Project(int ownerID, String name, String description) {
        this.ownerID = ownerID;
        this.name = name;
        this.description = description;
    }

    public Project(int projectID, String name, String description, Timestamp createdDate, String courseName, String capeImage) {
        this.projectID = projectID;
        this.name = name;
        this.description = description;
        this.createdDate = createdDate;
        this.courseName = courseName;
        this.capeImage = capeImage;
    }

    public Project(int projectID, int ownerID, String name, String description, int capeID, Timestamp createdDate, String courseName, String capeImage, String authorName, int nViews) {
        this.projectID = projectID;
        this.ownerID = ownerID;
        this.name = name;
        this.description = description;
        this.capeID = capeID;
        this.createdDate = createdDate;
        this.courseName = courseName;
        this.capeImage = capeImage;
        this.authorName = authorName;
        this.nViews = nViews;
    }

    public Project(int projectID, String name, String description, Timestamp createdDate, String courseName, String capeImage, String authorName, int nViews) {
        this.projectID = projectID;
        this.name = name;
        this.description = description;
        this.createdDate = createdDate;
        this.courseName = courseName;
        this.capeImage = capeImage;
        this.authorName = authorName;
        this.nViews = nViews;
    }

    
    
    
    public Project(int ownerID, String description, int capeID) {
        this.ownerID = ownerID;
        this.description = description;
        this.capeID = capeID;
    }

    public Project(int ownerID, String description, int capeID, Timestamp createdDate) {
        this.ownerID = ownerID;
        this.description = description;
        this.capeID = capeID;
        this.createdDate = createdDate;
    }

    public int getCapeID() {
        return capeID;
    }

    public void setCapeID(int capeID) {
        this.capeID = capeID;
    }
    
    public int getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(int ownerID) {
        this.ownerID = ownerID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCapeImage() {
        return capeImage;
    }

    public void setCapeImage(String capeImage) {
        this.capeImage = capeImage;
    }

    public int getProjectID() {
        return projectID;
    }

    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public int getnViews() {
        return nViews;
    }

    public void setnViews(int nViews) {
        this.nViews = nViews;
    }
   
    
}
