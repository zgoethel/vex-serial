package net.jibini.marbles;

import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.joml.Vector3d;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import gnu.io.NRSerialPort;
import net.jibini.cortex.Cortex;
import net.jibini.cortex.SensorLog;
import net.jibini.cortex.port.DigitalPort;
import net.jibini.cortex.port.MotorPort;
import net.jibini.cortex.port.Port;
import nu.pattern.OpenCV;

public class Marbles
{
	public static final String CORTEX_PORT = "COM13";
	public static final int CORTEX_BAUD = 38400;
	
	public int centerX = 720 / 2, centerY = 480 / 2;
	
	//public static final Scalar MARBLE_WOOD = new Scalar()

	public Cortex cortex;
	public VideoCapture capture;
	public SensorLog r, g, b;
	
	public Mat frame = new Mat();
	public JFrame f = new JFrame();
	public JLabel l = new JLabel();
	public JFrame c = new JFrame();
	
	public int target, home, lastTarget, speed;
	public long lastSpin;
	
	public Port ben, bencoder;
	public Port wings, wingEncoder;
	
	public Vector3d[] marbles =
	{
			new Vector3d(75, 100, 90),
			new Vector3d(144, 156, 180),
			new Vector3d(112, 122, 114),
			new Vector3d(172, 175, 157),
			
			new Vector3d(80, 80, 70)
	};
	
	public String[] marbleNames =
	{
			"Clear White",
			"Wood",
			"Steel",
			"Opaque White",
			
			"None"
	};
	
	/*public JSlider[] sliders = new JSlider[6];
	int[] a = new int[sliders.length];*/

	static
	{
		OpenCV.loadShared();
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public void start()
	{
		System.out.print("Available ports: ");
		for (String s : NRSerialPort.getAvailableSerialPorts())
			System.out.print(s + " ");
		System.out.println();
		cortex = new Cortex(CORTEX_PORT, CORTEX_BAUD, true);

		capture = new VideoCapture();
		capture.open(0);
		Cortex.startInfiniteThread(this::update);
		
		f.add(l);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		/*JPanel p = new JPanel(new GridLayout(6, 1));
		c.add(p);
		for (int i = 0; i < sliders.length; i ++)
			p.add(sliders[i] = new JSlider(0, 255), i);
		for (int i = 0; i < 3; i ++)
			sliders[i].setValue(0);
		for (int i = 3; i < 6; i ++)
			sliders[i].setValue(255);
		c.pack();
		c.setVisible(true);
		c.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);*/
		
		r = new SensorLog();
		g = new SensorLog();
		b = new SensorLog();
		
		l.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent arg0)
			{}

			@Override
			public void mouseEntered(MouseEvent arg0)
			{}

			@Override
			public void mouseExited(MouseEvent arg0)
			{}

			@Override
			public void mousePressed(MouseEvent arg0)
			{

				centerX = arg0.getPoint().x;
				centerY = arg0.getPoint().y;
			}

			@Override
			public void mouseReleased(MouseEvent arg0)
			{}
		});
		
		try
		{
			ben = new MotorPort(4);
			bencoder = new DigitalPort(0);
			wings = new MotorPort(1);
			wingEncoder = new DigitalPort(4);
			
			Thread.sleep(10000);
			home = bencoder.get();
			target = home;
			
			lastSpin = System.currentTimeMillis();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	private void update()
	{
		/*for (int i = 0; i < sliders.length; i ++)
			a[i] = (sliders[i] == null) ? 0 : sliders[i].getValue();*/

		capture.read(frame);
		Imgproc.resize(frame, frame, new Size(720, 480));
		//Imgproc.GaussianBlur(frame, frame, new Size(11, 11), 4);
		//Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2HSV);
		/*Core.inRange(frame, new Scalar(a[0], a[1], a[2]), new Scalar(a[3], a[4], a[5]), frame);
		
		Mat erode = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(11, 11));
	    Mat dilate = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(12, 12));
	    Imgproc.erode(frame, frame, erode);
	    Imgproc.dilate(frame, frame, dilate);*/
		
		Mat cropped = new Mat(frame, new Rect(centerX - 20, centerY - 20, 40, 40));
	    Scalar mean = Core.mean(cropped);
		Imgproc.rectangle(frame, new Point(centerX - 20, centerY - 20), new Point(centerX + 20, centerY + 20), mean);
		
		double nr, ng, nb;
		Imgproc.putText(frame, "B" + (nb = mean.val[0]), new Point(0, 40), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(255, 0, 0));
		Imgproc.putText(frame, "G" + (ng = mean.val[1]), new Point(0, 80), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(0, 255, 0));
		Imgproc.putText(frame, "R" + (nr = mean.val[2]), new Point(0, 120), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(0, 0, 255));
		r.putValue(nr);
		g.putValue(ng);
		b.putValue(nb);
		
		/*if (b.getAverage(10) > 160 && g.getAverage(10) > 215)
			Imgproc.putText(frame, "Opaque White", new Point (0, 300), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(255, 255, 255));
		else if (r.getAverage(10) < 145)
			Imgproc.putText(frame, "Steel", new Point (0, 300), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(255, 255, 255));
		else if (g.getAverage(10) > 160 && r.getAverage(10) > 160 && b.getAverage(10) < 200)
			Imgproc.putText(frame, "Wood", new Point (0, 300), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(255, 255, 255));
		else
			Imgproc.putText(frame, "Clear White", new Point (0, 300), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(255, 255, 255));*/
		
		Vector3d color = new Vector3d(b.getAverage(10), g.getAverage(10), r.getAverage(10));
		double shortest = 100;
		int shortestId = 0;
		
		for (int i = 0; i < marbles.length; i ++)
		{
			double dist = marbles[i].distance(color);
			
			if (dist < shortest)
			{
				shortest = dist;
				shortestId = i;
			}
		}
		
		Imgproc.putText(frame, marbleNames[shortestId], new Point (0, 300), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(255, 255, 255));
		
		displayImage(Mat2BufferedImage(frame));
		frame.release();
		
		if (target - cortex.sensorValues.digital[0] < 6 && ben.get() > 0)
		{
			System.out.println("Stop");
			ben.set(0);
		} else if (target - cortex.sensorValues.digital[0] >= 6 && ben.get() == 0)
		{
			System.out.println("Start");
			ben.set(16);
		}
		
		if (System.currentTimeMillis() - lastSpin > 3000 && ben.get() == 0)
		{
			target += 45;
			lastSpin = System.currentTimeMillis();
		}
	}

	public BufferedImage Mat2BufferedImage(Mat m)
	{
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if (m.channels() > 1)
			type = BufferedImage.TYPE_3BYTE_BGR;
		int bufferSize = m.channels() * m.cols() * m.rows();
		byte[] b = new byte[bufferSize];
		m.get(0, 0, b);
		BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);
		return image;
	}

	public void displayImage(Image img2)
	{
		ImageIcon icon = new ImageIcon(img2);
		f.pack();
		l.setIcon(icon);
	}

	public static Marbles MARBLES;

	public static void main(String[] args)
	{
		MARBLES = new Marbles();
		MARBLES.start();
	}
}
