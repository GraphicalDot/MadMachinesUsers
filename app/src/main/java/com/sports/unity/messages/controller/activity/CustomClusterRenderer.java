package com.sports.unity.messages.controller.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.sports.unity.R;
import com.sports.unity.common.controller.CustomAppCompatActivity;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.messages.controller.model.Person;

/**
 * Created by manish on 03/03/16.
 */
public class CustomClusterRenderer extends DefaultClusterRenderer<Person> {

    private IconGenerator clusterIconGenerator;
    private ImageView icon;
    private TextView textView;
    private PeopleService peopleService;
    private String username;

    public CustomClusterRenderer(Context context, GoogleMap map, ClusterManager<Person> clusterManager) {
        super(context, map, clusterManager);
        this.clusterIconGenerator = new IconGenerator(context);
        Activity act = (CustomAppCompatActivity) context;
        peopleService = (PeopleService) context;
        View view = act.getLayoutInflater().inflate(R.layout.cluster_view, null);
        icon = (ImageView) view.findViewById(R.id.cluster_icon);
        this.clusterIconGenerator.setContentView(view);
        this.username = TinyDB.getInstance(context).getString(TinyDB.KEY_USER_JID);

    }

    @Override
    protected void onBeforeClusterItemRendered(Person item, MarkerOptions markerOptions) {
        //super.onBeforeClusterItemRendered(item, markerOptions);
        if (item.isFriend()) {
            icon.setImageResource(R.drawable.ic_marker_my_friends);
//            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_my_friends));
        } else if (item.isCommonInterest()) {
            icon.setImageResource(R.drawable.ic_marker_ppl_with_same_int);
//            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_ppl_with_same_int));
        } else if (item.getUsername().equalsIgnoreCase(username)) {
            //Set Marker for self user
        } else {
            icon.setImageResource(R.drawable.ic_marker_su_users);
//            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_su_users));
        }
        // super.onBeforeClusterItemRendered(item, markerOptions);
        clusterIconGenerator.setBackground(icon.getDrawable());
        Bitmap markerIcon = clusterIconGenerator.makeIcon("");
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(markerIcon));
    }

    @Override
    protected void onClusterItemRendered(Person clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
        if (clusterItem.getUsername().equalsIgnoreCase(username)) {
            marker.setDraggable(true);
        }
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<Person> cluster, MarkerOptions markerOptions) {
        super.onBeforeClusterRendered(cluster, markerOptions);
        String count = "1+";
        if (cluster.getSize() > 100) {
            count = "99+";
        } else if (cluster.getSize() > 10 && cluster.getSize() < 100) {
            int numeric = cluster.getSize() / 10;
            numeric = numeric * 10;
            count = numeric + "+";
        }
        clusterIconGenerator.setBackground(icon.getDrawable());
        icon.setImageResource(R.drawable.ic_marker_cluster);
        Bitmap icon = clusterIconGenerator.makeIcon(count);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<Person> cluster) {
        return cluster.getSize() > 1;
    }
}
