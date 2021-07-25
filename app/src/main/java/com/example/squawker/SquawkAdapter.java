package com.example.squawker;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.squawker.provider.SquawkerContract;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SquawkAdapter extends RecyclerView.Adapter<SquawkAdapter.SquawkViewHolder>{

    private Context mContext;
    private Cursor mData;

    private static SimpleDateFormat sDateFormat = new SimpleDateFormat("dd MMM");

    private static final long MINUTE_MILLIS = 1000 * 60;
    private static final long HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final long DAY_MILLIS = 24 * HOUR_MILLIS;

    public SquawkAdapter(Context context){
        this.mContext = context;
    }

    @NonNull
    @Override
    public SquawkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_squawk_list, parent, false);
        return new SquawkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SquawkViewHolder holder, int position) {
        mData.moveToPosition(position);
        String message = mData.getString(MainActivity.COL_NUM_MESSAGE);
        String author = mData.getString(MainActivity.COL_NUM_AUTHOR);
        String authorKey = mData.getString(MainActivity.COL_NUM_AUTHOR_KEY);
        // Get the date for displaying
        long dateMillis = mData.getLong(MainActivity.COL_NUM_DATE);
        String date = "";
        long now = System.currentTimeMillis();

        // Change how the date is displayed depending on whether it was written in the last minute,
        // the hour, etc.
        if (now - dateMillis < (DAY_MILLIS)) {
            if (now - dateMillis < (HOUR_MILLIS)) {
                long minutes = Math.round((now - dateMillis) / MINUTE_MILLIS);
                date = minutes + "m";
            } else {
                long minutes = Math.round((now - dateMillis) / HOUR_MILLIS);
                date = minutes + "h";
            }
        } else {
            Date dateDate = new Date(dateMillis);
            date = sDateFormat.format(dateDate);
        }
        // Add a dot to the date string
        date = "\u2022 " + date;
        holder.authorMessage.setText(message);
        holder.authorName.setText(author);
        holder.dateTextView.setText(date);

        // Choose the correct, and in this case, locally stored asset for the instructor. If there
        // were more users, you'd probably download this as part of the message.
        switch (authorKey) {
            case SquawkerContract.ASSER_KEY:
                holder.authorImage.setImageResource(R.drawable.asser);
                break;
            case SquawkerContract.CEZANNE_KEY:
                holder.authorImage.setImageResource(R.drawable.cezanne);
                break;
            case SquawkerContract.JLIN_KEY:
                holder.authorImage.setImageResource(R.drawable.jlin);
                break;
            case SquawkerContract.LYLA_KEY:
                holder.authorImage.setImageResource(R.drawable.lyla);
                break;
            case SquawkerContract.NIKITA_KEY:
                holder.authorImage.setImageResource(R.drawable.nikita);
                break;
            default:
                holder.authorImage.setImageResource(R.drawable.test);
        }
    }

    @Override
    public int getItemCount() {
        if (mData == null)
            return 0;
        else
            return mData.getCount();
    }
    public void swapCursor(Cursor newCursor){
        mData = newCursor;
        notifyDataSetChanged();
    }


    static class SquawkViewHolder extends RecyclerView.ViewHolder{
        final TextView authorName;
        final TextView authorMessage;
        final ImageView authorImage;
        final TextView dateTextView;
        public SquawkViewHolder(@NonNull View itemView) {
            super(itemView);
            authorImage = itemView.findViewById(R.id.author_image_view);
            authorName = itemView.findViewById(R.id.author_text_view);
            authorMessage = itemView.findViewById(R.id.message_text_view);
            dateTextView = itemView.findViewById(R.id.date_text_view);
        }
    }

}
