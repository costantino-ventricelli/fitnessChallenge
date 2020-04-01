
package it.fitnesschallenge;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import it.fitnesschallenge.model.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditList extends Fragment {

    private static final String FIREBASE_USER = "firebaseUser";
    private User mUser;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;
    private FirebaseUser mFireStoreUser;
    private Context mContext;


    public EditList() {
    }


    static EditList newInstance(User user) {
        EditList fragment = new EditList();
        Bundle args = new Bundle();
        args.putParcelable(FIREBASE_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            mUser = getArguments().getParcelable(FIREBASE_USER);
        mAuth = FirebaseAuth.getInstance();
        mFireStoreUser = mAuth.getCurrentUser();
        if (mFireStoreUser != null)
            mDatabase = FirebaseFirestore.getInstance();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_list, container, false);


        return view;
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
