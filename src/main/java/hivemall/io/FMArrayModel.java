package hivemall.io;

public final class FMArrayModel implements FactorizationMachineModel {

    public FMArrayModel(int p) {
        // TODO Auto-generated constructor stub
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
	public void updateWi_regression(Feature[] x, double y, int i, int time) {
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
	public void updateWi_classification(Feature[] x, double y, int i, int time) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateV_classification(Feature[] x, double y, int f, int i, int time) {
		// TODO Auto-generated method stub
		
	}

}
