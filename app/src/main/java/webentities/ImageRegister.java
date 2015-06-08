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
public class ImageRegister implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private List<Image> imageList;
    
    private int projectID;
    

    public ImageRegister(List<Image> imageList) {
        this.imageList = imageList;
    }

    public ImageRegister() {
    }

    public List<Image> getImageList() {
        return imageList;
    }

    public void setImageList(List<Image> imageList) {
        this.imageList = imageList;
    }
    
    
    
    
}
