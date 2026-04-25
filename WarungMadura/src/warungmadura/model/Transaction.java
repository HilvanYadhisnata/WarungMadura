package warungmadura.model;

import java.util.List;

public class Transaction {
    private int id;
    private String transactionCode;
    private String kasir;
    private int totalQty;
    private int subTotal;
    private int total;
    private String paymentMethod;
    private int amountPaid;
    private int changeAmount;
    private String transactionDate;
    private String transactionTime;
    private List<CartItem> items;

    public Transaction() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTransactionCode() { return transactionCode; }
    public void setTransactionCode(String transactionCode) { this.transactionCode = transactionCode; }
    public String getKasir() { return kasir; }
    public void setKasir(String kasir) { this.kasir = kasir; }
    public int getTotalQty() { return totalQty; }
    public void setTotalQty(int totalQty) { this.totalQty = totalQty; }
    public int getSubTotal() { return subTotal; }
    public void setSubTotal(int subTotal) { this.subTotal = subTotal; }
    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public int getAmountPaid() { return amountPaid; }
    public void setAmountPaid(int amountPaid) { this.amountPaid = amountPaid; }
    public int getChangeAmount() { return changeAmount; }
    public void setChangeAmount(int changeAmount) { this.changeAmount = changeAmount; }
    public String getTransactionDate() { return transactionDate; }
    public void setTransactionDate(String transactionDate) { this.transactionDate = transactionDate; }
    public String getTransactionTime() { return transactionTime; }
    public void setTransactionTime(String transactionTime) { this.transactionTime = transactionTime; }
    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }
}
