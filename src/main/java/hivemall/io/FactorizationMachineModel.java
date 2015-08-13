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
import hivemall.mf.FactorizationMachineUDTF.Feature;

public interface FactorizationMachineModel {

    public float getW(int i);
    
    public float getV(int i, int f);

	public void initParamsForPi(int groupSize, int factor);

	public void initWforW0();

	public boolean check(int index);

	public void addElement(int index);

	public void updateW0_regression(final Feature[] x, double y, int time);

	public void updateV_regression(Feature[] x, double y, int f, int i, int time);

	public void updateW0_classification(Feature[] x, double y, int time);

	public void updateWi_classification(Feature[] x, double y, int i, int idxForX, int time);

	public void updateV_classification(Feature[] x, double y, int f, int i, int time);

	public void updateWi_regression(Feature[] x, double y, int i, int idxForX, int time);

	public Float predict(Feature[] features);

	void initParamsForPi(int groupSize, int factor, int col);

    
}
