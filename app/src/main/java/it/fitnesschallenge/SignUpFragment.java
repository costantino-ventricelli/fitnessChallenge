package it.fitnesschallenge;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import it.fitnesschallenge.model.User;

public class SignUpFragment extends Fragment {

    private static String[] ROLE;
    private static final int PICK_IMAGE_REQUEST = 1;
    private TextInputLayout emailTextInut;
    private TextInputLayout passwordTextInut;
    private TextInputLayout rePasswordTextInput;
    private TextInputLayout nomeTextInput;
    private TextInputLayout cognomeTextInput;
    private TextInputLayout roleTextInput;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    private static final String TAG = "SignUpFragment";

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_sign_up, container, false);
        emailTextInut = view.findViewById(R.id.email_text_input);
        passwordTextInut = view.findViewById(R.id.password_text_input);
        rePasswordTextInput = view.findViewById(R.id.re_password_text_input);
        nomeTextInput = view.findViewById(R.id.name_text_input);
        cognomeTextInput = view.findViewById(R.id.surname_text_input);
        roleTextInput = view.findViewById(R.id.role_text_input);
        progressBar = view.findViewById(R.id.sign_up_progress_bar);
        AutoCompleteTextView roleAutoComplete = view.findViewById(R.id.dropdown_role);
        MaterialButton signIn = view.findViewById(R.id.sign_in);

        ROLE = new String[] {getContext().getString(R.string.trainer),
                getContext().getString(R.string.user),
                getContext().getString(R.string.trainer_user)};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                R.layout.drop_down_single_layout,
                ROLE);
        roleAutoComplete.setAdapter(adapter);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email = emailTextInut.getEditText().getText().toString();
                String password = passwordTextInut.getEditText().getText().toString();
                String rePassword = rePasswordTextInput.getEditText().getText().toString();
                String nome = nomeTextInput.getEditText().getText().toString();
                String cognome = cognomeTextInput.getEditText().getText().toString();
                String role = roleTextInput.getEditText().getText().toString();
                validateForm(email, password, rePassword, nome, cognome, role);
            }
        });

        return view;
    }

    private void validateForm(String email, String password, String rePassword,
                              String nome, String cognome, String role){
        if(email.isEmpty()){
            emailTextInut.setError(getString(R.string.complete_correctly_field));
            return;
        }
        if(password.isEmpty() && rePassword.isEmpty() && password.equals(rePassword)){
            passwordTextInut.setError(getString(R.string.complete_correctly_field));
            rePasswordTextInput.setError(getString(R.string.complete_correctly_field));
            return;
        }
        if(nome.isEmpty()){
            nomeTextInput.setError(getString(R.string.complete_correctly_field));
            return;
        }
        if(cognome.isEmpty()){
            cognomeTextInput.setError(getString(R.string.complete_correctly_field));
            return;
        }
        if(role.isEmpty()){
            roleTextInput.setError(getString(R.string.complete_correctly_field));
            return;
        }

        registerUser(email, password, nome, cognome, role);
    }

    private void registerUser(final String email, final String password, final String nome,
                              final String cognome, final String role){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        sendDataToFirebase(new User(email, nome, cognome, role));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Exception throw", e);
                    }
                });
    }

    private void sendDataToFirebase(User user){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection("user")
                .document(user.getUsername())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.GONE);
                        Log.d(TAG, "Data correctly upload");
                        new MaterialAlertDialogBuilder(getContext())
                                .setTitle("Success")
                                .setMessage(getContext().getString(R.string.sign_in_successfully))
                                .show();
                        SystemClock.sleep(1000);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new MaterialAlertDialogBuilder(getContext())
                                .setTitle(getContext().getString(R.string.sign_in_fail))
                                .show();
                        Log.d(TAG, e.getMessage());
                    }
                });
    }

}
