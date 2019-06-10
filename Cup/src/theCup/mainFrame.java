package theCup;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.opencv.core.Mat;

public class mainFrame extends JFrame{
Container contentPane;
JPanel topPanel;
JPanel leftPanel;
ImagePanel centerPanel;
JPanel rightPanel;
JPanel bottomPanel;
VideoCap videoCap;

public mainFrame(){
this.setDefaultCloseOperation(EXIT_ON_CLOSE);
contentPane = this.getContentPane(); 
}

public static void main(String[] args) {
	mainFrame myFrame = new mainFrame();
	myFrame.create();
}

public void create() {
	videoCap = new VideoCap(0);
	videoCap.read();
    int height = videoCap.getImage(videoCap.getCapturedMat()).getHeight();
    int width = videoCap.getImage(videoCap.getCapturedMat()).getWidth();
	this.setSize(new Dimension(width, height));
	topPanel = new JPanel();
	//leftPanel = new JPanel();
	centerPanel = new ImagePanel(videoCap);
	//rightPanel = new JPanel();
	//bottomPanel = new JPanel();
	topPanel.setBackground(Color.black);
	//leftPanel.setBackground(Color.green);
	//centerPanel.setBackground(Color.black);
	//rightPanel.setBackground(Color.blue);
	//bottomPanel.setBackground(Color.black);
	contentPane.add(topPanel, BorderLayout.PAGE_START);
	//contentPane.add(leftPanel, BorderLayout.LINE_START);
	contentPane.add(centerPanel, BorderLayout.CENTER);
	//contentPane.add(rightPanel, BorderLayout.LINE_END);
	//contentPane.add(bottomPanel, BorderLayout.PAGE_END);
	this.setVisible(true);
	mainThread myThread = new mainThread();
	myThread.start();
}

public void paint(Graphics g) {
	super.paint(g);
}

class mainThread extends Thread{

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
