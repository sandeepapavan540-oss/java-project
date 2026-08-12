package vehicleapp.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import vehicleapp.database.ApiClient;
import vehicleapp.database.UserSession;
import vehicleapp.model.Vehicle;

public class CustomerVehicleView extends JFrame {

    private JPanel gridPanel;
    private JTextField txtSearch;
    private JComboBox<String> cmbFilterType;
    private List<VehicleData> vehicleList;


    public CustomerVehicleView() {
        this("");
    }


    public CustomerVehicleView(String initialSearchKeyword) {
        setTitle("Vehicle Showroom - Available Vehicles");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1000, 700));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);


        BackgroundPanel mainBackground = new BackgroundPanel("/login_bg.png");
        mainBackground.setLayout(new BorderLayout(15, 15));
        mainBackground.setBorder(new EmptyBorder(20, 30, 20, 30));
        setContentPane(mainBackground);


        JPanel topBar = new JPanel(new BorderLayout(15, 0));
        topBar.setOpaque(false);

        JLabel lblTitle = createGradientTitle("VEHICLE SHOWROOM");
        topBar.add(lblTitle, BorderLayout.WEST);


        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setOpaque(false);

        txtSearch = new JTextField(12);
        styleTextField(txtSearch);
        txtSearch.setToolTipText("Search Brand, Model, or Number...");

        cmbFilterType = new JComboBox<>(new String[]{"All Types", "Car", "Van", "SUV", "Bike", "Lorry", "Other"});
        cmbFilterType.setPreferredSize(new Dimension(120, 35));
        cmbFilterType.setBackground(new Color(20, 28, 40));
        cmbFilterType.setForeground(Color.WHITE);

        JButton btnSearch = create3DButton("🔍 Search", new Color(52, 152, 219));
        btnSearch.setPreferredSize(new Dimension(100, 35));

        JButton btnRefresh = create3DButton("🔄 Refresh", new Color(46, 204, 113));
        btnRefresh.setPreferredSize(new Dimension(100, 35));

        JButton btnBack = create3DButton("⬅️ Back", new Color(231, 76, 60));
        btnBack.setPreferredSize(new Dimension(90, 35));

        btnSearch.addActionListener(e -> fetchVehiclesFromDB());
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            cmbFilterType.setSelectedIndex(0);
            fetchVehiclesFromDB();
        });
        btnBack.addActionListener(e -> {
            new VehicleForm().setVisible(true);
            dispose();
        });

        filterPanel.add(txtSearch);
        filterPanel.add(cmbFilterType);
        filterPanel.add(btnSearch);
        filterPanel.add(btnRefresh);
        filterPanel.add(btnBack);

        topBar.add(filterPanel, BorderLayout.EAST);
        mainBackground.add(topBar, BorderLayout.NORTH);


        gridPanel = new JPanel(new GridLayout(0, 3, 20, 20)); // Columns 3, HGap 20, VGap 20
        gridPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainBackground.add(scrollPane, BorderLayout.CENTER);

        if (initialSearchKeyword != null && !initialSearchKeyword.trim().isEmpty()) {
            String keyword = initialSearchKeyword.trim();
            boolean matchedType = false;

            for (int i = 0; i < cmbFilterType.getItemCount(); i++) {
                if (cmbFilterType.getItemAt(i).equalsIgnoreCase(keyword)) {
                    cmbFilterType.setSelectedIndex(i);
                    matchedType = true;
                    break;
                }
            }

            if (!matchedType) {
                txtSearch.setText(keyword);
            }
        }

        fetchVehiclesFromDB();
    }


    private void fetchVehiclesFromDB() {
        String searchKey = txtSearch.getText().trim().toLowerCase();
        String selectedType = cmbFilterType.getSelectedItem().toString();


        SwingWorker<List<Vehicle>, Void> fetchWorker = new SwingWorker<>() {
            @Override
            protected List<Vehicle> doInBackground() {
                return ApiClient.getAllVehicles(); // GET /api/vehicles/available
            }

            @Override
            protected void done() {
                List<Vehicle> apiVehicles;
                try {
                    apiVehicles = get();
                } catch (Exception ex) {
                    apiVehicles = null;
                }

                vehicleList = new ArrayList<>();

                if (apiVehicles == null) {
                    JOptionPane.showMessageDialog(
                            CustomerVehicleView.this,
                            "Could not connect to the server. Please make sure the backend is running.",
                            "Connection Error",
                            JOptionPane.WARNING_MESSAGE
                    );
                    renderVehicleCards();
                    return;
                }

                for (Vehicle v : apiVehicles) {
                    boolean matchesSearch = searchKey.isEmpty()
                            || (v.getBrand() != null && v.getBrand().toLowerCase().contains(searchKey))
                            || (v.getModel() != null && v.getModel().toLowerCase().contains(searchKey));

                    boolean matchesType = selectedType.equalsIgnoreCase("All Types")
                            || selectedType.equalsIgnoreCase(v.getVehicleType());

                    if (matchesSearch && matchesType) {
                        String priceFormatted = String.format("%,.2f", v.getPrice());
                        String imagePath = (v.getImages() != null && !v.getImages().isEmpty())
                                ? v.getImages().get(0)
                                : null;

                        vehicleList.add(new VehicleData(
                                String.valueOf(v.getVehicleId()),
                                v.getBrand(),
                                v.getModel(),
                                v.getVehicleType(),
                                priceFormatted,
                                v.getStatus(),
                                imagePath
                        ));
                    }
                }

                renderVehicleCards();
            }
        };
        fetchWorker.execute();
    }


    private void renderVehicleCards() {
        gridPanel.removeAll();

        if (vehicleList.isEmpty()) {
            JLabel lblNoData = new JLabel("No Vehicles Available Matching Your Criteria", SwingConstants.CENTER);
            lblNoData.setFont(new Font("Segoe UI", Font.BOLD, 18));
            lblNoData.setForeground(new Color(200, 210, 225));
            gridPanel.add(lblNoData);
        } else {
            for (VehicleData v : vehicleList) {
                GlassCardPanel3D card = new GlassCardPanel3D();
                card.setLayout(new BorderLayout(10, 10));
                card.setBorder(new EmptyBorder(15, 15, 15, 15));


                JLabel lblImage = new JLabel("No Image Preview", SwingConstants.CENTER);
                lblImage.setPreferredSize(new Dimension(220, 130));
                lblImage.setOpaque(true);
                lblImage.setBackground(new Color(15, 20, 30, 180));
                lblImage.setForeground(new Color(150, 160, 180));


                if (v.imagePath != null && !v.imagePath.isEmpty()) {
                    try {
                        URL imageUrl = new URL("http://localhost:5000/uploads/" + v.imagePath);
                        ImageIcon icon = new ImageIcon(imageUrl);
                        Image img = icon.getImage().getScaledInstance(220, 130, Image.SCALE_SMOOTH);
                        lblImage.setIcon(new ImageIcon(img));
                        lblImage.setText("");
                    } catch (Exception ex) {
                        // Image load fail උනොත් "No Image Preview" placeholder එකම පෙන්නනවා
                    }
                }


                JPanel infoPanel = new JPanel(new GridLayout(4, 1, 2, 2));
                infoPanel.setOpaque(false);

                JLabel lblTitle = new JLabel(v.brand + " " + v.model);
                lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
                lblTitle.setForeground(new Color(0, 230, 255));

                JLabel lblType = new JLabel("Type: " + v.type);
                lblType.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                lblType.setForeground(new Color(200, 210, 225));

                JLabel lblPrice = new JLabel("Rs. " + v.price);
                lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 15));
                lblPrice.setForeground(new Color(46, 204, 113));

                JLabel lblStatus = new JLabel("Status: " + v.status);
                lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lblStatus.setForeground(v.status.equalsIgnoreCase("Available") ? Color.ORANGE : Color.RED);

                infoPanel.add(lblTitle);
                infoPanel.add(lblType);
                infoPanel.add(lblPrice);
                infoPanel.add(lblStatus);

                // Action Buttons Panel (Buy + Offer + View Details)
                JPanel cardButtonPanel = new JPanel(new GridLayout(1, 3, 6, 0));
                cardButtonPanel.setOpaque(false);

                JButton btnBuy = create3DButton("🛒 Buy", new Color(46, 204, 113));
                JButton btnOffer = create3DButton("💰 Offer", new Color(230, 126, 34));
                JButton btnDetails = create3DButton("ℹ️ Details", new Color(155, 89, 182));

                btnBuy.addActionListener(e -> processBuyVehicle(v));
                btnOffer.addActionListener(e -> processMakeOffer(v));
                btnDetails.addActionListener(e -> showVehiclePopup(v));

                cardButtonPanel.add(btnBuy);
                cardButtonPanel.add(btnOffer);
                cardButtonPanel.add(btnDetails);

                card.add(lblImage, BorderLayout.NORTH);
                card.add(infoPanel, BorderLayout.CENTER);
                card.add(cardButtonPanel, BorderLayout.SOUTH);

                gridPanel.add(card);
            }
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }


    private void processMakeOffer(VehicleData v) {
        int buyerId = UserSession.getUserId();
        if (buyerId <= 0) {
            JOptionPane.showMessageDialog(this, "You must log in before making an offer.!", "Not Logged In", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String input = JOptionPane.showInputDialog(
                this,
                "Listed Price: Rs. " + v.price + "\n" + v.brand + " " + v.model + " this is your offer (Rs.):",
                "Make an Offer",
                JOptionPane.PLAIN_MESSAGE
        );

        if (input == null || input.trim().isEmpty()) return; // Cancel කළා

        double offerAmount;
        try {
            offerAmount = Double.parseDouble(input.trim().replace(",", ""));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "please enter valid Number !", "Invalid Amount", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (offerAmount <= 0) {
            JOptionPane.showMessageDialog(this, "Offer Amount is less than 0", "Invalid Amount", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JsonObject body = new JsonObject();
        body.addProperty("vehicle_id", Integer.parseInt(v.id));
        body.addProperty("offer_amount", offerAmount);
        body.addProperty("buyer_id", buyerId);

        // Network call එක EDT (UI Thread) එකේ block නොවෙන්න SwingWorker එකෙන් background thread එකකට
        SwingWorker<String, Void> offerWorker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                return ApiClient.sendPost("/offers/make", body);
            }

            @Override
            protected void done() {
                try {
                    String jsonResponse = get();
                    JsonObject responseObj = new Gson().fromJson(jsonResponse, JsonObject.class);
                    String message = responseObj.has("message") ? responseObj.get("message").getAsString() : "";

                    if (message.toLowerCase().contains("successfully")) {
                        JOptionPane.showMessageDialog(
                                CustomerVehicleView.this,
                                "✅ ඔයාගේ Offer එක (Rs. " + String.format("%,.2f", offerAmount) + ") sending  Seller ! Seller will Accept to infrom.",
                                "Offer Sent",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    } else {
                        JOptionPane.showMessageDialog(CustomerVehicleView.this, "❌ " + message, "Offer Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(CustomerVehicleView.this, "Server Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        offerWorker.execute();
    }


    private void processBuyVehicle(VehicleData v) {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Do you want to proceed with purchasing " + v.brand + " " + v.model + " (Rs. " + v.price + ")?",
                "Confirm Purchase",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(
                    this,
                    "please cantact me 0723425678 " + v.brand + " " + v.model + ".",
                    "Purchase Inquiry Sent",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }


    private void showVehiclePopup(VehicleData v) {
        JDialog dialog = new JDialog(this, v.brand + " " + v.model + " Details", true);
        dialog.setSize(420, 380);
        dialog.setLocationRelativeTo(this);

        GlassCardPanel3D popCard = new GlassCardPanel3D();
        popCard.setLayout(new GridLayout(6, 1, 10, 10));
        popCard.setBorder(new EmptyBorder(25, 25, 25, 25));

        popCard.add(createPopLabel("Brand: " + v.brand));
        popCard.add(createPopLabel("Model: " + v.model));
        popCard.add(createPopLabel("Type: " + v.type));
        popCard.add(createPopLabel("Price: Rs. " + v.price));
        popCard.add(createPopLabel("Status: " + v.status));

        JButton btnClose = create3DButton("Close", new Color(231, 76, 60));
        btnClose.addActionListener(e -> dialog.dispose());
        popCard.add(btnClose);

        dialog.add(popCard);
        dialog.setVisible(true);
    }

    private JLabel createPopLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(Color.WHITE);
        return lbl;
    }

    // --- Data Class ---
    public static class VehicleData {
        String id, brand, model, type, price, status, imagePath;

        public VehicleData(String id, String brand, String model, String type, String price, String status, String imagePath) {
            this.id = id;
            this.brand = brand;
            this.model = model;
            this.type = type;
            this.price = price;
            this.status = status;
            this.imagePath = imagePath;
        }
    }


    private JLabel createGradientTitle(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.LEFT) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                GradientPaint titleGradient = new GradientPaint(0, 0, new Color(9, 116, 232), getWidth(), 0, new Color(0, 230, 255));
                g2.setPaint(titleGradient);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), 0, fm.getAscent());
            }
        };
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbl.setPreferredSize(new Dimension(280, 35));
        return lbl;
    }

    private void styleTextField(JTextField txt) {
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setPreferredSize(new Dimension(140, 35));
        txt.setBackground(new Color(15, 20, 30, 200));
        txt.setForeground(Color.WHITE);
        txt.setCaretColor(new Color(0, 230, 255));
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 70, 100), 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
    }

    private JButton create3DButton(String text, Color baseColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();

                g2.setColor(baseColor.darker().darker());
                g2.fillRoundRect(0, 3, w, h - 3, 10, 10);

                g2.setColor(getModel().isPressed() ? baseColor.darker() : baseColor);
                g2.fillRoundRect(0, 0, w, h - 3, 10, 10);

                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillRoundRect(0, 0, w, (h - 3) / 2, 10, 10);

                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    // 3D Glass Panel Component
    class GlassCardPanel3D extends JPanel {
        public GlassCardPanel3D() { setOpaque(false); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int corner = 20;

            for (int i = 0; i < 6; i++) {
                g2.setColor(new Color(0, 0, 0, Math.max(0, (40 / 6) * (6 - i))));
                g2.fillRoundRect(i, i + 2, w - (i * 2), h - (i * 2) - 2, corner, corner);
            }

            GradientPaint glassGradient = new GradientPaint(
                    0, 0, new Color(28, 35, 48, 230),
                    0, h, new Color(15, 18, 25, 240)
            );
            g2.setPaint(glassGradient);
            g2.fillRoundRect(3, 0, w - 6, h - 3, corner, corner);

            g2.setColor(new Color(0, 230, 255, 25));
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawRoundRect(3, 0, w - 6, h - 3, corner, corner);
        }
    }


    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            var imgUrl = getClass().getResource(imagePath);
            if (imgUrl != null) {
                backgroundImage = new ImageIcon(imgUrl).getImage();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, new Color(15, 18, 25), getWidth(), getHeight(), new Color(10, 12, 18)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
            g.setColor(new Color(0, 0, 0, 140));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerVehicleView().setVisible(true));
    }
}