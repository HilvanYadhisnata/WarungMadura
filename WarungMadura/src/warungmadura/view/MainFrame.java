package warungmadura.view;

import warungmadura.model.User;
import warungmadura.util.AppColors;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.net.URL;

public class MainFrame extends JFrame {
    private User currentUser;
    private JPanel contentArea;
    private CardLayout cardLayout;
    private JLabel clockLabel;

    private KasirPanel kasirPanel;
    private ProductPanel productPanel;
    private HistoryPanel historyPanel;

    public MainFrame(User user) {
        this.currentUser = user;
        setTitle("Warung Madura - Sistem Kasir");
        setSize(1200, 750);
        setMinimumSize(new Dimension(1000, 650));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
        startClock();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Content
        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(AppColors.BG_LIGHT);

        kasirPanel = new KasirPanel(currentUser);
        productPanel = new ProductPanel();
        historyPanel = new HistoryPanel();

        contentArea.add(kasirPanel, "kasir");
        contentArea.add(productPanel, "produk");
        contentArea.add(historyPanel, "riwayat");

        add(contentArea, BorderLayout.CENTER);
        cardLayout.show(contentArea, "kasir");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(AppColors.PRIMARY_DARK);
        sidebar.setPreferredSize(new Dimension(200, 0));

        // Top: logo
        JPanel logoPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        logoPanel.setBackground(AppColors.PRIMARY_DARK);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        JLabel logo;
        try {
            // Coba load via classpath dulu, lalu fallback ke path relatif dari lokasi .class / .jar
            java.net.URL imgUrl = getClass().getResource("/img/warmad_bg.png");
            if (imgUrl == null) {
                // Fallback: cari relatif terhadap lokasi file .class
                java.io.File jarDir = new java.io.File(
                    getClass().getProtectionDomain().getCodeSource().getLocation().toURI()
                ).getParentFile();
                java.io.File imgFile = new java.io.File(jarDir, "img/warmad.png");
                if (!imgFile.exists()) {
                    // Fallback kedua: relatif terhadap working directory
                    imgFile = new java.io.File("WarungMadura/img/warmad.png");
                }
                imgUrl = imgFile.toURI().toURL();
            }
            ImageIcon logoIcon = new ImageIcon(imgUrl);
            Image scaledImg = logoIcon.getImage().getScaledInstance(-1, 150, Image.SCALE_SMOOTH);
            logo = new JLabel(new ImageIcon(scaledImg));
            } catch (Exception ex) {
            logo = new JLabel("WARUNG", SwingConstants.CENTER);
            logo.setForeground(Color.WHITE);
        }
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        clockLabel = new JLabel("--:--:--", SwingConstants.CENTER);
        clockLabel.setFont(AppColors.monoFont(12));
        clockLabel.setForeground(new Color(0xABB2B9));

        logoPanel.add(logo);
        logoPanel.add(clockLabel);
        sidebar.add(logoPanel, BorderLayout.NORTH);

        // Navigation
        JPanel navPanel = new JPanel(new GridBagLayout());
        navPanel.setBackground(AppColors.PRIMARY_DARK);
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.insets = new Insets(4, 0, 4, 0);

        gbc.gridy = 0;
        navPanel.add(createNavButton("Kasir", "kasir"), gbc);
        gbc.gridy = 1;
        navPanel.add(createNavButton("Produk", "produk"), gbc);
        gbc.gridy = 2;
        navPanel.add(createNavButton("Riwayat", "riwayat"), gbc);

        sidebar.add(navPanel, BorderLayout.CENTER);

        // Bottom: user info
        JPanel userPanel = new JPanel(new GridLayout(3, 1, 0, 3));
        userPanel.setBackground(new Color(0x0D2B45));
        userPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel userIcon = new JLabel(currentUser.getUsername(), SwingConstants.LEFT);
        userIcon.setFont(AppColors.bodyFont(12));
        userIcon.setForeground(Color.WHITE);

        JLabel roleLabel = new JLabel(currentUser.getRole().toUpperCase(), SwingConstants.LEFT);
        roleLabel.setFont(AppColors.titleFont(10));
        roleLabel.setForeground(AppColors.ACCENT);

        StyledButton logoutBtn = new StyledButton("Logout", AppColors.DANGER, AppColors.DANGER.darker());
        logoutBtn.setFont(AppColors.bodyFont(12));
        logoutBtn.setPreferredSize(new Dimension(0, 32));
        logoutBtn.addActionListener(e -> logout());

        userPanel.add(userIcon);
        userPanel.add(roleLabel);
        userPanel.add(logoutBtn);
        sidebar.add(userPanel, BorderLayout.SOUTH);

        return sidebar;
    }

    private JButton createNavButton(String text, String card) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(AppColors.bodyFont(14));
        btn.setBackground(AppColors.PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(AppColors.ACCENT); btn.repaint(); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(AppColors.PRIMARY); btn.repaint(); }
        });
        btn.addActionListener(e -> {
            cardLayout.show(contentArea, card);
            if ("riwayat".equals(card)) historyPanel.refresh();
            if ("produk".equals(card)) productPanel.refresh();
        });
        return btn;
    }

    private void startClock() {
        Timer timer = new Timer(1000, e -> {
            String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            clockLabel.setText(time);
        });
        timer.start();
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin logout?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }
}
