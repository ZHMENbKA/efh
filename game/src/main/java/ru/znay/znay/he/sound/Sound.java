package ru.znay.znay.he.sound;

import ru.znay.znay.he.Game;
import ru.znay.znay.he.cfg.Constants;

import java.applet.Applet;
import java.applet.AudioClip;

public class Sound {
    public static final Sound backMusic = new Sound("/sound/111.mid");
    public static final Sound pickup = new Sound("/sound/pickup.wav");
    public static final Sound playerHurt = new Sound("/sound/playerhurt.wav");
    public static final Sound monsterHurt = new Sound("/sound/monsterhurt.wav");
    public static final Sound hit = new Sound("/sound/hit.wav");
    public static final Sound startGame = new Sound("/sound/startgame.wav");
    public static final Sound takeEquip = new Sound("/sound/questbegin.wav");

    private AudioClip clip;

    public void init() {

    }

    private Sound(String name) {
        try {
            clip = Applet.newAudioClip(Sound.class.getResource(name));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void play() {
        if (!Game.soundOn) return;
        try {
            new Thread() {
                public void run() {
                    clip.play();
                }
            }.start();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void loop() {
        if (!Game.soundOn) return;
        try {
            new Thread() {
                public void run() {
                    clip.loop();
                }
            }.start();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            new Thread() {
                public void run() {
                    clip.stop();
                }
            }.start();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}