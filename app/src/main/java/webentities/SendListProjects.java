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
public class SendListProjects implements Serializable{
    
    
    private static final long serialVersionUID = 4581012321740530080L;
 
    private List<Project> listPorjects;
    
    private Image projectImage;

    public SendListProjects() {
    }

    public SendListProjects(List<Project> listPorjects, Image projectImage) {
        this.listPorjects = listPorjects;
        this.projectImage = projectImage;
    }

    public List<Project> getListPorjects() {
        return listPorjects;
    }

    public void setListPorjects(List<Project> listPorjects) {
        this.listPorjects = listPorjects;
    }

    public Image getProjectImage() {
        return projectImage;
    }

    public void setProjectImage(Image projectImage) {
        this.projectImage = projectImage;
    }
    
}
