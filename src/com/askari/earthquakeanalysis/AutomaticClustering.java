package com.askari.earthquakeanalysis;

import java.util.Arrays;

public class AutomaticClustering {
	public static int getOptimalK (double[][] data) {
		double[] variance = new double[17];
		double[] globalOptimum = new double[17];
		
		int k = 2;
		int n = data.length;
		int counter = 0;
		
		double[][] distance = getEuclideanDistance(data, data, data.length);
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
			
			if (n > 2 && n < 18) {
				/*
				 * Cluster = n - 1
				 */
				int kk = n;
				kk--;
				int[] classDataN = getDataClass(classData, kk);
				
				double[][] centroid = getCentroid(data, classDataN, kk);
				double[] clusterVariance = getClusterVariance(data, centroid, classDataN, kk);
				double varianceWithinCluster = getVarianceWithinCluster(data, clusterVariance, classDataN, kk);
				double varianceBetweenCluster = getVarianceBetweenCluster(data, clusterVariance, centroid, classDataN, kk);
				variance[kk] = varianceWithinCluster / varianceBetweenCluster;
			}
		
			n--;
		}
		
		for (int a = 2; a < 16; a++) {
			globalOptimum[a] = (variance[a + 1] + variance[a - 1]) - (2.0 * variance[a]);
		}
			
		double maxGO = globalOptimum[2];
		int maxGlobalOptimum = 0;
		
		for (int a = 2; a < 16; a++) {
			if (globalOptimum[a] > maxGO) {
				maxGO = globalOptimum[a];
				maxGlobalOptimum = a;
			}
		}
		
		return maxGlobalOptimum;
	}
	
	public static int[] getDataClass(int[] classData, int k) {
		int[] classData2 = Arrays.copyOf(classData, classData.length);
		
		int counterClass = 0;
		boolean found = false;
		for (int i = 0; i < classData2.length; i++) {
			for (int j = 0; j < classData2.length; j++) {
				if (classData2[j] == i) {
					found = true;
					classData2[j] = counterClass;
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
			for (int a = 0; a < classData2.length; a++) {
				if (classData2[a] == 0) {
					classData2[a] = counterClass;
					counterClass++;
				}
				
				if (counterClass == k) {
					break;
				}
			}
		}
		
		return classData2;
	}
	
	public static double[][] getEuclideanDistance(double[][] data, double[][] centroid, int k) {
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
	
	public static double[][] getCentroid(double[][] data, int[] cluster, int k) {
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
	
	public static double[] getClusterVariance(double[][] data, double[][] centroid, int[] cluster, int k) {
		double[] clusterVariance = new double[k];
		int count = 0;
		int a, b;
		
		for (int i = 0; i < k; i++) {
			for (a = 0; a < data.length; a++) {
				if (cluster[a] == i) {
					count++;
					for (b = 0; b < data[0].length; b++) {
						clusterVariance[i] += Math.pow((data[a][b] - centroid[i][b]), 2); 
					}
				}
			}
			
			if (count == 1) {
				clusterVariance[i] = 0;
			} else {
				clusterVariance[i] = 1.0 / (count - 1) * clusterVariance[i];
			}

			count = 0;
		}
		return clusterVariance;
	}
	
	public static double getVarianceWithinCluster(double[][] data, double[] clusterVariance, int[] cluster, int k) {
		double varianceWithinCluster = 0;
		int count = 0;
		
		for (int i = 0; i < k; i++) {
			for (int a = 0; a < data.length; a++) {
				if (cluster[a] == i) {
					count++;
				}
			}
			varianceWithinCluster += (count - 1) * clusterVariance[i];
			count = 0;
		}
		varianceWithinCluster = 1.0 / (data.length - k) * varianceWithinCluster;
		
		return varianceWithinCluster;
	}
	
	public static double getVarianceBetweenCluster(double[][] data, double[] clusterVariance, double[][] centroid, int[] cluster, int k) {
		double varianceBetweenCluster = 0;
		int count = 0;
		
		double[] meanCentroid = new double[data[0].length];
		for (int x = 0; x < centroid.length; x++) {
			for (int z = 0; z < centroid[0].length; z++) {
				meanCentroid[z] += centroid[x][z];
			}
		}
		
		for (int i = 0; i < k; i++) {
			for (int a = 0; a < data.length; a++) {
				if (cluster[a] == i) {
					count++;
				}
			}
			
			for (int n = 0; n < data[0].length; n++) {
					varianceBetweenCluster += count * Math.pow(centroid[i][n] - (meanCentroid[n] / centroid.length), 2);
			}
			count = 0;
		}
		
		if (k == 1) {
			varianceBetweenCluster = 0;
		} else {
			varianceBetweenCluster = 1.0 / (k - 1) * varianceBetweenCluster;
		}
		
		return varianceBetweenCluster;
	}
}