package com.echx.captcha.core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import com.echx.captcha.bean.SwipeCaptcha;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwipCaptchaBuilder {
	private static Logger log = LoggerFactory.getLogger(SwipCaptchaBuilder.class);
	
	private final List<byte[]> oriImages = new ArrayList<>();
	// 源文件宽度
	private final int oriWidth = 600;
	// 源文件高度
	private final int oriHeight = 370;
	
	private final float rate = 0.93f;
	private final int targetLength = (int)(130*rate);
	private final int targetWidth = (int)(130*rate);
	private final int circleR = (int)(21*rate);
	//半圆缩进值
	private final int r1 = (int)(12*rate);
	
	private final int range1 = (int)(35*rate);
	private final int rangepx1 = 1;
	private final int rangepx2 = 2;
	private final int rangepx3 = 3;
	private final int range2 = (int)(76*rate);
	private final int range3 = (int)(126*rate);
	//抠图边界颜色
	private final int[] colors = {0x111111,0x111111,0x111111};
	
	
	public static SwipCaptchaBuilder getInstance() {
		return Holder.ins;
	}

	private SwipCaptchaBuilder() {
		initOriImages();
	}
	
	private static class Holder{
		private static final SwipCaptchaBuilder ins = new SwipCaptchaBuilder();
	}
	
	private void initOriImages(){
		String configFilePath = SwipCaptchaBuilder.class.getResource("/").getPath();
		configFilePath = configFilePath+ "/swipeimages/";
		try {
            File fold = new File(configFilePath);
            if(fold.exists()&&fold.isDirectory()){
            	File[] propFiles = fold.listFiles(new FileFilter() {
        			@Override
        			public boolean accept(File pathname) {
        				if (pathname.getName().endsWith("jpg"))
        					return true;
        				return false;
        			}
        		});
            	log.info("Scan to get "+propFiles.length+" img files.");
            	if(propFiles.length<1) {
            		throw new IllegalStateException("请至少准备一张滑动背景图，在Classpath目录："+configFilePath);
            	}
        		for (File propFile : propFiles) {
        			log.info("[Captcha] image:["+propFile+"] is being loaded...");
        			try {
        				oriImages.add(Files.readAllBytes(propFile.toPath()));
        			} catch (Exception th) {
        				log.warn("加在滑动背景图出错："+propFile,th);
						throw new IllegalStateException("加载滑动背景图出错，在Classpath目录："+propFile.getPath());
        			}
        		}
            }else{
				throw new IllegalStateException("请至少准备一张滑动背景图，在Classpath目录："+configFilePath);
			}
        } catch (Exception e) {
            log.warn("Failed to load file from " + configFilePath + ": " + e.getMessage(), e);
            throw new IllegalStateException(e);
        }
	}
	
	/**
	 * 生成滑动抠图块和带抠图阴影的原图
	 * 
	 */
	public SwipeCaptcha cutImage() throws IOException{
		long b = System.currentTimeMillis();
		long c = 0;
		int[][] templateV = getBlockData();
		// 最终图像
//		BufferedImage newImage = new BufferedImage(targetLength+3, oriHeight+3,BufferedImage.TYPE_INT_ARGB);
		BufferedImage newImage = new BufferedImage(targetLength+3, targetLength+3,BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = newImage.createGraphics();
		graphics.setBackground(Color.white);
		c = System.currentTimeMillis();
		log.debug("time1:{}",(c-b));

		// 随机扣图坐标点
		Random random = new Random();
		int x = random.nextInt(oriWidth - targetLength-100) + 100;
		int y = random.nextInt(oriHeight - targetWidth-5) + 5;

		byte[] oriImageByte = oriImages.get(((int)(Math.random()*100))%oriImages.size());

		InputStream is = new ByteArrayInputStream(oriImageByte);
		BufferedImage oriImag = ImageIO.read(is);
		is.close();
		b = System.currentTimeMillis();
		log.debug("time2:{}",(b-c));

		// 背景图抠图区域遮罩，抠图区域存入newImage，抠出的图边缘轮廓处理
		cutByTemplate(oriImag,newImage, templateV, x, y);

		// 获取感兴趣的目标区域
//		BufferedImage targetImageNoDeal = getTargetArea(x, y, targetLength, targetWidth, is, imagtype);

//		cutByTemplate(oriImag, templateV, newImage);


		c = System.currentTimeMillis();
		log.debug("time3:{}",(c-b));

		SwipeCaptcha sc = new SwipeCaptcha();
		sc.setPosition(new int[]{x,y});
		sc.setType("png");
		sc.setReceptType("jpeg");

//		BufferedImage dest = oriImag;
//		BufferedImage dest2 = newImage;
		BufferedImage dest = new BufferedImage(oriWidth, oriHeight, BufferedImage.TYPE_3BYTE_BGR);
		ImageBlur.simpleBlur(oriImag, dest);

//		BufferedImage dest2 = new BufferedImage(targetLength, oriHeight, BufferedImage.TYPE_INT_ARGB);
		BufferedImage dest2 = new BufferedImage(targetLength, targetLength, BufferedImage.TYPE_INT_ARGB);
		ImageBlur.simpleBlur(newImage, dest2);

		sc.setLostImage(ImageHelper.fromBufferedImage(dest2,sc.getType()));

		sc.setReceptImage(ImageHelper.fromBufferedImage2(dest,sc.getReceptType()));
		b = System.currentTimeMillis();
		log.debug("time4:{}",(b-c));
		return sc;
	}
	
	/**
	 * 根据模板图片抠图
	 */
	private void cutByTemplate(BufferedImage oriImage, int[][] templateImage,BufferedImage targetImage){
		// 源文件图像矩阵
//		int[][] oriImageData = getData(oriImage);
		// 模板图像矩阵
		int[][] block = templateImage;
		// 模板图像宽度
		for (int i = 0; i <targetLength; i++) {
			// 模板图片高度
			for (int j = 0; j < targetWidth; j++) {
				if (block[i][j] == 1) {
					targetImage.setRGB(i, j, /*oriImageData[i][j]*/oriImage.getRGB(i, j));
				}else if(block[i][j] == -1) {
					targetImage.setRGB(i, j, colors[0]);
				}else if(block[i][j] == -2) {
					targetImage.setRGB(i, j, colors[1]);
				}else if(block[i][j] == -3) {
					targetImage.setRGB(i, j, colors[2]);
				}
			}
		}
	}
	
	private void cutByTemplate(BufferedImage oriImage,BufferedImage targetImage, int[][] templateImage, int x,
			int y){
//		// 复制源文件图像矩阵
//		BufferedImage ori_copy_image = new BufferedImage(oriImage.getWidth(), oriImage.getHeight(),
//				BufferedImage.TYPE_INT_RGB);
		
		// 源文件图像矩阵
//		int[][] oriImageData = getData(oriImage);
		//抠图
//		int[][] templateImageData = templateImage;

//		// copy 源图
//		for (int i = 0; i < oriImage.getWidth(); i++) {
//			for (int j = 0; j < oriImage.getHeight(); j++) {
//				int rgb_ori = oriImage.getRGB(i, j);
//				int r = (0xff & rgb_ori);
//				int g = (0xff & (rgb_ori >> 8));
//				int b = (0xff & (rgb_ori >> 16));
//				rgb_ori = r + (g << 8) + (b << 16) + (255 << 24);
//				
//				ori_copy_image.setRGB(i, j, rgb_ori);
//			}
//		}

		for (int i = 0; i < targetLength; i++) {
			for (int j = 0; j < targetWidth; j++) {
//				int rgb = templateImage.getRGB(i, j);
				int rgb = templateImage[i][j];
				// 原图中对应位置变暗处理
				/**
				 * alpha* color1 + (1-alpha) * color2;
				 * 变暗 color2取RGB(0,0,0)，alpha从0到1越小越黑 
				 * 变亮 color2取RGB(255,255,255)alpha从0到1越小越白
				 */
				int rgb_ori = oriImage.getRGB(x + i, y + j);
				
				if (rgb == 1) {
//					targetImage.setRGB(i, y + j, rgb_ori);
					targetImage.setRGB(i, j, rgb_ori);
					int r = (int)((0xff & rgb_ori)*0.5);
					int g = (int)((0xff & (rgb_ori >> 8))*0.5);
					int b = (int)((0xff & (rgb_ori >> 16))*0.5);
					rgb_ori = r + (g << 8) + (b << 16) + (255 << 24);
					
//					rgb_ori = (int)(rgb_ori*0.9 + tt*0.1);
					oriImage.setRGB(x + i, y + j, rgb_ori);
				} else if(rgb == -1){
					int r = (int)((0xff & rgb_ori)*0.6 + 0.4*0xff);
					int g = (int)((0xff & (rgb_ori >> 8))*0.6 + 0.4*0xff);
					int b = (int)((0xff & (rgb_ori >> 16))*0.6 + 0.4*0xff);
					rgb_ori = r + (g << 8) + (b << 16) + (255 << 24);
					oriImage.setRGB(x + i, y + j,rgb_ori);
//					targetImage.setRGB(i, y + j, rgb_ori);
					targetImage.setRGB(i, j, rgb_ori);
				}else if(rgb == -2){
					int r = (int)((0xff & rgb_ori)*0.5 + 0.5*0xff);
					int g = (int)((0xff & (rgb_ori >> 8))*0.5 + 0.5*0xff);
					int b = (int)((0xff & (rgb_ori >> 16))*0.5 + 0.5*0xff);
					rgb_ori = r + (g << 8) + (b << 16) + (255 << 24);
					oriImage.setRGB(x + i, y + j,rgb_ori);
//					targetImage.setRGB(i, y + j, rgb_ori);
					targetImage.setRGB(i, j, rgb_ori);
				}else if(rgb == -3){
					int r = (int)((0xff & rgb_ori)*0.8);
					int g = (int)((0xff & (rgb_ori >> 8))*0.8);
					int b = (int)((0xff & (rgb_ori >> 16))*0.8);
					rgb_ori = r + (g << 8) + (b << 16) + (255 << 24);
					oriImage.setRGB(x + i, y + j,rgb_ori);
//					targetImage.setRGB(i, y + j, rgb_ori);
					targetImage.setRGB(i, j, rgb_ori);
				}
			}
		}
	}

	/**
	 * 生成抠图形状矩阵
	 * (11,h1)  (w1,71)  (220,h2)
	 * @return
	 * @throws Exception
	 */
	private int[][] getBlockData() {
		int[][] data = new int[targetLength][targetWidth];
		double y1 = targetWidth-circleR-4;
		double x2 = targetLength-circleR-4;
		
		double w1 = circleR + Math.random() * (targetLength-3*circleR-r1);
		double h1 = circleR + Math.random() * (targetWidth-3*circleR-r1);
		double h2 = circleR + Math.random() * (targetWidth-3*circleR-r1);
		double po = circleR*circleR;
		
		double xbegin = targetLength-circleR-r1;
		double ybegin = targetWidth-circleR-r1;
		
//		System.out.println("r1(11,"+h1+"),r2("+w1+",71),r3(220,"+h2+")");
		for (int i = 0; i < targetLength; i++) {
			for (int j = 0; j < targetWidth; j++) {
				//左边○
				double d1 = Math.pow(i - r1,2) + Math.pow(j - h1,2);
				//下○
				double d2 = Math.pow(j - y1,2) + Math.pow(i - w1,2);
				//右边○
				double d3 = Math.pow(i - x2,2) + Math.pow(j - h2,2);
				
				
				if (d1 <= po
						|| (j >= ybegin && d2 >= po)
						|| (i >= xbegin && d3 >= po)
						) {
					if(
							(po-d1>=0&&po-d1<range1)
							|| 
							(j - ybegin < rangepx1 && j - ybegin>=0 && i <= xbegin+rangepx1)||(d2 >= po&&d2-po<range1)
							|| 
							((i - xbegin < rangepx1 &&i >= xbegin &&j <= ybegin+rangepx1) || (d3 - po < range1 && d3>=po))
							) {
						data[i][j] = -1;
					}else if (

							(po-d1>=0&&po-d1<range2)
							|| 
							(j - ybegin < rangepx2 && j - ybegin>=0&& i <= xbegin+rangepx1)||(d2 >= po&&d2-po<range2)
							|| 
							((i - xbegin < rangepx2 &&i >= xbegin&&j <= ybegin+rangepx1) || (d3 - po < range2 && d3>=po))
							) {
						data[i][j] = -2;
					}else if (

							(po-d1>=0&&po-d1<range3)
							|| 
							(j - ybegin < rangepx3 && j - ybegin>=0&& i <= xbegin+rangepx1)||(d2 >= po&&d2-po<range3)
							|| 
							((i - xbegin < rangepx3 &&i >= xbegin&&j <= ybegin+rangepx1) || (d3 - po < range3 && d3>=po))
							) {
						data[i][j] = -3;
					}else {
						data[i][j] = 0;
					}
					
				}  else {
					data[i][j] = 1;
				}
			}
		}
		return data;
	}
	
	public static void main(String[] args) {




	}

}
