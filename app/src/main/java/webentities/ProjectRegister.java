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
public class ProjectRegister implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    private Project project;
    
    private List<String> listTags;
    
    private List<Integer> listCoworker;
    
    private UserCourse userCourse;

    public ProjectRegister() {
    }

    public ProjectRegister(Project project) {
        this.project = project;
    }

    public ProjectRegister(Project project, List<String> listTags, List<Integer> listCoworker, UserCourse userCourse) {
        this.project = project;
        this.listTags = listTags;
        this.listCoworker = listCoworker;
        this.userCourse = userCourse;
    }

    

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<String> getListTags() {
        return listTags;
    }

    public void setListTags(List<String> listTags) {
        this.listTags = listTags;
    }

    public List<Integer> getListCoworker() {
        return listCoworker;
    }

    public void setListCoworker(List<Integer> listCoworker) {
        this.listCoworker = listCoworker;
    }

    public UserCourse getUserCourse() {
        return userCourse;
    }

    public void setUserCourse(UserCourse userCourse) {
        this.userCourse = userCourse;
    }
    
    
    
}
