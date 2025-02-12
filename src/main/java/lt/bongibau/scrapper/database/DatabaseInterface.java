package lt.bongibau.scrapper.database;

import java.sql.*;
public class DatabaseInterface {
    private Connection connection;
    public DatabaseInterface() throws SQLException {
        String url="jdbc:sqlite:mydatabase.db";
        this.connection= DriverManager.getConnection(url);
        this.createTables();
    }
    public void close() throws SQLException {
        this.connection.close();
    }
    public void createTables() throws SQLException {
        Statement statement = this.connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS DATA (id INTEGER AUTOINCREMENT PRIMARY KEY,url TEXT, content TEXT, modificationDate TEXT, viewingDate TEXT)");
        statement.close();
    }
    public void insertData(Data data) throws SQLException {
        String query = "INSERT INTO DATA (url, content, modificationDate, viewingDate) VALUES(?,?,?,?)";
        PreparedStatement preparedStatement = this.connection.prepareStatement(query);
        preparedStatement.setString(1, data.url());
        preparedStatement.setString(2, data.content());
        preparedStatement.setString(3, data.modificationDate().toString());
        preparedStatement.setString(4, data.viewingDate().toString());
        preparedStatement.execute();
        preparedStatement.close();
    }
}
