package BaiduProcess;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

public class MultiThread extends Thread {  
	private String filename;
	
	private static String getDefaultCharSet() {  
        OutputStreamWriter writer = new OutputStreamWriter(new ByteArrayOutputStream());  
        String enc = writer.getEncoding();  
        return enc;  
    }
	public static void main(String[] args) {
	
		System.out.println("Default Charset=" + Charset.defaultCharset());
        System.out.println("file.encoding=" + System.getProperty("file.encoding"));
        System.out.println("Default Charset=" + Charset.defaultCharset());
        System.out.println("Default Charset in Use=" + getDefaultCharSet());
        
		if (args.length > 0) {
			for (String filename : args) {
				MultiThread mT = new MultiThread(filename);
				mT.start();
			}
		} else {
			System.out.println("Usage: java -jar process.jar [filename ...]");
		}
		
	}
	
	public MultiThread(String fn) {
		filename = fn;
	}
	
	public void run() {
		System.out.println("MyThread.run() " + filename);
		BaiduExtractor bE = new BaiduExtractor();
		try {
			bE.processing(filename);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}  
}
