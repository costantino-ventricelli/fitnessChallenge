package it.fitnesschallenge;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

import it.fitnesschallenge.adapter.RoomsAdapter;
import it.fitnesschallenge.model.view.StatisticsRoomsViewModel;

public class Rooms extends Fragment {

    private static final String TAG = "Room";
    private static final String ROOM = "room";

    private RecyclerView mRecyclerView;
    private RoomsAdapter mRoomsAdapter;

    public Rooms() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rooms, container, false);

        mRecyclerView = view.findViewById(R.id.fragment_rooms_recyclerview);

        final MaterialButton createNewRoom = view.findViewById(R.id.rooms_create_new_room_button);

        StatisticsRoomsViewModel mViewModel = ViewModelProviders.of(getActivity()).get(StatisticsRoomsViewModel.class);

        /* TODO: Accesso online per prelevare le room.
        mViewModel.getAllRooms().observe(getViewLifecycleOwner(), new Observer<List<it.fitnesschallenge.model.room.entity.Room>>() {
            @Override
            public void onChanged(final List<it.fitnesschallenge.model.room.entity.Room> rooms) {
                mRoomsAdapter = new RoomsAdapter(rooms);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mRecyclerView.setAdapter(mRoomsAdapter);
                mRoomsAdapter.setOnClickListener(new RoomsAdapter.OnClickListener() {
                    @Override
                    public void onClick(int position, final RoomsAdapter.ViewHolder view) {
                        Log.d(TAG, "Click su room alla posizione: " + position);
                        Intent intent = new Intent(getActivity(), RoomActivity.class);
                        intent.putExtra(ROOM, rooms.get(position));
                        ActivityOptions options = ActivityOptions
                                .makeSceneTransitionAnimation(getActivity(),
                                        Pair.create((View) view.getRoomName(), "room_name"),
                                        Pair.create((View) view.getImageRoom(), "room_image"),
                                        Pair.create((View) view.getRoomCreator(), "room_creator"));
                        getActivity().startActivity(intent, options.toBundle());
                    }
                });
            }
        });*/

        createNewRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewRoomActivity.class);
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(getActivity(),
                                createNewRoom, "new_room");
                getActivity().startActivity(intent, options.toBundle());
            }
        });
        return view;
    }
}
