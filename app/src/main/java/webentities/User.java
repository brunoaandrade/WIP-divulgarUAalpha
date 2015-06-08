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
public class User implements Serializable{
    
    
    private static final long serialVersionUID = 1L;
    
    private int mec;
    private String name;
    private String mail;
    private String ano;
    private String foto;
    private String curso;
    
    public User() {
    }

    public User(int mec) {
        this.mec = mec;
    }

    
    public User(int mec, String name, String mail, String ano, String foto, String curso) {
        this.mec = mec;
        this.name = name;
        this.mail = mail;
        this.ano = ano;
        this.foto = foto;
        this.curso = curso;
    }
    
    public User(int mec, String name, String mail) {
        this.mec = mec;
        this.name = name;
        this.mail = mail;
    }
    

    public User(String name, String mail) {
        this.name = name;
        this.mail = mail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public int getMec() {
        return mec;
    }

    public void setMec(int mec) {
        this.mec = mec;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }
    
    
    
    
}
