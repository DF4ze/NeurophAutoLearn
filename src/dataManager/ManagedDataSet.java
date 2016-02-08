/**
 * 
 */
package dataManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.exceptions.NeurophException;
import org.neuroph.core.exceptions.VectorSizeMismatchException;

import debug.debug;

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
	private LinkedList<EvalDataSetRow> reevalItems = new LinkedList<>();
	/**
	 * FIFO for new row to juge by fitness
	 */
	private LinkedList<EvalDataSetRow> waitingLine = new LinkedList<>();
	/**
	 * Singleton 
	 */
	//private static ManagedDataSet me = null;


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
	
	/**
	 * Thread that manage the sort of incoming rows
	 */
	private Thread tSorter;
	/**
	 * For the hasChanged() function, remember the last count of entries
	 */
	private int countEntries = 0;
	
	
	/**
	 * Create an instance of DataSetManger that will validate new rows with fitness 
	 * 
	 * @param inputSize Number of inputs
	 * @param outputSize Number of outputs
	 * @param maxFitness Maximum of fitness
	 * @param acceptedError Percent less thant maxFitness is authorized
	 */
	public ManagedDataSet(int inputSize, int outputSize, double maxFitness, double acceptedError) throws NeurophException{
		super(inputSize, outputSize, true);
		
		setParameters( inputSize, outputSize, maxFitness, acceptedError );
		
		if( this.inputSize == null ||
				this.outputSize == null ||
						this.maxFitness == null ||
								this.acceptedError == null)
			throw new NeurophException("One or more parameters aren't set");
		
		launchSorter();
		
	/*	if( launchSorter() )
			System.out.println( "Thread launch asked" );
		else
			System.out.println( "Thread launch not asked" );
	*/
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
	public void setParameters( int inputSize, int outputSize, double maxFitness, double acceptedError ){
		setInputSize(inputSize);
		setOutputSize(outputSize);
		setMaxFitness(maxFitness);
		setAcceptedError(acceptedError);
	}
	
//	public static ManagedDataSet getInstance() throws NeurophException{
//		if( me == null ){
//			me = new ManagedDataSet();
//		}
//		
//		return me;
//	}

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
				//tSorter.setDaemon(true);
				
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
		this.reevalItems = reevalItems;
		
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
			if( reevalItems.size() != 0 )
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
		// TODO : remove this comments...?
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
		if( !contains( edsr ) )
			super.addRow(edsr.toFitDataSetRow());
		
		if( debug.isDebug() ){
			//System.out.println("Accepted row : "+edsr);
		}
	}
	
	/**
	 * Returns true if this list contains the specified element. More formally, 
	 * returns true if and only if this list contains at least one element e such that (o==null ? e==null : o.equals(e)).
	 * 
	 * @param edsr EvalDataSetRow to evaluate
	 * @return true if inputs finded
	 */
	public boolean contains( EvalDataSetRow edsr ){
		return getRows().contains(edsr);
	}
	

	
	/**
	 * return true if accepted data list has changed
	 * 
	 * @return true if changed
	 */
	public boolean hasChanged(){
		boolean hasChanged = false;
		if( getRows().size() != countEntries ){
			countEntries = getRows().size();
			hasChanged = true;
		}
		return hasChanged;
	}
	
	/**
	 * return true if given fitness is in range of accepted values
	 * 
	 * @param fitness fitness to evaluate
	 * @return true if given fitness is in range of accepted values
	 */
	public boolean isFitnessInAcceptedRange( double fitness ){
		double delta = (getAcceptedError() * getMaxFitness())/100;
		
		boolean accepted = false;
		if( fitness >= ( getMaxFitness() - delta ) )
			accepted = true;
		
		return accepted;
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
	public Double getMaxFitness() {
		return maxFitness;
	}

	/**
	 * @param maxFitness the maxFitness to set
	 */
	protected void setMaxFitness(double maxFitness) {
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
	protected void setAcceptedError(double acceptedError) {
		this.acceptedError = acceptedError;
	}

	/**
	 * @return the inputSize
	 */
	public Integer getInputNb() {
		return inputSize;
	}

	/**
	 * @param inputSize the inputSize to set
	 */
	protected void setInputSize(Integer inputSize) {
		this.inputSize = inputSize;
	}

	/**
	 * @return the outputSize
	 */
	public Integer getOutputNb() {
		return outputSize;
	}

	/**
	 * @param outputSize the outputSize to set
	 */
	protected void setOutputSize(Integer outputSize) {
		this.outputSize = outputSize;
	}

	/**
	 * @param acceptedError the acceptedError to set
	 */
	protected void setAcceptedError(Double acceptedError) {
		this.acceptedError = acceptedError;
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
		this.waitingLine = waitingLine;
	}
	
	public void setRows( List<FitDataSetRow> rows ){
		clear();
		
		for( FitDataSetRow row : rows ){
			addRow(row);
		}
	}
	public void setRows( ManagedDataSet mds ){
		setRows( mds.getFitRows() );
	}

	public List<FitDataSetRow> getFitRows(){
		List<FitDataSetRow> rows = new ArrayList<FitDataSetRow>();
		for( DataSetRow row : getRows()){
			rows.add( (FitDataSetRow)row );
		}
		return rows;
	}
	
	public void loadFromFile( String fileName ){
		setRows( (ManagedDataSet)DataSet.load(fileName));
	}
}
