/**
 * 
 */
package dataManager;

import java.util.LinkedList;

import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.exceptions.NeurophException;
import org.neuroph.core.exceptions.VectorSizeMismatchException;

/**
 * This class recept Fited Supervised inputs and make the choice to put them in 
 * a "valid" train set or sent it to reevaluate due to fitness
 * 
 * @author Clement ORTIZ
 *
 */
public class ManagedDataSet extends FitDataSet {

	private static final long serialVersionUID = 1L;

	/**
	 * FIFO for rows to reeval
	 */
	private static LinkedList<EvalDataSetRow> reevalItems = new LinkedList<>();
	/**
	 * FIFO for new row to juge by fitness
	 */
	private static LinkedList<EvalDataSetRow> waitingLine = new LinkedList<>();
	/**
	 * Singleton 
	 */
	private static ManagedDataSet me = null;


	/**
	 * Maximum of fitness this network can have
	 */
	private static Double maxFitness;
	
	/**
	 * Percent less thant maxFitness that is authorized
	 */
	private static Double acceptedError;
	
	/**
	 * Size of inputs vector
	 */
	private static Integer inputSize;
	/**
	 * Size of outputs vector
	 */
	private static Integer outputSize;
	
	/**
	 * Thread that manage the sort of incoming rows
	 */
	private static Thread tSorter;
	
	
	/**
	 * Create an instance of DataSetManger that will validate new rows with fitness 
	 * 
	 * @param inputSize Number of inputs
	 * @param outputSize Number of outputs
	 * @param maxFitness Maximum of fitness
	 * @param acceptedError Percent less thant maxFitness is authorized
	 */
	private ManagedDataSet() throws NeurophException{
		super(inputSize, outputSize, true);
		
		if( inputSize == null ||
				outputSize == null ||
				maxFitness == null ||
				acceptedError == null)
			throw new NeurophException("One or more parameters aren't set");
		
		if( launchSorter() )
			System.out.println( "Thread launch asked" );
		else
			System.out.println( "Thread launch not asked" );
	}	

	/**
	 * Static function that need to be call before <code>getInstance()</code>
	 * Set the parameters needed for <code>DataSetManager</code> to be instanciated
	 * 
	 * @param inputSize Size of inputs vector
	 * @param outputSize Size of outputs vector
	 * @param maxFitness Maximum fitness that can be set
	 * @param acceptedError Percent error of maxFitness that the DataSet will accept
	 */
	public static void setParameters( int inputSize, int outputSize, double maxFitness, double acceptedError ){
		setInputSize(inputSize);
		setOutputSize(outputSize);
		setMaxFitness(maxFitness);
		setAcceptedError(acceptedError);
	}
	
	public static ManagedDataSet getInstance() throws NeurophException{
		if( me == null ){
			me = new ManagedDataSet();
		}
		
		return me;
	}

	/**
	 * Launch the deamon thread that sort incoming rows
	 * 
	 * @return true if running, else may <code>acceptedError</code> and/or <code>maxFitness</code> not set
	 */
	public boolean launchSorter(){
		boolean isRunning = false;
		if( tSorter == null ){
			if( acceptedError != null && maxFitness != null){
				DataSetFitnessSorter dsfs = new DataSetFitnessSorter( this );
				
				tSorter = new Thread(dsfs);
				tSorter.setDaemon(true);
				
				tSorter.start();
				
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {}
				
				isRunning = true;
			}
		}else
			isRunning = true;
		
		return isRunning;
	}
	
	public LinkedList<EvalDataSetRow> getReevalItems() {
		return reevalItems;
	}

	protected void setReevalItems(LinkedList<EvalDataSetRow> reevalItems) {
		ManagedDataSet.reevalItems = reevalItems;
		
	}

	/**
	 * Return an item that need to be reevaluated
	 * ( remove it from the stack )
	 * 
	 * @return EvalDataSetRow DataSet that contains the inputs to reevaluate
	 */
	public EvalDataSetRow popReevalItem(){
		EvalDataSetRow item = null;
		synchronized (reevalItems) {
			item = reevalItems.pop();
			
		}
		return item;
	}

	/**
	 * Add a row to be reevaluate and send a notifyAll
	 * 
	 * @param edsr Row to be reevaluate
	 */
	protected void addReevalItem( EvalDataSetRow edsr ){
		synchronized (reevalItems) {
			reevalItems.addLast( edsr );
			reevalItems.notifyAll();
		}
	}

	/**
	 * Return an item that need to be juged
	 * ( remove it from the stack )
	 * 
	 * @return EvalDataSetRow DataSet that contains the inputs to juge
	 */
	public EvalDataSetRow popWaitingItem(){
		EvalDataSetRow item = null;
		//synchronized (this) {
			item = waitingLine.pop();
			
		//}
		return item;
	}

	/**
	 * Add a row to be juged and send a notifyAll
	 * 
	 * @param edsr Row to be juged
	 */
	protected void addWaitingItem( EvalDataSetRow edsr ){
		synchronized (waitingLine) {
			waitingLine.addLast( edsr );
			waitingLine.notifyAll();
		}
	}
	
	/**
	 * Will accept a row as convinient fitness
	 * 
	 * @param edsr
	 */
	protected void acceptRow( EvalDataSetRow edsr ){
		super.addRow(edsr.toFitDataSetRow());
	}
	
	
	

	/* (non-Javadoc)
	 * @see dataManager.FitDataSet#addRow(dataManager.FitDataSetRow)
	 */
	@Override
	public void addRow(FitDataSetRow row) throws VectorSizeMismatchException, NeurophException {
    	if( row.isFited() == false )
    		throw new NeurophException("None Fited data aren't allowed");
    	
    	addWaitingItem( new EvalDataSetRow( row ) );
	}



	/* (non-Javadoc)
	 * @see dataManager.FitDataSet#addRow(double[], double[], double)
	 */
	@Override
	public void addRow(double[] input, double[] output, double fitness)throws VectorSizeMismatchException, NeurophException {
    	addWaitingItem( new EvalDataSetRow( input, output, fitness ) );
	}



	/* (non-Javadoc)
	 * @see org.neuroph.core.data.DataSet#addRow(org.neuroph.core.data.DataSetRow)
	 */
	@Override
	public void addRow(DataSetRow row) throws VectorSizeMismatchException, NeurophException {
    	if( !(row instanceof FitDataSetRow) )
    		throw new NeurophException("None Fitness data aren't allowed");

    	addWaitingItem( new EvalDataSetRow( (FitDataSetRow)row ) );
	}



	/* (non-Javadoc)
	 * @see org.neuroph.core.data.DataSet#addRow(double[])
	 */
	@Override
	public void addRow(double[] input) throws NeurophException {
    	throw new NeurophException("None Fitness data aren't allowed");
	}



	/* (non-Javadoc)
	 * @see org.neuroph.core.data.DataSet#addRow(double[], double[])
	 */
	@Override
	public void addRow(double[] input, double[] output) throws NeurophException {
    	throw new NeurophException("None Fitness data aren't allowed");
	}

	/**
	 * @return the maxFitness
	 */
	public static Double getMaxFitness() {
		return maxFitness;
	}

	/**
	 * @param maxFitness the maxFitness to set
	 */
	protected static void setMaxFitness(double maxFitness) {
		ManagedDataSet.maxFitness = maxFitness;
	}

	/**
	 * @return the acceptedError
	 */
	public static Double getAcceptedError() {
		return acceptedError;
	}

	/**
	 * @param acceptedError the acceptedError to set
	 */
	protected static void setAcceptedError(double acceptedError) {
		ManagedDataSet.acceptedError = acceptedError;
	}

	/**
	 * @return the inputSize
	 */
	public static Integer getInputNb() {
		return inputSize;
	}

	/**
	 * @param inputSize the inputSize to set
	 */
	protected static void setInputSize(Integer inputSize) {
		ManagedDataSet.inputSize = inputSize;
	}

	/**
	 * @return the outputSize
	 */
	public static Integer getOutputNb() {
		return outputSize;
	}

	/**
	 * @param outputSize the outputSize to set
	 */
	protected static void setOutputSize(Integer outputSize) {
		ManagedDataSet.outputSize = outputSize;
	}

	/**
	 * @param acceptedError the acceptedError to set
	 */
	protected static void setAcceptedError(Double acceptedError) {
		ManagedDataSet.acceptedError = acceptedError;
	}

	/**
	 * @return the waitingLine
	 */
	public LinkedList<EvalDataSetRow> getWaitingLine() {
		return waitingLine;
	}

	/**
	 * @param waitingLine the waitingLine to set
	 */
	protected void setWaitingLine(LinkedList<EvalDataSetRow> waitingLine) {
		ManagedDataSet.waitingLine = waitingLine;
	}

}
