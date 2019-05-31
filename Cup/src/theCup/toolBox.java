package theCup;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.awt.image.BufferedImage;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


public class toolBox {
	public static Mat bufferedImage2Mat(BufferedImage in)
    {
          Mat out;
          byte[] data;
          int r, g, b;
          int height = in.getHeight();
          int width = in.getWidth();
          if(in.getType() == BufferedImage.TYPE_INT_RGB || in.getType() == BufferedImage.TYPE_INT_ARGB)
          {
              out = new Mat(height, width, CvType.CV_8UC3);
              data = new byte[height * width * (int)out.elemSize()];
              int[] dataBuff = in.getRGB(0, 0, width, height, null, 0, width);
              for(int i = 0; i < dataBuff.length; i++)
              {
                  data[i*3 + 2] = (byte) ((dataBuff[i] >> 16) & 0xFF);
                  data[i*3 + 1] = (byte) ((dataBuff[i] >> 8) & 0xFF);
                  data[i*3] = (byte) ((dataBuff[i] >> 0) & 0xFF);
              }
          }
          else
          {
              out = new Mat(height, width, CvType.CV_8UC1);
              data = new byte[height * width * (int)out.elemSize()];
              int[] dataBuff = in.getRGB(0, 0, width, height, null, 0, width);
              for(int i = 0; i < dataBuff.length; i++)
              {
                r = (byte) ((dataBuff[i] >> 16) & 0xFF);
                g = (byte) ((dataBuff[i] >> 8) & 0xFF);
                b = (byte) ((dataBuff[i] >> 0) & 0xFF);
                data[i] = (byte)((0.21 * r) + (0.71 * g) + (0.07 * b)); //luminosity
              }
           }
           out.put(0, 0, data);
           return out;
     }
    
    public static Mat image2Mat( Image image) {

    	
    	BufferedImage bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(image, 0, 0, null);
        bGr.dispose();

        return bufferedImage2Mat( bimage);

    }
    
    public static Point quadraticInterpolation(Point p1, Point p2, Point p3) {
    	
    	Point p4 = new Point();
    	p4.x = p1.x + 2*(p1.x - p3.x);
    	p4.y = p3.y*(p4.x - p1.x)*(p4.x-p2.x)/((p3.x - p1.x)*(p3.x-p2.x))+
    			p2.y*(p4.x - p1.x)*(p4.x-p3.x)/((p2.x - p1.x)*(p2.x-p3.x))+
    			p1.y*(p4.x - p3.x)*(p4.x-p2.x)/((p1.x - p3.x)*(p1.x-p2.x));
    	return p4;
    }
    
    public static Image drawCircle(Mat matrix, Point CENTER, Scalar scalar) throws Exception {
        
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
        Imgproc.circle (
           matrix,                 //Matrix obj of the image
           CENTER,  5,                  //Radius
           scalar,  //Scalar object for color
           10                      //Thickness of the circle
        );
      
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", matrix, matOfByte);

        byte[] byteArray = matOfByte.toArray();

        InputStream in = new ByteArrayInputStream(byteArray);
        BufferedImage bufImage = ImageIO.read(in);
   
        return bufImage;
     }
    public static Point findCentroid(Mat map) {
    	Moments moments = Imgproc.moments(map);
    	Point centroid = new Point();
    	centroid.x = moments.get_m10() / moments.get_m00();
    	centroid.y = moments.get_m01() / moments.get_m00();
    	return centroid;
    }
    
    public static Image drawCurve(Mat matrix, ArrayList<Point> points) throws Exception {
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
        List<MatOfPoint> list = new ArrayList();
        List<Point> pointsL = new ArrayList();
        int x = 1;
        while (x < 20) {
        	pointsL.add(points.get(points.size()-x));
        	x++;
        }
        MatOfPoint MOP = new MatOfPoint ();
        MOP.fromList(pointsL);
        list.add(
              MOP 
        );
        // Drawing polylines
        Imgproc.polylines (
           matrix,                    // Matrix obj of the image
           list,                      // java.util.List<MatOfPoint> pts
           false,                     // isClosed
           new Scalar(200, 0, 200),     // Scalar object for color
           8                          // Thickness of the line
        );
        // Encoding the image
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", matrix, matOfByte);

        // Storing the encoded Mat in a byte array
        byte[] byteArray = matOfByte.toArray();

        // Displaying the image
        InputStream in = new ByteArrayInputStream(byteArray);
        BufferedImage bufImage = ImageIO.read(in);

        return bufImage;
     }
    
    public static Image findCircles (Mat src) {
    	try {
    	Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);

        Imgproc.medianBlur(gray, gray, 5);

        Mat circles = new Mat();
        Imgproc.HoughCircles(gray, circles, Imgproc.HOUGH_GRADIENT, 1.0,
                (double)gray.rows()/16, // change this value to detect circles with different distances to each other
                100.0, 30.0, 1, 30); // change the last two parameters
                // (min_radius & max_radius) to detect larger circles

        for (int x = 0; x < circles.cols(); x++) {
            double[] c = circles.get(0, x);
            Point center = new Point(Math.round(c[0]), Math.round(c[1]));
            // circle center
            Imgproc.circle(src, center, 1, new Scalar(0,100,100), 3, 8, 0 );
            // circle outline
            int radius = (int) Math.round(c[2]);
            Imgproc.circle(src, center, radius, new Scalar(255,0,255), 3, 8, 0 );
        }
        
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", src, matOfByte);

        byte[] byteArray = matOfByte.toArray();

        InputStream in = new ByteArrayInputStream(byteArray);
        BufferedImage bufImage = ImageIO.read(in);
   
        return bufImage;
    	}catch(Exception e) {
    	Mat2Image mat2Img = new Mat2Image();
    	Image newI = mat2Img.getImage(src);
    	return newI;
    	}
    	
    
    }

}
