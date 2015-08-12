/*
 * Hivemall: Hive scalable Machine Learning Library
 *
 * Copyright (C) 2015 Makoto YUI
 * Copyright (C) 2013-2015 National Institute of Advanced Industrial Science and Technology (AIST)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package hivemall.io;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import hivemall.utils.collections.IntOpenHashMap;

public final class FactorizationMachineModel {

	private final int factor;
	private final String initMethod;
	private final Random random;
	private final float sigma;
	private final int group;
	
	List<Integer> pi;
	
	//protected IntOpenHashMap<float[]> lambdaV; 
	protected float[] lambdaW; // getW(i, f)	
	//protected float[][] lambdaV; // getV(i, f)
	
	/**** OUTPUT VARIABLES ****/
	IntOpenHashMap<Float> w;
	IntOpenHashMap<float[]> V;
	
	
    public FactorizationMachineModel(String initMethod, int factor, float sigma, List<Integer> piUpperLimit) {
    	this.initMethod = initMethod;
    	this.factor = factor;
    	this.sigma = sigma;
    	this.group = piUpperLimit.size();
    	
    	this.V = new IntOpenHashMap<float[]>(301);
    	this.w = new IntOpenHashMap<Float>(301);
    	
    	this.pi = piUpperLimit;
    	
    	initForW0();
    	
    	random = new Random();
    }
    
    public void initForW0(){
    	this.w.put(0, 0f);
    }
    
    public boolean exist(int i){
    	if(w.containsKey(i)){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    public void insertNewVector(int i){
    	V.put(i, getNewVector());
    }
    
    public void initLambdas(int group, int factor, float initLambdas){
    	this.lambdaW = new float[group+1];	// lambdaW[0]:w0, lambdaW[1]:wi[0], ...
    	Arrays.fill(lambdaW, initLambdas);
    }

    
    // GET
    public float[] getRecord(int i){
    	if(!V.containsKey(i)){
    		insertNewVector(i);
    	}
    	return V.get(i);
    }
    
    public float[] getNewVector(){
   		float[] ret = new float[factor];
    	if(initMethod.equals("random")){
    		for(int i=0; i<factor; i++){
    			ret[i] = getRandom();
    		}
    	}else{
    		// other method
    	}
    	return ret;
    }
    
    public float getW0(){
    	return w.get(0);
    }
    public float getWi(int idx){
    	idx++;
    	System.out.println("getWi index:" + idx);	//****
    	/*
    	if(!w.containsKey(idx)){
    		w.put(idx, 0f);
    	}
    	*/
    	System.out.println(w.containsKey(idx));
    	Float v = w.get(idx);
    	
    	return v.floatValue();
    }
    public float getV(int i, int f){
    	if(!V.containsKey(i)){
    		float[] tmp = new float[factor];
    		for(int j=0; j<factor; j++) tmp[j] = getRandom();
    		V.put(i, tmp);
    	}
    	return V.get(i)[f];
    }
    public int getPi(int i){
    	int ret = -1;
    	System.out.println(i);//**
    	for(int j=0; j<this.group; j++){
    		System.out.println("pi:" + pi.get(j));
    		if(i <= pi.get(j)){
    			return j;
    		}
    	}
    	
    	/*tmp*/
    	System.out.println("cannnot find the class of " + i);
    	System.exit(1);
		return ret;
    }
    //** lambda
    public float getLambdaW(int pi){
    	return lambdaW[pi];
    }
  
    // UPDATE(SET)
    public void updateW0(float nextW0){
    	this.w.put(0, nextW0);
    }
    public void updateWi(int i, float nextWi){
    	this.w.put(i+1, nextWi);
    }
    public void updateVif(int i, int f, float nextVif){
    	if(!V.containsKey(i)){
    		float[] tmpV = new float[factor];
    		for(int j=0; j<factor; j++){
    			tmpV[j] = getRandom();
    		}
    		V.put(i, tmpV);
    	}
    	this.V.get(i)[f] = nextVif;
    }
    
	public void insertW(int i) {
		System.out.println("insertW:" + i);	//****
		w.put(i+1, 0f);
	}
	
	public void insertV(int i){
		System.out.println("insertV:" + i);	//****
		float[] tmp =  new float[factor];
		for(int j=0; j<factor; j++) tmp[j] = getRandom();
		V.put(i, tmp);
	}
	public float getRandom(){
		return random.nextFloat() * sigma;	// tmp
	}
	
	public IntOpenHashMap<Float> getW(){
		return w;
	}
	public IntOpenHashMap<float[]> getV(){
		return V;
	}
    
}
