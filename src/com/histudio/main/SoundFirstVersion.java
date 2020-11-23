package com.histudio.main;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundFirstVersion {

	private File file;
	private Clip clip;

	public static final SoundFirstVersion musicBackground = new SoundFirstVersion("res/music.wav");
	public static final SoundFirstVersion playerDamaged = new SoundFirstVersion("res/damaged2.wav");
	public static final SoundFirstVersion enemyDamaged = new SoundFirstVersion("res/damaged.wav");
	public static final SoundFirstVersion playerJump = new SoundFirstVersion("res/jump.wav");

	public SoundFirstVersion(String filename) {
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
