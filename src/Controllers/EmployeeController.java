package Controllers;

import Models.Funcionario;
import java.sql.SQLException;
import Controllers.Dtos.EmployeeResponse;
import Controllers.Dtos.EmployeeDto;
import Controllers.Dtos.PaginateResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;

public class EmployeeController {

    public static final String MESSAGE_ERROR_INTERNAL = "Ocorreu um erro inesperado. Por favor, tente novamente.\r\nSe o problema continuar, entre em contato com o suporte.";
    protected Funcionario model;
    protected String message;
    protected EmployeeDto employeeDto = null;

    public EmployeeResponse create(Long userId, String name, Date hireDate, Double salary, boolean status) {

        try {
            String validationMessage = validations(userId, name, hireDate, salary, status);
            if (validationMessage != null) {
                return new EmployeeResponse(null, validationMessage);
            }
            model = new Funcionario(userId, name, hireDate, salary, status);
            Long id = model.insertAndReturnId();
            employeeDto = new EmployeeDto(
                    id,
                    model.getUserId(),
                    model.getName(),
                    model.getHireDate(),
                    model.getSalary(),
                    model.getStatus(),
                    null,
                    null
            );
            return new EmployeeResponse(employeeDto, "Novo funcionário criado com sucesso.");
        } catch (SQLException e) {
            Logger.getLogger(EmployeeController.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            message = MESSAGE_ERROR_INTERNAL;
        }
        return new EmployeeResponse(employeeDto, message);
    }

    public EmployeeResponse update(Long id, Long userId, String name, Date hireDate, Double salary, boolean status) {
        try {
            String validationMessage = validations(null, name, hireDate, salary, status);
            if (validationMessage != null) {
                return new EmployeeResponse(null, validationMessage);
            }

            Funcionario employee = Funcionario.findById(id);
            if (employee == null) {
                return new EmployeeResponse(null, "Funcionário não encontrado.");
            }

            employee.setName(name);
            employee.setHireDate(hireDate);
            employee.setSalary(salary);
            employee.setStatus(status);

            boolean updated = employee.update();
            if (updated) {
                employeeDto = new EmployeeDto(
                        employee.getId(),
                        employee.getUserId(),
                        employee.getName(),
                        employee.getHireDate(),
                        employee.getSalary(),
                        employee.getStatus(),
                        employee.getCreatedAt(),
                        employee.getUpdatedAt()
                );
                return new EmployeeResponse(employeeDto, "Funcionário atualizado com sucesso.");
            } else {
                message = "Falha ao atualizar o funcionário.";
            }
        } catch (SQLException e) {
            Logger.getLogger(EmployeeController.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            message = MESSAGE_ERROR_INTERNAL;
        }
        return new EmployeeResponse(null, message);
    }

    protected String validations(Long id, Long userId, String name, Date hireDate, Double salary, boolean status) throws SQLException {

        String nameMessage = nameValidate(name);
        if (nameMessage != null) {
            return nameMessage;
        }

        if (id != null && !Funcionario.belongsToUser(id, userId)) {
            return "O funcionário não pertence ao usuário especificado.";
        }

        // Validação do salário
        String salaryMessage = salaryValidate(salary);
        if (salaryMessage != null) {
            return salaryMessage;
        }

        // Validação da data de contratação
        String hireDateMessage = hireDateValidate(hireDate);
        if (hireDateMessage != null) {
            return hireDateMessage;
        }

        return null;
    }

    protected String validations(Long userId, String name, Date hireDate, Double salary, boolean status) throws SQLException {
        return validations(null, userId, name, hireDate, salary, status);
    }

    protected String nameValidate(String name) {
        Integer maxSize = 50;
        if (name == null || name.isEmpty()) {
            return "O nome não pode ser vazio.";
        }

        if (name.length() > maxSize) {
            return "O nome não pode ter mais de " + maxSize + " caracteres.";
        }
        return null;
    }

    protected String salaryValidate(Double salary) {
        if (salary == null) {
            return "O salário é obrigatório.";
        }

        if (salary < 0 || salary >= 9999999.999) {
            return "O salário deve estar entre R$ 0,01 e R$ 9.999.999,99.";
        }

        String[] parts = salary.toString().split("\\.");
        if (parts.length > 1 && parts[1].length() > 3) {
            return "O salário não pode ter mais de 3 casas decimais.";
        }

        return null;
    }

    protected String hireDateValidate(Date hireDate) {
        if (hireDate == null) {
            return "A data de contratação é obrigatória.";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);

        try {
            sdf.parse(sdf.format(hireDate));
        } catch (ParseException e) {
            return "A data de admissão deve estar no formato dd/MM/yyyy.";
        }

        return null;
    }

    public boolean delete(Long id) {
        try {
            return Funcionario.delete(id);
        } catch (SQLException e) {
            Logger.getLogger(EmployeeController.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            message = MESSAGE_ERROR_INTERNAL;
        }
        return false;
    }

    public PaginateResponse listPaginate(String search, Long userId, int page, int perPage) {
        List<EmployeeDto> employeeDtos = new ArrayList<>();
        int total = 0;

        try {
            int offset = (page - 1) * perPage;

            List<Funcionario> employees = Funcionario.findPaginated(userId, search, perPage, offset);

            for (Funcionario employee : employees) {
                employeeDtos.add(new EmployeeDto(
                        employee.getId(),
                        employee.getUserId(),
                        employee.getName(),
                        employee.getHireDate(),
                        employee.getSalary(),
                        employee.getStatus(),
                        employee.getCreatedAt(),
                        employee.getUpdatedAt()
                ));
            }

            total = Funcionario.getTotalCountBySearch(userId, search);

        } catch (SQLException e) {
            Logger.getLogger(EmployeeController.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            message = MESSAGE_ERROR_INTERNAL;
        }

        return new PaginateResponse(employeeDtos, total, page, message);
    }
}
