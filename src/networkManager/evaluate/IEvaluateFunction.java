/**
 * 
 */
package networkManager.evaluate;

import dataManager.EvalDataSetRow;

/**
 * @author Clement ORTIZ
 *
 */
public interface IEvaluateFunction {

	public double evaluate( EvalDataSetRow evalItem );
	public String toString();
}
