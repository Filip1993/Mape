package com.filipkesteli.mape;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    //vec umetnuta varijabla tipa GoogleMap
    private GoogleMap mMap;

    //definiramo konstante:
    private static LatLng LAT_LNG_ZAGREB = new LatLng(45.817, 16); //koordinate Zagreba
    private static final float INIT_ZOOM_LEVEL = 17.0f; //zoom level:
    //pocetna adresa na koju postavljamo pocetni marker -> Geocoderom pretvaramo String adresu u LatLng
    private static final String ADDRESS = "Trg bana Josipa Jelačića, Zagreb"; //adresa:

    //onCreate ovdje ne diram (bar ne u ovom primjeru)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //defaultni activity
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //ovdje postavljamo svoju poslovnu logiku (umjesto setupMap metode):
        configureMap(); //konfiguriramo mapu

        //Promijenjiva konstanta -> najprije smo kameru i tocku postavili na Zagreb, a onda to saltamo na svaku promjenu korisnika, tj. po korisnikovoj volji

        LAT_LNG_ZAGREB = getLatLngFromAddress(ADDRESS); //Metoda koja ce dati adresu -> preko adrese nalazimo latitude i longitude, odnosno koordinate mjesta na koje je korisnik kliknuo

        addMarker(LAT_LNG_ZAGREB); //Dodaj marker -> gdje hoces marker
        setupAdapter(); //Adapter za popup window
        animateCamera(LAT_LNG_ZAGREB); //ANIMIRAT cemo kameru

        setupListeners();
        /*
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        */
    }

    private void configureMap() {
        mMap.getUiSettings().setCompassEnabled(true); //compass postavljen
        mMap.getUiSettings().setZoomControlsEnabled(true); //zoom postavljen
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID); //tip mape
        mMap.setTrafficEnabled(true);
    }

    //Geocoding is the process of transforming a street address or other description of a location into a (latitude, longitude) coordinate.
    //Koristimo GEOKODIRANJE, odnosno iz dane adrese nalazimo koordinate lokacije
    private LatLng getLatLngFromAddress(String address) {
        LatLng latLng = LAT_LNG_ZAGREB; //osiguravamo se da geoCoder ne pukne!
        //API od GEOCODER-a:
        Geocoder geocoder = new Geocoder(this, Locale.getDefault()); //lociranje geokodera prema nekom lokalitetu
        //idem pitati Geocoder: Daj mi listu adresa (ili jednu adresu) i izvuci mi lattitude i longitude
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);

            //ako adresa postoji -> broj znakova razicit od 0
            if (addresses.size() > 0) {
                Address a = addresses.get(0);
                double lat = a.getLatitude();
                double lng = a.getLongitude();
                latLng = new LatLng(lat, lng);
            }
        } catch (IOException e) {
        }

        return latLng;
    }

    //gdje hoces marker s obzirom na Latlng:
    private void addMarker(LatLng latLng) {
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(getString(R.string.zagreb_glavni_grad))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cast_dark))
        );
    }

    private void setupAdapter() {
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                //inflateamo view element
                View view = getLayoutInflater().inflate(R.layout.info_window, null);
                TextView markerText = (TextView) view.findViewById(R.id.marker_text);
                ImageView markerIcon = (ImageView) view.findViewById(R.id.marker_icon);

                //umecemo text i sliku u resurs info_window.xml:
                markerText.setText(R.string.zagreb_glavni_grad);
                markerIcon.setImageResource(R.drawable.ic_cast_dark);

                return view;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });
    }

    //animiramo kameru da se zoomira
    private void animateCamera(LatLng latLng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, INIT_ZOOM_LEVEL));
    }

    private void setupListeners() {
        //kada kliknemo na mapu, dodaj marker, animiraj kameru i Toastaj poruku
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                addMarker(latLng);
                animateCamera(latLng);
                String address = getAddressFromLatLng(latLng);

                Toast.makeText(MapsActivity.this, address, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Reverse Geocoding -> reverzno geokodiranje - iz kliknute koordinate (Latlng), daj mi adresu
    private String getAddressFromLatLng(LatLng latLng) {
        String address = "";

        //Geocoder
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        //Lista adresa (importamo android.location.address):
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            //ako adresa postoji, odnosno ako je duljina stringa veca od 0:
            if (addresses.size() > 0) {
                Address a = addresses.get(0); //izvuci mi adresu ako imas adresu
                for (int i = 0; i < a.getMaxAddressLineIndex(); i++) {
                    address += a.getAddressLine(i) + "\n";
                }
            }
        } catch (IOException e) {
        }

        return address;
    }
}
