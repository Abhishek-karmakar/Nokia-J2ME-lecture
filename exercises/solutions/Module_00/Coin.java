// EI testattu. Sisältänee virheitä.

public class Coin
{
	private boolean coinSide;
	
	public boolean getCoinSide() {
		return this.coinSide;
	}
	
	public void setCoinSide(boolean coinSide) {
		this.coinSide = coinSide;
	}

	public Coin () {
		throwCoin();
	}
	
	public void throwCoin() {
		
		int result = (int)Math.round(Math.random());
		
		if ( result == 0 ) {
			setCoinSide(false);
		} else {
			setCoinSide(true);
		}
	}
	
	public static void main(String[] args) {
		MyThread t = new MyThread();
		t.start();
	}
}

class MyThread extends Thread {
    public void run() {
        Coin c = new Coin();
        int tails=0;
        int heads=0;
        for(int i=0; i<10; i++) {
            System.out.print("Throwing coin: ");
            c.throwCoin();
            boolean which = c.getCoinSide();
            if(which) {
                tails++;
                System.out.println("Tails");
            } else {
                heads++;
                System.out.println("Heads");
            }
            
            try {
               sleep(1000);
            }catch(Exception e) {
                e.printStackTrace();
            }
            
        }
        
        System.out.println("Heads: " + heads);
        System.out.println("Tails: " + tails);
    }
}
