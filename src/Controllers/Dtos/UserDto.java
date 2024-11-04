package Controllers.Dtos;

import Models.Usuario;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class UserDto {
    private Long id;
    private String name;
    private String email;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public UserDto(Long id, String name, String email, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UserDto(Usuario user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCreatedAt() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy 'às' H:mm:ss");
        return dateFormat.format(createdAt);
    }

    public String getUpdatedAt() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy 'às' H:mm:ss");
        return dateFormat.format(updatedAt);
    }

    public boolean isAuth() {
        return id != null;
    }
}
