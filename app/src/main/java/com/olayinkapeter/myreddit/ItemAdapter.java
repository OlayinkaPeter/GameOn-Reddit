package com.olayinkapeter.myreddit;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Olayinka_Peter on 4/25/2018.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {

    private List<Item> itemList;
    Context context;

    public ItemAdapter(List<Item> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row, parent, false);
        return new MyViewHolder(itemview);
    }
    @Override
    public void onBindViewHolder(ItemAdapter.MyViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.title.setText(item.getTitle());
        holder.score.setText(item.getScore());
        holder.subreddit.setText(item.getSubreddit());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, score, subreddit, url;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            title = (TextView) itemView.findViewById(R.id.title);
            score = (TextView) itemView.findViewById(R.id.score);
            subreddit = (TextView) itemView.findViewById(R.id.subreddit);
        }
    }
}
