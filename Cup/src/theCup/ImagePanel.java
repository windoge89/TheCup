package theCup;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.opencv.core.*;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.tracking.Tracker;
import org.opencv.tracking.TrackerBoosting;
import org.opencv.tracking.TrackerCSRT;
import org.opencv.tracking.TrackerGOTURN;
import org.opencv.tracking.TrackerKCF;
import org.opencv.tracking.TrackerMIL;
import org.opencv.tracking.TrackerMOSSE;
import org.opencv.tracking.TrackerMedianFlow;
import org.opencv.tracking.TrackerTLD;

public class ImagePanel extends JPanel{
	VideoCap videoCap;
	Rect2d trackingRect;
	boolean drawRect;
	boolean updateCheck;
	boolean trackerInit;
	boolean drawArrow;
	Tracker myTracker;
	Pipeline pipeline;
	Point oldPoint;
	Point newPoint;
	double oldTime;
	
	public ImagePanel(VideoCap vc) {
		pipeline = new Pipeline();
		videoCap = vc;
		videoCap.read();
		trackingRect = new Rect2d();
		drawRect = false;
		updateCheck = false;
		trackerInit = false;
		drawArrow = false;
		myTracker = TrackerCSRT.create();
		oldPoint = new Point();
		newPoint = new Point();
		oldTime = System.currentTimeMillis();
		oldPoint.x = 200;
		oldPoint.y = 200;
		myMouseListener listener = new myMouseListener(); 
        addMouseListener(listener);
        addMouseMotionListener(listener);
	}
	
class myMouseListener extends MouseAdapter{
	 public void mousePressed(MouseEvent e) {
         trackingRect.x = e.getX();
         trackingRect.y = e.getY();
         drawRect = false;
     }
	 public void mouseDragged(MouseEvent e) { 
         trackingRect.width = e.getX() - trackingRect.x;
         trackingRect.height = e.getY() - trackingRect.y;
         drawRect = true;
     }
	 public void mouseReleased(MouseEvent e) {
		 trackingRect.width = e.getX() - trackingRect.x;
         trackingRect.height = e.getY() - trackingRect.y;
         if(!trackerInit) {
        	 pipeline.process(videoCap.getCapturedMat());
         myTracker.init(pipeline.maskOutput(), trackingRect);
         trackerInit = true;
         }
         drawRect = true;
       }
}
   

    @Override
    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	videoCap.read();
    	Mat image = videoCap.getCapturedMat();
    	
        if(drawRect) {
        	if(trackerInit) {
        	pipeline.process(videoCap.getCapturedMat());
        	updateCheck = myTracker.update(pipeline.maskOutput(), trackingRect);
        	if(drawArrow) {
        	oldPoint.x = newPoint.x;
        	oldPoint.y = newPoint.y;
        	newPoint.x = (trackingRect.tl().x-trackingRect.br().x)/2 + trackingRect.br().x;
    		newPoint.y = (trackingRect.tl().y-trackingRect.br().y)/2 + trackingRect.br().y;
    		if((Math.pow((newPoint.x - oldPoint.x), 2) + Math.pow((newPoint.y - oldPoint.y),2)) > 4) {
           Imgproc.arrowedLine(
        			image,
        			ToolBox.predictPosition(oldPoint, newPoint, System.currentTimeMillis()-oldTime)[0],
        			ToolBox.predictPosition(oldPoint, newPoint, System.currentTimeMillis()-oldTime)[1],
        			new Scalar(255,0,0),
        			5);
    		}
        	oldTime = System.currentTimeMillis();
        	}else {
        		newPoint.x = (trackingRect.tl().x-trackingRect.br().x)/2 + trackingRect.br().x;
        		newPoint.y = (trackingRect.tl().y-trackingRect.br().y)/2 + trackingRect.br().y;
             	oldTime = System.currentTimeMillis();
             	drawArrow = true;
        		
        	}
        	if(!updateCheck) {trackerInit = false;
        	drawArrow = false;
        	myTracker = TrackerCSRT.create();}
        	}
        	Imgproc.rectangle (
                    image,
                    trackingRect.tl(),
                    trackingRect.br(),
                    new Scalar(0, 0, 255),     
                    5                          
                 );
        }
        g.drawImage(videoCap.getImage(image), 0, 0, this);
        
    }

}