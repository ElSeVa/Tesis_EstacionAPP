package com.example.myapplication;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.preferencias.Preferencias;
import com.example.myapplication.ui.Temporizador;
import com.example.myapplication.ui.api.APIService;
import com.example.myapplication.ui.api.ApiUtils;
import com.example.myapplication.ui.home.HomeFragment;
import com.example.myapplication.ui.models.Conductor;
import com.example.myapplication.ui.models.Reservacion;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MyDrawerController, GoogleApiClient.OnConnectionFailedListener {

    private AppBarConfiguration mAppBarConfiguration;
    final Context context = MainActivity.this;
    private TextView tvNombreUsuario,tvEmailUsuario;
    private final APIService mAPIService = ApiUtils.getAPIService();
    private Integer idConductor;
    private NavigationView navigationView;
    private ImageView ivCabecera;
    DrawerLayout drawer;
    Toolbar toolbar;
    SharedPreferences.Editor cuenta;
    Uri photo = null;
    private static String TAG = "Servicio";


    private final Map<String, String> mapCuenta= new HashMap<>();
    private final Preferencias loginPref = new Preferencias("Login");
    private final Preferencias cuentaPref = new Preferencias("Cuenta");

    private GoogleApiClient googleApiClient;


    @Override
    protected void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> optionalPendingResult =  Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if(optionalPendingResult.isDone()){
            GoogleSignInResult googleSignInResult = optionalPendingResult.get();
            handlerSignInResult(googleSignInResult);
        }else {
            optionalPendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull @NotNull GoogleSignInResult googleSignInResult) {
                    //handlerSignInResult(googleSignInResult);
                }
            });
        }
    }

    private void handlerSignInResult(GoogleSignInResult googleSignInResult) {
        if(googleSignInResult.isSuccess()){
            GoogleSignInAccount account = googleSignInResult.getSignInAccount();
            assert account != null;
            Glide.with(this).load(account.getPhotoUrl()).override(200).into(ivCabecera);
            tvNombreUsuario.setText(account.getDisplayName());
            tvEmailUsuario.setText(account.getEmail());
            mapCuenta.put("idConductor",String.valueOf(idConductor));
            mapCuenta.put("Nombre",account.getDisplayName());
            mapCuenta.put("Uri",Objects.requireNonNull(account.getPhotoUrl()).toString());
            cuentaPref.setPrefCuenta(context,mapCuenta);
            /*
            cuenta = getSharedPreferences("Cuenta", MODE_PRIVATE).edit();
            cuenta.putInt("idConductor",idConductor);
            cuenta.putString("Nombre",account.getDisplayName());
            cuenta.putString("Uri", Objects.requireNonNull(account.getPhotoUrl()).toString());
            cuenta.apply();
            */
            photo = account.getPhotoUrl();
            Log.d("MIAPP",account.getPhotoUrl().toString());
        }else{
            if (idConductor == 0) {
                goLogInScreen();
            }
        }
    }

    private void goLogInScreen() {
        Intent intent = new Intent(context,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        createNotificationChannel();

        Notification builder = new NotificationCompat.Builder(this, "Canal 1")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Nueva Reserva")
                .setContentText("Tienes una nueva reservacion para confirmar")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();

        startService(new Intent(MainActivity.this,Temporizador.class));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        //SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
        idConductor = loginPref.getPrefInteger(context,"idConductor",0);
        //idConductor = prefs.getInt("idConductor",0);
        Call<Conductor> conductorCall = mAPIService.findConductor(idConductor);

        navigationView = findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        ivCabecera = hView.findViewById(R.id.ivCabecera);
        tvNombreUsuario = hView.findViewById(R.id.tvNombreUsuario);
        tvEmailUsuario = hView.findViewById(R.id.tvEmailUsuario);

        conductorCall.enqueue(new Callback<Conductor>() {
            @Override
            public void onResponse(Call<Conductor> call, Response<Conductor> response) {
                if(response.isSuccessful() && response.body() != null){
                    Conductor conductor = response.body();
                    if(conductor.getPropietario() == 0){
                        Menu menu = navigationView.getMenu();
                        menu.setGroupVisible(R.id.groupGarage,false);
                    }else{
                        verificarEstadosGarage(notificationManager, builder);
                    }
                    mapCuenta.put("idConductor",String.valueOf(idConductor));
                    mapCuenta.put("Nombre", conductor.getNombre());
                    //cuenta = getSharedPreferences("Cuenta", MODE_PRIVATE).edit();
                    //cuenta.putInt("idConductor",idConductor);
                    //cuenta.putString("Nombre",conductor.getNombre());
                    if(photo!=null){
                        mapCuenta.put("Uri",photo.toString());
                        //cuenta.putString("Uri",photo.toString());
                    }
                    cuentaPref.setPrefCuenta(context,mapCuenta);
                    //cuenta.apply();
                    tvNombreUsuario.setText(conductor.getNombre());
                    tvEmailUsuario.setText(conductor.getEmail());
                }
            }

            @Override
            public void onFailure(Call<Conductor> call, Throwable t) {
                t.printStackTrace();
            }
        });



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration mAppBarConfiguration2 = new AppBarConfiguration.Builder(R.id.reservasFragment,
                R.id.settingFragment)
                .build();

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.reservasFragment, R.id.settingFragment,R.id.nav_home, R.id.nav_gallery, R.id.nav_perfil, R.id.nav_promocion)
                .setDrawerLayout(drawer)
                .build();

        NavController navController1 = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController1, mAppBarConfiguration2);
        NavigationUI.setupWithNavController(bottomNavigationView, navController1);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if(verificacionDestino(destination)){
                bottomNavigationView.setVisibility(View.GONE);
            }else{
                toolbar.setVisibility(View.VISIBLE);
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });

        //FragmentManager fragmentManager = getFragmentManager();
        //fragmentManager.findFragmentById(R.id.btnReservaHora);

    }

    private void verificarEstadosGarage(NotificationManagerCompat notificationManager, Notification builder){
        Call<List<Reservacion>> reservacionCall = mAPIService.obtenerReservasEstados(idConductor);
        reservacionCall.enqueue(new Callback<List<Reservacion>>() {
            @Override
            public void onResponse(Call<List<Reservacion>> call, Response<List<Reservacion>> response) {
                if(response.isSuccessful()){
                    List<Reservacion> reservacionList = response.body();
                    if(reservacionList.size() != 0){
                        notificationManager.notify(0,builder);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Reservacion>> call, Throwable t) {

            }
        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Canal 1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private boolean verificacionDestino(NavDestination destination){
        return destination.getId() == R.id.nav_gallery
                || destination.getId() == R.id.mapMuestraFragment
                || destination.getId() == R.id.menuReservaFragment
                || destination.getId() == R.id.horaFragment
                || destination.getId() == R.id.mediaFragment
                || destination.getId() == R.id.estadiaFragment
                || destination.getId() == R.id.comentariosFragment
                || destination.getId() == R.id.nav_perfil
                || destination.getId() == R.id.takePromotionFragment;
    }

    public void logoutIntent(View v){
        SharedPreferences.Editor Cuenta = getSharedPreferences("Cuenta", MODE_PRIVATE).edit();
        Cuenta.clear();
        Cuenta.apply();
        SharedPreferences.Editor prefs = getSharedPreferences("Login", MODE_PRIVATE).edit();
        prefs.clear();
        prefs.apply();
        SharedPreferences.Editor Mantener = getSharedPreferences("MantenerUsuario", MODE_PRIVATE).edit();
        Mantener.clear();
        Mantener.apply();
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull @NotNull Status status) {
                if(status.isSuccess()){
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                    finish();

                }else {
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public void setDrawer_locker() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        toolbar.setNavigationIcon(null);
    }

    @Override
    public void setDrawer_unlocker() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public void onConnectionFailed(@NonNull @NotNull ConnectionResult connectionResult) {
        Toast.makeText(context, "No se puedo iniciar sesion", Toast.LENGTH_SHORT).show();
    }

}