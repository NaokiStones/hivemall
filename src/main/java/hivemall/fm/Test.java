package hivemall.fm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import hivemall.io.FMMapModel;
import hivemall.io.FactorizationMachineModel;
import hivemall.mf.FactorizationMachineUDTF.Feature;

public class Test {
	static FactorizationMachineModel model;
	static int factor = 10;
	static int t = 0;
	static boolean classification = false;

	public static void main(String args[]) {
		//
		double[][] testMatrix = new double[100][300];
		Random random = new Random();
		random.setSeed(111);

		ArrayList<Float> y = new ArrayList<Float>();
		// make testMatrix
		for (int i = 0; i < 100; i++) {
			for (int j = 0; j < 300; j++) {
				if (j < 3) {
					if (i < 30) {
						testMatrix[i][0] = 1;
					} else if (i < 40) {
						testMatrix[i][1] = 1;
					} else {
						testMatrix[i][2] = 1;
					}

				} else if (i < 30) {
					y.add(700.0f);
					if (j < 100) {
						if (random.nextDouble() < 0.2) {
							testMatrix[i][j] = 0.007 * random.nextFloat(); // tmp
						} else {
							testMatrix[i][j] = 0;
						}
					} else {
						testMatrix[i][j] = 0;
					}
				} else if (i < 40) {
					y.add(100.0f);
					if (j <= 100 && j < 200) {
						if (random.nextDouble() < 0.3) {
							testMatrix[i][j] = 0.003 * random.nextFloat(); // tmp
						} else {
							testMatrix[i][j] = 0;
						}
					} else {
						testMatrix[i][j] = 0;
					}
				} else {
					y.add(300.0f);
					if (j <= 200) {
						if (random.nextDouble() < 0.2) {
							testMatrix[i][j] = 0.0002 * random.nextFloat(); // tmp
						} else {
							testMatrix[i][j] = 0;
						}
					} else {
						testMatrix[i][j] = 0;
					}
				}
			}
		}

		boolean classification = false;
		String etaUpdateMethod = "fix";
		int factor = 20;
		// factor = factor;
		float sigma = 0.02f;
		float lambdaInit = 0.01f;
		float etaInit = 0.005f;

		int[] x_group = new int[4];
		x_group[0] = 3;
		x_group[1] = 97;
		x_group[2] = 100;
		x_group[3] = 100;

		model = new FMMapModel(classification, factor, lambdaInit, etaInit, x_group, sigma, etaUpdateMethod);
		ArrayList<Feature[]> xxContainer = new ArrayList<Feature[]>();

		// create Feature Vector
		for (int i = 0; i < 100; i++) {
			List<Feature> x = new ArrayList<Feature>();
			for (int j = 0; j < 300; j++) {
				if (testMatrix[i][j] != 0) {
					Feature f = new Feature(j, testMatrix[i][j]);
					x.add(f);
				}
			}
			int xSize = x.size();
			Feature[] xx = new Feature[xSize];
			for (int j = 0; j < xSize; j++) {
				xx[j] = x.get(j);
			}
			xxContainer.add(xx); // tmp
		}

		// before training test [regression]
		float beforeDiff = 0f;
		if (!classification) {
			for (int i = 0; i < 100; i++) {
				 //beforeDiff += (model.predict(xxContainer.get(i)) - y.get(i));
				// * (model.predict(xxContainer.get(i)) - y.get(i));
			}
		} else {
			// TODO classification
		}

		// TRAIN!!
		t = 0;
		for(int kk=0; kk<1000; kk++){
			//System.out.println(kk);
			for (int i = 0; i < 100; i++) {
				train(xxContainer.get(i), y.get(i), x_group);
				t++;
			}

			// after training test [regression]
			float afterDiff = 0f;
			float zeroDiff = 0f;
			if (!classification) {
				for (int i = 0; i < 100; i++) {
					// System.out.println("AfterDiff:" + afterDiff); //****
					afterDiff += (model.predict(xxContainer.get(i)) - y.get(i)) 
							* (model.predict(xxContainer.get(i)) - y.get(i));
					zeroDiff += y.get(i) * y.get(i);
				}
			} else {
				// TODO classification
			}
			// Result
			//		System.out.println("Before:" + beforeDiff);
			System.out.print(/*"After:" + */afterDiff + ",");
			//System.out.println("Zero:"  + zeroDiff);
		}
	}

	public static void train(Feature[] x, double y, int[] x_group) {
		// params
		final int featureSize = x.length;
		final int groupSize = x_group.length;

		if (t == 0) {
			model.initParamsForPi(groupSize, factor);
			model.initWforW0();
		}

		// Check Lacking Columns before Training
		for (int i = 0; i < featureSize; i++) {
			if (!model.check(x[i].index)) {
				model.addElement(x[i].index);
			}
		}

		// training
		int indexForReducedFeatureVector = 0;
		if (!classification) {
			model.updateW0_regression(x, y, t);
			indexForReducedFeatureVector = 0;
			for (Feature e : x) {
				int i = e.index;
				model.updateWi_regression(x, y, i, indexForReducedFeatureVector, t);
				indexForReducedFeatureVector++;
				for (int f = 0, k = factor; f < k; f++) {
					model.updateV_regression(x, y, i, f, t);
				}
			}
		} else {
			model.updateW0_classification(x, y, t);
			indexForReducedFeatureVector = 0;
			for (Feature e : x) {
				int i = e.index;
				model.updateWi_classification(x, y, i, indexForReducedFeatureVector, t);
				indexForReducedFeatureVector++;
				for (int f = 0, k = factor; f < k; f++) {
					model.updateV_classification(x, y, f, i, t);
				}
			}
		}
	}
}
