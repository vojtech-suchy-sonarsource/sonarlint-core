package foo;

import java.sql.SQLException;

public class DbHelper {

  static boolean executeQuery(java.sql.Connection connection, String user, String pass) throws SQLException {
    String query = "SELECT 1 FROM users WHERE user = ? AND pass = ?"; // Unsafe

    try (java.sql.PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setString(1, user);
      statement.setString(2, pass);
      java.sql.ResultSet resultSet = statement.executeQuery(); // Noncompliant
      return resultSet.next();
    }
  }

}
