package hivemall.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import hivemall.mf.FactorizationMachineUDTF.Feature;

public final class FMArrayModel implements FactorizationMachineModel {
	private float w0;
	private float[] w;
	private float[][] V;

	private float eta = (float) 0.1; // reg0, number:by intuition
	private float lambda0;
	private float[] lambdaW;
	private float[][] lambdaV;

	private String task;

	private int[] groupRangeUpperLimit;

	private int k;
	private int factor;
	private Random random = new Random();
	private int group;
	private ArrayList<Float> results = new ArrayList<Float>();
	private float alpha = (float) 0.5;
	private float t0 = 2;
	private float power_t = (float) 0.1;
	private boolean classification;
	private float lambdaInit;
	private float etaInit;
	private int[] x_group;
	private float sigma;
	private String etaUpdateMethod;


	public FMArrayModel(int p, boolean classification, int factor, float lambdaInit, float etaInit, final int[] x_group, float sigma, String etaUpdateMethod) {
		// TODO Auto-generated constructor stub
		this.classification = classification;
		this.factor = factor;
		this.lambdaInit = lambdaInit;
		this.etaInit = etaInit;
		this.x_group = x_group;
		this.sigma = sigma;
		this.etaUpdateMethod = etaUpdateMethod;

		int groupSize = x_group.length;

		initParamsForPi(groupSize, factor);
	}



	@Override
	public float getW(int i) {
		if(i==0){
			return w0;
		}else{
			return w[i-1];
		}
	}

	@Override
	public float getV(int i, int f) {
		return V[i][f];
	}

	@Override
	public void initParamsForPi(int groupSize, int factor, int col) {
		// init w0, W, V, lambdaW0, lambdaW, lambdaV
		w0 = 0f;
		
		w = getUniformalVector(0f);
		
		V = new float[col][factor];
		for(int i=0; i<col; i++){
			Arrays.fill(V[i], getRandom());
		}
		
		this.lambda0 = lambdaInit;
		this.lambdaW  = getUniformalVector(0f);
		this.lambdaV  = new float[groupSize][factor];
	}

	private float[] getUniformalVector(float f) {
		float[] ret = new float[factor];
		Arrays.fill(ret, f);
		return ret;
	}
	
	private float getRandom() {
		float ret = (float) random.nextGaussian() * sigma;
		return ret;
	}
	




























	@Override
	public void initWforW0() {
		w0 = 0;
	}

	@Override
	public boolean check(int index) {
		// DO Nothing
		return false;
	}

	@Override
	public void addElement(int index) {
		// DO Nothing

	}

	@Override
	public void updateW0_regression(Feature[] x, double y, int time) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateV_regression(Feature[] x, double y, int f, int i, int time) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateW0_classification(Feature[] x, double y, int time) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateWi_classification(Feature[] x, double y, int i, int idxForX, int time) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateV_classification(Feature[] x, double y, int f, int i, int time) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateWi_regression(Feature[] x, double y, int i, int idxForX, int time) {
		// TODO Auto-generated method stub

	}

	@Override
	public Float predict(Feature[] features) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public void initParamsForPi(int groupSize, int factor) {
		// TODO Auto-generated method stub
		
	}


}
