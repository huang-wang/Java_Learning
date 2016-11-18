package BinConlector;

import java.util.Enumeration;

public class Rcycle {
	public static void main(String[] args) {
		BinList bins = new BinList();
		bins.add(new Bin(Aluminum.class));
		bins.add(new Bin(Paper.class));
		Bin bin = new Bin(Trash.class);
		bin.add(new Paper(2));
		bin.add(new Aluminum(3));
		bin.add(new Aluminum(2));
		bin.add(new Paper(2.5));
		bins.sortBin(bin);
		Enumeration e = bins.elements();
		while(e.hasMoreElements()){
			Bin b = (Bin)e.nextElement();
			System.out.println(b.getType().getName() + " sumValue = " + Trash.sumValue(b));
		}
	}
}
