package Controllers.Dtos;

public class UserResponse {

    private UserDto user;
    private String message;

    public UserResponse(UserDto user, String message) {
        this.user = user;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public UserDto getUser() {
        return user;
    }

}
