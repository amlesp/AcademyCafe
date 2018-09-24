package com.popovic.academycafe;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.eegeo.indoors.IndoorMapView;
import com.eegeo.mapapi.EegeoApi;
import com.eegeo.mapapi.EegeoMap;
import com.eegeo.mapapi.MapView;
import com.eegeo.mapapi.camera.CameraPosition;
import com.eegeo.mapapi.camera.CameraUpdateFactory;
import com.eegeo.mapapi.indoors.OnIndoorEnteredListener;
import com.eegeo.mapapi.indoors.OnIndoorExitedListener;
import com.eegeo.mapapi.map.OnMapReadyCallback;
import com.eegeo.mapapi.markers.Marker;
import com.eegeo.mapapi.markers.MarkerOptions;

public class MainActivity extends AppCompatActivity {

    private MapView m_mapView;
    private EegeoMap m_eegeoMap = null;
    private IndoorMapView m_indoorMapView = null;
    private Button btnFloorUp, btnFlorDown;
    private LinearLayout btnContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EegeoApi.init(this, BuildConfig.API_KEY);
        setContentView(R.layout.activity_main);
        m_mapView = (MapView) findViewById(R.id.mapView);
        m_mapView.onCreate(savedInstanceState);

        initUI();

        m_mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final EegeoMap map) {

                m_eegeoMap = map;

                RelativeLayout uiContainer = (RelativeLayout) findViewById(R.id.eegeo_ui_container);
                m_indoorMapView = new IndoorMapView(m_mapView, uiContainer, m_eegeoMap);

                //ad marker in Academy Cafe (indoor map)
                Marker marker = m_eegeoMap.addMarker(new MarkerOptions()
                        .position(Constants.ACADEMY_CAFE_COORDINATES)
                        .indoor("california_academy_of_sciences_19794", 0)
                        .iconKey("coffee")
                        .labelText(getString(R.string.marker_on) + " " + 1));

                IndoorEventListener listener = new IndoorEventListener(m_eegeoMap, MainActivity.this);

                map.addOnIndoorEnteredListener(listener);
                map.addOnIndoorExitedListener(listener);

            }
        });
    }

    private void initUI() {
        btnContainer = findViewById(R.id.btnContainer);
        btnFloorUp = findViewById(R.id.btnFloorUp);
        btnFlorDown = findViewById(R.id.btnFloorDown);
    }


    public void onFlorUp(View view) {
        m_eegeoMap.setIndoorFloor(m_eegeoMap.getCurrentFloorIndex() + 1);
    }

    public void onFlorDown(View view) {
        m_eegeoMap.setIndoorFloor(m_eegeoMap.getCurrentFloorIndex() - 1);
    }

    public void onExitIndoor(View view) {
        m_eegeoMap.exitIndoorMap();
    }

    @Override
    protected void onResume() {
        super.onResume();
        m_mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        m_mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m_mapView.onDestroy();
    }

    public class IndoorEventListener implements OnIndoorEnteredListener, OnIndoorExitedListener {
        private EegeoMap m_map;
        private Context m_context;

        IndoorEventListener(EegeoMap map, Context context) {
            this.m_map = map;
            this.m_context = context;
        }

        @Override
        public void onIndoorEntered() {
            btnContainer.setVisibility(View.VISIBLE);
            //set first floor indoor map when user enter to indoor view of the building
            CameraPosition position = new CameraPosition.Builder()
                    .target(37.7701, -122.466407)
                    .indoor("california_academy_of_sciences_19794", 0)
                    .zoom(17)
                    .bearing(270)
                    .build();
            m_map.moveCamera(CameraUpdateFactory.newCameraPosition(position));

        }

        @Override
        public void onIndoorExited() {
            btnContainer.setVisibility(View.GONE);
        }
    }

}
