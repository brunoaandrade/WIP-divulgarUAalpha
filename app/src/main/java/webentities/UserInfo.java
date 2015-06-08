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
public class UserInfo implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private User user;
    
    private List<UserCourse> courseList;
    
    private MobileUser mobileUser;

    public UserInfo() {
    }

    public UserInfo(User user, List<UserCourse> courseList, MobileUser mobileUser) {
        this.user = user;
        this.courseList = courseList;
        this.mobileUser = mobileUser;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<UserCourse> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<UserCourse> courseList) {
        this.courseList = courseList;
    }

    public MobileUser getMobileUser() {
        return mobileUser;
    }

    public void setMobileUser(MobileUser mobileUser) {
        this.mobileUser = mobileUser;
    }
    
    
    
}
