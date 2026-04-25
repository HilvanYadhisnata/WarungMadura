package warungmadura.view;

import warungmadura.controller.ProductDAO;
import warungmadura.controller.TransactionDAO;
import warungmadura.model.*;
import warungmadura.util.AppColors;
import warungmadura.util.FormatUtil;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.List;

public class KasirPanel extends JPanel {
    private User currentUser;
    private ProductDAO productDAO = new ProductDAO();
    private TransactionDAO transactionDAO = new TransactionDAO();

    private List<Product> allProducts = new ArrayList<>();
    private List<CartItem> cart = new ArrayList<>();

    private JPanel productGrid;
    private DefaultTableModel cartModel;
    private JLabel totalLabel, subtotalLabel, qtyLabel;
    private JTextField searchField;
    private JComboBox<String> categoryFilter;

    private String selectedCategory = "Semua";

    public KasirPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(AppColors.BG_LIGHT);
        initUI();
        loadProducts();
    }

    private void initUI() {
        // Left: product catalog
        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.setBackground(AppColors.BG_LIGHT);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 8));

        // Search & filter bar
        JPanel searchBar = new JPanel(new BorderLayout(8, 0));
        searchBar.setBackground(AppColors.BG_LIGHT);

        searchField = new JTextField();
        searchField.setFont(AppColors.bodyFont(13));
        searchField.setPreferredSize(new Dimension(0, 38));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(AppColors.BORDER, 1, true),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        searchField.putClientProperty("placeholder", "Cari produk...");

        categoryFilter = new JComboBox<>(new String[]{"Semua", "Makanan Ringan", "Minuman", "Sembako"});
        categoryFilter.setFont(AppColors.bodyFont(13));
        categoryFilter.setPreferredSize(new Dimension(140, 38));
        categoryFilter.addActionListener(e -> {
            selectedCategory = (String) categoryFilter.getSelectedItem();
            filterProducts();
        });

        searchBar.add(searchField, BorderLayout.CENTER);
        searchBar.add(categoryFilter, BorderLayout.EAST);

        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { filterProducts(); }
        });

        leftPanel.add(searchBar, BorderLayout.NORTH);

        // Product grid
        productGrid = new JPanel(new GridLayout(0, 3, 10, 10));
        productGrid.setBackground(AppColors.BG_LIGHT);

        JScrollPane gridScroll = new JScrollPane(productGrid);
        gridScroll.setBorder(BorderFactory.createEmptyBorder());
        gridScroll.getViewport().setBackground(AppColors.BG_LIGHT);
        gridScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        leftPanel.add(gridScroll, BorderLayout.CENTER);

        // Right: cart
        JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
        rightPanel.setBackground(AppColors.BG_LIGHT);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(15, 8, 15, 15));
        rightPanel.setPreferredSize(new Dimension(380, 0));

        // Cart header
        JLabel cartTitle = new JLabel("Keranjang Belanja");
        cartTitle.setFont(AppColors.titleFont(16));
        cartTitle.setForeground(AppColors.PRIMARY);
        rightPanel.add(cartTitle, BorderLayout.NORTH);

        // Cart table
        String[] cols = {"Produk", "Harga", "Qty", "Subtotal", "X"};
        cartModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return c == 2 || c == 4; }
            public Class<?> getColumnClass(int c) { return c == 2 ? Integer.class : String.class; }
        };
        JTable cartTable = new JTable(cartModel);
        cartTable.setFont(AppColors.bodyFont(12));
        cartTable.setRowHeight(36);
        cartTable.setShowGrid(false);
        cartTable.setIntercellSpacing(new Dimension(0, 1));
        cartTable.getTableHeader().setFont(AppColors.titleFont(12));
        cartTable.getTableHeader().setBackground(AppColors.PRIMARY);
        cartTable.getTableHeader().setForeground(Color.WHITE);

        // Pastikan teks tabel keranjang terbaca
        cartTable.setForeground(AppColors.TEXT_DARK);
        cartTable.setBackground(Color.WHITE);
        cartTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (sel) {
                    comp.setBackground(new Color(0xE8F0FE));
                    comp.setForeground(AppColors.TEXT_DARK);
                } else {
                    comp.setBackground(r % 2 == 0 ? Color.WHITE : new Color(0xF8F9FA));
                    comp.setForeground(AppColors.TEXT_DARK);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return comp;
            }
        });
        cartTable.getColumnModel().getColumn(0).setPreferredWidth(110);
        cartTable.getColumnModel().getColumn(1).setPreferredWidth(75);
        cartTable.getColumnModel().getColumn(2).setPreferredWidth(45);
        cartTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        cartTable.getColumnModel().getColumn(4).setPreferredWidth(30);

        // Delete button renderer
        cartTable.getColumn("X").setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JButton btn = new JButton("X");
                btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
                btn.setForeground(AppColors.DANGER);
                btn.setBorderPainted(false);
                btn.setContentAreaFilled(false);
                return btn;
            }
        });
        cartTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = cartTable.rowAtPoint(e.getPoint());
                int col = cartTable.columnAtPoint(e.getPoint());
                if (col == 4 && row >= 0) {
                    cart.remove(row);
                    updateCart();
                }
            }
        });

        cartModel.addTableModelListener(e -> {
            if (e.getColumn() == 2 && e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                if (row >= 0 && row < cart.size()) {
                    Object val = cartModel.getValueAt(row, 2);
                    try {
                        int qty = Integer.parseInt(val.toString());
                        if (qty <= 0) { cart.remove(row); }
                        else { cart.get(row).setQty(qty); }
                        SwingUtilities.invokeLater(() -> updateCart());
                    } catch (Exception ex) {}
                }
            }
        });

        JScrollPane tableScroll = new JScrollPane(cartTable);
        tableScroll.setBorder(new LineBorder(AppColors.BORDER, 1, true));
        rightPanel.add(tableScroll, BorderLayout.CENTER);

        // Summary & payment
        JPanel summaryPanel = new JPanel(new GridLayout(0, 1, 0, 6));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(AppColors.BORDER, 1, true),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        qtyLabel = makeSummaryRow(summaryPanel, "Total Item:", "0");
        subtotalLabel = makeSummaryRow(summaryPanel, "Subtotal:", "Rp 0");

        JSeparator sep = new JSeparator();
        summaryPanel.add(sep);

        totalLabel = new JLabel("TOTAL: Rp 0");
        totalLabel.setFont(AppColors.titleFont(18));
        totalLabel.setForeground(AppColors.PRIMARY);
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        summaryPanel.add(totalLabel);

        StyledButton bayarBtn = new StyledButton("BAYAR", AppColors.SUCCESS, AppColors.SUCCESS.darker());
        bayarBtn.setPreferredSize(new Dimension(0, 48));
        bayarBtn.setFont(AppColors.titleFont(15));
        bayarBtn.addActionListener(e -> openPaymentDialog());
        summaryPanel.add(bayarBtn);

        StyledButton clearBtn = new StyledButton("Kosongkan", AppColors.DANGER, AppColors.DANGER.darker());
        clearBtn.setFont(AppColors.bodyFont(12));
        clearBtn.addActionListener(e -> { cart.clear(); updateCart(); });
        summaryPanel.add(clearBtn);

        rightPanel.add(summaryPanel, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        split.setDividerLocation(760);
        split.setDividerSize(4);
        split.setBorder(null);
        add(split, BorderLayout.CENTER);
    }

    private JLabel makeSummaryRow(JPanel parent, String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(label);
        lbl.setFont(AppColors.bodyFont(13));
        lbl.setForeground(AppColors.TEXT_MUTED);
        JLabel val = new JLabel(value, SwingConstants.RIGHT);
        val.setFont(AppColors.titleFont(13));
        val.setForeground(AppColors.TEXT_DARK);
        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);
        parent.add(row);
        return val;
    }

    private void loadProducts() {
        allProducts = productDAO.getAllProducts();
        filterProducts();
    }

    private void filterProducts() {
        String keyword = searchField.getText().trim().toLowerCase();
        productGrid.removeAll();

        for (Product p : allProducts) {
            boolean matchCategory = "Semua".equals(selectedCategory) || p.getCategory().equals(selectedCategory);
            boolean matchKeyword = keyword.isEmpty() || p.getName().toLowerCase().contains(keyword) || p.getCategory().toLowerCase().contains(keyword);
            if (matchCategory && matchKeyword) {
                productGrid.add(createProductCard(p));
            }
        }

        productGrid.revalidate();
        productGrid.repaint();
    }

    private JPanel createProductCard(Product product) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(AppColors.BORDER, 1, true),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Image area
        JPanel imgPanel = new JPanel(new BorderLayout());
        imgPanel.setBackground(new Color(0xF8F9FA));
        imgPanel.setPreferredSize(new Dimension(0, 80));
        imgPanel.setBorder(new LineBorder(AppColors.BORDER, 1, true));

        String emoji = getCategoryEmoji(product.getCategory());
        JLabel imgLabel = new JLabel(emoji, SwingConstants.CENTER);
        imgLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));

        // Try to load product image
        if (product.getImagePath() != null && !product.getImagePath().isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon(product.getImagePath());
                Image img = icon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
                imgLabel.setIcon(new ImageIcon(img));
                imgLabel.setText("");
            } catch (Exception ignored) {}
        }

        imgPanel.add(imgLabel, BorderLayout.CENTER);
        card.add(imgPanel, BorderLayout.NORTH);

        // Info
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 0, 2));
        infoPanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setFont(AppColors.titleFont(12));
        nameLabel.setForeground(AppColors.TEXT_DARK);

        JLabel catLabel = new JLabel(product.getCategory());
        catLabel.setFont(AppColors.bodyFont(10));
        catLabel.setForeground(AppColors.TEXT_MUTED);

        JLabel priceLabel = new JLabel(FormatUtil.formatRupiah(product.getPrice()));
        priceLabel.setFont(AppColors.titleFont(13));
        priceLabel.setForeground(AppColors.ACCENT);

        infoPanel.add(nameLabel);
        infoPanel.add(catLabel);
        infoPanel.add(priceLabel);
        card.add(infoPanel, BorderLayout.CENTER);

        // Add button
        StyledButton addBtn = new StyledButton("+ Tambah", AppColors.PRIMARY, AppColors.PRIMARY.darker());
        addBtn.setFont(AppColors.bodyFont(11));
        addBtn.setPreferredSize(new Dimension(0, 28));
        addBtn.addActionListener(e -> addToCart(product));
        card.add(addBtn, BorderLayout.SOUTH);

        // Click whole card
        card.addMouseListener(new MouseAdapter() {
            Color original = Color.WHITE;
            public void mouseEntered(MouseEvent e) { card.setBackground(new Color(0xF0F4F8)); infoPanel.setBackground(new Color(0xF0F4F8)); }
            public void mouseExited(MouseEvent e) { card.setBackground(original); infoPanel.setBackground(original); }
            public void mouseClicked(MouseEvent e) { addToCart(product); }
        });

        return card;
    }

    private String getCategoryEmoji(String category) {
        return switch (category) {
            case "Makanan Ringan" -> "MR";
            case "Minuman" -> "MIN";
            case "Sembako" -> "SMB";
            default -> "?";
        };
    }

    private void addToCart(Product product) {
        for (CartItem item : cart) {
            if (item.getProduct().getId() == product.getId()) {
                item.setQty(item.getQty() + 1);
                updateCart();
                return;
            }
        }
        cart.add(new CartItem(product, 1));
        updateCart();
    }

    private void updateCart() {
        cartModel.setRowCount(0);
        int totalQty = 0;
        long total = 0;
        for (CartItem item : cart) {
            cartModel.addRow(new Object[]{
                item.getProduct().getName(),
                FormatUtil.formatRupiah(item.getProduct().getPrice()),
                item.getQty(),
                FormatUtil.formatRupiah(item.getSubtotal()),
                "X"
            });
            totalQty += item.getQty();
            total += item.getSubtotal();
        }
        qtyLabel.setText(String.valueOf(totalQty));
        subtotalLabel.setText(FormatUtil.formatRupiah(total));
        totalLabel.setText("TOTAL: " + FormatUtil.formatRupiah(total));
    }

    private void openPaymentDialog() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Keranjang belanja kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        long total = cart.stream().mapToLong(CartItem::getSubtotal).sum();
        int totalQty = cart.stream().mapToInt(CartItem::getQty).sum();

        PaymentDialog dialog = new PaymentDialog((Frame) SwingUtilities.getWindowAncestor(this), total, totalQty, cart, currentUser, transactionDAO);
        dialog.setVisible(true);

        if (dialog.isTransactionCompleted()) {
            cart.clear();
            updateCart();
        }
    }
}
