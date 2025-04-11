package com.example.farm_to_table;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messageList;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView timestampTextView;
        ImageView messageImageView;

        public MessageViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
            messageImageView = itemView.findViewById(R.id.ImageView); // Add ImageView
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);

        if (message.getText().startsWith("content://")) {
            // It's an image message
            holder.messageTextView.setVisibility(View.GONE);
            holder.messageImageView.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(Uri.parse(message.getText()))
                    .into(holder.messageImageView);
        } else {
            // It's a text message
            holder.messageTextView.setVisibility(View.VISIBLE);
            holder.messageImageView.setVisibility(View.GONE);
            holder.messageTextView.setText(message.getText());
        }

        holder.timestampTextView.setText(message.getTimestamp());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
