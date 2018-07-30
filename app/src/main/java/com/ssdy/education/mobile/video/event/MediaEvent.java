package com.ssdy.education.mobile.video.event;

/**
 * 描述：图片预览相关参数传递
 * 作者：shaobing
 * 时间： 2017/2/22 09:43
 */
public class MediaEvent {

    //视频链接
    private String url;
    //  0 录制完成之后预览（没有删除） 1.点击确认之后预览     2.提交老师没有批改预览   3.提交之后老师已经批改预览（习题的界面，与任务资源改分A+等分数区分开）
    //  4.老师资源预览 5.提交之后老师已经批改预览（任务资源改分A+等分数） 6.微课批回重做（重做理由放comment位置）
    private int state;
    //老师的语音评论链接   1.链接为空代表没有评论     如果不为空则代表有语音评论
    private String music_url;
    //文字评论，和语音品论只能出现一个。
    private String comment;
    //视频分数分数
    private String score;
    //图片是否点赞
    private String isPraise;

    public MediaEvent(String url, int state, String music_url, String comment, String score, String isPraise){
        this.url =url;
        this.state =state;
        this.music_url = music_url;
        this.comment = comment;
        this.score = score;
        this.isPraise = isPraise;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getMusic_url() {
        return music_url;
    }

    public void setMusic_url(String music_url) {
        this.music_url = music_url;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String isPraise() {
        return isPraise;
    }

    public void setPraise(String praise) {
        isPraise = praise;
    }
}
