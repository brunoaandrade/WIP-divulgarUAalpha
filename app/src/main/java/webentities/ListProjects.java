/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webentities;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author MoLt1eS
 */
public class ListProjects implements Serializable{
    private static final long serialVersionUID = 1L;
    
    List<Project> listProject;

    public ListProjects() {
    }

    public ListProjects(List<Project> listProject) {
        this.listProject = listProject;
    }

    public List<Project> getListProject() {
        return listProject;
    }

    public void setListProject(List<Project> listProject) {
        this.listProject = listProject;
    }
    
    
    
    
}
