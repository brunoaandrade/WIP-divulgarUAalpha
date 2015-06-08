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
public class ListImages implements Serializable{
    private static final long serialVersionUID = 1L;
    
    List<Image> listImages;

    public ListImages() {
    }

    public ListImages(List<Image> listImages) {
        this.listImages = listImages;
    }

    public List<Image> getListImages() {
        return listImages;
    }

    public void setListImages(List<Image> listImages) {
        this.listImages = listImages;
    }
    
    
    
}
