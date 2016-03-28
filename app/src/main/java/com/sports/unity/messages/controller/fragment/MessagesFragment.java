package com.sports.unity.messages.controller.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.sports.unity.R;
import com.sports.unity.common.controller.MainActivity;
import com.sports.unity.common.model.ContactsHandler;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.PermissionUtil;
import com.sports.unity.messages.controller.activity.GroupDetailActivity;
import com.sports.unity.messages.controller.activity.PeopleAroundMeMap;
import com.sports.unity.messages.controller.viewhelper.OnSearchViewQueryListener;
import com.sports.unity.util.ActivityActionHandler;
import com.sports.unity.util.ActivityActionListener;
import com.sports.unity.util.Constants;
import com.sports.unity.util.NotificationHandler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Agupta on 8/13/2015.
 */
public class MessagesFragment extends Fragment implements View.OnClickListener, MainActivity.PermissionResultHandler, ContactsHandler.ContactCopyCompletedListener {

    private OnSearchViewQueryListener mListener = null;

    private FrameLayout frame;
    private TextView contacts;
    private TextView chats;
    private TextView others;
    private LinearLayout buttonContainerLayout;
    private Activity activity;
    private Fragment currentFragment;
    private LinearLayout childStripLayout;

    private TextView friendsUnreadCount;
    private TextView othersUnreadCount;
    private View backgroundDimmer;
    private MenuItem syncProgress;

    private SearchView searchView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    private void getAndSetUnreadCount() {
        int friendsChatUnreadCount = NotificationHandler.getInstance(getContext()).getUnreadFriendsChatCount();
        int otherChatUnreadCount = NotificationHandler.getInstance(getContext()).getUnreadOthersChatCount();
        int messagesCount = NotificationHandler.getInstance(getContext()).getUnreadMessageCount();

        if (friendsChatUnreadCount == 0) {
            friendsUnreadCount.setVisibility(View.GONE);
        } else {
            friendsUnreadCount.setVisibility(View.VISIBLE);
            if (friendsChatUnreadCount > 99) {
                friendsUnreadCount.setText(Html.fromHtml("99<sup>+</sup>"));
            } else {
                friendsUnreadCount.setText(String.valueOf(friendsChatUnreadCount));
            }
        }
        if (otherChatUnreadCount == 0) {
            othersUnreadCount.setVisibility(View.GONE);
        } else {
            othersUnreadCount.setVisibility(View.VISIBLE);
            if (otherChatUnreadCount > 99) {
                othersUnreadCount.setText(Html.fromHtml("99<sup>+</sup>"));
            } else {
                othersUnreadCount.setText(String.valueOf(otherChatUnreadCount));
            }
        }
        ((MainActivity) getActivity()).updateUnreadMessages(messagesCount);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(com.sports.unity.R.layout.messages, container, false);
        frame = (FrameLayout) v.findViewById(com.sports.unity.R.id.childFragmentContainer);

        LinearLayout friendsList = (LinearLayout) v.findViewById(R.id.fragment_friends);
        friendsList.setOnClickListener(this);
        LinearLayout othersChatList = (LinearLayout) v.findViewById(R.id.fragment_others);
        othersChatList.setOnClickListener(this);
        LinearLayout contactsList = (LinearLayout) v.findViewById(R.id.fragment_contacts);
        contactsList.setOnClickListener(this);

        frame = (FrameLayout) v.findViewById(com.sports.unity.R.id.childFragmentContainer);

        contacts = (TextView) v.findViewById(R.id.txt_contacts);
        chats = (TextView) v.findViewById(R.id.txt_friends);
        others = (TextView) v.findViewById(R.id.txt_others);

        buttonContainerLayout = (LinearLayout) v.findViewById(com.sports.unity.R.id.fragmentChangeButtonLayout);
        childStripLayout = (LinearLayout) v.findViewById(R.id.fragmentChangeButtonContainer);

        friendsUnreadCount = (TextView) v.findViewById(R.id.friends_unread_count);
        othersUnreadCount = (TextView) v.findViewById(R.id.others_unread_count);
//        backgroundDimmer = v.findViewById(R.id.background_dimmer);


//        fabMenu = (FloatingActionMenu) v.findViewById(R.id.fab_menu);
//        fabMenu.setOnMenuButtonClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                fabMenu.toggle(true);
//            }
//        });
//
//        fabMenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
//            @Override
//            public void onMenuToggle(boolean opened) {
//                if (opened) {
//                    backgroundDimmer.setVisibility(View.VISIBLE);
//                } else {
//                    backgroundDimmer.setVisibility(View.GONE);
//                }
//            }
//        });
//
//        FloatingActionButton peopleAroundMeFab = (FloatingActionButton) v.findViewById(R.id.people_around_me);
//        peopleAroundMeFab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                fabMenu.toggle(true);
//                if (!PermissionUtil.getInstance().isRuntimePermissionRequired()) {
//                    startPeopleAroundMeActivity();
//                } else {
//                    if (PermissionUtil.getInstance().requestPermission(getActivity(), new ArrayList<String>(Arrays.asList(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)), getResources().getString(R.string.location_permission_message), Constants.REQUEST_CODE_LOCATION_PERMISSION)) {
//                        startPeopleAroundMeActivity();
//                    }
//                }
//            }
//        });
//
//        FloatingActionButton createGroupFab = (FloatingActionButton) v.findViewById(R.id.create_group);
//        createGroupFab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                fabMenu.toggle(true);
//                Intent intent = new Intent(getActivity(), GroupDetailActivity.class);
//                startActivity(intent);
//            }
//        });

        ChatFragment fragment = new ChatFragment();
        mListener = fragment;
        currentFragment = fragment;
        getChildFragmentManager().beginTransaction().replace(com.sports.unity.R.id.childFragmentContainer, fragment).commit();
        return v;
    }

    private void startPeopleAroundMeActivity() {

        Intent intent = new Intent(getActivity(), PeopleAroundMeMap.class);
        startActivity(intent);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Log.i("Adding Child Fragment", "Now");
        view.setFocusableInTouchMode(true);
        view.requestFocus();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_friends: {
                if (currentFragment instanceof ChatFragment) {
                    //do nothing
                } else {
                    ChatFragment fragment = new ChatFragment();
                    currentFragment = fragment;
                    mListener = fragment;
                    getChildFragmentManager().beginTransaction().replace(com.sports.unity.R.id.childFragmentContainer, fragment).commit();
                    buttonContainerLayout.setBackgroundResource(R.drawable.btn_chat_focused);
                    chats.setTextColor(getResources().getColor(R.color.ColorPrimary));
                    contacts.setTextColor(getResources().getColor(R.color.app_theme_blue));
                    others.setTextColor(getResources().getColor(R.color.app_theme_blue));
                    othersUnreadCount.setBackgroundResource(R.drawable.ic_msg_notification);
                    othersUnreadCount.setTextColor(getResources().getColor(android.R.color.white));
                    friendsUnreadCount.setBackgroundResource(R.drawable.ic_msg_notification_white);
                    friendsUnreadCount.setTextColor(getResources().getColor(android.R.color.black));
                }
                break;
            }
            case R.id.fragment_contacts: {
                if (currentFragment instanceof ContactsFragment) {
                    //do nothing
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.INTENT_KEY_CONTACT_FRAGMENT_USAGE, ContactsFragment.USAGE_FOR_CONTACTS);

                    ContactsFragment fragment = new ContactsFragment();
                    mListener = fragment;
                    currentFragment = fragment;
                    fragment.setArguments(bundle);

                    getChildFragmentManager().beginTransaction().replace(com.sports.unity.R.id.childFragmentContainer, fragment).commit();
                    buttonContainerLayout.setBackgroundResource(R.drawable.btn_contacts_focused);
                    contacts.setTextColor(getResources().getColor(R.color.ColorPrimary));
                    chats.setTextColor(getResources().getColor(R.color.app_theme_blue));
                    others.setTextColor(getResources().getColor(R.color.app_theme_blue));
                    othersUnreadCount.setBackgroundResource(R.drawable.ic_msg_notification);
                    othersUnreadCount.setTextColor(getResources().getColor(android.R.color.white));
                    friendsUnreadCount.setBackgroundResource(R.drawable.ic_msg_notification);
                    friendsUnreadCount.setTextColor(getResources().getColor(android.R.color.white));
                }
                break;
            }
            case R.id.fragment_others: {
                if (currentFragment instanceof OthersFragment) {
                    //do nothing
                } else {
                    OthersFragment fragment = new OthersFragment();
                    mListener = fragment;
                    currentFragment = fragment;
                    getChildFragmentManager().beginTransaction().replace(com.sports.unity.R.id.childFragmentContainer, fragment).commit();
                    buttonContainerLayout.setBackgroundResource(R.drawable.btn_others_focused);
                    others.setTextColor(getResources().getColor(R.color.ColorPrimary));
                    contacts.setTextColor(getResources().getColor(R.color.app_theme_blue));
                    chats.setTextColor(getResources().getColor(R.color.app_theme_blue));
                    othersUnreadCount.setBackgroundResource(R.drawable.ic_msg_notification_white);
                    othersUnreadCount.setTextColor(getResources().getColor(android.R.color.black));
                    friendsUnreadCount.setBackgroundResource(R.drawable.ic_msg_notification);
                    friendsUnreadCount.setTextColor(getResources().getColor(android.R.color.white));
                }
                break;
            }
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.sync_contacts) {
            if (PermissionUtil.getInstance().requestPermission(getActivity(), new ArrayList<String>(Arrays.asList(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS)))) {

                ContactsHandler.getInstance().addCallToSyncContacts(getActivity().getApplicationContext());
                showSyncProgress();
                Toast.makeText(getActivity().getApplicationContext(), "Syncing contacts", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Contact permission is denied by you.", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_messages_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        syncProgress = menu.findItem(R.id.menu_progress);
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        if (ContactsHandler.getInstance().isInProcess()) {
            showSyncProgress();
        }
        Activity act = getActivity();
        if (act != null && act instanceof MainActivity) {
            ((MainActivity) act).setSearchView(searchView, menu.findItem(R.id.action_search));
        }
        int searchImgId = android.support.v7.appcompat.R.id.search_button;
        ImageView v = (ImageView) searchView.findViewById(searchImgId);
        v.setImageResource(R.drawable.ic_menu_search);
        searchView.setQueryHint("Search...");
        updateSearchViewUI();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (mListener != null) {
                    mListener.onSearchQuery(newText);
                }
                return true;
            }
        });
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });

    }

    public void showSyncProgress() {

        if (syncProgress != null) {
            syncProgress.setVisible(true);
            syncProgress.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            syncProgress.setActionView(R.layout.menu_progress);
        }
    }

    private void hideSyncProgress() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (syncProgress != null) {
                    syncProgress.setActionView(null);
                    syncProgress.setVisible(false);
                    syncProgress.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                }
            }
        });
    }

    private void updateSearchViewUI() {
        try {
            Field searchCloseButton = SearchView.class.getDeclaredField("mCloseButton");
            searchCloseButton.setAccessible(true);
            ImageView closeBtn = (ImageView) searchCloseButton.get(searchView);
            closeBtn.setImageResource(R.drawable.ic_close_blk);
            Field searchEditField = SearchView.class.getDeclaredField("mSearchSrcTextView");
            searchEditField.setAccessible(true);
            EditText editText = (EditText) searchEditField.get(searchView);
            editText.setTextColor(getResources().getColor(R.color.gray1));
            editText.setBackgroundColor(getResources().getColor(R.color.textColorPrimary));
            editText.setHintTextColor(getResources().getColor(R.color.textColorPrimary));
            this.searchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    childStripLayout.setVisibility(View.GONE);
                    ((MainActivity) getActivity()).enableSearch();
                }
            });
            this.searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    childStripLayout.setVisibility(View.VISIBLE);
                    ((MainActivity) getActivity()).disableSearch();
                    if (mListener != null) {
                        mListener.onSearchQuery("");
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ActivityActionListener activityActionListener = new ActivityActionListener() {
        @Override
        public void handleAction(int id, Object object) {
        }

        @Override
        public void handleAction(int id) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getAndSetUnreadCount();
                }
            });
        }

        @Override
        public void handleMediaContent(int id, String mimeType, Object messageContent, Object mediaContent) {

        }

        @Override
        public void handleMediaContent(int id, String mimeType, Object messageContent, String thumbnailImage, Object mediaContent) {

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        ContactsHandler.getInstance().addCopyCompleteListener(MessagesFragment.this);
        ActivityActionHandler.getInstance().addActionListener(ActivityActionHandler.UNREAD_COUNT_KEY, activityActionListener);
        getAndSetUnreadCount();
        mListener = (OnSearchViewQueryListener) currentFragment;
        Activity act = getActivity();
        if (act != null && act instanceof MainActivity) {
            if (PermissionUtil.getInstance().isRuntimePermissionRequired()) {
                ((MainActivity) getActivity()).addLocationResultListener(this);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ContactsHandler.getInstance().removeCopyCompleteListener();
        ActivityActionHandler.getInstance().removeActionListener(ActivityActionHandler.UNREAD_COUNT_KEY);
    }


    @Override
    public void onPermissionResult(int requestCode, int[] grantResults) {
        if (requestCode == Constants.REQUEST_CODE_LOCATION_PERMISSION) {
            if (PermissionUtil.getInstance().verifyPermissions(grantResults)) {
                startPeopleAroundMeActivity();
            } else {
                PermissionUtil.getInstance().showSnackBar(getActivity(), getString(R.string.permission_denied));
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Activity act = getActivity();
        if (act != null && act instanceof MainActivity) {
            ((MainActivity) getActivity()).removeLocationResultListener();
        }
    }

    @Override
    public void onComplete(boolean success) {
        hideSyncProgress();
        notifyToContactFragment();
    }

    public void notifyToContactFragment() {
        Activity activity = getActivity();
        if (activity != null && activity instanceof MainActivity) {
            ((MainActivity) activity).notifyToContactFragment();
        }
    }
}
