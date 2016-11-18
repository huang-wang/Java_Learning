package BinConlector;

import java.util.Vector;

public class Bin extends Vector {
	private Class bin;
	public Bin(Class param){
		bin = param;
	}
	public Class getType(){
		return bin;
	}
	public boolean grap(Trash trash){
		if(trash.getClass().equals(bin)){
			add(trash);
			return true;
		}else{
			return false;
		}
	}
}
