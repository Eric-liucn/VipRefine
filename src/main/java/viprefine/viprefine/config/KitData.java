package viprefine.viprefine.config;

import org.spongepowered.api.entity.living.player.Player;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class KitData {
    private static String DATABASEURL;
    private static Connection connection;
    private static Statement statement;

    public static Connection getConnection() {
        return connection;
    }

    public static String getDATABASEURL() {
        return DATABASEURL;
    }

    public static Statement getStatement() {
        return statement;
    }

    public static void setUpDataBase(File file) throws IOException {
        if (!file.exists()){
            file.mkdir();
        }

        File db = new File(file,"kitRedeem.db");

        if (!db.exists()){
            db.createNewFile();
        }

        DATABASEURL = "jdbc:sqlite:"+db.getPath();

        try {
            connection = DriverManager.getConnection(DATABASEURL);
            statement = connection.createStatement();
            if (connection!=null){
                createTableIfNotExist();
            }
        }catch (SQLException e){
            e.getMessage();
        }
    }

    private static void createTableIfNotExist(){
        String createTable = "CREATE TABLE IF NOT EXISTS KITREDEEM (\nUUID TEXT PRIMARY KEY,\nNAME TEXT);";
        try {
            statement.execute(createTable);
        }catch (SQLException e){
            e.getMessage();
        }
    }

    public static void addNewColumn(String date) {
        String addNewColumn = String.format("ALTER TABLE KITREDEEM ADD COLUMN TAG_%s INTEGER;",date);
        try{
            statement.execute(addNewColumn);
        }catch (SQLException e){
            e.getMessage();
        }
    }

    public static void setUpUser(Player player) {
        String uuid = player.getUniqueId().toString();
        String name = player.getName();
        String setUpUser = String.format("INSERT INTO KITREDEEM (UUID,NAME)\nVALUES('%s','%s');",uuid,name);
        try{
            statement.execute(setUpUser);
        }catch (SQLException e){e.getMessage();}
    }

    public static void whenRedeem(Player player,String date){
        String uuid = player.getUniqueId().toString();
        String setData = String.format("UPDATE KITREDEEM SET TAG_%s = 'Y' WHERE UUID = '%s' ;",date,uuid);
        try{
            statement.execute(setData);
        }catch (SQLException e){
            e.getMessage();
        }
    }

    public static String getKitRedeemData(Player player,String date) {
        String uuid = player.getUniqueId().toString();
        String getData = String.format("SELECT * FROM KITREDEEM WHERE UUID = '%s';",uuid);
        try (ResultSet resultSet = statement.executeQuery(getData)){
            while (resultSet.next()){
                if (resultSet.getString("TAG_"+date) != null){
                    return resultSet.getString("TAG_"+date);
                }else return "N";
            }
        }catch (SQLException e){
            e.getMessage();
        }
        return "N";
    }


}
