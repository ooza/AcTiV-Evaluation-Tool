package activ;

import java.awt.Rectangle;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import org.math.plot.*;

/* Frame Evaluation of current frame */

class MathUtils{
    static double[] createRange(double s, double e, double step){
        int size = (int)((e-s)/step) ;
        double [] x = new double[size+1];
        int j = 0;
        for(double i = s; i <= e; i+= step){
            x[j++] = i;
        }
        return x;
    }
}

public class FrameEvalCF extends javax.swing.JFrame {
    Rectangle [] Gi, Di;
    /**
     * Creates new form FrameEvalCF
     */
    public FrameEvalCF(Rectangle []G, Rectangle []D) {
        initComponents();
        Gi = G;
        Di = D;
        // define your data
        double[] r = MathUtils.createRange(0, 1.1, 0.1);
        double[] p = MathUtils.createRange(0, 1.1, 0.1);
        for(int i = 0; i < r.length; i++)
            System.out.println("r[i] = "+r[i]);
        double[][] rer = new double[r.length][];
        double[][] rep = new double[r.length][];
    
        System.out.println(r.length);
        System.out.println(p.length);
        
        for(int i = 0; i < r.length; i++){
            rer[i] = new double[p.length];
            rep[i] = new double[p.length];
            for(int j = 0; j < p.length; j++){
                double [] c = {r[i], p[j]};
                WolfAlgorithm w = new WolfAlgorithm(Gi, Di, c);
                w.doMatching();
                rer[i][j] = w.Rob();
                rep[i][j] = w.Pob();
                System.out.println("tr = "+r[i]+", tp = "+p[j]+" correct matches = "+w.correctMatches());
                //r[i][j]+", "+rep[i][j]);
            }
        }
        System.out.println(rer[0].length * rer.length);
 
        // create your PlotPanel (you can use it as a JPanel)
        Plot3DPanel plot = new Plot3DPanel();
 
        // define the legend position
        plot.addLegend("SOUTH");
        
        // add a line plot to the PlotPanel
        plot.addGridPlot("Recall", r, p, rer);
        plot.addGridPlot("Precision", r, p, rep);
        
 
        plot.plotCanvas.setFixedBounds(0, 0, 1);
        plot.plotCanvas.setFixedBounds(1, 0, 1);
        plot.plotCanvas.setFixedBounds(2, 0, 1);
        // put the PlotPanel in a JFrame like a JPanel
        setTitle("Current Frame Eval.");
        setSize(600, 600);
        setContentPane(plot);
        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
           
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 702, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 469, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
