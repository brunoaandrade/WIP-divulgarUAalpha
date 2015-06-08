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
public class Coworker implements Serializable{
    
    private static final long serialVersionUID = 1L;
 
    private int projectID;
    
    private int user;
    
    private boolean isIn;

    public Coworker() {
    }

    public Coworker(int projectID, int user) {
        this.projectID = projectID;
        this.user = user;
    }

    public Coworker(int projectID, int user, boolean isIn) {
        this.projectID = projectID;
        this.user = user;
        this.isIn = isIn;
    }

    public int getProjectID() {
        return projectID;
    }

    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public boolean isIsIn() {
        return isIn;
    }

    public void setIsIn(boolean isIn) {
        this.isIn = isIn;
    }
    
    
    
}
