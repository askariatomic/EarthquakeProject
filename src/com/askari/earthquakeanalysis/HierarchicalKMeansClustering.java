package com.askari.earthquakeanalysis;

import java.util.Arrays;
import java.util.Random;

public class HierarchicalKMeansClustering {
	public static double[] getHierarchicalKMeansClustering(double[][] data, int k) {
		int numberOfComputation = 10;
		double[][] dataset = new double[k * numberOfComputation][data[0].length];
		int counter = 0;
		
		for (int x = 0; x < numberOfComputation; x++) {
			boolean moving = true;
			
			double[][] initCentroid = getRandom(data, k);		
			double[] cluster = getEuclideanDistance(data, initCentroid, k);
			
			double[][] centroid = new double[k][data[0].length];
			while (moving) {
				centroid = getCentroid(data, cluster, k);
				double[] tempCluster = getEuclideanDistance(data, centroid, k);			
				
				if (Arrays.equals(cluster, tempCluster)) {
					moving = false;
				}
				cluster = Arrays.copyOf(tempCluster, tempCluster.length);
			}
			
			for (int p = 0; p < k; p++) {
				for (int q = 0; q < data[0].length; q++) {
					dataset[counter][q] = centroid[p][q];
				}
				counter++;
			}
		}
				
		double[][] centroidHierarchicalClustering = getHierarchicalClustering(dataset, k);
		boolean moving = true;
		double[] cluster = getEuclideanDistance(data, centroidHierarchicalClustering, k);
		double[][] centroid = new double[k][data[0].length];
		
		while (moving) {
			centroid = getCentroid(data, cluster, k);
			double[] tempCluster = getEuclideanDistance(data, centroid, k);			
			
			if (Arrays.equals(cluster, tempCluster)) {
				moving = false;
			}
			cluster = Arrays.copyOf(tempCluster, tempCluster.length);
		}
		
		return cluster;
	}

	
	public static double[][] getRandom(double[][] data, int k) {
		double[][] random = new double[k][data[0].length];
		
		for (int a = 0; a < k; a++) {
			int rand = new Random().nextInt(data.length);
			for (int b = 0; b < data[0].length; b++) {
				random[a][b] = data[rand][b];
			}
		}
		return random;
	}
	
	public static double[] getEuclideanDistance(double[][] data, double[][] centroid, int k) {
		double[][] cluster = new double[data.length][k];
		double[] min = new double[data.length];
		double[] clusterClass = new double[data.length];
		double dist = 0;
		
		for (int a = 0; a < k; a++) {
			for (int b = 0; b < data.length; b++) {
				for (int c = 0; c < data[0].length; c++) {
					dist += Math.pow((data[b][c] - centroid[a][c]), 2);
				}
				cluster[b][a] = Math.sqrt(dist);
				dist = 0;
			}
		}
		
		for (int i = 0; i < cluster.length; i++) {
			min[i] = cluster[i][0];
			for (int j = 0; j < cluster[0].length; j++) {
				if (cluster[i][j] < min[i]) {
					min[i] = cluster[i][j];
					clusterClass[i] = j;
				}
			}
		}
		return clusterClass;
	}
	
	public static double[][] getEuclideanDistanceHierarchical(double[][] data, double[][] centroid, int k) {
		double[][] cluster = new double[data.length][k];
		double dist = 0;
		
		for (int a = 0; a < k; a++) {
			for (int b = 0; b < data.length; b++) {
				for (int c = 0; c < data[0].length; c++) {
					dist += Math.pow((data[b][c] - centroid[a][c]), 2);
				}
				cluster[b][a] = Math.sqrt(dist);
				dist = 0;
			}
		}
		return cluster;
	}
	
	public static double[][] getCentroid(double[][] data, double[] cluster, int k) {
		double[][] centroid = new double[k][data[0].length];
		int count = 0;
		int a, b;
		
		for (int i = 0; i < k; i++) {
			for (a = 0; a < data.length; a++) {
				if (cluster[a] == i) {
					count++;
					for (b = 0; b < data[0].length; b++) {
						centroid[i][b] += data[a][b];
					}
				}
			}
			
			for (int c = 0; c < centroid[0].length; c++) {
				centroid[i][c] = centroid[i][c] / count;
			}
			count = 0;
		}
		return centroid;
	}
	
	public static double[][] getCentroidHierarchical(double[][] data, int[] cluster, int k) {
		double[][] centroid = new double[k][data[0].length];
		int count = 0;
		
		for (int i = 0; i < k; i++) {
			for (int a = 0; a < data.length; a++) {
				if (cluster[a] == i) {	
					for (int b = 0; b < data[0].length; b++) {
						centroid[i][b] += data[a][b];
						count++;
					}
				}
			}
			
			for (int c = 0; c < centroid[0].length; c++) {
				centroid[i][c] = centroid[i][c] / (count / data[0].length);
			}
			count = 0;
		}
		return centroid;
	}
	
	public static double[][] getHierarchicalClustering(double[][] data, int k) {
		int n = data.length;
		int counter = 0;
		
		double[][] distance = getEuclideanDistanceHierarchical(data, data, data.length);
		int[] classData = new int[data.length];
		
		while (n > k) {
			double min = Integer.MAX_VALUE;
			int x = 0;
			int y = 0;
			
			for (int i = 0; i < distance.length; i++) {
				for (int j = i + 1; j < distance[0].length; j++) {
					if (distance[i][j] != 0 && distance[i][j] < min) {
						min = distance[i][j];
						x = i;
						y = j;
					}
				}
			}
			
			distance[x][y] = 0.0;
			distance[y][x] = 0.0;
			
			if (counter == 0) {
				counter++;
				classData[x] = counter;
				classData[y] = counter;
			} else if (classData[x] == 0 && classData[y] == 0) {
				counter++;
				classData[x] = counter;
				classData[y] = counter;
			} else if (classData[x] != 0 && classData[y] == 0) {
				classData[y] = classData[x];
			} else if (classData[x] == 0 && classData[y] != 0) {
				classData[x] = classData[y];
			} else if (classData[x] > classData[y]) {
				int dataX = classData[x];
				for (int d = 0; d < classData.length; d++) {
					if (classData[d] == dataX) {
						classData[d] = classData[y];
					}
				}
			} else if (classData[y] > classData[x]) {	
				int dataY = classData[y];
				for (int s = 0; s < classData.length; s++) {
					if (classData[s] == dataY) {
						classData[s] = classData[x];
					}
				}
			}	else if (classData[x] == classData[y]) {
				n++;
			}

			// Optimal for now!
			for (int i = 0; i < distance.length; i++) {
				if (distance[i][x] == distance[i][y]) {
					continue;
				} else if (distance[i][x] > distance[i][y]) {
					distance[i][x] = 0;
					distance[x][i] = 0;
				} else {
					distance[i][y] = 0;
					distance[y][i] = 0;
				}
			}
			
			n--;
		}
		
		int counterClass = 0;
		boolean found = false;
		for (int i = 0; i < classData.length; i++) {
			for (int j = 0; j < classData.length; j++) {
				if (classData[j] == i) {
					found = true;
					classData[j] = counterClass;
				}
			}
			if (found == true) {
				counterClass++;
			} else {
				continue;
			}
			found = false;
		}
		
		if (counterClass < k) {
			for (int a = 0; a < classData.length; a++) {
				if (classData[a] == 0) {
					classData[a] = counterClass;
					counterClass++;
				}
				
				if (counterClass == k) {
					break;
				}
			}
		}
		
		double[][] centroid = getCentroidHierarchical(data, classData, k);
		
		return centroid;
	}
}