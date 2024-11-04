package Controllers.Dtos;

import Models.Funcionario;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EmployeeDto {

    private Long id;
    private Long userId;
    private String name;
    private Date hireDate;
    private double salary;
    private boolean status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public EmployeeDto(
            Long id,
            Long userId,
            String name,
            Date hireDate,
            double salary,
            boolean status,
            Timestamp createdAt,
            Timestamp updatedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.hireDate = hireDate;
        this.salary = salary;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public EmployeeDto(Funcionario employee) {
        this.id = employee.getId();
        this.userId = employee.getUserId();
        this.name = employee.getName();
        this.hireDate = employee.getHireDate();
        this.salary = employee.getSalary();
        this.status = employee.getStatus();
        this.createdAt = employee.getCreatedAt();
        this.updatedAt = employee.getUpdatedAt();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public double getSalary() {
        return salary;
    }

    public boolean getStatus() {
        return status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public String getFormattedCreatedAt() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("d/MM/yyyy 'às' H:mm:ss");
        return dateFormat.format(createdAt);
    }

    public String getFormattedUpdatedAt() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy 'às' H:mm:ss");
        return dateFormat.format(updatedAt);
    }

    public String getFormattedHireDate() {
        if (hireDate == null) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(hireDate);
    }

    public String getFormattedSalary() {
        NumberFormat decimalFormat = NumberFormat.getInstance(new Locale("pt", "BR"));
        decimalFormat.setMaximumFractionDigits(2);
        decimalFormat.setMinimumFractionDigits(2);
        return decimalFormat.format(salary);
    }
    
    public String getFormmattedStatus() {
        return status
                ? "Ativo"
                : "Desativado";
    }
}
