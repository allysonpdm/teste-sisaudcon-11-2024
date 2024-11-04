package Controllers;

import Models.Usuario;
import java.sql.SQLException;
import Controllers.Dtos.UserResponse;
import Controllers.Dtos.UserDto;
import Controllers.Dtos.PaginateResponse;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.EmailUtil;
import java.util.ArrayList;
import java.util.List;

public class UserController {

    public static final String MESSAGE_ERROR_INTERNAL = "Ocorreu um erro inesperado. Por favor, tente novamente.\r\nSe o problema continuar, entre em contato com o suporte.";
    protected Usuario model;
    protected String message;
    protected UserDto userDto = null;

    public UserResponse login(String email, String password) {
        try {
            message = "Usuário ou senha inválidos";
            Usuario user = model.findByEmailAndPassword(email, password);

            if (user != null) {
                message = "Login bem-sucedido";
                userDto = new UserDto(user);
            }
        } catch (SQLException e) {
            message = "Banco de dados indisponível. Por favor, tente novamente mais tarde.";
        } catch (Exception e) {
            message = MESSAGE_ERROR_INTERNAL;
            //e.printStackTrace();
        }

        return new UserResponse(userDto, message);
    }

    public UserResponse create(String name, String email, String password) {
        try {
            String validationMessage = validations(name, email, password);
            if (validationMessage != null) {
                return new UserResponse(null, validationMessage);
            }
            model = new Usuario(name, email, password);
            Long id = model.insertAndReturnId();
            userDto = new UserDto(id, model.getName(), model.getEmail(), null, null);
            return new UserResponse(userDto, "Novo usuário criado com sucesso.");
        } catch (SQLException e) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            message = MESSAGE_ERROR_INTERNAL;
        }
        return new UserResponse(userDto, message);
    }

    public UserResponse update(Long id, String name, String email, String password) {
        try {
            String validationMessage = validations(id, name, email, password);
            if (validationMessage != null) {
                return new UserResponse(null, validationMessage);
            }

            Usuario usuario = Usuario.findById(id);
            if (usuario == null) {
                return new UserResponse(null, "Usuário não encontrado.");
            }
            
            usuario.setName(name);
            usuario.setEmail(email);
            usuario.setPassword(password);

            boolean updated = usuario.update();
            if (updated) {
                userDto = new UserDto(usuario.getId(), usuario.getName(), usuario.getEmail(), null, null);
                return new UserResponse(userDto, "Usuário atualizado com sucesso.");
            } else {
                message = "Falha ao atualizar o usuário.";
            }
        } catch (SQLException e) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            message = MESSAGE_ERROR_INTERNAL;
        }
        return new UserResponse(null, message);
    }

    public PaginateResponse listPaginate(String search, int page, int perPage) {
        List<UserDto> userDtos = new ArrayList<>();
        int total = 0;
        try {
            int offset = (page - 1) * perPage;

            List<Usuario> users = (search == null || search.isEmpty())
                    ? Usuario.findPaginated(perPage, offset)
                    : Usuario.findPaginated(search, perPage, offset);

            for (Usuario user : users) {
                userDtos.add(new UserDto(user));
            }

            total = (search == null || search.isEmpty())
                    ? Usuario.getTotalCount()
                    : Usuario.getTotalCountBySearch(search);
        } catch (SQLException e) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            message = MESSAGE_ERROR_INTERNAL;
        }

        return new PaginateResponse(userDtos, total, page, message);
    }

    protected String validations(Long id, String name, String email, String password) throws SQLException {
        
        String nameMessage = nameValidate(name);
        if (nameMessage != null) {
            return nameMessage;
        }
        
        String passwordMessage = passwordValidate(password);
        if (passwordMessage != null) {
            return passwordMessage;
        }

        String emailValidationMessage = emailValidate(email, id);
        if (emailValidationMessage != null) {
            return emailValidationMessage;
        }

        return null;
    }
    
    protected String validations(String name, String email, String password) throws SQLException {
        return validations(null, name, email, password);
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

    protected String emailValidate(String email, Long ignoreId) throws SQLException {
        Integer maxSize = 160;
        if (!EmailUtil.isEmail(email)) {
            return "O email fornecido é inválido.";
        }

        if (email.length() > maxSize) {
            return "O email não pode ter mais de " + maxSize + " caracteres.";
        }

        if (model.emailExists(email, ignoreId)) {
            return "Este e-mail já está em uso. Escolha outro.";
        }

        return null;
    }

    protected String passwordValidate(String password) {
        if (password == null || password.isEmpty()) {
            return "A senha não pode ser vazia.";
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            return model.delete(id);
        } catch (SQLException e) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            message = MESSAGE_ERROR_INTERNAL;
        }

        return false;
    }
}
