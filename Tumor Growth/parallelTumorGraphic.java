/**
 * Clase que hace los gráficos del simulador del crecimiento tumoral paralelo
 * @author Marta García Pérez
 */

import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.lang.String;
import java.util.concurrent.*;

public class parallelTumorGraphic extends JPanel
{
	static Paint_Tumor a_cel;
	static Menu_Tumor menu = new Menu_Tumor();
	static JPanel panel_AM; //panel para el menu y el automata

	public parallelTumorGraphic()
	{
		ini_AM();
		setSize(640,480);
		setLayout(new BorderLayout());
		add(panel_AM);
	}

	public void ini_AM()
	{
		panel_AM = new JPanel();
		panel_AM.setSize(640,480);
		panel_AM.setLayout(new BorderLayout());

		panel_AM.add(menu,BorderLayout.EAST);
		panel_AM.setVisible(true);
	}

	public static void addA_Cel()
	{
		a_cel = new Paint_Tumor();
		panel_AM.add(a_cel, BorderLayout.CENTER);
	}

	public static void restart()
	{
		a_cel.setVisible(false);
	}

	public static void update(int a[][]) 
    {
    	for(int i = 0; i < parallelTumoralGrowth.size; ++i)
	        for (int j = 0; j < parallelTumoralGrowth.size; ++j) 
	        {
	            if(a[i][j] == 1)
	            	Paint_Tumor.img.setRGB(j, i, Color.WHITE.getRGB());
	            else
	            	Paint_Tumor.img.setRGB(j,i, Color.BLACK.getRGB());
	        }
        a_cel.repaint();
    }
}

class Paint_Tumor extends JPanel
{
	static BufferedImage img;

	public Paint_Tumor()
	{
		img = new BufferedImage(parallelTumoralGrowth.size, parallelTumoralGrowth.size, BufferedImage.TYPE_INT_RGB);
	}

	public void paintComponent(Graphics g)
	{
		g.drawImage(img,0,0,getWidth(),getHeight(),this);
	}
}

class Menu_Tumor extends JPanel
{
	private JLabel dim = new JLabel("Dimension");
	JTextField txtDim = new JTextField("35");

	private JLabel sem = new JLabel("Semilla");
	JTextField txtSem = new JTextField("2");

	private JLabel L_ps = new JLabel("Probabilidad de Sobrevivir");
	JTextField txtPs = new JTextField("1");

	private JLabel L_pp = new JLabel("Probabilidad de Proliferar");
	JTextField txtPp = new JTextField("0.25");

	private JLabel L_pm = new JLabel("Probabilidad de Migrar");
	JTextField txtPm = new JTextField("0.8");

	private JLabel L_Np = new JLabel("PH necesario para Proliferar");
	JTextField txtNp = new JTextField("1");

	private JButton boton = new JButton("Start");

	Menu_Tumor()
	{
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));	

		add(dim);
		add(txtDim);
		add(sem);
		add(txtSem);
		add(L_ps);
		add(txtPs);
		add(L_pp);
		add(txtPp);
		add(L_pm);
		add(txtPm);
		add(L_Np);
		add(txtNp);

		boton.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				if(boton.getText().equals("Start"))
				{
					boton.setText("Restart");

					parallelTumoralGrowth.size = Integer.parseInt(txtDim.getText());
					parallelTumoralGrowth.semilla = Integer.parseInt(txtSem.getText());

					parallelTumoralGrowth.Ps = Float.parseFloat(txtPs.getText());
					parallelTumoralGrowth.Pp = Float.parseFloat(txtPp.getText());
					parallelTumoralGrowth.Pm = Float.parseFloat(txtPm.getText());
					parallelTumoralGrowth.NP = Integer.parseInt(txtNp.getText());

					parallelTumorGraphic.addA_Cel();

					parallelTumoralGrowth.inicializar();

					parallelTumorGraphic.update(parallelTumoralGrowth.tiempoT);

					new Thread(()->{parallelTumoralGrowth.computacion();}).start();
				}
				else
					if(boton.getText().equals("Restart"))
					{
						parallelTumorGraphic.restart();
						boton.setText("Start");
					}
			}
		});
		add(boton);
	}
}