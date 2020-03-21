package it.fitnesschallenge.adapter;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ItemTouchHelperCallBack2 extends ItemTouchHelper.Callback {

    private final ItemTouchHelperContract2 mItemTouchHelperContract;

    public ItemTouchHelperCallBack2(ItemTouchHelperContract2 mItemTouchHelperContract) {
        this.mItemTouchHelperContract = mItemTouchHelperContract;
    }


    public interface ItemTouchHelperContract2 {
        void onRowMoved(int fromPosition, int toPosition);

        void onRowSelected(HomeAdapter.ViewHolder viewHolder);

        void onRowClear(HomeAdapter.ViewHolder viewHolder);

    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        mItemTouchHelperContract.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }


    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE)
            if (viewHolder instanceof HomeAdapter.ViewHolder) {
                HomeAdapter.ViewHolder showViewHolder = (HomeAdapter.ViewHolder) viewHolder;
                mItemTouchHelperContract.onRowSelected(showViewHolder);
            }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (viewHolder instanceof HomeAdapter.ViewHolder) {
            HomeAdapter.ViewHolder showViewHolder = (HomeAdapter.ViewHolder) viewHolder;
            mItemTouchHelperContract.onRowClear(showViewHolder);
        }
    }


}
