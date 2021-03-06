package model;

import java.util.Arrays;

import org.bytedeco.opencv.opencv_imgproc.Vec3fVector;

public class VisionSnapShot {
	
	private Vec3fVector balls;
	private int[][] walls;
	private double[] cross;
	private int[][] robot;
	
	
	
	public VisionSnapShot(Vec3fVector balls, int[][] walls, double[] cross, int[][] robot) {
		this.balls = balls;
		this.walls = walls;
		this.cross = cross;
		this.robot = robot;
	}

	public Vec3fVector getBalls() {
		return balls;
	}
	public void setBalls(Vec3fVector balls) {
		this.balls = balls;
	}
	public int[][] getWalls() {
		return walls;
	}
	public void setWalls(int[][] walls) {
		this.walls = walls;
	}
	public double[] getCross() {
		return cross;
	}
	public void setCross(double[] cross) {
		this.cross = cross;
	}
	public int[][] getRobot() {
		return robot;
	}
	public void setRobot(int[][] robot) {
		this.robot = robot;
	}

	@Override
	public String toString() {
		return "VisionSnapShot [\nballs=" + balls + ", \nwalls=" + Arrays.toString(walls) + ", \ncross="
				+ Arrays.toString(cross) + ", \nrobot=" + Arrays.toString(robot) + "]";
	}
	
	
	
}
