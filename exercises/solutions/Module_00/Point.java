public class Point
{
	private int x;
	private int y;
	
	public int getX () {
		return this.x;
	}
	
	public void setX (int x) {
		this.x = x;
	}
	
	public int getY () {
		return this.y;
	}
	
	public void setY (int y) {
		this.y = y;
	}
	
	public void setPoint(int x, int y) {
		setX(x);
		setY(y);
	}
	
	public Point () {
		setPoint(0,0);
	}
	
	public Point (int x, int y) {
		setPoint(x,y);
	}
	
	public boolean equals(Object a) {
		if ( a instanceof Point ) {
			Point p = (Point) a;
			
			if ( this.x == p.x && this.y == p.y )
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	
	public String toString () {
		return "(" + getX() + "," + getY() + ")";
	}
}