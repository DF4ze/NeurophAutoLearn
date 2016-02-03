package tests;

import java.util.Scanner;

import networkManager.NetworkTeacher;
import networkManager.evaluate.CorridorDriverEvaluation;

import org.neuroph.core.Layer;
import org.neuroph.core.data.DataSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.ConnectionFactory;
import org.neuroph.util.NeuronFactory;
import org.neuroph.util.NeuronProperties;
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
		double maxError = 2;
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
				"- 'sn' pour enregistrer le NetWork\n"+
				"- 'ln' pour charger le NetWork\n"+
				"- 'sd' pour enregistrer le DataSet\n"+
				"- 'ld' pour charger le DataSet\n"+
				"- 'c' pour compter le DataSet\n"+
				"- 'nn' pour montrer le NN\n"+
				"- 'l' pour lister le DataSet\n");

		
		Scanner sc = new Scanner(System.in);
		
		String key1;
		do{
			System.out.println("\n\n1er input?");
			key1 = sc.next();
			if( !key1.toLowerCase().equals("q") )	{
				switch( key1 ){
				case "sn":
					nn.save("testedNN.nnet");
					System.out.println("NN Saved ");
					continue;
				case "ln":
					nn = (MultiLayerPerceptron) MultiLayerPerceptron.createFromFile("testedNN.nnet");
					nn.learn(mds);
					System.out.println("NN loaded ");
					continue;
				case "sd":
					mds.save("testedDataset.tset");
					System.out.println("DataSet Saved ");
					continue;
				case "ld":
					mds.loadFromFile("testedDataset.dst");
					nn.learn(mds);
					System.out.println("DataSet Loaded ");
					continue;
				case "c":
					System.out.println("Count : "+mds.size());
					continue;
				case "nn":
					for( int i=0; i < nn.getLayers().length; i++ )
						System.out.println("\tLayer N°"+i+" : "+nn.getLayers()[i].getNeuronsCount()+" neurons");
					continue;
				case "l":
					System.out.println( "list : \n"+mds.toString() );
					continue;
				case "+":
					Layer layer = nn.getLayerAt(nn.getLayers().length -2);
					int nbNeurons = layer.getNeuronsCount();
					nn.removeLayerAt(nn.getLayers().length -2);
					layer = new Layer(nbNeurons+1);
					for( int i=0; i < nbNeurons+1; i++ ){
						layer.addNeuron(NeuronFactory.createNeuron(new NeuronProperties(TransferFunctionType.SIGMOID, true)));
					}
					nn.addLayer(nn.getLayers().length -2, layer);
					ConnectionFactory.fullConnect(nn.getLayerAt(nn.getLayers().length -3), nn.getLayerAt(nn.getLayers().length -2));
					ConnectionFactory.fullConnect(nn.getLayerAt(nn.getLayers().length -2), nn.getLayerAt(nn.getLayers().length -1));
					
					nn.learn(mds);
					
					System.out.println( "Hidden Neuron added : "+layer.getNeuronsCount()+" neurons" );
					for( int i=0; i < nn.getLayers().length; i++ )
						System.out.println("\tLayer N°"+i+" : "+nn.getLayers()[i].getNeuronsCount()+" neurons");
					continue;
				case "++":
					Layer lastLayer = nn.getLayerAt(nn.getLayers().length -2);
					int nbNeurLast = lastLayer.getNeuronsCount();
					nn.removeLayerAt(nn.getLayers().length -2);
//					for( int i =0; i < lastLayer.getNeurons().length; i++ ){
//						lastLayer.getNeurons()[i].removeAllOutputConnections();;
//					}
					lastLayer = new Layer(nbNeurLast);
					for( int i=0; i< nbNeurLast; i++ ){
						lastLayer.addNeuron(NeuronFactory.createNeuron(new NeuronProperties(TransferFunctionType.SIGMOID, true)));
					}
					nn.addLayer(nn.getLayers().length -2, lastLayer);
					
					Layer layer2 = new Layer(nbInputs);
					for( int i=0; i < nbInputs; i++ ){
						layer2.addNeuron(NeuronFactory.createNeuron(new NeuronProperties(TransferFunctionType.SIGMOID, true)));
					}
					nn.addLayer(nn.getLayers().length -1, layer2);
					ConnectionFactory.fullConnect(nn.getLayerAt(nn.getLayers().length -4), nn.getLayerAt(nn.getLayers().length -3));
					ConnectionFactory.fullConnect(nn.getLayerAt(nn.getLayers().length -3), nn.getLayerAt(nn.getLayers().length -2));
					ConnectionFactory.fullConnect(nn.getLayerAt(nn.getLayers().length -2), nn.getLayerAt(nn.getLayers().length -1));
					
					nn.learn(mds);
					
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
						
					}catch( Exception e ){
						System.out.println("il ne s'agit pas d'un double");
					}
				}
			}
			
		}while( !key1.equals("q") );
		
		System.out.println("Fin ...");
		sc.close();
		System.exit(0);
	}

}
