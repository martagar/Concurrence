/**
 * Belusov-Zhabotinsky's chemical reaction simulation
 * @author Marta García Pérez
 */

import java.lang.Math;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class belZabParallel implements Runnable {
	static float[][][] a;
	static float[][][] b;
	static float[][][] c;

	int p = 0;
	int q = 1;
	static int size;
	static float alfa;
	static float beta;
	static float gamma;
	static CyclicBarrier barrera = new CyclicBarrier(4,new stop());
	int initial;
	int fin;
	static AtomicInteger k = new AtomicInteger();
	static boolean u;

	public static class stop implements Runnable	{
		public void run()	{
 			belZabGraphic.update(k.get());
 			if(k.equals(0))		k.set(1);
 			else		k.set(0);
 		}
	}

	belZabParallel(int ini, int end) {initial = ini; fin = end;}

	public static void setup() {
		Random r = new Random();
		a = new float[size][size][2];
		b = new float[size][size][2];
		c = new float[size][size][2];

		for(int x = 0; x < size; ++x)
			for(int y = 0; y < size; ++y)	{
				a[x][y][0] = r.nextFloat();
				b[x][y][0] = r.nextFloat();
				c[x][y][0] = r.nextFloat();
			}
		k.set(0);
	}

	public void compute() {
		for(int x = initial; x < fin; ++x)
			for(int y = 0; y < size; ++y)	{
				float c_a = 0;
				float c_b = 0;
				float c_c = 0;
				for(int i = x-1; i <= x+1; ++i)
					for(int j = y-1; j <= y+1; ++j)	{
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

	public void run()	{
		while(u) {
			compute();
			try{
				barrera.await();
			}catch(InterruptedException e){}
			catch(BrokenBarrierException e){}
		}
	}

	public static void make_automaton()	{
		ExecutorService pool = Executors.newFixedThreadPool(4);

		for(int i = 0; i < 4; ++i)
			pool.execute(new belZabParallel(i*size/4, (i+1)*size/4));

		pool.shutdown();
	}
}
