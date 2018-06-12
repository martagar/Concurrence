import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import java.util.concurrent.*;

public class Mandelbrot implements Runnable
{
	private static final int MAX_ITER = 100000;
	private static final double ZOOM = 150;
	private static Graficos frame_grafico;

	int inicio;
	int end;

	private double zx, zy, cX, cY, tmp;

	public Mandelbrot(int ini, int fin)
	{
		inicio = ini;
		end = fin;
	}

	public void run()
	{
		for (int y = inicio; y < end; y++)
		{
			for (int x = 0; x < frame_grafico.getWidth(); x++)
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
		 	    	frame_grafico.Imagen.setRGB(x, y, iter | (iter << 8));
	 		}
		}
	}

	public static void main(String[] args) throws InterruptedException
	{
		frame_grafico = new Graficos();

		ExecutorService pool = Executors.newFixedThreadPool(4);
		for(int i = 0; i < 4; ++i)
			pool.execute(new Mandelbrot((i*frame_grafico.getHeight())/4, ((i+1)*frame_grafico.getHeight())/4));

		pool.shutdown();
		pool.awaitTermination(1,TimeUnit.DAYS);

		frame_grafico.setVisible(true);
	}
}

class Graficos extends JFrame
{
	BufferedImage Imagen;

	Graficos()
	{
		super("Conjunto de Mandelbrot");
		setBounds(100, 100, 800, 600);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		Imagen = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
	}

	public void paint(Graphics g)
	{
		g.drawImage(Imagen, 0, 0, this);
	}
}
