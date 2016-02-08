package tests;

public class test {

	public test() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		for( int i=0; i<500; i++ )
			System.out.print( i%10 );
		int count = 0;
		System.out.println();
		for( int i=0; i<500; i++ ){
			if( i%10 == 0 )
				count++;
			System.out.print( i%10 == 0?"|":" " );
		}
	}

}
