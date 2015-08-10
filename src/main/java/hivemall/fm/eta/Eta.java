package hivemall.fm.eta;

import java.util.Map;

public class Eta {
	protected int factor;
	
	protected Map<Integer, Float> etaW;
	protected Map<Integer, float[]> etaV; 
	
	protected Map<Integer, Float> accW;
	protected Map<Integer, float[]> accV;
	
	
	
	public Eta(int factor){
		this.factor = factor;
	}
	
	// GET
	public float getW0(){
		return etaW.get(0);
	}
	public float getWi(int i){
		return etaW.get(i);
	}
	public float getVif(int i, int f){
		return etaV.get(i)[f];
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
}
