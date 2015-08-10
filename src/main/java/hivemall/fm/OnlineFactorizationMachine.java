package hivemall.fm;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import hivemall.fm.eta.Eta;
import hivemall.io.FMFeature;
import hivemall.io.FactorizationMachineModel;

public abstract class OnlineFactorizationMachine extends UDFWithOptions{
	/** The number of latent factors */
	protected int factor;
	/** The regularization factor */
	protected Map<Integer, Float> lambdaW;
	protected Map<Integer, float[]> lambdaV;
	
	/** The initial standard deviation of value of V */
	protected float sigma;
	/** The number of iterations */
	protected int iterations;
	/** Whether to check conversion*/
	protected boolean conversionCheck;
	/** Threshold to determine convergence */
	protected float convergenceRate;
	/** Regression or Classification */
	protected String task;
	/** Model */
	protected FactorizationMachineModel fmm;
	
	/** Eta */
	Eta eta = new Eta(this.factor);

	
	public OnlineFactorizationMachine(String task, int factor){
		this.task = task;
		this.factor = factor;
	}
	
	
	protected void train(List<FMFeature> x, final float y){
		if(task.equals("regression")){
			//
			float diff0 = predictForTraining(x, y) - y;
			float grad0 = gLossW0(diff0, y);
			eta.updateW0(grad0);
			float w0 = fmm.getW0();
			float nextW0 = w0 - eta.getW0() * (grad0 + 2 * lambdaW.get(0) * w0);
			fmm.updateW0(nextW0);
			// 
			for(FMFeature fmf:x){
				int i = fmf.getKey();
				float value =fmf.getValue();
				int pi = fmm.getPi(i);
				//
				if(!check(i)){
					createNewColumnParams(i);
				}
				//
				float wi = fmm.getWi(i);
				float diffWi = predictForTraining(x, y) - y;
				float xi = value;
				float gradWi = dLossWi(diffWi, y, xi);
				eta.updateWi(i, gradWi);
				float nextWi = wi - eta.getWi(i) * (gradWi + 2 * lambdaW.get(pi) * wi);
				fmm.updateWi(i, nextWi);
				//
				for(int f=0, k=factor; f<k; f++){
					float vif = fmm.getV(i, f);
					float predictVif = predictForTraining(x, y);
					float gradVif = dLossVif(predictVif, y, i, f, x);
					eta.updateVif(i, f, gradVif);
					float nextVif = vif - eta.getVif(i, f) * (gradVif + 2 * lambdaV.get(pi)[f] * vif);
				}
			}
		}else if(task.equals("classification")){
			
		}else{
			
		}
	}
	
	protected void createNewColumnParams(int i){
		// OUTPUT PARAMS
		fmm.addW(i);
		fmm.addV(i);
		
		// LAMBDA
		lambdaW.put(i, (float)0);
		float[] tmp = new float[factor+1];
		Arrays.fill(tmp, (float)0);
		lambdaV.put(i,tmp);
		
	}
	
	protected boolean check(int i){
		if(fmm.exist(i)){
			return true;
		}else{
			return false;
		}
	}
	
	protected float getDyVif(int i, int f, List<FMFeature> x){
		float ret = 0;
		for(FMFeature fmf:x){
			int j = fmf.getKey();
			float value = fmf.getValue();
			if(i == j) continue;
			ret += fmm.getV(j, f) * value;
		}
		return ret;
	}
	
	protected float dLossVif(float predictVif , float y, int i, int f, List<FMFeature> x){
		float ret = 0;
		if(task.equals("regression")){
			float dyVif = getDyVif(i, f, x);
			ret += 2 * (predictVif - y) * dyVif;
		}else if(task.equals("classification")){
			
		}
		return ret;
	}
	protected float dLossWi(float lossWi, float y, float xl){
		float ret = 0;
		if(task.equals("regression")){
			ret = 2 * lossWi *xl;
		}else if(task.equals("classification")){
			
		}
		return ret;
	}
	protected float gLossW0(float lossW0, float y){
		if(task.equals("regression")){
			float ret = 0;
			ret = 2 * lossW0 * 1;
			return ret;
		}else if(task.equals("classification")){
			
		}
	}
	protected float predictForTraining(List<FMFeature> x, float y){
		float ret = 0;		
		if(task.equals("regression")){
			// w0
			ret += fmm.getW0();
			for(FMFeature dataf:x){
				int idx = dataf.getKey();
				ret += fmm.getWi(idx) * dataf.getValue();
			}
			
			float sumV = 0;
			float sumV2= 0;
			for(int f=0, k=factor; f<k; f++){
				for(FMFeature dataf:x){
					int idx = dataf.getKey();
					float value = dataf.getValue();
					float vif = fmm.getV(idx, f);
					sumV += vif * value;
					sumV2+= (vif * vif) * (value * value);
				}
				sumV *= sumV;
			}
			ret += 0.5 * (sumV - sumV2);	
		}else if(task.equals("classification")){
			
		}
		return ret;
	}

}
