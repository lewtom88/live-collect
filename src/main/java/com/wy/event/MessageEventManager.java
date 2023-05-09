package com.wy.event;

import com.wy.event.video.VideoDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class MessageEventManager {

    private List<Event> events;

    private AtomicBoolean eventSwitch;

    private VideoDriver videoDriver;

    public MessageEventManager() {
        eventSwitch = new AtomicBoolean(false);
        events = new ArrayList<>();
        videoDriver = new VideoDriver();

        events.add(new Event("进", 8, (m) -> {
            videoDriver.forward();
            m.resetEvents();
        }));
        events.add(new Event("退", 8, (m) -> {
            videoDriver.backward();
            m.resetEvents();
        }));
        events.add(new Event("赞", 8, (m) -> {
            videoDriver.like();
        }));
    }

    public void receive(String message) {
        if (!eventSwitch.get()) {
            return;
        }

        for (Event event : events) {
            event.receive(message, this);
        }
    }

    private void resetEvents() {
        for (Event event : events) {
            event.reset();
        }
    }

    public Map<String, Integer> getVideoComments() {
        return events.stream().collect(
                Collectors.toMap(Event::getTextPoint, Event::getCurrent));
    }

    public void turnOff() {
        eventSwitch.set(false);
        resetEvents();
    }

    public void turnOn() {
        resetEvents();
        eventSwitch.set(true);
    }
}
