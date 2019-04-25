package org.easydarwin.easyplayer;/*
 * Copyright (C) 2015 Zhang Rui <bbcallen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


import org.easydarwin.easyplayer.views.ProVideoView;
import org.esaydarwin.rtsp.player.BuildConfig;
import org.esaydarwin.rtsp.player.R;
import org.esaydarwin.rtsp.player.databinding.ActivityMainProBinding;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

import android.content.pm.ActivityInfo;
import android.support.v4.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ProVideoActivity extends AppCompatActivity {

    private static final String TAG = "ProVideoActivity";

    //"rtsp://10.9.0.106:554/user=admin&password=abc-123&channel=0&stream=0.sdp?", "rtsp://10.9.0.104:554/user=admin&password=abc-123&channel=0&stream=0.sdp?",
    //private String[] url = {"rtmp://218.38.152.69:1935/da_live/72136989/mp4:ch001", "rtmp://media3.sinovision.net:1935/live/livestream", "rtp://239.20.0.104:2006", "rtp://239.20.0.203:2204", "rtp://239.20.0.197:2192", "rtp://239.20.0.198:2194", "rtp://239.20.0.199:2196", "rtp://239.10.0.210:1025"};
    private String[] url = {"http://jzvd.nathen.cn/342a5f7ef6124a4a8faf00e738b8bee4/cf6d9db0bd4d41f59d09ea0a81e918fd-5287d2089db37e62345123a1be272f8b.mp4", "rtmp://218.38.152.69:1935/da_live/72136989/mp4:ch001", "rtmp://media3.sinovision.net:1935/live/livestream", "rtsp://10.39.73.40:554/PLTV/88888898/224/3221226379/10000100000000060000000002231939_0.smil", "rtsp://10.39.72.37:554/PLTV/88888888/224/3221226254/10000100000000060000000001754363_0.smil", "rtsp://10.39.73.40:554/PLTV/88888898/224/3221226406/10000100000000060000000003794850_0.smil", "rtsp://10.255.23.89:554/PLTV/88888888/224/3221226227/10000100000000060000000001359680_0.smil", "rtsp://10.255.23.92:554/PLTV/88888888/224/3221226257/10000100000000060000000001762841_0.smil", "rtsp://121.58.54.138:554/PLTV/88888888/224/3221226258/10000100000000060000000001771358_0.smi", "rtsp://121.58.54.137:554/PLTV/88888888/224/3221226259/10000100000000060000000001771360_0.smil", "rtsp://10.255.23.86:554/PLTV/88888888/224/3221226261/10000100000000060000000001771362_0.smil", "rtsp://121.58.54.136:554/PLTV/88888888/224/3221226260/10000100000000060000000001771361_0.smil", "rtsp://10.39.73.39:554/PLTV/88888898/224/3221226386/10000100000000060000000002232252_0.smil"};

    private ProVideoView mVideoView;
    private View mProgress;
    private ActivityMainProBinding mBinding;

    public static final int REQUEST_WRITE_STORAGE = 111;
    /**
     * 获取当前时间int
     *
     * @return
     */
    public static final SimpleDateFormat INT_DATE_FORMAT_DATE = new SimpleDateFormat(
            "yyyyMMdd");

    public static int getLocalTimeInInt() {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            String format = INT_DATE_FORMAT_DATE.format(calendar.getTime());
            if (!TextUtils.isEmpty(format)) {
                return Integer.parseInt(format);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main_pro);

        //超时不能使用
        if (getLocalTimeInInt() > 20190430)
            System.exit(0);

        // init player
        IjkMediaPlayer.loadLibrariesOnce(null);

        if (BuildConfig.DEBUG) {
            IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);
        }

        IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
      /*  ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 5);
        //某些视频在SeekTo的时候，会跳回到拖动前的位置，这是因为视频的关键帧的问题，通俗一点就是FFMPEG不兼容，视频压缩过于厉害，seek只支持关键帧，出现这个情况就是原始的视频文件中i 帧比较少
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
        //播放前的探测Size，默认是1M, 改小一点会出画面更快
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 1024 * 10);
        //打开秒开
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 1);
        //如果是rtsp协议，可以优先用tcp(默认是用udp)
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_transport", "tcp");
        //0为软解,1为硬解
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "videotoolbox", 0);*/


       /* //rtsp设置 https://ffmpeg.org/ffmpeg-protocols.html#rtsp
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_transport", "tcp");
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_flags", "prefer_tcp");
        //根据媒体类型来配置
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "allowed_media_types", "video");
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "timeout", 20000);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "buffer_size", 1316);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "infbuf", 1);  // 无限读
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzemaxduration", 100L);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 10240L);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flush_packets", 1L);
        //  关闭播放器缓冲，这个必须关闭，否则会出现播放一段时间后，一直卡主，控制台打印 FFP_MSG_BUFFERING_START
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0L);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1L);*/


        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 5);   //丢帧  是在视频帧处理不过来的时候丢弃一些帧达到同步的效果
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 0);  //设置是否开启环路过滤: 0开启，画面质量高，解码开销大，48关闭，画面质量差点，解码开销小
        //播放延时的解决方案
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", 1);//设置播放前的探测时间 1,达到首屏秒开效果
        //如果是rtsp协议，可以优先用tcp(默认是用udp)
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_transport", "tcp");
        ijkMediaPlayer.setOption(1, "analyzemaxduration", 100L);
        ijkMediaPlayer.setOption(1, "flush_packets", 1L);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 1);   //需要准备好后自动播放
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "fast", 1);//不额外优化
        ijkMediaPlayer.setOption(4, "packet-buffering", 0);  //是否开启预缓冲，一般直播项目会开启，达到秒开的效果，不过带来了播放丢帧卡顿的体验
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 0);  //自动旋屏
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 0);   //处理分辨率变化
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "max-buffer-size", 0);//最大缓冲大小,单位kb
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "min-frames", 2);   //默认最小帧数2
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max_cached_duration", 3);   //最大缓存时长
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "infbuf", 1);   //是否限制输入缓存数
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "nobuffer");
        ijkMediaPlayer.setOption(1, "probesize", 200);  //播放前的探测Size，默认是1M, 改小一点会出画面更快
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "reconnect", 5);  //播放重连次数
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1);//清空DNS

        mVideoView = mBinding.videoView;
        mProgress = findViewById(android.R.id.progress);
        mVideoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int arg1, int arg2) {
                switch (arg1) {
                    case IMediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                        //mTextView.append("\nMEDIA_INFO_VIDEO_TRACK_LAGGING");
                        break;
                    case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        //mTextView.append("\nMEDIA_INFO_VIDEO_RENDERING_START");
                        mProgress.setVisibility(View.GONE);
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        //mTextView.append("\nMEDIA_INFO_BUFFERING_START");
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        //mTextView.append("\nMEDIA_INFO_BUFFERING_END");
                        break;
                    case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
                        //mTextView.append("\nMEDIA_INFO_NETWORK_BANDWIDTH: " + arg2);
                        break;
                    case IMediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                        //mTextView.append("\nMEDIA_INFO_BAD_INTERLEAVING");
                        break;
                    case IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                        //mTextView.append("\nMEDIA_INFO_NOT_SEEKABLE");
                        break;
                    case IMediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                        //mTextView.append("\nMEDIA_INFO_METADATA_UPDATE");
                        break;
                    case IMediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                        //mTextView.append("\nMEDIA_INFO_UNSUPPORTED_SUBTITLE");
                        break;
                    case IMediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                        //mTextView.append("\nMEDIA_INFO_SUBTITLE_TIMED_OUT");
                        break;
                    case IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                        break;
                    case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                        //mTextView.append("\nMEDIA_INFO_AUDIO_RENDERING_START");
                        break;
                }

                return false;
            }
        });
/**
 * 播放错误
 */
        mVideoView.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                mProgress.setVisibility(View.GONE);
                Toast.makeText(ProVideoActivity.this, "播放错误", Toast.LENGTH_SHORT).show();
                mVideoView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mVideoView.reStart();
                    }
                }, 5000);
                return true;
            }
        });
/**
 * 播放完成
 */
        mVideoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                mProgress.setVisibility(View.GONE);
                Toast.makeText(ProVideoActivity.this, "播放完成", Toast.LENGTH_SHORT).show();
            }
        });

        TextView TextView = findViewById(R.id.tv_time);
        TextView.setText("正在播放：" + url[i]);

        if (url[i] != null)
            mVideoView.setVideoPath(url[i]);
        else {
            Log.e(TAG, "Null Data Source\n");
            finish();
            return;
        }
        mVideoView.start();
    }

    /**
     * 换台
     */
    public void player() {
        mProgress.setVisibility(View.VISIBLE);
        mVideoView.setVideoPath(url[i]);
        mVideoView.toggleRender();
    }


    int i = 0;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        final boolean uniqueDown = event.getRepeatCount() == 0
                && event.getAction() == KeyEvent.ACTION_DOWN;

        // 上键上一页
        if (uniqueDown && keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {

            i--;
            if (i < 0)
                i = url.length - 1;
            player();
            return true;
        }

        // 按右键下一页
        else if (uniqueDown && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            i++;
            if (i > url.length)
                i = 0;
            player();
            return true;

        }
        return super.dispatchKeyEvent(event);
    }


    @Override
    protected void onStop() {
        mVideoView.stopPlayback();
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public boolean isLandscape() {
        int orientation = getResources().getConfiguration().orientation;
        return orientation == ORIENTATION_LANDSCAPE;
    }

    public void onChangeOritation(View view) {
        setRequestedOrientation(isLandscape() ?
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    public void onTakePicture(View view) {
//        if (mVideoView.isInPlaybackState()) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
//            } else {
//                doTakePicture();
//            }
//        }
    }

    public void onChangePlayMode(View view) {
        int mMode = mVideoView.toggleAspectRatio();
    }
}
