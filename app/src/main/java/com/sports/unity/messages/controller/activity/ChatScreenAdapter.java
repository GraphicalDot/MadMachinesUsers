package com.sports.unity.messages.controller.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.messages.controller.model.Message;
import com.sports.unity.messages.controller.model.Stickers;
import com.sports.unity.messages.controller.model.ToolbarActionsForChatScreen;
import com.sports.unity.messages.controller.viewhelper.AudioRecordingHelper;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.FileOnCloudHandler;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by madmachines on 8/9/15.
 */
public class ChatScreenAdapter extends BaseAdapter {

    private ArrayList<Message> messageList;
//    private ArrayList<Integer> messageListForFilter = new ArrayList<>();


    private boolean nearByChat = false;
    private Activity activity;
    private String searchString = "";

    private HashMap<String, byte[]> mediaMap = null;
    private AudioRecordingHelper audioRecordingHelper = null;

    private ArrayList<Integer> positions = new ArrayList<>();
    private int currentPosition = 0;

    private AudioEventListener audioEventListener = new AudioEventListener();
    private ImageOrVideoClickListener imageOrVideoClickListener = new ImageOrVideoClickListener();

    public ChatScreenAdapter(ChatScreenActivity chatScreenActivity, ArrayList<Message> messagelist, boolean otherChat) {
        this.messageList = messagelist;
        activity = chatScreenActivity;
        mediaMap = chatScreenActivity.getMediaMap();
        nearByChat = otherChat;

        audioRecordingHelper = AudioRecordingHelper.getInstance(activity);
        audioRecordingHelper.clearProgressMap();
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        int flag;
        if (messageList.get(position).iAmSender) {
            flag = 1;
        } else {
            flag = 0;
        }
        return flag;
    }

    public void filterSearchQuery(String searchString) {
        this.searchString = searchString;
        this.searchString.toLowerCase();

        positions.clear();
        currentPosition = 0;
//        messageListForFilter.clear();
        if (searchString.length() == 0 || messageList.size() == 0) {
            //do nothing
        } else {

            for (int position = 0; position < messageList.size(); position++) {
                if (messageList.get(position).textData.toLowerCase().contains(searchString)) {
                    positions.add(position);
                }
            }
        }

        notifyDataSetChanged();
    }

    public int getPosition(String nav) {
        int size = positions.size();
        int nextPosition = -1;
        Log.i("size", String.valueOf(size));
        if (size == 0) {
            return nextPosition;
        } else {

            switch (nav) {
                case "up":
                    currentPosition--;
                    if (currentPosition < 0) {
                        currentPosition = size - 1;
                        nextPosition = positions.get(currentPosition);
                    } else {
                        nextPosition = positions.get(currentPosition);
                    }
                    break;
                case "down":
                    currentPosition++;
                    if (currentPosition > size - 1) {
                        currentPosition = 0;
                        nextPosition = positions.get(currentPosition);
                    } else {
                        nextPosition = positions.get(currentPosition);
                    }
                    break;
            }
        }
        return nextPosition;
    }

    public static class ViewHolder {

        private TextView message;
        private TextView timeStamp;
        private ImageView receivedStatus;
        private FrameLayout mediaContentLayout;
        private RelativeLayout mediaPlayerLayout;
        private ImageView playandPause;
        private SeekBar seekBar;
        private TextView duration;

        public SeekBar getSeekBar() {
            return seekBar;
        }

        public ImageView getPlayandPause() {
            return playandPause;
        }

        public TextView getDuration() {
            return duration;
        }
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;

        Message message = messageList.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            holder = new ViewHolder();
            switch (getItemViewType(position)) {
                case 0:
                    vi = inflater.inflate(R.layout.chat_msg_recieve, parent, false);
                    holder.message = (TextView) vi.findViewById(R.id.singleMessageLeft);
                    holder.message.setTypeface(FontTypeface.getInstance(activity.getApplicationContext()).getRobotoRegular());
                    holder.mediaContentLayout = (FrameLayout) vi.findViewById(R.id.image_message_parent);
                    holder.timeStamp = (TextView) vi.findViewById(R.id.timestampLeft);
                    holder.timeStamp.setTypeface(FontTypeface.getInstance(activity.getApplicationContext()).getRobotoCondensedRegular());
                    holder.playandPause = (ImageView) vi.findViewById(R.id.playAndPause);
                    holder.seekBar = (SeekBar) vi.findViewById(R.id.seekbar);
                    holder.mediaPlayerLayout = (RelativeLayout) vi.findViewById(R.id.mediaPlayer);
                    holder.duration = (TextView) vi.findViewById(R.id.duration);
                    holder.receivedStatus = null;
                    vi.setTag(holder);
                    break;
                case 1:
                    vi = inflater.inflate(R.layout.chat_msg_send, parent, false);
                    holder.message = (TextView) vi.findViewById(R.id.singleMessageRight);
                    holder.message.setTypeface(FontTypeface.getInstance(activity.getApplicationContext()).getRobotoRegular());
                    holder.mediaContentLayout = (FrameLayout) vi.findViewById(R.id.image_message_parent);
                    holder.timeStamp = (TextView) vi.findViewById(R.id.timestampRight);
                    holder.timeStamp.setTypeface(FontTypeface.getInstance(activity.getApplicationContext()).getRobotoCondensedRegular());
                    holder.receivedStatus = (ImageView) vi.findViewById(R.id.receivedStatus);
                    holder.playandPause = (ImageView) vi.findViewById(R.id.playAndPause);
                    holder.seekBar = (SeekBar) vi.findViewById(R.id.seekbar);
                    holder.mediaPlayerLayout = (RelativeLayout) vi.findViewById(R.id.mediaPlayer);
                    holder.duration = (TextView) vi.findViewById(R.id.duration);
                    vi.setTag(holder);
                    break;
            }

        } else {
            holder = (ViewHolder) vi.getTag();
        }
        holder.playandPause.setTag(R.id.playAndPause, holder);

        if (!message.mimeType.equals(SportsUnityDBHelper.MIME_TYPE_STICKER)) {
            if (message.iAmSender) {
                ((LinearLayout) holder.message.getParent()).setBackgroundResource(R.drawable.chat_blue);
            } else {
                ((LinearLayout) holder.message.getParent()).setBackgroundResource(R.drawable.chat_white);
            }
        } else {
            ((LinearLayout) holder.message.getParent()).setBackgroundResource(android.R.color.transparent);
        }

        if (ToolbarActionsForChatScreen.getInstance(activity).isItemSelected(position)) {
            ColorDrawable drawable = new ColorDrawable(activity.getResources().getColor(R.color.list_selector));
            ((FrameLayout) vi).setForeground(drawable);
        } else {
            ColorDrawable drawable = new ColorDrawable(Color.TRANSPARENT);
            ((FrameLayout) vi).setForeground(drawable);
        }

        if (message.mimeType.equals(SportsUnityDBHelper.MIME_TYPE_AUDIO)) {

            Integer lastTag = (Integer) holder.seekBar.getTag();
            if (lastTag != null && audioRecordingHelper.getCurrentPlayingMessageId() == lastTag &&
                    lastTag != message.id) {
                audioRecordingHelper.pauseAudio();
            } else {
                //nothing
            }

            holder.mediaContentLayout.setVisibility(View.GONE);
            holder.message.setVisibility(View.GONE);
            holder.mediaPlayerLayout.setVisibility(View.VISIBLE);

            ProgressBar progressBar = (ProgressBar) holder.mediaPlayerLayout.findViewById(R.id.progressBarAudio);
            if (message.textData.length() == 0 && message.iAmSender == true || message.mediaFileName == null && message.iAmSender == false) {
                holder.playandPause.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
                holder.playandPause.setVisibility(View.VISIBLE);
            }

            if (message.mediaFileName != null) {
                audioRecordingHelper.initUI(message.mediaFileName, holder, message.id);
            } else {
                //nothing
            }

            holder.seekBar.setTag(message.id);
            holder.seekBar.getThumb().setColorFilter(activity.getResources().getColor(R.color.app_theme_blue), PorterDuff.Mode.SRC_IN);
            holder.seekBar.setOnSeekBarChangeListener(audioEventListener);

            holder.playandPause.setTag(position);
            holder.playandPause.setOnClickListener(audioEventListener);

        } else if (message.mimeType.equals(SportsUnityDBHelper.MIME_TYPE_TEXT)) {
            holder.message.setVisibility(View.VISIBLE);
            holder.mediaPlayerLayout.setVisibility(View.GONE);
            holder.mediaContentLayout.setVisibility(View.GONE);

            if (searchString.length() != 0) {
                String textData = message.textData;
                SpannableStringBuilder linkifiedText = new SpannableStringBuilder(textData);
                Pattern p = Pattern.compile(searchString.toLowerCase());
                Matcher m = p.matcher(textData.toLowerCase());
                while (m.find()) {
                    int start = m.start();
                    int end = m.end();
//                    String hashtag = textData.substring(start, end);
                    BackgroundColorSpan span = new BackgroundColorSpan(Color.YELLOW);
                    linkifiedText.setSpan(span, start, end, 0);
//                    Spannable spanText = Spannable.Factory.getInstance().newSpannable(message.textData);
//                    spanText.setSpan(new BackgroundColorSpan(Color.YELLOW), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                holder.message.setText(linkifiedText, TextView.BufferType.SPANNABLE);
            } else {
                holder.message.setText(message.textData);
            }

        } else if (message.mimeType.equals(SportsUnityDBHelper.MIME_TYPE_IMAGE)) {
            holder.message.setVisibility(View.GONE);
            holder.mediaPlayerLayout.setVisibility(View.GONE);
            holder.mediaContentLayout.setVisibility(View.VISIBLE);

            holder.message.setText("");

            byte[] content = null;
            if (mediaMap.containsKey(message.mediaFileName)) {
                content = mediaMap.get(message.mediaFileName);
            } else {
                content = message.media;
            }

            ImageView image = (ImageView) holder.mediaContentLayout.findViewById(R.id.image_message);
            image.setVisibility(View.VISIBLE);

            int width = activity.getResources().getDimensionPixelSize(R.dimen.media_msg_content_width);
            int height = activity.getResources().getDimensionPixelSize(R.dimen.media_msg_content_height);

            image.setLayoutParams(new FrameLayout.LayoutParams(width, height));

            if (content != null) {
                image.setImageBitmap(BitmapFactory.decodeByteArray(content, 0, content.length));
                image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                image.setOnClickListener(imageOrVideoClickListener);
                image.setTag(position);
            } else {
                image.setImageResource(R.drawable.grey_bg_rectangle);
                image.setOnClickListener(null);
                image.setTag(null);
            }

            ProgressBar progressBar = (ProgressBar) holder.mediaContentLayout.findViewById(R.id.progressBar);
            if ((message.textData.length() == 0 && message.iAmSender == true) || (message.mediaFileName == null && message.iAmSender == false)) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        } else if (message.mimeType.equals(SportsUnityDBHelper.MIME_TYPE_VIDEO)) {
            holder.message.setVisibility(View.GONE);
            holder.mediaPlayerLayout.setVisibility(View.GONE);
            holder.mediaContentLayout.setVisibility(View.VISIBLE);

            holder.message.setText("");

            ImageView image = (ImageView) holder.mediaContentLayout.findViewById(R.id.image_message);
            image.setVisibility(View.VISIBLE);

            int width = activity.getResources().getDimensionPixelSize(R.dimen.media_msg_content_width);
            int height = activity.getResources().getDimensionPixelSize(R.dimen.media_msg_content_height);
            image.setLayoutParams(new FrameLayout.LayoutParams(width, height));

            boolean inProgress = false;
            int status = FileOnCloudHandler.getInstance(activity).getMediaContentStatus(message);
            if (status == FileOnCloudHandler.STATUS_NONE) {
                //nothing
            } else if (status == FileOnCloudHandler.STATUS_DOWNLOADING || status == FileOnCloudHandler.STATUS_UPLOADING) {
                inProgress = true;
            }

            if (inProgress) {
                image.setOnClickListener(null);
                image.setTag(null);
            } else {
                image.setOnClickListener(imageOrVideoClickListener);
                image.setTag(position);
            }
            image.setImageResource(R.drawable.grey_bg_rectangle);

            ProgressBar progressBar = (ProgressBar) holder.mediaContentLayout.findViewById(R.id.progressBar);
            if (inProgress) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        } else if (message.mimeType.equals(SportsUnityDBHelper.MIME_TYPE_STICKER)) {
            holder.message.setText("");
            holder.message.setVisibility(View.GONE);
            holder.mediaPlayerLayout.setVisibility(View.GONE);
            holder.mediaContentLayout.setVisibility(View.VISIBLE);

            ImageView image = (ImageView) holder.mediaContentLayout.findViewById(R.id.image_message);
            ProgressBar progressBar = (ProgressBar) holder.mediaContentLayout.findViewById(R.id.progressBar);

            progressBar.setVisibility(View.GONE);
            image.setVisibility(View.VISIBLE);

            String content = message.textData;
            int separatorIndex = content.indexOf('/');
            String folderName = content.substring(0, separatorIndex);
            String name = content.substring(separatorIndex + 1);

            Stickers.getInstance().loadStickerFromAsset(activity, folderName, name);

            Bitmap bitmap = Stickers.getInstance().getStickerBitmap(folderName, name);
            if (bitmap != null) {
                int size = activity.getResources().getDimensionPixelSize(R.dimen.sticker_msg_content_size);
                image.setImageBitmap(bitmap);
                image.setVisibility(View.VISIBLE);
                image.setLayoutParams(new FrameLayout.LayoutParams(size, size));
                image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            } else {
                image.setVisibility(View.GONE);
            }

        }

        showMediaContentStatus(message, holder.mediaContentLayout);

        switch (

                getItemViewType(position)

                )

        {
            case 0:
                holder.timeStamp.setText(CommonUtil.getDefaultTimezoneTimeInAMANDPM(Long.parseLong(message.sendTime)));
                break;
            case 1:
                holder.timeStamp.setText(CommonUtil.getDefaultTimezoneTimeInAMANDPM(Long.parseLong(message.sendTime)));
                break;

        }

        if (holder.receivedStatus == null)

        {
            //do nothing
        } else

        {

            if (message.messagesRead == true) {
                holder.receivedStatus.setImageResource(R.drawable.ic_msg_read);
            } else if (message.recipientR != null) {
                holder.receivedStatus.setImageResource(R.drawable.ic_msg_delivered);
            } else if (message.serverR != null) {
                holder.receivedStatus.setImageResource(R.drawable.ic_msg_sent);
            } else {
                holder.receivedStatus.setImageResource(R.drawable.ic_msg_pending);
            }

        }

        return vi;
    }

    public void notifydataset(ArrayList<Message> messagelist) {
        this.messageList = messagelist;
        notifyDataSetChanged();
    }

    private class ImageOrVideoClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();

            Message message = messageList.get(position);

            int contentStatus = FileOnCloudHandler.getInstance(activity).getMediaContentStatus(message);

            if (contentStatus == FileOnCloudHandler.STATUS_DOWNLOADED || contentStatus == FileOnCloudHandler.STATUS_UPLOADED) {
                Intent intent = new Intent(activity, ImageOrVideoViewActivity.class);
                intent.putExtra(Constants.INTENT_KEY_FILENAME, message.mediaFileName);
                intent.putExtra(Constants.INTENT_KEY_MIMETYPE, message.mimeType);
                activity.startActivity(intent);
            } else if (contentStatus == FileOnCloudHandler.STATUS_DOWNLOAD_FAILED) {
                FileOnCloudHandler.getInstance(activity).requestForDownload(message.textData, message.mimeType, message.id);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            } else if (contentStatus == FileOnCloudHandler.STATUS_UPLOAD_FAILED) {
                ChatManager chatManager = ChatManager.getInstanceFor(XMPPClient.getConnection());
                Chat chat = chatManager.getThreadChat(ChatScreenActivity.getJABBERID());
                if (chat == null) {
                    chat = chatManager.createChat(ChatScreenActivity.getJABBERID() + "@mm.io");
                }
                FileOnCloudHandler.getInstance(activity).requestForUpload(message.mediaFileName, message.mimeType, chat, message.id, nearByChat);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }

        }

    }

    private void showMediaContentStatus(Message message, ViewGroup imageContentLayout) {
        if (message.mimeType.equalsIgnoreCase(SportsUnityDBHelper.MIME_TYPE_STICKER)) {
            imageContentLayout.findViewById(R.id.image_content_status).setVisibility(View.GONE);
        } else if (message.mimeType.equalsIgnoreCase(SportsUnityDBHelper.MIME_TYPE_TEXT)) {
            //nothing
        } else if (message.mimeType.equalsIgnoreCase(SportsUnityDBHelper.MIME_TYPE_AUDIO)) {
            //TODO
        } else if (message.mimeType.equalsIgnoreCase(SportsUnityDBHelper.MIME_TYPE_IMAGE)) {
            imageContentLayout.findViewById(R.id.image_content_status).setVisibility(View.GONE);
        } else if (message.mimeType.equalsIgnoreCase(SportsUnityDBHelper.MIME_TYPE_VIDEO)) {
            int status = FileOnCloudHandler.getInstance(activity).getMediaContentStatus(message);
            showVideoContentStatus(status, message.mediaFileName, imageContentLayout);
        }
    }

    private void showVideoContentStatus(int status, String fileName, ViewGroup imageContentLayout) {
        ImageView imageView = (ImageView) imageContentLayout.findViewById(R.id.image_content_status);
        if (status == FileOnCloudHandler.STATUS_NONE) {
            //nothing
        } else if (status == FileOnCloudHandler.STATUS_UPLOADING) {
            imageView.setVisibility(View.GONE);
        } else if (status == FileOnCloudHandler.STATUS_UPLOADED) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.ic_play);
        } else if (status == FileOnCloudHandler.STATUS_UPLOAD_FAILED) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.ic_retry);
        } else if (status == FileOnCloudHandler.STATUS_DOWNLOADING) {
            imageView.setVisibility(View.GONE);
        } else if (status == FileOnCloudHandler.STATUS_DOWNLOADED) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.ic_play);
        } else if (status == FileOnCloudHandler.STATUS_DOWNLOAD_FAILED) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.ic_download);
        }
    }

    private class AudioEventListener implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

        @Override
        public void onClick(View v) {
            ViewHolder holder = (ViewHolder) v.getTag(R.id.playAndPause);
            int position = (Integer) v.getTag();
            Message message = messageList.get(position);
            audioRecordingHelper.handlePlayOrPauseEvent(message, holder);
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            audioRecordingHelper.setProgress((Integer) seekBar.getTag(), seekBar.getProgress());
        }

    }

}
