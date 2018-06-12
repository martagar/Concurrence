/**
 * Clase que hace los gráficos del autómata celular con cálculo de entropía
 * @author Marta García Pérez
 */

import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.lang.String;
import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.data.xy.*;

public class ca1DGraphicSimulation extends JPanel
{
	private static int nGen;
	private static int n_celulas;

	static Automata_Cel_1D a_cel;
	static Menu_1D menu = new Menu_1D();
	static JPanel panel_AM; //frame para el menu y el automata
	static JFrame frame_graficas;

	public ca1DGraphicSimulation()
	{
		ini_AM();
		setSize(640,480);
		setLayout(new BorderLayout());
		add(panel_AM);
	}

    private static XYDataset crea_datos_Hamming()
    {
    	XYSeriesCollection conj_datos = new XYSeriesCollection();
    	XYSeries hamming = new XYSeries("Hamming");

    	for(int i = 0; i < ca1DSimulator.iter; ++i)
    		hamming.add(i,ca1DSimulator.curv_Hamming[i]);

    	conj_datos.addSeries(hamming);

    	return conj_datos;
    }

    private static JPanel crea_Grafica_Hamming()
    {
    	XYDataset datos = crea_datos_Hamming();

    	JFreeChart graf = ChartFactory.createXYLineChart("Curva de Hamming", "Generaciones", "Celulas", datos);

    	return new ChartPanel(graf);
    }

    private static XYDataset crea_datos_Entropia()
    {
    	XYSeriesCollection conj_datos = new XYSeriesCollection();
    	XYSeries entropia = new XYSeries("Entropia");

    	for(int i = 0; i < ca1DSimulator.iter+1; ++i)
    		entropia.add(i,ca1DSimulator.entropia_espacial[i]);

    	conj_datos.addSeries(entropia);

    	return conj_datos;
    }

    private static JPanel crea_Grafica_Entropia()
    {
    	XYDataset datos = crea_datos_Entropia();

    	JFreeChart graf = ChartFactory.createXYLineChart("Curva de Entropia Espacial", "Generaciones", "Entropia", datos);

    	return new ChartPanel(graf);
    }

    public static void dibujarGraficas()
    {
    	frame_graficas = new JFrame();
    	frame_graficas.setSize(640,480);
    	frame_graficas.setLayout(new GridLayout(2,1));
    	JPanel graf_curvHamming = crea_Grafica_Hamming();
    	JPanel graf_entropia = crea_Grafica_Entropia();

    	frame_graficas.add(graf_curvHamming);
    	frame_graficas.add(graf_entropia);
    	frame_graficas.setVisible(true);
    }

	public void ini_AM()
	{
		panel_AM = new JPanel();
		panel_AM.setSize(640,480);
		panel_AM.setLayout(new BorderLayout());

		panel_AM.add(menu,BorderLayout.EAST);
		panel_AM.setVisible(true);
	}

	public static void addA_Cel(int n_cel, int gen)
	{
		n_celulas = n_cel;
		nGen = gen;
		a_cel = new Automata_Cel_1D(n_cel, gen+1);
		panel_AM.add(a_cel, BorderLayout.CENTER);
	}

	public static void restart()
	{
		ca1DSimulator.gen = 0;
		a_cel.setVisible(false);
	}

	public static void update(int gene[], int fila) 
    {
        for (int i = 0; i < n_celulas; ++i) 
        {
            Color c = new Color((gene[i]*25)%255, (gene[i]*50)%255, (gene[i]*75)%255);
            Automata_Cel_1D.img.setRGB(i, fila, c.getRGB());
        }
        a_cel.repaint();
    }

    public static int[] intr_manual()
    {
    	if(menu.txtCelMan.getText().equals(""))
    		return new int[0];
    	else
    	{
    		int[] res = new int[menu.txtCelMan.getText().length()];
    		int i = 0;
    		int j = 0;
    		while(i < menu.txtCelMan.getText().length())
    		{
    			if(menu.txtCelMan.getText().charAt(i) != ',')
    			{
    				res[j] = Character.getNumericValue(menu.txtCelMan.getText().charAt(i));
    				j++;
    			}
    			i++;
    		}
    		return res;
    	}
    }

    public static int[] calc_regla()
	{
		int[] res = new int[8];
		if(menu.txtRegla.getText().equals(""))
			return res;
		else
		{
			int r = Integer.parseInt(menu.txtRegla.getText());
			for(int i = 0; i < 8; ++i)
			{
				if(r == 0)
					res[i] = 0;
				else
				{
					res[i] = r%2;
					r /= 2;
				}
			}
			return res;
		}
	}
}

class Automata_Cel_1D extends JPanel
{
	static BufferedImage img;

	public Automata_Cel_1D(int n_cel, int nGen)
	{
		img = new BufferedImage(n_cel, nGen, BufferedImage.TYPE_INT_RGB);
	}

	public void paintComponent(Graphics g)
	{
		g.drawImage(img,0,0,getWidth(),getHeight(),this);
	}
}

class Menu_1D extends JPanel
{
	private JLabel nCel = new JLabel("Numero de Celulas");
	JTextField txtCel = new JTextField();

	private JLabel num_gen = new JLabel("Numero de Generaciones");
	JTextField txtGen = new JTextField();

	private JLabel nRegla = new JLabel("Numero de Regla");
	JTextField txtRegla = new JTextField();

	private JLabel dim = new JLabel("Dimension del Lenguaje");
	JTextField txtDim = new JTextField();

	private JLabel cel_man = new JLabel("Introduccion Manual");
	JTextField txtCelMan = new JTextField();

	private JButton boton = new JButton("Start");

	Menu_1D()
	{
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		add(nCel);
		add(txtCel);
		add(num_gen);
		add(txtGen);
		add(nRegla);
		add(txtRegla);
		add(cel_man);
		add(txtCelMan);

		boton.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				if(boton.getText().equals("Start"))
				{
					boton.setText("Restart");

					if(txtCelMan.getText().equals(""))
						ca1DSimulator.n_c = Integer.parseInt(txtCel.getText());
					else
						ca1DSimulator.n_c = txtCelMan.getText().length();

					ca1DSimulator.iter = Integer.parseInt(txtCel.getText());
					ca1DSimulator.k = 2;

					ca1DGraphicSimulation.addA_Cel(ca1DSimulator.n_c,ca1DSimulator.iter);

					ca1DSimulator.inicializar(4,ca1DSimulator.n_c/2);

					ca1DGraphicSimulation.update(ca1DSimulator.tiempoT,ca1DSimulator.gen++);

					ca1DSimulator.realiza_automata();
				}
				else
					if(boton.getText().equals("Restart"))
					{
						ca1DGraphicSimulation.restart();
						boton.setText("Start");
					}
			}
		});
		add(boton);
	}
}