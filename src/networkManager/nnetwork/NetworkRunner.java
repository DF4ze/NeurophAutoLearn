package networkManager.nnetwork;

import org.neuroph.core.exceptions.NeurophException;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.MomentumBackpropagation;

import dataManager.EvalDataSetRow;
import dataManager.ManagedDataSet;

public class NetworkRunner {

	private Integer inputSize;
	private Integer outputSize;
	private ManagedDataSet mds;
	private MultiLayerPerceptron neuralNet;
	private MomentumBackpropagation learningRule;
	//private ArrayList<EvalDataSetRow> last2 = new ArrayList<>();
	
	/**
	 * Get an instance of NetworkRunner giving it the input, ouput size and a ManagedDataSet
	 * This instance will generate the neuronnal network according to inputs  
	 * 
	 * @param inputSize
	 * @param outputSize
	 */
	public NetworkRunner(Integer inputSize, Integer outputSize, ManagedDataSet mds) {
		if( inputSize == null || outputSize == null )
			throw new NeurophException("Input or Outputs can't be null!");
		if( mds == null )
			throw new NeurophException("ManagedDataSet can't be null!");
		
		this.mds = mds;
		this.inputSize = inputSize;
		this.outputSize = outputSize;
		
        //create neural network
		neuralNet = new MultiLayerPerceptron(inputSize, inputSize, outputSize);
        //get backpropagation learning rule from network
		//learningRule = neuralNet.getLearningRule();

		learningRule = new MomentumBackpropagation();
        learningRule.setLearningRate(0.2);
        learningRule.setMaxError(0.01);
        learningRule.setMaxIterations(5000);
        learningRule.setMomentum(0.7);
        
        neuralNet.setLearningRule(learningRule);
		
	}


	


	/**
	 * Will run the network with the given inputs in row
	 * then set the ouput in this same row and return it
	 * 
	 * @param row EvalDataSetRow that contains inputs
	 * @return the same EvalDataSetRow with outputs sets
	 */
	public EvalDataSetRow calculate( EvalDataSetRow row ){
		/*try{
			double[] inputs1 = last2.get(0).getInput();
			double[] outputs1 = last2.get(0).getDesiredOutput();
			double[] inputs2 = last2.get(1).getInput();
			double[] outputs2 = last2.get(1).getDesiredOutput();
			if( inputs1[0] == inputs2[0] &&
					inputs1[1] == inputs2[1] &&
					outputs1[0] == outputs2[0]){
				//neuralNet.randomizeWeights(new NguyenWidrowRandomizer(0.0, 1.0));
				neuralNet.randomizeWeights(new WeightsRandomizer());
				System.out.println("Randomized");
			}
		}catch(Exception e){
			
		}*/
		
		//learn();
		
		neuralNet.setInput( row.getInput() );
        neuralNet.calculate();
        //Layer layer = LayerFactory.createLayer(5, TransferFunctionType.SIGMOID);
        //neuralNet.addLayer(layer);
        
        
        row.setDesiredOutput( neuralNet.getOutput() );
        
       /* try{
        	if( last2.size() == 0 )
            	last2.add(row);
            else if( last2.size() == 1 ){
            	last2.add(last2.get(0));
            	last2.set(0, row);
            }else{
            	last2.set(1, last2.get(0));
            	last2.set(0, row);
            }
        	
        	
        }catch( Exception e ){
        }
        */
        
        return row;
	}
	
	public void learn(){
		learn(mds);
	}
	public void learn(ManagedDataSet mds){
        //add learning listener in order to print out training info
        /*learningRule.addListener(new LearningEventListener() {
            @Override
            public void handleLearningEvent(LearningEvent event) {
                BackPropagation bp = (BackPropagation) event.getSource();
                if (event.getEventType().equals(LearningEvent.Type.LEARNING_STOPPED)) {
                    System.out.println();
                    System.out.println("Training completed in " + bp.getCurrentIteration() + " iterations");
                    System.out.println("With total error " + bp.getTotalNetworkError() + '\n');
                } else {
                    //System.out.println("Iteration: " + bp.getCurrentIteration() + " | Network error: " + bp.getTotalNetworkError());
                }
            }
        });*/

        //train neural network
        neuralNet.learn(mds);
        //System.out.println( "learned" );

	}
	
	/**
	 * @return the inputSize
	 */
	public Integer getInputSize() {
		return inputSize;
	}

	/**
	 * @param inputSize the inputSize to set
	 */
	public void setInputSize(Integer inputSize) {
		this.inputSize = inputSize;
	}

	/**
	 * @return the outputSize
	 */
	public Integer getOutputSize() {
		return outputSize;
	}

	/**
	 * @param outputSize the outputSize to set
	 */
	public void setOutputSize(Integer outputSize) {
		this.outputSize = outputSize;
	}

	/**
	 * @return the neuralNet
	 */
	public MultiLayerPerceptron getNeuralNet() {
		return neuralNet;
	}

	/**
	 * @param neuralNet the neuralNet to set
	 */
	public void setNeuralNet(MultiLayerPerceptron neuralNet) {
		this.neuralNet = neuralNet;
	}





	/**
	 * @return the learningRule
	 */
	public MomentumBackpropagation getLearningRule() {
		return learningRule;
	}





	/**
	 * @param learningRule the learningRule to set
	 */
	public void setLearningRule(MomentumBackpropagation learningRule) {
		this.learningRule = learningRule;
	}

}
