/**
 * Belusov-Zhabotinsky's chemical reaction simulation
 * @author Marta García Pérez
 */

import java.lang.Math;
import java.util.Random;
import javax.swing.*;
import java.awt.*;

public class belZab {
	float[][][] a;
	float[][][] b;
	float[][][] c;

	int p = 0;
	int q = 1;
	int size;
	final float alfa = 1.2f;
	final float beta = 1.0f;
	final float gamma = 1.0f;

	public void setup()	{
		Random r = new Random();
		a = new float[size][size][2];
		b = new float[size][size][2];
		c = new float[size][size][2];

		for(int x = 0; x < size; ++x)
			for(int y = 0; y < size; ++y)	{
				a[x][y][p] = r.nextFloat();
				b[x][y][p] = r.nextFloat();
				c[x][y][p] = r.nextFloat();
			}
	}

	public void compute()	{
		for(int x = 0; x < size; ++x)
			for(int y = 0; y < size; ++y)	{
				float c_a = 0;
				float c_b = 0;
				float c_c = 0;
				for(int i = x-1; i <= x+1; ++i)
					for(int j = y-1; j <= y+1; ++j) {
						c_a += a[(i+size)%size][(j+size)%size][p];
						c_b += b[(i+size)%size][(j+size)%size][p];
						c_c += c[(i+size)%size][(j+size)%size][p];
					}
				c_a /= 9.0;
				c_b /= 9.0;
				c_c /= 9.0;
				a[x][y][q] = Math.max(0,Math.min(1,c_a+c_a*(alfa*c_b-gamma*c_c)));
				b[x][y][q] = Math.max(0,Math.min(1,c_b+c_b*(beta*c_c-alfa*c_a)));
				c[x][y][q] = Math.max(0,Math.min(1,c_c+c_c*(gamma*c_a-beta*c_b)));
			}

		if(p == 0) {
			p = 1;
			q = 0;
		}	else {
			p = 0;
			q = 1;
		}
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setLayout(new BorderLayout());
		frame.setSize(640,480);
		frame.setResizable(true);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.add(new belZabGraphic());

		frame.setVisible(true);
	}
}
