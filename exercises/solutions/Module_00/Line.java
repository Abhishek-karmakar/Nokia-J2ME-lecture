
public class Line
{
	private Point begin;
	private Point end;
	

	public Line ()
	{
		this.begin = new Point();
		this.end = new Point();
	}
	
	public void setLine (int x1, int y1, int x2, int y2)
	{
		this.begin.setPoint(x1, y1);
		this.end.setPoint(x2, y2);
	}

	public String toString ()
	{
		String begin = "Begin: " + this.begin;
		String end = " End: " + this.end;
		return begin + end;
	}
}