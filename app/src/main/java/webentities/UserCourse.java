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
public class UserCourse implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    private int courseID;
    
    private String courseName;

    public UserCourse() {
    }

    public UserCourse(String courseName) {
        this.courseName = courseName;
    }

    public UserCourse(int courseID) {
        this.courseID = courseID;
    }

    public UserCourse(int courseID, String courseName) {
        this.courseID = courseID;
        this.courseName = courseName;
    }
    

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }
    
    
    
}
