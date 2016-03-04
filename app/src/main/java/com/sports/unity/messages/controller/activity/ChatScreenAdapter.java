package com.sports.unity.messages.controller.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.util.Base64;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sports.unity.Database.DBUtil;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;

/**
 * Created by madmachines on 8/9/15.
 */
public class ChatScreenAdapter extends BaseAdapter {

    private ArrayList<Message> messageList;
//    private ArrayList<Integer> messageListForFilter = new ArrayList<>();


    private boolean nearByChat = false;
    private Activity activity;
    private String searchString = "";
    private boolean isGroupChat = false;
    private String groupServerId;
    private String jid = null;

    //    private HashMap<String, byte[]> mediaMap = null;
    private AudioRecordingHelper audioRecordingHelper = null;

    private ArrayList<Integer> positions = new ArrayList<>();
    private int currentPosition = 0;

    private AudioEventListener audioEventListener = new AudioEventListener();
    private ImageOrVideoClickListener imageOrVideoClickListener = new ImageOrVideoClickListener();

    public ChatScreenAdapter(ChatScreenActivity chatScreenActivity, ArrayList<Message> messagelist, boolean otherChat, boolean isGroupChat, String groupServerId, String jid) {
        this.messageList = messagelist;
        activity = chatScreenActivity;
//        mediaMap = chatScreenActivity.getMediaMap();
        nearByChat = otherChat;
        this.isGroupChat = isGroupChat;
        audioRecordingHelper = AudioRecordingHelper.getInstance(activity);
        audioRecordingHelper.clearProgressMap();
        this.groupServerId = groupServerId;
        this.jid = jid;
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
                Message message = messageList.get(position);
                if (message.textData.toLowerCase().contains(searchString) && message.mimeType.equals(SportsUnityDBHelper.MIME_TYPE_TEXT)) {
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
        private TextView sendersName;

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
                    holder.sendersName = (TextView) vi.findViewById(R.id.group_sender_name);
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
                    holder.sendersName = null;
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

        if (isGroupChat) {
            if (holder.sendersName != null) {
                holder.sendersName.setVisibility(View.VISIBLE);
            } else {
                //nothing
            }
        } else {
            if (holder.sendersName != null) {
                holder.sendersName.setVisibility(View.GONE);
            } else {
                //nothing
            }
        }

        if (holder.sendersName != null) {
            String name = SportsUnityDBHelper.getInstance(activity).getUserNameByPhoneNumber(messageList.get(position).number);
            if (name == null) {
                holder.sendersName.setText(messageList.get(position).number);
            } else {
                holder.sendersName.setText(name);
            }
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

            boolean inProgress = false;
            int status = FileOnCloudHandler.getInstance(activity).getMediaContentStatus(message);
            if (status == FileOnCloudHandler.STATUS_NONE) {
                //nothing
            } else if (status == FileOnCloudHandler.STATUS_DOWNLOADING || status == FileOnCloudHandler.STATUS_UPLOADING) {
                inProgress = true;
            }

            if (inProgress) {
                holder.playandPause.setVisibility(View.GONE);
            } else {
                holder.playandPause.setVisibility(View.VISIBLE);
            }

            holder.seekBar.setTag(message.id);
            holder.seekBar.getThumb().setColorFilter(activity.getResources().getColor(R.color.app_theme_blue), PorterDuff.Mode.SRC_IN);
            holder.playandPause.setTag(position);
            holder.playandPause.setOnClickListener(audioEventListener);

            audioRecordingHelper.initUI(message.mediaFileName, holder, message.id);

            if (message.mediaFileName != null && DBUtil.isFileExist(activity, message.mimeType, message.mediaFileName)) {
                holder.seekBar.setEnabled(true);
                holder.seekBar.setOnSeekBarChangeListener(audioEventListener);
            } else {
                holder.seekBar.setEnabled(false);
                holder.seekBar.setOnSeekBarChangeListener(null);
            }

            ProgressBar progressBar = (ProgressBar) holder.mediaPlayerLayout.findViewById(R.id.progressBarAudio);
            if (inProgress) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }

            showMediaContentStatus(message, holder.playandPause, status);
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
                    BackgroundColorSpan span = new BackgroundColorSpan(Color.YELLOW);
                    linkifiedText.setSpan(span, start, end, 0);
                    holder.message.setText(linkifiedText, TextView.BufferType.SPANNABLE);
                }
            } else {
                holder.message.setText(message.textData);
            }

        } else if (message.mimeType.equals(SportsUnityDBHelper.MIME_TYPE_IMAGE)) {
            holder.message.setVisibility(View.GONE);
            holder.mediaPlayerLayout.setVisibility(View.GONE);
            holder.mediaContentLayout.setVisibility(View.VISIBLE);

            holder.message.setText("");

            ImageView image = (ImageView) holder.mediaContentLayout.findViewById(R.id.image_message);
            image.setVisibility(View.VISIBLE);

            int width = activity.getResources().getDimensionPixelSize(R.dimen.media_msg_content_width);
            int height = activity.getResources().getDimensionPixelSize(R.dimen.media_msg_content_height);

            image.setLayoutParams(new FrameLayout.LayoutParams(width, height));

            if (message.mediaFileName != null && DBUtil.isFileExist(activity, message.mimeType, message.mediaFileName)) {
                File file = new File(DBUtil.getFilePath(activity, message.mimeType, message.mediaFileName));
                if (message.media != null) {
                    BitmapDrawable thumbnailDrawable = new BitmapDrawable(activity.getResources(), BitmapFactory.decodeByteArray(message.media, 0, message.media.length));
                    Glide.with(activity).load(file).placeholder(thumbnailDrawable).into(image);
                } else {
                    Glide.with(activity).load(file).into(image);
                }
//                image.setOnClickListener(imageOrVideoClickListener);
//                image.setTag(R.id.image_message, position);
            } else {
                if (message.media != null) {
                    image.setImageBitmap(BitmapFactory.decodeByteArray(message.media, 0, message.media.length));
                } else {
                    image.setImageResource(R.drawable.grey_bg_rectangle);
                }
//                image.setOnClickListener(null);
//                image.setTag(R.id.image_message, null);
            }
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);

            boolean inProgress = false;
            int status = FileOnCloudHandler.getInstance(activity).getMediaContentStatus(message);
            if (status == FileOnCloudHandler.STATUS_NONE) {
                //nothing
            } else if (status == FileOnCloudHandler.STATUS_DOWNLOADING || status == FileOnCloudHandler.STATUS_UPLOADING) {
                inProgress = true;
            }

            if (inProgress) {
                image.setOnClickListener(null);
                image.setTag(R.id.image_message, null);
            } else {
                image.setOnClickListener(imageOrVideoClickListener);
                image.setTag(R.id.image_message, position);
            }

            ProgressBar progressBar = (ProgressBar) holder.mediaContentLayout.findViewById(R.id.progressBar);
            if (inProgress) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }

            showMediaContentStatus(message, (ImageView) holder.mediaContentLayout.findViewById(R.id.image_content_status), status);
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

            {
                if (message.media != null) {
                    image.setImageBitmap(BitmapFactory.decodeByteArray(message.media, 0, message.media.length));
                } else {
                    image.setImageResource(R.drawable.grey_bg_rectangle);
                }
                image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }

            boolean inProgress = false;
            int status = FileOnCloudHandler.getInstance(activity).getMediaContentStatus(message);
            if (status == FileOnCloudHandler.STATUS_NONE) {
                //nothing
            } else if (status == FileOnCloudHandler.STATUS_DOWNLOADING || status == FileOnCloudHandler.STATUS_UPLOADING) {
                inProgress = true;
            }

            if (inProgress) {
                image.setOnClickListener(null);
                image.setTag(R.id.image_message, null);
            } else {
                image.setOnClickListener(imageOrVideoClickListener);
                image.setTag(R.id.image_message, position);
            }

            ProgressBar progressBar = (ProgressBar) holder.mediaContentLayout.findViewById(R.id.progressBar);
            if (inProgress) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }

            showMediaContentStatus(message, (ImageView) holder.mediaContentLayout.findViewById(R.id.image_content_status), status);
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
            } else {
                image.setVisibility(View.GONE);
            }
            image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            image.setOnClickListener(null);
            image.setTag(R.id.image_message, null);

            showMediaContentStatus(message, (ImageView) holder.mediaContentLayout.findViewById(R.id.image_content_status), FileOnCloudHandler.STATUS_NONE);
        }

        switch (getItemViewType(position)) {
            case 0:
                holder.timeStamp.setText(CommonUtil.getDefaultTimezoneTimeInAMANDPM(Long.parseLong(message.sendTime)));
                break;
            case 1:
                holder.timeStamp.setText(CommonUtil.getDefaultTimezoneTimeInAMANDPM(Long.parseLong(message.sendTime)));
                break;
        }

        if (holder.receivedStatus == null) {
            //do nothing
        } else {
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
            int position = (Integer) v.getTag(R.id.image_message);

            Message message = messageList.get(position);

            int contentStatus = FileOnCloudHandler.getInstance(activity).getMediaContentStatus(message);

            if (contentStatus == FileOnCloudHandler.STATUS_DOWNLOADED || contentStatus == FileOnCloudHandler.STATUS_UPLOADED) {
                if (DBUtil.isFileExist(activity, message.mimeType, message.mediaFileName)) {
                    Intent intent = new Intent(activity, ImageOrVideoViewActivity.class);
                    intent.putExtra(Constants.INTENT_KEY_FILENAME, message.mediaFileName);
                    intent.putExtra(Constants.INTENT_KEY_MIMETYPE, message.mimeType);
                    activity.startActivity(intent);
                } else {
                    Toast.makeText(activity, "Media doesn't exist.", Toast.LENGTH_SHORT).show();
                }
            } else if (contentStatus == FileOnCloudHandler.STATUS_DOWNLOAD_FAILED) {
                FileOnCloudHandler.getInstance(activity).requestForDownload(message.textData, message.mimeType, message.id, jid);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            } else if (contentStatus == FileOnCloudHandler.STATUS_UPLOAD_FAILED) {
                Chat chat = null;
                if (!isGroupChat) {
                    ChatManager chatManager = ChatManager.getInstanceFor(XMPPClient.getConnection());
                    chat = chatManager.getThreadChat(jid);
                    if (chat == null) {
                        chat = chatManager.createChat(jid + "@mm.io");
                    }
                } else {
                    //do nothing
                }

                String thumbnailImage = null;
                if (message.media != null) {
                    thumbnailImage = Base64.encodeToString(message.media, Base64.DEFAULT);
                }

                FileOnCloudHandler.getInstance(activity).requestForUpload(message.mediaFileName, null, message.mimeType, chat, message.id, nearByChat, isGroupChat, groupServerId, jid);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }

        }

    }

    private void showMediaContentStatus(Message message, ImageView statusView, int status) {
        if (message.mimeType.equalsIgnoreCase(SportsUnityDBHelper.MIME_TYPE_STICKER)) {
            statusView.setVisibility(View.GONE);
        } else if (message.mimeType.equalsIgnoreCase(SportsUnityDBHelper.MIME_TYPE_TEXT)) {
            //nothing
        } else if (message.mimeType.equalsIgnoreCase(SportsUnityDBHelper.MIME_TYPE_AUDIO)) {
            showAudioContentStatus(status, statusView);
        } else if (message.mimeType.equalsIgnoreCase(SportsUnityDBHelper.MIME_TYPE_IMAGE)) {
            showImageContentStatus(status, statusView);
        } else if (message.mimeType.equalsIgnoreCase(SportsUnityDBHelper.MIME_TYPE_VIDEO)) {
            showVideoContentStatus(status, statusView);
        }
    }

    private void showImageContentStatus(int status, ImageView imageView) {
        imageView.setVisibility(View.GONE);
        if (status == FileOnCloudHandler.STATUS_NONE) {
            //nothing
        } else if (status == FileOnCloudHandler.STATUS_UPLOADING) {
            //nothing
        } else if (status == FileOnCloudHandler.STATUS_UPLOADED) {
            //nothing
        } else if (status == FileOnCloudHandler.STATUS_UPLOAD_FAILED) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.ic_retry);
        } else if (status == FileOnCloudHandler.STATUS_DOWNLOADING) {
            //nothing
        } else if (status == FileOnCloudHandler.STATUS_DOWNLOADED) {
            //nothing
        } else if (status == FileOnCloudHandler.STATUS_DOWNLOAD_FAILED) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.ic_download);
        }
    }

    private void showVideoContentStatus(int status, ImageView imageView) {
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

    private void showAudioContentStatus(int status, ImageView imageView) {
        if (status == FileOnCloudHandler.STATUS_NONE) {
            //nothing
        } else if (status == FileOnCloudHandler.STATUS_UPLOADING) {
            imageView.setVisibility(View.GONE);
        } else if (status == FileOnCloudHandler.STATUS_UPLOADED) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.ic_play_blue);
        } else if (status == FileOnCloudHandler.STATUS_UPLOAD_FAILED) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.ic_retry_blue);
        } else if (status == FileOnCloudHandler.STATUS_DOWNLOADING) {
            imageView.setVisibility(View.GONE);
        } else if (status == FileOnCloudHandler.STATUS_DOWNLOADED) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.ic_play_blue);
        } else if (status == FileOnCloudHandler.STATUS_DOWNLOAD_FAILED) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.ic_download_blue);
        }
    }

    private class AudioEventListener implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

        @Override
        public void onClick(View v) {
            ViewHolder holder = (ViewHolder) v.getTag(R.id.playAndPause);
            int position = (Integer) v.getTag();
            Message message = messageList.get(position);

            int contentStatus = FileOnCloudHandler.getInstance(activity).getMediaContentStatus(message);

            if (contentStatus == FileOnCloudHandler.STATUS_DOWNLOADED || contentStatus == FileOnCloudHandler.STATUS_UPLOADED) {
                if (DBUtil.isFileExist(v.getContext(), message.mimeType, message.mediaFileName)) {
                    audioRecordingHelper.handlePlayOrPauseEvent(message, holder);
                } else {
                    Toast.makeText(activity, "Media doesn't exist.", Toast.LENGTH_SHORT).show();
                }
            } else if (contentStatus == FileOnCloudHandler.STATUS_DOWNLOAD_FAILED) {
                FileOnCloudHandler.getInstance(activity).requestForDownload(message.textData, message.mimeType, message.id, jid);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            } else if (contentStatus == FileOnCloudHandler.STATUS_UPLOAD_FAILED) {
                ChatManager chatManager = ChatManager.getInstanceFor(XMPPClient.getConnection());
                Chat chat = chatManager.getThreadChat(jid);
                if (chat == null) {
                    chat = chatManager.createChat(jid + "@mm.io");
                }

                String thumbnailImage = null;
                if (message.media != null) {
                    thumbnailImage = Base64.encodeToString(message.media, Base64.DEFAULT);
                }
                FileOnCloudHandler.getInstance(activity).requestForUpload(message.mediaFileName, thumbnailImage, message.mimeType, chat, message.id, nearByChat, isGroupChat, groupServerId, jid);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
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
