package sample.model;

import java.io.Serializable;

public class User implements Serializable
{
    public static final long serialVersionUID = 20171116L;
    private String username;
    private String password;

    public void setUsername(String username)
    {
        this.username = username;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}
