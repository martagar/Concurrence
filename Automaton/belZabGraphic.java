/**
 * Chemical reaction's graphics
 * @author Marta García Pérez
 */

import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.lang.String;

public class belZabGraphic extends JPanel {
	static Paint_BelZab a_cel;
	static Menu_BelZab menu = new Menu_BelZab();
	static JPanel panel_AM; //panel para el menu y el automata

	public belZabGraphic() {
		ini_AM();
		setSize(640,480);
		setLayout(new BorderLayout());
		add(panel_AM);
	}

	public void ini_AM() {
		panel_AM = new JPanel();
		panel_AM.setSize(640,480);
		panel_AM.setLayout(new BorderLayout());

		panel_AM.add(menu,BorderLayout.EAST);
		panel_AM.setVisible(true);
	}

	public static void addA_Cel()	{
		a_cel = new Paint_BelZab();
		panel_AM.add(a_cel, BorderLayout.CENTER);
	}

	public static void restart() {
		a_cel.setVisible(false);
		belZabParallel.u = false;
	}

	public static void update(int k) {
    	for(int i = 0; i < belZabParallel.size; ++i)
	        for (int j = 0; j < belZabParallel.size; ++j) {
	            Color c = new Color(belZabParallel.a[i][j][k], belZabParallel.b[i][j][k], belZabParallel.c[i][j][k]);
	            Paint_BelZab.img.setRGB(j, i, c.getRGB());
	        }
      a_cel.repaint();
    }
}

class Paint_BelZab extends JPanel {
	static BufferedImage img;

	public Paint_BelZab()	{
		img = new BufferedImage(belZabParallel.size, belZabParallel.size, BufferedImage.TYPE_INT_RGB);
	}

	public void paintComponent(Graphics g) {
		g.drawImage(img,0,0,getWidth(),getHeight(),this);
	}
}

class Menu_BelZab extends JPanel {
	private JLabel dim = new JLabel("Size");
	JTextField txtDim = new JTextField();

	private JLabel alpha = new JLabel("Alpha");
	JTextField txtalpha = new JTextField("1.2");

	private JLabel beta = new JLabel("Beta");
	JTextField txtbeta = new JTextField("1.0");

	private JLabel gamma = new JLabel("Gamma");
	JTextField txtgamma = new JTextField("1.0");

	private JButton boton = new JButton("Start");

	Menu_BelZab()	{
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

		add(dim);
		add(txtDim);
		add(alpha);
		add(txtalpha);
		add(beta);
		add(txtbeta);
		add(gamma);
		add(txtgamma);

		boton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt)	{
				if(boton.getText().equals("Start"))	{
					boton.setText("Restart");

					belZabParallel.size = Integer.parseInt(txtDim.getText());

					belZabParallel.alfa = Float.parseFloat(txtalpha.getText());
					belZabParallel.beta = Float.parseFloat(txtbeta.getText());
					belZabParallel.gamma = Float.parseFloat(txtgamma.getText());

					belZabGraphic.addA_Cel();

					belZabParallel.setup();
					belZabParallel.u = true;

					belZabGraphic.update(1);

					belZabParallel.make_automaton();
				}	else if(boton.getText().equals("Restart")) {
						belZabGraphic.restart();
						boton.setText("Start");
					}
			}
		});
		add(boton);
	}
}
