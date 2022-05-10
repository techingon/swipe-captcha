package com.echx.captcha.core;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;

public class ImageHelper {

	public static byte[] fromBufferedImage(BufferedImage img,String imagType) throws IOException {
//		img = scaleImage(img, 0.5);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ImageIO.write(img,imagType, bos);
		byte[] d = bos.toByteArray();
		bos.close();
		
//		byte[] d = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
		return d;
	}
	
	public static byte[] fromBufferedImage2(BufferedImage img,String imagType) throws IOException {
//		img = scaleImage(img, 0.5);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// 得到指定Format图片的writer
		Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName(imagType);
		ImageWriter writer = (ImageWriter) iter.next(); 

		// 得到指定writer的输出参数设置(ImageWriteParam )
		ImageWriteParam iwp = writer.getDefaultWriteParam();
		iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT); // 设置可否压缩
		iwp.setCompressionQuality(0.6f); // 设置压缩质量参数

		iwp.setProgressiveMode(ImageWriteParam.MODE_DISABLED);

		ColorModel colorModel = ColorModel.getRGBdefault();
		// 指定压缩时使用的色彩模式
		iwp.setDestinationType(new javax.imageio.ImageTypeSpecifier(colorModel,
		colorModel.createCompatibleSampleModel(16, 16)));

		// 此处因为ImageWriter中用来接收write信息的output要求必须是ImageOutput
		// 通过ImageIo中的静态方法，得到byteArrayOutputStream的ImageOutput
		writer.setOutput(ImageIO
		.createImageOutputStream(bos));
		IIOImage iIamge = new IIOImage(img, null, null);
		writer.write(null, iIamge, iwp);
		
		byte[] d = bos.toByteArray();
		bos.close();
		return d;
	}
	
	public static BufferedImage fromByte(byte[] data) throws IOException {
		InputStream is = new ByteArrayInputStream(data);
		BufferedImage image = ImageIO.read(is);
		is.close();
		return image;
	}
	
	  /*** 
	   * 按指定的比例缩放图片 
	   */
	  public static BufferedImage scaleImage(BufferedImage bufferedImage, double scale) { 
	    try { 
	      int width = bufferedImage.getWidth(); 
	      int height = bufferedImage.getHeight(); 
	  
	      width = (int)(width * scale); 
	      height = (int)(height * scale); 
	  
	      Image image = bufferedImage.getScaledInstance(width, height, 
	          Image.SCALE_SMOOTH); 
	      BufferedImage outputImage = new BufferedImage(width, height, 
	    		  bufferedImage.getType()); 
	      Graphics graphics = outputImage.getGraphics(); 
	      graphics.drawImage(image, 0, 0, null); 
	      graphics.dispose(); 
//	      ImageIO.write(outputImage, format, new File(destinationPath)); 
	      return outputImage;
	    } catch (Exception e) { 
	    	throw e;
	    }
	  } 
	  
	
}
