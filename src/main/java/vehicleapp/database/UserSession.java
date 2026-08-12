package vehicleapp.database;

public class UserSession {
    private static int userId;
    private static String username;
    private static String userType;

    public static void setSession(int id, String name, String type) {
        userId = id;
        username = name;
        userType = type;
    }

    public static int getUserId() { return userId; }
    public static String getUsername() { return username; }
    public static String getUserType() { return userType; }

    public static void clearSession() {
        userId = 0;
        username = null;
        userType = null;
    }
}