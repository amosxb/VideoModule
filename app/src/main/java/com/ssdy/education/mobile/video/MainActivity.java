package com.ssdy.education.mobile.video;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ssdy.education.mobile.video.ui.VideoActivity;
import com.ssdy.education.mobile.video.utils.ToastUtil;

import java.io.File;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private File mFile;
    //RequestCode
    private static final int REQUESTCODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFile = new File(Environment.getExternalStorageDirectory() + File.separator + UUID.randomUUID() + ".mp4");
                Intent intent = new Intent(MainActivity.this, VideoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(VideoActivity.FILEPATH, mFile.getAbsolutePath());
                intent.putExtras(bundle);
                startActivityForResult(intent, REQUESTCODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUESTCODE) {
                ToastUtil.showLongToast(MainActivity.this, "视频拍摄成功!!");
            }
        }
    }
}
