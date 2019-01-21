package com.pachong.demo.Service;

import com.pachong.demo.Entity.Music;
import com.pachong.demo.Entity.Params;
import com.pachong.demo.Tools.AesCBCUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class MusicService {

    @Autowired
    private AesCBCUtils aesCBCUtils;

    public String getMusic(String musicName) throws NoSuchPaddingException, IOException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, JSONException {
        Map data = new HashMap();
        Map head = new HashMap();
        String d = String.format("{\"s\":\"%s\",\"offset\":0,\"limit\":30,\"type\":\"1\",\"total\":\"true\"}", musicName);
        Params params = aesCBCUtils.getParams(d);
        Connection con = Jsoup.connect("https://music.163.com/weapi/cloudsearch/get/web?csrf_token=");
        data.put("params", params.getParams());
        data.put("encSecKey", params.getEncSecKey());
        head.put("Content-Type", "application/x-www-form-urlencoded");
        head.put("Accept","*/*");
        con.headers(head);
        con.data(data);
        Document doc = con.post();
        try {
            JSONObject json = new JSONObject(doc.getElementsByTag("body").text());
            JSONArray jsonArray = json.getJSONObject("result").getJSONArray("songs");
            JSONArray result = new JSONArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = json.getJSONObject("result").getJSONArray("songs").getJSONObject(i);
                JSONObject ob = new JSONObject();
                ob.put("musicName", jsonObject.get("name").toString());
                ob.put("musicId", jsonObject.get("id").toString());
                ob.put("singerName", jsonObject.getJSONArray("ar").getJSONObject(0).get("name").toString());
                ob.put("pictureUrl", jsonObject.getJSONObject("al").get("picUrl").toString());
                ob.put("time", jsonObject.get("dt").toString());
                result.put(ob);
            }
            return result.toString();
        } catch (Exception e){
            return "";
        }
    }

    public List<String> getLyric(String id) throws NoSuchPaddingException, IOException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, JSONException {
        Map data = new HashMap();
        Map head = new HashMap();
        String d = String.format("{\"id\":\"%s\",\"lv\":-1,\"tv\":-1,\"csrf_token\":\"\"}", id);
        Params params = aesCBCUtils.getParams(d);
        Connection con = Jsoup.connect("https://music.163.com/weapi/song/lyric?csrf_token=");
        data.put("params", params.getParams());
        data.put("encSecKey", params.getEncSecKey());
        head.put("Content-Type", "application/x-www-form-urlencoded");
        head.put("Accept","*/*");
        con.headers(head);
        con.data(data);
        Document doc = con.post();
        JSONObject json = new JSONObject(doc.getElementsByTag("body").text());
        List<String> ric = new ArrayList<>();
        try {
            String lyric = json.getJSONObject("lrc").get("lyric").toString();
            List<String> list = Arrays.asList(lyric.split("\n"));
            for (int i=0; i<list.size(); i++)
                ric.add(list.get(i).substring(list.get(i).indexOf("]") + 1));
            return ric;
        }
        catch (Exception exception) {
            ric.add("暂无歌词");
            return ric;
        }
    }

    public JSONArray getHome(String cat) throws IOException, JSONException {
        JSONArray result = new JSONArray();
        Connection con = Jsoup.connect("https://music.163.com/discover/playlist/" + cat);
        Document doc = con.get();
        Elements elements = doc.getElementById("m-pl-container").getElementsByTag("li");
        for (int i=0; i<elements.size() - 1; i++){
            Element e = elements.get(i);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("src", e.getElementsByTag("img").get(0).attr("src"));
            jsonObject.put("href", e.getElementsByTag("a").get(0).attr("href"));
            jsonObject.put("title", e.getElementsByTag("a").get(0).attr("title"));
            jsonObject.put("number", e.getElementsByClass("nb").get(0).text());
            jsonObject.put("author", e.getElementsByTag("a").get(3).text());
            result.put(jsonObject);
        }
        return result;
    }

    public List<Music> getList(String id) throws IOException, JSONException, ScriptException, NoSuchMethodException {
        Connection con = Jsoup.connect("https://music.163.com" + id);
        Document doc = con.get();
        String key1 = doc.getElementById("song-list-pre-cache").getElementsByTag("a").get(0).attr("href");
        key1 = key1.substring(9, 12);
        String key2 = doc.getElementsByClass("j-img").first().attr("data-key");
        String key = "undefined" + key2 + key1;
        String encryptedjson = doc.getElementById("song-list-pre-data").text();
        JSONArray jsonArray = new JSONArray(compile(key, encryptedjson));
        List<Music> musicList = new ArrayList<>();
        for(int i=0; i<jsonArray.length(); i++){
            Music music = new Music();
            JSONObject object = (JSONObject) jsonArray.get(i);
            music.setMusicName(object.get("name").toString());
            music.setSongerName(object.getJSONArray("ar").getJSONObject(0).get("name").toString());
            music.setPictureUrl(object.getJSONObject("al").get("picUrl").toString());
            music.setMusicId(object.getJSONObject("pv").get("id").toString());
            music.setTime(Integer.parseInt(object.get("dt").toString()));
            musicList.add(music);
        }
        return musicList;
    }

    public String compile(String key, String json) throws FileNotFoundException, ScriptException, NoSuchMethodException {
        ScriptEngine jsEngine = (new ScriptEngineManager()).getEngineByName("nashorn");
//        jsEngine.eval(new FileReader(new File(ResourceUtils.getFile("classpath:static/decode.js").getAbsolutePath())));
        jsEngine.eval(new BufferedReader(new InputStreamReader(MusicService.class.getResourceAsStream("/static/decode.js"))));
        Invocable invocableEngine = (Invocable) jsEngine;
        return  (String) invocableEngine.invokeFunction("getResult", json, key);
    }

    public JSONArray analyse(Elements es) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (int i=0; i<es.size(); i++){
            JSONObject ob = new JSONObject();
            ob.put("src", es.get(i).getElementsByTag("img").get(0).attr("src").
                    replaceAll("40y40", "150y150"));
            ob.put("href", es.get(i).getElementsByTag("a").get(1).attr("href"));
            ob.put("name", es.get(i).getElementsByTag("a").get(1).text());
            ob.put("tip", es.get(i).getElementsByTag("p").get(1).text());
            jsonArray.put(ob);
        }
        return jsonArray;
    }

    public String getTop() throws IOException, JSONException {
        Connection con = Jsoup.connect("https://music.163.com/discover/toplist");
        Document doc = con.get();
        JSONArray result = new JSONArray();
        Element element = doc.getElementsByClass("n-minelst").get(0);
        JSONObject object = new JSONObject();
        object.put("title", element.getElementsByTag("h2").get(0).text());
        object.put("detail", analyse(element.getElementsByTag("ul").get(0).getElementsByTag("li")));
        result.put(object);
        object = new JSONObject();
        object.put("title", element.getElementsByTag("h2").get(1).text());
        object.put("detail", analyse(element.getElementsByTag("ul").get(1).getElementsByTag("li")));
        result.put(object);
        return result.toString();
    }

    public List<Music> getTopList(String id) throws IOException, JSONException {
        Connection con = Jsoup.connect("https://music.163.com" + id);
        Document doc = con.get();
        JSONArray jsonArray = new JSONArray(doc.getElementById("song-list-pre-data").text());
        List<Music> musicList = new ArrayList<>();
        for(int i=0; i<jsonArray.length(); i++){
            Music music = new Music();
            JSONObject object = (JSONObject) jsonArray.get(i);
            music.setMusicName(object.get("name").toString());
            music.setSongerName(object.getJSONArray("artists").getJSONObject(0).get("name").toString());
            music.setPictureUrl(object.getJSONObject("album").get("picUrl").toString());
            music.setMusicId(object.get("id").toString());
            music.setTime(Integer.parseInt(object.get("duration").toString()));
            musicList.add(music);
        }
        return musicList;
    }
}
