package com.example.myapplication.ui.home;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.preferencias.Preferencias;
import com.example.myapplication.ui.CallReservaciones;
import com.example.myapplication.ui.Temporizador;
import com.example.myapplication.ui.api.APIService;
import com.example.myapplication.ui.api.ApiUtils;
import com.example.myapplication.ui.mapabox.LocationListeningCallback;
import com.example.myapplication.ui.mapabox.MapaBox;
import com.example.myapplication.ui.models.Estadia;
import com.example.myapplication.ui.models.Garage;
import com.example.myapplication.ui.models.Mapa;
import com.example.myapplication.ui.models.Reservacion;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.os.Looper.getMainLooper;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

public class HomeFragment extends Fragment implements OnMapReadyCallback, PermissionsListener, MapboxMap.OnMarkerClickListener{

    private MapboxMap mapboxMap;
    private MapView mapView;
    private MapaBox mapaBox;
    private MainActivity activity;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationComponentOptions locationComponentOptions;
    private APIService mAPIService;
    private SharedPreferences.Editor editor;
    private Call<Reservacion> espera;
    private Call<Reservacion> reservacion;
    private final List<Estadia> estadiaList = new ArrayList<>();
    private Call<List<Mapa>> mapas;
    private Call<List<Garage>> garage;
    private final LocationListeningCallback callback = new LocationListeningCallback(activity);
    private String horario;
    private String vehiculo;
    private String filtro;
    private Button btnGarage;
    private FloatingActionButton fab_location_search;
    private final String geojsonSourceLayerId = "geojsonSourceLayerId";
    private final String symbolIconId = "symbolIconId";
    private TextView tempo;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    private static final long START_TIME_IN_MILLIS = 1800000;
    private CountDownTimer mCountDownTimer;
    private boolean isExecute;
    private long tiempo;
    private long tiempoFinal;
    //private SharedPreferences spTempo;
    private Temporizador temporizador;
    private int idReservacion;
    private CallReservaciones call;
    private final Preferencias filtrosPref = new Preferencias("Filtros");
    private final Preferencias tiempoPref = new Preferencias("Tiempo");
    private final Preferencias loginPref = new Preferencias("Login");
    private final Map<String, String> mapTiempo= new HashMap<>();
    private final Map<String, String> mapLogin = new HashMap<>();

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setExitTransition(inflater.inflateTransition(R.transition.fade));

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Mapbox.getInstance(activity, getString(R.string.mapbox_access_token));
        View view = inflater.inflate(R.layout.fragment_home,container,false);

        btnGarage = view.findViewById(R.id.btnGarage);
        fab_location_search = view.findViewById(R.id.fab_location_search);
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        tempo = view.findViewById(R.id.tempo);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //spTempo = activity.getSharedPreferences("Tiempo", Context.MODE_PRIVATE);
        //if(spTempo!=null){
            tiempo = tiempoPref.getPrefLong(activity,"tiempoRestante",START_TIME_IN_MILLIS);
            //tiempo = spTempo.getLong("millisLeft", START_TIME_IN_MILLIS);
            isExecute = tiempoPref.getPrefBoolean(activity,"seEstaEjecutando",false);
            //isExecute = spTempo.getBoolean("timerRunning", false);
            tiempoFinal = tiempoPref.getPrefLong(activity,"tiempoFinal", 0L);
            //tiempoFinal = spTempo.getLong("endTime", 0);
            if(tiempoFinal == 0){
                tiempoFinal = System.currentTimeMillis() + START_TIME_IN_MILLIS;
            }
            idReservacion = tiempoPref.getPrefInteger(activity,"idReservacion",0);
            //idReservacion = spTempo.getInt("idReservacion",0);
        //}

        if(tiempo == START_TIME_IN_MILLIS || tiempo < 0){
            temporizador = new Temporizador(START_TIME_IN_MILLIS,1000);
        }

        if(temporizador == null){
            temporizador = new Temporizador(tiempo,1000);
        }

        temporizador.setTextView(tempo);
        temporizador.setTiempo(tiempo);
        temporizador.setTiempoFinal(tiempoFinal);
        temporizador.setExecuteTime(isExecute);

        mAPIService = ApiUtils.getAPIService();
        mapas = mAPIService.findAllMapa();
        garage = mAPIService.findAllGarage();

        mapaBox = new MapaBox(activity);

        call = new CallReservaciones();
        call.agregar(temporizador);
        if(idReservacion != 0){
            reservacion = mAPIService.obtenerReservacionPorId(idReservacion);
            espera = mAPIService.obtenerReservacionPorId(idReservacion);

            mCountDownTimer = new CountDownTimer(START_TIME_IN_MILLIS,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    espera = mAPIService.obtenerReservacionPorId(idReservacion);
                    if(!espera.isExecuted()){
                        espera.enqueue(new Callback<Reservacion>() {
                            @Override
                            public void onResponse(Call<Reservacion> calls, Response<Reservacion> response) {
                                if(response.isSuccessful()){
                                    Reservacion r = response.body();
                                    if (r != null) {
                                        if(r.getEstado().equalsIgnoreCase("Esperando")){
                                            call.notificar(true);
                                        }else{
                                            mCountDownTimer.cancel();
                                            stopTimer();
                                            new Handler().postDelayed(() -> {
                                                tempo.setVisibility(View.GONE);
                                            },2000);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<Reservacion> calls, Throwable t) {
                                Log.d("ERROR","Tira error todo el rato " + t.getMessage());
                            }
                        });
                    }
                    espera.timeout();

                }
                @Override
                public void onFinish() {
                    call.notificar(false);
                }
            }.start();
        }
        if(temporizador.getExecuteTime()){
            Toast.makeText(activity, "Esta activado? " + temporizador.getExecuteTime(), Toast.LENGTH_SHORT).show();
            reservacion.enqueue(new Callback<Reservacion>() {
                @Override
                public void onResponse(Call<Reservacion> calls, Response<Reservacion> response) {
                    if(!response.isSuccessful() && response.body() == null){
                        Toast.makeText(activity, "Code: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                    Reservacion reservacion = response.body();
                    if (reservacion != null && temporizador.getExecuteTime() ) {
                        if(reservacion.getId() == idReservacion && reservacion.getEstado().equals("Esperando")){
                            Toast.makeText(activity, "Activando temporizador", Toast.LENGTH_SHORT).show();
                            startTimer();
                            tempo.setVisibility(View.VISIBLE);
                        }else{
                            Toast.makeText(activity, "No hay temporizador", Toast.LENGTH_SHORT).show();
                            stopTimer();
                            //SharedPreferences.Editor editor = activity.getSharedPreferences("Tiempo", Context.MODE_PRIVATE).edit();
                            mapTiempo.putAll(crearClaveValorDefault());
                            //Map<String, String> mapTiempo = crearClaveValorDefault();
                            //Preferencias tiempoPref = new Preferencias("Tiempo");
                            tiempoPref.setPrefTiempos(activity,mapTiempo);
                            /*
                            editor.putLong("millisLeft", START_TIME_IN_MILLIS);
                            editor.putBoolean("timerRunning", false);
                            editor.putLong("endTime", 0);
                            editor.apply();
                            */
                            tempo.setVisibility(View.GONE);
                        }
                    }
                    Toast.makeText(activity, "Timer Running? " + temporizador.getExecuteTime(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<Reservacion> call, Throwable t) {

                }
            });
        }
        //Preferencias filtrosPref = new Preferencias("Filtros");
        //SharedPreferences filtros = activity.getSharedPreferences("Filtros", Context.MODE_PRIVATE);
        vehiculo = filtrosPref.getPrefString(activity,"vehiculo",null);
        horario = filtrosPref.getPrefString(activity,"horario",null);
        String precio = filtrosPref.getPrefString(activity,"precio","Bajo");
        filtro = filtrosPref.getPrefString(activity,"filtro","No");
        /*
        vehiculo = filtros.getString("vehiculo", null);
        horario = filtros.getString("horario", null);
        String precio = filtros.getString("precio", "Bajo");
        filtro = filtros.getString("filtro", "No");
        */
        Call<List<Estadia>> estadias;
        if(precio.equals("Alto")){
            estadias = mAPIService.ordenarPrecios("Si");
        }else {
            estadias = mAPIService.ordenarPrecios("No");
        }
        estadias.enqueue(new Callback<List<Estadia>>() {
            @Override
            public void onResponse(Call<List<Estadia>> call, Response<List<Estadia>> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        estadiaList.addAll(response.body());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Estadia>> call, Throwable t) {

            }
        });
        //Toast.makeText(activity, "Tipo Vehiculo: " + vehiculo + ", Horario: "+ horario+", Precio: "+precio, Toast.LENGTH_SHORT).show();


        locationEngine = LocationEngineProvider.getBestLocationEngine(activity);
    }



    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        mapaBox.setMapboxMap(this.mapboxMap);
        garage.enqueue(new Callback<List<Garage>>() {
            @Override
            public void onResponse(@NotNull Call<List<Garage>> call,@NotNull Response<List<Garage>> response) {
                llenarMapa(mapas, response);
            }

            @Override
            public void onFailure(@NotNull Call<List<Garage>> call,@NotNull Throwable t) {

            }
        });

        mapboxMap.setStyle(getString(R.string.navigation_guidance_day), style -> {
            try {
                enableLocationComponent(style);
                initSearchFab();

                style.addImage(symbolIconId, BitmapFactory.decodeResource(
                        activity.getResources(), R.drawable.map_default_map_marker));

                // Create an empty GeoJSON source using the empty feature collection
                setUpSource(style);

                // Set up a new symbol layer for displaying the searched location's feature coordinates
                setupLayer(style);
            }catch (Exception e){
                Toast.makeText(activity, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        mapboxMap.setOnMarkerClickListener(this);
        mapboxMap.addOnMapClickListener(point -> {
            if(btnGarage.isEnabled()){
                btnGarage.setVisibility(View.GONE);
                btnGarage.setEnabled(false);
            }
            return false;
        });

        mapboxMap.setOnInfoWindowClickListener(marker -> {
            if(btnGarage.isEnabled()){
                btnGarage.setVisibility(View.GONE);
                btnGarage.setEnabled(false);
            }
            return false;
        });

    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if(marker.isInfoWindowShown() && btnGarage.isEnabled()){
            btnGarage.setVisibility(View.GONE);
            btnGarage.setEnabled(false);
        }else{
            btnGarage.setEnabled(true);
            btnGarage.setVisibility(View.VISIBLE);
        }
        garage.cancel();
        garage = mAPIService.findAllGarage();
        garage.enqueue(new Callback<List<Garage>>() {
            @Override
            public void onResponse(@NotNull Call<List<Garage>> call, @NotNull Response<List<Garage>> response) {
                if(response.isSuccessful() && response.body() != null){
                    for (Garage responseGarage : response.body()){
                        if(marker.getTitle().equals(responseGarage.getNombre())){
                            //Toast.makeText(activity, marker.getTitle(), Toast.LENGTH_LONG).show();
                            //Preferencias loginPref = new Preferencias("Login");
                            //Map<String, String> mapLogin = new HashMap<>();
                            mapLogin.put("idGarage",responseGarage.getId().toString());
                            loginPref.setPrefLogin(activity,mapLogin);
                            /*
                            editor = activity.getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE).edit();
                            editor.putInt("idGarage", responseGarage.getId());
                            editor.apply();
                            */
                            btnGarage.setOnClickListener(v -> {
                                Navigation.findNavController(v).navigate(R.id.mapMuestraFragment);
                            });
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<Garage>> call, @NotNull Throwable t) {

            }
        });

        return false;
    }

    private void initSearchFab() {
        PlaceAutocomplete.clearRecentHistory(activity);
        fab_location_search.setOnClickListener(v ->  {
            Intent intent = new PlaceAutocomplete.IntentBuilder()
                    .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : getString(R.string.mapbox_access_token))
                    .placeOptions(PlaceOptions.builder()
                            .country("AR")
                            .bbox(-58.53174646204063,-34.70397620778088,-58.34834698583572,-34.53774024796235)
                            .backgroundColor(Color.parseColor("#EEEEEE"))
                            .hint("Buscar Comuna, Barrio, Etc.")
                            .build(PlaceOptions.MODE_CARDS))
                    .build(activity);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        });
    }

    private void setUpSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(geojsonSourceLayerId));
    }

    private void setupLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addLayer(new SymbolLayer("SYMBOL_LAYER_ID", geojsonSourceLayerId).withProperties(
                iconImage(symbolIconId),
                iconOffset(new Float[] {0f, -8f})
        ));
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(activity)) {
            // Activate the MapboxMap LocationComponent to show user location
            // Adding in LocationComponentOptions is also an optional parameter
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            LocationComponentActivationOptions locationComponentActivationOptions = LocationComponentActivationOptions
                    .builder(activity, loadedMapStyle)
                    .locationComponentOptions(locationComponentOptions)
                    .build();

            locationComponentOptions =
                    LocationComponentOptions.builder(activity)
                            .pulseEnabled(true)
                            .pulseColor(Color.GREEN)
                            .pulseInterpolator(new BounceInterpolator())
                            .build();

            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(activity,loadedMapStyle).build());
            //locationComponent.activateLocationComponent(this, loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);
            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);
            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);
            LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                    .setPriority(LocationEngineRequest.PRIORITY_NO_POWER)
                    .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME)
                    .build();
            locationEngine.requestLocationUpdates(request, callback, getMainLooper());
            locationEngine.getLastLocation(callback);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(activity);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(activity, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            if(mapboxMap.getStyle() != null){
                enableLocationComponent(mapboxMap.getStyle());
            }
        } else {
            Toast.makeText(activity, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            activity.finish();
        }
    }

    public void llenarMapa(Call<List<Mapa>> call, Response<List<Garage>> responseGarage){
        call.enqueue(new Callback<List<Mapa>>() {
            @Override
            public void onResponse(@NotNull Call<List<Mapa>> call, @NotNull Response<List<Mapa>> response) {
                if(response.isSuccessful() && response.body() != null){
                    for (Mapa mapa : response.body()) {
                        if(responseGarage.body() != null){
                            for (Garage garage : responseGarage.body()){
                                for (Estadia estadia : estadiaList){
                                    if(filtro.equals("Si")){
                                        if(garage.getId().equals(mapa.getIdGarage())
                                                && estadia.getIdGarage().equals(mapa.getIdGarage())
                                                && estadia.getVehiculoPermitido().equals(vehiculo)
                                                && estadia.getHorario().equals(horario)){
                                            definirDisponibilidad(garage,mapa);
                                        }
                                    }
                                }

                                if(filtro.equals("No")){
                                    if(garage.getId().equals(mapa.getIdGarage())){
                                        definirDisponibilidad(garage,mapa);
                                    }
                                }

                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<Mapa>> call, @NotNull Throwable t) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {

            // Retrieve selected location's CarmenFeature
            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);

            // Create a new FeatureCollection and add a new Feature to it using selectedCarmenFeature above.
            // Then retrieve and update the source designated for showing a selected location's symbol layer icon

            if (mapboxMap != null) {
                Style style = mapboxMap.getStyle();
                if (style != null) {
                    GeoJsonSource source = style.getSourceAs(geojsonSourceLayerId);
                    if (source != null) {
                        source.setGeoJson(FeatureCollection.fromFeatures(
                                new Feature[] {Feature.fromJson(selectedCarmenFeature.toJson())}));
                    }
                    // Move map camera to the selected location
                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                                            ((Point) selectedCarmenFeature.geometry()).longitude()))
                                    .zoom(12)
                                    .build()), 4000);
                }
            }
        }
    }

    private void definirDisponibilidad(Garage garage, Mapa mapa){
        switch (garage.getDisponibilidad()){
            case "Abierto":
                mapaBox.mostrarMarcadores(Double.parseDouble(mapa.getLatitud()),
                        Double.parseDouble(mapa.getLongitud()),
                        garage.getNombre(), R.drawable.ic_mapbox_abierto);
                break;
            case "Completo":
                mapaBox.mostrarMarcadores(Double.parseDouble(mapa.getLatitud()),
                        Double.parseDouble(mapa.getLongitud()),
                        garage.getNombre(), R.drawable.ic_mapbox_completo);
                break;
            case "Promocion":
                mapaBox.mostrarMarcadores(Double.parseDouble(mapa.getLatitud()),
                        Double.parseDouble(mapa.getLongitud()),
                        garage.getNombre(),R.drawable.ic_mapbox_promocion);
                break;
            default:
                mapaBox.mostrarMarcadores(Double.parseDouble(mapa.getLatitud()),
                        Double.parseDouble(mapa.getLongitud()),
                        garage.getNombre(),R.drawable.ic_mapbox_cerrado);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
        //SharedPreferences prefs = activity.getSharedPreferences("Tiempo", Context.MODE_PRIVATE);
        //if(prefs != null){
            if(tiempo != START_TIME_IN_MILLIS && isExecute && tiempoFinal == 0){
                tiempo = tiempoPref.getPrefLong(activity,"tiempoRestante",START_TIME_IN_MILLIS);
                isExecute = tiempoPref.getPrefBoolean(activity,"seEstaEjecutando",false);
                tiempoFinal = tiempoPref.getPrefLong(activity,"tiempoFinal",0L);
                //tiempo = prefs.getLong("millisLeft", START_TIME_IN_MILLIS);
                //isExecute = prefs.getBoolean("timerRunning", false);
                //tiempoFinal = prefs.getLong("endTime", 0);
            }
        //}
        temporizador.setTiempo(tiempo);
        temporizador.setExecuteTime(isExecute);
        temporizador.updateCountDownText();
        if (temporizador.getExecuteTime()) {
            temporizador.setTiempoFinal(tiempoFinal);
            tiempo = tiempoFinal - System.currentTimeMillis();
            temporizador.setTiempo(tiempo);
            if (temporizador.getTiempo() < 0) {
                temporizador.setTiempo(0);
                temporizador.setExecuteTime(false);
                temporizador.updateCountDownText();
            } else {
                startTimer();
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();

        mapTiempo.putAll(crearClaveValor());
        tiempoPref.setPrefTiempos(activity,mapTiempo);
        /*
        SharedPreferences prefs = activity.getSharedPreferences("Tiempo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("millisLeft", temporizador.getTiempo());
        editor.putBoolean("timerRunning", temporizador.getExecuteTime());
        editor.putLong("endTime", temporizador.getTiempoFinal());
        editor.apply();
        */
        if (temporizador != null) {
            temporizador.cancel();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
        mapTiempo.putAll(crearClaveValor());
        tiempoPref.setPrefTiempos(activity,mapTiempo);
        /*
        SharedPreferences prefs = activity.getSharedPreferences("Tiempo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("millisLeft", temporizador.getTiempo());
        editor.putBoolean("timerRunning", temporizador.getExecuteTime());
        editor.putLong("endTime", temporizador.getTiempoFinal());
        editor.apply();
        */
        if (temporizador != null) {
            temporizador.cancel();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    private void startTimer() {
        if(temporizador!=null){
            tiempoFinal = System.currentTimeMillis() + tiempo;
            temporizador = new Temporizador(tiempo, 1000);
            temporizador.setTextView(tempo);
            temporizador.setTiempo(tiempo);
            temporizador.setExecuteTime(isExecute);
            temporizador.setTiempoFinal(tiempoFinal);
            temporizador.start();
            temporizador.setExecuteTime(true);
        }
    }

    private void stopTimer() {
        if (temporizador != null) {
            temporizador.cancel();
            temporizador.setExecuteTime(false);
            mapTiempo.putAll(crearClaveValor());
            tiempoPref.removePref(activity,"idReservacion");
            tiempoPref.setPrefTiempos(activity,mapTiempo);
            /*
            SharedPreferences.Editor editor = spTempo.edit();
            editor.putLong("millisLeft",temporizador.getTiempo());
            editor.putBoolean("timerRunning",temporizador.getExecuteTime());
            editor.putLong("endTime",temporizador.getTiempoFinal());
            editor.remove("idReservacion");
            editor.apply();
            */
            call.notificar(false);
        }
    }

    private Map<String,String> crearClaveValor(){
        Map<String, String> map = new HashMap<>();
        map.put("tiempoRestante", String.valueOf(temporizador.getTiempo()));
        map.put("seEstaEjecutando", String.valueOf(temporizador.getExecuteTime()));
        map.put("tiempoFinal", String.valueOf(temporizador.getTiempoFinal()));
        return map;
    }
    private Map<String,String> crearClaveValorDefault(){
        Map<String, String> map = new HashMap<>();
        map.put("tiempoRestante", String.valueOf(START_TIME_IN_MILLIS));
        map.put("seEstaEjecutando", String.valueOf(false));
        map.put("tiempoFinal", String.valueOf(0));
        return map;
    }
}