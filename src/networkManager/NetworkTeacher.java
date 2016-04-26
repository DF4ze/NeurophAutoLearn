package networkManager;

import networkManager.evaluate.IEvaluateFunction;
import networkManager.nnetwork.NetworkRunner;

import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.core.exceptions.NeurophException;
import org.neuroph.nnet.learning.BackPropagation;

import dataManager.EvalDataSetRow;
import dataManager.ManagedDataSet;

public class NetworkTeacher implements LearningEventListener{
	
	public static int BEGINNING = 0;
	public static int RUNNING = 1;
	

	private ManagedDataSet mds;
	private IEvaluateFunction evaluateFunction;
	private NetworkRunner networkRunner;
	private Thread tDeleguate;
	
	/**
	 * Maximum of fitness this network can have
	 */
	private Double maxFitness;
	
	/**
	 * Percent less thant maxFitness that is authorized
	 */
	private Double acceptedError;
	
	/**
	 * Size of inputs vector
	 */
	private Integer inputSize;
	/**
	 * Size of outputs vector
	 */
	private Integer outputSize;
	
	
	
	
	
	
	
	public NetworkTeacher() {
	}

	public NetworkTeacher(ManagedDataSet mds) throws NeurophException{
		setManagedDataSet(mds);
		
	}

	public NetworkTeacher(IEvaluateFunction function) {
		setEvaluateFunction(function);
	}
	
	/**
	 * Get a NetworkTeacher based on the ManagedDataSet and the EvaluateFunction given in parameters
	 * ManagedDataSet wil retrieve all admited rows
	 * EvaluateFunction will set the fitness of outputs
	 * 
	 * @param mds the ManagedDataSet that contains admited rows (could be empty)
	 * @param evaluateFunction from IEvaluateFunction, implement the evaluate() function
	 */
	public NetworkTeacher(ManagedDataSet mds, IEvaluateFunction evaluateFunction) throws NeurophException{
		setManagedDataSet(mds);
		setEvaluateFunction(evaluateFunction);
	}
	
	
	
	
	/**
	 * Will add this row to the <code>ManagedDataSet</code>
	 * And ask to calculate and evaluate outputs vectors
	 * 
	 * If evaluation is success, row is keeped for next try
	 * else it will be send to "re-evaluate queue"
	 * 
	 * @param evalRow a row that contains inputs vectors (param IN/OUT : ouputs and fitness will be set) 
	 * @return a simple DataSetRow that contains inputs and outputs vector
	 * @throws NeurophException
	 */
	public DataSetRow addInputs( EvalDataSetRow evalRow ) throws NeurophException{
		// verifications...
		if( getEvaluateFunction() == null )
			throw new NeurophException( "EvaluateFunction not set!" );
		
		// Setting ManagedDataSet if it isn't
		if( getManagedDataSet() == null ){
			// verifications...
			if( getInputSize() == null || 
					getOutputSize() == null ||
					getMaxFitness() == null ||
					getAcceptedError() == null)
				throw new NeurophException("ManagedDataSet information not correctly set!");
			
			// retrieve instance
			setManagedDataSet( new ManagedDataSet(
					getInputSize(),
					getOutputSize(),
					getMaxFitness(),
					getAcceptedError()) );
		}
		
		// learn network if datas changed
		if( getManagedDataSet().hasChanged() ){
//			BackPropagation learningRule = getNetworkRunner().getNeuralNet().getLearningRule();
//			MultiLayerPerceptron neuralNet = new MultilayerPerceptronOptimazer<>()
//	                .withLearningRule(learningRule)
//	                .createOptimalModel(getManagedDataSet());
//
//	        getNetworkRunner().setNeuralNet(neuralNet);
			getNetworkRunner().getNeuralNet().learn( getManagedDataSet() );
		}
		
		// and get outputs
		evalRow = getNetworkRunner().calculate(evalRow);
		
		// retrieve fitness
		Double fitness = evaluateFunction.evaluate(evalRow);
		if( fitness == null )
			return null;
		
		evalRow.setFitness(fitness);
		
		// starting Reevaluate treatment if not launched
		if( tDeleguate == null ){
			tDeleguate = new Thread(new ReevaluateTeacherDeleguate(mds, getEvaluateFunction()));
			tDeleguate.start();
		}
		
		
		// add it to the ManagerDataSet
		mds.addRow(evalRow);
		
		return evalRow.toDataSetRow();
	}
	
	
	

	/**
	 * @return the ManagedDataSet
	 */
	public ManagedDataSet getManagedDataSet() {
		return mds;
	}

	/**
	 * @param mds the ManagedDataSet to set
	 */
	public void setManagedDataSet(ManagedDataSet mds) throws NeurophException {
		this.mds = mds;
		
		if( mds.getMaxFitness() != null &&
				mds.getInputNb() != null &&
						mds.getOutputNb() != null &&
								mds.getAcceptedError() != null ){
			
			setMaxFitness(mds.getMaxFitness());
			setInputSize(mds.getInputNb());
			setOutputSize(mds.getOutputNb());
			setAcceptedError(mds.getAcceptedError());
			
			setNetworkRunner(new NetworkRunner(getInputSize(), getOutputSize(), getManagedDataSet()));
			BackPropagation bp = getNetworkRunner().getNeuralNet().getLearningRule();
			bp.addListener(this);
		}else
			throw new NeurophException("ManagedDataSet not correctly initialized!");

	}
	
	
	@Override
	public void handleLearningEvent(LearningEvent event) {
		BackPropagation bp = (BackPropagation)event.getSource();
        if (event.getEventType() == LearningEvent.Type.LEARNING_STOPPED)
            System.out.println(bp.getCurrentIteration() + ". iteration : "+ bp.getTotalNetworkError());
//        else
//        	System.out.println(bp.getCurrentIteration() + ". iteration : "+ bp.getTotalNetworkError());
        
		//LearningEvent.Type.EPOCH_ENDED
	}
	

	/**
	 * @return the evaluateFunction
	 */
	public IEvaluateFunction getEvaluateFunction() {
		return evaluateFunction;
	}

	/**
	 * @param evaluateFunction the evaluateFunction to set
	 */
	public void setEvaluateFunction(IEvaluateFunction evaluateFunction) {
		this.evaluateFunction = evaluateFunction;
	}

	/**
	 * @return the maxFitness
	 */
	public Double getMaxFitness() {
		return maxFitness;
	}

	/**
	 * @param maxFitness the maxFitness to set
	 */
	public void setMaxFitness(Double maxFitness) {
		this.maxFitness = maxFitness;
	}

	/**
	 * @return the acceptedError
	 */
	public Double getAcceptedError() {
		return acceptedError;
	}

	/**
	 * @param acceptedError the acceptedError to set
	 */
	public void setAcceptedError(Double acceptedError) {
		this.acceptedError = acceptedError;
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
	public  void setOutputSize(Integer outputSize) {
		this.outputSize = outputSize;
	}

	/**
	 * @return the networkRunner
	 */
	public NetworkRunner getNetworkRunner() {
		return networkRunner;
	}

	/**
	 * @param networkRunner the networkRunner to set
	 */
	public void setNetworkRunner(NetworkRunner networkRunner) {
		this.networkRunner = networkRunner;
	}


	
	


}
