package warungmadura.util;

import java.io.*;
import java.util.*;

public class DatabaseUtil {
    public static final String DATA_DIR = "data";
    public static final String USERS_FILE = DATA_DIR + "/users.csv";
    public static final String PRODUCTS_FILE = DATA_DIR + "/products.csv";
    public static final String TRANSACTIONS_FILE = DATA_DIR + "/transactions.csv";
    public static final String ITEMS_FILE = DATA_DIR + "/transaction_items.csv";
    public static final String COUNTER_FILE = DATA_DIR + "/counters.properties";
    public static final String SEP = "|";
    public static final String SEP_REGEX = "\\|";

    public static void initDatabase() {
        new File(DATA_DIR).mkdirs();
        initUsers();
        initProducts();
        File tf = new File(TRANSACTIONS_FILE);
        if (!tf.exists()) writeLines(TRANSACTIONS_FILE, new ArrayList<>());
        File itf = new File(ITEMS_FILE);
        if (!itf.exists()) writeLines(ITEMS_FILE, new ArrayList<>());
    }

    private static void initUsers() {
        File f = new File(USERS_FILE);
        if (!f.exists()) {
            writeLines(USERS_FILE, Arrays.asList(
                "1|admin|admin123|admin",
                "2|kasir1|kasir123|kasir"
            ));
        }
    }

    private static void initProducts() {
        File f = new File(PRODUCTS_FILE);
        if (!f.exists()) {
            writeLines(PRODUCTS_FILE, Arrays.asList(
                "1|Roti Aoka|Makanan Ringan|2000|100|",
                "2|Chitato|Makanan Ringan|5000|100|",
                "3|Biskuit Milkuat|Makanan Ringan|3000|100|",
                "4|Aqua|Minuman|3000|100|",
                "5|Teh Botol Sosro|Minuman|5000|100|",
                "6|Ultramilk|Minuman|7000|100|",
                "7|Indomie|Sembako|4000|100|",
                "8|Minyak Bimoli 2Liter|Sembako|25000|100|",
                "9|Gulaku 1Kg|Sembako|18000|100|"
            ));
            setCounter("product_id", 9);
        }
    }

    public static List<String[]> readAll(String filePath) {
        List<String[]> result = new ArrayList<>();
        File f = new File(filePath);
        if (!f.exists()) return result;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    result.add(line.split(SEP_REGEX, -1));
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return result;
    }

    public static void writeLines(String filePath, List<String> lines) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static synchronized int nextId(String entity) {
        Properties props = loadCounters();
        int current = Integer.parseInt(props.getProperty(entity, "0"));
        int next = current + 1;
        props.setProperty(entity, String.valueOf(next));
        saveCounters(props);
        return next;
    }

    public static void setCounter(String entity, int value) {
        Properties props = loadCounters();
        props.setProperty(entity, String.valueOf(value));
        saveCounters(props);
    }

    private static Properties loadCounters() {
        Properties props = new Properties();
        File f = new File(COUNTER_FILE);
        if (f.exists()) {
            try (FileInputStream fis = new FileInputStream(f)) {
                props.load(fis);
            } catch (IOException e) { e.printStackTrace(); }
        }
        return props;
    }

    private static void saveCounters(Properties props) {
        try (FileOutputStream fos = new FileOutputStream(COUNTER_FILE)) {
            props.store(fos, "ID Counters");
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static String escape(String s) {
        return s == null ? "" : s.replace("|", "_").replace("\n", " ");
    }
}
