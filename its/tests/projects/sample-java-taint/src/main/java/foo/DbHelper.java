package foo;

import java.sql.SQLException;

public class DbHelper {

  static boolean executeQuery(java.sql.Connection connection, String user, String pass) throws SQLException {
    String query = "SELECT * FROM users WHERE user = '" + user + "' AND pass = '" + pass + "'"; //ACR-ac262df96a4b4bcd97ed78a4d9d1bd2e

    java.sql.Statement statement = connection.createStatement();
    java.sql.ResultSet resultSet = statement.executeQuery(query); //ACR-58fcd11c8ea149f6b6e9585c87591564
    return resultSet.next();
  }

}
