package com.kacu.planerplus;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;


public class CardCaptionedAccountsAdapterWhite extends RecyclerView.Adapter<CardCaptionedAccountsAdapterWhite.ViewHolder> {

    private final String[] imagesId;
    private final String[] email;
    StorageReference storageReference;
    public Listener listener;

    public interface Listener
    {
        void onClick(int position);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }


    public CardCaptionedAccountsAdapterWhite(String[] imagesId, String[] email) {
        this.imagesId = imagesId;
        this.email = email;
    }

    @NotNull
    @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
  {
      CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_captioned_accounts_white, parent, false);
      return new ViewHolder(cv);
  }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final CardView cardView = holder.cardView;



        TextView textView = cardView.findViewById(R.id.tvEmailAdapter);
        textView.setText(email[position]);
        storageReference = FirebaseStorage.getInstance().getReference();
        CircleImageView imageView = cardView.findViewById(R.id.imageProfile);
        StorageReference profileRef = storageReference.child("users/"+imagesId[position]+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(imageView));


        cardView.setOnClickListener(v -> {
            if (listener != null)
            {
                listener.onClick(position);
            }
        });



    }

    @Override
    public int getItemCount() {
        return imagesId.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {
        private final CardView cardView;

        public ViewHolder(CardView v)
        {
            super(v);
            cardView = v;
        }

    }
}

