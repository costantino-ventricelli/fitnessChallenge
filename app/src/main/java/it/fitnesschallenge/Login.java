package it.fitnesschallenge;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import it.fitnesschallenge.model.User;

import static android.util.Patterns.EMAIL_ADDRESS;
import static it.fitnesschallenge.model.Fragment.LOGIN_FRAGMENT;
import static it.fitnesschallenge.model.Fragment.SIGN_UP_FRAGMENT;

public class Login extends Fragment{

    private static final String TAG = "Login";
    //istanza per l'accesso in firebase
    private FirebaseAuth mAuth;
    private ImageView topImageView;
    private FirebaseFirestore database;
    private FirebaseUser firebaseUser;
    private User user;
    private ProgressBar progressBar;

    public Login() {
        // Required empty public constructor
    }

    /**
     * In onStart verificheremo se l'utente ha abilitato l'auto login dalle shared preferences, dopo
     * di che preleveremo l'account dal data base locale e effettueremo il login
     */
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            firebaseUser = mAuth.getCurrentUser();
            readUserFromDB();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        HomeActivity.setCurrentFragment(LOGIN_FRAGMENT);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //prelievo l'istanza del DB Firebase
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        //questo listener cattura l'aperura della tastiera per nascondere l'immagine superiore
        KeyboardVisibilityEvent.setEventListener(getActivity(), new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if(isOpen) {
                    int previouslyHeigth = topImageView.getHeight();
                    int duration = 200;
                    int finalHeigth = 0;
                    ValueAnimator valueAnimator = ValueAnimator.ofInt(previouslyHeigth,
                            finalHeigth);
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            topImageView.getLayoutParams().height =
                                    (int) animation.getAnimatedValue();
                            topImageView.requestLayout();
                        }
                    });
                    valueAnimator.setInterpolator(new DecelerateInterpolator());
                    valueAnimator.setDuration(duration);
                    valueAnimator.start();
                    valueAnimator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            topImageView.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                }
                else{
                    int previusyHeigth = topImageView.getHeight();
                    int finalHeight = Math.round(getContext().getResources()
                                    .getDisplayMetrics().density * 150);
                    int duration = 100;
                    ValueAnimator valueAnimator = ValueAnimator.ofInt(previusyHeigth, finalHeight);
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            topImageView.getLayoutParams().height =
                                    (int) animation.getAnimatedValue();
                            topImageView.requestLayout();
                        }
                    });
                    valueAnimator.setInterpolator(new DecelerateInterpolator());
                    valueAnimator.setDuration(duration);
                    valueAnimator.start();
                    topImageView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        final TextInputLayout usernameInputText = view.findViewById(R.id.login_username_field);
        final TextInputLayout passwordInputText = view.findViewById(R.id.login_password_field);
        progressBar = view.findViewById(R.id.login_progress_bar);
        MaterialButton loginButton = view.findViewById(R.id.login_button);
        MaterialButton signUpButton = view.findViewById(R.id.sign_up_button);
        topImageView = view.findViewById(R.id.login_image_view);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean allCompiled = false;
                String username = usernameInputText.getEditText().getText().toString();
                String password = passwordInputText.getEditText().getText().toString();
                if(!username.isEmpty() && EMAIL_ADDRESS.matcher(username).matches())
                    allCompiled = true;
                else
                    usernameInputText.setError(getString(R.string.
                            complete_correctly_field));
                if(!password.isEmpty())
                    allCompiled = true;
                else
                    passwordInputText.setError(getString(R.string.
                            complete_correctly_field));
                if(allCompiled)
                    signInMethod(username, password);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpFragment signUpFragment = new SignUpFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_from_right,
                        R.anim.enter_from_rigth, R.anim.exit_from_left);
                transaction.addToBackStack(SIGN_UP_FRAGMENT);
                transaction.replace(R.id.fragmentContainer, signUpFragment, SIGN_UP_FRAGMENT)
                        .commit();
            }
        });

        return view;
    }

    //metodo per il login con email e password
    private void signInMethod(String username, String password){
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            firebaseUser = mAuth.getCurrentUser();
                            readUserFromDB();
                        } else {
                            Snackbar.make(getView(), getString(R.string.login_failed), Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void readUserFromDB(){
        progressBar.setVisibility(View.VISIBLE);
        database = FirebaseFirestore.getInstance();
        try {
            DocumentReference documentReference = database.collection("user")
                    .document(firebaseUser.getEmail());
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    progressBar.setVisibility(View.GONE);
                    user = documentSnapshot.toObject(User.class);
                    Log.d(TAG, "User read from db: " + user.getNome());
                }
            });
        }catch (NullPointerException ex){
            Toast.makeText(getContext(), getContext().getResources()
                                .getString(R.string.shit_error), Toast.LENGTH_LONG).show();
        }
    }
}
