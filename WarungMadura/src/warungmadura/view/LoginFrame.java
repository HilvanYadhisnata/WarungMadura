package warungmadura.view;

import warungmadura.controller.UserDAO;
import warungmadura.model.User;
import warungmadura.util.AppColors;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private UserDAO userDAO = new UserDAO();

    public LoginFrame() {
        setTitle("Warung Madura - Login");
        setSize(580, 780);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, AppColors.PRIMARY_DARK, 0, getHeight(), AppColors.PRIMARY);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new GridBagLayout());

        JPanel card = new JPanel(new BorderLayout(0, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        card.setPreferredSize(new Dimension(480, 600));

        // Logo section
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        JLabel label;
        try {
            java.net.URL imgUrl = getClass().getResource("/img/warmad.png");
            if (imgUrl == null) {
                java.io.File jarDir = new java.io.File(
                    getClass().getProtectionDomain().getCodeSource().getLocation().toURI()
                ).getParentFile();
                java.io.File imgFile = new java.io.File(jarDir, "img/warmad.png");
                if (!imgFile.exists()) imgFile = new java.io.File("WarungMadura/img/warmad.png");
                imgUrl = imgFile.toURI().toURL();
            }
            ImageIcon logoIcon = new ImageIcon(imgUrl);
            Image scaledImg = logoIcon.getImage().getScaledInstance(-1, 100, Image.SCALE_SMOOTH);
            label = new JLabel("Selamat Datang!", new ImageIcon(scaledImg), SwingConstants.CENTER);
        } catch (Exception ex) {
            label = new JLabel("Selamat Datang!", SwingConstants.CENTER);
        }
        label.setFont(AppColors.titleFont(16));
        label.setForeground(AppColors.PRIMARY);
        label.setVerticalTextPosition(SwingConstants.BOTTOM);
        label.setHorizontalTextPosition(SwingConstants.CENTER);
        label.setIconTextGap(20);
        topPanel.add(label, BorderLayout.NORTH);

        JLabel titleLabel = new JLabel("WARUNG MADURA", SwingConstants.CENTER);
        titleLabel.setFont(AppColors.titleFont(22));
        titleLabel.setForeground(AppColors.PRIMARY);
        topPanel.add(titleLabel, BorderLayout.CENTER);

        JLabel subLabel = new JLabel("Sistem Kasir Digital", SwingConstants.CENTER);
        subLabel.setFont(AppColors.bodyFont(13));
        subLabel.setForeground(AppColors.TEXT_MUTED);
        topPanel.add(subLabel, BorderLayout.SOUTH);

        card.add(topPanel, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(AppColors.titleFont(13));
        userLabel.setForeground(AppColors.TEXT_DARK);
        formPanel.add(userLabel, gbc);

        gbc.gridy = 1;
        usernameField = createTextField(" Masukkan username...");
        formPanel.add(usernameField, gbc);

        gbc.gridy = 2;
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(AppColors.titleFont(13));
        passLabel.setForeground(AppColors.TEXT_DARK);
        formPanel.add(passLabel, gbc);

        gbc.gridy = 3;
        passwordField = new JPasswordField();
        passwordField = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getPassword().length == 0 && !hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(AppColors.TEXT_MUTED);
                    g2.setFont(AppColors.bodyFont(13));
                    g2.drawString(" Masukkan password...", 12, getHeight() / 2 + 5);
                }
            }
        };
        passwordField.setFont(AppColors.bodyFont(14));
        passwordField.setPreferredSize(new Dimension(0, 44));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(AppColors.BORDER, 1, true),
            BorderFactory.createEmptyBorder(0, 12, 0, 12)
        ));
        formPanel.add(passwordField, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(20, 0, 6, 0);
        StyledButton loginBtn = new StyledButton("MASUK", AppColors.ACCENT, AppColors.ACCENT_DARK);
        loginBtn.setPreferredSize(new Dimension(0, 48));
        loginBtn.setFont(AppColors.titleFont(14));
        loginBtn.addActionListener(e -> doLogin());
        formPanel.add(loginBtn, gbc);

        // Hint
        gbc.gridy = 5;
        gbc.insets = new Insets(16, 0, 0, 0);
        JLabel hint = new JLabel("<html><center><font color='#7F8C8D' size='2'>Default: admin / admin123<br>Kasir: kasir1 / kasir123</font></center></html>", SwingConstants.CENTER);
        formPanel.add(hint, gbc);

        card.add(formPanel, BorderLayout.CENTER);

        // Add card with rounded border
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(2, 2, 2, 2),
            BorderFactory.createCompoundBorder(
                new LineBorder(new Color(0xE0E0E0), 1, true),
                BorderFactory.createEmptyBorder(38, 38, 38, 38)
            )
        ));

        passwordField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin();
            }
        });

        mainPanel.add(card);
        setContentPane(mainPanel);
    }

    private JTextField createTextField(String placeholder) {
        JTextField tf = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(AppColors.TEXT_MUTED);
                    g2.setFont(AppColors.bodyFont(13));
                    g2.drawString(placeholder, 12, getHeight() / 2 + 5);
                }
            }
        };
        tf.setFont(AppColors.bodyFont(14));
        tf.setPreferredSize(new Dimension(0, 44));
        tf.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(AppColors.BORDER, 1, true),
            BorderFactory.createEmptyBorder(0, 12, 0, 12)
        ));
        return tf;
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan password harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = userDAO.login(username, password);
        if (user != null) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                MainFrame mainFrame = new MainFrame(user);
                mainFrame.setVisible(true);
            });
        } else {
            JOptionPane.showMessageDialog(this, "Username atau password salah!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }
}
