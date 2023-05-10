package com.wy.event.video;

public class VideoWinDriver extends VideoDriver {
    @Override
    public String getPythonCommand() {
        return "py";
    }

    @Override
    public String getScriptPath() {
        String path = this.getClass().getClassLoader().getResource("py_scripts").getPath();
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return path;
    }
}
