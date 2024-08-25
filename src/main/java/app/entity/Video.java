package app.entity;

import java.math.BigInteger;

public class Video {
    private String imageLink = "";
    private String titleVideo = "";
    private String channelName = "";
    private BigInteger likes = BigInteger.valueOf(0);
    private BigInteger comments = BigInteger.valueOf(0);
    private BigInteger views = BigInteger.valueOf(0);

    public BigInteger getComments() {
        return comments;
    }

    public void setComments(BigInteger comments) {
        this.comments = comments;
    }

    public BigInteger getViews() {
        return views;
    }

    public void setViews(BigInteger views) {
        this.views = views;
    }

    public BigInteger getLikes() {
        return likes;
    }

    public void setLikes(BigInteger likes) {
        this.likes = likes;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String descriptionVideo) {
        this.channelName = descriptionVideo;
    }

    public String getTitleVideo() {
        return titleVideo;
    }

    public void setTitleVideo(String titleVideo) {
        this.titleVideo = titleVideo;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }


}
