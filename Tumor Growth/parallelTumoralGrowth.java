/** 
 * Programa que simula un tumor paralelo
 * @author Marta García Pérez
 */

import java.util.Random;
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.*;

public class parallelTumoralGrowth implements Runnable
{
	static CyclicBarrier barrera;
	static int[][] tiempoT;
	static int[][] tiempoT1;
	static int[][] PH;
	static int size;
	static int semilla;
	static float Ps;
	static float Pp;
	static float Pm;
	static int NP;
	int inicial;
	int fin;
	static int n = 0;

	static class parada implements Runnable
	{
		public void run()
		{
			parallelTumorGraphic.update(tiempoT1);
			for(int i = 0; i < tiempoT.length; ++i)
				tiempoT[i] = tiempoT1[i].clone();
		}
	}

	parallelTumoralGrowth(int ini, int end)
	{
		inicial = ini;
		fin = end;
	}

	public static void inicializar()
	{
		tiempoT = new int[size][size];
		tiempoT1 = new int[size][size];
		PH = new int[size][size];
		parada par = new parada();
		barrera = new CyclicBarrier(4, par);
		Random r = new Random();

		for(int i = 0; i < size; ++i)
			for(int j = 0; j < size; ++j)
			{
				PH[i][j] = 0;
				tiempoT[i][j] = tiempoT1[i][j] = 0;
			}

		for(int i = (size/2)-(semilla/2); i < (size/2)+(semilla/2); ++i)
		{
			for(int j = (size/2)-(semilla/2); j < (size/2)+(semilla/2); ++j)
			{
				if(semilla > 0)
				{
					tiempoT[i][j] = tiempoT1[i][j] = r.nextInt(2);
					if(tiempoT[i][j] == 1)
						semilla--;
				}
			}
		}
	}

	public void nextGen()
	{
		Random r = new Random();
		for(int i = inicial; i < fin; ++i)
			if(i != 0 && i != tiempoT.length-1)
				for(int j = 1; j < tiempoT.length-1; ++j)
				{
					if(tiempoT[i][j] == 1)
						if (r.nextDouble() < Ps)
						{
							if (r.nextDouble() < Pp)
							{
								PH[i][j]++;
								if(PH[i][j] >= NP)
								{
									if(!modifica(i,j))
										migra(i,j);
								}
								else
									migra(i,j);
							}
							else
								migra(i,j);
						}
						else
						{
							synchronized(barrera)
								{tiempoT1[i][j] = 0;}
						}
				}
	}

	public static void migra(int i, int j)
	{
		Random r = new Random();
		if(r.nextDouble() < Pm)
			if(modifica(i,j))
			{
				synchronized(barrera)
					{tiempoT1[i][j] = 0;}
			}
	}

	public static boolean modifica(int i, int j)
	{
		Random r = new Random();
		boolean cambio = true;
		PH[i][j] = 0;

		double P1 = (1-tiempoT[i-1][j])/(double)(4-(tiempoT[i+1][j] + tiempoT[i-1][j] + tiempoT[i][j+1] + tiempoT[i][j-1]));
		double P2 = (1-tiempoT[i+1][j])/(double)(4-(tiempoT[i+1][j] + tiempoT[i-1][j] + tiempoT[i][j+1] + tiempoT[i][j-1]));
		double P3 = (1-tiempoT[i][j-1])/(double)(4-(tiempoT[i+1][j] + tiempoT[i-1][j] + tiempoT[i][j+1] + tiempoT[i][j-1]));
		double P4 = (1-tiempoT[i][j+1])/(double)(4-(tiempoT[i+1][j] + tiempoT[i-1][j] + tiempoT[i][j+1] + tiempoT[i][j-1]));
		
		double rr = r.nextDouble();
		synchronized(barrera)
		{
			if(rr <= P1 && tiempoT1[i-1][j] == 0)
			{
				tiempoT1[i-1][j] = 1;
				PH[i-1][j] = 0;
			}
			else
				if(rr <= P1+P2 && tiempoT1[i+1][j] == 0)
				{
					tiempoT1[i+1][j] = 1;
					PH[i+1][j] = 0;
				}
				else
					if(rr <= P1+P2+P3 && tiempoT1[i][j-1] == 0)
					{
						tiempoT1[i][j-1] = 1;
						PH[i][j-1] = 0;
					}
					else
						if(tiempoT1[i][j+1] == 0)
						{
							tiempoT1[i][j+1] = 1;
							PH[i][j+1] = 0;
						}
						else
							cambio = false;
		}

		return cambio;
	}

	public void run()
	{
		try{
			while(true)
			{
				nextGen();
				barrera.await();
			}
		}catch(InterruptedException | BrokenBarrierException e){}
	}

	public static void computacion()
	{
		ExecutorService pool = Executors.newFixedThreadPool(4);

		for(int i = 0; i < 4; ++i)
			pool.execute(new parallelTumoralGrowth(i*size/4,(i+1)*size/4));
		pool.shutdown();
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Simulador Crecimiento Tumoral Paralelo");
		frame.setLayout(new BorderLayout());
		frame.setSize(640,480);
		frame.setResizable(true);
		frame.setLocationRelativeTo(null);

		frame.add(new parallelTumorGraphic());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}