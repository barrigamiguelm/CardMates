package com.example.cardmates;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cardmates.model.ShopInfo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsFragment extends Fragment {

    //todo: que al hacer click en el detalle del marker te lleve a google maps con el sitio seleccionado.
    GoogleMap mMap;


    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {

            mMap = googleMap;

            MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.map_style);
            mMap.setMapStyle(style);
            addMapMarkers();

        }
    };

    private void addMapMarkers() {
        LatLng centroMadrid = new LatLng(40.41679630990555, -3.7037858393783822);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://cardmates-8e17e-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        databaseReference.child("Shops").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                float zoom = 10;
                for (DataSnapshot ds : snapshot.getChildren()) {

                    ShopInfo shI = ds.getValue(ShopInfo.class);
                    Double latitud = shI.getLat();
                    Double longitud = shI.getLang();
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(latitud, longitud));
                    markerOptions.title(shI.getName());
                    markerOptions.snippet("Pulsa aqui para mostrar en Maps");
                    mMap.addMarker(markerOptions);

                    mMap.setOnInfoWindowClickListener(marker -> {

                    });
                }

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(@NonNull Marker marker) {
                        Uri gmmIntentUri = Uri.parse("geo:" + marker.getPosition().latitude + "," + marker.getPosition().longitude + "?z=" + 19);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }
                });

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centroMadrid, zoom));
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}