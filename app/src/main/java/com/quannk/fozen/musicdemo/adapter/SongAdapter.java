package com.quannk.fozen.musicdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quannk.fozen.musicdemo.R;
import com.quannk.fozen.musicdemo.item.ItemSong;

import java.util.ArrayList;

/**
 * Created by Foze on 25/04/2017.
 */

public class SongAdapter extends RecyclerView.Adapter implements Filterable {
    private SongClick songClick;
    private ArrayList<ItemSong> list;
    private ArrayList<ItemSong> listSongsDisplayed;
    private Context mContext;
    private int position = 0;

    public SongAdapter(Context mContext, ArrayList<ItemSong> list) {
        this.mContext = mContext;
        this.list = list;
        listSongsDisplayed=list;
    }

    public void setSongClick(SongClick songClick) {
        this.songClick = songClick;
    }

    @Override
    public Filter getFilter() {
        Filter filter =new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<ItemSong> filArrayList =new ArrayList<>();

                if (constraint == null) {
                    filArrayList.addAll(list);
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < list.size(); i++) {
                        String songName = list.get(i).getName();
                        songName = songName.toString().toLowerCase();

                        if (songName.indexOf(constraint.toString()) > -1) {
                            filArrayList.add(list.get(i));
                        }
                    }
                }
                results.values = filArrayList;
                results.count = filArrayList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listSongsDisplayed = (ArrayList<ItemSong>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }

    private class SongHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvAlbum;
        private ImageView iv_playsong;
        private LinearLayout linearLayout;

        public SongHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvAlbum = (TextView) itemView.findViewById(R.id.tv_album);
            iv_playsong = (ImageView) itemView.findViewById(R.id.iv_playsong);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.ln_itemsong);
        }
    }

    public interface SongClick {
        void click(int position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemSong = View.inflate(mContext, R.layout.item_song, null);
        RecyclerView.LayoutParams params
                = new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
        itemSong.setLayoutParams(params);
        return new SongHolder(itemSong);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        SongHolder songHolder = (SongHolder) holder;
        songHolder.tvName.setText(list.get(position).getName());
        songHolder.tvAlbum.setText(list.get(position).getAlbum());

        songHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songClick.click(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
