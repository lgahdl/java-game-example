package com.histudio.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.histudio.main.Game;

public class Npc extends Entity {

	public List<String> phrases;

	public Npc(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		phrases = new ArrayList<String>();
		phrases.add("Welcome to the HellLand");
		phrases.add("Please, kill the demons");
	}

	public int animPhraseIndex = 0, phraseIndex = 0, animTime = 0, animMaxTime = 5;

	public boolean dialogue = false;

	public void tick() {
		int xPlayer = Game.player.getX();
		int yPlayer = Game.player.getY();
		if (Math.abs(xPlayer - this.getX()) < 20 && Math.abs(yPlayer - this.getY()) < 20) {
			if(!Game.ui.dialogueClosed) {
				dialogue=true;
				Game.ui.dialogue=true;				
			}
		} else {
			dialogue=false;
			Game.ui.dialogue=false;
			Game.ui.dialogueClosed=false;
		}
	}

	public void render(Graphics g) {
		super.render(g);
		if (dialogue) {
			Game.ui.dialoguePhrases = this.phrases;
			Game.ui.renderDialogue(g);
		}
	}

}
