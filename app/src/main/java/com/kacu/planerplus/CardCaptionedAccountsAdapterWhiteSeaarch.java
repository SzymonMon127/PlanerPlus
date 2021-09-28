package com.kacu.planerplus;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CardCaptionedAccountsAdapterWhiteSeaarch extends RecyclerView.Adapter<CardCaptionedAccountsAdapterWhiteSeaarch.CardCaptionedAccountsAdapterWhiteSeaarchViewHolder> implements Filterable {
    private List<UsersProfileItem> exampleList;
    private List<UsersProfileItem> exampleListFull;
    StorageReference storageReference;
    public Listener listener;

    public interface Listener
    {
        void onClick(int position);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }
    class CardCaptionedAccountsAdapterWhiteSeaarchViewHolder extends RecyclerView.ViewHolder {
        CircleImageView  imageView;
        TextView textView1;


        CardCaptionedAccountsAdapterWhiteSeaarchViewHolder(View itemView) {
            super(itemView);
            storageReference = FirebaseStorage.getInstance().getReference();
            imageView = itemView.findViewById(R.id.imageProfile);
            textView1 = itemView.findViewById(R.id.tvEmailAdapter);
// sett item
        }
    }

    CardCaptionedAccountsAdapterWhiteSeaarch(List<UsersProfileItem> exampleList) {
        this.exampleList = exampleList;
        exampleListFull = new ArrayList<>(exampleList);
    }

    @NonNull
    @Override
    public CardCaptionedAccountsAdapterWhiteSeaarchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_captioned_accounts_white,
                parent, false);
        return new CardCaptionedAccountsAdapterWhiteSeaarchViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CardCaptionedAccountsAdapterWhiteSeaarchViewHolder holder, int position) {
        UsersProfileItem currentItem = exampleList.get(position);


        holder.textView1.setText(currentItem.getText1());
        StorageReference profileRef = storageReference.child("users/"+currentItem.getImageResource()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(holder.imageView));
        holder.itemView.setOnClickListener(v -> {
            if (listener != null)
            {
                listener.onClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return exampleList.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<UsersProfileItem> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(exampleListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (UsersProfileItem item : exampleListFull) {
                    if (item.getText1().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            exampleList.clear();
            exampleList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
