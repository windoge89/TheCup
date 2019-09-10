package theCup;

import org.opencv.core.Mat;
import org.opencv.core.Point;

import javafx.geometry.Point3D;

public class Camera {
	
	String NAME;
	VideoCap VC;
	double X;
	double Y;
	double Z;
	Point3D TL;
	Point3D BR;
	Point3D FP;
	
	public void setName(String name) {
		NAME = name;
	}public void setVideoCap(VideoCap vc) {
		VC = vc;
	}public void setX(double x) {
		X = x;
	}public double getX() {
		return X;
	}public void setY(double y) {
		Y = y;
	}public double getY() {
		return Y;
	}public void setZ(double z) {
		Z = z;
	}public double getZ() {
		return Z;
	}public void setTL(Point3D tl) {
		TL = tl;
	}public Point3D getTL() {
		return TL;
	}public void setBR(Point3D br) {
		BR = br;
	}public Point3D getBR() {
		return BR;
	}public Point3D getLocation() {
		return new Point3D(X,Y,Z);
	}public void orthogonalizeFrustum() {
		double distance1 = this.getTL().distance(X, Y, Z);
		double distance2 = this.getBR().distance(X,Y,Z);
		double distanceAverage = (distance1+distance2)/2;
		Point3D tl = new Point3D(
				(this.getTL().getX()-X)*(distanceAverage/distance1)+X,
				(this.getTL().getY()-Y)*(distanceAverage/distance1)+Y,
				(this.getTL().getZ()-Z)*(distanceAverage/distance1)+Z);
		Point3D br = new Point3D(
				(this.getBR().getX()-X)*(distanceAverage/distance2)+X,
				(this.getBR().getY()-Y)*(distanceAverage/distance2)+Y,
				(this.getBR().getZ()-Z)*(distanceAverage/distance2)+Z);
		this.setTL(tl);
		this.setBR(br);	
	}public void setFP(Point p) {
		double distance1 = Math.pow(Math.pow(p.x, 2) + Math.pow(p.y, 2), 0.5);
		double distance2 = Math.pow(Math.pow(this.VC.getCapturedMat().width(), 2) + Math.pow(this.VC.getCapturedMat().height(), 2), 0.5);
		FP = new Point3D(
				(this.getBR().getX()-this.getTL().getX())*(distance1/distance2)+this.getTL().getX(),
				(this.getBR().getY()-this.getTL().getY())*(distance1/distance2)+this.getTL().getY(),
				(this.getBR().getZ()-this.getTL().getZ())*(distance1/distance2)+this.getTL().getZ());
	}public Point3D getFP() {
		return FP;
	}public Mat getCapturedMat() {
		return VC.getCapturedMat();
	}public void read() {
		VC.read();
	}
	
	
}
