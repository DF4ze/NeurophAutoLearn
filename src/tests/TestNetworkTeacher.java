package tests;

import java.util.Scanner;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

import dataManager.EvalDataSetRow;
import dataManager.ManagedDataSet;
import networkManager.NetworkTeacher;
import networkManager.XorEvaluation;

public class TestNetworkTeacher {

	public TestNetworkTeacher() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		XorEvaluation evaluateFunction = new XorEvaluation();
		ManagedDataSet.setParameters(2, 1, 10, 10);
		ManagedDataSet mds = ManagedDataSet.getInstance();
		NetworkTeacher teacher = new NetworkTeacher(mds, evaluateFunction);

		Scanner sc = new Scanner(System.in);
		
		String key1;
		do{
			System.out.println("\n\n1er input?");
			key1 = sc.next();
			if( !key1.equals("q") )	{
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
						teacher.addInputs(evalRow);
						
						System.out.println("Resultats => output "+evalRow.getDesiredOutput()[0]
								+" | Fitness "+evalRow.getFitness());
						
					}catch( ParseException e ){
						System.out.println("il ne s'agit pas d'un double");
					}
				}
			}
			
		}while( !key1.equals("q") );
		
		System.out.println("Fin ...");
	}

}
