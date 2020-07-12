/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package activ;

import java.awt.Rectangle;

/**
 *
 * @author zayeneo
 */
public class Evaluator {
    Rectangle [][] G;
    Rectangle [][] D;
    
    WolfAlgorithm wa;
    
    double rob, pob;
    int k = 0;
    
    int falseA = 0, missD = 0, correctM = 0;
    int gk , dk;
    public Evaluator(Rectangle [][]Gi, Rectangle [][]Di, double[]constraints){
        G = Gi;
        D = Di;
        rob = 0;
        pob = 0;
        gk = 0;
        dk = 0;
        
        int max = (Gi.length>Di.length)?Gi.length:Di.length;
        
        for(int i = 0; i < max; i++){
            
                
            if ((Gi[i] == null) || (Di[i] == null)){
                if (((Gi[i] == null) ^ (Di[i] == null))){
                    k++;   
                }
                continue;
            }
            //System.out.println("Gi["+i+"][0] = "+Gi[i][0]);      
            wa = new WolfAlgorithm(G[i], D[i], constraints);  
            wa.printData();
            wa.doMatching();
            rob += wa.Rob();
            pob += wa.Pob();
            falseA += wa.falseAlarms();
            correctM += wa.correctMatches();
            missD +=wa.missDetections();
            k++;
        }
    }
    
    
    public double Rob(){
        //System.out.println("retutning Rob "+(rob/k));
        return rob/k; ///SumG;
    }
    
    public double Pob(){
       //System.out.println("retutning Pob "+(pob/k));
        return pob/k;///SumD;
    }
    
    public int missDetections(){
        return missD;
    }
    
    public int correctMatches(){
        return correctM;
    }
    
    public int falseAlarms(){
        return falseA;
    }
}
