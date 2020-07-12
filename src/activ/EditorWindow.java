/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//http://stackoverflow.com/questions/21718018/unable-to-load-library-libtesseract302-in-play-project
package activ;

import com.googlecode.javacv.FFmpegFrameGrabber;
import com.googlecode.javacv.FrameGrabber;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import static java.lang.Short.SIZE;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.sourceforge.tess4j.*;

class RectFrame {
    Rectangle r;
    int frame;
    
    public RectFrame(Rectangle r, int frame){
        this.r = r;
        this.frame = frame;
    }
}

public class EditorWindow extends javax.swing.JFrame {

    class GPanel extends JPanel
    {
        private Rectangle2D[] points = { new Rectangle2D.Double(50, 50,SIZE, SIZE), new Rectangle2D.Double(150, 100,SIZE, SIZE)};
        Rectangle2D s = new Rectangle2D.Double();      
        
        private double zoom = 0.5;
        private double percent =  0.1; // step
        
        private Image img;
        private BufferedImage bImg;
        
        int h = 0;
        int w = 0;


        public GPanel(Image i){
            setImage(i);
        }
        
        public void setImage(Image i){
            img = i;
            h = 0;
            w = 0;
            if (img != null){
                h = img.getHeight(null);
                w = img.getWidth(null);
                bImg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);
                bImg.getGraphics().drawImage(img, 0, 0, null);
            }
        }
        
        @Override
        public void paintComponent(Graphics g){
            /* this needs to be done or it will mess things up */
            super.paintComponent(g);
            
            Graphics2D g2D = (Graphics2D) g;
            g2D.scale(zoom, zoom);
            g2D.drawImage(img, 0, 0, this);
            
            /* change the size depending the the zoom value */
            if (img != null)
                setPreferredSize(new Dimension((int)(zoom * w), 
                        (int)(zoom * h)));
            
               
            g.setColor(Color.RED);
            //System.out.println(currentFrame+" size = "+Gi[currentFrame].length);
            try {
            if (Gi[currentFrame] != null)
                for (int i = 0; i < Gi[currentFrame].length; i++) {
                    Rectangle item = Gi[currentFrame][i];
                    if (item == null) break;
                    g2D.drawRect(item.x, item.y, item.width, item.height);
                    //System.out.println("item x = "+item.x);
                }
            
            g.setColor(Color.GREEN);
            //System.out.println(currentFrame+" size = "+Gi[currentFrame].length);
            if (Di[currentFrame] != null)
                for (int i = 0; i < Di[currentFrame].length; i++) {
                    Rectangle item = Di[currentFrame][i];
                    if (item == null) 
                        break;
                    g2D.drawRect(item.x, item.y, item.width, item.height);
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
             /* update UI more precisely the scrollbars*/
            updateUI(); 
            repaint();
            
        }    
        
        public void setZoomPercentage(int zoomPercentage) {
            percent = ((double) zoomPercentage) / 100;
        }

        public void originalSize() {
            zoom = 1;
        }

        public void zoomIn() {
            zoom += percent;
        }

        public void zoomOut() {
            zoom -= percent;

            if (zoom < percent) {
                if (percent > 1.0) {
                    zoom = 1.0;
                } else {
                    zoomIn();
                }
            }
        }
    }
    
    
    String GXml, DXml;
    Vector<Vector<Rectangle>> G;
    Vector<Vector<Rectangle>> D;
    Rectangle [][] Gi;
    Rectangle [][] Di;
    String VideoURL;
    int currentFrame;
    int nbFrames ;
    FFmpegFrameGrabber g;
    GPanel gPanel;
    
    LinkedHashMap<String, Integer> hm;
    
    int counter = 0;
    
    int setFrameIndex(String id, String vd)
    {
        hm.put("id"+id+vd, counter);
        counter ++;
        
        System.out.println("id"+id+vd + " : "+ counter);
        
        return counter -1;
    }
    
    int getFrameIndex(String id, String vd)
    {
        try{
        return hm.get("id"+id+vd);
        }catch(Exception e)
        {
            System.out.println("Error finding "+id+", "+vd);
        }
        
        System.exit(-1);
        return 0;
    }
    
    public EditorWindow(String GXml, String DXml, String VUrl) {
        hm = new LinkedHashMap<>();
        
        initComponents();
        setTitle("AcTiV Evaluation");
        jlFrames.setModel(new DefaultListModel());
        JPanel pnl = new JPanel(new GridLayout(1, 1));
        gPanel = new GPanel(/*ImageIO.read(new File("logo.jpg"))*/null);
        JScrollPane scrollPanel; 
        
        scrollPanel = new JScrollPane(gPanel);
        scrollPanel.setAutoscrolls(true);
        pnl.add(scrollPanel);
        //jpDrawArea.add(pnl);
        jpDrawArea.setLayout(new GridLayout(1, 1));
        jpDrawArea.add(pnl);
        this.GXml = GXml;
        this.DXml = DXml;
        VideoURL = VUrl;
        /*
        try {
            g = new FFmpegFrameGrabber(VideoURL);
            g.start();
            g.setFrameNumber(0);
            currentFrame = 0;
            gPanel.setImage(g.grab().getBufferedImage());
        } catch (FrameGrabber.Exception ex) {
            ex.printStackTrace();
        }
        */
        doParseGXml();
        doParseDXml();
        /*
        for(int i = 0; i < Gi.length; i++){
            if (Gi[i] != null)
                for(int j = 0; j < Gi[i].length; j++){
                    System.out.println(Gi[i][j]+" - "+Di[i][j]);
                }
        }*/
        
        DefaultListModel tmp = (DefaultListModel)jlFrames.getModel();
        tmp.clear();
        
        /*
        for(long i = 0; i < g.getLengthInFrames(); i++){
            tmp.addElement("Frame "+(i+1));
        }*/
        pack();
        setVisible(true);
    }

    public void doParseGXml(){
        Element root = null;
        int nbRect = 0;
        try {
            File fXmlFile = new File(GXml);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            root = doc.getDocumentElement();
            doc.getDocumentElement().normalize();
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(EditorWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Vector <RectFrame> V = new Vector();
        
        NodeList framesList = root.getChildNodes();
            
        if (framesList == null)
            return;
        nbFrames = root.getElementsByTagName("frame").getLength();
        
        for(int j = 0; j < framesList.getLength(); j++){
            Node nd = framesList.item(j);
            Element e;
               
            if(nd.getNodeType() == Node.ELEMENT_NODE){
                e = (Element)nd;
            }           
            
            else
                continue;
            
            int fs = setFrameIndex(e.getAttribute("id"), e.getAttribute("source"));
                    
            NodeList childs = e.getChildNodes();
            Vector < Point > v = new Vector();
            for(int k = 0; k < childs.getLength(); k++){
                Node n = childs.item(k);
                if(n.getNodeType() == Node.ELEMENT_NODE){
                    Element el = (Element)n;
                    if (n.getNodeName() == "rectangle"){
                        nbRect++;
                        Rectangle r = new Rectangle();
                        r.x = Integer.parseInt(el.getAttribute("x"));
                        r.y = Integer.parseInt(el.getAttribute("y"));
                        r.width = Integer.parseInt(el.getAttribute("width"));
                        r.height = Integer.parseInt(el.getAttribute("height"));
                        V.add(new RectFrame(r, fs));
                        //System.out.println(r);
                    }
                }
            }          
        }
        
        Gi = new Rectangle[nbFrames][];
        
        System.out.println("number of frames in G = "+nbFrames);
        
        for(int i = 0; i < nbFrames; i++){
            Gi[i] = null;
        }
        if (V.size()== 0)
            return;
        int oi = V.elementAt(0).frame; // old index
        int [] tabSize = new int[nbRect];
        Vector <RectFrame> vrf = new Vector();
        for(int i = 0; i < V.size(); i++){
            RectFrame rf = V.elementAt(i);
            if (oi == rf.frame)
                vrf.add(rf);
            else
            {
                //System.out.println("old index = "+oi+", "+Gi.length);
                Gi[oi] = new Rectangle[vrf.size()];
                for(int j = 0; j < vrf.size(); j++)
                    Gi[oi][j] = vrf.elementAt(j).r;
                oi = rf.frame;
                vrf.clear();
                vrf.add(rf);
            }
        }
        Gi[oi] = new Rectangle[vrf.size()];
        for(int j = 0; j < vrf.size(); j++)
            Gi[oi][j] = vrf.elementAt(j).r;
    }
        
 public void doParseDXml(){
        Element root = null;
        int nbRect = 0;
        try {
            File fXmlFile = new File(DXml);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            root = doc.getDocumentElement();
            doc.getDocumentElement().normalize();
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(EditorWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Vector <RectFrame> V = new Vector();
        
        NodeList framesList = root.getChildNodes();
            
        if (framesList == null)
            return;
        nbFrames = root.getElementsByTagName("frame").getLength();
        
        for(int j = 0; j < framesList.getLength(); j++){
            Node nd = framesList.item(j);
            Element e;
               
            if(nd.getNodeType() == Node.ELEMENT_NODE){
                e = (Element)nd;
            }           
            
            else
                continue;
            
            int fs = getFrameIndex(e.getAttribute("id"), e.getAttribute("source"));
            NodeList childs = e.getChildNodes();
            Vector < Point > v = new Vector();
            for(int k = 0; k < childs.getLength(); k++){
                Node n = childs.item(k);
                if(n.getNodeType() == Node.ELEMENT_NODE){
                    Element el = (Element)n;
                    if (n.getNodeName() == "rectangle"){
                        nbRect++;
                        Rectangle r = new Rectangle();
                        r.x = Integer.parseInt(el.getAttribute("x"));
                        r.y = Integer.parseInt(el.getAttribute("y"));
                        r.width = Integer.parseInt(el.getAttribute("width"));
                        r.height = Integer.parseInt(el.getAttribute("height"));
                        V.add(new RectFrame(r, fs));
                        //System.out.println(r);
                    }
                }
            }          
        }
        
        Di = new Rectangle[nbFrames][];
        System.out.println("number of frames in D = "+nbFrames);
        
        for(int i = 0; i < nbFrames; i++){
            Di[i] = null;
        }
        if (V.size()== 0)
            return;
        int oi = V.elementAt(0).frame; // old index
        int [] tabSize = new int[nbRect];
        Vector <RectFrame> vrf = new Vector();
        for(int i = 0; i < V.size(); i++){
            RectFrame rf = V.elementAt(i);
            if (oi == rf.frame)
                vrf.add(rf);
            else
            {
                //System.out.println("old index = "+oi+", "+Gi.length);
                //System.out.println(hm.get(425));
                try{
                    Di[oi] = new Rectangle[vrf.size()];
                }
                catch(ArrayIndexOutOfBoundsException e)
                {
                    for(String s: hm.keySet())
                    {
                        if (hm.get(s) == oi)
                            System.out.println("Video "+s);
                    }
                }
                for(int j = 0; j < vrf.size(); j++)
                    Di[oi][j] = vrf.elementAt(j).r;
                oi = rf.frame;
                vrf.clear();
                vrf.add(rf);
            }
        }
        Di[oi] = new Rectangle[vrf.size()];
        for(int j = 0; j < vrf.size(); j++)
            Di[oi][j] = vrf.elementAt(j).r;
    }  
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jpLeft = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jlFrames = new javax.swing.JList();
        jToolBar2 = new javax.swing.JToolBar();
        btnZoomIn = new javax.swing.JButton();
        btnZoomValue = new javax.swing.JButton();
        btnZoomOut = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jpDrawArea = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jlFrames.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jlFrames.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jlFramesValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jlFrames);

        javax.swing.GroupLayout jpLeftLayout = new javax.swing.GroupLayout(jpLeft);
        jpLeft.setLayout(jpLeftLayout);
        jpLeftLayout.setHorizontalGroup(
            jpLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
        );
        jpLeftLayout.setVerticalGroup(
            jpLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
        );

        jToolBar2.setRollover(true);

        btnZoomIn.setText("+");
        btnZoomIn.setFocusable(false);
        btnZoomIn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnZoomIn.setMargin(new java.awt.Insets(1, 14, 2, 14));
        btnZoomIn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnZoomIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnZoomInActionPerformed(evt);
            }
        });
        jToolBar2.add(btnZoomIn);

        btnZoomValue.setText("100%");
        btnZoomValue.setFocusable(false);
        btnZoomValue.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnZoomValue.setMargin(new java.awt.Insets(1, 14, 2, 14));
        btnZoomValue.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnZoomValue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnZoomValueActionPerformed(evt);
            }
        });
        jToolBar2.add(btnZoomValue);

        btnZoomOut.setText("-");
        btnZoomOut.setFocusable(false);
        btnZoomOut.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnZoomOut.setMargin(new java.awt.Insets(1, 14, 2, 14));
        btnZoomOut.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnZoomOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnZoomOutActionPerformed(evt);
            }
        });
        jToolBar2.add(btnZoomOut);

        jButton1.setText("Evaluate CF");
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBar2.add(jButton1);

        jButton2.setText("Evaluate All");
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jToolBar2.add(jButton2);

        jButton3.setText("Evaluate Harmonic Mean");
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jToolBar2.add(jButton3);

        jButton4.setText("Performance Value");
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jToolBar2.add(jButton4);

        javax.swing.GroupLayout jpDrawAreaLayout = new javax.swing.GroupLayout(jpDrawArea);
        jpDrawArea.setLayout(jpDrawAreaLayout);
        jpDrawAreaLayout.setHorizontalGroup(
            jpDrawAreaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jpDrawAreaLayout.setVerticalGroup(
            jpDrawAreaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, 711, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jpLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jpDrawArea, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jpLeft, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jpDrawArea, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jlFramesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jlFramesValueChanged
        
        int i = jlFrames.getSelectedIndex();
        System.out.println("i = "+i);
        i++;
        try {
            /* this idea gets less performant if the user skips more than 
            800 frames and it would be better to use setFrameNumber instead. */
            if(( i > currentFrame) && (i - currentFrame < 800))
                for(int j = currentFrame; j < i-1; j++){
                    g.grabFrame();
                } 
            else
                g.setFrameNumber(i);
            
            currentFrame = i;
            gPanel.setImage(
                    g.grab().getBufferedImage()
            );
        } catch (FrameGrabber.Exception ex) {
            ex.printStackTrace();
        }
        
        jlFrames.ensureIndexIsVisible(i);
    }//GEN-LAST:event_jlFramesValueChanged

    private void btnZoomInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnZoomInActionPerformed
        gPanel.zoom+=0.1;
    }//GEN-LAST:event_btnZoomInActionPerformed

    private void btnZoomValueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnZoomValueActionPerformed
        gPanel.zoom = 1;
    }//GEN-LAST:event_btnZoomValueActionPerformed

    private void btnZoomOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnZoomOutActionPerformed
        gPanel.zoom-=0.1;
    }//GEN-LAST:event_btnZoomOutActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        /*double [] c = {0.8, 0.6}; 
        
        WolfAlgorithm wa = new WolfAlgorithm(Gi[currentFrame], Di[currentFrame], c);
        wa.printData();
        wa.doMatching();
        wa.printResult();
        wa.printRP();*/
        
        new FrameEvalCF(Gi[currentFrame], Di[currentFrame]);
                
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        new FrameEvalAF(Gi, Di);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        new PerformanceView(Gi, Di);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        double [] c = {0.8, 0.4};
        double T = 20;
        double Rov = 0, Pov = 0;
                
        for(int k = 1; k <= T; k++)
        {
            double [] co = { k/T, c[1]};
            Evaluator e = new Evaluator(Gi, Di, co);
            Rov += e.Rob();
            Pov += e.Pob();
                    
            double [] co2 = { c[0], k/T};
             e = new Evaluator(Gi, Di, co2);
             Rov += e.Rob();
             Pov += e.Pob();
        }               
                
        System.out.println("Before Rov = "+Rov);
        System.out.println("Before Pov = "+Pov);
                
        NumberFormat formatter = new DecimalFormat("#0.00");
        formatter.setMaximumFractionDigits(2);
        Rov = 1/(2*T) * Rov;
        Pov = 1/(2*T) * Pov;
        double rer;
        System.out.println("After Rov = "+Rov);
        System.out.println("After Pov = "+Pov);
        System.out.println("2 * (Pov * Rov)/(Pov + Rov) = "+(2 * (Pov * Rov)/(Pov + Rov)));
        if ((Pov == 0) && (Rov == 0))
            rer= 0;
        else
            rer = 2 * (Pov * Rov)/(Pov + Rov);
        JOptionPane.showMessageDialog(null, "Recall: "+(formatter.format(Rov))+" Precision: "+
                formatter.format(Pov) + " F-measure: "+ formatter.format(rer), 
                "Performance values", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jButton4ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnZoomIn;
    private javax.swing.JButton btnZoomOut;
    private javax.swing.JButton btnZoomValue;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JList jlFrames;
    private javax.swing.JPanel jpDrawArea;
    private javax.swing.JPanel jpLeft;
    // End of variables declaration//GEN-END:variables
}
