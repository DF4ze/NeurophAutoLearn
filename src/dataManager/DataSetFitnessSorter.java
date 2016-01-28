package dataManager;

public class DataSetFitnessSorter implements Runnable {

	private ManagedDataSet dsm;
	
	public DataSetFitnessSorter( ManagedDataSet dsm ) {
		this.dsm = dsm;
	}

	@Override
	public void run() {
		System.out.println("Thread effectly running");
		synchronized (dsm.getWaitingLine()) {
			while( true ){
			
				try {
					dsm.getWaitingLine().wait(100000l);
				} catch (InterruptedException e) {}
			
				while( dsm.getWaitingLine().size() != 0 ){
					EvalDataSetRow evalItem = dsm.popWaitingItem();
					if( dsm.isFitnessInAcceptedRange( evalItem.getFitness() ) )
						dsm.acceptRow( evalItem );
					else
						dsm.addReevalItem(evalItem);
				}
			}
		
		}

	}
	
	

}
