package Models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Timestamp;
import java.util.List;
import database.DataAccessObject;
import utils.HashUtil;
import utils.StringUtil;

public class Usuario {

    private Long id;
    private String name;
    private String email;
    private String password;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Usuario(Long id, String name, String email, String password, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        setName(name);
        setEmail(email);
        setPassword(password);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Usuario(String name, String email, String password) {
        this.name = name;
        setEmail(email);
        setPassword(password);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = StringUtil.upperCaseFirst(name);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email.trim().toLowerCase();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = HashUtil.sha256(password);
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static Usuario findByEmailAndPassword(String email, String password) throws SQLException {
        String hashedPassword = HashUtil.sha256(password);
        DataAccessObject dbHelper = new DataAccessObject();
        String query = "SELECT * FROM usuarios WHERE email = ? AND password = ?";
        ResultSet rs = dbHelper.executeQuery(query, email, hashedPassword);
        if (rs.next()) {
            return new Usuario(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at")
            );
        }
        rs.close();
        dbHelper.closeConnection();

        return null;
    }

    public boolean insert() throws SQLException {
        DataAccessObject dbHelper = new DataAccessObject();
        String query = "INSERT INTO usuarios (name, email, password) VALUES (?, ?, ?)";
        int rowsAffected = dbHelper.save(query, this.name, this.email, this.password);
        return rowsAffected > 0;
    }

    public Long insertAndReturnId() throws SQLException {
        DataAccessObject dbHelper = new DataAccessObject();
        String query = "INSERT INTO usuarios (name, email, password) VALUES (?, ?, ?) RETURNING id";
        ResultSet rs = dbHelper.executeInsert(query, this.name, this.email, this.password);

        if (rs.next()) {
            this.id = rs.getLong(1);
            return this.id;
        }
        rs.close();
        dbHelper.closeConnection();

        return null;
    }

    public static boolean emailExists(String email, Long ignoreId) throws SQLException {
        DataAccessObject dbHelper = new DataAccessObject();

        // Modifica a consulta para ignorar o ID especificado, se fornecido
        String query = "SELECT COUNT(*) FROM usuarios WHERE email = ?";
        if (ignoreId != null) {
            query += " AND id != ?";
        }

        ResultSet rs;
        rs = (ignoreId != null)
                ? dbHelper.executeQuery(query, email, ignoreId)
                : dbHelper.executeQuery(query, email);

        boolean exists = false;
        if (rs.next()) {
            exists = rs.getInt(1) > 0;
        }

        rs.close();
        dbHelper.closeConnection();

        return exists;
    }

    public static List<Usuario> findPaginated(int pageSize, int offset) throws SQLException {
        DataAccessObject dbHelper = new DataAccessObject();
        String query = "SELECT * FROM usuarios ORDER BY id LIMIT ? OFFSET ?";
        ResultSet rs = dbHelper.executeQuery(query, pageSize, offset);

        List<Usuario> users = new ArrayList<>();
        while (rs.next()) {
            users.add(new Usuario(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at")
            ));
        }
        rs.close();
        dbHelper.closeConnection();

        return users;
    }

    public static List<Usuario> findPaginated(String search, int pageSize, int offset) throws SQLException {
        DataAccessObject dbHelper = new DataAccessObject();
        String query;
        ResultSet rs;

        if (search == null || search.trim().isEmpty()) {
            query = "SELECT * FROM usuarios ORDER BY id LIMIT ? OFFSET ?";
            rs = dbHelper.executeQuery(query, pageSize, offset);
        } else {
            Long numericId = convertToLong(search);

            if (numericId != null) {
                query = "SELECT * FROM usuarios WHERE id = ? OR name ILIKE ? OR email ILIKE ? ORDER BY id LIMIT ? OFFSET ?";
                rs = dbHelper.executeQuery(query, numericId, "%" + search + "%", "%" + search + "%", pageSize, offset);
            } else {
                query = "SELECT * FROM usuarios WHERE name ILIKE ? OR email ILIKE ? ORDER BY id LIMIT ? OFFSET ?";
                rs = dbHelper.executeQuery(query, "%" + search + "%", "%" + search + "%", pageSize, offset);
            }
        }

        List<Usuario> users = new ArrayList<>();
        while (rs.next()) {
            users.add(new Usuario(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at")
            ));
        }

        rs.close();
        dbHelper.closeConnection();

        return users;
    }

    public static int getTotalCount() throws SQLException {
        DataAccessObject dbHelper = new DataAccessObject();
        String query = "SELECT COUNT(*) FROM usuarios";
        ResultSet rs = dbHelper.executeQuery(query);
        if (rs.next()) {
            return rs.getInt(1);
        }
        rs.close();
        dbHelper.closeConnection();

        return 0;
    }

    public static int getTotalCountBySearch(String search) throws SQLException {
        DataAccessObject dbHelper = new DataAccessObject();
        Long numericId = convertToLong(search);
        String query;

        ResultSet rs;
        if (numericId == null) {
            query = "SELECT COUNT(*) FROM usuarios WHERE name ILIKE ? OR email ILIKE ?";
            rs = dbHelper.executeQuery(query, "%" + search + "%", "%" + search + "%");
        } else {
            query = "SELECT COUNT(*) FROM usuarios WHERE id = ? OR name ILIKE ? OR email ILIKE ?";
            rs = dbHelper.executeQuery(query, numericId, "%" + search + "%", "%" + search + "%");
        }

        int totalCount = 0;
        if (rs.next()) {
            totalCount = rs.getInt(1);
        }
        rs.close();
        dbHelper.closeConnection();

        return totalCount;
    }

    private static Long convertToLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static boolean delete(Long id) throws SQLException {
        DataAccessObject dbHelper = new DataAccessObject();
        String query = "DELETE FROM usuarios WHERE id = ?";
        int rowsAffected = dbHelper.save(query, id);
        return rowsAffected > 0;
    }

    public boolean update() throws SQLException {
        DataAccessObject dbHelper = new DataAccessObject();
        String query = "UPDATE usuarios SET name = ?, email = ?, password = ?, updated_at = NOW() WHERE id = ?";
        int rowsAffected = dbHelper.save(query, this.name, this.email, this.password, this.id);
        dbHelper.closeConnection();
        return rowsAffected > 0;
    }

    public static Usuario findById(Long id) throws SQLException {
        DataAccessObject dbHelper = new DataAccessObject();
        String query = "SELECT * FROM usuarios WHERE id = ?";
        ResultSet rs = dbHelper.executeQuery(query, id);

        Usuario usuario = null;
        if (rs.next()) {
            usuario = new Usuario(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at")
            );
        }

        rs.close();
        dbHelper.closeConnection();

        return usuario;
    }
}
