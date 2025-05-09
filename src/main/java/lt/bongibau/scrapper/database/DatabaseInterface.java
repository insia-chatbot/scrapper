package lt.bongibau.scrapper.database;

import java.sql.*;
public class DatabaseInterface {
    private final static DatabaseInterface instance= new DatabaseInterface();
    private Connection connection;
    private DatabaseInterface() {
        String url="jdbc:sqlite:insa_sites.db";
        try {
            this.connection = DriverManager.getConnection(url);
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        this.createTables();
    }
    public void close() {
        try{
            this.connection.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
    public void createTables() {
        try {
            Statement statement = this.connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS DATA (id INTEGER PRIMARY KEY AUTOINCREMENT,url TEXT, content TEXT, modificationDate TEXT, viewingDate TEXT)");
            statement.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
    public synchronized void insertData(Data data) {
        try {
            String query = "INSERT INTO DATA (url, content, modificationDate, viewingDate) VALUES(?,?,?,?)";
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            preparedStatement.setString(1, data.url());
            preparedStatement.setString(2, data.content());
            if(data.modificationDate()!=null)preparedStatement.setString(3, data.modificationDate().toString());
            else preparedStatement.setNull(3, Types.VARCHAR);
            preparedStatement.setString(4, data.viewingDate().toString());
            preparedStatement.execute();
            preparedStatement.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }

    }

    public void deleteData(){
        try {
            Statement statement = this.connection.createStatement();
            statement.execute("DELETE FROM DATA");
            statement.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public static DatabaseInterface getInstance() {
        return instance;
    }
}
