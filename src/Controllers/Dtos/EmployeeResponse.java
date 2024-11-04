package Controllers.Dtos;

public class EmployeeResponse {

    private EmployeeDto employee;
    private String message;

    public EmployeeResponse(EmployeeDto employee, String message) {
        this.employee = employee;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public EmployeeDto getEmployee() {
        return employee;
    }

}
