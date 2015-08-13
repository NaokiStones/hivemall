package hivemall.io;

import java.util.Arrays;
import java.util.Random;
import hivemall.mf.FactorizationMachineUDTF.Feature;
import hivemall.utils.collections.IntOpenHashMap;

public class FMMapModel implements FactorizationMachineModel {
	
	// tmp Params
	protected int tmpHashMapSize = 1000;
	
	String task;
	protected int factor;
	protected float lambdaInit;
	
	// Random
	Random random;
	protected float sigma;
	
	// Learning Parameters
	IntOpenHashMap<Float> w;
	IntOpenHashMap<float[]> V;
//	Map<Integer, Float> w;
//	Map<Integer, float[]> V;
	
	
	float[] lambdaW;
	float[][] lambdaV;
	int[] x_group;
	
	// Eta
	protected Eta eta;
	
	boolean classification;
	
	
	public FMMapModel(boolean classification, int factor, float lambdaInit, float etaInit, final int[] x_group, float sigma, String etaUpdateMethod) {
		this.factor = factor;
		this.lambdaInit = lambdaInit;
		
		this.x_group = x_group;
		this.sigma = sigma;
		this.classification = classification;

		this.w = new IntOpenHashMap<Float>(100);
		this.V = new IntOpenHashMap<float[]>(100);
		//this.w = new HashMap<Integer, Float>(); //new IntOpenHashMap<Float>(tmpHashMapSize);
		//this.V = new HashMap<Integer, float[]>();
		
		random = new Random();
		random.setSeed(117);
		
		initParamsForPi(x_group.length, factor);
		
		initWforW0();
		eta = new Eta(factor, etaInit, sigma, etaUpdateMethod);
		
	}

	
	public void initWforW0() {
		this.w.put(0, 0f);
		this.lambdaW[0] = lambdaInit;		
	}


	@Override
	public float getV(int i, int f) {
		return V.get(i)[f];
	}

	@Override
	public void initParamsForPi(int groupSize, int factor) {
		lambdaW = new float[groupSize+1];
		Arrays.fill(lambdaW, lambdaInit);
		lambdaV = new float[groupSize][factor];
		for(int i=0; i<groupSize; i++){
			Arrays.fill(lambdaV[i], lambdaInit);
		}
	}



	@Override
	public boolean check(int index) {
		// Don't check w0
		// using w
		int indexW = index + 1;
		if(w.containsKey(indexW)){
			return true;
		}else{
			return false;
		}
		
	}

	@Override
	public void addElement(int index) {
		addElementW(index);
		addElementV(index);
		eta.addElementW(index);	// eta , accW
		eta.addElementV(index);	// eta , accV
	}

	private void addElementW(int index) {
		int idx = index + 1;
		w.put(idx, 0f);
	}

	private void addElementV(int index) {
		float[] tmp = getRandomFloatArray();
		V.put(index, tmp);
	}



	private float[] getRandomFloatArray() {
		float[] ret = new float[factor];
		for(int tmpI=0,IMAX=factor; tmpI < IMAX; tmpI++){
			ret[tmpI] = getRandom();
		}
		return ret;
	}



	private float getRandom() {
		//float ret = (float)random.nextGaussian() * this.sigma;
		float ret = (float)random.nextGaussian() *0.001f;
		return ret;
	}



	@Override
	public void updateW0_regression(Feature[] x, double y, int time) {
		float predict0 = predictForTraining(x, y, false);
		float grad0 = gLossW0_regression(predict0, (float)y);
		eta.addAccWi(0, grad0, true);
		float w0 = getW0();
		float nextW0 = w0 - eta.getEtaW0(time) * (grad0 + 2 * getLambdaW(0, true) * w0); //tmp
		updateW0(nextW0);
//		System.out.println("nextW0:" + nextW0);//****
//		System.out.println("grad0:" + grad0);//****
	}

	private void updateW0(float nextW0) {
		this.w.put(0, nextW0);
	}

	private float getLambdaW(int pi, boolean w0) {
		if(w0){
			return lambdaW[0];
		}else{
			pi += 1;
			return lambdaW[pi];
		}
	}
	
	private float getW0() {
		return this.w.get(0);
	}

	private float gLossW0_regression(float predict0, float y) {
		float ret = 0;
		float lossW0 = predict0 - y;
		ret = 2 * lossW0 * 1;
		
		return ret;
	}

	int dummy=0;
	private float predictForTraining(Feature[] x, double y, boolean classification) {
		float ret = 0f;
		
		// w0
		ret += getW0();
		float tmpRetW0 = ret;	// tmp
		// wi
		for(Feature dataf:x){
			int idx = dataf.index;
			float val = (float)dataf.value;
			ret += getWi(idx) * val;
		}
		
		float tmpRetW = ret;	// tmp
		
		// V
		for(int f=0, k=factor; f<k; f++){
			double sumV = 0f;
			double sumV2= 0f;
			for(Feature dataf:x){
				int idx = dataf.index;
				double value = dataf.value;
				double vif = getV(idx, f);
//				System.out.println("value:" + value);	//****
//				System.out.println("vif:" + vif);		//****
				double tmpRet = ret;
				sumV += vif * value;
				sumV2+= (vif * vif) * (value * value);
//				System.out.println("sumV:" + sumV); 	//****
//				System.out.println("sumV2:" + sumV2); 	//****
				if(Double.isNaN(sumV) || Double.isNaN(sumV2) || Double.isInfinite(sumV) || Double.isInfinite(sumV2)){
					System.out.println("sumV:" + sumV); 	//****
					System.out.println("sumV2:" + sumV2); 	//****
					System.out.println("NaN or Inifinite");
					System.exit(1);
				}
				dummy=0;
			}
			sumV *= sumV;
			ret += 0.5 * (float)(sumV - sumV2);
		}

		
		if(classification){
			ret = (float)sigmoid(ret);
			if(ret > 0.5){
				ret = 1f;
			}else{
				ret = 0f;
			}
		}
		dummy=0;
		return ret;
	}

	private float getWi(int idx) {
		idx = idx +1;
		return this.w.get(idx);
	}



	private float sigmoid(float x) {
		float ret = (float) 1 / ( 1f + (float)Math.exp(x));
		return ret;
	}



	@Override
	public void updateWi_regression(Feature[] x, double y, int idxForBigFeature, int idxForX, int time) {
		final int pi = pi(idxForBigFeature);
		
		float wi = getWi(idxForBigFeature);
		float predictWi = predictForTraining(x, y, false);
		float xi = (float)x[idxForX].value;
		float gradWi = dLossWi(predictWi, (float)y, xi, false);
		eta.addAccWi(idxForBigFeature, gradWi, false);
		float nextWi = wi - eta.getWi(idxForBigFeature, time) * (gradWi + 2 * getLambdaW(pi, false) * wi);
		updateWi(idxForBigFeature, nextWi);
	}

	private float dLossWi(float predictWi, float y, float xi, boolean classification) {

		float ret = 0;
		if(!classification){
			float lossWi = predictWi - y;
			ret = 2 * lossWi *xi;
		}else{
			ret = (sigmoid(predictWi*y) - 1) * y * xi;
		}
		return ret;
	}



	private void updateWi(int i, float nextWi) {
		int idx = i + 1;
		this.w.put(idx, nextWi);
	}



	private int pi(int i) {
		int sum = 0;
		int ret = -1;
		for(int j=0, j_max=x_group.length; j< j_max; j++){
			if(i < sum + this.x_group[j]){
				ret = j;
				break;
			}else{
				sum += this.x_group[j];
			}
		}
		return ret;
	}



	@Override
	public void updateV_regression(Feature[] x, double y, int i, int f, int time) {
		final int pi = pi(i);
		
		float vif = getV(i, f);
		float predictVif = predictForTraining(x, y, false);
		float gradVif = dLossVif(predictVif, y, i, f, x, false);
		eta.addAccVif(i, f, gradVif);

		float nextVif = vif - eta.getVif(i, f, time) * (gradVif + 2 * getLambdaV(pi, f) * vif);
		updateVif(i, f, nextVif);
	}



	private float dLossVif(float predictVif, double y, int i, int f, Feature[] x, boolean classification) {
		float ret = 0;
		if(!classification){
			float dyVif = getDyVif(i, f, x);
			ret += 2 * (predictVif - y) * dyVif;
		}else{
			float dyVif = getDyVif(i, f, x);
			ret += (sigmoid(predictVif * (float)y) - 1) * y * dyVif;
		}
		return ret;
	}



	private float getDyVif(int i, int f, Feature[] x) {
		float ret = 0;
		for(Feature fmf:x){
			int j = fmf.index;
			float value = (float)fmf.value;
			if(i == j) continue;
			ret += getV(j, f) * value;
		}
		return ret;
	}



	private float getLambdaV(int pi, int f) {
		return lambdaV[pi][f];
	}



	private void updateVif(int i, int f, float nextVif) {
		float[] tmp = this.V.get(i);
		tmp[f] = nextVif;
		this.V.put(i, tmp);
	}



	@Override
	public float getW(int i) {
		return getW(i);
	}



	@Override
	public void updateW0_classification(Feature[] x, double y, int time) {
		float predict0 = predictForTraining(x, y, true);
		float grad0 = gLossW0_classification(predict0, (float)y);
		eta.addAccWi(0, grad0, true);
		float w0 = getW0();
		float nextW0 = w0 - eta.getEtaW0(time) * (grad0 + 2 * getLambdaW(0, true) * w0); //tmp
		updateW0(nextW0);
	}

	private float gLossW0_classification(float predict0, float y) {
		float ret = (sigmoid(predict0 * y) -1) * y * 1.0f;
		return ret;
	}



	@Override
	public void updateWi_classification(Feature[] x, double y, int i, int idxForReducedFeature, int time) {
		//ret = 2 * (predict(y, record) - y) * xl;
		final int pi = pi(i);
		
		float wi = getWi(i);
		float predictWi = predictForTraining(x, y, true);
		float xi = (float)x[i].value;
		float gradWi = dLossWi(predictWi, (float)y, xi, true);
		eta.addAccWi(i, gradWi, true);
		float nextWi = wi - eta.getWi(i, time) * (gradWi + 2 * getLambdaW(pi, false) * wi);
		updateWi(i, nextWi);
	}



	@Override
	public void updateV_classification(Feature[] x, double y, int f, int i, int time) {
		final int pi = pi(i);
		
		float vif = getV(i, f);
		float predictVif = predictForTraining(x, y, true);
		float gradVif = dLossVif(predictVif, y, i, f, x, true);
		eta.addAccVif(i, f, gradVif);

		float nextVif = vif - eta.getVif(i, f, time) * (gradVif + 2 * getLambdaV(pi, f) * vif);
		updateVif(i, f, nextVif);
	}


	@Override
	public Float predict(Feature[] x) {
		float ret = 0f;
		
//		// SHOW Input
//		for(Feature tx:x){
//			System.out.print(tx.index + ":" + tx.value + ", ");
//		}
//		System.out.println("");
//		// SHOW weight W
//		for(Feature tx:x){
//			System.out.print(tx.index + ":" + w.get(tx.index) + ", ");
//		}
//		System.out.println("");
		
		// W0
		ret += getW0();
		
//		System.out.println("predict ret[w0]:" + ret); 	//****
		int idxForReducedFeatureVector = 0;
		// W
		for(Feature dataf:x){
			int idx = dataf.index;
			float val = (float)dataf.value;
			ret += getWi(idxForReducedFeatureVector) * val;
			idxForReducedFeatureVector++;
		}

//		System.out.println("predict ret[W]:" + ret);	//****
		// V

		for(int f=0, k=factor; f<k; f++){
			float sumV = 0;
			float sumV2= 0;
			for(Feature dataf:x){
				int j = dataf.index;
				float value = (float)dataf.value;
				float vif = getV(j, f);
				sumV += vif * value;
				sumV2+= (vif * vif * value * value);
			}
			sumV *= sumV;
			ret += 0.5 * (sumV - sumV2);
		}


//		System.out.println("predict ret[V]:" + ret);	//****
		if(classification){
			ret = (float)sigmoid(ret);
			if(ret > 0.5){
				ret = 1f;
			}else{
				ret = 0f;
			}
		}
		return ret;
	}

}
