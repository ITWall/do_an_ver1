package model;

import java.util.Timer;

public class TimerManagement {
    private Timer timer;
    private int interval;

    public TimerManagement(Timer timer, int interval) {
        this.timer = timer;
        this.interval = interval;
    }

    public TimerManagement(Timer timer) {
        this.timer = timer;
        this.interval = 16;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
}
