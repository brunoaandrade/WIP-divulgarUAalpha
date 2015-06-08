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
public class ProjectRegisterResponse implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    private int projectid;
    
    private AnswerStatus answerStatus;

    public ProjectRegisterResponse() {
    }

    public ProjectRegisterResponse(int projectid, AnswerStatus answerStatus) {
        this.projectid = projectid;
        this.answerStatus = answerStatus;
    }

    public int getProjectid() {
        return projectid;
    }

    public void setProjectid(int projectid) {
        this.projectid = projectid;
    }

    public AnswerStatus getAnswerStatus() {
        return answerStatus;
    }

    public void setAnswerStatus(AnswerStatus answerStatus) {
        this.answerStatus = answerStatus;
    }
    
}
