package it.fitnesschallenge.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import it.fitnesschallenge.R;
import it.fitnesschallenge.model.room.entity.Room;

public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.ViewHolder> {

    private static final String TAG = "RoomsAdapter";

    private OnClickListener mListener;
    private List<Room> mRoomList;

    public RoomsAdapter(List<Room> mRoomList) {
        this.mRoomList = mRoomList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.rooms_layout, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mCardView.setTag("Item_" + position);
        holder.mRoomName.setText(mRoomList.get(position).getRoomName());
        holder.mMembersNumber.setText(NumberFormat.getInstance(Locale.getDefault())
                .format(mRoomList.get(position).getRoomMembers()));
    }

    @Override
    public int getItemCount() {
        return mRoomList.size();
    }

    public interface OnClickListener {
        void onClick(int position, ViewHolder view);
    }

    public void setOnClickListener(OnClickListener listener) {
        this.mListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView mCardView;
        private CircularImageView mImageView;
        private TextView mRoomName;
        private TextView mMembersNumber;

        ViewHolder(@NonNull View itemView, final OnClickListener mListener) {
            super(itemView);
            final ViewHolder holder = this;
            mCardView = itemView.findViewById(R.id.rooms_card_view);
            mImageView = itemView.findViewById(R.id.rooms_layout_room_image);
            mRoomName = itemView.findViewById(R.id.rooms_layout_room_name);
            mMembersNumber = itemView.findViewById(R.id.rooms_layout_members);
            mRoomName.setTransitionName("room_name_shared_" + getAdapterPosition());
            mImageView.setTransitionName("room_image_shared_" + getAdapterPosition());
            mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClick(getAdapterPosition(), holder);
                }
            });
        }

        public TextView getRoomName() {
            return mRoomName;
        }

        public CircularImageView getImageRoom() {
            return mImageView;
        }
    }
}
