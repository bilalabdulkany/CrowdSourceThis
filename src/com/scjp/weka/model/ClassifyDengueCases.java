package com.scjp.weka.model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.Serializable;

import com.scjp.weka.bean.DengueBean;

import android.content.Context;
import android.content.res.AssetManager;
import weka.classifiers.trees.RandomTree;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;

public class ClassifyDengueCases implements Serializable {

	private static final String MODELS_PATH = "model/";
	private RandomTree tree;
	AssetManager am;
	public double readClassifier(DengueBean bean, String year, String district,Context context) throws Exception {
		// Initialize the random tree.
		double result = 0;
		String path = MODELS_PATH + year + "/" + district + ".model";
		am = context.getAssets();
		InputStream is = am.open(path);
		 ObjectInputStream ois = new ObjectInputStream(is);
		 
		tree = (RandomTree)ois.readObject(); //SerializationHelper.read();
		ois.close();
		System.out.println("Took the arff file");
		Instances instances = this.getDataset();
		instances.setClassIndex(instances.numAttributes() - 2);
		String testCases = bean.getMeanTemp() + "," + bean.getMeanHumidity() + "," + bean.getMeanWindspeed() + ","
				+ bean.getDengueCases() + "," + bean.getMeanSeaPressure();
		instances.add(getInstanceFromSignal(testCases, ","));

		System.out.println(instances.toSummaryString());

		for (int i = 0; i < instances.numAttributes(); i++) {
			System.out.println(instances.attribute(i).toString());

		}

		for (int i = 0; i < instances.numInstances(); i++) {
			Instance instance = instances.instance(i);
			System.out.println("Instance to string:" + instance.toString());
			result = tree.classifyInstance(instance);
			instance.setClassValue(result);
			System.out.println("Instance classified as " + result);
		}

		return result;
	}

	private Instances getDataset() throws Exception {
		InputStream is = am.open("model/DataFile.arff");
		
		Instances instancesFromFile = new Instances(new BufferedReader(new InputStreamReader(is, "UTF-8")));
		
		System.out.println("no problem in reading file");
		return instancesFromFile;
	}

	private DenseInstance getInstanceFromSignal(String signal, String separator) {
		String[] dataString = signal.split(separator);
		double data[] = new double[dataString.length];
		for (int i = 0; i < dataString.length; i++) {
			try {
				data[i] = Double.parseDouble(dataString[i]) - 1;
				System.out.println("parsed:" + dataString[i] + " as " + data[i]);
			} catch (Exception e) {
				data[i] = Double.NaN;
				System.out.println("parsed:" + dataString[i] + " as " + data[i]);
			}
		}
		DenseInstance instance = new DenseInstance(1.0, data);
		System.out.println("instance is:" + instance.toString());
		return instance;
	}


}
