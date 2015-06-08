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
public class GetProjectImage implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    private Project project;
    
    private List<Image> imageList;

    public GetProjectImage() {
    }

    public GetProjectImage(Project project, List<Image> imageList) {
        this.project = project;
        this.imageList = imageList;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<Image> getImageList() {
        return imageList;
    }

    public void setImageList(List<Image> imageList) {
        this.imageList = imageList;
    }
    
    
    
    
    
}
