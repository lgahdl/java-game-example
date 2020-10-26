package com.histudio.main;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound {

	private File file;
	private Clip clip;

	public static final Sound musicBackground = new Sound("res/music.wav");
	public static final Sound playerDamaged = new Sound("res/damaged2.wav");
	public static final Sound enemyDamaged = new Sound("res/damaged.wav");
	public static final Sound playerJump = new Sound("res/jump.wav");

	public Sound(String filename) {
		try {
			file = new File(filename);
			if (file.exists()) {

			} else {
				System.out.println("FILEPATH " + filename + " DOES NOT EXISTS");
			}
			clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(file));
		} catch (Throwable e) {

		}
	}

	public void play() {
		try {
			new Thread() {
				public void run() {
					clip.loop(1);
				}
			}.start();
		} catch (Throwable e) {

		}
	}

	public void loop() {
		try {
			new Thread() {
				public void run() {
					try {
						clip.loop(Clip.LOOP_CONTINUOUSLY);
					} catch (Exception e) {
						System.out.println(e);
					}
				}
			}.start();
		} catch (Throwable e) {

		}
	}

}
