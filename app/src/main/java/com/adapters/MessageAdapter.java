package com.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.activities.R;
import com.models.Message;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Message> messages;
    private Context context;
    private int meuId;

    private static final int TYPE_SENT = 1;
    private static final int TYPE_RECEIVED = 2;

    public MessageAdapter(ArrayList<Message> messages, Context context, int meuId) {
        this.messages = messages;
        this.context = context;
        this.meuId = meuId;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        return message.getSender_id() == meuId ? TYPE_SENT : TYPE_RECEIVED;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if (holder instanceof SentMessageViewHolder) {
            ((SentMessageViewHolder) holder).bind(message);
        } else {
            ((ReceivedMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView content;

        SentMessageViewHolder(View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.messageContent);
        }

        void bind(Message message) {
            content.setText(message.getContent());
        }
    }

    public class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView content, senderName;

        ReceivedMessageViewHolder(View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.senderName);
            content = itemView.findViewById(R.id.messageContent);
        }

        void bind(Message message) {
            senderName.setText(message.getSender_name());
            content.setText(message.getContent());
        }
    }

    public void updateList(ArrayList<Message> newMessages) {
        messages.clear();
        messages.addAll(newMessages);
        notifyDataSetChanged();
    }
}

