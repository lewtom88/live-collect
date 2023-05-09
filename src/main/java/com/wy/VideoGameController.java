package com.wy;

import com.wy.model.Result;
import com.wy.service.LiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@CrossOrigin
public class VideoGameController {

    @Autowired
    private LiveService liveService;

    @RequestMapping("/get_video_comments")
    public Result<Map<String, Integer>> getVideoComments() {
        Result<Map<String, Integer>> r = new Result<>();
        Map<String, Integer> videoComments = liveService.getVideoComments();
        r.setData(videoComments);

        return r;
    }

    @RequestMapping("/video_turn_off")
    public Result<Boolean> turnOff() {
        liveService.turnOffVideo();
        Result<Boolean> r = new Result<>();
        r.setData(false);

        return r;
    }

    @RequestMapping("/video_turn_on")
    public Result<Boolean> turnOn() {
        liveService.turnOnVideo();
        Result<Boolean> r = new Result<>();
        r.setData(true);

        return r;
    }
}
