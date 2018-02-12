import java.awt.Color;

public class AppRunner
{
	public static void main(String[] args)
	{
		Point p1 = new Point();
		p1.setPoint(5,3);
		Point p2 = new Point(5,0);
		
		if ( p1.equals(p2) )
		{
			System.out.println("Point p1 and point p2 are the SAME");
		}
		else
		{
			System.out.println("Point p1 and point p2 are DIFFERENT");
		}
		System.out.println("p1 coordinates = " + p1 + " p2 coordinates = " + p2);
		
		// Testing Line-class
		Line line1 = new Line();
		line1.setLine(4,2,7,6);
		System.out.println("Line 1 coordinates = " + line1);
		
		// Testing Pixel-class
		Color red = new Color(255,0,0);
		Pixel px1 = new Pixel(3, 4, red);
		System.out.println(px1);
	}
}