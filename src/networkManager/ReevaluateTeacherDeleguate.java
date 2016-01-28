package networkManager;

import networkManager.evaluate.IEvaluateFunction;
import dataManager.EvalDataSetRow;
import dataManager.FitDataSet;
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
		
			while( true ){
				synchronized (mds.getReevalItems()) {
					try {
						mds.getReevalItems().wait(100000l);
					} catch (InterruptedException e) {}
				}
				while( mds.getReevalItems().size() != 0 ){
					//System.out.println("Rééval...");
					EvalDataSetRow evalItem = mds.popReevalItem();
					
					//FitDataSet lastDatas = evalItem.getPrevEval();
					double [] outputs = new double [evalItem.getDesiredOutput().length];
					for( int i=0; i < evalItem.getDesiredOutput().length; i++ ){
						outputs[i] = Math.random();
					}
					
					evalItem.setDesiredOutput(outputs);
					
					double fitness = getEvalFunction().evaluate(evalItem);
					
					evalItem.setFitness(fitness);
					
					mds.addRow(evalItem);
					
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {}
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
