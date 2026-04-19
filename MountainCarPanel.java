import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class MountainCarPanel extends JPanel {

    private double worldwidth;
    private int carX, carY, oldCarX, oldCarY;
    private double scaleX, scaleY;


    public MountainCarPanel() {
         this.setBackground(new Color(150,150,150));
         JFrame frame = new JFrame("Mountain Car Environment");
         frame.setBounds(100,100,600,600);
         frame.setContentPane(this);
         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         frame.setVisible(true);
    }

    public void render(double[] state) {
        worldwidth = MountainCarEnv.MAX_POS - MountainCarEnv.MIN_POS;
        //scale makes the drawing grow/shrink as the window does
        scaleX = getSize().getWidth()/worldwidth*0.9;
        scaleY = getSize().getHeight()/worldwidth*0.9;
        oldCarX = carX;
        oldCarY = carY;
        carX = getXFor(state[2]);
        carY = getYFor(state[2]);
        this.repaint();
    }

    private int getXFor(double val) {
        return (int) (10+scaleX*(val-MountainCarEnv.MIN_POS));
    }
    private int getYFor(double val) {
        return (int) (getSize().getHeight()*0.55 - scaleY*0.75*(Math.sin(val*3)));
    }

    public void paintComponent(Graphics g){
    	super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        //Get rid of the old car
        g2d.setColor(super.getBackground());
        Ellipse2D.Double circle = new Ellipse2D.Double(oldCarX-7, oldCarY-7, 14, 14);
        g2d.fill(circle);
        //In with the new car
        g.setColor(Color.black);
        circle = new Ellipse2D.Double(carX-7, carY-7, 14, 14);
        g2d.fill(circle);
        //Draw the Mountain
        int np=50;
        int[] xpnts = new int[np];
        int[] ypnts = new int[np];
        for (int i=0; i<np; i++) {
            xpnts[i] = getXFor(1.0*i*(worldwidth)/(np-1)+MountainCarEnv.MIN_POS);
            ypnts[i] = getYFor(1.0*i*(worldwidth)/(np-1)+MountainCarEnv.MIN_POS);
        }
        g2d.setStroke(new BasicStroke(2));
        g2d.drawPolyline(xpnts,ypnts,np);
        //Draw the Goal
        g2d.setStroke(new BasicStroke(1));
        g2d.drawLine(getXFor(MountainCarEnv.GOAL_POS),getYFor(MountainCarEnv.GOAL_POS),
                getXFor(MountainCarEnv.GOAL_POS), getYFor(MountainCarEnv.GOAL_POS)-50);
        g2d.setColor(Color.green);
        int[] triangleX = new int[3];
        int[] triangleY = new int[3];
        triangleX[0] = getXFor(MountainCarEnv.GOAL_POS);
        triangleX[1] = getXFor(MountainCarEnv.GOAL_POS);
        triangleX[2] = getXFor(MountainCarEnv.GOAL_POS)-40;
        triangleY[0] = getYFor(MountainCarEnv.GOAL_POS)-50;
        triangleY[1] = getYFor(MountainCarEnv.GOAL_POS)-30;
        triangleY[2] = getYFor(MountainCarEnv.GOAL_POS)-40;
        g2d.fillPolygon(triangleX, triangleY, 3);
        //Draw the left border
        g2d.setColor(Color.black);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(10, getYFor(MountainCarEnv.MIN_POS)-40,
                10, getYFor(MountainCarEnv.MIN_POS)+15);

    }
}
