package com.wy.event.video;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VideoDriver {

    Logger logger = LoggerFactory.getLogger(VideoDriver.class);

    private ProcessBuilder upBuilder;

    private ProcessBuilder downBuilder;

    private ProcessBuilder likeBuilder;

    public VideoDriver() {
        upBuilder = new ProcessBuilder("python3", "./py_scripts/up.py");
        downBuilder = new ProcessBuilder("python3", "./py_scripts/down.py");
        likeBuilder = new ProcessBuilder("python3", "./py_scripts/like.py");
    }

    public void forward() {
        try {
            Process process = downBuilder.start();
            process.waitFor();
        } catch (Exception e) {
            logger.error("Failed to execute python.", e);
        }
    }

    public void backward() {
        try {
            Process process = upBuilder.start();
            process.waitFor();
        } catch (Exception e) {
            logger.error("Failed to execute python.", e);
        }
    }

    public void like() {
        try {
            Process process = likeBuilder.start();
            process.waitFor();
        } catch (Exception e) {
            logger.error("Failed to execute python.", e);
        }
    }

    public static void main(String[] args) throws Exception {
        VideoDriver driver = new VideoDriver();
        for (int i = 0; i < 10000; i++) {
            System.out.println("execute forward command...");
            driver.backward();
            Thread.sleep(2000);
        }
    }
}
