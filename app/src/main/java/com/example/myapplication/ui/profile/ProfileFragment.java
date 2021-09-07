package com.example.myapplication.ui.profile;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.ui.api.APIService;
import com.example.myapplication.ui.api.ApiUtils;
import com.example.myapplication.ui.models.Conductor;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileFragment extends Fragment {

    private ImageView ivFotoSecundProfile;
    private CircleImageView ivFotoPrincProfile;

    private final APIService mAPIService = ApiUtils.getAPIService();

    private TextView tvPerfilNombreProfile,tvPerfilVehiculo;

    private Button btnPromociones;

    private GoogleSignInClient mGoogleSignInClient;
    private MainActivity activity;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        ivFotoPrincProfile = root.findViewById(R.id.ivFotoPrincProfile);
        ivFotoSecundProfile = root.findViewById(R.id.ivFotoSecundProfile);
        tvPerfilNombreProfile = root.findViewById(R.id.tvPerfilNombreProfile);
        tvPerfilVehiculo = root.findViewById(R.id.tvPerfilVehiculo);
        btnPromociones = root.findViewById(R.id.btnPromociones);

        SharedPreferences a = activity.getSharedPreferences("Cuenta", Context.MODE_PRIVATE);
        if(a != null){
            int idConductor = a.getInt("idConductor",0);
            String uri = a.getString("Uri",null);
            String nombre = a.getString("Nombre",null);
            if(uri != null){
                Glide.with(activity).load(uri).into(ivFotoPrincProfile);
            }
            //ivFotoPrincProfile.setImageBitmap(account.getPhotoUrl());
            tvPerfilNombreProfile.setText(nombre);
            Call<Conductor> call = mAPIService.findConductor(idConductor);

            call.enqueue(new Callback<Conductor>() {
                @Override
                public void onResponse(Call<Conductor> call, Response<Conductor> response) {
                    if(response.isSuccessful()){
                        Conductor c = response.body();
                        assert c != null;
                        tvPerfilVehiculo.setText(c.getTipoVehiculo());
                    }
                }

                @Override
                public void onFailure(Call<Conductor> call, Throwable t) {
                    t.printStackTrace();
                }
            });

        }

        btnPromociones.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.takePromotionFragment);
        });

        activity.setDrawer_unlocker();

        return root;
    }

}
