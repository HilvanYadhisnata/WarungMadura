package warungmadura.view;

import warungmadura.util.AppColors;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class StyledButton extends JButton {
    private Color bgColor;
    private Color hoverColor;
    private Color pressColor;
    private Color currentColor;
    private boolean isHover = false;
    private boolean isPress = false;
    private int radius = 10;

    public StyledButton(String text, Color bg, Color hover) {
        super(text);
        this.bgColor = bg;
        this.hoverColor = hover;
        this.pressColor = hover.darker();
        this.currentColor = bg;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setForeground(Color.WHITE);
        setFont(AppColors.titleFont(13));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setOpaque(false);

        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { isHover = true; repaint(); }
            public void mouseExited(MouseEvent e) { isHover = false; isPress = false; repaint(); }
            public void mousePressed(MouseEvent e) { isPress = true; repaint(); }
            public void mouseReleased(MouseEvent e) { isPress = false; repaint(); }
        });
    }

    public StyledButton(String text, Color bg) {
        this(text, bg, bg.darker());
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color c = isPress ? pressColor : (isHover ? hoverColor : bgColor);
        g2.setColor(c);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
        g2.dispose();
        super.paintComponent(g);
    }

    public void setRadius(int r) { this.radius = r; }
}
