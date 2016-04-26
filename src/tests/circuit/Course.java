package tests.circuit;

import java.util.ArrayList;

public class Course {

	public Course() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
//		Voiture v1 = new Voiture(1);
////		v1.loadDS("FullDataSet.dst");
////		v1.loadNN("testedNN.nnet"/*"CorridorDriver.nnet"*/);
//		Voiture v2 = new Voiture(2);
//		Voiture v3 = new Voiture(3);
//		Voiture v4 = new Voiture(4);
//		Voiture v5 = new Voiture(5);
		
		ArrayList<Voiture> concurrents = new ArrayList<Voiture>();
		for ( int i=1; i<20 ; i++ )
			concurrents.add(new Voiture( i ));
//		concurrents.add(v2);
//		concurrents.add(v3);
//		concurrents.add(v4);
//		concurrents.add(v5);
		
		Piste p = new Piste(140, 50, 20);
		
		Commissaire comi = new Commissaire(p, concurrents, 2000);
		
		comi.demarrerCourse();

	}

}
