package com.sports.unity.messages.controller.viewhelper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sports.unity.Database.DBUtil;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.messages.controller.activity.ChatScreenAdapter;
import com.sports.unity.messages.controller.model.Message;
import com.sports.unity.util.ActivityActionHandler;
import com.sports.unity.util.ActivityActionListener;
import com.sports.unity.util.ThreadTask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by madmachines on 27/11/15.
 */
public class AudioRecordingHelper {

    private static final int DURATION = 1000;

    private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
    private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
    private static final String AUDIO_RECORDER_FOLDER = "SportsUnityAudioRecorder";

    private int currentFormat = 0;
    private int output_formats[] = {MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.OutputFormat.THREE_GPP};
    private String file_exts[] = {AUDIO_RECORDER_FILE_EXT_MP4, AUDIO_RECORDER_FILE_EXT_3GP};

    private ImageButton record_button;
    private TextView counter_time;

    private MediaRecorder recorder = null;
    private Timer timer = null;
    private String mFilename = null;

    private ViewGroup rootLayout = null;
    private Context context;
    private MediaPlayer mediaPlayer = null;

    private static AudioRecordingHelper audioRecordingHelper = null;

    public AudioRecordingHelper() {

    }

    public static AudioRecordingHelper getInstance() {
        if (audioRecordingHelper == null) {
            audioRecordingHelper = new AudioRecordingHelper();
        }
        return audioRecordingHelper;
    }

    public AudioRecordingHelper(Activity activity) {
        this.context = (Context) activity;
    }

    public void initView(ViewGroup viewGroup) {
        rootLayout = viewGroup;

        record_button = (ImageButton) rootLayout.findViewById(R.id.record_button);
        counter_time = (TextView) rootLayout.findViewById(R.id.time_counter);

        reset();
        setOnClickListener();
    }

    private void setOnClickListener() {

        record_button.setOnTouchListener(new View.OnTouchListener() {

            private static final int INSIDE_STATUS = 0;
            private static final int OUTSIDE_STATUS = 1;

            private Rect recordButtonRect = null;
            private int[] location = null;

            private int lastTouchStatus = INSIDE_STATUS;

            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        Log.i("Audio", "Start Recording");

                        startRecording();

                        location = new int[2];
                        view.getLocationOnScreen(location);
                        recordButtonRect = new Rect(location[0], location[1], location[0] + view.getWidth(), location[1] + view.getHeight());

                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        Log.i("Audio", "Stop Recording");

                        stopRecording();

                        int pointX = location[0] + (int) event.getX();
                        int pointY = location[1] + (int) event.getY();

                        if (recordButtonRect.contains(pointX, pointY)) {
                            sendVoiceRecord();
                        } else {
                            cancelVoiceRecord();
                        }

                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        int pointX = location[0] + (int) event.getX();
                        int pointY = location[1] + (int) event.getY();

                        Log.i("Audio", "Move " + pointX + " : " + pointY);

                        if (recordButtonRect.contains(pointX, pointY)) {
                            if (lastTouchStatus == OUTSIDE_STATUS) {
                                lastTouchStatus = INSIDE_STATUS;
                                insideOfRecordButton();
                            } else {
                                //nothing
                            }
                        } else {
                            if (lastTouchStatus == INSIDE_STATUS) {
                                lastTouchStatus = OUTSIDE_STATUS;
                                outsideOfRecordButton();
                            } else {
                                //nothing
                            }
                        }

                        break;
                    }

                }
                return false;
            }
        });
    }

    private void startRecording() {
        startCounter();

        counter_time = (TextView) rootLayout.findViewById(R.id.time_counter);
        counter_time.setText("00:00");
        counter_time.setTextColor(rootLayout.getResources().getColor(android.R.color.holo_red_light));

        mFilename = getFilename();
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(output_formats[currentFormat]);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile( DBUtil.getFilePath( context, mFilename));
        recorder.setOnErrorListener(errorListener);
        recorder.setOnInfoListener(infoListener);

        try {
            recorder.prepare();
            recorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        if (null != recorder) {
            try {
                recorder.stop();
            } catch (RuntimeException stopException) {
                //handle cleanup here
            }
            recorder.reset();
            recorder.release();
            recorder = null;
        }

        cancelTimeCounter();
        reset();
    }

    private void cancelVoiceRecord() {
        {
            View button = rootLayout.findViewById(R.id.record_button);

            Animation animation = AnimationUtils.loadAnimation(rootLayout.getContext(), R.anim.enlarge_unlimited_reverse);
            button.startAnimation(animation);
        }
    }

    private void sendVoiceRecord() {

        new ThreadTask(null) {

            private File file = new File(DBUtil.getFilePath( context, mFilename));
            private int size = (int) file.length();
            private byte[] voiceContent = new byte[size];

            @Override
            public Object process() {
                voiceContent = DBUtil.loadContentFromExternalFileStorage( context, mFilename);
//                try {
//                    BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
//                    buf.read(voiceContent, 0, voiceContent.length);
//                    buf.close();
//                } catch (FileNotFoundException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//                DBUtil.writeContentToExternalFileStorage(context, mFilename, voiceContent);
                return null;
            }

            @Override
            public void postAction(Object object) {
                sendActionToCorrespondingActivityListener(1, ActivityActionHandler.CHAT_SCREEN_KEY, SportsUnityDBHelper.MIME_TYPE_AUDIO, mFilename, voiceContent);
            }

        }.start();


    }

    private void reset() {
        counter_time = (TextView) rootLayout.findViewById(R.id.time_counter);
        counter_time.setText("00:00");
        counter_time.setTextColor(rootLayout.getResources().getColor(R.color.messages_bg));

        changeLayout_InRecordButton();
    }

    private void changeLayout_InRecordButton() {
        rootLayout.findViewById(R.id.time_counter).setVisibility(View.VISIBLE);

        rootLayout.findViewById(R.id.record_button_white).setVisibility(View.GONE);
        rootLayout.findViewById(R.id.time_counter1).setVisibility(View.GONE);
        rootLayout.findViewById(R.id.release_text).setVisibility(View.GONE);
    }

    private void changeLayout_OutRecordButton() {
        rootLayout.findViewById(R.id.time_counter).setVisibility(View.GONE);

        rootLayout.findViewById(R.id.record_button_white).setVisibility(View.VISIBLE);
        rootLayout.findViewById(R.id.time_counter1).setVisibility(View.VISIBLE);
        rootLayout.findViewById(R.id.release_text).setVisibility(View.VISIBLE);
    }

    private void insideOfRecordButton() {
        changeLayout_InRecordButton();

        {
            View button = rootLayout.findViewById(R.id.record_button);

            Animation animation = AnimationUtils.loadAnimation(rootLayout.getContext(), R.anim.enlarge_unlimited_reverse);
            button.startAnimation(animation);
        }

        String lastTimeText = counter_time.getText().toString();
        counter_time = (TextView) rootLayout.findViewById(R.id.time_counter);
        counter_time.setText(lastTimeText);
    }

    private void outsideOfRecordButton() {
        changeLayout_OutRecordButton();

        {
            View button = rootLayout.findViewById(R.id.record_button_white);

            Animation animation = AnimationUtils.loadAnimation(rootLayout.getContext(), R.anim.scale_bounce);
            button.startAnimation(animation);
        }
        {
            View button = rootLayout.findViewById(R.id.record_button);

            Animation animation = AnimationUtils.loadAnimation(rootLayout.getContext(), R.anim.enlarge_unlimited);
            button.startAnimation(animation);
        }

        String lastTimeText = counter_time.getText().toString();
        counter_time = (TextView) rootLayout.findViewById(R.id.time_counter1);
        counter_time.setText(lastTimeText);
    }

    private void startCounter() {
        cancelTimeCounter();

        timer = new Timer();
        timer.schedule(new TimerTask() {

            private int minutes = 0, seconds = 0;

            @Override
            public void run() {
                seconds++;
                if (seconds == 60) {
                    seconds = 0;
                    minutes++;
                }

                final StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append((minutes > 9 ? minutes : "0" + minutes));
                stringBuilder.append(":");
                stringBuilder.append((seconds > 9 ? seconds : "0" + seconds));

                counter_time.post(new Runnable() {

                    public void run() {
                        counter_time.setText(stringBuilder.toString());
                    }
                });

            }
        }, DURATION, DURATION);
    }

    private void cancelTimeCounter() {
        counter_time.setText("00:00");

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private String getFilename() {
//        String filepath = Environment.getExternalStorageDirectory().getPath();
//        File file = new File(filepath, AUDIO_RECORDER_FOLDER);
//
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//        Log.i("", "" + file.getAbsolutePath() + "/" + System.currentTimeMillis() + file_exts[currentFormat]);
        return DBUtil.getUniqueFileName( context, SportsUnityDBHelper.MIME_TYPE_AUDIO);
    }

    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {

        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            Log.i("Error: ", +what + ", " + extra);
        }

    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {

        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            Log.i("Warning: ", +what + ", " + extra);
        }

    };

    private boolean sendActionToCorrespondingActivityListener(int id, String key, String mimeType, Object messageContent, Object mediaContent) {
        boolean success = false;

        ActivityActionHandler activityActionHandler = ActivityActionHandler.getInstance();
        ActivityActionListener actionListener = activityActionHandler.getActionListener(key);

        if (actionListener != null) {
            actionListener.handleMediaContent(id, mimeType, messageContent, mediaContent);
            success = true;
        }
        return success;
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    private class MediaplayerCompletionListener implements MediaPlayer.OnCompletionListener {

        private ChatScreenAdapter.ViewHolder holder = null;

        MediaplayerCompletionListener(ChatScreenAdapter.ViewHolder holder) {

            this.holder = holder;
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            cancelTimer();
            holder.seekBar.setProgress(0);
            holder.seekBar.refreshDrawableState();
            mediaPlayer.stop();
            mediaPlayer.seekTo(0);
        }
    }

    public void createMediaPlayer(String filename, Activity activity) {
        if (filename == null) {
            //TODO
        } else {
            File file = new File(DBUtil.getFilePath( activity.getBaseContext(), filename));
            mediaPlayer = new MediaPlayer();
            mediaPlayer = MediaPlayer.create(activity, Uri.parse(file.getAbsolutePath()));
        }
    }

    public void initUI(final ChatScreenAdapter.ViewHolder holder) {
        holder.seekBar.setMax(mediaPlayer.getDuration());
        holder.duration.setText(String.format("%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getDuration()),
                TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getDuration()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getDuration()))
        ));
        mediaPlayer.setOnCompletionListener(new MediaplayerCompletionListener(holder));
    }

    public void startTimer(int initialDelay, int interval, ChatScreenAdapter.ViewHolder holder, Activity activity) {
        cancelTimer();

        SeekTimerTask timerTask = new SeekTimerTask(holder, activity);
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, initialDelay, interval);
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    public void pauseMediaPlayer() {
        mediaPlayer.pause();
    }

    public void startMediaPlayer() {
        mediaPlayer.start();
    }

    private class SeekTimerTask extends TimerTask {

        private ChatScreenAdapter.ViewHolder holder = null;
        private Activity activity = null;

        private SeekTimerTask(ChatScreenAdapter.ViewHolder viewHolder, Activity activity) {
            holder = viewHolder;
            this.activity = activity;
        }

        @Override
        public void run() {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int sec = mediaPlayer.getCurrentPosition();
                    Log.i("sec", String.valueOf(sec));
                    holder.seekBar.setProgress(sec);
                    int progress = holder.seekBar.getProgress();
                    holder.duration.setText(String.format("%d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(progress),
                            TimeUnit.MILLISECONDS.toSeconds(progress) -
                                    TimeUnit.MINUTES.toSeconds(progress)
                    ));
                }
            });
        }

    }

}
