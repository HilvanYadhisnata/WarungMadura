package warungmadura.controller;

import warungmadura.model.*;
import warungmadura.util.DatabaseUtil;
import java.util.*;

public class TransactionDAO {

    public boolean saveTransaction(Transaction t) {
        // Save transaction header
        List<String> tLines = new ArrayList<>();
        for (String[] row : DatabaseUtil.readAll(DatabaseUtil.TRANSACTIONS_FILE)) {
            tLines.add(String.join(DatabaseUtil.SEP, row));
        }
        String tLine = t.getTransactionCode() + DatabaseUtil.SEP +
            DatabaseUtil.escape(t.getKasir()) + DatabaseUtil.SEP +
            t.getTotalQty() + DatabaseUtil.SEP +
            t.getSubTotal() + DatabaseUtil.SEP +
            t.getTotal() + DatabaseUtil.SEP +
            DatabaseUtil.escape(t.getPaymentMethod()) + DatabaseUtil.SEP +
            t.getAmountPaid() + DatabaseUtil.SEP +
            t.getChangeAmount() + DatabaseUtil.SEP +
            t.getTransactionDate() + DatabaseUtil.SEP +
            t.getTransactionTime();
        tLines.add(tLine);
        DatabaseUtil.writeLines(DatabaseUtil.TRANSACTIONS_FILE, tLines);

        // Save transaction items
        List<String> iLines = new ArrayList<>();
        for (String[] row : DatabaseUtil.readAll(DatabaseUtil.ITEMS_FILE)) {
            iLines.add(String.join(DatabaseUtil.SEP, row));
        }
        for (CartItem item : t.getItems()) {
            iLines.add(t.getTransactionCode() + DatabaseUtil.SEP +
                DatabaseUtil.escape(item.getProduct().getName()) + DatabaseUtil.SEP +
                item.getProduct().getPrice() + DatabaseUtil.SEP +
                item.getQty() + DatabaseUtil.SEP +
                item.getSubtotal());
        }
        DatabaseUtil.writeLines(DatabaseUtil.ITEMS_FILE, iLines);
        return true;
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        List<String[]> rows = DatabaseUtil.readAll(DatabaseUtil.TRANSACTIONS_FILE);
        Collections.reverse(rows); // most recent first
        for (String[] row : rows) {
            if (row.length < 10) continue;
            Transaction t = new Transaction();
            t.setTransactionCode(row[0]);
            t.setKasir(row[1]);
            t.setTotalQty(parseInt(row[2]));
            t.setSubTotal(parseInt(row[3]));
            t.setTotal(parseInt(row[4]));
            t.setPaymentMethod(row[5]);
            t.setAmountPaid(parseInt(row[6]));
            t.setChangeAmount(parseInt(row[7]));
            t.setTransactionDate(row[8]);
            t.setTransactionTime(row[9]);
            list.add(t);
        }
        return list;
    }

    public List<CartItem> getTransactionItems(String code) {
        List<CartItem> items = new ArrayList<>();
        for (String[] row : DatabaseUtil.readAll(DatabaseUtil.ITEMS_FILE)) {
            if (row.length >= 5 && row[0].equals(code)) {
                Product p = new Product();
                p.setName(row[1]);
                p.setPrice(parseInt(row[2]));
                items.add(new CartItem(p, parseInt(row[3])));
            }
        }
        return items;
    }

    private int parseInt(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }
}
