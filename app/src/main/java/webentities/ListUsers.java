package webentities;

import java.util.List;

public class ListUsers {

    private List<User> listUser;

    public ListUsers(List<User> listUser) {
        this.listUser = listUser;
    }

    public List<User> getListUser() {
        return listUser;
    }

    public void setListUser(List<User> listUser) {
        this.listUser = listUser;
    }


}