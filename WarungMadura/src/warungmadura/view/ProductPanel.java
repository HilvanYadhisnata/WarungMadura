package warungmadura.view;

import warungmadura.controller.ProductDAO;
import warungmadura.model.Product;
import warungmadura.util.AppColors;
import warungmadura.util.FormatUtil;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;

public class ProductPanel extends JPanel {
    private ProductDAO productDAO = new ProductDAO();
    private DefaultTableModel tableModel;
    private JTable table;
    private List<Product> products;

    public ProductPanel() {
        setLayout(new BorderLayout());
        setBackground(AppColors.BG_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initUI();
        refresh();
    }

    private void initUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setBackground(AppColors.BG_LIGHT);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel title = new JLabel("Manajemen Produk");
        title.setFont(AppColors.titleFont(20));
        title.setForeground(AppColors.PRIMARY);
        header.add(title, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setBackground(AppColors.BG_LIGHT);

        StyledButton addBtn = new StyledButton("+ Tambah Produk", AppColors.SUCCESS, AppColors.SUCCESS.darker());
        addBtn.addActionListener(e -> showProductForm(null));

        StyledButton editBtn = new StyledButton("Edit", AppColors.PRIMARY, AppColors.PRIMARY.darker());
        editBtn.addActionListener(e -> editSelected());

        StyledButton delBtn = new StyledButton("Hapus", AppColors.DANGER, AppColors.DANGER.darker());
        delBtn.addActionListener(e -> deleteSelected());

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(delBtn);
        header.add(btnPanel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "Nama Produk", "Kategori", "Harga", "Stok", "Gambar"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(AppColors.bodyFont(13));
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(new Color(0xE8F0FE));
        table.setSelectionForeground(AppColors.TEXT_DARK);
        table.getTableHeader().setFont(AppColors.titleFont(13));
        table.getTableHeader().setBackground(AppColors.PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(70);
        table.getColumnModel().getColumn(5).setPreferredWidth(200);

        // Alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) editSelected();
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(AppColors.BORDER, 1, true));
        add(scroll, BorderLayout.CENTER);
    }

    public void refresh() {
        products = productDAO.getAllProducts();
        tableModel.setRowCount(0);
        for (Product p : products) {
            tableModel.addRow(new Object[]{
                p.getId(), p.getName(), p.getCategory(),
                FormatUtil.formatRupiah(p.getPrice()),
                p.getStock(),
                p.getImagePath() != null && !p.getImagePath().isEmpty() ? p.getImagePath() : "(tidak ada)"
            });
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih produk terlebih dahulu!", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        showProductForm(products.get(row));
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih produk terlebih dahulu!", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Product p = products.get(row);
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus produk \"" + p.getName() + "\"?", "Konfirmasi", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (productDAO.deleteProduct(p.getId())) {
                refresh();
                JOptionPane.showMessageDialog(this, "Produk berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void showProductForm(Product existing) {
        boolean isEdit = existing != null;
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), isEdit ? "Edit Produk" : "Tambah Produk", true);
        dialog.setSize(440, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.insets = new Insets(5, 0, 5, 0);

        JTextField nameField = new JTextField(isEdit ? existing.getName() : "");
        JComboBox<String> catCombo = new JComboBox<>(new String[]{"Makanan Ringan", "Minuman", "Sembako"});
        JTextField priceField = new JTextField(isEdit ? String.valueOf(existing.getPrice()) : "");
        JTextField stockField = new JTextField(isEdit ? String.valueOf(existing.getStock()) : "100");
        JTextField imgField = new JTextField(isEdit ? existing.getImagePath() : "");

        if (isEdit) catCombo.setSelectedItem(existing.getCategory());

        addFormRow(panel, gbc, 0, "Nama Produk", nameField);
        addFormRow(panel, gbc, 2, "Kategori", catCombo);
        addFormRow(panel, gbc, 4, "Harga (Rp)", priceField);
        addFormRow(panel, gbc, 6, "Stok", stockField);

        gbc.gridy = 8;
        JLabel imgLbl = new JLabel("Path Gambar (opsional)");
        imgLbl.setFont(AppColors.titleFont(12));
        panel.add(imgLbl, gbc);

        gbc.gridy = 9;
        JPanel imgPanel = new JPanel(new BorderLayout(5, 0));
        imgPanel.setBackground(Color.WHITE);
        imgField.setFont(AppColors.bodyFont(12));
        imgField.setPreferredSize(new Dimension(0, 36));
        imgField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(AppColors.BORDER, 1, true),
            BorderFactory.createEmptyBorder(0, 8, 0, 8)
        ));
        StyledButton browseBtn = new StyledButton("Browse", AppColors.PRIMARY, AppColors.PRIMARY.darker());
        browseBtn.setPreferredSize(new Dimension(80, 36));
        browseBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));
            if (chooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                imgField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });
        imgPanel.add(imgField, BorderLayout.CENTER);
        imgPanel.add(browseBtn, BorderLayout.EAST);
        panel.add(imgPanel, gbc);

        gbc.gridy = 10;
        gbc.insets = new Insets(15, 0, 0, 0);
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setBackground(Color.WHITE);

        StyledButton saveBtn = new StyledButton(isEdit ? "Simpan" : "Tambah", AppColors.SUCCESS, AppColors.SUCCESS.darker());
        saveBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String cat = (String) catCombo.getSelectedItem();
                int price = Integer.parseInt(priceField.getText().trim().replaceAll("[^0-9]", ""));
                int stock = Integer.parseInt(stockField.getText().trim().replaceAll("[^0-9]", ""));
                String img = imgField.getText().trim();

                if (name.isEmpty()) { JOptionPane.showMessageDialog(dialog, "Nama tidak boleh kosong!"); return; }

                Product p = isEdit ? existing : new Product();
                p.setName(name); p.setCategory(cat); p.setPrice(price); p.setStock(stock); p.setImagePath(img);

                boolean success = isEdit ? productDAO.updateProduct(p) : productDAO.addProduct(p);
                if (success) {
                    refresh(); dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Produk berhasil " + (isEdit ? "diperbarui" : "ditambahkan") + "!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Harga dan stok harus berupa angka!");
            }
        });

        StyledButton cancelBtn = new StyledButton("Batal", AppColors.TEXT_MUTED, AppColors.TEXT_MUTED.darker());
        cancelBtn.addActionListener(e -> dialog.dispose());

        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);
        panel.add(btnPanel, gbc);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int rowStart, String label, JComponent field) {
        gbc.gridy = rowStart;
        JLabel lbl = new JLabel(label);
        lbl.setFont(AppColors.titleFont(12));
        panel.add(lbl, gbc);

        gbc.gridy = rowStart + 1;
        if (field instanceof JTextField) {
            JTextField tf = (JTextField) field;
            tf.setFont(AppColors.bodyFont(13));
            tf.setPreferredSize(new Dimension(0, 38));
            tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppColors.BORDER, 1, true),
                BorderFactory.createEmptyBorder(0, 8, 0, 8)
            ));
        } else if (field instanceof JComboBox) {
            ((JComboBox<?>) field).setFont(AppColors.bodyFont(13));
            field.setPreferredSize(new Dimension(0, 38));
        }
        panel.add(field, gbc);
    }
}
