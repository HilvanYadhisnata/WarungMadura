package warungmadura.view;

import warungmadura.controller.TransactionDAO;
import warungmadura.model.CartItem;
import warungmadura.model.Transaction;
import warungmadura.util.AppColors;
import warungmadura.util.FormatUtil;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class HistoryPanel extends JPanel {
    private TransactionDAO transactionDAO = new TransactionDAO();
    private DefaultTableModel tableModel;
    private JTable table;
    private List<Transaction> transactions;

    public HistoryPanel() {
        setLayout(new BorderLayout());
        setBackground(AppColors.BG_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initUI();
    }

    private void initUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setBackground(AppColors.BG_LIGHT);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel title = new JLabel("Riwayat Transaksi");
        title.setFont(AppColors.titleFont(20));
        title.setForeground(AppColors.PRIMARY);
        header.add(title, BorderLayout.WEST);

        StyledButton refreshBtn = new StyledButton("Refresh", AppColors.PRIMARY, AppColors.PRIMARY.darker());
        refreshBtn.addActionListener(e -> refresh());
        header.add(refreshBtn, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Table
        String[] cols = {"Kode", "Kasir", "Tanggal", "Waktu", "Total Item", "Total", "Metode", "Bayar", "Kembali"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(AppColors.bodyFont(12));
        table.setRowHeight(38);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(new Color(0xE8F0FE));
        table.setSelectionForeground(AppColors.TEXT_DARK);
        table.getTableHeader().setFont(AppColors.titleFont(12));
        table.getTableHeader().setBackground(AppColors.PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);

        int[] widths = {130, 80, 90, 70, 80, 110, 90, 110, 90};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel) comp.setBackground(r % 2 == 0 ? Color.WHITE : new Color(0xF8F9FA));
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                // Color total column
                if (c == 5 && !sel) comp.setForeground(AppColors.PRIMARY);
                else if (!sel) comp.setForeground(AppColors.TEXT_DARK);
                return comp;
            }
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) showDetail();
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(AppColors.BORDER, 1, true));
        add(scroll, BorderLayout.CENTER);

        // Footer hint
        JLabel hint = new JLabel("Double-click pada baris untuk melihat detail transaksi");
        hint.setFont(AppColors.bodyFont(11));
        hint.setForeground(AppColors.TEXT_MUTED);
        hint.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        add(hint, BorderLayout.SOUTH);
    }

    public void refresh() {
        transactions = transactionDAO.getAllTransactions();
        tableModel.setRowCount(0);
        for (Transaction t : transactions) {
            tableModel.addRow(new Object[]{
                t.getTransactionCode(),
                t.getKasir(),
                t.getTransactionDate(),
                t.getTransactionTime(),
                t.getTotalQty() + " item",
                FormatUtil.formatRupiah(t.getTotal()),
                t.getPaymentMethod(),
                FormatUtil.formatRupiah(t.getAmountPaid()),
                FormatUtil.formatRupiah(t.getChangeAmount())
            });
        }
    }

    private void showDetail() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        Transaction t = transactions.get(row);
        List<CartItem> items = transactionDAO.getTransactionItems(t.getTransactionCode());

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Detail Transaksi", true);
        dialog.setSize(480, 520);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel titleLabel = new JLabel("Detail: " + t.getTransactionCode());
        titleLabel.setFont(AppColors.titleFont(16));
        titleLabel.setForeground(AppColors.PRIMARY);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Info grid
        JPanel infoGrid = new JPanel(new GridLayout(0, 2, 8, 6));
        infoGrid.setBackground(new Color(0xF8F9FA));
        infoGrid.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(AppColors.BORDER, 1, true),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        addInfoRow(infoGrid, "Kasir:", t.getKasir());
        addInfoRow(infoGrid, "Tanggal:", t.getTransactionDate());
        addInfoRow(infoGrid, "Waktu:", t.getTransactionTime());
        addInfoRow(infoGrid, "Metode Bayar:", t.getPaymentMethod());
        addInfoRow(infoGrid, "Jumlah Bayar:", FormatUtil.formatRupiah(t.getAmountPaid()));
        addInfoRow(infoGrid, "Kembalian:", FormatUtil.formatRupiah(t.getChangeAmount()));

        // Items table
        String[] cols = {"Produk", "Harga", "Qty", "Subtotal"};
        DefaultTableModel itemModel = new DefaultTableModel(cols, 0);
        for (CartItem item : items) {
            itemModel.addRow(new Object[]{
                item.getProduct().getName(),
                FormatUtil.formatRupiah(item.getProduct().getPrice()),
                item.getQty(),
                FormatUtil.formatRupiah(item.getSubtotal())
            });
        }
        JTable itemTable = new JTable(itemModel);
        itemTable.setFont(AppColors.bodyFont(12));
        itemTable.setRowHeight(32);
        itemTable.setShowGrid(false);
        itemTable.getTableHeader().setFont(AppColors.titleFont(12));
        itemTable.getTableHeader().setBackground(AppColors.PRIMARY);
        itemTable.getTableHeader().setForeground(Color.WHITE);

        JLabel totalLbl = new JLabel("TOTAL: " + FormatUtil.formatRupiah(t.getTotal()), SwingConstants.RIGHT);
        totalLbl.setFont(AppColors.titleFont(16));
        totalLbl.setForeground(AppColors.PRIMARY);
        totalLbl.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        JPanel centerPanel = new JPanel(new BorderLayout(0, 8));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(infoGrid, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(itemTable), BorderLayout.CENTER);
        centerPanel.add(totalLbl, BorderLayout.SOUTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        StyledButton closeBtn = new StyledButton("Tutup", AppColors.PRIMARY, AppColors.PRIMARY.darker());
        closeBtn.addActionListener(e -> dialog.dispose());
        panel.add(closeBtn, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void addInfoRow(JPanel panel, String label, String value) {
        JLabel l = new JLabel(label);
        l.setFont(AppColors.bodyFont(12));
        l.setForeground(AppColors.TEXT_MUTED);
        JLabel v = new JLabel(value);
        v.setFont(AppColors.titleFont(12));
        v.setForeground(AppColors.TEXT_DARK);
        panel.add(l);
        panel.add(v);
    }
}
