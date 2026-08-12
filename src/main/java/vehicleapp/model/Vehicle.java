package vehicleapp.model;

import java.util.List;

public class Vehicle {
    private int vehicle_id;
    private String brand;
    private String model;
    private double price;
    private String vehicle_type;
    private String status;
    private List<String> images;       // Backend එකෙන් JSON array එකක් විදිහට එනවා
    private String seller_name;        // Backend එකේ JOIN එකෙන් එන seller username එක

    // Default Constructor
    public Vehicle() {}

    // Getters සහ Setters
    public int getVehicleId() { return vehicle_id; }
    public void setVehicleId(int vehicle_id) { this.vehicle_id = vehicle_id; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getVehicleType() { return vehicle_type; }
    public void setVehicleType(String vehicle_type) { this.vehicle_type = vehicle_type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }

    public String getSellerName() { return seller_name; }
    public void setSellerName(String seller_name) { this.seller_name = seller_name; }
}