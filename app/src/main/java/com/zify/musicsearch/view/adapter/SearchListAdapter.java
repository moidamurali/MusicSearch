package com.zify.musicsearch.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.zify.musicsearch.R;
import com.zify.musicsearch.model.Artist;
import com.zify.musicsearch.utils.thumbnailutils.BitmapCache;
import com.zify.musicsearch.utils.thumbnailutils.ThumbnailCreateor;
import com.zify.musicsearch.view.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.MusicSearchViewHolder>  implements Filterable {
    private List<Artist> mArtistList;
    private List<Artist> dictionaryWords;
    private List<Artist> filteredList = new ArrayList<>();
    private Context mContext;
    private RecyclerItemClickListener recyclerItemClickListener;
    Bitmap mBitmap;
    private CustomFilter mFilter;


    public SearchListAdapter(List<Artist> mArtistList, Context context, RecyclerItemClickListener recyclerItemClickListener) {
        this.mArtistList = mArtistList;
        this.mContext = context;
        this.recyclerItemClickListener = recyclerItemClickListener;
        mFilter = new CustomFilter(SearchListAdapter.this);
        dictionaryWords = mArtistList;
        filteredList.addAll(dictionaryWords);

        int w = 100, h = 100;
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        mBitmap = Bitmap.createBitmap(w, h, conf);
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
        String sourceString = "Currently Streaming:" + "\n \n" + mArtist.getUrl();
        holder.currentStreaming.setText(Html.fromHtml(sourceString));
        clickURL(holder.currentStreaming, sourceString,mArtist.getUrl());

        if(mArtist.getImage().get(0).getText()!=null){
            Bitmap found = BitmapCache.GetInstance().GetBitmapFromMemoryCache(mArtist.getImage().get(0).getText());
            if (found != null) {
                holder.artistImage.setImageBitmap(found);
            }else{
                ThumbnailCreateor.BitmapWorkerTask task = new ThumbnailCreateor.BitmapWorkerTask(holder.artistImage,  mArtist.getImage().get(0).getText());

                ThumbnailCreateor.AsyncDrawable downloadedDrawable = new ThumbnailCreateor.AsyncDrawable(mContext.getResources(), mBitmap ,task);
                holder.artistImage.setImageDrawable(downloadedDrawable);
                task.execute(String.valueOf(mArtist.getImage().get(0).getText()));
            }

        }


        holder.layoutParent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

              recyclerItemClickListener.onItemClick(mArtistList.get(position));

                return true;
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

    private void clickURL(TextView textView, String value,final String url){
        SpannableString ss = new SpannableString(value);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                mContext.startActivity(intent);
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        ss.setSpan(clickableSpan, 22, value.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(bss, 0, 20, Spannable.SPAN_INCLUSIVE_INCLUSIVE);


        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(Color.TRANSPARENT);
    }

}
