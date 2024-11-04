package utils;

public class DateUtil {
    public static java.sql.Date toSqlDate(java.util.Date date) {
        return (date == null) ? null : new java.sql.Date(date.getTime());
    }
}
