/**
 * Automaton Cellular 1D
 * @author Marta García Pérez
 */

import java.util.Scanner;
import java.util.Random;
import java.lang.Math;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;

public class ca1DSimulator implements ca1DSim, Runnable {
	static int k;
	static int iter;
	static int n_c;
	static int[] timeT;
	private static int[] timeT1;
	private static int[] rule;

	private static CyclicBarrier barrier;

	private static ca1DGraphicSimulation graphic;
	static int gen = 0;

	static double[] space_entropy;
	static double[] curv_Hamming;
	private static double temporary_entropy;
	private static double[] cont;
	static double[] cont_c;
	private static int cell;

	private int initial;
	private int end;

	//Class of the barrier
	public static class stop implements Runnable	{
		//interface Runnable implementation
		public void run()	{
 			cont_c[timeT1[cell]]++;
 			space_entropy[gen] = entropy(cont, timeT.length);

 			curv_Hamming[gen-1] = curv_Hamming();

 			timeT = timeT1.clone();
 			graphic.update(timeT,gen++);
 		}

 		public double curv_Hamming() {
 			double curv_Hamming = 0;
 			for(int i = 0; i < timeT.length; ++i)
 				if(timeT[i] != timeT1[i])
 					curv_Hamming++;
 			return curv_Hamming;
 		}
	}

	ca1DSimulator(int ini, int f) {initial = ini; end = f;}

	//interface ca1DSim implementation
	public void nextGen()	{
		for(int i = 0; i < cont.length; ++i)
			cont[i] = 0;
		for(int i = initial; i < end; ++i) {
			timeT1[i] = rule[(timeT[(timeT.length+(i-1))%timeT.length]*4 + timeT[i]*2 + timeT[(i+1)%timeT.length])];
			cont[timeT1[i]]++;
		}
	}

	public void caComputation(int nGen)	{
		try{
			for(int i = 0; i < nGen; ++i)	{
				nextGen();
				barrier.await();
			}
		}
		catch(InterruptedException e){System.err.println("Excepcion IE");}
		catch(BrokenBarrierException e){System.err.println("Excepcion BBE");}
	}

	public static void initialize(int n_thread, int cel) {
		timeT = new int[n_c];
		timeT1 = new int[n_c];
		rule = new int[8];
		rule = ca1DGraphicSimulation.calc_rule();
		timeT = ca1DGraphicSimulation.intr_manual();

		cont = new double[k];
		cont_c = new double[k];
		cell = cel;
		space_entropy = new double[iter+1];
		curv_Hamming = new double[iter];
		for(int i = 0; i < k; ++i) {
			cont[i] = 0;
			cont_c[i] = 0;
		}

		if(timeT.length == 0)	{
			timeT = new int[n_c];
			fill_rand();
		}

		space_entropy[gen] = entropy(cont,timeT.length);

		stop bar = new stop();
		barrier = new CyclicBarrier(n_thread,bar);
	}

	public static void fill_rand() {
		Random r = new Random();
		for(int i = 0; i < timeT.length; ++i)	{
			timeT[i] = Math.abs((r.nextInt())%k);
			cont[timeT[i]]++;
		}
	}

	public static void fill_man()
	{
		Scanner scan = new Scanner(System.in);
		for(int i = 0; i < timeT.length; ++i)	{
			System.out.print("cell > ");
			timeT[i] = Math.abs((scan.nextInt())%k);
		}
	}

	//type will be timeT.length if it is the space entropy and iter if it is the temporary entropy
	public static double entropy(double[] c, int type) {
		double entropy = 0;
		for(int i = 0; i < c.length; ++i)	{
			c[i] /= (double)type;
			if(c[i] != 0)
				entropy += (c[i]*(Math.log(c[i])/Math.log(k)));
		}
		return -entropy;
	}

	//interface Runnable implementation
	public void run() {caComputation(iter);}

	public static void make_automaton()	{
		int n_threads = 4;
		Thread threads[] = new Thread[n_threads];

		for(int i = 0; i < n_threads; ++i)
			threads[i] = new Thread(new ca1DSimulator(i*n_c/n_threads,(i+1)*n_c/n_threads));

		for(int i = 0; i < n_threads; ++i)
			threads[i].start();

		try{
			for(int i = 0; i < n_threads; ++i)
				threads[i].join();
		}catch(InterruptedException e){}

		graphic.drawGraphics();
	}
}
