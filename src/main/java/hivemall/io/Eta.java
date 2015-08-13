package hivemall.io;

import java.util.Arrays;
import java.util.Random;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import hivemall.utils.collections.IntOpenHashMap;
import sun.reflect.generics.tree.VoidDescriptor;

public class Eta {
	protected int factor;
	protected float eta0;
	protected IntOpenHashMap<Float> etaW;
	protected IntOpenHashMap<float[]> etaV;
	
	protected IntOpenHashMap<Float> AccW;
	protected IntOpenHashMap<float[]> AccV;
	
	//
	protected Random random;
	protected float sigma;
	
	// 
	protected float t0;	// default : 0.1
	protected float power_t;	// default : 0.1
	
	protected String etaUpdateMethod;
	
	public Eta(int factor, float eta0, float sigma, String etaUpdateMethod){
		this.factor = factor;
		this.eta0= eta0;
		this.sigma = sigma;
		this.etaUpdateMethod = etaUpdateMethod;
		
		etaW = new IntOpenHashMap<Float>(1000);
		etaV = new IntOpenHashMap<float[]>(1000);
		initEtaWforW0();
		
		this.AccW = new IntOpenHashMap<Float>(1000);
		this.AccV = new IntOpenHashMap<float[]>(1000);
		initAccWforW0();

		
		random = new Random();
		random.setSeed(201);
		t0 = 0.1f;
		power_t = 0.1f;
	}
	
	protected void initEtaWforW0() {
		etaW.put(0, eta0);
	}
	protected void initAccWforW0(){
		AccW.put(0, 0f);
	}
	
	public float getEtaW(int i){
		return etaW.get(i);
	}
	public float getEtaV(int i, int f){
		return etaV.get(i)[f];
	}
	
	public float[] getRandomFloatArray(){
		float[] ret = new float[factor];
		for(int tmpI=0,IMAX=factor; tmpI < IMAX; tmpI++){
			ret[tmpI] = getRandom();
		}
		return ret;
	}
	private float getRandom() {
		float ret = random.nextFloat() * this.sigma;
		return ret;
	}

	public void addElementW(int index) {
		int idx = index + 1;
//		System.out.println("eta0:" + eta0);
		etaW.put(idx, eta0);
		AccW.put(idx, 0f);
	}

	public void addAccWi(int i, float grad0, boolean w0) {
		if(w0){
			float nextAcc = AccW.get(0) + grad0;
			AccW.put(0, nextAcc);
		}else{
			int idx = i+1;
			float nextAcc = AccW.get(idx) + grad0;
			AccW.put(idx, nextAcc);
		}
	}

	public float getEtaW0(int time) {
		float ret = Float.NaN;
		if(etaUpdateMethod.equals("fix")){
			ret = etaW.get(0);
		}else if(etaUpdateMethod.equals("time")){
			ret = (float)1 / (float)(t0 + 0.1 * time);	// int1 = t
		}else if(etaUpdateMethod.equals("powerTime")){
			ret = eta0 / (float)Math.pow(time, power_t);	// int1 = t
		}else if(etaUpdateMethod.equals("ada")){
			ret =  (float)(eta0 * ( 1 / Math.sqrt(AccW.get(0)))); 
		}
		return ret;
	}

	public void addAccVif(int i, int f, float gradVif) {
		float nextVif = etaV.get(i)[f] + gradVif;
		etaV.get(i)[f] = nextVif;
	}

	public float getWi(int i, int time) {
		int idx = i + 1;
		float ret = (float)(-1.0);
		if(etaUpdateMethod.equals("fix")){
			ret = etaW.get(0);
		}else if(etaUpdateMethod.equals("time")){
			ret = (float)1 / (float)(t0 + 0.1 * time);	// int1 = t
		}else if(etaUpdateMethod.equals("powerTime")){
			ret = eta0 / (float)Math.pow(time, power_t);	// int1 = t
		}else if(etaUpdateMethod.equals("ada")){
			ret =  (float)(eta0 * ( 1 / Math.sqrt(AccW.get(idx)))); 
		}
		return ret;

	}
	
	public float getVif(int i, int f, int time) {
		float ret = (float)(-1.0);
		if(etaUpdateMethod.equals("fix")){
			ret = etaW.get(0);
		}else if(etaUpdateMethod.equals("time")){
			ret = (float)1 / (float)(t0 + 0.1 * time);	// int1 = t
		}else if(etaUpdateMethod.equals("powerTime")){
			ret = eta0 / (float)Math.pow(time, power_t);	// int1 = t
		}else if(etaUpdateMethod.equals("ada")){
			ret =  (float)(eta0 * ( 1 / Math.sqrt(AccV.get(i)[f]))); 
		}
		return ret;
	}

	public void addElementV(int index) {
		float[] tmp = getUniformFloatArray(eta0);
		etaV.put(index, tmp);
		
		tmp = getUniformFloatArray(0f);
		AccV.put(index, tmp);
	}

	private float[] getUniformFloatArray(float initNum) {
		float[] ret = new float[factor];
		Arrays.fill(ret, initNum);
		return ret;
	}


}
