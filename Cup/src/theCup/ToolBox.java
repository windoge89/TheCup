package theCup;

import org.opencv.core.Point;

public class ToolBox {
public static Point[] predictPosition(Point p0, Point p1, double time) {
	Point p2 = new Point();
	p2.x = (p1.x - p0.x)/(time/200) + p1.x;
	p2.y = (p1.y - p0.y)/(time/200) + p1.y;
	Point[] points = new Point[2];
	points[0] = p1;
	points[1] = p2;
	return points;
}
}
