package com.wy.event.video;

public class VideoCommonDriver extends VideoDriver {

    @Override
    public String getPythonCommand() {
        return "python3";
    }

    @Override
    public String getScriptPath() {
        return this.getClass().getClassLoader().getResource("py_scripts").getPath();
    }

}
