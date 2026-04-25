package warungmadura;

import warungmadura.view.LoginFrame;
import warungmadura.util.DatabaseUtil;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Gunakan Nimbus agar warna custom (button sidebar, tabel) tidak di-override sistem
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            // Override warna tabel Nimbus agar alternating row dan header tetap konsisten
            UIManager.put("Table.background", java.awt.Color.WHITE);
            UIManager.put("Table.foreground", new java.awt.Color(0x2C3E50));
            UIManager.put("Table.selectionBackground", new java.awt.Color(0xE8F0FE));
            UIManager.put("Table.selectionForeground", new java.awt.Color(0x2C3E50));
            UIManager.put("TableHeader.background", new java.awt.Color(0x1B4F72));
            UIManager.put("TableHeader.foreground", java.awt.Color.WHITE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        DatabaseUtil.initDatabase();

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
