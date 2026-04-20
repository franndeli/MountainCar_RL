import javax.swing.JFrame;

public class StateDiscretizationTester {

   // This class can help you test your state-space discretization

    private static final StateDiscretization discretization = new StateDiscretization();

    private static double getValue(double position, double velocity) {
        return discretization.getValue(position, velocity);
    }

    private static void putValue(double position, double velocity, double value) {
        discretization.putValue(position, velocity, value);
    }

    ///////////////////////////////////////////
    // Don't change any code below this line //
    ///////////////////////////////////////////

    private static final int nb = 1000;

    public static void main(String[] args) {
        
        try {
            // This first part will show the original values
            double[][] originalValues = new double[1000][1000];
            for (int i=0; i<1000; i++)
                for (int j=0; j<1000; j++)
                    originalValues[i][j] = Math.sin(0.00002*i*j);
            HeatMapWindow hm1 = new HeatMapWindow(originalValues);
            hm1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            hm1.setSize(1000,1000);
            hm1.setVisible(true);
            hm1.update(originalValues);
            
            // This second part will show the values you stored
            // If all is well, both images should like alike, possibly 
            // with a slight reduction in resolution
            double[][] storedValues = new double[1000][1000];
            for (int i=0; i<1000; i++)
                for (int j=0; j<1000; j++) 
                    putValue(toPosition(i),toVelocity(j),Math.sin(0.00002*i*j));
            for (int i=0; i<1000; i++)
                for (int j=0; j<1000; j++) 
                    storedValues[i][j] = getValue(toPosition(i),toVelocity(j));
            
            HeatMapWindow hm2 = new HeatMapWindow(storedValues);
            hm2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            hm2.setSize(1000,1000);
            hm2.setVisible(true);
            hm2.update(storedValues);
        }
        catch (Exception e) {System.out.println(e.getMessage());}
    }

    private static double toPosition(int x) {
        return 1.0 * x / nb * (MountainCarEnv.MAX_POS-MountainCarEnv.MIN_POS) + MountainCarEnv.MIN_POS + (MountainCarEnv.MAX_POS-MountainCarEnv.MIN_POS)/(nb*2);
    }

    private static double toVelocity(int x) {
        return 1.0 * x / nb * (MountainCarEnv.MAX_SPEED*2) - MountainCarEnv.MAX_SPEED + MountainCarEnv.MAX_SPEED/nb;
    }
}
