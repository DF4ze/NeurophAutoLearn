package networkManager;

import networkManager.evaluate.IEvaluateFunction;
import dataManager.EvalDataSetRow;
import dataManager.ManagedDataSet;

public class ReevaluateTeacherDeleguate implements Runnable {

	private ManagedDataSet mds;
	private IEvaluateFunction evalFunction;
	
	/**
	 * @param mds
	 * @param evalFunction
	 */
	public ReevaluateTeacherDeleguate( ManagedDataSet mds, IEvaluateFunction evalFunction) {
		this.setMds(mds);
		this.setEvalFunction(evalFunction);
	}



	@Override
	public void run() {
		//System.out.println("Rééval Thread started");
			while( true ){
				synchronized (mds.getReevalItems()) {
					try {
						mds.getReevalItems().wait(1000l);
					} catch (InterruptedException e) {}
				}
				//System.out.println("Rééval Thread wake up");
				while( mds.getReevalItems().size() != 0 ){
					//System.out.println("Rééval...");
					EvalDataSetRow evalItem = mds.popReevalItem();
					if( evalItem != null){
						//FitDataSet lastDatas = evalItem.getPrevEval();
						double [] outputs = new double [evalItem.getDesiredOutput().length];
						for( int i=0; i < evalItem.getDesiredOutput().length; i++ ){
							outputs[i] = Math.random();
						}
						
						evalItem.setDesiredOutput(outputs);
						
						Double fitness = getEvalFunction().evaluate(evalItem);
						
						if( fitness == null )
							continue;
						
						evalItem.setFitness(fitness);
						
						mds.addRow(evalItem);
						
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {}
					}
				}
			
		
		}

	}



	/**
	 * @return the ManagedDataSet
	 */
	public ManagedDataSet getMds() {
		return mds;
	}



	/**
	 * @param mds the ManagedDataSet to set
	 */
	public void setMds(ManagedDataSet mds) {
		this.mds = mds;
	}



	/**
	 * @return the evalFunction
	 */
	public IEvaluateFunction getEvalFunction() {
		return evalFunction;
	}



	/**
	 * @param evalFunction the evalFunction to set
	 */
	public void setEvalFunction(IEvaluateFunction evalFunction) {
		this.evalFunction = evalFunction;
	}

}
