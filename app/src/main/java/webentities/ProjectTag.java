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
public class ProjectTag implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    private int projectID;
    
    private String tag;

    public ProjectTag() {
    }

    public ProjectTag(int projectID, String tag) {
        this.projectID = projectID;
        this.tag = tag;
    }

    public int getProjectID() {
        return projectID;
    }

    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
    
    
    
}
