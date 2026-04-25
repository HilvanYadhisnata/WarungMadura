package warungmadura.util;

import java.text.NumberFormat;
import java.util.Locale;

public class FormatUtil {
    private static final Locale LOCALE_ID = new Locale("id", "ID");

    public static String formatRupiah(long amount) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(LOCALE_ID);
        nf.setMaximumFractionDigits(0);
        return nf.format(amount).replace("IDR", "Rp").replace("\u00a0", "").replace(",00","").trim();
    }

    public static String formatNumber(long number) {
        return NumberFormat.getNumberInstance(LOCALE_ID).format(number);
    }
}
