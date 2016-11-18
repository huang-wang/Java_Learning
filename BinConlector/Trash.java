package BinConlector;

import java.util.Vector;
import java.util.*;
public abstract class Trash {
	private double weight;
	public Trash(double weight){
		this.weight = weight;
	}
	public abstract double getVal();
	public double weight(){
		return weight;
	}
	public static double sumValue(Vector bin){
		Enumeration e = bin.elements();
		double sum = 0.0;
		while(e.hasMoreElements()){
			Trash t = (Trash)e.nextElement();
			sum += t.weight() * t.getVal();
		}
		return sum;
	}
}
