package com.ssdy.education.mobile.video.event;


/**
 * Created by yanshu on 2017/2/16.
 */

public class HomeworkEvent {

    public final int code;
    //code 代表什么情况下发过来的消息
    //1 : 首页传，作业详情界面
    public final static int CODE1 = 1;
    //2 ：详情页传到每个任务的fragment
    public final static int CODE2 = 2;
    //3 ：照片完成返回
    public final static int CODE3 = 3;
    //4 ：录像完成返回
    public final static int CODE4 = 4;
    //5 ：录音完成返回
    public final static int CODE5 = 5;
    //6 ：习题返回
    public final static int CODE6 = 6;
    //7 ：拍照裁剪完返回
    public final static int CODE7 = 7;
    //8 : 通知首页通知是否保留提交按钮（是否语音作业）
    public final static int CODE8 = 8;

    //9 : 提交反馈，刷新首页
    public final static int CODE9 = 9;
    //10: homeworkDetailActivity提交按钮处理交给TaskAllFragment
    public final static int CODE10 =10;

    //任务相关
    public String taskContext;
    public String completeType;

    //判断拍照 录像 录音结果是否保留
    public boolean isSave;
    //录音时间
    public int duration;
    //文件地址（分辨是哪一个normalfragment的文件）
    public String filePath;

    //实现在HomeworkDetailActivity判断，是否显示语音提交按钮
    public Boolean isVisible;
    //作业状态，实现在HomeworkDetailActivity判断，语音提交按钮是否可点击
    public Boolean isClickable;



    //详情页传到每个任务的fragment
    public HomeworkEvent(int code, String taskContext, String completeType) {
        this.code = code;
        this.taskContext = taskContext;
        this.completeType = completeType;
    }

    //详情页传到每个任务的fragment
    public HomeworkEvent(int code, boolean isSave, int duration, String filePath) {
        this.code = code;
        this.isSave = isSave;
        this.duration = duration;
        this.filePath = filePath;
    }

    //通知首页通知是否保留提交按钮（是否语音作业）
    public HomeworkEvent(int code, boolean isVisible, boolean isClickable) {
        this.code = code;
        this.isVisible = isVisible;
        this.isClickable = isClickable;
    }

    //9 : 提交反馈，刷新首页
    public HomeworkEvent(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "HomeworkEvent{" +
                "code=" + code +
                ", taskContext='" + taskContext + '\'' +
                ", completeType='" + completeType + '\'' +
                ", isSave=" + isSave +
                ", duration=" + duration +
                ", filePath='" + filePath + '\'' +
                ", isVisible=" + isVisible +
                ", isClickable=" + isClickable +
                '}';
    }
}
