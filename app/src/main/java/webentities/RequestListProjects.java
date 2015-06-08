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
public class RequestListProjects implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private ProjectOrderType projectOrderType;
    
    private String query;

    public RequestListProjects() {
    }

    public RequestListProjects(ProjectOrderType projectOrderType, String query) {
        this.projectOrderType = projectOrderType;
        this.query = query;
    }

    public ProjectOrderType getProjectOrderType() {
        return projectOrderType;
    }

    public void setProjectOrderType(ProjectOrderType projectOrderType) {
        this.projectOrderType = projectOrderType;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
    
    
    
    
    
}
