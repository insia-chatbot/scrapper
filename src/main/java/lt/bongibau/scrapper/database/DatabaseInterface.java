package lt.bongibau.scrapper.database;

import java.sql.*;
public class DatabaseInterface {
    private final static boolean remote = true;
    private final static DatabaseInterface instance= new DatabaseInterface();
    private Connection connection;
    private DatabaseInterface() {
        String url;
        try {
            if(remote)this.connection = DriverManager.getConnection("jdbc:mysql://gateway01.eu-central-1.prod.aws.tidbcloud.com:4000/IAN-database","23dLGqqq48TAXKk.root","sIR0uMJnVvmqmp2F");
            else this.connection = DriverManager.getConnection("jdbc:sqlite:insa_sites.db");
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
            String createTableSQL;
            if (remote) {
                createTableSQL = "CREATE TABLE IF NOT EXISTS DATA (id INT AUTO_INCREMENT PRIMARY KEY, url VARCHAR(2083), content TEXT, modificationDate DATETIME, viewingDate DATETIME)";
            } else {
                createTableSQL = "CREATE TABLE IF NOT EXISTS DATA (id INTEGER PRIMARY KEY AUTOINCREMENT, url TEXT, content TEXT, modificationDate TEXT, viewingDate TEXT)";
            }
            statement.execute(createTableSQL);
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
