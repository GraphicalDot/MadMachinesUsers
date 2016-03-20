package com.sports.unity.messages.controller.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.messages.controller.model.Contacts;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by madmachines on 2/9/15.
 */
public class ContactListAdapter extends ArrayAdapter<Contacts> implements StickyListHeadersAdapter, Filterable {

    private final Activity context;
    private LayoutInflater inflater;

    private ArrayList<String> previouslySelectedMembersList = new ArrayList<>();
    private ArrayList<Contacts> selectedMemberList;
    private ArrayList<Contacts> inUseContactListForAdapter;
    private Button invite;
    int registeredContactCount = 0;

    private int itemLayoutId = 0;
    private boolean multipleSelection = false;

    private ItemFilter contactFilter;
    private ArrayList<Contacts> finalContact;
    private ArrayList<Contacts> usedContact;

    public ContactListAdapter(Activity context, int resource, ArrayList<Contacts> list, boolean multipleSelection, int registeredContactCount, ArrayList<Contacts> selectedMembersList) {
        super(context, resource, list);
        this.context = context;
        this.inUseContactListForAdapter = list;
        itemLayoutId = resource;
        this.multipleSelection = multipleSelection;
        inflater = context.getLayoutInflater();
        this.registeredContactCount = registeredContactCount;
        this.selectedMemberList = selectedMembersList;
        contactFilter = new ItemFilter();
        usedContact = finalContact = list;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        Contacts contacts = null;
        try {
            contacts = usedContact.get(position);
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(itemLayoutId, null, true);

            ImageView userIcon = (ImageView) rowView.findViewById(R.id.user_icon);

            TextView txtTitle = (TextView) rowView.findViewById(R.id.contact_name);
            txtTitle.setTypeface(FontTypeface.getInstance(context.getApplicationContext()).getRobotoRegular());

            TextView status = (TextView) rowView.findViewById(R.id.status);
            status.setTypeface(FontTypeface.getInstance(context.getApplicationContext()).getRobotoLight());

            if (itemLayoutId == R.layout.list_contact_msgs) {
                Button invite = (Button) rowView.findViewById(R.id.btn_invite);
                invite.setTypeface(FontTypeface.getInstance(context.getApplicationContext()).getRobotoRegular());

                if (contacts.isRegistered()) {
                    invite.setVisibility(View.INVISIBLE);
                }
            } else if (itemLayoutId == R.layout.list_item_members) {
                if (multipleSelection) {
                    rowView.findViewById(R.id.checkbox).setVisibility(View.VISIBLE);
                } else {
                    rowView.findViewById(R.id.checkbox).setVisibility(View.GONE);
                }
            }

            txtTitle.setText(contacts.name);
            status.setText(contacts.status);

            if (previouslySelectedMembersList.contains(contacts.jid)) {
                CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.checkbox);
                if(checkBox != null) {
                    checkBox.setChecked(true);
                }

                rowView.setTag(new Boolean(false));
            } else {
                if (selectedMemberList != null && selectedMemberList.contains(contacts)) {
                    CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.checkbox);
                    if(checkBox != null) {
                        checkBox.setChecked(true);
                    }
                } else {
                    CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.checkbox);
                    if(checkBox != null) {
                        checkBox.setChecked(false);
                    }
                }
                rowView.setTag(new Boolean(true));
            }

            txtTitle.setText(contacts.name);
            status.setText(contacts.status);

            if (contacts.image != null) {
                userIcon.setImageBitmap(BitmapFactory.decodeByteArray(contacts.image, 0, contacts.image.length));
            }
            return rowView;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //    public void filter(String filterText) {
//        if (filterText.length() == 0) {
//            inUseContactListForAdapter.clear();
//            inUseContactListForAdapter = null;
//
//            inUseContactListForAdapter = originalContactList;
//            originalContactList = null;
//        } else {
//            if( originalContactList == null ){
//                originalContactList = inUseContactListForAdapter;
//            }
//
//            ArrayList<Contacts> contacts = originalContactList;
//            inUseContactListForAdapter.clear();
//            for (Contacts c : contacts) {
//                if (c.name.contains(filterText)) {
//                    inUseContactListForAdapter.add(c);
//                }
//            }
//        }
//        super.notifyDataSetChanged();
//    }

    public void refreshSelectedMembers(ArrayList<Contacts> selectedMembersList) {
        this.selectedMemberList = selectedMembersList;
    }

    public ArrayList<Contacts> getInUseContactListForAdapter() {
        return inUseContactListForAdapter;
    }

    public void setPreviouslySelectedMembersList(ArrayList<String> previouslySelectedMembersList) {
        this.previouslySelectedMembersList = previouslySelectedMembersList;
    }

    public ArrayList<String> getPreviouslySelectedMembersList() {
        return previouslySelectedMembersList;
    }

    public ArrayList<Contacts> getUsedContact() {
        return usedContact;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        String headerText = "";
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = inflater.inflate(R.layout.list_header, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.list_header_title);
            holder.text.setTypeface(FontTypeface.getInstance(context).getRobotoMedium());
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        if (usedContact.size() == finalContact.size()) {
            if (position >= registeredContactCount) {
                headerText = "Invite People to Sports Unity";
            } else {
                headerText = "" + getHeader(position);
            }
        } else {
            headerText = "" + getHeader(position);
        }
        holder.text.setText(headerText);
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        if (usedContact.size() == finalContact.size()) {
            if (position >= registeredContactCount) {
                return (long) 0.0;
            } else {
                return getHeader(position);
            }
        } else {
            return getHeader(position);
        }

    }

    @SuppressLint("NewApi")
    public char getHeader(int position) {
        char c = usedContact.get(position).name.subSequence(0, 1).charAt(0);
        boolean isAlphabetic = Character.isLetter(c);
        if (isAlphabetic) {
            c = Character.toUpperCase(c);
        } else {
            //do nothing
        }
        return c;
    }

    class HeaderViewHolder {
        TextView text;
    }

    @Override
    public Filter getFilter() {
        return contactFilter;
    }

    @Override
    public int getCount() {
        return usedContact.size();
    }

    @Override
    public Contacts getItem(int position) {
        return usedContact.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();
            final ArrayList<Contacts> nlist = new ArrayList<Contacts>();

            for (Contacts c : finalContact) {
                if (!nlist.contains(c)) {
                    if (c.name.toLowerCase().contains(filterString)) {
                        nlist.add(c);
                    }
                }
            }

            results.values = nlist;
            results.count = nlist.size();
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            usedContact = (ArrayList<Contacts>) results.values;
            ContactListAdapter.this.notifyDataSetChanged();
        }

    }

    public void refreshContacts() {
        usedContact = finalContact;
        this.notifyDataSetChanged();
    }

    public void updateContacts(ArrayList<Contacts> list) {
        this.inUseContactListForAdapter = list;
        contactFilter = new ItemFilter();
        usedContact = finalContact = list;
        this.notifyDataSetChanged();
    }
}

