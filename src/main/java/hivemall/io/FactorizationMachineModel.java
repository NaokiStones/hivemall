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
import java.util.Map;
import java.util.Random;

public final class FactorizationMachineModel {
	
	private final int factor;
	private final String initMethod;
	private final Random random;
	private final float sigma;
	private final int group;
	
	List<Integer> pi;

	/**** OUTPUT VARIABLES ****/
	Map<Integer, Float> w;
	Map<Integer, float[]> V;
	
	
    public FactorizationMachineModel(String initMethod, int factor, float sigma, List<Integer> piUpperLimit) {
    	this.factor = factor;
    	this.initMethod = initMethod;
    	this.sigma = sigma;
    	this.group = piUpperLimit.size();
    	for(int i=0; i<this.group; i++){
    		
    	}
    	
    	random = new Random();
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
    			ret[i] = (float)random.nextGaussian() * sigma;
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
    	return w.get(idx);
    }
    public float getV(int i, int f){
    	return V.get(i)[f];
    }
    public int getPi(int i){
    	int ret = -1;
    	for(int j=0; j<this.group; j++){
    		if(j <= pi.get(j)){
    			return j;
    		}
    	}
    	System.out.println("cannnot find the group");//***
    	return ret;
    }
  
    // UPDATE(SET)
    public void updateW0(float nextW0){
    	this.w.put(0, nextW0);
    }
    public void updateWi(int i, float nextWi){
    	this.w.put(i+1, nextWi);
    }
    public void updateVif(int i, int f, float nextVif){
    	this.V.get(i)[f] = nextVif;
    }
    
    public void addW(int i){
    	this.w.put(i, (float)0);
    }
    
    public void addV(int i){
    	float[] tmp = new float[factor];
    	Arrays.fill(tmp, (float)0);
    	this.V.put(i, tmp);
    }
    
}
