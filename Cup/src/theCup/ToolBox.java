package theCup;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.opencv.core.Point;

public class ToolBox {
	ArrayList<ArrayList> calibrationMatrix;
	ArrayList<Double> calibrationVector;
	ArrayList<ArrayList> projectileMatrix;
	ArrayList<Double> projectileVector;
	
	public ToolBox() {
		calibrationMatrix = new ArrayList();
		calibrationVector = new ArrayList();
		double[][] XYZD = new double[][] {{1,1,1,2},{-1,1,1,2},{1,-1,1,2},{-1,-1,1,2}};
		addCalibrationEquationsFromEqualExpressions(this.calculateEqualExpressionsFromXYZD(XYZD));
		System.out.println(solveCalibrationEquations());
		System.out.println(calibrationMatrix);
		System.out.println(calibrationVector);
	}
	public int[][] twoLayerTreeHelper(int upperBound){
		int[][] array = new int[upperBound*(upperBound-1)/2][2];
		int x = 0;
			for (int first=0;first<upperBound;first++) {
				for(int second=first+1;second<upperBound;second++) {
					array[x][0] = first;
					array[x][1] = second;
					x++;
				}
			}
		return array;
	}
	public double[][] calculateEqualExpressionsFromXYZD(double[][] array){
		double[][] eeArray = new double[array.length][4];
		int x = 0;
		while(x<array.length) {
			eeArray[x][0] = 2*array[x][0];
			eeArray[x][1] = 2*array[x][1];
			eeArray[x][2] = 2*array[x][2];
			eeArray[x][3] = (Math.pow(array[x][3],2)-Math.pow(array[x][2],2)-Math.pow(array[x][1],2)-Math.pow(array[x][0],2));
			x++;
		}
		return eeArray;
	}
	public void addCalibrationEquationsFromEqualExpressions(double[][] array) {
		int[][] helper = twoLayerTreeHelper(array.length);
		double[][] matrixExpressions = new double[helper.length][array[0].length - 1];
		double[] vectorConstants = new double[helper.length];
		for(int x = 0; x < helper.length; x++) {
			vectorConstants[x] = array[helper[x][1]][array[0].length-1] - array[helper[x][0]][array[0].length-1];
			for(int y = 0; y < (matrixExpressions[0].length); y++) {
				matrixExpressions[x][y] = array[helper[x][0]][y] - array[helper[x][1]][y];
			}
		}
		int x = 0;
		while(x < helper.length) {
			addCalibrationEquation(matrixExpressions[x], vectorConstants[x]);
			x++;
		}
	}
	public double[] arrayList2array(ArrayList arraylist){
		double[] array = new double[arraylist.size()];
		for (int column=0;column<arraylist.size();column++) {
				array[column] = (double)arraylist.get(column);	
		}
		return array;
	}
	public double[][] arrayList2array2D(ArrayList<ArrayList> arraylist){
		double[][] array = new double[arraylist.size()][arraylist.get(0).size()];
		for (int column=0;column<arraylist.size();column++) {
			for(int row=0;row<arraylist.get(0).size();row++) {
				array[column][row] = (double)arraylist.get(column).get(row);
			}
		}
		return array;
	}
	public void resetCalibrationEquations() {
		calibrationMatrix = new ArrayList();
		calibrationVector = new ArrayList();
	}
	public void addCalibrationEquation(double[] matrixExpression, double vectorConstant) {
		ArrayList<Double> cM = new ArrayList();
		for(int x=0;x<matrixExpression.length;x++) {
		cM.add(matrixExpression[x]);
		}
		calibrationMatrix.add(cM);
		calibrationVector.add(vectorConstant);
	}
	public RealVector solveCalibrationEquations() {
		DecompositionSolver solver = 
		new SingularValueDecomposition(new Array2DRowRealMatrix(arrayList2array2D(calibrationMatrix),false)).getSolver();
		RealVector solution = solver.solve(new ArrayRealVector(arrayList2array(calibrationVector), false));
		return solution;
	}
	public void resetProjectileEquations() {
		projectileMatrix = new ArrayList();
		projectileVector = new ArrayList();
	}
	public void addProjectileEquation(double[] matrixExpression, double vectorConstant) {
		ArrayList<Double> pM = new ArrayList();
		for(int x=0;x<matrixExpression.length;x++) {
		pM.add(matrixExpression[x]);
		}
		projectileMatrix.add(pM);
		projectileVector.add(vectorConstant);
	}
	public RealVector solveProjectileEquations() {
		DecompositionSolver solver = 
		new SingularValueDecomposition(new Array2DRowRealMatrix(arrayList2array2D(projectileMatrix),false)).getSolver();
		RealVector solution = solver.solve(new ArrayRealVector(arrayList2array(projectileVector), false));
		return solution;
	}

	
}
