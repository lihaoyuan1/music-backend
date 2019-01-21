package com.pachong.demo.Controller;

import com.pachong.demo.Entity.Music;
import com.pachong.demo.Service.MusicService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.script.ScriptException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping(value = "/music")
@CrossOrigin(allowCredentials = "true")
public class Controller {

    @Autowired
    private MusicService musicService;

    @PostMapping(value = "/search")
    public String search(@RequestParam("musicName") String musicName) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IOException, JSONException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        return musicService.getMusic(musicName);
    }

    @PostMapping(value = "/lyric")
    public List<String> lyric(@RequestParam("id") String id) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IOException, JSONException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        return musicService.getLyric(id);
    }

    @GetMapping(value = "/home")
    public String home() throws IOException, JSONException {
        return musicService.getHome("").toString();
    }

    @GetMapping(value = "/cat")
    public String cat(@RequestParam("cat") String cat) throws IOException, JSONException {
        return musicService.getHome("?cat=" + cat).toString();
    }

    @GetMapping(value = "/playList")
    public List<Music> getList(@RequestParam("id") String id) throws IOException, JSONException, ScriptException, NoSuchMethodException {
        return musicService.getList(id);
    }

    @GetMapping(value = "/nextPage")
    public String nextPage(@RequestParam("cat") String cat, @RequestParam("offset") String offset) throws IOException, JSONException {
        return musicService.getHome("?cat=" + cat + "&limit=35&offset=" + offset).toString();
    }

    @GetMapping(value = "/getTop")
    public String getTop() throws IOException, JSONException {
        return musicService.getTop();
    }

    @GetMapping(value = "/getTopList")
    public List<Music> getTopList(@RequestParam("id") String id) throws IOException, JSONException {
        return musicService.getTopList(id);
    }
}
