package com.toolsbox.contractor.view.activity.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaeger.library.StatusBarUtil;
import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.Constant;
import com.toolsbox.contractor.common.model.ContractorInfo;
import com.toolsbox.contractor.common.model.ServiceItem;
import com.toolsbox.contractor.common.utils.DeviceUtil;
import com.toolsbox.contractor.common.utils.GlobalUtils;
import com.toolsbox.contractor.common.utils.StringHelper;
import com.toolsbox.contractor.common.utils.ValidationHelper;
import com.toolsbox.contractor.view.activity.basic.BaseActivity;
import com.toolsbox.contractor.view.activity.main.home.IndustrySelectActivity;
import com.toolsbox.contractor.view.adapter.ContractorAdapter;
import com.toolsbox.contractor.view.adapter.PlaceArrayAdapter;
import com.toolsbox.contractor.view.customUI.IconEditText;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import co.lujun.androidtagview.TagContainerLayout;


public class SignupActivity extends BaseActivity implements View.OnClickListener{
    private static String TAG = "SignupActivity";
    private ImageButton btnBack;
    private CardView cvContinue;
    private IconEditText etBusinessName, etIndustry, etPhone;
    private AutoCompleteTextView etAddress;
    private RelativeLayout rlIndustry;
    private TagContainerLayout tagIndustry;
    private PlaceArrayAdapter placeArrayAdapter;
    private LatLng selectedLocation;
    private ArrayList<Integer> selectedArrIndustry;
    private static final int REQUEST_CODE_INDUSTRY_SELECT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initVariable();
        initUI();
    }

    void initVariable(){
        selectedLocation = null;
        selectedArrIndustry = new ArrayList<>();
    }

    void initUI(){
        StatusBarUtil.setTranslucent(this, 0);
        btnBack = findViewById(R.id.btn_back);
        cvContinue = findViewById(R.id.cv_continue);
        btnBack.setOnClickListener(this);
        cvContinue.setOnClickListener(this);
        etBusinessName = findViewById(R.id.et_business_name);
        etIndustry = findViewById(R.id.et_industry);
        etPhone = findViewById(R.id.et_phone);
        etAddress = findViewById(R.id.et_address);
        rlIndustry = findViewById(R.id.rl_industry);
        rlIndustry.setOnClickListener(this);
        tagIndustry = findViewById(R.id.tag_industry);

        List<String> arrBusinessType = new LinkedList<>(Arrays.asList(Constant.gArrBusinessType));
        ArrayAdapter<String> spinnerTest = new ArrayAdapter<>(this, R.layout.simple_spinner_item, arrBusinessType);
        spinnerTest.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        etAddress.setThreshold(3);
        etAddress.setOnItemClickListener(mAutocompleteClickListener);
        placeArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1);
        etAddress.setAdapter(placeArrayAdapter);


    }

    AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = placeArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();
            PlacesClient placesClient = com.google.android.libraries.places.api.Places.createClient(SignupActivity.this);
            placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                @Override
                public void onSuccess(FetchPlaceResponse response) {
                    Place place = response.getPlace();
                    selectedLocation = place.getLatLng();
                    DeviceUtil.closeKeyboard(SignupActivity.this);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    if (exception instanceof ApiException) {
                        Log.e(TAG, "Error!" + exception.getMessage());
                        Toast.makeText(SignupActivity.this, exception.getMessage() + "", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    };

    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_back:
                finish();
                break;
            case R.id.cv_continue:

                Toast.makeText(this, etBusinessName.getText()+"  "+etPhone.getText(), Toast.LENGTH_SHORT).show();

                if (requiredFieldNotEmpty()) {
                    DeviceUtil.closeKeyboard(SignupActivity.this);
                    ContractorInfo item = new ContractorInfo();
                    item.business_name = etBusinessName.getText().toString();
                    item.business_number = "";
                    item.business_structure = 0;
                    item.industry = GlobalUtils.getFormatedStringFromIndustry(selectedArrIndustry);
                    item.speciality_title = "";
                    item.phone = etPhone.getText().toString();
                    item.address = etAddress.getText().toString();
                    item.address_lati = String.valueOf(selectedLocation.latitude);
                    item.address_longi = String.valueOf(selectedLocation.longitude);
                    Intent intent = new Intent(SignupActivity.this,  Signup2Activity.class);
                    intent.putExtra("item", item);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, etBusinessName.getText()+"  "+etPhone.getText(), Toast.LENGTH_LONG).show();

                    Toast.makeText(this, R.string.please_fill_all_the_required_info, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.rl_industry:
                Intent intent1 = new Intent(this, IndustrySelectActivity.class);
                startActivityForResult(intent1, REQUEST_CODE_INDUSTRY_SELECT);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_INDUSTRY_SELECT:
                    tagIndustry.removeAllTags();
                    ServiceItem item = (ServiceItem) data.getSerializableExtra("service");
                    selectedArrIndustry = new ArrayList<>();
                    selectedArrIndustry.add(item.getId());
                    tagIndustry.addTag(item.getTitle());
                    break;
            }
        }
    }

    boolean requiredFieldNotEmpty(){

        return !(StringHelper.isEmpty(etBusinessName.getText().toString())  || selectedArrIndustry.size() < 1 ||
                !ValidationHelper.isValidPhoneNumber(etPhone.getText().toString()) || selectedLocation == null);
    }

}
