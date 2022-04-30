import java.util.Arrays;

public class Tour implements Comparable<Tour> {
    private char[] tour;
    private int distance;

    public Tour(char[] tour) {
        this.tour = tour;
        calculateDistance();
    }

    public void calculateDistance() {
        distance = (int)Main.distances.get(0)[Main.index(tour[0])];

        for (int i = 1; i < tour.length; i++) {
            distance += (int)Main.distances.get(Main.index(tour[i-1]))[Main.index(tour[i])];
        }

        distance += (int)Main.distances.get(Main.index(tour[tour.length-1]))[0];
    }

    public char[] getTour() {
        return tour;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public int compareTo(Tour o) {
        if (this.getDistance() < o.getDistance()) return -1;
        else return 1;
    }

    @Override
    public String toString() {
        return distance + "";
    }
}
