package temp;

public class Temp implements Comparable<Temp>{
	
	private static int count = 0;
	private int num;
	
	/**
	 * Create a new Temp from an infinite set of Temps.
	 */
	public Temp() {
		num = count++;
	}
	
	@Override
	public String toString() {
		return "t" + num;
	}

	@Override
    public int compareTo(Temp t) {
            return this.num - t.num;
    }
    
    @Override
    public boolean equals(Object t) {
            return super.equals(t) || ((t instanceof Temp) && (this.num == ((Temp)t).num));
    }
}
