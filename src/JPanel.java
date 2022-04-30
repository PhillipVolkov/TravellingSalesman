import java.awt.*;
import java.util.*;

public class JPanel extends javax.swing.JPanel {
    final static int size = 30;
    final static int margin = 500;
    final static double spacing = 3;
    final static int yFlip = -1;

    static HashMap<Character, Double[]> grid = new HashMap<>();
    static char[] tour = null;

    static ArrayList<Integer[]> circles = new ArrayList<>();

    public JPanel() {}

    public void updateGrid(HashMap<Character, Double[]> grid) {
        this.grid = grid;
    }

    public void updateTour(char[] tour) {
        this.tour = tour;
    }

    public void clearCircles() {
        circles = new ArrayList<>();
    }

    public void drawCircle(double x, double y, double radius) {
        circles.add(new Integer[] {(int)x, (int)y, (int)radius});
    }

    @Override
    public void paint(Graphics g) {
        g.clearRect(0, 0, this.getWidth(), this.getHeight());

        if (tour != null) {
            g.setColor(Color.BLACK);
            g.drawLine((int)(grid.get('x')[0]*spacing+margin), (int)(grid.get('x')[1]*yFlip*spacing+margin),
                    (int)(grid.get(tour[0])[0]*spacing+margin), (int)(grid.get(tour[0])[1]*yFlip*spacing+margin));
            for (int i = 1; i < tour.length; i++) {
                g.drawLine((int)(grid.get(tour[i-1])[0]*spacing+margin), (int)(grid.get(tour[i-1])[1]*yFlip*spacing+margin),
                        (int)(grid.get(tour[i])[0]*spacing+margin), (int)(grid.get(tour[i])[1]*yFlip*spacing+margin));
            }

            g.drawLine((int)(grid.get(tour[tour.length-1])[0]*spacing+margin), (int)(grid.get(tour[tour.length-1])[1]*yFlip*spacing+margin),
                    (int)(grid.get('x')[0]*spacing+margin), (int)(grid.get('x')[1]*yFlip*spacing+margin));
        }

        for (Character key : grid.keySet()) {
            int x = (int)(grid.get(key)[0]*spacing+margin)-size/2;
            int y = (int)(grid.get(key)[1]*yFlip*spacing+margin)-size/2;
            String s = String.valueOf(key);

            g.setColor(Color.WHITE);
            g.fillArc(x, y, size, size, 0, 360);

            g.setColor(Color.BLACK);
            g.drawArc(x, y, size, size, 0, 360);
            g.drawString(s, (int)(x+(size*1.0-g.getFontMetrics().stringWidth(s))/2), (int)(y+size/2.0+g.getFontMetrics().getDescent()));
        }

        g.setColor(Color.RED);
        for (Integer[] circle : circles) {
            int circSize = (int)(circle[2]*2*spacing);
            int x = (int)(circle[0]*spacing)+margin - circSize/2;
            int y = (int)(circle[1]*yFlip*spacing)+margin - circSize/2;

            g.drawArc(x, y, circSize, circSize, 0, 360);
        }
    }
}
