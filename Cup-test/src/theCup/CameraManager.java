package theCup;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect2d;
import org.opencv.tracking.*;


import javafx.geometry.Point3D;

public class CameraManager {
	
	ArrayList<Camera> Cameras = new ArrayList();
	ArrayList<Tracker> Trackers = new ArrayList();
	ArrayList<Rect2d> TrackingRects = new ArrayList();
	
	public void addCamera(int source, String name, String tracker) {
		Camera c = new Camera();
		c.setName(name);
		VideoCap vc = new VideoCap(source);
		c.setVideoCap(vc);
		Cameras.add(c);
		Rect2d tr = new Rect2d();
		TrackingRects.add(tr);
		if(tracker == "TrackerBoosting") {
			Tracker t = TrackerBoosting.create();
			Trackers.add(t);
		}else if(tracker == "TrackerCSRT") {
			Tracker t = TrackerCSRT.create();
			Trackers.add(t);
		}else if(tracker == "TrackerKCF") {
			Tracker t = TrackerKCF.create();
			Trackers.add(t);
		}else{
			System.out.println("INVALID TRACKER. DEFAULTING TO TrackerCSRT");
			Tracker t = TrackerCSRT.create();
			Trackers.add(t);
		}
	}public void setTrackingRect(int cameraNumber, Rect2d tr) {
		TrackingRects.set(cameraNumber, tr);
	}
	public void initializeCamera(int cameraNumber) {
		Cameras.get(cameraNumber).read();
   	 	Mat image = Cameras.get(cameraNumber).getCapturedMat();
   	 	Trackers.get(cameraNumber).init(image, TrackingRects.get(cameraNumber));
	}public void update() {
		for(int x=0;x<Cameras.size();x++) {
			//Cameras.get(x).read();
	   	 	Mat image = Cameras.get(x).getCapturedMat();
			boolean updateCheck = Trackers.get(x).update(image, TrackingRects.get(x));
    		if(!updateCheck) {
    			Cameras.get(x).FP = null;
    			System.out.println("PROBLEM WITH CAMERA #"+x);
    		}else {
    			Point center = new Point();
    			center.x = (TrackingRects.get(x).tl().x + TrackingRects.get(x).br().x)/2;
    			center.y = (TrackingRects.get(x).tl().y + TrackingRects.get(x).br().y)/2;
    			//Cameras.get(x).setFP(center);
    		}
		}
	}public Point3D calculatePositionUsingCameras() {
		ArrayList<Point3D> points = new ArrayList();
		for (int x=1; x<Cameras.size();x++) {
			for (int y=0; y<(Cameras.size()-1);y++) {
				if((Cameras.get(x).getFP() != null) && (Cameras.get(y).getFP() != null)) {
					points.add(this.findIntersection(Cameras.get(x), Cameras.get(y)));
				}
			}
		}
		Point3D sumPoint = new Point3D(0,0,0);
		for (int z=0;z<points.size();z++) {
			sumPoint.add(points.get(z));
		}
		return sumPoint.multiply(1/points.size());
	}private double dFunc(Point3D p1, Point3D p2, Point3D p3, Point3D p4) {
		return (p1.getX() - p2.getX())*(p3.getX() - p4.getX())+
				(p1.getY() - p2.getY())*(p3.getY() - p4.getY())+
				(p1.getZ() - p2.getZ())*(p3.getZ() - p4.getZ());
	}public Point3D findIntersection(Camera c1, Camera c2) {
		double Ma = ((dFunc(c1.getLocation(),c2.getLocation(),c2.getFP(),c2.getLocation())
				*dFunc(c2.getFP(),c2.getLocation(),c1.getFP(),c1.getLocation()))
				+
				(dFunc(c2.getFP(),c2.getLocation(),c2.getFP(),c2.getLocation())
				*dFunc(c1.getLocation(),c2.getLocation(),c1.getFP(),c1.getLocation())))
				/
				((dFunc(c1.getFP(),c1.getLocation(),c1.getFP(),c1.getLocation())
				*dFunc(c2.getFP(),c2.getLocation(),c2.getFP(),c2.getLocation()))
				+
				(dFunc(c1.getFP(),c1.getLocation(),c2.getFP(),c2.getLocation())
				*dFunc(c2.getFP(),c2.getLocation(),c1.getFP(),c1.getLocation())));
		double Mb = (dFunc(c1.getLocation(), c2.getLocation(), c2.getFP(), c2.getLocation())
				+
				Ma*dFunc(c1.getFP(), c1.getLocation(), c2.getFP(), c2.getLocation()))
				/
				dFunc(c2.getFP(), c2.getLocation(), c2.getFP(), c2.getLocation());
		Point3D Pa = c1.getFP().subtract(c1.getLocation()).multiply(Ma).add(c1.getLocation());
		Point3D Pb = c2.getFP().subtract(c2.getLocation()).multiply(Mb).add(c2.getLocation());
		return Pa.midpoint(Pb);
	}
}
