package com.wy.event;

import java.util.concurrent.atomic.AtomicInteger;

public class Event {

    private String textPoint;

    private int triggerLevel;

    private AtomicInteger current;

    private EventExecutor executor;

    public Event(String textPoint, int triggerLevel, EventExecutor ee) {
        this.textPoint = textPoint;
        this.triggerLevel = triggerLevel;
        this.executor = ee;
        this.current = new AtomicInteger(0);
    }

    public void receive(String message, MessageEventManager manager) {
        if (textPoint.equals("进") && current.incrementAndGet() >= triggerLevel) {
            executor.execute(manager);
            current = new AtomicInteger(0);
        }
    }

    public void reset() {
        current = new AtomicInteger(0);
    }

    public String getTextPoint() {
        return textPoint;
    }

    public int getCurrent() {
        return current.get();
    }
}
