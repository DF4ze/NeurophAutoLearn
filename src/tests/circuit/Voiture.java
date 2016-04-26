package tests.circuit;

import java.io.File;

import networkManager.NetworkTeacher;
import networkManager.evaluate.CorridorDriverEvaluation;
import networkManager.nnetwork.NetworkRunner;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.nnet.MultiLayerPerceptron;

import dataManager.EvalDataSetRow;
import dataManager.ManagedDataSet;

public class Voiture {
	private NetworkTeacher pilote;
	private int position;
	private Integer numero;
	private String dataFile = "voiture";
	
	

	public Voiture( int numero ) {
		this.numero = numero;
		
		dataFile = dataFile+numero+".tset";
		
		int nbInputs = 2;
		int nbOutputs = 1;
		double maxFitness = 10;
		double maxError = 2;
		
		ManagedDataSet mds = new ManagedDataSet(nbInputs, nbOutputs, maxFitness, maxError);
		CorridorDriverEvaluation evaluateFunction= new CorridorDriverEvaluation( mds );
		pilote = new NetworkTeacher(mds, evaluateFunction);
		
		File file = new File(dataFile);
		if( file.exists() ){
			loadDS( dataFile );
		}
	}
	
	public void loadNN( String nnFilePath ){
		pilote.getNetworkRunner().setNeuralNet( (MultiLayerPerceptron) NeuralNetwork.createFromFile("testedNN.nnet") );
		pilote.getNetworkRunner().getNeuralNet().learn( pilote.getManagedDataSet() );
		System.out.println("NN loaded ");
	}
	
	public void loadDS( String dsFilePath ){
		pilote.getManagedDataSet().loadFromFile(dsFilePath);
		try {
			Thread.sleep( 250 );
		} catch (InterruptedException e) {}
		pilote.getNetworkRunner().getNeuralNet().learn( pilote.getManagedDataSet() );
		System.out.println("DS loaded ");
	}
	
	public void saveDS( String dsFilePath ){
		ManagedDataSet mds = pilote.getManagedDataSet();
		mds.save(dsFilePath);
	}
	
	public void saveDS(  ){
		saveDS(dataFile);
	}
	
	public Double getDirection( double gauche, double droite ){
		double[] inputs = new double [2];
		inputs[0] = gauche;
		inputs[1] = droite;
		
		// on créé l'enregistrement
		EvalDataSetRow evalRow = new EvalDataSetRow(inputs, 1);
		
		// on le donne au teacher qui va modifier la fitness de chacuns des outputs
		Object o = pilote.addInputs(evalRow);
		
		Double direction = null;
		if( o != null )
			direction = evalRow.getDesiredOutput()[0];
		
		return direction;
	}

	/**
	 * @return the position
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(int position) {
		this.position = position;
	}

	/**
	 * @return the numero
	 */
	public Integer getNumero() {
		return numero;
	}

	/**
	 * @param numero the numero to set
	 */
	public void setNumero(int numero) {
		this.numero = numero;
	}

	public NetworkRunner getNetWorkRunner(){
		return pilote.getNetworkRunner();
	}
	public ManagedDataSet getDataSet(){
		return pilote.getManagedDataSet();
	}
}
