package com.nodhan.firebaseassgn;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by nodhan on 14/11/16.
 */

class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    List<String> uris;
    Context context;

    ImageAdapter(List<String> uris, Context context) {
        this.uris = uris;
        this.context = context;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_uploaded_image, parent, false);
        return new ImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Uri uri = Uri.parse(uris.get(position));
        //Loading Image from URL
        Picasso.with(context).load(uri).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return uris.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        ImageViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_view);
        }
    }
}
