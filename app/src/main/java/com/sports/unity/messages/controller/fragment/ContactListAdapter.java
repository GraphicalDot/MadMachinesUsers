package com.sports.unity.messages.controller.fragment;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.messages.controller.model.Contacts;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by madmachines on 2/9/15.
 */
public class ContactListAdapter extends ArrayAdapter<Contacts> implements View.OnClickListener, StickyListHeadersAdapter {

    private final Activity context;
    private LayoutInflater inflater;

    private ArrayList<Contacts> originalContactList;
    private ArrayList<Contacts> inUseContactListForAdapter;
    private Button invite;
    int frequentContactCount = 0;

    private int itemLayoutId = 0;
    private boolean multipleSelection = false;

    public ContactListAdapter(Activity context, int resource, ArrayList<Contacts> list, boolean multipleSelection, int frequentContactCount) {
        super(context, resource, list);
        this.context = context;
        this.inUseContactListForAdapter = list;
        itemLayoutId = resource;
        this.multipleSelection = multipleSelection;
        inflater = context.getLayoutInflater();
        this.frequentContactCount = frequentContactCount;
    }

    public View getView(int position, View view, ViewGroup parent) {
        Contacts contacts = getItem(position);

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(itemLayoutId, null, true);

        ImageView userIcon = (ImageView) rowView.findViewById(R.id.user_icon);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.contact_name);
        txtTitle.setTypeface(FontTypeface.getInstance(context.getApplicationContext()).getRobotoRegular());

        TextView status = (TextView) rowView.findViewById(R.id.status);
        status.setTypeface(FontTypeface.getInstance(context.getApplicationContext()).getRobotoLight());

        if (itemLayoutId == R.layout.list_contact_msgs) {
            invite = (Button) rowView.findViewById(R.id.btn_invite);
            invite.setTypeface(FontTypeface.getInstance(context.getApplicationContext()).getRobotoRegular());
            invite.setOnClickListener(this);

            if (contacts.registered) {
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

        if (contacts.image != null) {
            userIcon.setImageBitmap(BitmapFactory.decodeByteArray(contacts.image, 0, contacts.image.length));
        }
        return rowView;

    }

    @Override
    public void onClick(View v) {
        Toast.makeText(context, "Not implemented yet", Toast.LENGTH_SHORT).show();
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

    public ArrayList<Contacts> getInUseContactListForAdapter() {
        return inUseContactListForAdapter;
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

        if (position < frequentContactCount) {
            headerText = "Recents";
        } else {
            headerText = "" + inUseContactListForAdapter.get(position).toString().subSequence(0, 1).charAt(0);
        }
        holder.text.setText(headerText);
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        if (position < frequentContactCount) {
            return (long) 0.0;
        } else {
            return inUseContactListForAdapter.get(position).name.subSequence(0, 1).charAt(0);
        }

    }

    class HeaderViewHolder {
        TextView text;
    }
}



