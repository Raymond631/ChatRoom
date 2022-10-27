package sample.util;

import com.mysql.cj.jdbc.MysqlDataSource;
import sample.model.MyMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MyDatabase
{
    private static Connection connection;

    public static void initDB()
    {
        try
        {
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setURL("jdbc:mysql://127.0.0.1:3306/chatroom?characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true");
            dataSource.setUser("Raymond");
            dataSource.setPassword("CSU@82092102");

            connection = dataSource.getConnection();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static boolean toLogin(String username, String password)
    {
        boolean ris = false;
        try
        {
            String sql = "select * from user where username ='" + username + "' and password ='" + password + "'";
            PreparedStatement statement = null;
            statement = connection.prepareStatement(sql);
            ResultSet res = statement.executeQuery();
            ris = res.next();
            statement.close();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        return ris;
    }

    public static boolean toRegister(String username, String password)
    {
        try
        {
            String sql = "insert into user (username,password) values ('" + username + "','" + password + "')";
            PreparedStatement statement = null;
            statement = connection.prepareStatement(sql);
            int n = statement.executeUpdate();
            statement.close();
            if (n == 0)
                return false;
            else
                return true;
        }
        catch (SQLException e)
        {
            return false;
        }
    }

    public static boolean toMessage(String sql)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            int n = statement.executeUpdate();
            statement.close();
            if (n == 0)
                return false;
            else
                return true;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<String> toFriendList(String friendSQL, String sender)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement(friendSQL);
            ResultSet res = statement.executeQuery();
            ArrayList<String> friendList = new ArrayList<>();
            while (res.next())
            {
                String personA = res.getString("personA");
                String personB = res.getString("personB");
                if (personA.equals(sender))
                {
                    friendList.add(personB);
                }
                else
                {
                    friendList.add(personA);
                }
            }
            return friendList;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void toLogOut(String timeSql)
    {
        try
        {
            PreparedStatement statement = statement = connection.prepareStatement(timeSql);
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<MyMessage> getRecord(String senders, String getter)
    {
        ArrayList<MyMessage> record = new ArrayList<>();
        try
        {
            String getTime = "select * from user where username='" + getter + "'";
            PreparedStatement statement = statement = connection.prepareStatement(getTime);
            ResultSet res = statement.executeQuery();
            if (res.next())
            {
                String outTime = res.getString("outTime");
                if (!outTime.equals("null"))
                {
                    String getMessage = "select * from record where time >= '" + outTime + "' and sender ='" + senders + "' and recipient= '" + getter + "'";
                    statement = connection.prepareStatement(getMessage);
                    ResultSet res2 = statement.executeQuery();
                    while (res2.next())
                    {
                        String sender = res2.getString("sender");
                        String content = res2.getString("content");

                        MyMessage msg = new MyMessage();
                        msg.setMessageType(MyMessage.getRecord);
                        msg.setSender(sender);
                        msg.setMessage(content);

                        record.add(msg);
                    }
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        catch (NullPointerException e)//outTime为null时
        {
            return record;
        }
        return record;
    }

    public static void updateInfo(String message)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement(message);
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

    }

    public static ArrayList<String> getUserInfo(String message)
    {
        ArrayList<String> resp = new ArrayList<>();
        try
        {
            PreparedStatement statement = connection.prepareStatement(message);
            ResultSet res = statement.executeQuery();
            if (res.next())
            {
                String username = "";
                username = res.getString("username");
                resp.add(username);

                String introduction = "";
                introduction = res.getString("introduction");
                resp.add(introduction);

                String address = "";
                address = res.getString("address");
                resp.add(address);

                String phone = "";
                phone = res.getString("phone");
                resp.add(phone);
            }
            else
            {
                return null;
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        catch (NullPointerException ignored)
        {
            return null;
        }
        return resp;
    }

    public static boolean addFriend(String check1, String check2, String sql)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement(check1);
            ResultSet res1 = statement.executeQuery();
            if (res1.next())
            {
                return false;
            }
            else
            {
                statement = connection.prepareStatement(check2);
                ResultSet res2 = statement.executeQuery();
                if (res2.next())
                {
                    return false;
                }
                else
                {
                    statement = connection.prepareStatement(sql);
                    int n = statement.executeUpdate();
                    if (n == 1)
                        return true;
                    else
                        return false;
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static String getHistory(String sql)
    {
        String history = "";
        try
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet res = statement.executeQuery();
            while (res.next())
            {
                String sender = res.getString("sender");
                String content = res.getString("content");
                String temp = sender + ":" + content + "\n";
                history += temp;
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        return history;
    }
}
