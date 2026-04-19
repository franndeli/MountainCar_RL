import java.awt.GridLayout;
import javax.swing.JFrame;

public class HeatMapWindow extends JFrame{
    private double[][] datav;
    private double[][] valueF;
    private double[][] policy;
    private HeatMap[] panels;
    private boolean useGraphicsYAxis = true;

    public HeatMapWindow(double[][] v) throws Exception
    {
        super("Heat Map Window");
        datav = v;

        panels = new HeatMap[1];
        panels[0] = new HeatMap(datav, useGraphicsYAxis, Gradient.GRADIENT_RAINBOW);
        panels[0].setDrawYAxisTitle(true);
        panels[0].setYAxisTitle("Velocity");
        panels[0].setYCoordinateBounds(0.07, -0.07);
        panels[0].setDrawYTicks(true);
        panels[0].setDrawXAxisTitle(true);
        panels[0].setXAxisTitle("Position");
        panels[0].setXCoordinateBounds(-1.2, 0.6);
        panels[0].setDrawXTicks(true);

        this.setLayout(new GridLayout(0,1));
        this.getContentPane().add(panels[0]);
        pack();
    }

    public void update(double[][] v) {
        this.datav = v;
        panels[0].updateData(datav, useGraphicsYAxis);
    }

    public HeatMapWindow(double[][] v, double[][] policy) throws Exception
    {
        super("Heat Map Window");
        datav = v;
        this.policy = policy;

        panels = new HeatMap[2];
        panels[0] = new HeatMap(datav, useGraphicsYAxis, Gradient.GRADIENT_RAINBOW);
        panels[1] = new HeatMap(policy, useGraphicsYAxis, Gradient.GRADIENT_HC);
        for (int i=0; i<2; i++) {
            panels[i].setDrawYAxisTitle(true);
            panels[i].setYAxisTitle("Velocity");
            panels[i].setYCoordinateBounds(-0.7, 0.7);
            panels[i].setDrawYTicks(true);
            panels[i].setDrawXAxisTitle(true);
            panels[i].setXAxisTitle("Position");
            panels[i].setXCoordinateBounds(-1.2, 0.6);
            panels[i].setDrawXTicks(true);
        }

        this.setLayout(new GridLayout(0,2));
        for (int i=0; i<panels.length; i++)
            this.getContentPane().add(panels[i]);
        pack();
    }

    public void update(double[][] v, double[][] p) {
        this.datav = v;
        this.policy = p;
        panels[0].updateData(datav, useGraphicsYAxis);
        panels[1].updateData(policy, useGraphicsYAxis);
    }

}
