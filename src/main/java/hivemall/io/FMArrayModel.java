package hivemall.io;

import hivemall.mf.FactorizationMachineUDTF.Feature;

public class FMArrayModel implements FactorizationMachineModel{
	/** Learning Parameters*/
	private float w0;
	private float[] w;
	private float[][] V;
	
	private boolean classification;
	
	/** Regulation Parameters*/
	private	float lambdaW0;
	private float[] lambdaW;
	private float[][] lambdaV;
	
	/** */
	private Eta eta;
	
    public FMArrayModel(boolean classification, float sigma, float lambda0, int factor, float eta0, String etaUpdateMethod) {
    	/** Initialize from Argument*/
    	this.classification = classification;
    	
    	/** Initialize Without Argument*/

    	
    	/** Initialize using Arguments*/
		init(sigma, lambda0, eta0);
    	eta = new Eta(factor, eta0, sigma, etaUpdateMethod, eta0);
    }   
    
	
	private void init(float sigma, float lambda0, float eta0){
		// w0, w, V
		this.w0 = 0f;
		this.w = getUniformArray(0f);
		this.V = getRandomMatrix(sigma);
		
		// lambdaW0, lambdaW, lambdaV
		lambdaW0 = lambda0;
		lambdaW = getUniformArray(lambda0);
		lambdaV = getUniformMatrix(lambda0);
		
		// etaW, etaV
		//eta.etaW = getUniformArray(eta0);
		//eta.etaV = getUnifor
	}

	private float[][] getUniformMatrix(float lambda0) {
		// TODO Auto-generated method stub
		return null;
	}

	private float[][] getRandomMatrix(float sigma) {
		// TODO Auto-generated method stub
		return null;
	}

	private float[][] getRandomMatrix(float sigma) {
		// TODO Auto-generated method stub
		return null;
	}

	private float[] getUniformArray(float f) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getW(int i) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getV(int i, int f) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void initParamsForPi(int groupSize, int factor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initWforW0() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean check(int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addElement(int index) {
		// TODO Auto-generated method stub
		
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
	public void initParamsForPi(int groupSize, int factor, int col) {
		// TODO Auto-generated method stub
		
	}
}