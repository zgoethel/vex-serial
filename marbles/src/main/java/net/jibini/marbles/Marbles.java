package net.jibini.marbles;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

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
import nu.pattern.OpenCV;

public class Marbles
{
	public static final String CORTEX_PORT = "COM13";
	public static final int CORTEX_BAUD = 38400;
	
	//public static final Scalar MARBLE_WOOD = new Scalar()

	public Cortex cortex;
	public VideoCapture capture;
	
	public Mat frame = new Mat();
	public JFrame f = new JFrame();
	public JLabel l = new JLabel();
	public JFrame c = new JFrame();
	
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
		//cortex = new Cortex(CORTEX_PORT, CORTEX_BAUD, true);

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
		
		Mat cropped = new Mat(frame, new Rect(720 / 2 - 20, 480 / 2 - 20, 40, 40));
	    Scalar mean = Core.mean(cropped);
		Imgproc.rectangle(frame, new Point(720 / 2 - 20, 480 / 2 - 20), new Point(720 / 2 + 20, 480 / 2 + 20), mean);
		
		double r, g, b;
		Imgproc.putText(frame, "B" + (b = mean.val[0]), new Point(0, 40), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(255, 0, 0));
		Imgproc.putText(frame, "G" + (g = mean.val[1]), new Point(0, 80), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(0, 255, 0));
		Imgproc.putText(frame, "R" + (r = mean.val[2]), new Point(0, 120), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(0, 0, 255));
		
		if (b > 160 && g > 200)
			Imgproc.putText(frame, "Opaque White", new Point (0, 300), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(255, 255, 255));
		else if (r < 135)
			Imgproc.putText(frame, "Steel", new Point (0, 300), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(255, 255, 255));
		else if (g > 100 && r > 160 && b < 150)
			Imgproc.putText(frame, "Wood", new Point (0, 300), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(255, 255, 255));
		else
			Imgproc.putText(frame, "Clear White", new Point (0, 300), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(255, 255, 255));
			
		
		displayImage(Mat2BufferedImage(frame));
		frame.release();
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
