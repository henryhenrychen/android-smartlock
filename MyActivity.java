ckage com.mycompany.smartlock;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static android.widget.Toast.LENGTH_SHORT;


public class MyActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private TextView car_show ;
    private Switch lock_switch ;
    private static final int START_LOCATION = 2;
    private GoogleApiClient mGoogleApiClient ;
    private Location mLastLocation ;
    private Item item_locate ;
    private LocationRequest locationRequest;
    private int judge ;
    public String db_name = " locateSQL" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        car_show = (TextView) findViewById(R.id.textView) ;
        lock_switch = (Switch) findViewById(R.id.switch1);
        item_locate = new Item() ;
        buildGoogleApiClient();
        configLocationRequest();
        //primer() ;
        judge = 0 ;
        Toast.makeText(this, "Build !!!", LENGTH_SHORT).show() ;
        lock_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //lock_switch.setText("Tap to unlock");
                    judge = 1;
                } else {
                    //lock_switch.setText("lock");
                    judge = 0;
                    update();
                }
            }

        });
    }
    public void update(){
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, locationRequest, (com.google.android.gms.location.LocationListener) MyActivity.this);
    }
    public void primer(){
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            double lat = mLastLocation.getLatitude() ;
            double lng = mLastLocation.getLongitude() ;
            item_locate.setLongitude(lng);
            item_locate.setLatitude(lat);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }
    private void configLocationRequest() {
        locationRequest = new LocationRequest();
        // 設定讀取位置資訊的間隔時間為一秒（1000ms）
        locationRequest.setInterval(2000);
        // 設定讀取位置資訊最快的間隔時間為一秒（1000ms）
        locationRequest.setFastestInterval(1000);
        // 設定優先讀取高精確度的位置資訊（GPS）
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    public void clickFunction(View view){
        Intent intentMap = new Intent(this, MapsActivity.class);
        // 啟動地圖元件
        // 設定儲存的座標`
        //intentMap.putExtra("lat", item_locate.getLatitude());
        //intentMap.putExtra("lng", item_locate.getLongitude());
        SharedPreferences mylat = getSharedPreferences("mylocation", 0);
        float lat = mylat.getFloat("lastlat", 0);
        intentMap.putExtra("lat", (double)lat);
        SharedPreferences mylng = getSharedPreferences("mylocation", 0);
        float lng = mylng.getFloat("lastlng", 0);
        intentMap.putExtra("lng", (double)lng);
        startActivityForResult(intentMap, START_LOCATION);

    }
    @Override
    public void onStart() {
        super.onStart();
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, "start !!!", LENGTH_SHORT).show() ;
            mGoogleApiClient.connect();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();

        // 移除Google API用戶端連線
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    public void BI(View view){
        Toast.makeText(this, "Ringing !!!", LENGTH_SHORT).show() ;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }


    @Override
    public void onConnected(Bundle bundle) {
        // 已經連線到Google Services
        // 啟動位置更新服務
        // 位置資訊更新的時候，應用程式會自動呼叫LocationListener.onLocationChanged
           // LocationServices.FusedLocationApi.requestLocationUpdates(
             //  mGoogleApiClient, locationRequest, (com.google.android.gms.location.LocationListener) MyActivity.this);
        update() ;
        Toast.makeText(this, "update !!!", LENGTH_SHORT).show() ;
        /*mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            Toast.makeText(this, "location saved !!!", LENGTH_SHORT).show() ;
            double lat = mLastLocation.getLatitude() ;
            double lng = mLastLocation.getLongitude() ;
            item_locate.setLongitude(lng);
            item_locate.setLatitude(lat);
        }
        else
            Toast.makeText(this, "Ringing !!!", LENGTH_SHORT).show() ;*/


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "shit" +" !!!", LENGTH_SHORT).show() ;
    }

    @Override
    public void onLocationChanged(Location location) {
        // 位置改變
        // Location參數是目前的位置
        if(judge ==1 ) {
            Toast.makeText(this, "location saved !!!", LENGTH_SHORT).show() ;
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            /*item_locate.setLongitude(lng);
            item_locate.setLatitude(lat);*/
            SharedPreferences mylat = getSharedPreferences("mylocation", 0);
            SharedPreferences.Editor editor = mylat.edit();
            editor.putFloat("lastlat", (float)lat);
            editor.commit();
            SharedPreferences mylng = getSharedPreferences("mylocation", 0);
            editor = mylng.edit();
            editor.putFloat("lastlng", (float)lng);
            editor.commit();
        }
        /*// 設定目前位置的標記
        if (currentMarker == null) {
            currentMarker = mMap.addMarker(new MarkerOptions().position(latLng));
        }
        else {
            currentMarker.setPosition(latLng);
        }

        // 移動地圖到目前的位置
        moveMap(latLng);*/
    }


    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}

