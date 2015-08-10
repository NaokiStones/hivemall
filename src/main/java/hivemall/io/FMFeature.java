package hivemall.io;

public class FMFeature {
	private final int key;
	private final float value;
	
	public FMFeature(int key, float value){
		this.key = key;
		this.value = value;
	}
	public int getKey(){
		return key;
	}
	public float getValue(){
		return value;
	}
}
