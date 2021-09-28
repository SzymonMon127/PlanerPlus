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


public class CardCaptionedAccountsAdapterWhiteTasks extends RecyclerView.Adapter<CardCaptionedAccountsAdapterWhiteTasks.ViewHolder> {

    private final String[] hour;
    private final String[] user;
    private final String[] task;
    public Listener listener;

    public interface Listener
    {
        void onClick(int position);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }


    public CardCaptionedAccountsAdapterWhiteTasks(String[] hour, String[] user, String[] task) {
        this.hour = hour;
        this.user = user;
        this.task = task;
    }

    @NotNull
    @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
  {
      CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_captioned_calendar_task, parent, false);
      return new ViewHolder(cv);
  }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final CardView cardView = holder.cardView;



        TextView textView = cardView.findViewById(R.id.tvUser);
        textView.setText(user[position]);
        TextView textView1 = cardView.findViewById(R.id.tvTask);
        textView1.setText(task[position]);
        TextView textView2 = cardView.findViewById(R.id.tvHour);
        textView2.setText(hour[position]);



        cardView.setOnClickListener(v -> {
            if (listener != null)
            {
                listener.onClick(position);
            }
        });



    }

    @Override
    public int getItemCount() {
        return task.length;
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

