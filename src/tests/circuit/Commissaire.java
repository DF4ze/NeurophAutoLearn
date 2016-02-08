package tests.circuit;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.util.data.norm.MaxMinNormalizer;
import org.neuroph.util.data.norm.Normalizer;
import org.neuroph.util.data.norm.RangeNormalizer;

public class Commissaire {

	private Piste piste = new Piste(140, 50, 5);
	private ArrayList<Voiture> concurrents = new ArrayList<Voiture>();
	private long vitesse = 500;
	
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
			}
		}
	}
	
	public void demarrerCourse(){
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new RunTime(), 0, vitesse);
		
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
				
					
				myDouble d1 = new myDouble(new Double (v.getPosition() - piste.gauche ));
				myDouble d2 = new myDouble(new Double (piste.droite - v.getPosition()));
				normalise( d1, d2 );
				
				double espaceG = d1.d;
				double espaceD = d2.d;
				
				Double dDeriv = v.getDirection(espaceG, espaceD);
				if( dDeriv == null )
					continue;
				
				dDeriv = (dDeriv*2) - 1; // calle la valeur entre -1  et 1
				int derive = (int) (dDeriv*piste.getLargeurPiste());
				
				v.setPosition(v.getPosition()+derive);
				
				if( v.getPosition() < piste.gauche || v.getPosition() > piste.droite ){
					crash.add(v);
					continue;
				}
				
				nvlPortion = dessineVoitureCourse(v, nvlPortion);
			}

			for( Voiture voit : crash ){
				nvlPortion = dessineVoitureCrash( voit, nvlPortion );
				concurrents.remove(voit);
			}
			
			System.out.println( nvlPortion );
			
			if( concurrents.size() == 0 ){
				this.cancel();
				System.out.println( "!!!!! Game Over !!!!" );
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
