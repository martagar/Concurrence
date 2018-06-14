/**
 * Tumor Growth Simulation
 * @author Marta García Pérez
 */

import java.util.Random;
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.*;

public class parallelTumoralGrowth implements Runnable {
	static CyclicBarrier barrier;
	static int[][] timeT;
	static int[][] timeT1;
	static int[][] PH;
	static int size;
	static int seed;
	static float Ps;
	static float Pp;
	static float Pm;
	static int NP;
	int initial;
	int end;
	static int n = 0;

	static class stop implements Runnable	{
		public void run()	{
			parallelTumorGraphic.update(timeT1);
			for(int i = 0; i < timeT.length; ++i)
				timeT[i] = timeT1[i].clone();
		}
	}

	parallelTumoralGrowth(int ini, int end)	{
		initial = ini;
		this.end = end;
	}

	public static void initialize()	{
		timeT = new int[size][size];
		timeT1 = new int[size][size];
		PH = new int[size][size];
		stop par = new stop();
		barrier = new CyclicBarrier(4, par);
		Random r = new Random();

		for(int i = 0; i < size; ++i)
			for(int j = 0; j < size; ++j)	{
				PH[i][j] = 0;
				timeT[i][j] = timeT1[i][j] = 0;
			}

		for(int i = (size/2)-(seed/2); i < (size/2)+(seed/2); ++i)
			for(int j = (size/2)-(seed/2); j < (size/2)+(seed/2); ++j)
				if(seed > 0) {
					timeT[i][j] = timeT1[i][j] = r.nextInt(2);
					if(timeT[i][j] == 1)
						seed--;
				}
	}

	public void nextGen()	{
		Random r = new Random();

		for(int i = initial; i < end; ++i)
			if(i != 0 && i != timeT.length-1)
				for(int j = 1; j < timeT.length-1; ++j)	{
					if(timeT[i][j] == 1)
						if (r.nextDouble() < Ps) {
							if (r.nextDouble() < Pp) {
								PH[i][j]++;
								if(PH[i][j] >= NP)
									if(!update(i,j))
										migrate(i,j);
								else
									migrate(i,j);
							}	else
									migrate(i,j);
						}	else {
								synchronized(barrier)
									{timeT1[i][j] = 0;}
						}
				}
	}

	public static void migrate(int i, int j) {
		Random r = new Random();
		if(r.nextDouble() < Pm)
			if(update(i,j))	{
				synchronized(barrier)
					{timeT1[i][j] = 0;}
			}
	}

	public static boolean update(int i, int j)
	{
		Random r = new Random();
		boolean change = true;
		PH[i][j] = 0;

		double P1 = (1-timeT[i-1][j])/(double)(4-(timeT[i+1][j] + timeT[i-1][j] + timeT[i][j+1] + timeT[i][j-1]));
		double P2 = (1-timeT[i+1][j])/(double)(4-(timeT[i+1][j] + timeT[i-1][j] + timeT[i][j+1] + timeT[i][j-1]));
		double P3 = (1-timeT[i][j-1])/(double)(4-(timeT[i+1][j] + timeT[i-1][j] + timeT[i][j+1] + timeT[i][j-1]));
		double P4 = (1-timeT[i][j+1])/(double)(4-(timeT[i+1][j] + timeT[i-1][j] + timeT[i][j+1] + timeT[i][j-1]));

		double rr = r.nextDouble();
		synchronized(barrier)	{
			if(rr <= P1 && timeT1[i-1][j] == 0)	{
				timeT1[i-1][j] = 1;
				PH[i-1][j] = 0;
			}	else if(rr <= P1+P2 && timeT1[i+1][j] == 0)	{
					timeT1[i+1][j] = 1;
					PH[i+1][j] = 0;
				}	else if(rr <= P1+P2+P3 && timeT1[i][j-1] == 0) {
						timeT1[i][j-1] = 1;
						PH[i][j-1] = 0;
					}	else if(timeT1[i][j+1] == 0) {
							timeT1[i][j+1] = 1;
							PH[i][j+1] = 0;
						}	else
								change = false;
		}

		return change;
	}

	public void run() {
		try{
			while(true)	{
				nextGen();
				barrier.await();
			}
		}catch(InterruptedException | BrokenBarrierException e){}
	}

	public static void computation() {
		ExecutorService pool = Executors.newFixedThreadPool(4);

		for(int i = 0; i < 4; ++i)
			pool.execute(new parallelTumoralGrowth(i*size/4,(i+1)*size/4));
		pool.shutdown();
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Tumor Growth Simulation");
		frame.setLayout(new BorderLayout());
		frame.setSize(640,480);
		frame.setResizable(true);
		frame.setLocationRelativeTo(null);

		frame.add(new parallelTumorGraphic());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
