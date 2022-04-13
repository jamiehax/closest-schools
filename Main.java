package ClosestSchools;
/*
* Author: Jamie Hackney
* Implements the closest pair of points recursive algorithm
* on locations of K-12 schools in Vermont obtained from http://geodata.vermont.gov/datasets/vt-school-locations-k-12

*/

import java.io.File;
import java.util.Scanner;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Random;
import java.lang.Math;
import java.io.File;

public class Main {

	public static void main(String[] args) throws IOException {

		// Creates an ArrayList containing School objects from the .csv file
		// From: https://stackoverflow.com/questions/49599194/reading-csv-file-into-an-arrayliststudent-java
		String line = null;
		ArrayList<School> schoolList = new ArrayList<School>();
		BufferedReader br = new BufferedReader(new FileReader("ClosestSchools/Data/VT_School_Locations__K12(1).csv"));
		if ((line = br.readLine()) == null) {
			return;
		}
		while ((line = br.readLine()) != null) {
			String[] temp = line.split(",");
			schoolList.add(new School(temp[4], Double.parseDouble(temp[0]), Double.parseDouble(temp[1])));
		}

		// Preprocess the data to create two sorted arrayLists (one by X-coordinate and one by Y-coordinate):
		ArrayList<School> Xsorted = new ArrayList<School>();
		ArrayList<School> Ysorted = new ArrayList<School>();
		Collections.sort(schoolList, new SortbyX());
		Xsorted.addAll(schoolList);
		Collections.sort(schoolList, new SortbyY());
		Ysorted.addAll(schoolList);

		// Run the Recursive Algorithm
		School[] cp = new School[2];
		cp = ClosestPoints(Xsorted, Ysorted);
		if (cp[0] != null)
			System.out.println("The two closest schools are " + cp[0].name + " and " + cp[1].name + ".");

	}

	// Recursive divide and conquer algorithm for closest points
	// sLx should be sorted by x coordinate and sLy should be sorted by y coordinate
	// Returns an array containing the two closest School objects
	public static School[] ClosestPoints(ArrayList<School> sLx, ArrayList<School> sLy) {

		School[] closestPair = new School[2];

		// base case brute force
		if (sLx.size() <= 3) {

			closestPair[0] = sLx.get(0);
			closestPair[1] = sLx.get(1);
			double minDist = getDist(sLx.get(0), sLx.get(1));

			for (int i = 0; i < sLx.size(); i++) {
				for (int k = i + 1; k < sLx.size(); k++) {
					double temp = getDist(sLx.get(i), sLx.get(k));
					if (temp < minDist) {
						minDist = temp;
						closestPair[0] = sLx.get(i);
						closestPair[1] = sLx.get(k);
					}
				}
			}
			return closestPair;
		}

		// divide sLx and sLy into Lx, Ly, and Rx, Ry
		int midX = sLx.size() / 2;
		double midline = (sLx.get(midX).getX() + sLx.get(midX - 1).getX()) / 2;

		// create Lx and Rx around midline
		ArrayList<School> Lx = new ArrayList<School>(sLx.subList(0, midX));
		ArrayList<School> Rx = new ArrayList<School>(sLx.subList(midX, sLx.size()));

		// create Ly and Ry around x midline to maintain sorting
		ArrayList<School> Ly = new ArrayList<School>();
		ArrayList<School> Ry = new ArrayList<School>();

		for (int i = 0; i < sLy.size(); i++) {
			if (sLy.get(i).getX() < midline) {
				Ly.add(sLy.get(i));
			} else {
				Ry.add(sLy.get(i));
			}
		}

		// recursive call
		School[] lPair = new School[2];
		School[] rPair = new School[2];
		lPair = ClosestPoints(Lx, Ly);
		double lDist = getDist(lPair[0], lPair[1]);
		rPair = ClosestPoints(Rx, Ry);
		double rDist = getDist(rPair[0], rPair[1]);
		double delta;

		if (lDist < rDist) {
			closestPair = lPair;
			delta = lDist;
		} else {
			closestPair = rPair;
			delta = rDist;
		}

		// create yDelta
		ArrayList<School> yDelta = new ArrayList<School>();
		for (int i = 0; i < sLy.size(); i++) {
			if (withinDelta(midline, delta, sLy.get(i))) {
				yDelta.add(sLy.get(i));
			}
		}

		// check points in yDelta (only 7 away from current point)
		for (int i = 0; i < yDelta.size(); i++) {
			for (int k = i + 1; k < i + 7 && k < yDelta.size(); k++) {
				double deltaPrime = getDist(yDelta.get(i), yDelta.get(k));
				if (deltaPrime < delta) {
					delta = deltaPrime;
					closestPair[0] = yDelta.get(i);
					closestPair[1] = yDelta.get(k);
				}
			}
		}
		return closestPair;
	}

	public static boolean withinDelta(double midline, double delta, School s) {
		double lowerBound = midline - delta;
		double upperBound = midline + delta;
		if (s.getX() > lowerBound && s.getX() < upperBound) {
			return true;
		} else {
			return false;
		}
	}

	public static double getDist(School s1, School s2) {
		return Math.sqrt(Math.pow(s1.getX() - s2.getX(), 2) + Math.pow(s1.getY() - s2.getY(), 2));
	}

}
