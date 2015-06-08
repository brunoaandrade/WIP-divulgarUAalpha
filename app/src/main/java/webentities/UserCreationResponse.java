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
public class UserCreationResponse implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    private int userID;
    
    private UserCreationAnswer userCreationAnswer;

    public UserCreationResponse() {
    }

    public UserCreationResponse(int userID, UserCreationAnswer userCreationAnswer) {
        this.userID = userID;
        this.userCreationAnswer = userCreationAnswer;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public UserCreationAnswer getUserCreationAnswer() {
        return userCreationAnswer;
    }

    public void setUserCreationAnswer(UserCreationAnswer userCreationAnswer) {
        this.userCreationAnswer = userCreationAnswer;
    }
    
    
    
}
