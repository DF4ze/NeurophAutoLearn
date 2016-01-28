package tests;

import java.util.Scanner;

import networkManager.NetworkTeacher;
import networkManager.evaluate.CorridorDriverEvaluation;

import org.neuroph.core.Layer;
import org.neuroph.core.Neuron;
import org.neuroph.core.input.InputFunction;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

import dataManager.EvalDataSetRow;
import dataManager.ManagedDataSet;

public class TestNetworkTeacher {

	public TestNetworkTeacher() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		
		int nbInputs = 2;
		int nbOutputs = 1;
		double maxFitness = 10;
		double maxError = 5;
		//XorEvaluation evaluateFunction = new XorEvaluation();
		CorridorDriverEvaluation evaluateFunction= new CorridorDriverEvaluation();
		
		ManagedDataSet.setParameters(nbInputs, nbOutputs, maxFitness, maxError);
		ManagedDataSet mds = ManagedDataSet.getInstance();
		NetworkTeacher teacher = new NetworkTeacher(mds, evaluateFunction);

		MultiLayerPerceptron nn = teacher.getNetworkRunner().getNeuralNet();
		
		System.out.println("=====================================\n"+
				"  ---=== Auto apprentissage ===---\n\n"+
				"Evaluation : '"+evaluateFunction+"'\n"+
				"nb inputs :  "+nbInputs+"\n"+
				"nb outputs : "+nbOutputs+"\n"+
				"maxFitness : "+maxFitness+"\n"+
				"maxError :   "+maxError+"\n"+
				"nb Layer :   "+nn.getLayers().length);
		for( int i=0; i < nn.getLayers().length; i++ )
			System.out.println("\tLayer N°"+i+" : "+nn.getLayers()[i].getNeuronsCount()+" neurons");
		
		System.out.println("\n"+
				"=====================================\n\n"+
				"- 'q' pour quitter\n"+
				"- 'c' pour compter le DataSet\n"+
				"- 's' pour lister le DataSet\n");

		
		Scanner sc = new Scanner(System.in);
		
		String key1;
		do{
			System.out.println("\n\n1er input?");
			key1 = sc.next();
			if( !key1.equals("q") )	{
				switch( key1 ){
				case "c":
					System.out.println("Count : "+mds.size());
					continue;
				case "s":
					System.out.println( "list : \n"+mds.toString() );
					continue;
				case "+":
					Layer layer = nn.getLayerAt(1);
					int nbNeurons = layer.getNeuronsCount();
					nn.removeLayerAt(1);
					layer = new Layer(nbNeurons+1);
					//layer.
					nn.addLayer(1, layer);
					System.out.println( "Hidden Neuron added : "+layer.getNeuronsCount()+" neurons" );
					continue;
				case "++":
					Layer layer2 = new Layer(nbInputs);
					nn.addLayer(1, layer2);
					System.out.println( "Layer added with "+layer2.getNeuronsCount()+" neurons" );
					for( int i=0; i < nn.getLayers().length; i++ )
						System.out.println("\tLayer N°"+i+" : "+nn.getLayers()[i].getNeuronsCount()+" neurons");

					continue;
				}
				
				System.out.println("2eme input?");
				String key2 = sc.next();
				if( !key2.equals("q") )	{
					try{
						double[] inputs = new double [2];
						inputs[0] = Double.parseDouble(key1);
						inputs[1] = Double.parseDouble(key2);
						
						// on créé l'enregistrement
						EvalDataSetRow evalRow = new EvalDataSetRow(inputs, 1);
						
						// on le donne au teacher qui va modifier la fitness de chacuns des outputs
						Object o = teacher.addInputs(evalRow);
						
						if( o != null )
							System.out.println("Resultats => output "+evalRow.getDesiredOutput()[0]
									+" | Fitness "+evalRow.getFitness());
						else
							System.out.println( "Inputs non traités" );
						
					}catch( ParseException e ){
						System.out.println("il ne s'agit pas d'un double");
					}
				}
			}
			
		}while( !key1.equals("q") );
		
		System.out.println("Fin ...");
		System.exit(0);
	}

}
