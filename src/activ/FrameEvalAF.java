package activ;

import java.awt.Color;
import java.awt.Rectangle;
import javax.swing.JFrame;
import org.math.plot.*;

/* Frame Evaluation of All frames */

public class FrameEvalAF extends javax.swing.JFrame {
    Rectangle [][] Gi, Di;
    /**
     * Creates new form FrameEvalCF
     */
    public FrameEvalAF(Rectangle [][]G, Rectangle [][]D) {
        initComponents();
        Gi = G;
        Di = D;
        // define your data
        double[] r = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1};
        double[] p = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1};
        
        double  [][] missD =new double[r.length][];
        
        double[][] rer = new double[r.length][];
        double[][] rep = new double[r.length][];
        
        System.out.println(r.length);
        System.out.println(p.length);
        
        for(int i = 0; i < r.length; i++){
            rer[i] = new double[p.length];
            rep[i] = new double[p.length];
            missD[i] = new double[p.length];
            for(int j = 0; j < p.length; j++){
                double [] c = {r[i], p[j]};
                Evaluator e = new Evaluator(Gi, Di, c);
                rer[i][j] = e.Rob();
                rep[i][j] = e.Pob();
                missD[i][j] = e.correctMatches();
                System.out.println("tr = "+r[i]+", tp = "+p[j]+" correct matches = "+e.correctMatches());
                // r[i][j]+", "+rep[i][j]);
            }
        }
        System.out.println(rer[0].length * rer.length);
 
        // create your PlotPanel (you can use it as a JPanel)
        Plot3DPanel plot = new Plot3DPanel();
 
        // define the legend position
        plot.addLegend("SOUTH");
        
        // add a line plot to the PlotPanel
        plot.addGridPlot("Recall",Color.CYAN,  r, p, rer);
        
        
        JFrame f = new JFrame("Precision");
        Plot3DPanel plot2 = new Plot3DPanel();
        plot2.addGridPlot("Precision", Color.red, r, p, rep);
        f.setContentPane(plot2);
        f.pack();
        f.setVisible(true);
 
        JFrame f2 = new JFrame("Correct detections");
        Plot3DPanel plot3 = new Plot3DPanel();
        plot3.addGridPlot("Correct Detections", Color.ORANGE, r, p, missD);
        f2.setContentPane(plot3);
        f2.pack();
        f2.setVisible(true);
        
        plot.plotCanvas.setFixedBounds(0, 0, 1);
        plot.plotCanvas.setFixedBounds(1, 0, 1);
        plot.plotCanvas.setFixedBounds(2, 0, 1);
        plot2.plotCanvas.setFixedBounds(0, 0, 1);
        plot2.plotCanvas.setFixedBounds(1, 0, 1);
        plot2.plotCanvas.setFixedBounds(2, 0, 1);
        plot3.plotCanvas.setFixedBounds(0, 0, 1);
        plot3.plotCanvas.setFixedBounds(1, 0, 1);
        plot3.plotCanvas.setFixedBounds(2, 0, 10);
        
        setTitle("Recall.");
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
