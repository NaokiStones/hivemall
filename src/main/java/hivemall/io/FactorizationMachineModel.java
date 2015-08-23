package hivemall.io;
import hivemall.mf.FactorizationMachineUDTF.Feature;

public interface FactorizationMachineModel {
	
	public float getW(int i);
	    
	public float getV(int i, int f);

	public void updateW0(Feature[] x, double y, long t);

	public void updateWi(Feature[] x, double y, int i, float xi, long t);

	public void updateV(Feature[] x, double y, int i, int f, long t);

	public void check(Feature[] x);	
	
	public float predict(Feature[] x);

	public int getSize();

}
