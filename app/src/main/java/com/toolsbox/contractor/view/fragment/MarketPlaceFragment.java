package com.toolsbox.contractor.view.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.Constant;
import com.toolsbox.contractor.common.utils.DeviceUtil;
import com.toolsbox.contractor.common.utils.PreferenceHelper;
import com.toolsbox.contractor.view.activity.main.home.IndustrySelectActivity;
import com.toolsbox.contractor.view.activity.main.market.ContractorResultActivity;
import com.toolsbox.contractor.view.activity.main.market.JobResultActivity;
import com.toolsbox.contractor.view.adapter.PlaceArrayAdapter;
import com.toolsbox.contractor.view.customUI.IconEditText;
import com.warkiz.widget.IndicatorSeekBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class MarketPlaceFragment extends Fragment implements View.OnClickListener {
    private static String TAG = "MarketPlaceFragment";
    private CardView cvSearchJobs;
    private AutoCompleteTextView etArea;
    private PlaceArrayAdapter placeArrayAdapterJob;
    private IndicatorSeekBar sbRangeJob;
    private int selectedIndustryJob;
    private LatLng selectedLocationJob;

    public MarketPlaceFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_market_place, container, false);
        initVariable();
        initUI(view);
        return view;
    }

    void initVariable(){
        selectedIndustryJob = PreferenceHelper.getIndustry();
    }

    void initUI(View view){
        cvSearchJobs = view.findViewById(R.id.cv_search_job);
        cvSearchJobs.setOnClickListener(this);

        etArea = view.findViewById(R.id.et_area);
        etArea.setThreshold(3);
        etArea.setOnItemClickListener(mAutocompleteClickListenerJob);

        placeArrayAdapterJob = new PlaceArrayAdapter(getActivity(), android.R.layout.simple_list_item_1);
        etArea.setAdapter(placeArrayAdapterJob);
        sbRangeJob = view.findViewById(R.id.isb_job);
        sbRangeJob.setIndicatorTextFormat("${PROGRESS}km");
        sbRangeJob.setProgress(200);
    }


    boolean requiredFieldNotEmptyJob(){
        return !(selectedIndustryJob == 0 || selectedLocationJob == null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cv_search_job:
                if (requiredFieldNotEmptyJob()){
                    Intent intent = new Intent(getActivity(), JobResultActivity.class);
                    intent.putExtra("Industry", selectedIndustryJob);
                    intent.putExtra("Lati", selectedLocationJob.latitude);
                    intent.putExtra("Longi", selectedLocationJob.longitude);
                    intent.putExtra("Radius", sbRangeJob.getProgress());
                    startActivity(intent);
                }
                break;

        }
    }


    AdapterView.OnItemClickListener mAutocompleteClickListenerJob
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = placeArrayAdapterJob.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();
            PlacesClient placesClient = com.google.android.libraries.places.api.Places.createClient(getActivity());
            placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                @Override
                public void onSuccess(FetchPlaceResponse response) {
                    Place place = response.getPlace();
                    selectedLocationJob = place.getLatLng();
                    DeviceUtil.closeKeyboard(getActivity());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    selectedLocationJob = null;
                    if (exception instanceof ApiException) {
                        Toast.makeText(getActivity(), exception.getMessage() + "", Toast.LENGTH_SHORT).show();
                    }
                    DeviceUtil.closeKeyboard(getActivity());
                }
            });
        }
    };

}
