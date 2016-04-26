package tests;

import org.neuroph.core.data.DataSet;

import dataManager.FitDataSet;
import dataManager.FitDataSetRow;

public class TestDataSetFit {

	public TestDataSetFit() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String args[]) {
		FitDataSet dataSet = new FitDataSet( 3, 1, true);
		dataSet.addRow(new FitDataSetRow( new double[]{0.5,0.6,0.7}, new double[]{0.4}, 10d));
		dataSet.addRow(new FitDataSetRow( new double[]{0.4,0.5,0.6}, new double[]{0.3}, 10d));
		dataSet.addRow(new FitDataSetRow( new double[]{0.3,0.2,0.4}, new double[]{0.2}, 10d));
		
		System.out.println(dataSet.toString()+"\n\n");
		System.out.println( dataSet.toCSV());
		
		dataSet.setFilePath("testDataSetFit.dst");
		dataSet.save();
		System.out.println("\nDatas Saved");
		
		DataSet ds = DataSet.load("testDataSetFit.dst");
		System.out.println("\nDatas Loaded : ");
		System.out.println( ds.toCSV());
		
	}
}
