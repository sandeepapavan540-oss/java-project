package vehicleapp.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import vehicleapp.database.ApiClient;

public class MainDashboard extends JFrame {

    private boolean isDarkMode = true;
    private Color colorBg, colorCardBg, colorText, colorPrimary;

    private JPanel sidebar, topBar, mainContent;
    private HoverCardPanel3D cardIncome, cardCommission, cardOffers;
    private HoverChartPanel3D cardChart;
    private JLabel lblTitle, lblIncomeTitle, lblIncomeVal, lblCommTitle, lblCommVal, lblOffersTitle, lblOffersVal;
    private ToggleSwitch3D btnThemeToggle;
    private JButton btnAddVehicle, btnLogOut, btnCompleteSale, btnViewAllVehicles, btnViewOffers;

    public MainDashboard() {
        setTitle("Vehicle Management System - Modern Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1000, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initColors();
        setLayout(new BorderLayout());


        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));

        btnAddVehicle = create3DButton(" Register Vehicle", new Color(9, 116, 232));

        btnViewAllVehicles = create3DButton("View Available Vehicles", new Color(155, 89, 182));

        btnViewOffers = create3DButton(" View Offers", new Color(230, 126, 34));

        btnCompleteSale = create3DButton(" Complete Sale (PDF + Email)", new Color(46, 125, 50));
        btnLogOut = create3DButton(" Log Out", new Color(231, 76, 60));

        sidebar.add(btnAddVehicle);
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(btnViewAllVehicles);
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(btnViewOffers);
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(btnCompleteSale);
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(btnLogOut);


        topBar = new JPanel(new BorderLayout());
        topBar.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));


        lblTitle = new JLabel("VEHICLE DEALERSHIP DASHBOARD") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                GradientPaint titleGradient = new GradientPaint(
                        0, 0, colorPrimary,
                        getWidth(), 0, isDarkMode ? new Color(0, 230, 255) : new Color(0, 80, 180)
                );
                g2.setPaint(titleGradient);
                g2.drawString(getText(), 0, g2.getFontMetrics().getAscent());
            }
        };
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setPreferredSize(new Dimension(450, 30));


        btnThemeToggle = new ToggleSwitch3D(isDarkMode);
        btnThemeToggle.setOnToggleListener(selected -> {
            isDarkMode = selected;
            initColors();
            applyTheme();
        });

        JPanel toggleContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        toggleContainer.setOpaque(false);
        JLabel lblModeText = new JLabel(isDarkMode ? "Dark Mode" : "Light Mode");
        lblModeText.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblModeText.setForeground(colorText);

        toggleContainer.add(lblModeText);
        toggleContainer.add(btnThemeToggle);

        topBar.add(lblTitle, BorderLayout.WEST);
        topBar.add(toggleContainer, BorderLayout.EAST);


        mainContent = new JPanel(new GridBagLayout());
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.BOTH;


        cardIncome = new HoverCardPanel3D(colorCardBg);
        cardIncome.setLayout(new GridLayout(2, 1));
        cardIncome.setBorder(new EmptyBorder(25, 25, 25, 25));

        lblIncomeTitle = new JLabel("Today's Income (Click for details)");

        lblIncomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblIncomeVal = new JLabel("Loading...");
        lblIncomeVal.setFont(new Font("Segoe UI", Font.BOLD, 26));
        cardIncome.add(lblIncomeTitle);
        cardIncome.add(lblIncomeVal);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.33; gbc.weighty = 0.25;
        mainContent.add(cardIncome, gbc);

        cardCommission = new HoverCardPanel3D(colorCardBg);
        cardCommission.setLayout(new GridLayout(2, 1));
        cardCommission.setBorder(new EmptyBorder(25, 25, 25, 25));

        lblCommTitle = new JLabel("Today's Commission (10%)");
        lblCommTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCommVal = new JLabel("Loading...");
        lblCommVal.setFont(new Font("Segoe UI", Font.BOLD, 26));
        cardCommission.add(lblCommTitle);
        cardCommission.add(lblCommVal);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.33; gbc.weighty = 0.25;
        mainContent.add(cardCommission, gbc);


        cardOffers = new HoverCardPanel3D(colorCardBg);
        cardOffers.setLayout(new GridLayout(2, 1));
        cardOffers.setBorder(new EmptyBorder(25, 25, 25, 25));

        lblOffersTitle = new JLabel("Pending Offers (Click to view)");
        lblOffersTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblOffersVal = new JLabel("Loading...");
        lblOffersVal.setFont(new Font("Segoe UI", Font.BOLD, 26));
        cardOffers.add(lblOffersTitle);
        cardOffers.add(lblOffersVal);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.33; gbc.weighty = 0.25;
        mainContent.add(cardOffers, gbc);

        // Chart
        cardChart = new HoverChartPanel3D(colorCardBg);
        cardChart.setBorder(new EmptyBorder(25, 25, 25, 25));

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3; gbc.weightx = 1.0; gbc.weighty = 0.75;
        mainContent.add(cardChart, gbc);

        add(sidebar, BorderLayout.WEST);
        add(topBar, BorderLayout.NORTH);
        add(mainContent, BorderLayout.CENTER);

        applyTheme();


        btnAddVehicle.addActionListener(e -> new VehicleForm().setVisible(true));

        btnCompleteSale.addActionListener(e -> processCompleteSale());

        btnViewAllVehicles.addActionListener(e -> showAllVehiclesDialog());

        btnViewOffers.addActionListener(e -> showOffersDialog());

        btnLogOut.addActionListener(e -> {
            new LoginForm().setVisible(true);
            MainDashboard.this.dispose();
        });


        loadDashboardStats();
        loadTodayStats();
        loadOffersCount();


        cardIncome.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showTodaySoldDetails();
            }
        });

        cardOffers.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showOffersDialog();
            }
        });


        Timer autoRefreshTimer = new Timer(30_000, e -> {
            loadTodayStats();
            loadOffersCount();
        });
        autoRefreshTimer.start();
    }

    private void loadOffersCount() {
        SwingWorker<String, Void> countWorker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                return ApiClient.sendGet("/offers/all-pending");
            }

            @Override
            protected void done() {
                try {
                    String jsonResponse = get();
                    com.google.gson.JsonArray offersArray = new Gson().fromJson(jsonResponse, com.google.gson.JsonArray.class);


                    double totalOfferAmount = 0;
                    for (int i = 0; i < offersArray.size(); i++) {
                        JsonObject offer = offersArray.get(i).getAsJsonObject();
                        totalOfferAmount += offer.get("offer_amount").getAsDouble();
                    }

                    lblOffersTitle.setText("Pending Offers (" + offersArray.size() + ") - Click to view");
                    lblOffersVal.setText(String.format("Rs. %,.2f", totalOfferAmount));
                } catch (Exception ex) {
                    lblOffersVal.setText("Rs. 0.00");
                    System.err.println("⚠ Could not load offers total: " + ex.getMessage());
                }
            }
        };
        countWorker.execute();
    }


    private void loadDashboardStats() {
        SwingWorker<String, Void> statsWorker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                return ApiClient.sendGet("/dashboard/admin");
            }

            @Override
            protected void done() {
                try {
                    String jsonResponse = get();
                    JsonObject responseObj = new Gson().fromJson(jsonResponse, JsonObject.class);

                    if (responseObj.has("sales")) {

                    }

                    // 🥧 Pie Chart එක real Available/Sold vehicle counts වලින් update කිරීම
                    if (responseObj.has("vehicles")) {
                        JsonObject vehicles = responseObj.getAsJsonObject("vehicles");
                        int available = vehicles.has("available_vehicles") && !vehicles.get("available_vehicles").isJsonNull()
                                ? vehicles.get("available_vehicles").getAsInt() : 0;
                        int sold = vehicles.has("sold_vehicles") && !vehicles.get("sold_vehicles").isJsonNull()
                                ? vehicles.get("sold_vehicles").getAsInt() : 0;
                        cardChart.setData(available, sold);
                    }
                } catch (Exception ex) {
                    System.err.println("⚠ Could not load dashboard stats: " + ex.getMessage());
                }
            }
        };
        statsWorker.execute();
    }


    private com.google.gson.JsonArray latestSoldTodayList = new com.google.gson.JsonArray();

    private void loadTodayStats() {
        SwingWorker<String, Void> todayWorker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                return ApiClient.sendGet("/dashboard/admin/today");
            }

            @Override
            protected void done() {
                try {
                    String jsonResponse = get();
                    JsonObject responseObj = new Gson().fromJson(jsonResponse, JsonObject.class);

                    if (responseObj.has("summary")) {
                        JsonObject summary = responseObj.getAsJsonObject("summary");
                        double todayRevenue = summary.has("today_revenue") ? summary.get("today_revenue").getAsDouble() : 0;
                        double todayCommission = summary.has("today_commission") ? summary.get("today_commission").getAsDouble() : 0;

                        lblIncomeVal.setText(String.format("Rs. %,.2f", todayRevenue));
                        lblCommVal.setText(String.format("Rs. %,.2f", todayCommission));
                    }

                    if (responseObj.has("sold_today")) {
                        latestSoldTodayList = responseObj.getAsJsonArray("sold_today");
                    }
                } catch (Exception ex) {

                    lblIncomeVal.setText("Rs. 0.00");
                    lblCommVal.setText("Rs. 0.00");
                    System.err.println("⚠ Could not load today's stats: " + ex.getMessage());
                }
            }
        };
        todayWorker.execute();
    }

    private void showTodaySoldDetails() {
        if (latestSoldTodayList == null || latestSoldTodayList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Today is not sell.", "Today's Sales", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] columns = {"Vehicle", "Type", "Buyer", "Sale Price (Rs.)", "Commission (Rs.)", "Time"};
        Object[][] data = new Object[latestSoldTodayList.size()][6];

        for (int i = 0; i < latestSoldTodayList.size(); i++) {
            JsonObject row = latestSoldTodayList.get(i).getAsJsonObject();
            data[i][0] = row.get("brand").getAsString() + " " + row.get("model").getAsString();
            data[i][1] = row.has("vehicle_type") && !row.get("vehicle_type").isJsonNull() ? row.get("vehicle_type").getAsString() : "-";
            data[i][2] = row.has("buyer_name") ? row.get("buyer_name").getAsString() : "-";
            data[i][3] = String.format("%,.2f", row.get("sale_price").getAsDouble());
            data[i][4] = String.format("%,.2f", row.get("commission_amount").getAsDouble());
            data[i][5] = row.has("transaction_date") ? row.get("transaction_date").getAsString() : "-";
        }

        JTable table = new JTable(data, columns);
        table.setEnabled(false); // Read-only view
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(650, 220));

        JOptionPane.showMessageDialog(
                this, scrollPane,
                "🚗 Vehicles Sold Today (" + latestSoldTodayList.size() + ")",
                JOptionPane.PLAIN_MESSAGE
        );
    }

    // 🔔 "View Offers" Button - Buyer ලා දාපු Pending Offers ඔක්කොම Card List එකකින් පෙන්නීම,
    // Accept/Reject කරන්න Buttons සමඟ
    private void showOffersDialog() {
        btnViewOffers.setEnabled(false);

        SwingWorker<String, Void> offersWorker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                return ApiClient.sendGet("/offers/all-pending");
            }

            @Override
            protected void done() {
                btnViewOffers.setEnabled(true);
                try {
                    String jsonResponse = get();
                    com.google.gson.JsonArray offersArray = new Gson().fromJson(jsonResponse, com.google.gson.JsonArray.class);
                    renderOffersDialog(offersArray);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MainDashboard.this, "Server Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        offersWorker.execute();
    }

    private void renderOffersDialog(com.google.gson.JsonArray offersArray) {
        JDialog dialog = new JDialog(this, "🔔 Pending Offers (" + offersArray.size() + ")", true);
        dialog.setSize(600, 450);
        dialog.setLocationRelativeTo(this);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(colorBg);

        if (offersArray == null || offersArray.isEmpty()) {
            JLabel lblEmpty = new JLabel("Pending Offers is not.");
            lblEmpty.setBorder(new EmptyBorder(20, 20, 20, 20));
            listPanel.add(lblEmpty);
        } else {
            for (int i = 0; i < offersArray.size(); i++) {
                JsonObject offer = offersArray.get(i).getAsJsonObject();
                listPanel.add(createOfferRow(offer, dialog));
                listPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            }
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        dialog.add(scrollPane);
        dialog.setVisible(true);
    }

    // Offer එකක් සඳහා Row Panel එකක් - Details + Accept/Reject Buttons
    private JPanel createOfferRow(JsonObject offer, JDialog parentDialog) {
        int offerId = offer.get("offer_id").getAsInt();
        int vehicleId = offer.get("vehicle_id").getAsInt();
        int buyerId = offer.get("buyer_id").getAsInt();
        String brand = offer.get("brand").getAsString();
        String model = offer.get("model").getAsString();
        double offerAmount = offer.get("offer_amount").getAsDouble();
        double originalPrice = offer.get("original_price").getAsDouble();
        String buyerName = offer.has("buyer_name") ? offer.get("buyer_name").getAsString() : "-";

        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(colorCardBg);
        row.setBorder(new EmptyBorder(12, 15, 12, 15));

        JLabel lblInfo = new JLabel("<html><b>" + brand + " " + model + "</b><br>"
                + "Offer: Rs. " + String.format("%,.2f", offerAmount) + " (Listed: Rs. " + String.format("%,.2f", originalPrice) + ")<br>"
                + "Buyer: " + buyerName + "</html>");
        lblInfo.setForeground(colorText);

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        btnPanel.setOpaque(false);
        JButton btnAccept = create3DButton("Accept", new Color(46, 204, 113));
        JButton btnReject = create3DButton(" Reject", new Color(231, 76, 60));

        btnAccept.addActionListener(e -> processAcceptOffer(offerId, vehicleId, buyerId, parentDialog));
        btnReject.addActionListener(e -> processRejectOffer(offerId, parentDialog));

        btnPanel.add(btnAccept);
        btnPanel.add(btnReject);

        row.add(lblInfo, BorderLayout.CENTER);
        row.add(btnPanel, BorderLayout.EAST);

        return row;
    }


    private void processAcceptOffer(int offerId, int vehicleId, int buyerId, JDialog parentDialog) {
        SwingWorker<Void, Void> acceptWorker = new SwingWorker<>() {
            String resultMessage = "";
            boolean success = false;

            @Override
            protected Void doInBackground() throws Exception {

                ApiClient.sendPut("/offers/" + offerId + "/accept");


                JsonObject sellBody = new JsonObject();
                sellBody.addProperty("vehicle_id", vehicleId);
                sellBody.addProperty("buyer_id", buyerId);
                String sellResponse = ApiClient.sendPost("/vehicles/sell", sellBody);

                JsonObject sellObj = new Gson().fromJson(sellResponse, JsonObject.class);
                resultMessage = sellObj.has("message") ? sellObj.get("message").getAsString() : sellResponse;
                success = resultMessage.toLowerCase().contains("successfully");
                return null;
            }

            @Override
            protected void done() {
                JOptionPane.showMessageDialog(
                        parentDialog,
                        (success ? " Offer Accepted & Sale Completed!\n" : "Offer accepted, but sale completion had an issue:\n") + resultMessage,
                        "Offer Accepted",
                        success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE
                );
                parentDialog.dispose();
                showOffersDialog();
                loadTodayStats();
                loadDashboardStats();
                loadOffersCount();
            }
        };
        acceptWorker.execute();
    }


    private void processRejectOffer(int offerId, JDialog parentDialog) {
        SwingWorker<String, Void> rejectWorker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                return ApiClient.sendPut("/offers/" + offerId + "/reject");
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(parentDialog, "Offer was Reject .", "Offer Rejected", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(parentDialog, "Server Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                parentDialog.dispose();
                showOffersDialog();
                loadOffersCount();
            }
        };
        rejectWorker.execute();
    }


    private void showAllVehiclesDialog() {
        btnViewAllVehicles.setEnabled(false);

        SwingWorker<String, Void> vehiclesWorker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {

                return ApiClient.sendGet("/vehicles/available");
            }

            @Override
            protected void done() {
                btnViewAllVehicles.setEnabled(true);
                try {
                    String jsonResponse = get();
                    com.google.gson.JsonArray vehiclesArray = new Gson().fromJson(jsonResponse, com.google.gson.JsonArray.class);

                    if (vehiclesArray == null || vehiclesArray.isEmpty()) {
                        JOptionPane.showMessageDialog(MainDashboard.this, "Showroom is not Available vehicle.", "Available Vehicles", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    String[] columns = {"ID", "Brand", "Model", "Type", "Price (Rs.)", "Status", "Seller"};
                    Object[][] data = new Object[vehiclesArray.size()][7];

                    for (int i = 0; i < vehiclesArray.size(); i++) {
                        JsonObject v = vehiclesArray.get(i).getAsJsonObject();
                        data[i][0] = v.get("vehicle_id").getAsInt();
                        data[i][1] = v.get("brand").getAsString();
                        data[i][2] = v.get("model").getAsString();
                        data[i][3] = v.has("vehicle_type") && !v.get("vehicle_type").isJsonNull() ? v.get("vehicle_type").getAsString() : "-";
                        data[i][4] = String.format("%,.2f", v.get("price").getAsDouble());
                        data[i][5] = v.has("status") && !v.get("status").isJsonNull() ? v.get("status").getAsString() : "-";
                        data[i][6] = v.has("seller_name") && !v.get("seller_name").isJsonNull() ? v.get("seller_name").getAsString() : "-";
                    }

                    JTable table = new JTable(data, columns);
                    table.setEnabled(false); // Read-only view
                    table.setRowHeight(24);
                    JScrollPane scrollPane2 = new JScrollPane(table);
                    scrollPane2.setPreferredSize(new Dimension(750, 350));

                    JOptionPane.showMessageDialog(
                            MainDashboard.this, scrollPane2,
                            "🏬 Available Vehicles in Showroom (" + vehiclesArray.size() + ")",
                            JOptionPane.PLAIN_MESSAGE
                    );
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MainDashboard.this, "Server Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        vehiclesWorker.execute();
    }


    private void processCompleteSale() {
        JTextField txtVehicleId = new JTextField();
        JTextField txtBuyerId = new JTextField();
        Object[] fields = {
                "Vehicle ID:", txtVehicleId,
                "Buyer ID:", txtBuyerId
        };

        int choice = JOptionPane.showConfirmDialog(
                this, fields, "Complete Sale - Generate PDF Invoice & Send Emails",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (choice != JOptionPane.OK_OPTION) return;

        String vehicleIdStr = txtVehicleId.getText().trim();
        String buyerIdStr = txtBuyerId.getText().trim();

        if (vehicleIdStr.isEmpty() || buyerIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "please enter Vehicle ID and  Buyer ID !", "Missing Info", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JsonObject body = new JsonObject();
        body.addProperty("vehicle_id", vehicleIdStr);
        body.addProperty("buyer_id", buyerIdStr);

        btnCompleteSale.setEnabled(false);


        SwingWorker<String, Void> sellWorker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                return ApiClient.sendPost("/vehicles/sell", body);
            }

            @Override
            protected void done() {
                btnCompleteSale.setEnabled(true);
                try {
                    String jsonResponse = get();
                    JsonObject responseObj = new Gson().fromJson(jsonResponse, JsonObject.class);
                    String message = responseObj.has("message") ? responseObj.get("message").getAsString() : "";

                    if (message.toLowerCase().contains("successfully")) {
                        // Backend එකෙන් එන "details" object එකෙන් PDF/Email real status එකම පෙන්නනවා
                        String detailsText = responseObj.has("details")
                                ? responseObj.getAsJsonObject("details").toString()
                                : "";
                        JOptionPane.showMessageDialog(
                                MainDashboard.this,
                                "✅ Sale Completed!\n\n" + detailsText,
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    } else {
                        JOptionPane.showMessageDialog(MainDashboard.this, "❌ " + message, "Sale Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MainDashboard.this, "Server Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        sellWorker.execute();
    }

    private void initColors() {
        if (isDarkMode) {
            colorBg = new Color(15, 18, 25);
            colorCardBg = new Color(28, 35, 48);
            colorText = new Color(240, 240, 240);
            colorPrimary = new Color(9, 116, 232);
        } else {
            colorBg = new Color(225, 230, 238);
            colorCardBg = new Color(248, 250, 252);
            colorText = new Color(33, 33, 33);
            colorPrimary = new Color(9, 116, 232);
        }
    }

    private void applyTheme() {
        sidebar.setBackground(isDarkMode ? new Color(10, 12, 18) : new Color(210, 218, 228));
        topBar.setBackground(colorCardBg);
        mainContent.setBackground(colorBg);

        cardIncome.setBaseColor(colorCardBg);
        cardCommission.setBaseColor(colorCardBg);
        cardOffers.setBaseColor(colorCardBg);
        cardChart.setBaseColor(colorCardBg);

        lblIncomeTitle.setForeground(isDarkMode ? new Color(180, 190, 200) : Color.GRAY);
        lblIncomeVal.setForeground(new Color(46, 204, 113));
        lblCommTitle.setForeground(isDarkMode ? new Color(180, 190, 200) : Color.GRAY);
        lblCommVal.setForeground(colorPrimary);
        lblOffersTitle.setForeground(isDarkMode ? new Color(180, 190, 200) : Color.GRAY);
        lblOffersVal.setForeground(new Color(230, 126, 34));

        repaint();
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
                g2.fillRoundRect(0, 4, w, h - 4, 12, 12);

                g2.setColor(getModel().isPressed() ? baseColor.darker() : baseColor);
                g2.fillRoundRect(0, 0, w, h - 4, 12, 12);

                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillRoundRect(0, 0, w, (h - 4) / 2, 12, 12);

                super.paintComponent(g);
            }
        };

        button.setMaximumSize(new Dimension(180, 45));
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }


    class ToggleSwitch3D extends JPanel {
        private boolean active;
        private OnToggleListener listener;

        public interface OnToggleListener {
            void onToggle(boolean selected);
        }

        public ToggleSwitch3D(boolean initialState) {
            this.active = initialState;
            setPreferredSize(new Dimension(55, 28));
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    active = !active;
                    repaint();
                    if (listener != null) {
                        listener.onToggle(active);
                    }
                }
            });
        }

        public void setOnToggleListener(OnToggleListener listener) {
            this.listener = listener;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            Color trackBg = active ? new Color(9, 116, 232) : new Color(200, 205, 215);
            g2.setColor(trackBg);
            g2.fillRoundRect(0, 0, w, h, h, h);

            g2.setColor(active ? new Color(5, 80, 160) : new Color(160, 165, 175));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, h, h);

            int knobSize = h - 6;
            int knobX = active ? w - knobSize - 3 : 3;
            int knobY = 3;

            g2.setColor(new Color(0, 0, 0, 60));
            g2.fillOval(knobX, knobY + 2, knobSize, knobSize);

            g2.setColor(Color.WHITE);
            g2.fillOval(knobX, knobY, knobSize, knobSize);
        }
    }


    class HoverCardPanel3D extends JPanel {
        protected Color baseColor;
        protected boolean isHovered = false;

        public HoverCardPanel3D(Color color) {
            this.baseColor = color;
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }

        public void setBaseColor(Color color) {
            this.baseColor = color;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int corner = 22;

            int offsetY = isHovered ? 0 : 4;
            int shadowDepth = isHovered ? 16 : 8;
            int shadowAlpha = isHovered ? 120 : 60;

            for (int i = 0; i < shadowDepth; i++) {
                g2.setColor(new Color(0, 0, 0, Math.max(0, (shadowAlpha / shadowDepth) * (shadowDepth - i))));
                g2.fillRoundRect(i + 2, i + offsetY + 6, w - (i * 2) - 4, h - (i * 2) - 8, corner, corner);
            }

            Color currentColor = isHovered ? baseColor.brighter() : baseColor;
            GradientPaint bgGradient = new GradientPaint(
                    0, offsetY, currentColor.brighter(),
                    0, h, currentColor.darker()
            );
            g2.setPaint(bgGradient);
            g2.fillRoundRect(6, offsetY, w - 12, h - 14, corner, corner);

            g2.setColor(new Color(255, 255, 255, isHovered ? (isDarkMode ? 100 : 160) : (isDarkMode ? 45 : 80)));
            g2.setStroke(new BasicStroke(isHovered ? 2.5f : 1.5f));
            g2.drawRoundRect(6, offsetY, w - 12, h - 14, corner, corner);

            GradientPaint gloss = new GradientPaint(
                    0, offsetY, new Color(255, 255, 255, isHovered ? 45 : 20),
                    0, h / 2, new Color(255, 255, 255, 0)
            );
            g2.setPaint(gloss);
            g2.fillRoundRect(6, offsetY, w - 12, (h - 14) / 2, corner, corner);
        }
    }


    class HoverChartPanel3D extends HoverCardPanel3D {

        private int availableCount = 0;
        private int soldCount = 0;


        private int hoveredSlice = -1;

        public HoverChartPanel3D(Color color) {
            super(color);

            addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int newHover = sliceAtPoint(e.getPoint());
                    if (newHover != hoveredSlice) {
                        hoveredSlice = newHover;
                        repaint();
                    }
                }
            });
        }


        public void setData(int available, int sold) {
            this.availableCount = Math.max(available, 0);
            this.soldCount = Math.max(sold, 0);
            repaint();
        }

        private Rectangle pieBounds;

        private int sliceAtPoint(Point p) {
            if (pieBounds == null) return -1;
            int total = availableCount + soldCount;
            if (total == 0 || !pieBounds.contains(p)) return -1;

            double cx = pieBounds.getCenterX();
            double cy = pieBounds.getCenterY();
            double angle = Math.toDegrees(Math.atan2(-(p.y - cy), p.x - cx));
            if (angle < 0) angle += 360;

            double availableAngle = 360.0 * availableCount / total;
            return (angle <= availableAngle) ? 0 : 1;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth() - 24;
            int h = getHeight() - 24;
            int offsetY = isHovered ? 0 : 4;

            // Title
            g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
            g2.setColor(colorText);
            g2.drawString("Vehicle Inventory Status (Available vs Sold)", 25, 38 + offsetY);

            int total = availableCount + soldCount;

            int diameter = Math.min(w, h) - 130;
            if (diameter < 40) diameter = 40; // Panel එක ගොඩක් කුඩා උනත් crash නොවෙන්න
            int pieX = 40;
            int pieY = 70 + offsetY;
            pieBounds = new Rectangle(pieX, pieY, diameter, diameter);

            if (total == 0) {
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                g2.setColor(colorText);
                g2.drawString("No vehicle data yet.", pieX, pieY + diameter / 2);
                return;
            }

            double availableAngle = 360.0 * availableCount / total;
            double soldAngle = 360.0 - availableAngle;

            Color availableColor = new Color(46, 204, 113);
            Color soldColor = new Color(231, 76, 60);


            g2.setColor(new Color(0, 0, 0, 60));
            g2.fillOval(pieX, pieY + 6, diameter, diameter);

            int availableExtra = (hoveredSlice == 0) ? 8 : 0;
            int soldExtra = (hoveredSlice == 1) ? 8 : 0;


            g2.setColor(availableColor);
            g2.fillArc(pieX - availableExtra / 2, pieY - availableExtra / 2, diameter + availableExtra, diameter + availableExtra, 0, (int) Math.round(availableAngle));


            g2.setColor(soldColor);
            g2.fillArc(pieX - soldExtra / 2, pieY - soldExtra / 2, diameter + soldExtra, diameter + soldExtra, (int) Math.round(availableAngle), (int) Math.round(soldAngle));


            int holeDiameter = diameter / 2;
            int holeX = pieX + (diameter - holeDiameter) / 2;
            int holeY = pieY + (diameter - holeDiameter) / 2;
            g2.setColor(baseColor);
            g2.fillOval(holeX, holeY, holeDiameter, holeDiameter);


            g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
            g2.setColor(colorText);
            String totalText = String.valueOf(total);
            FontMetrics fmCenter = g2.getFontMetrics();
            g2.drawString(totalText, pieX + diameter / 2 - fmCenter.stringWidth(totalText) / 2, pieY + diameter / 2 + fmCenter.getAscent() / 3);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            String subText = "Total Vehicles";
            FontMetrics fmSub = g2.getFontMetrics();
            g2.drawString(subText, pieX + diameter / 2 - fmSub.stringWidth(subText) / 2, pieY + diameter / 2 + fmCenter.getAscent() / 3 + 16);


            int legendX = pieX + diameter + 40;
            int legendY = pieY + 20;
            FontMetrics fm = g2.getFontMetrics(new Font("Segoe UI", Font.BOLD, 13));

            g2.setFont(new Font("Segoe UI", (hoveredSlice == 0) ? Font.BOLD : Font.PLAIN, 13));
            g2.setColor(availableColor);
            g2.fillRoundRect(legendX, legendY - 12, 14, 14, 4, 4);
            g2.setColor(colorText);
            g2.drawString(String.format("Available: %d (%.1f%%)", availableCount, 100.0 * availableCount / total), legendX + 22, legendY);

            legendY += 30;
            g2.setFont(new Font("Segoe UI", (hoveredSlice == 1) ? Font.BOLD : Font.PLAIN, 13));
            g2.setColor(soldColor);
            g2.fillRoundRect(legendX, legendY - 12, 14, 14, 4, 4);
            g2.setColor(colorText);
            g2.drawString(String.format("Sold: %d (%.1f%%)", soldCount, 100.0 * soldCount / total), legendX + 22, legendY);
        }
    }
}