package com.echx.captcha.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class ImageBlur {
    private static Logger log = LoggerFactory.getLogger(ImageBlur.class);
	
	static float[] matrix = {
	        0.111f, 0.111f, 0.111f, 
	        0.111f, 0.111f, 0.111f, 
	        0.111f, 0.111f, 0.111f, 
	    };
	
	static float[] matrix4 = {
	        0.25f, 0.25f, 
	        0.25f, 0.25f, 
	    };
	
	static BufferedImageOp op = new ConvolveOp( new Kernel(3, 3, matrix) );
	
	
	public static void simpleBlur(BufferedImage src,BufferedImage dest) {
//		    BufferedImageOp op = getGaussianBlurFilter(2,false);
            long a = System.currentTimeMillis();
		    op.filter(src, dest);
            long b = System.currentTimeMillis();
            log.debug("time4-0:{}",(b-a));
	}
	
	
	public static ConvolveOp getGaussianBlurFilter(int radius,
            boolean horizontal) {
        if (radius < 1) {
            throw new IllegalArgumentException("Radius must be >= 1");
        }
        
        int size = radius * 2 + 1;
        float[] data = new float[size];
        
        float sigma = radius / 3.0f;
        float twoSigmaSquare = 2.0f * sigma * sigma;
        float sigmaRoot = (float) Math.sqrt(twoSigmaSquare * Math.PI);
        float total = 0.0f;
        
        for (int i = -radius; i <= radius; i++) {
            float distance = i * i;
            int index = i + radius;
            data[index] = (float) Math.exp(-distance / twoSigmaSquare) / sigmaRoot;
            total += data[index];
        }
        
        for (int i = 0; i < data.length; i++) {
            data[i] /= total;
        }        
        
        Kernel kernel = null;
        if (horizontal) {
            kernel = new Kernel(size, 1, data);
        } else {
            kernel = new Kernel(1, size, data);
        }
        return new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
    }

}
