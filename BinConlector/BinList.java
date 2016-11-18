package BinConlector;

import java.util.Enumeration;
import java.util.Vector;

public class BinList extends Vector<Bin> {
	public boolean sortBin(Bin bin){
		Enumeration e = bin.elements();
		while(e.hasMoreElements()){
			Trash t = (Trash)e.nextElement(); 
			if(!sort(t)){
				return true;
			}
		}
		return false;
	}
	public boolean sort(Trash t){
		Enumeration e = elements();
		while(e.hasMoreElements()){
			Bin bin = (Bin)e.nextElement();
			if(bin.grap(t)){
				return true;
			}
		}
		return false;
	}
}
