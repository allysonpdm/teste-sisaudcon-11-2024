package Models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import database.DataAccessObject;
import utils.DateUtil;

public class Funcionario {

    private Long id;
    private Long userId;
    private String name;
    private Date hireDate;
    private double salary;
    private boolean status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Funcionario(Long id, Long userId, String name, Date hireDate, double salary, boolean status, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.hireDate = hireDate;
        this.salary = salary;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Funcionario(Long userId, String name, Date hireDate, double salary, boolean status) {
        this.userId = userId;
        this.name = name;
        this.hireDate = hireDate;
        this.salary = salary;
        this.status = status;
    }

    public Funcionario(Long userId, String name, java.util.Date hireDate, double salary, boolean status) {
        this.userId = userId;
        this.name = name;
        this.hireDate = DateUtil.toSqlDate(hireDate);
        this.salary = salary;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    public void setHireDate(java.util.Date hireDate) {
        this.hireDate = DateUtil.toSqlDate(hireDate);
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public static Funcionario findById(Long id) throws SQLException {
        DataAccessObject dbHelper = new DataAccessObject();
        String query = "SELECT * FROM funcionarios WHERE id = ?";
        ResultSet rs = dbHelper.executeQuery(query, id);

        if (rs.next()) {
            return new Funcionario(
                    rs.getLong("id"),
                    rs.getLong("user_id"),
                    rs.getString("name"),
                    rs.getDate("hire_date"),
                    rs.getDouble("salary"),
                    rs.getBoolean("status"),
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
        String query = "INSERT INTO funcionarios (user_id, name, hire_date, salary, status) VALUES (?, ?, ?, ?, ?)";
        int rowsAffected = dbHelper.save(query, this.userId, this.name, this.hireDate, this.salary, this.status);
        dbHelper.closeConnection();
        return rowsAffected > 0;
    }

    public Long insertAndReturnId() throws SQLException {
        DataAccessObject dbHelper = new DataAccessObject();
        String query = "INSERT INTO funcionarios (user_id, name, hire_date, salary, status) VALUES (?, ?, ?, ?, ?) RETURNING id";
        ResultSet rs = dbHelper.executeInsert(query, this.userId, this.name, this.hireDate, this.salary, this.status);

        if (rs.next()) {
            this.id = rs.getLong(1);
            rs.close();
            dbHelper.closeConnection();
            return this.id;
        }
        dbHelper.closeConnection();
        return null;
    }

    public static List<Funcionario> findPaginated(Long userId, String search, int pageSize, int offset) throws SQLException {
        DataAccessObject dbHelper = new DataAccessObject();
        StringBuilder query = new StringBuilder("SELECT * FROM funcionarios WHERE user_id = ?");
        List<Object> parameters = new ArrayList<>();
        parameters.add(userId);

        buildSearchQuery(search, query, parameters);

        query.append(" ORDER BY id LIMIT ? OFFSET ?");
        parameters.add(pageSize);
        parameters.add(offset);

        ResultSet rs = dbHelper.executeQuery(query.toString(), parameters.toArray());

        List<Funcionario> funcionarios = new ArrayList<>();
        while (rs.next()) {
            funcionarios.add(new Funcionario(
                    rs.getLong("id"),
                    rs.getLong("user_id"),
                    rs.getString("name"),
                    rs.getDate("hire_date"),
                    rs.getDouble("salary"),
                    rs.getBoolean("status"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at")
            ));
        }
        rs.close();
        dbHelper.closeConnection();
        return funcionarios;
    }
    
    public static List<Funcionario> findPaginated(Long userId, int pageSize, int offset) throws SQLException {
       DataAccessObject dbHelper = new DataAccessObject();
        StringBuilder query = new StringBuilder("SELECT * FROM funcionarios WHERE user_id = ?");
        List<Object> parameters = new ArrayList<>();
        parameters.add(userId);

        buildSearchQuery(null, query, parameters);

        query.append(" ORDER BY id LIMIT ? OFFSET ?");
        parameters.add(pageSize);
        parameters.add(offset);

        ResultSet rs = dbHelper.executeQuery(query.toString(), parameters.toArray());

        List<Funcionario> funcionarios = new ArrayList<>();
        while (rs.next()) {
            funcionarios.add(new Funcionario(
                    rs.getLong("id"),
                    rs.getLong("user_id"),
                    rs.getString("name"),
                    rs.getDate("hire_date"),
                    rs.getDouble("salary"),
                    rs.getBoolean("status"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at")
            ));
        }
        rs.close();
        dbHelper.closeConnection();
        return funcionarios;
    }

    public static int getTotalCountBySearch(Long userId, String search) throws SQLException {
        DataAccessObject dbHelper = new DataAccessObject();
        StringBuilder query = new StringBuilder("SELECT COUNT(*) FROM funcionarios WHERE user_id = ?");
        List<Object> parameters = new ArrayList<>();
        parameters.add(userId);

        buildSearchQuery(search, query, parameters);

        ResultSet rs = dbHelper.executeQuery(query.toString(), parameters.toArray());
        int totalCount = rs.next() ? rs.getInt(1) : 0;
        rs.close();
        dbHelper.closeConnection();
        return totalCount;
    }

    private static void buildSearchQuery(String search, StringBuilder query, List<Object> parameters) {
        if (search != null && !search.trim().isEmpty()) {
            try {
                double salary = Double.parseDouble(search);
                query.append(" AND salary = ?");
                parameters.add(salary);
            } catch (NumberFormatException e) {
                try {
                    Date hireDate = Date.valueOf(search);
                    query.append(" AND hire_date = ?");
                    parameters.add(hireDate);
                } catch (IllegalArgumentException ex) {
                    query.append(" AND name ILIKE ?");
                    parameters.add("%" + search + "%");
                }
            }
        }
    }

    public static boolean delete(Long id) throws SQLException {
        DataAccessObject dbHelper = new DataAccessObject();
        String query = "DELETE FROM funcionarios WHERE id = ?";
        int rowsAffected = dbHelper.save(query, id);
        dbHelper.closeConnection();
        return rowsAffected > 0;
    }

    public boolean update() throws SQLException {
        DataAccessObject dbHelper = new DataAccessObject();
        String query = "UPDATE funcionarios SET name = ?, hire_date = ?, salary = ?, status = ?, updated_at = NOW() WHERE id = ?";
        int rowsAffected = dbHelper.save(query, this.name, this.hireDate, this.salary, this.status, this.id);
        dbHelper.closeConnection();
        return rowsAffected > 0;
    }

    public static boolean belongsToUser(Long funcionarioId, Long userId) throws SQLException {
        DataAccessObject dbHelper = new DataAccessObject();
        String query = "SELECT COUNT(*) FROM funcionarios WHERE id = ? AND user_id = ?";
        ResultSet rs = dbHelper.executeQuery(query, funcionarioId, userId);

        boolean belongsToUser = rs.next() && rs.getInt(1) > 0;
        rs.close();
        dbHelper.closeConnection();
        return belongsToUser;
    }
}
