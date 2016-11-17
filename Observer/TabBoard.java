package Observer;
import java.util.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
class BoxObservable extends Observable{
	public void notifyObservers(Observer arg){
		setChanged();
		super.notifyObservers(arg);
	}
}

class Box extends Canvas implements Observer{
	/**
	 * 
	 */
	private BoxObservable observable;
	private static final long serialVersionUID = -5014861435630112917L;
	public int x, y;
	public Color[] colors = {Color.black, Color.red, Color.blue, Color.gray, Color.green};
	public Color color = newColor();
	public Color newColor(){
		return colors[(int)(Math.random()*colors.length)];
	}
	public Box(int x, int y, final BoxObservable observable){
		this.x = x;
		this.y = y;
		this.observable = observable;
		observable.addObserver(this);
		addMouseListener(new ML());
	}
	class ML extends MouseAdapter{
		public void mousePressed(MouseEvent e){
			observable.notifyObservers(Box.this);
		}
	}
	
	public void update(Observable o, Object arg) {
		Box box = (Box)arg;
		System.out.println(""+box.x+box.y);

		if(Math.abs(x - box.x) <= 1 && Math.abs(y - box.y) <= 1){
			color = newColor();

			repaint();
		}
	}
	
	public void paint(Graphics g){
		g.setColor(color);
		Dimension s = getSize();
		g.fillRect(x, y, s.width, s.height);
	}
	
}

public class TabBoard extends Frame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4222070763969113853L;
	public BoxObservable observable = new BoxObservable();
	public TabBoard(int grid){
		setTitle("demostration of observer");
		setLayout(new GridLayout(grid, grid));
		for(int i = 0; i < grid; i++){
			for(int j = 0; j < grid; j++){
				add(new Box(i, j, observable));
			}
		}
	}
	public static void main(String[] args) {
		DataInputStream in = new DataInputStream(System.in);
		int temp = 4;
//		try {
//			 temp = in.readInt();              // a problem i can't get the correct int value.
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			temp = 4;
//		}
		TabBoard tab = new TabBoard(temp);
		tab.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				System.exit(0);
			}
		});
		tab.setSize(100, 100);
		tab.setVisible(true);
	}
}
