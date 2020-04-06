package com.example.custodian.ui.home;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import com.example.custodian.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements HomePagerAdapter.OnPagerListener, View.OnKeyListener, OnMapReadyCallback {

    private GoogleMap gmap;
    ViewPager viewPager;
    LinearLayout sliderDots;
    AutoCompleteTextView address;
    MapView mapView;
    private int dots_count;
    private ImageView[] dots;
    ArgbEvaluator argbEvaluator;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        argbEvaluator = new ArgbEvaluator();
        viewPager = root.findViewById(R.id.viewPager);
        address = root.findViewById(R.id.address);
        mapView = root.findViewById(R.id.mapView);
        address.setOnKeyListener(this);

        if(mapView!=null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }

        address.setAdapter(new PlaceAutoSuggestAdapter(getContext(), android.R.layout.simple_list_item_1));
        address.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                TextView view = (TextView) v;
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    Double addLoc[] = getLatitudeAndLongitude(view.getText().toString());
                    LatLng ny = new LatLng(addLoc[0], addLoc[1]);
                    gmap.addMarker(new MarkerOptions().position(ny)).setTitle(view.getText().toString());
                    gmap.moveCamera(CameraUpdateFactory.newLatLng(ny));
                    InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                }
                return false;
            }
        });

        HomePagerAdapter adapter = new HomePagerAdapter(getOptions(), getContext(), this);
        viewPager.setAdapter(adapter);
        viewPager.setClipToPadding(false);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setPadding(20, 0, 20, 0);

        sliderDots = root.findViewById(R.id.sliderDots);
        dots_count = adapter.getCount();
        dots = new ImageView[dots_count];
        for (int i = 0; i < dots_count; i++) {
            dots[i] = new ImageView(getContext());
            dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.dot));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            sliderDots.addView(dots[i], params);
        }
        dots[0].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.active_dot));

        return root;
    }

    public static ArrayList<String> getOptions() {
        ArrayList<String> options = new ArrayList<>();
        options.add("Adrian");
        options.add("Emma");
        options.add("Rose");
        return options;
    }

    @Override
    public void onPagerClick(int position) {
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        switch (position){
//            case 0:
//                navController.navigate(R.id.quickFixFragment);
//                break;
//            case 1:
//                navController.navigate(R.id.costReductionFragment);
//                break;
//            case 2:
//                navController.navigate(R.id.bookingMain);
//                break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                (keyCode == KeyEvent.KEYCODE_ENTER)) {
            InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            return true;
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        gmap = googleMap;
        gmap.setMinZoomPreference(15);
        LatLng ny = new LatLng(40.7143528, -74.0059731);
        gmap.moveCamera(CameraUpdateFactory.newLatLng(ny));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private Double[] getLatitudeAndLongitude(String address) {
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> addresses;
        double latitude = 0, longitude = 0;
        try {
            addresses = geocoder.getFromLocationName(address, 1);
            Address addr = addresses.get(0);
            latitude = addr.getLatitude();
            longitude = addr.getLongitude();
        } catch (IOException e) {
            Log.i(HomeFragment.class.getName(),e.toString());
        }
        return new Double[]{latitude, longitude};
    }
}
