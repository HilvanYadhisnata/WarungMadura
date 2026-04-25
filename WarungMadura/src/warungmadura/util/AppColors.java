package warungmadura.util;

import java.awt.*;

public class AppColors {
    public static final Color PRIMARY      = new Color(0x1B4F72);   // Navy deep
    public static final Color PRIMARY_DARK = new Color(0x0D2B45);
    public static final Color ACCENT       = new Color(0xE67E22);   // Warm orange
    public static final Color ACCENT_DARK  = new Color(0xCA6F1E);
    public static final Color SUCCESS      = new Color(0x27AE60);
    public static final Color DANGER       = new Color(0xE74C3C);
    public static final Color WARNING      = new Color(0xF39C12);
    public static final Color BG_LIGHT     = new Color(0xF5F6FA);
    public static final Color BG_CARD      = Color.WHITE;
    public static final Color TEXT_DARK    = new Color(0x2C3E50);
    public static final Color TEXT_MUTED   = new Color(0x7F8C8D);
    public static final Color BORDER       = new Color(0xDDE1E7);

    public static Font titleFont(float size) {
        return new Font("Segoe UI", Font.BOLD, (int) size);
    }
    public static Font bodyFont(float size) {
        return new Font("Segoe UI", Font.PLAIN, (int) size);
    }
    public static Font monoFont(float size) {
        return new Font("Consolas", Font.PLAIN, (int) size);
    }
}
