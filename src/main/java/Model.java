
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class Model {
    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    private String DBName = "devcenter";
    private String DBUser = "root";
    private String DBPassword = "";
    //private String DBName = "heroku_be826f701685add";
    //private String DBUser = "b363f5860f72e3";
    //private String DBPassword = "94e5fe61";

    public Model() throws Exception {
        try {
            connect = DriverManager.getConnection("jdbc:mysql://b363f5860f72e3:94e5fe61@us-cdbr-iron-east-01.cleardb.net/heroku_be826f701685add?reconnect=true");
            //connect = DriverManager.getConnection("jdbc:mysql://localhost/"+DBName+"?user="+this.DBUser+"&password="+this.DBPassword);
        } catch (Exception e) {
            throw e;
        }
    }

    public String create_developer(String email, String plang, String lang) throws SQLException {
        //Insert on developers table and retrieve dev_id;
        preparedStatement = this.connect
                .prepareStatement("INSERT INTO developers VALUES (default, ?, default, default)");
        preparedStatement.setString(1, email);
        preparedStatement.executeUpdate();

        preparedStatement = this.connect
                .prepareStatement("SELECT last_insert_id() AS last_id FROM developers");
        resultSet = preparedStatement.executeQuery();
        resultSet.next();
        String dev_id = resultSet.getString("last_id");

        //Insert on programming languages table and retrieving plang_id;
        preparedStatement = this.connect
                .prepareStatement("INSERT INTO programming_languages VALUES (default, ?, ?)");
        preparedStatement.setString(1, dev_id);
        preparedStatement.setString(2, plang);
        preparedStatement.executeUpdate();

        preparedStatement = this.connect
                .prepareStatement("SELECT last_insert_id() AS last_id FROM programming_languages");
        resultSet = preparedStatement.executeQuery();
        resultSet.next();
        String plang_id = resultSet.getString("last_id");

        //Insert on languages table and retrieving lang_id;
        preparedStatement = this.connect
                .prepareStatement("INSERT INTO languages VALUES (default, ?, ?)");
        preparedStatement.setString(1, dev_id);
        preparedStatement.setString(2, lang);
        preparedStatement.executeUpdate();

        preparedStatement = this.connect
                .prepareStatement("SELECT last_insert_id() AS last_id FROM languages");
        resultSet = preparedStatement.executeQuery();
        resultSet.next();
        String lang_id = resultSet.getString("last_id");

        //Update dating developers table
        preparedStatement = this.connect
                .prepareStatement("UPDATE developers SET plang_id=?, lang_id=? WHERE id="+dev_id);
        preparedStatement.setString(1, plang_id);
        preparedStatement.setString(2, lang_id);
        preparedStatement.executeUpdate();

        close();
        return "[{\"status\":\"success\",\"ID\":\""+dev_id+"\",\"developer\":\""+email+"\",\"programming_language\":\""+plang+"\",\"language\":\""+lang+"\"}]";
    }


    public String delete_developer(int id) throws SQLException {
        preparedStatement = this.connect
                .prepareStatement("DELETE FROM developers WHERE id = ?");
        preparedStatement.setInt(1, id);
        int deleted = preparedStatement.executeUpdate();

        close();
        if(deleted == 0) return "[{\"status\":\"failed\",\"error\":\"No developer found to deleted.\"}]";
        else return "[{\"status\":\"success\",\"message\":\"Specified developer is deleted.\"}]";
    }

    public String add_interview(int dev_id, int score, String comment) throws SQLException {
        preparedStatement = this.connect
                .prepareStatement("INSERT INTO interviews VALUES(default,?,?,?)");
        preparedStatement.setInt(1, dev_id);
        preparedStatement.setInt(2, score);
        preparedStatement.setString(3, comment);
        int added = preparedStatement.executeUpdate();

        close();
        if(added == 0) return "[{\"status\":\"failed\",\"error\":\"System error!\"}]";
        else return "[{\"status\":\"success\",\"message\":\"Interview successfully added.\"}]";
    }

    public String delete_interview(int interview_id) throws SQLException {
        preparedStatement = this.connect
                .prepareStatement("DELETE FROM interviews WHERE interview_id = ?");
        preparedStatement.setInt(1, interview_id);
        int deleted = preparedStatement.executeUpdate();

        close();
        if(deleted == 0) return "[{\"status\":\"failed\",\"error\":\"No interview found to deleted.\"}]";
        else return "[{\"status\":\"success\",\"message\":\"Specified interview is deleted.\"}]";
    }

    public String retrieve_interviews(int count, int dev_id) throws SQLException {
        preparedStatement = this.connect
                .prepareStatement("SELECT d.id AS ID, d.email AS Email, pl.name AS ProgrammingLanguage, l.code AS LanguageCode FROM developers d INNER JOIN programming_languages pl ON d.plang_id = pl.plang_id INNER JOIN languages l ON d.lang_id = l.lang_id WHERE d.id = "+dev_id);
        resultSet = preparedStatement.executeQuery();
        resultSet.next();
        String JSON = "[{\"Developer\":[{\"ID\":\""+resultSet.getString("ID")+"\",\"Email\":\""+resultSet.getString("Email")+"\",\"Programming Language\":\""+resultSet.getString("ProgrammingLanguage")+"\",\"Language\":\""+resultSet.getString("LanguageCode")+"\"}],";

        preparedStatement = this.connect
                .prepareStatement("SELECT * FROM interviews WHERE id = "+dev_id+" LIMIT "+count);
        resultSet = preparedStatement.executeQuery();
        JSON += "\"Interviews\":[";
        if(!resultSet.next()){
            JSON += "{\"status\":\"failed\",\"error\":\"No Interview found\"},";
        }
        else{
            do{
                JSON += "{\"ID\":\""+resultSet.getString("interview_id")+"\",\"Score\":\""+resultSet.getString("score")+"\",\"Comment\":\""+resultSet.getString("comment")+"\"},";
            }while(resultSet.next());
        }
        JSON = JSON.substring(0, JSON.length()-1)+"]}]";
        close();
        if(JSON.length() < 40) return "[{\"status\":\"failed\",\"error\":\"No developer found\"}]";
        else return JSON;
    }

    public String retrieve_interviews(int count, int dev_id, String sort) throws SQLException {
        preparedStatement = this.connect
                .prepareStatement("SELECT d.id AS ID, d.email AS Email, pl.name AS ProgrammingLanguage, l.code AS LanguageCode FROM developers d INNER JOIN programming_languages pl ON d.plang_id = pl.plang_id INNER JOIN languages l ON d.lang_id = l.lang_id WHERE d.id = "+dev_id);
        resultSet = preparedStatement.executeQuery();
        resultSet.next();
        String JSON = "[{\"Developer\":[{\"ID\":\""+resultSet.getString("ID")+"\",\"Email\":\""+resultSet.getString("Email")+"\",\"Programming Language\":\""+resultSet.getString("ProgrammingLanguage")+"\",\"Language\":\""+resultSet.getString("LanguageCode")+"\"}],";

        preparedStatement = this.connect
                .prepareStatement("SELECT * FROM interviews WHERE id = "+dev_id+ " ORDER BY score "+sort+" LIMIT "+count);
        resultSet = preparedStatement.executeQuery();

        JSON += "\"Interviews\":[";
        if(!resultSet.next()){
            JSON += "{\"status\":\"failed\",\"error\":\"No Interview found\"},";
        }
        else{
            do{
                JSON += "{\"ID\":\""+resultSet.getString("interview_id")+"\",\"Score\":\""+resultSet.getString("score")+"\",\"Comment\":\""+resultSet.getString("comment")+"\"},";
            }while(resultSet.next());
        }

        JSON = JSON.substring(0, JSON.length()-1)+"]}]";
        close();
        if(JSON.length() < 40) return "[{\"status\":\"failed\",\"error\":\"No developer found\"}]";
        else return JSON;
    }

    public String retrieve_developer(int count) throws SQLException {
        preparedStatement = this.connect
                .prepareStatement("SELECT d.id AS ID, d.email AS Email, pl.name AS ProgrammingLanguage, l.code AS LanguageCode FROM developers d INNER JOIN programming_languages pl ON d.plang_id = pl.plang_id INNER JOIN languages l ON d.lang_id = l.lang_id LIMIT "+count);
        resultSet = preparedStatement.executeQuery();
        String JSON = "[";
        while(resultSet.next()){
            JSON += "{\"ID\":\""+resultSet.getString("ID")+"\",\"Email\":\""+resultSet.getString("Email")+"\",\"Programming Language\":\""+resultSet.getString("ProgrammingLanguage")+"\",\"Language\":\""+resultSet.getString("LanguageCode")+"\"},";
        }
        JSON = JSON.substring(0, JSON.length()-1)+"]";
        close();
        if(JSON.length() < 5) return "[{\"status\":\"failed\",\"error\":\"No developer found\"}]";
        else return JSON;
    }

    public String search_lang_developer(String lang, int count) throws SQLException {
        preparedStatement = this.connect
                .prepareStatement("SELECT d.id AS ID, d.email AS Email, pl.name AS ProgrammingLanguage, l.code AS LanguageCode FROM developers d INNER JOIN programming_languages pl ON d.plang_id = pl.plang_id INNER JOIN languages l ON d.lang_id = l.lang_id WHERE l.code LIKE '%"+lang+"%' LIMIT "+count);
        resultSet = preparedStatement.executeQuery();
        String JSON = "[";
        while(resultSet.next()){
            JSON += "{\"ID\":\""+resultSet.getString("ID")+"\",\"Email\":\""+resultSet.getString("Email")+"\",\"Programming Language\":\""+resultSet.getString("ProgrammingLanguage")+"\",\"Language\":\""+resultSet.getString("LanguageCode")+"\"},";
        }
        JSON = JSON.substring(0, JSON.length()-1)+"]";
        close();
        if(JSON.length() < 5) return "[{\"status\":\"failed\",\"error\":\"No developer found\"}]";
        else return JSON;
    }

    public String search_plang_developer(String plang, int count) throws SQLException {
        preparedStatement = this.connect
                .prepareStatement("SELECT d.id AS ID, d.email AS Email, pl.name AS ProgrammingLanguage, l.code AS LanguageCode FROM developers d INNER JOIN programming_languages pl ON d.plang_id = pl.plang_id INNER JOIN languages l ON d.lang_id = l.lang_id WHERE pl.name LIKE '%"+plang+"%' LIMIT "+count);
        resultSet = preparedStatement.executeQuery();
        String JSON = "[";
        while(resultSet.next()){
            JSON += "{\"ID\":\""+resultSet.getString("ID")+"\",\"Email\":\""+resultSet.getString("Email")+"\",\"Programming Language\":\""+resultSet.getString("ProgrammingLanguage")+"\",\"Language\":\""+resultSet.getString("LanguageCode")+"\"},";
        }
        JSON = JSON.substring(0, JSON.length()-1)+"]";
        close();
        if(JSON.length() < 5) return "[{\"status\":\"failed\",\"error\":\"No developer found\"}]";
        else return JSON;
    }

    public String search_developer(String lang, String plang, int count) throws SQLException {
        preparedStatement = this.connect
                .prepareStatement("SELECT d.id AS ID, d.email AS Email, pl.name AS ProgrammingLanguage, l.code AS LanguageCode FROM developers d INNER JOIN programming_languages pl ON d.plang_id = pl.plang_id INNER JOIN languages l ON d.lang_id = l.lang_id WHERE l.code LIKE '%"+lang+"%' AND pl.name LIKE '%"+plang+"%' LIMIT "+count);
        resultSet = preparedStatement.executeQuery();
        String JSON = "[";
        while(resultSet.next()){
            JSON += "{\"ID\":\""+resultSet.getString("ID")+"\",\"Email\":\""+resultSet.getString("Email")+"\",\"Programming Language\":\""+resultSet.getString("ProgrammingLanguage")+"\",\"Language\":\""+resultSet.getString("LanguageCode")+"\"},";
        }
        JSON = JSON.substring(0, JSON.length()-1)+"]";
        close();
        if(JSON.length() < 5) return "[{\"status\":\"failed\",\"error\":\"No developer found\"}]";
        else return JSON;
    }

    private void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }

            if (connect != null) {
                connect.close();
            }
        } catch (Exception e) {

        }
    }

}