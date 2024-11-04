package database;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataAccessObject {
    
    private Connection conn;

    public ResultSet executeQuery(String query, Object... params) throws SQLException {
        conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        setParameters(stmt, params);
        return stmt.executeQuery();
    }

    public int save(String query, Object... params) throws SQLException {
        conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        setParameters(stmt, params);
        int rowsAffected = stmt.executeUpdate();
        stmt.close();
        conn.close();
        return rowsAffected;
    }
    
    public ResultSet executeInsert(String query, Object... params) throws SQLException {
        conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        setParameters(stmt, params);
        stmt.executeUpdate();
        return stmt.getGeneratedKeys();
    }

    private void setParameters(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }
    
    public void closeConnection() throws SQLException
    {
        conn.close();
    }
    
}
