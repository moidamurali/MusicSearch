package com.zify.musicsearch.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.zify.musicsearch.R;
import com.zify.musicsearch.model.Artist;
import com.zify.musicsearch.utils.Utils;
import com.zify.musicsearch.utils.thumbnailutils.BitmapCache;
import com.zify.musicsearch.utils.thumbnailutils.ThumbnailCreateor;
import com.zify.musicsearch.view.activities.DetailsActivity;

import java.util.ArrayList;
import java.util.List;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.MusicSearchViewHolder>  implements Filterable {
    private List<Artist> mArtistList;
    private List<Artist> dictionaryWords;
    private List<Artist> filteredList = new ArrayList<>();
    private Context mContext;
    private CustomFilter mFilter;


    public SearchListAdapter(List<Artist> mArtistList, Context context) {
        this.mArtistList = mArtistList;
        this.mContext = context;
        mFilter = new CustomFilter(SearchListAdapter.this);
        dictionaryWords = mArtistList;
        filteredList.addAll(dictionaryWords);
    }

    @Override
    public MusicSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_search_row, parent, false);
        return new MusicSearchViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MusicSearchViewHolder holder, final int position) {
        final Artist mArtist = mArtistList.get(position);

        holder.artistName.setText(mArtist.getName());
        String sourceString = "Currently Streaming:" + "\n" + mArtist.getUrl();
        holder.currentStreaming.setText(Html.fromHtml(sourceString));
        Utils.clickURL(mContext,holder.currentStreaming, sourceString,mArtist.getUrl(),20,true);

        if(mArtist.getImage().get(0).getText()!=null){
            Bitmap found = BitmapCache.GetInstance().GetBitmapFromMemoryCache(mArtist.getImage().get(0).getText());
            if (found != null) {
                holder.artistImage.setImageBitmap(found);
            }else{
                ThumbnailCreateor.BitmapWorkerTask task = new ThumbnailCreateor.BitmapWorkerTask(holder.artistImage,  mArtist.getImage().get(0).getText());

                ThumbnailCreateor.AsyncDrawable downloadedDrawable = new ThumbnailCreateor.AsyncDrawable(mContext.getResources(), Utils.getResizedBitMap() ,task);
                holder.artistImage.setImageDrawable(downloadedDrawable);
                task.execute(String.valueOf(mArtist.getImage().get(0).getText()));
            }

        }


        holder.layoutParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.checkConnection(mContext)) {
                    Intent intent = new Intent(mContext, DetailsActivity.class);
                    intent.putExtra("artist_name", mArtist.getName());
                    mContext.startActivity(intent);
                } else {
                    Toast.makeText(mContext, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });




    }

    @Override
    public int getItemCount() {
        return mArtistList.size();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public class MusicSearchViewHolder extends RecyclerView.ViewHolder {
        private TextView artistName;
        private TextView currentStreaming;
        private ImageView artistImage;
        private LinearLayout layoutParent;

        public MusicSearchViewHolder(View itemView) {
            super(itemView);
            layoutParent = itemView.findViewById(R.id.root_view);
            artistName = itemView.findViewById(R.id.tv_title);
            currentStreaming = itemView.findViewById(R.id.tv_description);
            artistImage = itemView.findViewById(R.id.image_view);
        }
    }


    public class CustomFilter extends Filter {
        private SearchListAdapter mAdapter;
        private CustomFilter(SearchListAdapter mAdapter) {
            super();
            this.mAdapter = mAdapter;
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredList.clear();
            final FilterResults results = new FilterResults();
            if (constraint.length() == 0) {
                filteredList.addAll(dictionaryWords);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                for (final Artist  mWords : dictionaryWords) {
                    if (mWords.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(mWords);
                    }
                }
            }
            Log.v("Count Number " , ""+filteredList.size());
            results.values = filteredList;
            results.count = filteredList.size();

            mArtistList = filteredList;
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            Log.v("publishResults", ""+((List<String>) results.values).size());
            this.mAdapter.notifyDataSetChanged();
        }
    }



}
