package hivemall.fm;

import java.util.List;
import hivemall.fm.eta.Eta;
import hivemall.io.FMFeature;
import hivemall.io.FactorizationMachineModel;
import hivemall.utils.collections.IntOpenHashMap;

public abstract class OnlineFactorizationMachine/* extends UDFWithOptions*/{
	/** The number of latent factors */
	protected int factor;
	/** The regularization factor */ 		// そとのPで内部の実装を変える
	protected IntOpenHashMap<float[]> lambdaV; // getV(i, f)
	
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
	/** Eta Update Method*/
	protected String etaUpdateMethod;
	/** Eth0 */
	protected float eth0;
	/** Eta */
	protected Eta eta = new Eta(this.factor,  etaUpdateMethod, eth0);
	/** Time */
	protected int time; 
	/** Possible Task*/
	protected enum possibleTask {regression, classification};
	/** The number of group*/
	protected int group;
	/** The number to initialize Lambdas */
	protected float initLambdas;

	
	public OnlineFactorizationMachine(String task, String etaUpdateMethod, int factor, float eth0, float initLambdas){
		this.task = task;
		this.factor = factor;
		this.etaUpdateMethod = etaUpdateMethod;
		this.eth0 = eth0;
		this.initLambdas = initLambdas;
		
		this.time = 0;
		initParamsForPi();
	}
	
	protected void train(List<FMFeature> x, final float y, List<Integer> x_group){
		// Increment time
		this.time++;
		
		//
		this.group = x_group.size();
		
		if(task.equals(possibleTask.regression)){
			//
			float predict0 = predictForTraining(x, y);
			float grad0 = gLossW0_regression(predict0, y);
			eta.updateW0(grad0);
			float w0 = fmm.getW0();
			float nextW0 = w0 - eta.getW0(time) * (grad0 + 2 * fmm.getLambdaW(0) * w0);
			fmm.updateW0(nextW0);
			// 
			for(FMFeature fmf:x){
				int i = fmf.getKey();
				float value =fmf.getValue();
				
				int pi = -1;
				try {
					pi = fmm.getPi(i);
				} catch (Exception e) {
					e.printStackTrace();
				}
				//
				if(!check(i)){
					createNewColumnParams(i);
				}
				//
				float wi = fmm.getWi(i);
				float predictWi = predictForTraining(x, y);
				float xi = value;
				float gradWi = dLossWi(predictWi, y, xi);
				eta.updateWi(i, gradWi);
				float nextWi = wi - eta.getWi(i, time) * (gradWi + 2 * fmm.getLambdaW(pi) * wi);
				fmm.updateWi(pi, nextWi);
				//
				for(int f=0, k=factor; f<k; f++){
					float vif = fmm.getV(i, f);
					float predictVif = predictForTraining(x, y);
					float gradVif = dLossVif(predictVif, y, i, f, x);
					eta.updateVif(i, f, gradVif);
					float nextVif = vif - eta.getVif(i, f, time) * (gradVif + 2 * lambdaV.get(pi)[f] * vif);
					fmm.updateVif(pi, f, nextVif);
				}
			}
		}else if(task.equals(possibleTask.classification)){
			float predict0 = predictForTraining(x, y);
			float grad0 = gLossW0_regression(predict0, y);
			eta.updateW0(grad0);
			float w0 = fmm.getW0();
			float nextW0 = w0 - eta.getW0(time) * (grad0 + 2 * fmm.getLambdaW(0) * w0);
			fmm.updateW0(nextW0);
			// 
			for(FMFeature fmf:x){
				int i = fmf.getKey();
				float value =fmf.getValue();
				int pi = -1;
				try {
					pi = fmm.getPi(i);
				} catch (Exception e) {
					e.printStackTrace();
				}
				//
				if(!check(i)){
					createNewColumnParams(i);
				}
				//
				float wi = fmm.getWi(i);
				float predictWi = predictForTraining(x, y);
				float xi = value;
				float gradWi = dLossWi(predictWi, y, xi);
				eta.updateWi(i, gradWi);
				float nextWi = wi - eta.getWi(i, time) * (gradWi + 2 * fmm.getWi(pi) * wi);
				fmm.updateWi(pi, nextWi);
				//
				for(int f=0, k=factor; f<k; f++){
					float vif = fmm.getV(i, f);
					float predictVif = predictForTraining(x, y);
					float gradVif = dLossVif(predictVif, y, i, f, x);
					eta.updateVif(i, f, gradVif);
					float nextVif = vif - eta.getVif(i, f, time) * (gradVif + 2 * lambdaV.get(pi)[f] * vif);
					fmm.updateVif(pi, f, nextVif);
				}
			}
		}else{
			// S
		}
	}
	
	private void createNewColumnParams(int i) {
		fmm.insertW(i);
		fmm.insertV(i);
		eta.insertW(i);
		eta.insertV(i);
	}

	private void initParamsForPi() {
		fmm.initLambdas(group, factor, initLambdas);
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
		if(task.equals(possibleTask.regression)){
			float dyVif = getDyVif(i, f, x);
			ret += 2 * (predictVif - y) * dyVif;
		}else if(task.equals(possibleTask.classification)){
			
		}
		return ret;
	}
	protected float dLossWi(float predictWi, float y, float xl){
		float ret = 0;
		if(task.equals(possibleTask.regression)){
			float lossWi = predictWi - y;
			ret = 2 * lossWi *xl;
		}else if(task.equals(possibleTask.classification)){
			
		}
		return ret;
	}
	protected float gLossW0_regression(float predictW0, float y){
		float ret = 0;
		if(task.equals(possibleTask.regression)){
			float lossW0 = predictW0 - y;
			ret = 2 * lossW0 * 1;
		}else if(task.equals(possibleTask.classification)){
			
		}
		return ret;
	}
	protected float predictForTraining(List<FMFeature> x, float y){
		float ret = 0;		
		if(task.equals(possibleTask.regression)){
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
		}else if(task.equals(possibleTask.classification)){
			
		}
		return ret;
	}

}
