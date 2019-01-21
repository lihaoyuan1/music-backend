package com.pachong.demo.Entity;

public class Music {

    private String musicName;

    private String musicId;

    private String singerName;

    private String pictureUrl;

    private Integer time;

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getMusicId() {
        return musicId;
    }

    public void setMusicId(String musicId) {
        this.musicId = musicId;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSongerName(String singerName) {
        this.singerName = singerName;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Music{" +
                "musicName='" + musicName + '\'' +
                ", musicId='" + musicId + '\'' +
                ", songerName='" + singerName + '\'' +
                ", pictureUrl='" + pictureUrl + '\'' +
                ", time=" + time +
                '}';
    }
}
