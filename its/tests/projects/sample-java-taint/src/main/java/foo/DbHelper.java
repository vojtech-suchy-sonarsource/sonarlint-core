package foo;

import java.sql.SQLException;

public class DbHelper {

  static boolean executeQuery(java.sql.Connection connection, String user, String pass) throws SQLException {
    String query = "SELECT * FROM users WHERE user = '" + user + "' AND pass = '" + pass + "'"; //ACR-030670f265154899b7b00d85b16ca72e

    java.sql.Statement statement = connection.createStatement();
    java.sql.ResultSet resultSet = statement.executeQuery(query); //ACR-3632a586100946eeba8f0fae164817e7
    return resultSet.next();
  }

}
