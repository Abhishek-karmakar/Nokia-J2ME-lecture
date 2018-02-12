import java.awt.Color;

public class Pixel extends Point
{
	private Color color;

	public Color getColor () {
		return color;
	}
	
	public void setColor (Color color) {
		this.color = color;
	}
	
	public Pixel(int x, int y, Color color) {
        super(x,y);
		this.color = color;
	}
	
	public void setPixel(int x, int y, Color color) {
		setPoint(x, y);
		setColor(color);
	}
	
	public String toString() {
	    String temp = super.toString();
	    temp += " color = " + color.toString();
	    return temp;
	}
}