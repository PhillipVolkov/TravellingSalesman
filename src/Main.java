import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class Main {
    static ArrayList<double[]> distances = new ArrayList<>();
    static HashMap<Character, Double[]> grid = new HashMap<>();
    static JFrame frame = new JFrame();
    static JPanel panel = new JPanel();

    static final double percentageTop = 0.05;
    static final double percentageRandom = 0.15;
    static final int totalPop = 250;
    static final double mutationRate = 0.85;
    static final int maxMutations = 1;

    static final int numTrials = 1000;

    public static void main(String[] args) {
        frame.setContentPane(panel);
        frame.setSize(1000, 1000);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        read();
        makeGrid();
        draw();

        Tour bestTour = null;
        double averageDistance = 0;
        for (int i = 0; i < numTrials; i++) {
            Tour currTour = geneticAlgorithm();

            if (bestTour == null || currTour.getDistance() < bestTour.getDistance()) bestTour = currTour;

            System.out.println("GEN " + i + " : " + currTour.getDistance());
            averageDistance += currTour.getDistance();
        }
        averageDistance /= numTrials;

        System.out.println("\n*-----------------------------------------------------*");
        System.out.println("BEST TOUR: \t\t" + bestTour);
        System.out.println("AVERAGE TOUR: \t" + averageDistance);
        System.out.println("*-----------------------------------------------------*");
        panel.updateTour(bestTour.getTour());
        frame.repaint();
    }

    public static Tour geneticAlgorithm() {
        //INIT
        Tour[] currentGen = new Tour[totalPop];
        for (int i = 0; i < currentGen.length; i++) currentGen[i] = new Tour(randomTour());

        boolean found = false;
        int[] prev = new int[5];

        while (!found) {
            //
            //SELECTION
            //
            Arrays.sort(currentGen);
            Tour[] nextGen = new Tour[currentGen.length];
            //top 5
            for (int i = 0; i < percentageTop*totalPop; i++) {
                nextGen[i] = currentGen[i];
                currentGen[i] = null;
            }
            //random
            for (int i = (int)(percentageTop*totalPop); i < (percentageTop*totalPop)+(percentageRandom*totalPop); i++) {
                Random rand = new Random();

                int random = 0;
                do {
                    random = rand.nextInt(currentGen.length);
                }
                while(currentGen[random] == null);

                nextGen[i] = currentGen[random];
                currentGen[random] = null;
            }


            //
            //CROSSOVER
            //
            for (int i = (int)((percentageTop*totalPop)+(percentageRandom*totalPop)); i < currentGen.length; i++) {
                //random two parents
                Random rand = new Random();

                int parent1Num = rand.nextInt(10);
                int parent2Num = 0;
                do {
                    parent2Num = rand.nextInt(10);
                }
                while(parent1Num == parent2Num);

                char[] parent1 = nextGen[parent1Num].getTour();
                char[] parent2 = nextGen[parent2Num].getTour();

                //order crossover
                char[] offSpring = new char[parent1.length];
                int start = rand.nextInt(offSpring.length);
                int end = -1;
                for (int j = start; j < start+offSpring.length/2; j++) {
                    int index = j;
                    if (index >= offSpring.length) index -= offSpring.length;

                    offSpring[index] = parent1[index];
                    end = index;
                }
                int count = end+1;
                for (int j = 0; j < parent2.length; j++) {
                    if (contains(offSpring, parent2[j])) continue;

                    if (count >= offSpring.length) count -= offSpring.length;
                    offSpring[count] = parent2[j];
                    count++;
                }

                //mutate chance (switch random two)
                if (Math.random() < mutationRate) {
                    final int numMutations = rand.nextInt(maxMutations)+1;
                    int mutationCount = 0;

                    while (mutationCount < numMutations) {
                        int index1 = rand.nextInt(offSpring.length);
                        int index2 = 0;
                        do {
                            index2 = rand.nextInt(offSpring.length);
                        }
                        while (index1 == index2);

                        char temp = offSpring[index1];
                        offSpring[index1] = offSpring[index2];
                        offSpring[index2] = temp;
                        mutationCount++;
                    }
                }
                nextGen[i] = new Tour(offSpring);
            }

            panel.updateTour(nextGen[0].getTour());
            frame.repaint();

//            try {
//                Thread.sleep(50);
//            } catch (InterruptedException e) {}

            currentGen = nextGen;

            for (int i = prev.length-1; i > 0; i--) {
                prev[i] = prev[i-1];
            }
            prev[0] = nextGen[0].getDistance();

            //
            //EVALUATE
            //
            found = true;
            for (int i : prev) {
                if (i != currentGen[0].getDistance()) found = false;
            }
        }

        return currentGen[0];
    }

    public static char[] randomTour() {
        ArrayList<Integer> usedChars = new ArrayList<>();

        for (int i = 0; i < distances.size()-1; i++) {
            Random rand = new Random();

            int random = 0;
            do {
                random = rand.nextInt(distances.size());
            }
            while (usedChars.contains(random) || random == 0);

            usedChars.add(random);
        }

        char[] chars = new char[usedChars.size()];
        for (int i = 0; i < usedChars.size(); i++) {
            chars[i] = label(usedChars.get(i));
        }

        return chars;
    }

    public static char label(int num) {
        if (num == 0) return 'x';
        return (char)('a'-1+num);
    }

    public static char index(char label) {
        if (label == 'x') return 0;
        return (char)(label+1-'a');
    }

    public static boolean contains(char[] arr1, char a) {
        for (int i = 0; i < arr1.length; i++) {
            if (arr1[i] == a) return true;
        }
        return false;
    }

    public static void read() {
        try {
            FileReader file = new FileReader("C:\\Users\\Phillip\\git\\TravellingSalesman\\src\\distances.txt");
            BufferedReader reader = new BufferedReader(file);
            String newLine = null;

            while ((newLine = reader.readLine()) != null) {
                String[] distance = newLine.split(" ");
                double[] distanceINT = new double[distance.length];
                for (int i = 0; i < distance.length; i++) {
                    distanceINT[i] = Double.parseDouble(distance[i]);
                }

                distances.add(distanceINT);
            }

        } catch (Exception e) {}
    }

    public static void makeGrid() {
        for (int i = 0; i < 2; i++) {
            grid.put(label(i), new Double[] {distances.get(0)[i], 0.0});
        }

        for (int i = 2; i < distances.size(); i++) {
            panel.clearCircles();

            double x1 = grid.get(label(i-1))[0];
            double y1 = grid.get(label(i-1))[1];
            double r1 = distances.get(i)[i-1];

            double x2 = grid.get(label(i-2))[0];
            double y2 = grid.get(label(i-2))[1];
            double r2 = distances.get(i)[i-2];

            double[][] points = intersectTwoCircles(x1, y1, r1, x2, y2, r2);

            ArrayList<double[]> pointList = new ArrayList<>();
            pointList.add(points[0]);
            pointList.add(points[1]);

            for (int j = 0; j < i; j++) {
//                panel.drawCircle(grid.get(label(j))[0], grid.get(label(j))[1], distances.get(i)[j]);

                for (int k = 0; k < pointList.size(); k++) {
                    x1 = grid.get(label(j))[0];
                    y1 = grid.get(label(j))[1];
                    r1 = distances.get(i)[j];

                    x2 = pointList.get(k)[0];
                    y2 = pointList.get(k)[1];
                    r2 = 0;

                    double dis = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));

                    if (dis*0.93 > r1 || dis*1.07 < r1) {
                        pointList.remove(k);
                        k--;
                    }
                }
            }

            grid.put(label(i), new Double[] {pointList.get(0)[0], pointList.get(0)[1]});
        }
    }

    static double[][] intersectTwoCircles(double x1, double y1, double r1, double x2, double y2, double r2) {
        double centerdx = x1 - x2;
        double centerdy = y1 - y2;
        double R = Math.sqrt(centerdx * centerdx + centerdy * centerdy);
        if (!(Math.abs(r1 - r2) <= R && R <= r1 + r2)) { // no intersection
            return new double[2][2]; // empty list of results
        }
        // intersection(s) should exist

        double R2 = R*R;
        double R4 = R2*R2;
        double a = (r1*r1 - r2*r2) / (2 * R2);
        double r2r2 = (r1*r1 - r2*r2);
        double c = Math.sqrt(2 * (r1*r1 + r2*r2) / R2 - (r2r2 * r2r2) / R4 - 1);

        double fx = (x1+x2) / 2 + a * (x2 - x1);
        double gx = c * (y2 - y1) / 2;
        double ix1 = fx + gx;
        double ix2 = fx - gx;

        double fy = (y1+y2) / 2 + a * (y2 - y1);
        double gy = c * (x1 - x2) / 2;
        double iy1 = fy + gy;
        double iy2 = fy - gy;

        // note if gy == 0 and gx == 0 then the circles are tangent and there is only one solution
        // but that one solution will just be duplicated as the code is currently written
        return new double[][] {new double[] {ix1, iy1}, new double[] {ix2, iy2}};
    }

    public static void draw() {
        panel.updateGrid(grid);
        frame.repaint();
    }
}
