package tests.circuit;

import java.util.Timer;
import java.util.TimerTask;

public class Piste {
	private int largeurTotale;
	private int largeurPiste;
	private int vitesseVirage;
	protected double milieu;
	/**
	 * @return the milieu
	 */
	public double getMilieu() {
		return milieu;
	}


	protected long gauche;
	protected long droite;
	private GeneratePiste gp;
	

	/**
	 * @param largeurTotale
	 * @param largeurPiste
	 * @param vitesseVirage
	 */
	public Piste(int largeurTotale, int largeurPiste, int vitesseVirage) {
		setLargeurTotale( largeurTotale );
		setLargeurPiste( largeurPiste );
		setVitesseVirage( vitesseVirage );
		
		setPositionBordInit();
		
		gp = new GeneratePiste(this);
	}

	public Piste(int largeurTotale, int largeurPiste, int vitesseVirage, boolean setTimer) {
		setLargeurTotale( largeurTotale );
		setLargeurPiste( largeurPiste );
		setVitesseVirage( vitesseVirage );
		
		setPositionBordInit();
		
		gp = new GeneratePiste(this);
		if( setTimer ){
			Timer timer = new Timer();
			timer.scheduleAtFixedRate(gp, 0, 250);
		}
	}

	protected void setPositionBordInit(){
		milieu = getLargeurTotale()/2;
		gauche = Math.round(milieu - ( getLargeurPiste()/2 ));
		droite = Math.round(milieu + ( getLargeurPiste()/2 ));
	}
	
	protected void setPositionBord( double milieu ){
		this.milieu = milieu; 
		gauche = Math.round(milieu - ( getLargeurPiste()/2 ));
		droite = Math.round(milieu + ( getLargeurPiste()/2 ));
	}
	
	public String getPisteDessin(){
		String sPiste = "";
		for( int i = 0; i <= getLargeurTotale(); i++ ){
			if( i == gauche || i == droite)
				sPiste += "|";
			else if( i < gauche || i > droite)
				sPiste += ".";
			else 
				sPiste += " ";
		}
		
		return sPiste;
	}
	
	public void genererProchainePortion(){
		gp.run();
	}
	
	public void launchTimer(){
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(gp, 0, 250);

	}
	/**
	 * @return the largeurTotale
	 */
	public int getLargeurTotale() {
		return largeurTotale;
	}

	/**
	 * @param largeurTotale the largeurTotale to set
	 */
	public void setLargeurTotale(int largeurTotale) {
		this.largeurTotale = largeurTotale;
	}

	/**
	 * @return the largeurPiste
	 */
	public int getLargeurPiste() {
		return largeurPiste;
	}

	/**
	 * @param largeurPiste the largeurPiste to set
	 */
	public void setLargeurPiste(int largeurPiste) {
		this.largeurPiste = largeurPiste;
	}

	/**
	 * @return the vitesseVirage
	 */
	public int getVitesseVirage() {
		return vitesseVirage;
	}

	/**
	 * @param vitesseVirage the vitesseVirage to set
	 */
	public void setVitesseVirage(int vitesseVirage) {
		this.vitesseVirage = vitesseVirage;
	}


	class GeneratePiste extends TimerTask{

		private Piste piste;
		private boolean show = false;
		
		public GeneratePiste( Piste piste ){
			this.piste = piste;
		}
		public GeneratePiste( Piste piste, boolean show ){
			this.piste = piste;
			this.show = show;
		}
		
		@Override
		public void run() {
			double memoMilieu = 0;
			do{
				long deviation = Math.round( piste.getVitesseVirage() * Math.random() );
				deviation = deviation * (Math.round( Math.random() ) == 1?1:-1);
				
				if( memoMilieu == 0 )
					memoMilieu = piste.milieu;
				
				double nvMilieu = memoMilieu + deviation;
				piste.setPositionBord(nvMilieu);
			}while( piste.gauche < 0 || piste.droite > piste.getLargeurTotale() );
			
			if( show ){
				String sPiste = piste.getPisteDessin();
				System.out.println(sPiste);
			}
		}
		
	}
}
