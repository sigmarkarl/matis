package org.simmi;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.swing.JComponent;
import javax.swing.JFrame;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFRenderer;

public class PDFStuff extends JComponent {
	PDFPage page;
	
	public PDFStuff( String fname ) throws IOException {
		File file = new File( fname );

		RandomAccessFile raf = new RandomAccessFile(file, "r");
		FileChannel channel = raf.getChannel();
		ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
		PDFFile pdffile = new PDFFile(buf);
		page = pdffile.getPage(0);
	}
	
	public void paintComponent( Graphics g ) {
		super.paintComponent( g );
		
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		PDFRenderer renderer = new PDFRenderer(page, g2, new Rectangle(0, 0, 500, 500), null, Color.RED);
		try {
			if( page != null ) page.waitForFinish();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		renderer.run();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame	jf = new JFrame();
		
		try {
			PDFStuff hey = new PDFStuff("/home/sigmar/hafsjor/gogn/arsreikningar_2/Deloitte/Vísir.pdf");
			jf.add( hey );
			jf.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
			jf.setSize(800, 600);
			jf.setVisible( true );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
