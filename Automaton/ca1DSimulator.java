/**
 * Clase de implementación del autómata celular con cálculo de la entropía
 * @author Marta García Pérez
 */

import java.util.Scanner;
import java.util.Random;
import java.lang.Math;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;

public class ca1DSimulator implements ca1DSim, Runnable
{
	//Atributos del problema inicial
	static int k;
	static int iter;
	static int n_c;
	static int[] tiempoT;
	private static int[] tiempoT1;
	private static int[] regla;
	//Barrera
	private static CyclicBarrier barrera;
	//Atributos gráficos
	private static ca1DGraphicSimulation graphic;
	static int gen = 0;
	//Atributos para la entropía
	static double[] entropia_espacial;
	static double[] curv_Hamming;
	private static double entropia_temporal;
	private static double[] cont;
	static double[] cont_c;
	private static int celula;
	//Atributos para los hilos
	private int inicial;
	private int fin;

	//Clase que representa lo que hace la barrera cada vez que todos los hilos llegan a ella
	public static class stop implements Runnable
	{
		//Implementación de la interfaz Runnable
		public void run()
 		{
 			cont_c[tiempoT1[celula]]++;
 			entropia_espacial[gen] = entropia(cont, tiempoT.length);

 			curv_Hamming[gen-1] = curv_Hamming();

 			tiempoT = tiempoT1.clone();
 			graphic.update(tiempoT,gen++);
 		}

 		//Método propio del problema
 		public double curv_Hamming()
 		{
 			double curv_Hamming = 0;
 			for(int i = 0; i < tiempoT.length; ++i)
 				if(tiempoT[i] != tiempoT1[i])
 					curv_Hamming++;
 			return curv_Hamming;
 		}
	}

	//Constructor
	ca1DSimulator(int ini, int f) {inicial = ini; fin = f;}

	//Implementación de la interfaz ca1DSim
	public void nextGen()
	{
		for(int i = 0; i < cont.length; ++i)
			cont[i] = 0;
		for(int i = inicial; i < fin; ++i)
		{
			tiempoT1[i] = regla[(tiempoT[(tiempoT.length+(i-1))%tiempoT.length]*4 + tiempoT[i]*2 + tiempoT[(i+1)%tiempoT.length])];
			cont[tiempoT1[i]]++;
		}
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

	//Métodos propios de ca1DSimulator
	public static void inicializar(int n_hilo, int cel)
	{
		tiempoT = new int[n_c];
		tiempoT1 = new int[n_c];
		regla = new int[8];
		regla = ca1DGraphicSimulation.calc_regla();
		tiempoT = ca1DGraphicSimulation.intr_manual();

		cont = new double[k];
		cont_c = new double[k];
		celula = cel;
		entropia_espacial = new double[iter+1];
		curv_Hamming = new double[iter];
		for(int i = 0; i < k; ++i)
		{
			cont[i] = 0;
			cont_c[i] = 0;
		}

		if(tiempoT.length == 0)
		{
			tiempoT = new int[n_c];
			rell_ale();
		}

		entropia_espacial[gen] = entropia(cont,tiempoT.length);

		stop bar = new stop();
		barrera = new CyclicBarrier(n_hilo,bar);
	}

	public static void rell_ale()
	{
		Random r = new Random();
		for(int i = 0; i < tiempoT.length; ++i)
		{
			tiempoT[i] = Math.abs((r.nextInt())%k);
			cont[tiempoT[i]]++;
		}
	}

	public static void rell_man()
	{
		Scanner scan = new Scanner(System.in);
		for(int i = 0; i < tiempoT.length; ++i)
		{
			System.out.print("Celula > ");
			tiempoT[i] = Math.abs((scan.nextInt())%k);
		}
	}

	//tipo será tiempoT.length si es la entropia espacial y iter si es la entropía temporal
	public static double entropia(double[] c, int tipo) 
	{
		double entropia = 0;
		for(int i = 0; i < c.length; ++i)
		{
			c[i] /= (double)tipo;
			if(c[i] != 0)
				entropia += (c[i]*(Math.log(c[i])/Math.log(k)));
		}
		return -entropia;
	}

	//Implementación de la interfaz Runnable
	public void run() {caComputation(iter);}

	public static void realiza_automata()
	{
		int n_hilos = 4;
		Thread hilos[] = new Thread[n_hilos];

		for(int i = 0; i < n_hilos; ++i)
		{
			hilos[i] = new Thread(new ca1DSimulator(i*n_c/n_hilos,(i+1)*n_c/n_hilos));
		}	

		for(int i = 0; i < n_hilos; ++i)
		{
			hilos[i].start();
		}

		try{
			for(int i = 0; i < n_hilos; ++i)
			{
				hilos[i].join();
			}
		}catch(InterruptedException e){}		

		graphic.dibujarGraficas();
	}
}