import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import java.util.concurrent.*;

public class Mandelbrot implements Runnable {
	private static final int MAX_ITER = 100000;
	private static final double ZOOM = 150;
	private static Graph frame_graphic;

	int initial;
	int end;

	private double zx, zy, cX, cY, tmp;

	public Mandelbrot(int ini, int fin)	{
		initial = ini;
		end = fin;
	}

	public void run()	{
		for (int y = initial; y < end; y++)
			for (int x = 0; x < frame_graphic.getWidth(); x++)
			{
		 	    zx = zy = 0;
		 	    cX = (x - 400) / ZOOM;
		 	    cY = (y - 300) / ZOOM;
		 	    int iter = MAX_ITER;
		 	    while (zx * zx + zy * zy < 4 && iter > 0)
		 	    {
	 	     	    tmp = zx * zx - zy * zy + cX;
	 	     	    zy = 2.0 * zx * zy + cY;
	 	     	    zx = tmp;
	 	     	    iter--;
		 	    }
	 	    	frame_graphic.Image.setRGB(x, y, iter | (iter << 8));
	 		}
	}

	public static void main(String[] args) throws InterruptedException {
		frame_graphic = new Graph();

		ExecutorService pool = Executors.newFixedThreadPool(4);
		for(int i = 0; i < 4; ++i)
			pool.execute(new Mandelbrot((i*frame_graphic.getHeight())/4, ((i+1)*frame_graphic.getHeight())/4));

		pool.shutdown();
		pool.awaitTermination(1,TimeUnit.DAYS);

		frame_graphic.setVisible(true);
	}
}

class Graph extends JFrame {
	BufferedImage Image;

	Graph() {
		super("Mandelbrot Set");
		setBounds(100, 100, 800, 600);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		Image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
	}

	public void paint(Graphics g)	{
		g.drawImage(Image, 0, 0, this);
	}
}
