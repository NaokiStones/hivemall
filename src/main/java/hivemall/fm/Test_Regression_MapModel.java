package hivemall.fm;

import java.util.ArrayList;
import java.util.Random;

import hivemall.io.FMMapModel;
import hivemall.mf.FactorizationMachineUDTF;
import hivemall.mf.FactorizationMachineUDTF.Feature;

public class Test_Regression_MapModel {

	static int factor = 20;
	static boolean classification = false;
	static float lambda0 = 0.1f;
	static float eta0 = 0.001f;
	static int[] x_group = {30, 40, 100};
	static float sigma = 0.1f;
	static String etaUpdateMethod = "fix";

	public static void main(String[] args) {
		FactorizationMachineUDTF fmUDTF2 = new FactorizationMachineUDTF();
		fmUDTF2.model = new FMMapModel(classification, factor, lambda0, eta0, x_group, sigma, etaUpdateMethod);
		
		int ROW = 10;
		int COL = 40;
		Random rnd = new Random();
		rnd.setSeed(201);
		
		for(int numberOfIteration=0; numberOfIteration<10000; numberOfIteration++){
			// System.out.println(numberOfIteration);
			ArrayList<Feature[]> fArrayList = new ArrayList<Feature[]>();
			ArrayList<Double> ans = new ArrayList<Double>();
			for(int i=0; i<ROW; i++){
				ArrayList<Feature> feature = new ArrayList<Feature>();
				for(int j=0; j<COL; j++){
					if(i < (0.5 * ROW)){
						if(j == 0){
							feature.add(new Feature(j, 1));
						}else if(j == 1){
							
						}else if(j < 0.5 * COL){
							if(rnd.nextFloat() < 0.2){
								feature.add(new Feature(j, rnd.nextFloat()));
							}
						}
					}else{
						if(j > 0.5 * COL){
							if(rnd.nextFloat() < 0.2){
								feature.add(new Feature(j, rnd.nextFloat()));
							}
						}
					}
				}
				int featureSize = feature.size();
				Feature[] x = new Feature[featureSize];
				for(int k=0; k<featureSize; k++){
					x[k] = feature.get(k);
				}
				
				fArrayList.add(x);

				double y =0;

				if(i < ROW*0.5){
					y = 0.1;
				}else{
					y = 0.4;
				}
				ans.add(y);

				fmUDTF2.train(x, y, x_group);
			}
			float diff = 0f;
			for(int ii=0; ii < fArrayList.size(); ii++){
				double tmpDiff = (fmUDTF2.model.predict(fArrayList.get(ii)) - ans.get(ii));
				diff += tmpDiff * tmpDiff;
			}
			System.out.print(diff + ",");
		}
	}
}
