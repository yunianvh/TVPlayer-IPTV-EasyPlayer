# TVPlayer-IPTV-EasyPlayer|直播软件|IPTV直播软件|电视直播|友窝直播|超级直播软件定制开发
### 文章内容
<hr/>

 1. 功能简介
 2. 核心代码
 3. 秒开优化
 4. 换台简介
 5. 文章总结
 6. Demo地址

话说这是一款底层基于ffmpeg的播放器，俗称EasyPlayer、Ijkplayer、ExoPlayer、JiaoZiVideoPlayer等等，支持MMS, RTSP, RTMP, HLS(m3u8) 等常见的频流媒体协议；支持MKV，FLV，MP4，MOV，TS，RMVB等常见视频格式格式；

### 功能简介
<hr/>
我们又不是大佬，我们挑简单来说，话说一款直播软件的基本要素、、、唉唉，挑简单的说、、、、、、说什么说，就是实现一款支持各种流媒体协议并且同时支持各种视频格式的播放器罢了嘛；<br/><br/>
还不明白？不要慌，我们有参照物，对于电视端来说如友窝直播、超级直播、电视直播等等类似的IPTV直播软件；那么对于手机直播来说文章仅仅介绍的是拉流端，熟称播放端，那具体实现一整套手机直播系统如何解决呢？不慌，联系我，我是最简单的[玉念聿辉](https://blog.csdn.net/qq_35350654)；<br/>

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190425133555835.gif)

### 核心代码
<hr/>

**xml定义**

```
			<org.easydarwin.easyplayer.views.ProVideoView
                android:id="@+id/video_view"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:keepScreenOn="true" />
```
**ProVideoView引用**

```
	private ActivityMainProBinding mBinding;
	private TextView TextView;
	private View mProgress;
	private ProVideoView mVideoView;
	private String[] url = {"http://jzvd.nathen.cn/342a5f7ef6124a4a8faf00e738b8bee4/cf6d9db0bd4d41f59d09ea0a81e918fd-5287d2089db37e62345123a1be272f8b.mp4", "rtmp://218.38.152.69:1935/da_live/72136989/mp4:ch001"};
	 
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
       		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main_pro);

			 // init player
			IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
	        ijkMediaPlayer.loadLibrariesOnce(null);	
	        if (BuildConfig.DEBUG) {
	            ijkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);
	        }
        
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

        TextView = findViewById(R.id.tv_time);
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
```
完了、完了完了完了
### 秒开优化
<hr/>

秒开是什么鬼，就是换台的时候快一点呗，管他那么多，能设置的都给添上去，完了重新运行、对比，嗯、、、、、、完全看不出来，但你还是添加上去就是了；
```
		//丢帧  是在视频帧处理不过来的时候丢弃一些帧达到同步的效果
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 5);
        //设置是否开启环路过滤: 0开启，画面质量高，解码开销大，48关闭，画面质量差点，解码开销小
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 0);
        //播放延时的解决方案，设置播放前的探测时间 1,达到首屏秒开效果
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", 1);
        //如果是rtsp协议，可以优先用tcp(默认是用udp)
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_transport", "tcp");
        ijkMediaPlayer.setOption(1, "analyzemaxduration", 100L);
        ijkMediaPlayer.setOption(1, "flush_packets", 1L);
        //需要准备好后自动播放
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 1);
        //不额外优化
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "fast", 1);
        //是否开启预缓冲，一般直播项目会开启，达到秒开的效果，不过带来了播放丢帧卡顿的体验
        ijkMediaPlayer.setOption(4, "packet-buffering", 0);
        //自动旋屏
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 0);
        //处理分辨率变化
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 0);
        //最大缓冲大小,单位kb
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "max-buffer-size", 0);
        //默认最小帧数2
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "min-frames", 2);
        //最大缓存时长
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max_cached_duration", 3);
        //是否限制输入缓存数
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "infbuf", 1);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "nobuffer");
        //播放前的探测Size，默认是1M, 改小一点会出画面更快
        ijkMediaPlayer.setOption(1, "probesize", 200);
        //播放重连次数
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "reconnect", 5);
        //清空DNS
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1);
```
### 换台简介
<hr/>

这个挺有意思，我看了网上几份Ijkplayer的代码，换台既然是自杀式Activity切换，运行出来的效果就是每当换台眼前一黑，然后从新打开了一个新的世界，本人对生命还是热爱有加的，看看下面；

```
	/**
     * 换台
     */
    public void player() {
        TextView.setText("正在播放：" + url[i]);
        mProgress.setVisibility(View.VISIBLE);
        mVideoView.setVideoPath(url[i]);
        mVideoView.toggleRender();
    }
```
### 文章总结
<hr/>

没太多想法，只想能有一篇点击过万的文章，当然不一定是这篇，也不知道是我自私还是太菜了，写出来的文章总不对大家的胃口，但最近看到一句话我觉得还是挺有意思的，就用它来结尾吧！<br/>
以前我总是期望别人能帮助我一把，然后再给予他们翻倍的回报，但往往都以失望而终，但最近我试着去帮助别人，才突然发现帮助我的人也越来越多； 
### Demo地址
<hr/>

Github：https://github.com/Life1412378121/TVPlayer-IPTV-EasyPlayer.git<br/>
CSDN：https://download.csdn.net/download/qq_35350654/11143538
