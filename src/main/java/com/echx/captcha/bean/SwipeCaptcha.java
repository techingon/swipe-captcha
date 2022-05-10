package com.echx.captcha.bean;

import java.io.Serializable;

public class SwipeCaptcha implements Serializable{

	/**
	 * 扣图后的图片
	 */
	private byte[] receptImage;

	/**
	 * 扣出来的图片
	 */
	private byte[] lostImage;
	
	/**
	 * [x,y]
	 */
	private int[] position;
	
	private String type;
	private String ReceptType;

	public byte[] getReceptImage() {
		return receptImage;
	}

	public void setReceptImage(byte[] receptImage) {
		this.receptImage = receptImage;
	}

	public byte[] getLostImage() {
		return lostImage;
	}

	public void setLostImage(byte[] lostImage) {
		this.lostImage = lostImage;
	}

	public int[] getPosition() {
		return position;
	}

	public void setPosition(int[] position) {
		this.position = position;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getReceptType() {
		return ReceptType;
	}

	public void setReceptType(String receptType) {
		ReceptType = receptType;
	}
	
}
