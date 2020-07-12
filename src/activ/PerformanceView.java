package activ;

import java.awt.Rectangle;
import org.math.plot.*;

/* Frame Evaluation of All frames */

public class PerformanceView extends javax.swing.JFrame {
    Rectangle [][] Gi, Di;
    /**
     * Creates new form FrameEvalCF
     */
    public PerformanceView(Rectangle [][]G, Rectangle [][]D) {
        initComponents();
        Gi = G;
        Di = D;
        // define your data
        double[] r = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1};
        double[] p = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1};
        
        double[][] rer = new double[r.length][];
        double T = 20;
        
        //System.out.println(r.length);
        //System.out.println(p.length);
        
        for(int i = 0; i < r.length; i++){
            rer[i] = new double[p.length];
            for(int j = 0; j < p.length; j++){
                double [] c = {r[i], p[j]};
                double Rov = 0, Pov = 0;
                
                for(int k = 1; k <= T; k++)
                {
                    double [] co = { k/T, p[j]};
                    Evaluator e = new Evaluator(Gi, Di, co);
                    Rov += e.Rob();
                    Pov += e.Pob();
                    
                    double [] co2 = { r[i], k/T};
                    e = new Evaluator(Gi, Di, co2);
                    Rov += e.Rob();
                    Pov += e.Pob();
                }
                
                
                
                
                
                System.out.println("Before Rov = "+Rov);
                System.out.println("Before Pov = "+Pov);
                
                Rov = 1/(2*T) * Rov;
                Pov = 1/(2*T) * Pov;
                
                System.out.println("After Rov = "+Rov);
                System.out.println("After Pov = "+Pov);
                System.out.println("2 * (Pov * Rov)/(Pov + Rov) = "+(2 * (Pov * Rov)/(Pov + Rov)));
                if ((Pov == 0) && (Rov == 0))
                    rer[i][j] = 0;
                else
                    rer[i][j] = 2 * (Pov * Rov)/(Pov + Rov);
            }
        }
        System.out.println(rer[0].length * rer.length);
 
        for(int i = 0; i < rer.length; i++){
            for(int j = 0; j < rer.length; j++){
                System.out.println("rer["+i+"]["+j+"] = "+rer[i][j]);
            }
        }
        
        // create your PlotPanel (you can use it as a JPanel)
        Plot3DPanel plot = new Plot3DPanel();
 
        // define the legend position
        plot.addLegend("SOUTH");
        
        // add a line plot to the PlotPanel
        plot.addGridPlot("Harmonic Mean", r, p, rer);
        
 
        plot.plotCanvas.setFixedBounds(0, 0, 1);
        plot.plotCanvas.setFixedBounds(1, 0, 1);
        plot.plotCanvas.setFixedBounds(2, 0, 1);
        // put the PlotPanel in a JFrame like a JPanel
        setTitle("Harmonic Mean");
        setContentPane(plot);
        setSize(600, 600);
        pack();
        setVisible(true);
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
