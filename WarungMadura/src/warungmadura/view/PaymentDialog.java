package warungmadura.view;

import warungmadura.controller.TransactionDAO;
import warungmadura.model.*;
import warungmadura.util.AppColors;
import warungmadura.util.FormatUtil;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.List;

public class PaymentDialog extends JDialog {
    private long total;
    private int totalQty;
    private List<CartItem> cart;
    private User kasir;
    private TransactionDAO transactionDAO;
    private boolean transactionCompleted = false;

    private JComboBox<String> methodCombo;
    private JTextField amountField;
    private JLabel changeLabel;

    private static final String WARUNG_NAME = "WARUNG MADURA";
    private static final String LOCATION = "Jl. Maduran No. 17, Karawang";
    private static final String PHONE = "0812-3456-7890";

    public PaymentDialog(Frame parent, long total, int totalQty, List<CartItem> cart, User kasir, TransactionDAO dao) {
        super(parent, "Pembayaran", true);
        this.total = total;
        this.totalQty = totalQty;
        this.cart = new ArrayList<>(cart);
        this.kasir = kasir;
        this.transactionDAO = dao;
        setSize(450, 520);
        setLocationRelativeTo(parent);
        setResizable(false);
        initUI();
    }

    private void initUI() {
        JPanel main = new JPanel(new BorderLayout(0, 0));
        main.setBackground(Color.WHITE);
        main.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel("Proses Pembayaran");
        title.setFont(AppColors.titleFont(18));
        title.setForeground(AppColors.PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        main.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.gridx = 0;

        // Summary
        int row = 0;
        gbc.gridy = row++;
        JPanel summaryPanel = new JPanel(new GridLayout(0, 1, 0, 4));
        summaryPanel.setBackground(new Color(0xF8F9FA));
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(AppColors.BORDER, 1, true),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        // Cart items summary
        for (CartItem item : cart) {
            JPanel row2 = new JPanel(new BorderLayout());
            row2.setBackground(new Color(0xF8F9FA));
            JLabel itemName = new JLabel(item.getProduct().getName() + " x" + item.getQty());
            itemName.setFont(AppColors.bodyFont(12));
            JLabel itemPrice = new JLabel(FormatUtil.formatRupiah(item.getSubtotal()), SwingConstants.RIGHT);
            itemPrice.setFont(AppColors.bodyFont(12));
            row2.add(itemName, BorderLayout.WEST);
            row2.add(itemPrice, BorderLayout.EAST);
            summaryPanel.add(row2);
        }

        JSeparator sep = new JSeparator();
        summaryPanel.add(sep);

        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setBackground(new Color(0xF8F9FA));
        JLabel totalLbl = new JLabel("TOTAL (" + totalQty + " item)");
        totalLbl.setFont(AppColors.titleFont(14));
        totalLbl.setForeground(AppColors.PRIMARY);
        JLabel totalVal = new JLabel(FormatUtil.formatRupiah(total), SwingConstants.RIGHT);
        totalVal.setFont(AppColors.titleFont(14));
        totalVal.setForeground(AppColors.PRIMARY);
        totalRow.add(totalLbl, BorderLayout.WEST);
        totalRow.add(totalVal, BorderLayout.EAST);
        summaryPanel.add(totalRow);
        form.add(summaryPanel, gbc);

        // Payment method
        gbc.gridy = row++;
        gbc.insets = new Insets(12, 0, 4, 0);
        JLabel methodLabel = new JLabel("Metode Pembayaran");
        methodLabel.setFont(AppColors.titleFont(13));
        methodLabel.setForeground(AppColors.TEXT_DARK);
        form.add(methodLabel, gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 8, 0);
        methodCombo = new JComboBox<>(new String[]{"Tunai", "QRIS", "Transfer Bank", "Kartu Debit"});
        methodCombo.setFont(AppColors.bodyFont(13));
        methodCombo.setPreferredSize(new Dimension(0, 40));
        form.add(methodCombo, gbc);

        // Amount paid
        gbc.gridy = row++;
        gbc.insets = new Insets(4, 0, 4, 0);
        JLabel amountLabel = new JLabel("Jumlah Bayar (Rp)");
        amountLabel.setFont(AppColors.titleFont(13));
        amountLabel.setForeground(AppColors.TEXT_DARK);
        form.add(amountLabel, gbc);

        gbc.gridy = row++;
        amountField = new JTextField(String.valueOf(total));
        amountField.setFont(AppColors.titleFont(15));
        amountField.setPreferredSize(new Dimension(0, 44));
        amountField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(AppColors.ACCENT, 2, true),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        amountField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { updateChange(); }
        });
        form.add(amountField, gbc);

        // Kembalian
        gbc.gridy = row++;
        gbc.insets = new Insets(8, 0, 4, 0);
        JLabel changeTitle = new JLabel("Kembalian");
        changeTitle.setFont(AppColors.titleFont(13));
        changeTitle.setForeground(AppColors.TEXT_DARK);
        form.add(changeTitle, gbc);

        gbc.gridy = row++;
        changeLabel = new JLabel("Rp 0", SwingConstants.RIGHT);
        changeLabel.setFont(AppColors.titleFont(20));
        changeLabel.setForeground(AppColors.SUCCESS);
        changeLabel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(AppColors.SUCCESS, 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        changeLabel.setOpaque(true);
        changeLabel.setBackground(new Color(0xEAF7F0));
        form.add(changeLabel, gbc);

        main.add(form, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        StyledButton cancelBtn = new StyledButton("Batal", AppColors.TEXT_MUTED, AppColors.TEXT_MUTED.darker());
        cancelBtn.addActionListener(e -> dispose());

        StyledButton confirmBtn = new StyledButton("Bayar & Cetak Struk", AppColors.SUCCESS, AppColors.SUCCESS.darker());
        confirmBtn.addActionListener(e -> processPayment());

        btnPanel.add(cancelBtn);
        btnPanel.add(confirmBtn);
        main.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(main);
        updateChange();
    }

    private void updateChange() {
        try {
            long paid = Long.parseLong(amountField.getText().trim().replaceAll("[^0-9]", ""));
            long change = paid - total;
            if (change < 0) {
                changeLabel.setText("Kurang " + FormatUtil.formatRupiah(Math.abs(change)));
                changeLabel.setForeground(AppColors.DANGER);
                changeLabel.setBackground(new Color(0xFDE8E8));
            } else {
                changeLabel.setText(FormatUtil.formatRupiah(change));
                changeLabel.setForeground(AppColors.SUCCESS);
                changeLabel.setBackground(new Color(0xEAF7F0));
            }
        } catch (Exception e) {
            changeLabel.setText("Rp 0");
        }
    }

    private void processPayment() {
        try {
            long paid = Long.parseLong(amountField.getText().trim().replaceAll("[^0-9]", ""));
            if (paid < total) {
                JOptionPane.showMessageDialog(this, "Jumlah bayar kurang dari total!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String method = (String) methodCombo.getSelectedItem();
            long change = paid - total;

            // Build transaction
            LocalDateTime now = LocalDateTime.now();
            String code = "TRX" + now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String date = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String time = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            Transaction t = new Transaction();
            t.setTransactionCode(code);
            t.setKasir(kasir.getUsername());
            t.setTotalQty(totalQty);
            t.setSubTotal((int) total);
            t.setTotal((int) total);
            t.setPaymentMethod(method);
            t.setAmountPaid((int) paid);
            t.setChangeAmount((int) change);
            t.setTransactionDate(date);
            t.setTransactionTime(time);
            t.setItems(cart);

            transactionDAO.saveTransaction(t);
            transactionCompleted = true;

            printReceipt(t, paid, change);
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Masukkan jumlah bayar yang valid!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void printReceipt(Transaction t, long paid, long change) {
        String receipt = buildReceiptText(t, paid, change);

        JDialog receiptDialog = new JDialog((Frame) null, "Struk Pembelian", true);
        receiptDialog.setSize(400, 600);
        receiptDialog.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextArea receiptArea = new JTextArea(receipt);
        receiptArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        receiptArea.setEditable(false);
        receiptArea.setBackground(Color.WHITE);
        receiptArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JScrollPane scroll = new JScrollPane(receiptArea);
        scroll.setBorder(new LineBorder(AppColors.BORDER, 1));
        panel.add(scroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        StyledButton printBtn = new StyledButton("Print", AppColors.PRIMARY, AppColors.PRIMARY.darker());
        printBtn.addActionListener(e -> {
            try { receiptArea.print(); } catch (Exception ex) { ex.printStackTrace(); }
        });

        StyledButton closeBtn = new StyledButton("Tutup", AppColors.TEXT_MUTED, AppColors.TEXT_MUTED.darker());
        closeBtn.addActionListener(e -> receiptDialog.dispose());

        btnPanel.add(printBtn);
        btnPanel.add(closeBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        receiptDialog.setContentPane(panel);
        receiptDialog.setVisible(true);
    }

    private String buildReceiptText(Transaction t, long paid, long change) {
        int width = 48;
        String line = "-".repeat(width);
        StringBuilder sb = new StringBuilder();

        sb.append(center(WARUNG_NAME, width)).append("\n");
        sb.append(center(LOCATION, width)).append("\n");
        sb.append(center(PHONE, width)).append("\n");
        sb.append(line).append("\n");
        sb.append(padRight(t.getTransactionDate(), 24)).append(padLeft(t.getKasir(), 24)).append("\n");
        sb.append(padRight(t.getTransactionTime(), 24)).append(padLeft(t.getTransactionCode(), 24)).append("\n");
        sb.append(line).append("\n");

        for (CartItem item : t.getItems()) {
            sb.append(item.getProduct().getName()).append("\n");
            String qty = item.getQty() + " x " + FormatUtil.formatRupiah(item.getProduct().getPrice());
            String sub = FormatUtil.formatRupiah(item.getSubtotal());
            sb.append(padRight("  " + qty, 28)).append(padLeft(sub, 20)).append("\n");
        }

        sb.append(line).append("\n");
        sb.append(padRight("Total QTY:", 28)).append(padLeft(String.valueOf(t.getTotalQty()), 20)).append("\n");
        sb.append(padRight("Sub Total", 28)).append(padLeft(FormatUtil.formatRupiah(t.getSubTotal()), 20)).append("\n");
        sb.append(padRight("Total", 28)).append(padLeft(FormatUtil.formatRupiah(t.getTotal()), 20)).append("\n");
        sb.append(padRight("Bayar (" + t.getPaymentMethod() + ")", 28)).append(padLeft(FormatUtil.formatRupiah(paid), 20)).append("\n");
        sb.append(padRight("Kembali", 28)).append(padLeft(FormatUtil.formatRupiah(change), 20)).append("\n");
        sb.append(line).append("\n");
        sb.append(center("Terimakasih Telah Berbelanja", width)).append("\n");
        sb.append(center("Di " + WARUNG_NAME, width)).append("\n");

        return sb.toString();
    }

    private String center(String s, int width) {
        if (s.length() >= width) return s;
        int padding = (width - s.length()) / 2;
        return " ".repeat(padding) + s;
    }

    private String padRight(String s, int width) {
        if (s.length() >= width) return s.substring(0, width);
        return s + " ".repeat(width - s.length());
    }

    private String padLeft(String s, int width) {
        if (s.length() >= width) return s;
        return " ".repeat(width - s.length()) + s;
    }

    public boolean isTransactionCompleted() { return transactionCompleted; }
}
