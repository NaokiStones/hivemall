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
	protected Map<Integer, Float> accW ;
	protected Map<Integer, float[]> accV;
	
	
	public Eta(int factor, String etaUpdateMethod, float eth0){
		this.factor 			= factor;
		this.etaUpdateMethod	= etaUpdateMethod;
		this.eta0				= eth0;
	}
	
	// GET
	public float getW0(int t){
		float ret = (float)(-1.0);
		if(etaUpdateMethod.equals(possibleEtaUpDateMethod.fix)){
			ret = etaW.get(0);
		}else if(etaUpdateMethod.equals(possibleEtaUpDateMethod.time)){
			ret = (float)1 / (float)(t0 + 0.1 * t);	// int1 = t
		}else if(etaUpdateMethod.equals(possibleEtaUpDateMethod.powerTime)){
			ret = eta0 / (float)Math.pow(t, power_t);	// int1 = t
		}else if(etaUpdateMethod.equals(possibleEtaUpDateMethod.ada)){
			ret =  (float)(eta0 * ( 1 / Math.sqrt(accW.get(0)))); 
		}
		return ret;
	}
	public float getWi(int i, int t){
		float ret = (float)(-1.0);
		
		if(etaUpdateMethod.equals(possibleEtaUpDateMethod.fix)){
			ret = etaW.get(i+1);
		}else if(etaUpdateMethod.equals(possibleEtaUpDateMethod.time)){
			ret = (float)1 / (float)(t0 + 0.1 * t);	// int1 = t
		}else if(etaUpdateMethod.equals(possibleEtaUpDateMethod.powerTime)){
			ret = eta0 / (float)Math.pow(t, power_t);	// int1 = t
		}else if(etaUpdateMethod.equals(possibleEtaUpDateMethod.ada)){
			ret =  (float)(eta0 * ( 1 / Math.sqrt(accW.get(i+1)))); 
		}
		return ret;		
	}
	public float getVif(int i, int f, int t){
		float ret = (float)(-1.0);
		
		if(etaUpdateMethod.equals(possibleEtaUpDateMethod.fix)){
			ret = etaV.get(i+1)[f];
		}else if(etaUpdateMethod.equals(possibleEtaUpDateMethod.time)){
			ret = (float)1 / (float)(t0 + 0.1 * t);	// int1 = t
		}else if(etaUpdateMethod.equals(possibleEtaUpDateMethod.powerTime)){
			ret = eta0 / (float)Math.pow(t, power_t);	// int1 = t
		}else if(etaUpdateMethod.equals(possibleEtaUpDateMethod.ada)){
			ret =  (float)(eta0 * ( 1 / Math.sqrt(etaV.get(i+1)[f]))); 
		}
		return ret;
	}
	
	// UPDATE(SET)
	public void updateW0(float nextW0){
		etaW.put(0, nextW0);
	}
	public void updateWi(int i, float nextWi){
		etaW.put(i+1, nextWi);
	}
	public void updateVif(int i, int f, float nextVif){
		etaV.get(i)[f] = nextVif;
	}
	public void addAccWi(int i,float dWi){
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
