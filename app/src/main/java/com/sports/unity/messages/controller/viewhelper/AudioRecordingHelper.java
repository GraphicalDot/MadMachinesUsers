package com.sports.unity.messages.controller.viewhelper;

import android.app.Activity;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sports.unity.Database.DBUtil;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.messages.controller.activity.ChatScreenAdapter;
import com.sports.unity.messages.controller.model.Message;
import com.sports.unity.util.ActivityActionHandler;
import com.sports.unity.util.ActivityActionListener;
import com.sports.unity.util.ThreadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by madmachines on 27/11/15.
 */
public class AudioRecordingHelper {

    private static final int DURATION = 1000;

    private static AudioRecordingHelper audioRecordingHelper = null;

    synchronized public static AudioRecordingHelper getInstance(Activity activity) {
        if (audioRecordingHelper == null) {
            audioRecordingHelper = new AudioRecordingHelper(activity);
        }
        return audioRecordingHelper;
    }

    public static void cleanUp() {
        if( audioRecordingHelper != null ){
            audioRecordingHelper.cleanup();
        }
        audioRecordingHelper = null;
    }

    private int currentFormat = 0;
    private int output_formats[] = {MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.OutputFormat.THREE_GPP};

    private ImageButton record_button;
    private TextView counter_time;

    private MediaRecorder recorder = null;
    private Timer timer = null;
    private String mFilename = null;

    private ViewGroup rootLayout = null;
    private Activity activity;
    private MediaPlayer mediaPlayer = null;

    private int currentPlayingMessageId = -1;
    private ImageView playPauseButton = null;
    private HashMap<Integer, ProgressState> seekBarProgressMap = new HashMap<>();

    private AudioRecordingHelper(Activity activity) {
        this.activity = activity;
        mediaPlayer = new MediaPlayer();
    }

    public void initView(ViewGroup viewGroup) {
        rootLayout = viewGroup;

        record_button = (ImageButton) rootLayout.findViewById(R.id.record_button);
        counter_time = (TextView) rootLayout.findViewById(R.id.time_counter);

        reset();
        setOnClickListener();
    }

    public void putSeekProgress(int id, int duration, int maxDuration) {
        ProgressState progressState = null;
        if( seekBarProgressMap.containsKey(id) ) {
            progressState = seekBarProgressMap.get(id);
            progressState.setDuration(duration);
            progressState.setMaxDuration(maxDuration);
        } else {
            progressState = new ProgressState( duration, maxDuration);
            seekBarProgressMap.put(id, progressState);
        }
    }

    public int getSeekProgress(int id){
        int progress = 0;
        if( seekBarProgressMap.containsKey(id) ){
            progress = seekBarProgressMap.get(id).getDuration();
        } else {
            progress = 0;
        }
        return progress;
    }

    public void setProgress(int id, int progress) {
        ProgressState progressState = seekBarProgressMap.get(id);
        progressState.setDuration(progress);

        if (id == currentPlayingMessageId) {
            mediaPlayer.seekTo(progress);
        } else {
            //TODO
        }
    }

    public void clearProgressMap() {
        seekBarProgressMap.clear();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public int getCurrentPlayingMessageId() {
        return currentPlayingMessageId;
    }

    public void initUI(String fileName, ChatScreenAdapter.ViewHolder holder, int messageId) {
        ProgressState progressState = seekBarProgressMap.get(messageId);

        if( progressState != null ) {
            holder.getSeekBar().setMax(progressState.getMaxDuration());
            holder.getSeekBar().setProgress(progressState.getDuration());

            setDurationOnTimeView(holder.getDuration(), progressState);
        } else {
            boolean success = false;
            try {
                pauseAudio();

                File file = new File(DBUtil.getFilePath(activity.getBaseContext(), fileName));
                mediaPlayer = MediaPlayer.create(activity, Uri.parse(file.getAbsolutePath()));

                if( mediaPlayer != null ) {
                    success = true;
                } else {
                    //nothing
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }

            if( success ) {
                putSeekProgress(messageId, -1, mediaPlayer.getDuration());
                progressState = seekBarProgressMap.get(messageId);

                holder.getSeekBar().setMax(progressState.getMaxDuration());
                holder.getSeekBar().setProgress(0);

                setDurationOnTimeView(holder.getDuration(), progressState);
            } else {
                //nothing
            }
        }
    }

    public void handlePlayOrPauseEvent(Message message, ChatScreenAdapter.ViewHolder holder){
        handlePlayPauseEvent(message, holder);
    }

    public void stopAndReleaseMediaPlayer() {
        try {
            pauseAudio();
            mediaPlayer.stop();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        cancelTimerOnPlaying();
    }

    public void pauseAudio(){
        if( currentPlayingMessageId != -1 && mediaPlayer.isPlaying() ) {
            try {
                mediaPlayer.pause();
            }catch (Exception ex){
                ex.printStackTrace();
            }
            putSeekProgress(currentPlayingMessageId, mediaPlayer.getCurrentPosition(), mediaPlayer.getDuration());
            cancelTimerOnPlaying();

            if (playPauseButton != null) {
                playPauseButton.setImageResource(android.R.drawable.ic_media_play);
            } else {
                //nothing
            }
        } else {
            //nothing
        }
    }

    private void handlePlayPauseEvent(Message message, ChatScreenAdapter.ViewHolder holder) {
        int seekbarId = message.id;
        if (this.currentPlayingMessageId != seekbarId) {
            pauseAudio();
            playAudio(message, holder);
        } else {
            if( isPlaying() ) {
                pauseAudio();
            } else {
                playAudio(message, holder);
            }
        }

    }

    private void playAudio(Message message, ChatScreenAdapter.ViewHolder holder){
        boolean success = createMediaPlayer(message.mediaFileName, message.id, holder);
        if( success ) {
            try {
                startMediaPlayer();
                startTimer(0, 100, holder);

                this.currentPlayingMessageId = message.id;
                this.playPauseButton = holder.getPlayandPause();

                putSeekProgress( message.id, 0, mediaPlayer.getDuration());

                holder.getPlayandPause().setImageResource(android.R.drawable.ic_media_pause);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        } else {
            //nothing
        }
    }

    private boolean createMediaPlayer(String filename, int seekbarId, ChatScreenAdapter.ViewHolder holder) {
        boolean success = false;
        try {
            if (filename == null) {
               //nothing
            } else {
                File file = new File(DBUtil.getFilePath(activity.getBaseContext(), filename));

                mediaPlayer.reset();
                mediaPlayer = MediaPlayer.create(activity, Uri.parse(file.getAbsolutePath()));

                if( mediaPlayer != null ) {

                    if (getSeekProgress(seekbarId) > 0) {
                        mediaPlayer.seekTo(getSeekProgress(seekbarId));
                    }

                    mediaPlayer.setOnCompletionListener(new MediaPlayerCompletionListener(holder));
                    this.currentPlayingMessageId = seekbarId;

                    success = true;
                } else {
                    //nothing
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return success;
    }

    private void startMediaPlayer() throws IllegalStateException {
        mediaPlayer.start();
    }

    private void startTimer(int initialDelay, int interval, ChatScreenAdapter.ViewHolder holder) {
        cancelTimerOnPlaying();

        SeekTimerTask timerTask = new SeekTimerTask(holder, activity);
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, initialDelay, interval);
    }

    private void setDurationOnTimeView(TextView textView, ProgressState progressState) {
        int time = progressState.getDuration() == -1 ? progressState.getMaxDuration() : progressState.getDuration();
        textView.setText(String.format("%d:%02d", (time / 1000) / (60), (time / 1000) % 60));
    }

    private void setDurationOnTimeView(TextView textView, int time) {
        textView.setText(String.format("%d:%02d", (time / 1000) / (60), (time / 1000) % 60));
    }

    private void sendVoiceRecord() {
        Integer time = (Integer)counter_time.getTag();
        if( time != null && time > 0 ) {

            new ThreadTask(null) {

                private File file = new File(DBUtil.getFilePath(activity, mFilename));
                private int size = (int) file.length();
                private byte[] voiceContent = new byte[size];

                @Override
                public Object process() {
                    voiceContent = DBUtil.loadContentFromExternalFileStorage(activity, mFilename);
                    Log.d("Audio Helper", "content size " + voiceContent.length);
                    return null;
                }

                @Override
                public void postAction(Object object) {
                    sendActionToCorrespondingActivityListener(1, ActivityActionHandler.CHAT_SCREEN_KEY, SportsUnityDBHelper.MIME_TYPE_AUDIO, mFilename, voiceContent);
                }

            }.start();
        } else {
            //nothing
            Log.d("Audio", "Audio is too small to handle");
        }
    }

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

    private void startRecording() {
        startTimerForRecording();

        counter_time = (TextView) rootLayout.findViewById(R.id.time_counter);
        setDurationOnTimeView(counter_time, 0);
        counter_time.setTag(0);
        counter_time.setTextColor(rootLayout.getResources().getColor(android.R.color.holo_red_light));

        mFilename = DBUtil.getUniqueFileName(activity, SportsUnityDBHelper.MIME_TYPE_AUDIO);
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(output_formats[currentFormat]);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(DBUtil.getFilePath(activity, mFilename));
        recorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {

            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                Log.d("Audio", "On Error");
            }

        });
        recorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {

            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                Log.d("Audio", "On Info");
            }

        });

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

        cancelTimerOnRecording();
        reset();
    }

    private void cancelVoiceRecord() {
        {
            View button = rootLayout.findViewById(R.id.record_button);

            Animation animation = AnimationUtils.loadAnimation(rootLayout.getContext(), R.anim.enlarge_unlimited_reverse);
            button.startAnimation(animation);
        }

        //TODO delete saved file content
    }

    private void reset() {
        counter_time = (TextView) rootLayout.findViewById(R.id.time_counter);
        setDurationOnTimeView(counter_time, 0);
        counter_time.setTextColor(rootLayout.getResources().getColor(R.color.messages_bg));

        changeLayout_InRecordButton();
    }

    private void startTimerForRecording() {
        cancelTimerOnRecording();
        setDurationOnTimeView(counter_time, 0);

        timer = new Timer();
        timer.schedule(new TimerTask() {

            private int seconds = 0;

            @Override
            public void run() {
                seconds++;

                counter_time.post(new Runnable() {

                    public void run() {
                        setDurationOnTimeView(counter_time, seconds*1000);
                        counter_time.setTag(seconds);
                    }
                });

            }
        }, DURATION, DURATION);
    }

    private void cancelTimerOnRecording() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void cancelTimerOnPlaying() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    private void cleanup(){
        stopAndReleaseMediaPlayer();
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

                        pauseAudio();
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

                        Log.d("Audio", "Moving " + pointX + " : " + pointY);

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

    private class ProgressState {

        private int duration = 0;
        private int maxDuration = 0;

        public ProgressState(int duration, int maxDuration){
            this.duration = duration;
            this.maxDuration = maxDuration;
        }

        public int getDuration() {
            return duration;
        }

        public int getMaxDuration() {
            return maxDuration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public void setMaxDuration(int maxDuration) {
            this.maxDuration = maxDuration;
        }

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
                    if (timer != null) {
                        int time = mediaPlayer.getCurrentPosition();

                        Log.d("Audio", "Media Player Seek Time " + String.valueOf(time));

                        holder.getSeekBar().setProgress(time);
                        setDurationOnTimeView(holder.getDuration(), time);
                    }
                }

            });
        }

    }

    private class MediaPlayerCompletionListener implements MediaPlayer.OnCompletionListener {

        private ChatScreenAdapter.ViewHolder holder = null;

        private MediaPlayerCompletionListener(ChatScreenAdapter.ViewHolder holder) {
            this.holder = holder;
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            Log.d("Audio", "Media Player Completion Event");

            cancelTimerOnPlaying();

            holder.getSeekBar().setProgress(0);
            holder.getSeekBar().refreshDrawableState();

            mediaPlayer.pause();
            mediaPlayer.seekTo(0);

            holder.getPlayandPause().setImageResource(android.R.drawable.ic_media_play);
            putSeekProgress(currentPlayingMessageId, -1, mediaPlayer.getDuration());

            int time = mediaPlayer.getDuration();
            setDurationOnTimeView(holder.getDuration(), time);
        }
    }

}
