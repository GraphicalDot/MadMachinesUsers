package com.sports.unity.scores.controller.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.scoredetails.CommentriesModel;
import com.sports.unity.scores.model.ScoresJsonParser;
import com.sports.unity.scores.model.football.MatchCommentaryJsonCaller;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by madmachines on 6/1/16.
 */
public class BroadcastListAdapter extends RecyclerView.Adapter<BroadcastListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<CommentriesModel> list;

    private String sportsType;
   // private MatchCommentaryJsonCaller jsonCaller = new MatchCommentaryJsonCaller();

    public BroadcastListAdapter(String sportsType, ArrayList<CommentriesModel> list, Context activity) {
        this.sportsType = sportsType;
        this.list = list;
        this.context = activity;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView broadcast;
        private TextView commentTime;
        private ImageView commentImage;
        private View lvDivider;
        private LinearLayout backGroundColor;

        private View view;

        public ViewHolder(View v) {
            super(v);

            view = v;
            broadcast = (TextView) v.findViewById(R.id.broadcast);
            commentTime = (TextView) v.findViewById(R.id.comment_time);
            commentImage  = (ImageView) v.findViewById(R.id.comment_image);
            lvDivider = v.findViewById(R.id.lv_divider);
            backGroundColor = (LinearLayout) v.findViewById(R.id.back_ground_color);
            /*commentTime.setTypeface(FontTypeface.getInstance(view.getContext()).getRobotoCondensedBold());
            broadcast.setTypeface(FontTypeface.getInstance(view.getContext()).getRobotoMedium());*/
        }
    }

    @Override
    public BroadcastListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.score_broadcast_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(BroadcastListAdapter.ViewHolder holder, int position) {
       //jsonCaller.setJsonObject(jsonObject);

        try {
            if(list != null ) {

                CommentriesModel nextObject = null;
                CommentriesModel jsonObject = list.get(position);
                if(position<getItemCount()){
                    nextObject = list.get(position) ;
                }
                if (sportsType.equals(ScoresJsonParser.CRICKET)) {

                    if(jsonObject.getComment() != null) {
                        holder.broadcast.setText(Html.fromHtml(jsonObject.getComment()));
                        if(jsonObject.getComment().contains("FOUR") ||  jsonObject.getComment().contains("SIX")){

                            holder.broadcast.setTextColor(context.getResources().getColor(R.color.app_theme_blue));
                            holder.commentTime.setTextColor(context.getResources().getColor(R.color.app_theme_blue));
                        }else if(jsonObject.getComment().contains("OUT") || jsonObject.getComment().contains("WICKET")){

                            holder.broadcast.setTextColor(context.getResources().getColor(R.color.brick_red));
                            holder.commentTime.setTextColor(context.getResources().getColor(R.color.brick_red));

                        }else{
                            holder.broadcast.setTextColor(context.getResources().getColor(R.color.news_headline_mini));
                            holder.commentTime.setTextColor(context.getResources().getColor(R.color.news_headline_mini));
                        }

                    }




                    if("-1.0".equalsIgnoreCase(jsonObject.getOver())){
                        holder.commentImage.setImageResource(R.drawable.commentary_icon);
                        holder.commentTime.setVisibility(View.GONE);
                    }else{
                        holder.commentImage.setImageResource(R.drawable.grey_ring);
                        holder.commentTime.setVisibility(View.VISIBLE);
                        holder.commentTime.setText(jsonObject.getOver());

                    }
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.lvDivider .getLayoutParams();
                    if(jsonObject.getOver().contains(".1") && !nextObject.getOver().contains(".1")){
                        params.height = 3;
                        //holder.lvDivider.setBackground();

                    }else{
                        params.height = 1;
                    }
                    holder.lvDivider.setLayoutParams(params);

                } else if (sportsType.equals(ScoresJsonParser.FOOTBALL)) {
                    holder.broadcast.setText(jsonObject.getComment());
                    holder.commentTime.setText(jsonObject.getMinute());
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
   @Override
    public int getItemCount() {
        return list.size();
    }

}
