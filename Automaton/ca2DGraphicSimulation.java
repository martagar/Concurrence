/**
 * Clase que hace los gráficos del autómata celular
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
import java.util.concurrent.*;

public class ca2DGraphicSimulation extends JPanel
{
	private static int n_celulas;

	static Automata_Cel_2D a_cel;
	static Menu_2D menu = new Menu_2D();
	static Celulas celulas;
	static JPanel panel_AM; //panel para el menu y el automata
	static JFrame frame_CV; //frame para la gráfica de células vivas

	public ca2DGraphicSimulation()
	{
		ini_AM();
		setSize(640,480);
		setLayout(new BorderLayout());
		add(panel_AM);
	}

    private static XYDataset crea_datos()
    {
    	XYSeriesCollection conj_datos = new XYSeriesCollection();
    	XYSeries vivas = new XYSeries("Vivas");
    	XYSeries muertas = new XYSeries("Muertas");

    	for(int i = 0; i < ca2DSimulator.iter; ++i)
    	{
    		vivas.add(i,ca2DSimulator.celulas_vivas[i]);
    		muertas.add(i,ca2DSimulator.n_c*ca2DSimulator.n_c - ca2DSimulator.celulas_vivas[i]);
    	}

    	conj_datos.addSeries(vivas);
    	conj_datos.addSeries(muertas);

    	return conj_datos;
    }

    private static JPanel crea_Graficas()
    {
    	XYDataset datos = crea_datos();

    	JFreeChart graf = ChartFactory.createXYLineChart("Medicion celulas", "Generaciones", "Numero de Celulas", datos);

    	return new ChartPanel(graf);
    }

    public static void dibujarGrafica()
    {
    	frame_CV = new JFrame();
    	frame_CV.setSize(640,480);
    	frame_CV.setLayout(new BorderLayout());
    	JPanel graficas = crea_Graficas();

    	frame_CV.add(graficas);
    	frame_CV.setVisible(true);
    }

	public void ini_AM()
	{
		panel_AM = new JPanel();
		panel_AM.setSize(640,480);
		panel_AM.setLayout(new BorderLayout());

		panel_AM.add(menu,BorderLayout.EAST);
		panel_AM.setVisible(true);
	}

	public static void addA_Cel(int n_cel)
	{
		n_celulas = n_cel;
		a_cel = new Automata_Cel_2D(n_cel);
		panel_AM.add(a_cel, BorderLayout.CENTER);
		celulas = new Celulas();
		panel_AM.add(celulas, BorderLayout.SOUTH);
	}

	public static void restart()
	{
		a_cel.setVisible(false);
		celulas.setVisible(false);
		frame_CV.setVisible(false);
	}

	public static void update(int gene[][]) 
    {
    	for(int i = 0; i < gene.length; ++i)
	        for (int j = 0; j < n_celulas; ++j) 
	        {
	            Color c = new Color((gene[i][j]*25)%255, (gene[i][j]*50)%255, (gene[i][j]*75)%255);
	            Automata_Cel_2D.img.setRGB(j, i, c.getRGB());
	        }
        a_cel.repaint();
    }
}

class Celulas extends JPanel
{
	static JLabel celulas = new JLabel("Celulas vivas: \nCelulas muertas: ");

	Celulas()
	{
		add(celulas);
	}
	public static void actualizar()
	{
		celulas.setText("Celulas vivas: " + ca2DSimulator.cel_vivas.get() + " Celulas muertas: " + (ca2DSimulator.n_c*ca2DSimulator.n_c-ca2DSimulator.cel_vivas.get()));
	}
}

class Automata_Cel_2D extends JPanel
{
	static BufferedImage img;

	public Automata_Cel_2D(int n_cel)
	{
		img = new BufferedImage(n_cel, n_cel, BufferedImage.TYPE_INT_RGB);
	}

	public void paintComponent(Graphics g)
	{
		g.drawImage(img,0,0,getWidth(),getHeight(),this);
	}
}

class Menu_2D extends JPanel
{
	private JLabel nCel = new JLabel("Numero de Celulas");
	JTextField txtCel = new JTextField();

	private JLabel num_gen = new JLabel("Numero de Generaciones");
	JTextField txtGen = new JTextField();

	private JLabel Vecindad = new JLabel("Vecindad (Newmann/Moore)");
	JButton butVec = new JButton("Newmann");

	private JButton boton = new JButton("Start");

	Menu_2D()
	{
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		add(Vecindad);		

		butVec.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				if(butVec.getText().equals("Newmann"))
					butVec.setText("Moore");
				else
					butVec.setText("Newmann");
			}
		});

		add(butVec);
		add(nCel);
		add(txtCel);
		add(num_gen);
		add(txtGen);

		boton.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				if(boton.getText().equals("Start"))
				{
					boton.setText("Restart");

					ca2DSimulator.n_c = Integer.parseInt(txtCel.getText());

					if(ca2DSimulator.n_c >= 200)
					{
						ca2DSimulator.iter = Integer.parseInt(txtCel.getText());
						ca2DSimulator.vecindad = (butVec.getText().equals("Newmann")? 1 : 0);

						ca2DGraphicSimulation.addA_Cel(ca2DSimulator.n_c);

						ca2DSimulator.inicializar();

						ca2DGraphicSimulation.update(ca2DSimulator.tiempoT);

						ca2DSimulator.realiza_automata();
					}
				}
				else
					if(boton.getText().equals("Restart"))
					{
						ca2DGraphicSimulation.restart();
						boton.setText("Start");
					}
			}
		});
		add(boton);
	}
}