/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package activ;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Vector;

public class WolfAlgorithm {
	
	Rectangle [] G; /* Ground truth */
	Rectangle [] D; /* Detected Rect */ 
	
	int [][] normalMatches;
	int [][] mergeMatches;
	int [][] splitMatches;
	
        int nbFalseAlarms = 0;
        int nbUndetected = 0;
        
	double tr, tp;
	
	double [][] M1;
	double [][] M2;
	
	Vector <Integer> calculatedGRect;
	Vector <Integer> calculatedDRect;
	
	public static double IntersectArea(Rectangle r1, Rectangle r2)
	{
            double width=0,height=0;
            double s1=r1.width*r1.height; //r1 area
            double s2=r2.width*r2.height; //r2 area
     
     
    	if((r1.x+r1.width<=r2.x)||(r2.x+r2.width<=r1.x)) 
    		width=0;
    	else if((r1.y+r1.height<=r2.y)||(r2.y+r2.height<=r1.y)) 
    		height=0;
    	else{
    		width=Math.min((r1.x+r1.width),(r2.x+r2.width))-Math.max(r1.x,r2.x); //overlap's width
    		height=Math.min((r1.y+r1.height),(r2.y+r2.height))-Math.max(r1.y,r2.y);// overlap's height
    		//System.out.println("min between "+Math.min((r1.y+r1.height),(r2.y+r2.height))+" max between "+Math.max(r1.y,r2.y));
    	}
   
    	return width*height;
	    
	}
	
	/* Where Gi in Ground Truth db and Di in the System output rectangles */

	/* Recall Area */
	public static double Rar(Rectangle Gi, Rectangle Di)
	{
		return IntersectArea(Gi, Di) / (Gi.width * Gi.height);		
	}
	
	/* Precision Area */
	public static double Par(Rectangle Gi, Rectangle Di)
	{
		return IntersectArea(Gi, Di) / (Di.width * Di.height);	
	}
	
	/* Where k is the number of matched rectangles */
	public double fsc(int k){
		return 0.8;
		//return 1/(1+Math.log(k));
	}
	
	public double MatchG(int Gi){
                
		if (normalMatches[Gi] != null)
                    return 1;
		
		if (splitMatches[Gi] != null)
			return fsc(splitMatches[Gi].length);
		nbUndetected++;
		return 0;
	}
	
	
	public double MatchD(int Di){
		for(int i = 0; i < normalMatches.length; i++){
			if (normalMatches[i]!=null)
				if (normalMatches[i][0] == Di)
                                    return 1;
                                        
		}
		
		if (mergeMatches[Di] != null)
			return fsc(mergeMatches[Di].length);
                
		nbFalseAlarms++;
                
		return 0;
	}
	
	public WolfAlgorithm(Rectangle [] Gi, Rectangle [] Di, double [] constraints){
		normalMatches = new int[Gi.length][];
		splitMatches = new int [Gi.length][];
		mergeMatches = new int [Di.length][];
		
		G = Gi;
		D = Di;	
                
		M1 = new double [Gi.length][];
		M2 = new double [Gi.length][];
		
		tr = constraints[0];
		tp = constraints[1];
		
		for(int i = 0; i < Gi.length; i++){
			M1[i] = new double [Di.length];
			M2[i] = new double [Di.length];
			for(int j = 0; j < Di.length; j++)
			{
				M1[i][j] = Rar(G[i], D[j]);
				M2[i][j] = Par(G[i], D[j]);
			}
		}
		
		calculatedGRect = new Vector();
		calculatedDRect = new Vector();		
	}
	
	public void printData(){
		System.out.println("tr = "+tr+" tp = "+tp);
		System.out.println("------- M1 -------");
		for(int i = 0; i < M1.length; i++)
		{
			for(int j = 0; j < M1[i].length; j++){
				System.out.print("\t"+M1[i][j]);
			}
			System.out.println();
		}
		System.out.println("------- M2 -------");
		for(int i = 0; i < M2.length; i++)
		{
			for(int j = 0; j < M2[i].length; j++){
				System.out.print("\t"+M2[i][j]);
			}
			System.out.println();
		}
	}
	
	private boolean iRowContainOneElementSatisfying(int i){
		int M1StatisfyCount = 0;
		int M2StatisfyCount = 0;
		
		for (int j = 0; j < D.length; j++){
			if (M1[i][j] >= tr)
				M1StatisfyCount++;
			
			if (M2[i][j] >= tp)
				M2StatisfyCount++;
		}
                //System.out.println("M1Count = "+M1StatisfyCount+" M2Count = "+M2StatisfyCount + " returning "+((M1StatisfyCount == 1) && (M2StatisfyCount == 1)));
		return (M1StatisfyCount == 1) && (M2StatisfyCount == 1);
	}
	
	private boolean jRowContainOneElementSatisfying(int j){
		int M1StatisfyCount = 0;
		int M2StatisfyCount = 0;
		
                
		for (int i = 0; i < G.length; i++){
			if (M1[i][j] >= tr)
				M1StatisfyCount++;
			
			if (M2[i][j] >= tp)
				M2StatisfyCount++;
		}
		
		return (M1StatisfyCount == 1) && (M2StatisfyCount == 1);
	}
	
	public void doMatching(){
		/* One to one matching */
		for(int i = 0; i < G.length; i++)
		{
			if (calculatedGRect.contains(i))
				continue;
			
			for(int j = 0; j < D.length; j++){ // 1 - 1 matching
                                System.out.println("checking G"+i+" D"+j);
				if (calculatedDRect.contains(j))
					continue;
				
                                //TODO
				if (iRowContainOneElementSatisfying(i) && jRowContainOneElementSatisfying(j))
				{
					System.out.println("1-1 Matching found: "+i+" with "+j);
					normalMatches[i] = new int [1];
					normalMatches[i][0] = j;
                                        calculatedGRect.add(i);
                                        calculatedDRect.add(j);
					break;
				}else if(M1[i][j] >= tr){ // Merging
					Vector <Integer> v = new Vector();
					System.out.println("Merge matching possible");
					double Sum = 0;
					for(int k = i; k < G.length; k++)
					{
						if( M1[k][j] >= tr )
						{
							//System.out.print("\t"+k);
							v.add(k);
							Sum += M2[k][j];
						}
					}					
					
					if (Sum < tp)
						continue;
					
					//System.out.println();
					mergeMatches[j] = new int [v.size()];
					//System.out.println(matchTable[j].length);
					int x = 0;
					while(!v.isEmpty())
					{
						Integer I = v.remove(0);
						calculatedGRect.add(I);
						mergeMatches[j][x] = I;
						x++;
					}
                                        break;
				}else if (M2[i][j] >= tp)
				{
					Vector <Integer> v = new Vector();
					System.out.println("Splitting matching possible");
					double Sum = 0;
					for(int k = j; k < D.length; k++)
					{
						if( M2[i][k] >= tp )
						{
							//System.out.print("\t"+k);
							v.add(k);
							Sum += M1[i][k];
						}
					}					
					
					//System.out.println(Sum);
					
					if (Sum < tr)
						continue;
					
					//System.out.println();
					splitMatches[i] = new int [v.size()];
					//System.out.println(splitMatches[i].length);
					int x = 0;
					while(!v.isEmpty())
					{
						Integer I = v.remove(0);
						calculatedDRect.add(I);
						splitMatches[i][x] = I;
						x++;
					}break;
				}
			}
		}
	printResult();
        }
        
	
        public int correctMatches(){
            int nb = 0;
            for(int i = 0; i < normalMatches.length; i++){
                if (normalMatches[i] == null){
                    //System.out.println(i+" does not normal match");
                    continue;
                }
		nb++;
            }
            return nb;
        }
        
        public int missDetections(){
            int nb = 0;
            for(int i = 0; i < G.length; i++)
                if (MatchG(i) == 0)
                    nb++;
            return nb;
        }
        
        public int falseAlarms(){
            int nb = 0;
            for(int i = 0; i < D.length; i++)
                if (MatchD(i) == 0)
                    nb++;
            return nb;
        }
        
	public void printResult(){
		System.out.println("Normal matches:");
		for(int i = 0; i < normalMatches.length; i++){
			if (normalMatches[i] == null)
				continue;
			
			System.out.print("("+i+", "+normalMatches[i][0]+")\t");
		}
		
		System.out.println("\nMerge matches:");
		
		for(int i = 0; i < mergeMatches.length; i++){
			if (mergeMatches[i] == null)
				continue;
			
			System.out.print(i+"\t{\t");
			for(int j = 0; j < mergeMatches[i].length; j++)
				System.out.print(mergeMatches[i][j]+"\t");
			System.out.println("}");
		}
		
		System.out.println("\nSplit matches:");
		
		for(int i = 0; i < splitMatches.length; i++){
			if (splitMatches[i] == null)
				continue;
			
			System.out.print(i+"\t{\t");
			for(int j = 0; j < splitMatches[i].length; j++)
				System.out.print(splitMatches[i][j]+"\t");
			System.out.println("}");
		}
	}
	
	public double Rob(){
		double recall = 0;
		for(int i = 0; i < G.length; i++)
		{
			recall += MatchG(i);
		}

		recall = recall /G.length;
		return recall;
	}
	
	public double Pob(){
		double precision = 0;
		for(int i = 0; i < D.length; i++)
		{
			precision += MatchD(i);
		}
		
		precision = precision /D.length;
		return precision;
	}
	
	/* prints recall and precision */
	public void printRP(){
		
		double recall = Rob(), precision = Pob();

		System.out.println("Recall = "+recall+"\nPrecision = "+precision);
	}
	
}
