package vehicleapp.database;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class AuthService {


    private static String lastErrorMessage = "Unknown error";

    public static String getLastErrorMessage() {
        return lastErrorMessage;
    }


    public static boolean login(String email, String password) {
        try {
            JsonObject loginBody = new JsonObject();
            loginBody.addProperty("email", email);
            loginBody.addProperty("password", password);

            String jsonResponse = ApiClient.sendPost("/auth/login", loginBody);
            System.out.println("🔎 /auth/login raw response: " + jsonResponse);
            Gson gson = new Gson();
            JsonObject responseObj = gson.fromJson(jsonResponse, JsonObject.class);


            if (responseObj.has("user")) {
                JsonObject userObj = responseObj.getAsJsonObject("user");
                int userId = userObj.get("user_id").getAsInt();
                String username = userObj.get("username").getAsString();
                String userType = userObj.get("user_type").getAsString();
                UserSession.setSession(userId, username, userType);
                return true;
            }


            lastErrorMessage = responseObj.has("message")
                    ? responseObj.get("message").getAsString()
                    : "Login failed: " + jsonResponse;
            return false;
        } catch (Exception e) {
            lastErrorMessage = "Could not reach server: " + e.getMessage();
            e.printStackTrace();
            return false;
        }
    }

    //
    public static boolean register(String username, String email, String password, String userType) {
        try {

            JsonObject registerBody = new JsonObject();
            registerBody.addProperty("username", username);
            registerBody.addProperty("email", email);
            registerBody.addProperty("password", password);
            registerBody.addProperty("user_type", userType);


            String jsonResponse = ApiClient.sendPost("/auth/register", registerBody);

            Gson gson = new Gson();
            JsonObject responseObj = gson.fromJson(jsonResponse, JsonObject.class);


            if (responseObj.has("message") &&
                    responseObj.get("message").getAsString().toLowerCase().contains("successfully")) {
                return true;
            }

            lastErrorMessage = responseObj.has("message")
                    ? responseObj.get("message").getAsString()
                    : "Registration failed: " + jsonResponse;
            return false;
        } catch (Exception e) {
            lastErrorMessage = "Could not reach server: " + e.getMessage();
            e.printStackTrace();
            return false;
        }
    }

    // 🚪
    public static void logout() {
        UserSession.clearSession();
    }
}