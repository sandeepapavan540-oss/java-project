package vehicleapp.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import vehicleapp.database.AuthService;
import vehicleapp.database.UserSession;

public class LoginForm extends JFrame {

    private boolean isLoginView = true;
    private Timer slideTimer;

    // Login Components
    private JTextField txtLoginUsername;
    private JPasswordField txtLoginPassword;
    private JButton btnLogin;
    private JLabel lblGoToSignUp;

    // Register Components
    private JTextField txtRegUserId;
    private JTextField txtRegUsername, txtRegEmail;
    private JPasswordField txtRegPassword;
    private JComboBox<String> cmbUserType;
    private JButton btnRegister;
    private JLabel lblGoToLogin;

    private JPanel mainContainer;
    private GlassCardPanel3D slidingCard;

    public LoginForm() {
        setTitle("Vehicle Management System - Authentication");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(900, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        BackgroundPanel mainBackgroundPanel = new BackgroundPanel("/login_bg.png");
        mainBackgroundPanel.setLayout(new GridBagLayout());
        setContentPane(mainBackgroundPanel);


        mainContainer = new JPanel(null);
        mainContainer.setOpaque(false);
        mainContainer.setPreferredSize(new Dimension(850, 600));


        slidingCard = new GlassCardPanel3D();
        slidingCard.setBounds(0, 0, 425, 600);
        slidingCard.setLayout(new CardLayout());

        JPanel loginPanel = createLoginPanel();
        JPanel registerPanel = createRegisterPanel();

        slidingCard.add(loginPanel, "LOGIN");
        slidingCard.add(registerPanel, "REGISTER");

        mainContainer.add(slidingCard);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        mainBackgroundPanel.add(mainContainer, gbc);


        slideTimer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int targetX = isLoginView ? 0 : 425;
                int currentX = slidingCard.getX();

                if (currentX < targetX) {
                    currentX = Math.min(currentX + 25, targetX);
                    slidingCard.setLocation(currentX, 0);
                } else if (currentX > targetX) {
                    currentX = Math.max(currentX - 25, targetX);
                    slidingCard.setLocation(currentX, 0);
                } else {
                    slideTimer.stop();
                }
            }
        });


        lblGoToSignUp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                switchView(false);
            }
        });

        lblGoToLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                switchView(true);
            }
        });
    }

    private void switchView(boolean showLogin) {
        this.isLoginView = showLogin;
        CardLayout cl = (CardLayout) (slidingCard.getLayout());
        cl.show(slidingCard, showLogin ? "LOGIN" : "REGISTER");
        slideTimer.start();
    }


    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = createGradientTitle("WELCOME BACK");
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitle, gbc);

        gbc.gridwidth = 2; gbc.gridx = 0;


        gbc.gridy = 1;
        panel.add(createLabel("Email"), gbc);
        gbc.gridy = 2;
        txtLoginUsername = createStyledTextField();
        panel.add(txtLoginUsername, gbc);


        gbc.gridy = 3;
        panel.add(createLabel("Password"), gbc);
        gbc.gridy = 4;
        txtLoginPassword = createStyledPasswordField();
        panel.add(txtLoginPassword, gbc);


        gbc.gridy = 5; gbc.insets = new Insets(20, 8, 8, 8);
        btnLogin = create3DButton("Log In", new Color(9, 116, 232));
        panel.add(btnLogin, gbc);


        gbc.gridy = 6; gbc.insets = new Insets(10, 8, 8, 8);
        lblGoToSignUp = new JLabel("<html>Don't have an account? <font color='#00E6FF'><b>Sign Up</b></font></html>", SwingConstants.CENTER);
        lblGoToSignUp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblGoToSignUp.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(lblGoToSignUp, gbc);


        btnLogin.addActionListener(e -> {

            String email = txtLoginUsername.getText().trim();
            String password = new String(txtLoginPassword.getPassword());

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(LoginForm.this, "Please enter email and password", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            btnLogin.setEnabled(false);


            SwingWorker<Boolean, Void> loginWorker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() {
                    return AuthService.login(email, password);
                }

                @Override
                protected void done() {
                    btnLogin.setEnabled(true);
                    try {
                        boolean success = get();
                        if (success) {
                            String uname = UserSession.getUsername();
                            String userType = UserSession.getUserType();

                            JOptionPane.showMessageDialog(LoginForm.this, "Login Successful! Welcome " + uname, "Success", JOptionPane.INFORMATION_MESSAGE);


                            if ("ADMIN".equalsIgnoreCase(userType)) {
                                new MainDashboard().setVisible(true);
                            } else {
                                new VehicleForm().setVisible(true);
                            }

                            LoginForm.this.dispose();
                        } else {
                            JOptionPane.showMessageDialog(LoginForm.this, AuthService.getLastErrorMessage(), "Login Failed", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(LoginForm.this, "Server Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            loginWorker.execute();
        });

        return panel;
    }


    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 30, 15, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = createGradientTitle("CREATE ACCOUNT");
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitle, gbc);

        gbc.gridwidth = 2; gbc.gridx = 0;

        // User ID Field
        gbc.gridy = 1;
        panel.add(createLabel("User ID"), gbc);
        gbc.gridy = 2;
        txtRegUserId = createStyledTextField();
        panel.add(txtRegUserId, gbc);

        // Username
        gbc.gridy = 3;
        panel.add(createLabel("Username"), gbc);
        gbc.gridy = 4;
        txtRegUsername = createStyledTextField();
        panel.add(txtRegUsername, gbc);

        // Email
        gbc.gridy = 5;
        panel.add(createLabel("Email"), gbc);
        gbc.gridy = 6;
        txtRegEmail = createStyledTextField();
        panel.add(txtRegEmail, gbc);

        // Password
        gbc.gridy = 7;
        panel.add(createLabel("Password"), gbc);
        gbc.gridy = 8;
        txtRegPassword = createStyledPasswordField();
        panel.add(txtRegPassword, gbc);

        gbc.gridy = 9;
        panel.add(createLabel("User Type"), gbc);
        gbc.gridy = 10;
        String[] types = {"SELLER", "BUYER"};
        cmbUserType = new JComboBox<>(types);
        cmbUserType.setPreferredSize(new Dimension(260, 32));
        cmbUserType.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbUserType.setBackground(new Color(15, 20, 30));
        cmbUserType.setForeground(Color.WHITE);
        panel.add(cmbUserType, gbc);

        // Register Button
        gbc.gridy = 11; gbc.insets = new Insets(10, 5, 5, 5);
        btnRegister = create3DButton("Register", new Color(46, 204, 113));
        panel.add(btnRegister, gbc);

        // Go to Login Link
        gbc.gridy = 12; gbc.insets = new Insets(5, 5, 5, 5);
        lblGoToLogin = new JLabel("<html>Already have an account? <font color='#00E6FF'><b>Log In</b></font></html>", SwingConstants.CENTER);
        lblGoToLogin.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblGoToLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(lblGoToLogin, gbc);


        btnRegister.addActionListener(e -> {
            String username = txtRegUsername.getText().trim();
            String email = txtRegEmail.getText().trim();
            String password = new String(txtRegPassword.getPassword());
            String userType = (String) cmbUserType.getSelectedItem();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(LoginForm.this, "Please fill in all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            btnRegister.setEnabled(false);

            SwingWorker<Boolean, Void> registerWorker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() {
                    return AuthService.register(username, email, password, userType);
                }

                @Override
                protected void done() {
                    btnRegister.setEnabled(true);
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(LoginForm.this, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            txtRegUsername.setText("");
                            txtRegEmail.setText("");
                            txtRegPassword.setText("");
                            switchView(true);
                        } else {
                            JOptionPane.showMessageDialog(LoginForm.this, AuthService.getLastErrorMessage(), "Registration Failed", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(LoginForm.this, "Server Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            registerWorker.execute();
        });

        return panel;
    }


    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(new Color(200, 210, 225));
        return lbl;
    }

    private JLabel createGradientTitle(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                GradientPaint titleGradient = new GradientPaint(
                        0, 0, new Color(9, 116, 232),
                        getWidth(), 0, new Color(0, 230, 255)
                );
                g2.setPaint(titleGradient);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                g2.drawString(getText(), Math.max(0, x), fm.getAscent());
            }
        };
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbl.setPreferredSize(new Dimension(280, 25));
        return lbl;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(16);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setPreferredSize(new Dimension(260, 30));
        field.setBackground(new Color(15, 20, 30, 200));
        field.setForeground(Color.WHITE);
        field.setCaretColor(new Color(0, 230, 255));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 70, 100), 1, true),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(16);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setPreferredSize(new Dimension(260, 30));
        field.setBackground(new Color(15, 20, 30, 200));
        field.setForeground(Color.WHITE);
        field.setCaretColor(new Color(0, 230, 255));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 70, 100), 1, true),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));
        return field;
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

        button.setPreferredSize(new Dimension(260, 36));
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }


    class GlassCardPanel3D extends JPanel {
        public GlassCardPanel3D() {
            setOpaque(false);
        }

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}