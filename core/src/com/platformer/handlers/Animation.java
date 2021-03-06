package com.platformer.handlers;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import javax.xml.soap.Text;

public class Animation {

    private float time;
    private float delay;
    private int currentFrame;
    private int timesPlayed;
    public boolean shouldReset = false;

    TextureRegion[] frames;

    public Animation() {}

    public Animation (TextureRegion[] frames){
        this(frames, 1/12f);
    }

    public Animation(TextureRegion[] frames, float delay) {
        setFrames(frames, delay);
    }

    public void setFrames(TextureRegion[] frames, float delay) {
        timesPlayed = 0;
        this.frames = frames;
        this.delay = delay;
        time = 0;
        currentFrame = 0;
    }

    public void update(float dt) {
        if(delay <= 0) return;
        time += dt;
        while(time >=delay) {
            step();
        }
    }

    public void step() {
        time-=delay;
        currentFrame++;
        if(currentFrame == frames.length) {
            currentFrame = 0;
            timesPlayed++;
        }
    }

    public TextureRegion getFrame() {
        return frames[currentFrame];
    }
    public int getTimesPlayed(){ return timesPlayed; }
}
