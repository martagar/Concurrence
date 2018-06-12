/**
 * Clase de implementación de la interfaz
 * @author Marta García Pérez
 */

import java.util.Scanner;
import java.util.Random;
import java.lang.Math;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class ca2DSimulator implements ca1DSim, Runnable
{
	static int k = 2;
	static int n_hilo = 4;
	static int n_c;
	static int iter;
	static int[][] tiempoT;
	static int[][] tiempoT1;
	static CyclicBarrier barrera;
	static int vecindad;
	static AtomicInteger cel_vivas = new AtomicInteger();
	static double[] celulas_vivas;
	static int gen;
	int inicial;
	int fin;

	public static class stop implements Runnable
	{
		public void run()
 		{
 			for(int i = 0; i < tiempoT.length; ++i)
 				tiempoT[i] = tiempoT1[i].clone();
 			Celulas.actualizar();
 			celulas_vivas[gen] = (double)cel_vivas.get();
 			++gen;
  			ca2DGraphicSimulation.update(tiempoT);
  			if(gen == iter)
  				ca2DGraphicSimulation.dibujarGrafica();
  			cel_vivas.set(0);
 		}
	}

	ca2DSimulator(int ini, int f) {inicial = ini; fin = f;}

	public void nextGen()
	{
		int vec_vivas;
		for(int i = inicial; i < fin; ++i)
			for(int j = 0; j < tiempoT[0].length; ++j)
			{
				if(vecindad == 0)
					vec_vivas = vecVivas_Moore(i,j);
				else
					vec_vivas = vecVivas_Newmann(i,j);
				
				if(vec_vivas < 2 || vec_vivas > 3)
					tiempoT1[i][j] = 0;
				else
					if(vec_vivas == 3)
						tiempoT1[i][j] = 1;
					else
						tiempoT1[i][j] = tiempoT[i][j];
				if(tiempoT1[i][j] == 1)
					cel_vivas.incrementAndGet();
			}
	}

	public int vecVivas_Moore(int i, int j)
	{
		int vec_vivas = 0;

		if(tiempoT[(tiempoT.length+(i-1))%tiempoT.length][j] == 1)
			vec_vivas++;
		if(tiempoT[i][(tiempoT.length+(j-1))%tiempoT.length] == 1)
			vec_vivas++;
		if(tiempoT[i][(j+1)%tiempoT.length] == 1)
			vec_vivas++;
		if(tiempoT[(i+1)%tiempoT.length][j] == 1)
			vec_vivas++;

		return vec_vivas;
	}

	public int vecVivas_Newmann(int i, int j)
	{
		int vec_vivas = 0;
		for(int k = -1; k < 2; ++k)
			for(int l = -1; l < 2; ++l)
			{
				int x = (tiempoT.length+(i+k))%tiempoT.length;
				int y = (tiempoT.length+(j+l))%tiempoT.length;
				if(tiempoT[x][y] == 1 && (x != i || y != j))
					vec_vivas++;
			}
		return vec_vivas;
	}

	public void caComputation(int nGen)
	{
		try{
			for(int i = 0; i < nGen; ++i)
			{
				nextGen();
				barrera.await();
			}
		}
		catch(InterruptedException e){System.err.println("Excepcion IE");}
		catch(BrokenBarrierException e){System.err.println("Excepcion BBE");}
	}

	public static void inicializar()
	{
		tiempoT = new int[n_c][n_c];
		tiempoT1 = new int[n_c][n_c];
		stop bar = new stop();
		barrera = new CyclicBarrier(n_hilo,bar);
		celulas_vivas = new double[iter+1];
		gen = 0;

		rell_ale();
		Celulas.actualizar();
		celulas_vivas[gen] = (double)cel_vivas.get();
		++gen;
	}

	public static void rell_ale()
	{
		cel_vivas.set(0);
		Random r = new Random();
		for(int i = 0; i < tiempoT.length; ++i)
			for(int j = 0; j < tiempoT.length; ++j)
			{
				tiempoT[i][j] = Math.abs((r.nextInt())%k);
				if(tiempoT[i][j] == 1)
					cel_vivas.incrementAndGet();
			}
	}

	public void run() {caComputation(iter);}

	public static void realiza_automata()
	{
		ExecutorService pool = Executors.newFixedThreadPool(n_hilo);

		for(int i = 0; i < n_hilo; ++i)
			pool.execute(new ca2DSimulator(i*n_c/n_hilo, (i+1)*n_c/n_hilo));

		pool.shutdown();
	}
}