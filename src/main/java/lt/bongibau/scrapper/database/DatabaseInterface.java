package lt.bongibau.scrapper.database;

import java.sql.*;
public class DatabaseInterface {
    private Connection connection;
    public DatabaseInterface() throws SQLException {
        String url="jdbc:sqlite:mydatabase.db";
        this.connection= DriverManager.getConnection(url);
        this.createTables();
    }
    public void createTables() throws SQLException {
        Statement statement = this.connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS DATA (id INT AUTOINCREMENT PRIMARY KEY,url TEXT, content TEXT, dateModification TEXT, dateVisionage TEXT)");
    }
    public void insertData(Data data) throws SQLException {
        String query = "INSERT INTO DATA (url, content, dateModification, dateVisionage) VALUES(?,?,?,?)";
        PreparedStatement preparedStatement = this.connection.prepareStatement(query);
        preparedStatement.setString(1, data.getUrl());
        preparedStatement.setString(2, data.getContent());
        preparedStatement.setString(3, data.getDateModification().toString());
        preparedStatement.setString(4, data.getDateVisionage().toString());
        preparedStatement.execute();
    }
}
