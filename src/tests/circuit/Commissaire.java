package tests.circuit;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import networkManager.nnetwork.NetworkRunner;
import dataManager.ManagedDataSet;
import debug.debug;

public class Commissaire {

	private Piste piste = new Piste(140, 50, 5);
	private ArrayList<Voiture> concurrents = new ArrayList<Voiture>();
	private long vitesse = 500;
	private long compteur = 0;
	
	public Commissaire() {

	}
	
	public Commissaire( Piste piste ) {
		setPiste( piste );
		
	}
	
	public Commissaire( Piste piste, ArrayList<Voiture> concurrents ) {
		setPiste( piste );
		setConcurrents( concurrents );
	}
	
	public Commissaire( Piste piste, ArrayList<Voiture> concurrents, long vitesse ) {
		setPiste( piste );
		setConcurrents( concurrents );
		setVitesse(vitesse);
	}
	
	/**
	 * @return the piste
	 */
	public Piste getPiste() {
		return piste;
	}
	/**
	 * @param piste the piste to set
	 */
	public void setPiste(Piste piste) {
		this.piste = piste;
		
		if( concurrents != null ){
			for( Voiture v : concurrents ){
				v.setPosition( (int)(Math.round( piste.getMilieu())) );
			}
		}
	}
	/**
	 * @return the concurrents
	 */
	public ArrayList<Voiture> getConcurrents() {
		return concurrents;
	}
	/**
	 * @param concurrents the concurrents to set
	 */
	public void setConcurrents(ArrayList<Voiture> concurrents) {
		this.concurrents = concurrents;
		
		if( piste != null ){
			for( Voiture v : concurrents ){
				v.setPosition( (int)(Math.round( piste.getMilieu())) );
				
				if( debug.isDebug() ){
					ManagedDataSet mds = v.getDataSet();
					NetworkRunner nwr = v.getNetWorkRunner();
					
					System.out.println( "Voiture : "+v.getNumero() );
					System.out.println( "- nb données : "+mds.size() );
					System.out.println( "- nb layouts : "+nwr.getNeuralNet().getLayersCount() );
					
				}
			}
		}
	}
	
	public void demarrerCourse(){
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new RunTime(), 5000, vitesse);
		
	}

	
	/**
	 * @return the vitesse
	 */
	public long getVitesse() {
		return vitesse;
	}

	/**
	 * @param vitesse the vitesse to set
	 */
	public void setVitesse(long vitesse) {
		this.vitesse = vitesse;
	}


	class RunTime extends TimerTask{

		@Override
		public void run() {
			piste.genererProchainePortion();
			String nvlPortion = piste.getPisteDessin();
			
			ArrayList<Voiture> crash = new ArrayList<>();
			for( Voiture v : concurrents ){
//				if( debug.isDebug() ){
//					ManagedDataSet mds = v.getDataSet();
//					NetworkRunner nwr = v.getNetWorkRunner();
//					
//					System.out.println( "Voiture : "+v.getNumero() );
//					System.out.println( "- nb données : "+mds.size() );
//					System.out.println( "- nb layouts : "+nwr.getNeuralNet().getLayersCount() );
//					
//				}
					
				myDouble d1 = new myDouble(new Double (v.getPosition() - piste.gauche ));
				myDouble d2 = new myDouble(new Double (piste.droite - v.getPosition()));
				
				normalise( d1, d2 );
				
				if( d1.d < 0 ) d1.d = 0;
				if( d2.d < 0 ) d2.d = 0;
				if( d1.d > 1 ) d1.d = 1;
				if( d2.d > 1 ) d2.d = 1;
				
				double espaceG = d1.d;
				double espaceD = d2.d;
				
				Double dDeriv = v.getDirection(espaceG, espaceD);
				if( dDeriv == null )
					continue;
				
				dDeriv = dDeriv -0.5;
				dDeriv = (dDeriv*2); // calle la valeur entre -1  et 1
				int derive = (int) (dDeriv*(piste.getLargeurPiste()-(piste.getLargeurPiste()/5)));
				
				v.setPosition(v.getPosition()+derive);
				
				if( v.getPosition() < piste.gauche || v.getPosition() > piste.droite ){
					crash.add(v);
					continue;
				}
				
				nvlPortion = dessineVoitureCourse(v, nvlPortion);
			}

			for( Voiture voit : crash ){
				nvlPortion = dessineVoitureCrash( voit, nvlPortion );
				voit.saveDS();
				concurrents.remove(voit);
			}
			compteur ++;
			System.out.println( compteur+" "+nvlPortion );
			
			if( concurrents.size() == 0 ){
				this.cancel();
				System.out.println( "!!!!! Game Over !!!!" );
				System.exit(0);
			}
		}
		
		private void normalise( myDouble gauche, myDouble droite ){
			if( piste.getLargeurPiste() != 0 ){
				gauche.d = gauche.d/piste.getLargeurPiste();
				droite.d = droite.d/piste.getLargeurPiste();
			}
		}
		
		private String dessineVoitureCourse( Voiture v, String nvlPortion ){
			
			char[] str = nvlPortion.toCharArray();
			str[ v.getPosition() ] = (char) ( v.getNumero() == null?'X':'0'+v.getNumero() );
			
			String nvlPorVoit = new String(str);
			
			return nvlPorVoit;
		}
		private String dessineVoitureCrash( Voiture v, String nvlPortion ){
			
			if( v.getPosition() < 0 )
				v.setPosition(0);
			if( v.getPosition() > nvlPortion.length()-1 )
				v.setPosition(nvlPortion.length()-1);
			
			char[] str = nvlPortion.toCharArray();
			str[ v.getPosition() ] = '@';
			
			String nvlPorVoit = new String(str);
			
			return nvlPorVoit;
		}
		
	}
	
	class myDouble{
		public double d;
		
		public myDouble( double d ){
			this.d = d;
		}
	}
}
