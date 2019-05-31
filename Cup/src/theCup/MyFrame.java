package theCup;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.*;
import javax.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;

import org.opencv.core.*;
import org.opencv.core.Core.*;

import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.*;
import org.opencv.objdetect.*;
import java.awt.image.BufferedImage;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;



public class MyFrame extends JFrame{
    private JPanel contentPane;
    double startTime = System.currentTimeMillis();
    NeonPipeline CV = new NeonPipeline();
    public static void main(String[] args) {
    	
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MyFrame frame = new MyFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public MyFrame() {
    	
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, 1280, 720);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        new MyThread().start();
    }
    
    VideoCap videoCap = new VideoCap(1);
    
    public void paint(Graphics g){
        g = contentPane.getGraphics();
        videoCap.read();
        Mat image = videoCap.getCapturedMat();
        Image newImage = toolBox.findCircles(image);
        //CV.process(image);
        //Point center = toolBox.findCentroid(CV.hsvThresholdOutput());
        //if(Double.toString(center.x) != "NaN") {
        //CV.addPoint((Point)center);
        //}
        Mat output = CV.hsvThresholdOutput();
        System.out.println(1000*CV.getPointsSize()/(System.currentTimeMillis()-this.startTime));
        try {
        	//if(CV.getPointsSize() < 25) {
			//g.drawImage(toolBox.drawCircle(image, center, new Scalar(200, 0, 0)), 0, 0, this);
        	//}else {
        	/*int x = 1;
        	while(x<6) {
        		image = toolBox.image2Mat(toolBox.drawCircle(image, CV.getPoint(CV.getPointsSize()-x), new Scalar(200, 0, 0)));
        		x++;
        	}
        	Point prediction = new Point();
        	*/		
        	//g.drawImage(toolBox.drawCurve(image, CV.points),0,0,this);
        		g.drawImage(newImage,0,0,this);
        	//}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    class MyThread extends Thread{
    	
        @Override
        public void run() {
        	
            for (;;){
                repaint();
                try { Thread.sleep(30);
                } catch (InterruptedException e) {    }
            }
        }
    }
}