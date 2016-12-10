package myApp.lwttlzh.Taiwan_explore.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.Timer;
import java.util.TimerTask;

import myApp.lwttlzh.Taiwan_explore.R;

public class GmapFragment extends SupportMapFragment implements LocationListener,OnMapReadyCallback {


   public GoogleMap mMap;
    float zoom;
    private LocationManager locMgr;
    String bestProv;
    public double a,b;
    public LatLng poi;
  //  Context mContext;

    SQLiteDatabase coord = null;
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    /*@Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }*/



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_gmaps, container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        SupportMapFragment fragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);
        mMap = fragment.getMap();
       // mMap = fragment.getMap();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        locMgr = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        do {
            bestProv = locMgr.getBestProvider(criteria, true);
        }while(bestProv==null);
        //Toast.makeText(this.getActivity(),bestProv, Toast.LENGTH_LONG).show();
        //fragment.getMapAsync(this);

    }

    @Override
    public void onResume(){
        super.onResume();
       //coord  = openOrCreateDatabase("coord1.db",MODE_PRIVATE,null);
        if(locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            if(ContextCompat.checkSelfPermission(this.getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED){
                mMap.setMyLocationEnabled(true);
                //if (locMgr.getLastKnownLocation("network") == null) {
                if (locMgr.getLastKnownLocation("network") == null) {
                    //Toast.makeText(this.getActivity(),"請開啟網路定位服務", Toast.LENGTH_LONG).show();
                    //Toast.makeText(this.getActivity(),"請開啟定位服務", Toast.LENGTH_LONG).show();
                    showAlert();
                    /*mMap.setMyLocationEnabled(false);
                    retry();*/
                }else {
                    //locMgr.requestSingleUpdate("network", this, null);
                    locMgr.requestSingleUpdate("network", this, null);
                    Toast.makeText(this.getActivity(), "定位中", Toast.LENGTH_LONG).show();
                    Location location = locMgr.getLastKnownLocation("network");
                    a = location.getLatitude();
                    b = location.getLongitude();
                    if (location != null) {
                        LatLng Point = new LatLng(location.getLatitude(), location.getLongitude());
                        zoom = 18;
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Point, zoom));
                        locMgr.removeUpdates(this);
                        try {
                            locMgr.requestLocationUpdates(bestProv,1000,0,this);
                        }catch (SecurityException e) {
                        }
                    } else {
                        /*mMap.setMyLocationEnabled(false);
                        retry();*/
                    }
                    //Toast.makeText(this.getActivity(),"請到空曠的地點", Toast.LENGTH_LONG).show();
                }
            }
        }else{
            Toast.makeText(this.getActivity(),"請開啟定位服務", Toast.LENGTH_LONG).show();
        }

    }

    private void showAlert(){
        new AlertDialog.Builder(this.getActivity())
                .setTitle("請開啟服務")
                .setMessage("請開啟網路定位服務")
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                })
                .show();
    }

    /*private void retry(){
        onPause();
        Toast.makeText(this.getActivity(), "重試中", Toast.LENGTH_LONG).show();
        Timer t = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                onResume();
            }
        };
        t.schedule(task, 1000);
    }*/

    @Override
    public void onPause(){
        super.onPause();
        if(ContextCompat.checkSelfPermission(this.getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            //locMgr.removeUpdates(this);
        }
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
    /*@Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }*/

    @Override
    public void onLocationChanged(Location location) {
        String x = "緯度 =" + Double.toString(location.getLatitude());
        String y = "經度 =" + Double.toString(location.getLongitude());
        a = location.getLatitude();
        b = location.getLongitude();
        LatLng Point = new LatLng(location.getLatitude(),location.getLongitude());
        zoom = 18;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Point,zoom));
        //mMap.setMyLocationEnabled(true);
        Toast.makeText(this.getActivity(),"目前位置", Toast.LENGTH_LONG).show();
        Toast.makeText(this.getActivity(),x+"\n"+y,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Criteria criteria = new Criteria();
        bestProv = locMgr.getBestProvider(criteria,true);
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

  //  @Override
   /* public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map_container);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_container, fragment).commit();
        }
    }*/
   /* @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng marker = new LatLng(-33.867, 151.206);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 13));

       googleMap.addMarker(new MarkerOptions().title("Hello Google Maps!").position(marker));
    }*/
    public GoogleMap getmMap(){
        return mMap;
    }

}
