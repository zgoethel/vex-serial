package net.jibini.marbles;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import gnu.io.NRSerialPort;
import net.jibini.cortex.Cortex;
import nu.pattern.OpenCV;

public class Marbles
{
	public static final String CORTEX_PORT = "COM13";
	public static final int CORTEX_BAUD = 38400;

	public Cortex cortex;
	public VideoCapture capture;
	
	public Mat frame = new Mat();
	public JFrame f = new JFrame();
	public JLabel l = new JLabel();

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
	}

	private void update()
	{
		capture.read(frame);
		displayImage(Mat2BufferedImage(frame));
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
