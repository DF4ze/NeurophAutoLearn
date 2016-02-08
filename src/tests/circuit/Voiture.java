package tests.circuit;

import org.neuroph.nnet.MultiLayerPerceptron;

import networkManager.NetworkTeacher;
import networkManager.evaluate.CorridorDriverEvaluation;
import dataManager.EvalDataSetRow;
import dataManager.ManagedDataSet;

public class Voiture {
	private NetworkTeacher pilote;
	private int position;
	private Integer numero;
	
	

	public Voiture( int numero ) {
		this.numero = numero;
		
		int nbInputs = 2;
		int nbOutputs = 1;
		double maxFitness = 10;
		double maxError = 2;
		//XorEvaluation evaluateFunction = new XorEvaluation();
		CorridorDriverEvaluation evaluateFunction= new CorridorDriverEvaluation();
		
		ManagedDataSet.setParameters(nbInputs, nbOutputs, maxFitness, maxError);
		ManagedDataSet mds = ManagedDataSet.getInstance();
		pilote = new NetworkTeacher(mds, evaluateFunction);
	}
	
	public void loadNN( String nnFilePath ){
		pilote.getNetworkRunner().setNeuralNet( (MultiLayerPerceptron) MultiLayerPerceptron.createFromFile("testedNN.nnet") );
		pilote.getNetworkRunner().getNeuralNet().learn( pilote.getManagedDataSet() );
		System.out.println("NN loaded ");
	}
	
	public void loadDS( String dsFilePath ){
		pilote.getManagedDataSet().loadFromFile(dsFilePath);
		pilote.getNetworkRunner().getNeuralNet().learn( pilote.getManagedDataSet() );
		System.out.println("DS loaded ");
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

}
