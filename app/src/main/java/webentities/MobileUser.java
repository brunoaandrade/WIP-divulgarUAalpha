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
public class MobileUser implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    private String mobileID;

    public MobileUser() {
    }

    public MobileUser(String mobileID) {
        this.mobileID = mobileID;
    }

    public String getMobileID() {
        return mobileID;
    }

    public void setMobileID(String mobileID) {
        this.mobileID = mobileID;
    }
    
    
    
}
