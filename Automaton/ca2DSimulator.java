/**
 * Automaton Cellular 2D
 * @author Marta García Pérez
 */

import java.util.Scanner;
import java.util.Random;
import java.lang.Math;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class ca2DSimulator implements ca1DSim, Runnable {
	static int k = 2;
	static int n_thread = 4;
	static int n_c;
	static int iter;
	static int[][] timeT;
	static int[][] timeT1;
	static CyclicBarrier barrier;
	static int neighbourhood;
	static AtomicInteger living_cel = new AtomicInteger();
	static double[] living_cells;
	static int gen;
	int initial;
	int end;

	public static class stop implements Runnable {
		public void run()	{
 			for(int i = 0; i < timeT.length; ++i)
 				timeT[i] = timeT1[i].clone();
 			Cells.update();
 			living_cells[gen] = (double)living_cel.get();
 			++gen;
  			ca2DGraphicSimulation.update(timeT);
  			if(gen == iter)
  				ca2DGraphicSimulation.drawGraphic();
  			living_cel.set(0);
 		}
	}

	ca2DSimulator(int ini, int f) {initial = ini; end = f;}

	public void nextGen()	{
		int living_neigh;
		for(int i = initial; i < end; ++i)
			for(int j = 0; j < timeT[0].length; ++j) {
				if(neighbourhood == 0)
					living_neigh = livingNeigh_Moore(i,j);
				else
					living_neigh = livingNeigh_Newmann(i,j);

				if(living_neigh < 2 || living_neigh > 3)
					timeT1[i][j] = 0;
				else if(living_neigh == 3)
						timeT1[i][j] = 1;
					else
						timeT1[i][j] = timeT[i][j];

				if(timeT1[i][j] == 1)
					living_cel.incrementAndGet();
			}
	}

	public int livingNeigh_Moore(int i, int j) {
		int living_neigh = 0;

		if(timeT[(timeT.length+(i-1))%timeT.length][j] == 1)
			living_neigh++;
		if(timeT[i][(timeT.length+(j-1))%timeT.length] == 1)
			living_neigh++;
		if(timeT[i][(j+1)%timeT.length] == 1)
			living_neigh++;
		if(timeT[(i+1)%timeT.length][j] == 1)
			living_neigh++;

		return living_neigh;
	}

	public int livingNeigh_Newmann(int i, int j) {
		int living_neigh = 0;
		for(int k = -1; k < 2; ++k)
			for(int l = -1; l < 2; ++l)	{
				int x = (timeT.length+(i+k))%timeT.length;
				int y = (timeT.length+(j+l))%timeT.length;
				if(timeT[x][y] == 1 && (x != i || y != j))
					living_neigh++;
			}
		return living_neigh;
	}

	public void caComputation(int nGen) {
		try{
			for(int i = 0; i < nGen; ++i)	{
				nextGen();
				barrier.await();
			}
		}
		catch(InterruptedException e){System.err.println("Excepcion IE");}
		catch(BrokenBarrierException e){System.err.println("Excepcion BBE");}
	}

	public static void initialize() {
		timeT = new int[n_c][n_c];
		timeT1 = new int[n_c][n_c];
		stop bar = new stop();
		barrier = new CyclicBarrier(n_thread,bar);
		living_cells = new double[iter+1];
		gen = 0;

		fill_rand();
		Cells.update();
		living_cells[gen] = (double)living_cel.get();
		++gen;
	}

	public static void fill_rand()
	{
		living_cel.set(0);
		Random r = new Random();
		for(int i = 0; i < timeT.length; ++i)
			for(int j = 0; j < timeT.length; ++j) {
				timeT[i][j] = Math.abs((r.nextInt())%k);
				if(timeT[i][j] == 1)
					living_cel.incrementAndGet();
			}
	}

	public void run() {caComputation(iter);}

	public static void make_automaton()	{
		ExecutorService pool = Executors.newFixedThreadPool(n_thread);

		for(int i = 0; i < n_thread; ++i)
			pool.execute(new ca2DSimulator(i*n_c/n_thread, (i+1)*n_c/n_thread));

		pool.shutdown();
	}
}
