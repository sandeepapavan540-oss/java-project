package vehicleapp.database;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import vehicleapp.model.Vehicle;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.util.Timeout;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:5000/api";


    private static final RequestConfig REQUEST_CONFIG = RequestConfig.custom()
            .setConnectionRequestTimeout(Timeout.ofSeconds(5))
            .setResponseTimeout(Timeout.ofSeconds(15))
            .build();

    private static final PoolingHttpClientConnectionManager CONNECTION_MANAGER =
            new PoolingHttpClientConnectionManager();

    static {
        CONNECTION_MANAGER.setMaxTotal(20);
        CONNECTION_MANAGER.setDefaultMaxPerRoute(10);
    }

    private static final CloseableHttpClient HTTP_CLIENT = HttpClients.custom()
            .setConnectionManager(CONNECTION_MANAGER)
            .setDefaultRequestConfig(REQUEST_CONFIG)
            .build();


    public static String sendPost(String endpoint, JsonObject jsonBody) throws Exception {
        HttpPost httpPost = new HttpPost(BASE_URL + endpoint);
        StringEntity entity = new StringEntity(jsonBody.toString(), ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);

        try (CloseableHttpResponse response = HTTP_CLIENT.execute(httpPost)) {
            return EntityUtils.toString(response.getEntity());
        }
    }

    public static String sendGet(String endpoint) throws Exception {
        HttpGet httpGet = new HttpGet(BASE_URL + endpoint);

        try (CloseableHttpResponse response = HTTP_CLIENT.execute(httpGet)) {
            return EntityUtils.toString(response.getEntity());
        }
    }


    public static String sendPut(String endpoint) throws Exception {
        HttpPut httpPut = new HttpPut(BASE_URL + endpoint);

        try (CloseableHttpResponse response = HTTP_CLIENT.execute(httpPut)) {
            return EntityUtils.toString(response.getEntity());
        }
    }


    public static String sendMultipart(String endpoint, Map<String, String> textFields, List<File> imageFiles) throws Exception {
        HttpPost httpPost = new HttpPost(BASE_URL + endpoint);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        if (textFields != null) {
            for (Map.Entry<String, String> field : textFields.entrySet()) {
                builder.addTextBody(field.getKey(), field.getValue(), ContentType.TEXT_PLAIN);
            }
        }

        if (imageFiles != null) {
            for (File imageFile : imageFiles) {
                if (imageFile != null && imageFile.exists()) {

                    builder.addBinaryBody("images", imageFile, resolveImageContentType(imageFile), imageFile.getName());
                }
            }
        }

        httpPost.setEntity(builder.build());

        try (CloseableHttpResponse response = HTTP_CLIENT.execute(httpPost)) {
            return EntityUtils.toString(response.getEntity());
        }
    }


    private static ContentType resolveImageContentType(File file) {
        String name = file.getName().toLowerCase();
        if (name.endsWith(".png")) return ContentType.create("image/png");
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return ContentType.create("image/jpeg");
        if (name.endsWith(".webp")) return ContentType.create("image/webp");
        return ContentType.DEFAULT_BINARY;
    }


    public static List<Vehicle> getAllVehicles() {
        try {
            String jsonResponse = sendGet("/vehicles/available");

            Gson gson = new Gson();
            Type vehicleListType = new TypeToken<ArrayList<Vehicle>>(){}.getType();

            return gson.fromJson(jsonResponse, vehicleListType);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}