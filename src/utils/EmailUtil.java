package utils;

public class EmailUtil {

    private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

    public static boolean isEmail(String email) {
        return email.matches(EMAIL_REGEX);
    }

    public static String normalizeEmail(String email) {
        if (email != null) {
            return email.trim().toLowerCase();
        }
        return null;
    }

    public static String getDomain(String email) {
        if (isEmail(email)) {
            return email.substring(email.indexOf("@") + 1);
        }
        return null;
    }

}
