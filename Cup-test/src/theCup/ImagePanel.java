package theCup;

import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.*;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.tracking.Tracker;
import org.opencv.tracking.TrackerCSRT;
import org.opencv.tracking.TrackerKCF;
import org.opencv.tracking.TrackerMIL;
import org.opencv.tracking.TrackerMOSSE;
import org.opencv.tracking.TrackerTLD;

public class ImagePanel extends JPanel{
	CameraManager cm;
	VideoCap videoCap;
	Rect2d trackingRect;
	Rect2d predictionRect;
	boolean drawRect;
	boolean updateCheck;
	boolean trackerInit;
	Tracker myTracker;
	Pipeline pipeline;
	ToolBox toolBox;
	double height;
	double width;
	double startTime;
	double iterations;
	
	public ImagePanel(VideoCap vc) {
		cm = new CameraManager();
		cm.addCamera(0, "camera1", "TrackerCSRT");
		pipeline = new Pipeline();
		videoCap = vc;
		videoCap.read();
		trackingRect = new Rect2d();
		predictionRect = new Rect2d();
		drawRect = false;
		updateCheck = false;
		trackerInit = false;
		toolBox = new ToolBox();
		startTime = (System.currentTimeMillis()/1000);
		iterations = 0;
		myTracker = TrackerCSRT.create();
		myMouseListener listener = new myMouseListener(); 
        addMouseListener(listener);
        addMouseMotionListener(listener);
	}
	
class myMouseListener extends MouseAdapter{
	 @Override
	public void mousePressed(MouseEvent e) {
         trackingRect.x = e.getX();
         trackingRect.y = e.getY();
         drawRect = false;
         cm.setTrackingRect(0,trackingRect);
     }
	 @Override
	public void mouseDragged(MouseEvent e) { 
         trackingRect.width = e.getX() - trackingRect.x;
         trackingRect.height = e.getY() - trackingRect.y;
         drawRect = true;
         cm.setTrackingRect(0,trackingRect);
     }
	 @Override
	public void mouseReleased(MouseEvent e) {
		 trackingRect.width = e.getX() - trackingRect.x;
         trackingRect.height = e.getY() - trackingRect.y;
         if(!trackerInit) {
        	 cm.setTrackingRect(0,trackingRect);
        	 cm.initializeCamera(0);
        	 trackerInit = true;
        	 /*videoCap.read();
        	 Mat image = videoCap.getCapturedMat();
        	 pipeline.process(videoCap.getCapturedMat());
        	 myTracker.init(image, trackingRect);
        	 trackerInit = true;*/
         }
         drawRect = true;
       }
}
   

    @Override
    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	//videoCap.read();
    	//Mat image = videoCap.getCapturedMat();
    	cm.Cameras.get(0).read();
    	Mat image = cm.Cameras.get(0).getCapturedMat();
    	iterations++;
		//System.out.println(iterations/(System.currentTimeMillis()/1000 - startTime));

    	if(drawRect) {
        	if(trackerInit) {
        		cm.update();
        		/*pipeline.process(image);
        		updateCheck = myTracker.update(image, trackingRect);
        		if(!updateCheck) {
        			trackerInit = false;
        			myTracker = TrackerCSRT.create();
        		}*/
        		
        		
            	
        	}
        
        	Imgproc.rectangle (image,cm.TrackingRects.get(0).tl(),cm.TrackingRects.get(0).br(),new Scalar(0, 0, 255),5);
        	System.out.println(cm.TrackingRects.get(0).tl());
        }
   
        g.drawImage(videoCap.getImage(image), 0, 0, this);
        g.drawImage(videoCap.getImage(image), 0, 0, this);
        
    }

}