package vehicleapp.ui;

import com.google.gson.JsonObject;
import vehicleapp.database.ApiClient;
import vehicleapp.database.UserSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VehicleForm extends JFrame {

    private JTextField txtVehicleNumber, txtBrand, txtModel, txtPrice, txtCustomerName, txtContactNo;
    private JComboBox<String> cmbType, cmbStatus;
    private JButton btnSave, btnClear, btnBack, btnBrowse;
    private JButton btnSearchVehicle, btnSubmitInquiry;
    private JLabel lblImagePreview, lblCustName, lblContactNo, lblTitle;
    private JLabel lblPrice, lblStatus, lblImage;
    private JPanel imagePanel, buttonPanel;
    private File selectedImageFile = null;

    private boolean isSellMode = true;
    private JPanel sliderTogglePanel;
    private JLabel lblSellTab, lblBuyTab;

    public VehicleForm() {
        setTitle("Vehicle Management System - Buy & Sell Center");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(950, 750));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        BackgroundPanel mainBackground = new BackgroundPanel("/login_bg.png");
        mainBackground.setLayout(new GridBagLayout());
        setContentPane(mainBackground);

        GlassCardPanel3D formCard = new GlassCardPanel3D();
        formCard.setLayout(new GridBagLayout());
        formCard.setBorder(new EmptyBorder(25, 40, 25, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        sliderTogglePanel = createModeSlider();
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 10, 15, 10);
        formCard.add(sliderTogglePanel, gbc);

        lblTitle = createGradientTitle("VEHICLE SELLING REGISTRATION");
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 10, 10, 10);
        formCard.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(6, 10, 6, 10);

        Font labelFont = new Font("Segoe UI", Font.BOLD, 13);
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 14);

        // Customer Inputs
        lblCustName = createLabel("Customer Name:", labelFont);
        txtCustomerName = createStyledTextField(inputFont);
        gbc.gridx = 0; gbc.gridy = 2; formCard.add(lblCustName, gbc);
        gbc.gridx = 1; gbc.gridy = 2; formCard.add(txtCustomerName, gbc);

        lblContactNo = createLabel("Contact No:", labelFont);
        txtContactNo = createStyledTextField(inputFont);
        gbc.gridx = 0; gbc.gridy = 3; formCard.add(lblContactNo, gbc);
        gbc.gridx = 1; gbc.gridy = 3; formCard.add(txtContactNo, gbc);

        // Vehicle Inputs
        gbc.gridx = 0; gbc.gridy = 4; formCard.add(createLabel("Vehicle Number:", labelFont), gbc);
        gbc.gridx = 1; gbc.gridy = 4; txtVehicleNumber = createStyledTextField(inputFont); formCard.add(txtVehicleNumber, gbc);

        gbc.gridx = 0; gbc.gridy = 5; formCard.add(createLabel("Brand:", labelFont), gbc);
        gbc.gridx = 1; gbc.gridy = 5; txtBrand = createStyledTextField(inputFont); formCard.add(txtBrand, gbc);

        gbc.gridx = 0; gbc.gridy = 6; formCard.add(createLabel("Model:", labelFont), gbc);
        gbc.gridx = 1; gbc.gridy = 6; txtModel = createStyledTextField(inputFont); formCard.add(txtModel, gbc);

        gbc.gridx = 0; gbc.gridy = 7; formCard.add(createLabel("Vehicle Type:", labelFont), gbc);
        gbc.gridx = 1; gbc.gridy = 7;
        cmbType = new JComboBox<>(new String[]{"All Types", "Car", "Van", "SUV", "Bike", "Lorry", "Other"});
        styleComboBox(cmbType, inputFont);
        formCard.add(cmbType, gbc);

        // Price, Status & Image Fields
        lblPrice = createLabel("Price (Rs.):", labelFont);
        txtPrice = createStyledTextField(inputFont);
        gbc.gridx = 0; gbc.gridy = 8; formCard.add(lblPrice, gbc);
        gbc.gridx = 1; gbc.gridy = 8; formCard.add(txtPrice, gbc);

        lblStatus = createLabel("Status:", labelFont);
        cmbStatus = new JComboBox<>(new String[]{"Available", "Sold"});
        styleComboBox(cmbStatus, inputFont);
        gbc.gridx = 0; gbc.gridy = 9; formCard.add(lblStatus, gbc);
        gbc.gridx = 1; gbc.gridy = 9; formCard.add(cmbStatus, gbc);

        lblImage = createLabel("Vehicle Image:", labelFont);
        gbc.gridx = 0; gbc.gridy = 10; formCard.add(lblImage, gbc);

        imagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        imagePanel.setOpaque(false);

        btnBrowse = create3DButton("Choose...", new Color(52, 152, 219));
        btnBrowse.setPreferredSize(new Dimension(110, 35));

        lblImagePreview = new JLabel("No Image", SwingConstants.CENTER);
        lblImagePreview.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblImagePreview.setForeground(new Color(150, 160, 180));
        lblImagePreview.setPreferredSize(new Dimension(100, 50));
        lblImagePreview.setBorder(BorderFactory.createLineBorder(new Color(60, 80, 110), 1, true));

        imagePanel.add(btnBrowse);
        imagePanel.add(lblImagePreview);

        gbc.gridx = 1; gbc.gridy = 10; formCard.add(imagePanel, gbc);

        // Action Buttons Panel
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttonPanel.setOpaque(false);

        btnSave = create3DButton("Register Vehicle", new Color(46, 204, 113));
        btnSearchVehicle = create3DButton("🔍 Search Vehicle", new Color(52, 152, 219));
        btnSubmitInquiry = create3DButton("📩 Submit Inquiry", new Color(155, 89, 182));
        btnClear = create3DButton("Clear", new Color(241, 196, 15));
        btnBack = create3DButton("Back", new Color(231, 76, 60));

        gbc.gridx = 0; gbc.gridy = 11; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 10, 10, 10);
        formCard.add(buttonPanel, gbc);

        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.gridx = 0; mainGbc.gridy = 0;
        mainBackground.add(formCard, mainGbc);

        // Action Listeners
        btnBrowse.addActionListener(e -> chooseImage());
        btnSave.addActionListener(e -> saveSellVehicle());
        btnSearchVehicle.addActionListener(e -> navigateToCustomerViewWithFilter());
        btnSubmitInquiry.addActionListener(e -> submitInquiryAndNavigate());
        btnClear.addActionListener(e -> clearFields());
        btnBack.addActionListener(e -> {
            new LoginForm().setVisible(true);
            dispose();
        });

        updateFormModeUI();
    }

    private JPanel createModeSlider() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 5, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(15, 20, 30, 220));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(new Color(50, 70, 100));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
            }
        };
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(320, 42));

        lblSellTab = new JLabel("SELL VEHICLE", SwingConstants.CENTER);
        lblBuyTab = new JLabel("BUY / INQUIRE", SwingConstants.CENTER);

        Font tabFont = new Font("Segoe UI", Font.BOLD, 13);
        lblSellTab.setFont(tabFont);
        lblBuyTab.setFont(tabFont);

        lblSellTab.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblBuyTab.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblSellTab.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                isSellMode = true;
                updateFormModeUI();
            }
        });

        lblBuyTab.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                isSellMode = false;
                updateFormModeUI();
            }
        });

        panel.add(lblSellTab);
        panel.add(lblBuyTab);

        return panel;
    }

    private void updateFormModeUI() {
        buttonPanel.removeAll();

        if (isSellMode) {
            lblSellTab.setForeground(new Color(0, 230, 255));
            lblBuyTab.setForeground(new Color(150, 160, 180));
            lblTitle.setText("VEHICLE SELLING REGISTRATION");

            lblCustName.setVisible(false);
            txtCustomerName.setVisible(false);
            lblContactNo.setVisible(false);
            txtContactNo.setVisible(false);

            lblPrice.setVisible(true);
            txtPrice.setVisible(true);
            lblStatus.setVisible(true);
            cmbStatus.setVisible(true);
            lblImage.setVisible(true);
            imagePanel.setVisible(true);

            buttonPanel.add(btnSave);
            buttonPanel.add(btnClear);
            buttonPanel.add(btnBack);
        } else {
            lblSellTab.setForeground(new Color(150, 160, 180));
            lblBuyTab.setForeground(new Color(0, 230, 255));
            lblTitle.setText("VEHICLE BUYING SEARCH / REQUEST");

            lblCustName.setVisible(true);
            txtCustomerName.setVisible(true);
            lblContactNo.setVisible(true);
            txtContactNo.setVisible(true);

            lblPrice.setVisible(false);
            txtPrice.setVisible(false);
            lblStatus.setVisible(false);
            cmbStatus.setVisible(false);
            lblImage.setVisible(false);
            imagePanel.setVisible(false);

            buttonPanel.add(btnSearchVehicle);
            buttonPanel.add(btnSubmitInquiry);
            buttonPanel.add(btnClear);
            buttonPanel.add(btnBack);
        }

        sliderTogglePanel.repaint();
        buttonPanel.revalidate();
        buttonPanel.repaint();
        revalidate();
        repaint();
    }

    private void chooseImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Vehicle Image");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = fileChooser.getSelectedFile();
            ImageIcon icon = new ImageIcon(selectedImageFile.getAbsolutePath());
            Image img = icon.getImage().getScaledInstance(98, 48, Image.SCALE_SMOOTH);
            lblImagePreview.setIcon(new ImageIcon(img));
            lblImagePreview.setText("");
        }
    }

    private void saveSellVehicle() {
        String brand = txtBrand.getText().trim();
        String model = txtModel.getText().trim();
        String type = cmbType.getSelectedItem().toString();
        String priceStr = txtPrice.getText().trim();

        if (brand.isEmpty() || model.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter all required details!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric price!", "Invalid Price", JOptionPane.ERROR_MESSAGE);
            return;
        }


        int sellerId = UserSession.getUserId();
        if (sellerId <= 0) {
            JOptionPane.showMessageDialog(this, "Session expired! Please log in again.", "Not Logged In", JOptionPane.ERROR_MESSAGE);
            return;
        }


        String userType = UserSession.getUserType();
        String endpoint = "ADMIN".equalsIgnoreCase(userType)
                ? "/vehicles/register/admin"
                : "/vehicles/register/user";

        Map<String, String> fields = new HashMap<>();
        fields.put("brand", brand);
        fields.put("model", model);
        fields.put("price", String.valueOf(price));
        fields.put("vehicle_type", type);
        fields.put("seller_id", String.valueOf(sellerId));

        List<File> images = (selectedImageFile != null)
                ? List.of(selectedImageFile)
                : List.of();

        btnSave.setEnabled(false);

        SwingWorker<String, Void> saveWorker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                return ApiClient.sendMultipart(endpoint, fields, images);
            }

            @Override
            protected void done() {
                btnSave.setEnabled(true);
                try {
                    String jsonResponse = get();
                    JsonObject responseObj = new com.google.gson.Gson().fromJson(jsonResponse, JsonObject.class);
                    String message = responseObj.has("message") ? responseObj.get("message").getAsString() : "";

                    if (message.toLowerCase().contains("successfully")) {
                        JOptionPane.showMessageDialog(VehicleForm.this, "Vehicle Registration Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        new CustomerVehicleView().setVisible(true);
                        VehicleForm.this.dispose();
                    } else {
                        JOptionPane.showMessageDialog(VehicleForm.this, "Registration failed: " + message, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(VehicleForm.this, "Server Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        saveWorker.execute();
    }

    private String getFilterKeyword() {
        String brand = txtBrand.getText().trim();
        String model = txtModel.getText().trim();
        String vehNum = txtVehicleNumber.getText().trim();
        String type = cmbType.getSelectedItem().toString();

        if (!brand.isEmpty()) return brand;
        if (!model.isEmpty()) return model;
        if (!vehNum.isEmpty()) return vehNum;
        if (!type.equalsIgnoreCase("All Types")) return type;

        return "";
    }

    private void navigateToCustomerViewWithFilter() {
        String filterKey = getFilterKeyword();
        new CustomerVehicleView(filterKey).setVisible(true);
        this.dispose();
    }

    private void submitInquiryAndNavigate() {
        String custName = txtCustomerName.getText().trim();
        String contact = txtContactNo.getText().trim();

        if (custName.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your details!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String filterKey = getFilterKeyword();
        JOptionPane.showMessageDialog(this, "Inquiry submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        new CustomerVehicleView(filterKey).setVisible(true);
        this.dispose();
    }

    private void clearFields() {
        txtVehicleNumber.setText("");
        txtBrand.setText("");
        txtModel.setText("");
        txtPrice.setText("");
        txtCustomerName.setText("");
        txtContactNo.setText("");
        cmbType.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0);
        selectedImageFile = null;
        lblImagePreview.setIcon(null);
        lblImagePreview.setText("No Image");
    }

    private JLabel createLabel(String text, Font font) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(new Color(200, 210, 225));
        return lbl;
    }

    private JLabel createGradientTitle(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                GradientPaint titleGradient = new GradientPaint(0, 0, new Color(9, 116, 232), getWidth(), 0, new Color(0, 230, 255));
                g2.setPaint(titleGradient);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                g2.drawString(getText(), Math.max(0, x), fm.getAscent());
            }
        };
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl.setPreferredSize(new Dimension(420, 35));
        return lbl;
    }

    private JTextField createStyledTextField(Font font) {
        JTextField field = new JTextField(16);
        field.setFont(font);
        field.setPreferredSize(new Dimension(220, 36));
        field.setBackground(new Color(15, 20, 30, 200));
        field.setForeground(Color.WHITE);
        field.setCaretColor(new Color(0, 230, 255));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 70, 100), 1, true),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        return field;
    }

    private void styleComboBox(JComboBox<String> cmb, Font font) {
        cmb.setFont(font);
        cmb.setPreferredSize(new Dimension(220, 36));
        cmb.setBackground(new Color(20, 28, 40));
        cmb.setForeground(Color.WHITE);
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

                g2.setColor(new Color(255, 255, 255, 45));
                g2.fillRoundRect(0, 0, w, (h - 4) / 2, 12, 12);

                super.paintComponent(g);
            }
        };

        button.setPreferredSize(new Dimension(150, 40));
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    class GlassCardPanel3D extends JPanel {
        public GlassCardPanel3D() { setOpaque(false); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int corner = 24;

            for (int i = 0; i < 8; i++) {
                g2.setColor(new Color(0, 0, 0, Math.max(0, (60 / 8) * (8 - i))));
                g2.fillRoundRect(i, i + 2, w - (i * 2), h - (i * 2) - 2, corner, corner);
            }

            GradientPaint glassGradient = new GradientPaint(
                    0, 0, new Color(28, 35, 48, 240),
                    0, h, new Color(15, 18, 25, 250)
            );
            g2.setPaint(glassGradient);
            g2.fillRoundRect(4, 0, w - 8, h - 4, corner, corner);

            g2.setColor(new Color(0, 230, 255, 30));
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawRoundRect(4, 0, w - 8, h - 4, corner, corner);
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
            g.setColor(new Color(0, 0, 0, 120));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}