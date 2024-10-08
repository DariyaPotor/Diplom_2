package pojo;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class UserData {
    private String email;
    private String password;
    private String name;
    private boolean success;

    public UserData(String email, String password, String name){
        this.email = email;
        this.password = password;
        this.name = name;
    }
    public UserData(){}
}