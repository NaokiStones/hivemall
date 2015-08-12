package hivemall.fm.eta;

import java.util.Arrays;
import java.util.Map;

import hivemall.utils.collections.IntOpenHashMap;

public class Eta {
	protected int factor;
	protected String etaUpdateMethod;
	
	protected IntOpenHashMap<Float> etaW;
	protected IntOpenHashMap<float[]> etaV; 
	
	protected enum possibleEtaUpDateMethod {fix, time, powerTime, ada};
	
	// 
	protected float eta0 = 0.1f;
	
	// fix
	/* pass*/
	
	// time
	protected float t0 = 0.1f;
	
	// powerTime
	protected float power_t = 0.1f;
	
	// ada
	protected float alpha = 0.1f;
	protected IntOpenHashMap<Float> accW ;
	protected IntOpenHashMap<float[]> accV;
	
	
	public Eta(int factor, String etaUpdateMethod, float eta0){
		this.factor 			= factor;
		this.etaUpdateMethod	= etaUpdateMethod;
		this.eta0				= eta0;
		
		
		etaW = new IntOpenHashMap<Float>(100);
		etaW.put(0, eta0);	// for w0
		etaV = new IntOpenHashMap<float[]>(100);
	}
	
	// GET
	public float getW0(int t){
		float ret = (float)(-1.0);
		if(etaUpdateMethod.equals(possibleEtaUpDateMethod.fix.toString())){
			ret = etaW.get(0);
		}else if(etaUpdateMethod.equals(possibleEtaUpDateMethod.time.toString())){
			ret = (float)1 / (float)(t0 + 0.1 * t);	// int1 = t
		}else if(etaUpdateMethod.equals(possibleEtaUpDateMethod.powerTime.toString())){
			ret = eta0 / (float)Math.pow(t, power_t);	// int1 = t
		}else if(etaUpdateMethod.equals(possibleEtaUpDateMethod.ada.toString())){
			ret =  (float)(eta0 * ( 1 / Math.sqrt(accW.get(0)))); 
		}
		return ret;
	}
	public float getWi(int i, int t){
		if(i!=0) i++;
		float ret = (float)(-1.0);
		
		if(etaUpdateMethod.equals(possibleEtaUpDateMethod.fix.toString())){
			ret = etaW.get(i);
		}else if(etaUpdateMethod.equals(possibleEtaUpDateMethod.time.toString())){
			ret = (float)1 / (float)(t0 + 0.1 * t);	// int1 = t
		}else if(etaUpdateMethod.equals(possibleEtaUpDateMethod.powerTime.toString())){
			ret = eta0 / (float)Math.pow(t, power_t);	// int1 = t
		}else if(etaUpdateMethod.equals(possibleEtaUpDateMethod.ada.toString())){
			ret =  (float)(eta0 * ( 1 / Math.sqrt(accW.get(i+1)))); 
		}
		return ret;		
	}
	public float getVif(int i, int f, int t){
		float ret = (float)(-1.0);
		
		if(etaUpdateMethod.equals(possibleEtaUpDateMethod.fix.toString())){
			System.out.println("i :"+ i);
			System.out.println("f :"+ f);
			ret = etaV.get(i)[f];
		}else if(etaUpdateMethod.equals(possibleEtaUpDateMethod.time.toString())){
			ret = (float)1 / (float)(t0 + 0.1 * t);	// int1 = t
		}else if(etaUpdateMethod.equals(possibleEtaUpDateMethod.powerTime.toString())){
			ret = eta0 / (float)Math.pow(t, power_t);	// int1 = t
		}else if(etaUpdateMethod.equals(possibleEtaUpDateMethod.ada.toString())){
			ret =  (float)(eta0 * ( 1 / Math.sqrt(etaV.get(i+1)[f]))); 
		}
		return ret;
	}
	
	// UPDATE(SET)
	public void updateW0(float gradW0){
		float nextAccW0 = accW.get(0) + gradW0;
		accW.put(0, nextAccW0);
//		float nextEtaW0 = eta0;
//		etaW.put(0, nextW0);
	}
	public void updateWi(int i, float nextWi){
		etaW.put(i+1, nextWi);
	}
	public void updateVif(int i, int f, float nextVif){
		if(!etaV.containsKey(i)){
			insertV(i);
		}
		etaV.get(i)[f] = nextVif;
	}
	public void addAccWi(int i,float dWi){
		if(i!=0) i++;
		float tmpAccWi = accW.get(i);
		tmpAccWi += dWi;
		accW.put(i, tmpAccWi);
	}
	public void addAccVif(int i, int f, float dWif){
		float tmpAccVif = etaV.get(i)[f];
		tmpAccVif += dWif;
		accV.get(i)[f] = tmpAccVif;
	}

	public void insertW(int i) {
		etaW.put(i, eta0);
	}

	public void insertV(int i) {
		float[] tmp = new float[factor];
		Arrays.fill(tmp, eta0);
		etaV.put(i, tmp);
	}
}
