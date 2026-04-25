package warungmadura.controller;

import warungmadura.model.Product;
import warungmadura.util.DatabaseUtil;
import java.util.*;

public class ProductDAO {

    private Product parseProduct(String[] cols) {
        if (cols.length < 5) return null;
        try {
            Product p = new Product();
            p.setId(Integer.parseInt(cols[0].trim()));
            p.setName(cols[1]);
            p.setCategory(cols[2]);
            p.setPrice(Integer.parseInt(cols[3].trim()));
            p.setStock(Integer.parseInt(cols[4].trim()));
            p.setImagePath(cols.length > 5 ? cols[5] : "");
            return p;
        } catch (Exception e) { return null; }
    }

    private String toLine(Product p) {
        return p.getId() + DatabaseUtil.SEP +
               DatabaseUtil.escape(p.getName()) + DatabaseUtil.SEP +
               DatabaseUtil.escape(p.getCategory()) + DatabaseUtil.SEP +
               p.getPrice() + DatabaseUtil.SEP +
               p.getStock() + DatabaseUtil.SEP +
               DatabaseUtil.escape(p.getImagePath() == null ? "" : p.getImagePath());
    }

    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        for (String[] row : DatabaseUtil.readAll(DatabaseUtil.PRODUCTS_FILE)) {
            Product p = parseProduct(row);
            if (p != null) list.add(p);
        }
        list.sort(Comparator.comparing(Product::getCategory).thenComparing(Product::getName));
        return list;
    }

    public List<Product> searchProducts(String keyword) {
        String kw = keyword.toLowerCase();
        List<Product> list = new ArrayList<>();
        for (Product p : getAllProducts()) {
            if (p.getName().toLowerCase().contains(kw) || p.getCategory().toLowerCase().contains(kw)) {
                list.add(p);
            }
        }
        return list;
    }

    public boolean addProduct(Product p) {
        int id = DatabaseUtil.nextId("product_id");
        p.setId(id);
        List<String> lines = new ArrayList<>();
        for (String[] row : DatabaseUtil.readAll(DatabaseUtil.PRODUCTS_FILE)) {
            lines.add(String.join(DatabaseUtil.SEP, row));
        }
        lines.add(toLine(p));
        DatabaseUtil.writeLines(DatabaseUtil.PRODUCTS_FILE, lines);
        return true;
    }

    public boolean updateProduct(Product p) {
        List<String> lines = new ArrayList<>();
        for (String[] row : DatabaseUtil.readAll(DatabaseUtil.PRODUCTS_FILE)) {
            if (row.length > 0 && row[0].trim().equals(String.valueOf(p.getId()))) {
                lines.add(toLine(p));
            } else {
                lines.add(String.join(DatabaseUtil.SEP, row));
            }
        }
        DatabaseUtil.writeLines(DatabaseUtil.PRODUCTS_FILE, lines);
        return true;
    }

    public boolean deleteProduct(int id) {
        List<String> lines = new ArrayList<>();
        for (String[] row : DatabaseUtil.readAll(DatabaseUtil.PRODUCTS_FILE)) {
            if (row.length == 0 || !row[0].trim().equals(String.valueOf(id))) {
                lines.add(String.join(DatabaseUtil.SEP, row));
            }
        }
        DatabaseUtil.writeLines(DatabaseUtil.PRODUCTS_FILE, lines);
        return true;
    }
}
