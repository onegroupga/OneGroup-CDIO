package model;
import java.io.Serializable;

public class Coordinate implements Serializable {
	
	private static final long serialVersionUID = 3869446341373817110L;
	
	public double x, y;
	
	public Coordinate(double x, double y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "Coordinate [x=" + x + ", y=" + y + "]";
	}
	
}
