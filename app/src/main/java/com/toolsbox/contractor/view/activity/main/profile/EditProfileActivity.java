package com.toolsbox.contractor.view.activity.main.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.jaeger.library.StatusBarUtil;
import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.Constant;
import com.toolsbox.contractor.view.activity.basic.BaseActivity;
import com.toolsbox.contractor.view.adapter.PlaceArrayAdapter;


import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import static com.toolsbox.contractor.common.Constant.gArrPostedBy;

public class EditProfileActivity extends BaseActivity {

    private static final String TAG = "EditProfileActivity";
    private Toolbar toolbar;
    private RelativeLayout rlAddress, rlStructure;
    private AutoCompleteTextView etAddress;
    private Spinner spStructure;
    private PlaceArrayAdapter placeArrayAdapter;
    private LatLng selectedLocation;
    private int from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initVariable();
        initUI();
    }

    void initVariable(){
        from = getIntent().getIntExtra("from", 0);
    }

    void initUI(){
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorWhite), 0);
        setupToolbar();
        rlAddress = findViewById(R.id.rl_address);
        rlStructure = findViewById(R.id.rl_structure);
        etAddress = findViewById(R.id.et_address);
        etAddress.setThreshold(3);
        etAddress.setOnItemClickListener(mAutocompleteClickListener);
        spStructure = findViewById(R.id.sp_structure);
        if (from == 0){ // From edit structure
            rlAddress.setVisibility(View.GONE);
        } else {        // From edit address
            rlStructure.setVisibility(View.GONE);
        }

        placeArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1);
        etAddress.setAdapter(placeArrayAdapter);
        List<String> dataset = new LinkedList<>(Arrays.asList(Constant.gArrBusinessType));
        ArrayAdapter<String> spinnerStructure = new ArrayAdapter<>(this, R.layout.simple_spinner_item2, dataset);
        spinnerStructure.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spStructure.setAdapter(spinnerStructure);
    }

    void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (from == 0){
                actionBar.setTitle(R.string.edit_structure);
            } else {
                actionBar.setTitle(R.string.edit_address);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_industry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_done:
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                intent.putExtra("from", from);
                if (from == 0){
                    intent.putExtra("selectedStructure", spStructure.getSelectedItemPosition());
                } else {
                    if (selectedLocation == null){
                        Toast.makeText(this, R.string.please_fill_correct_address_info, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    intent.putExtra("selectedAddress", etAddress.getText().toString());
                    intent.putExtra("selectedLati", String.valueOf(selectedLocation.latitude));
                    intent.putExtra("selectedLongi", String.valueOf(selectedLocation.longitude));
                }
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = placeArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();
            PlacesClient placesClient = com.google.android.libraries.places.api.Places.createClient(EditProfileActivity.this);
            placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                @Override
                public void onSuccess(FetchPlaceResponse response) {
                    Place place = response.getPlace();
                    selectedLocation = place.getLatLng();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    selectedLocation = null;
                    if (exception instanceof ApiException) {
                        Toast.makeText(EditProfileActivity.this, exception.getMessage() + "", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    };

}
