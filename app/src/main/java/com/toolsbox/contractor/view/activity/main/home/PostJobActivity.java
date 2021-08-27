package com.toolsbox.contractor.view.activity.main.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.Constant;
import com.toolsbox.contractor.common.interFace.Notify;
import com.toolsbox.contractor.common.model.ContractorInfo;
import com.toolsbox.contractor.common.model.GeneralJobHistoryInfo;
import com.toolsbox.contractor.common.model.JobInfo;
import com.toolsbox.contractor.common.model.api.GeneralData;
import com.toolsbox.contractor.common.utils.ApiUtils;
import com.toolsbox.contractor.common.utils.AppPreferenceManager;
import com.toolsbox.contractor.common.utils.GlobalUtils;
import com.toolsbox.contractor.common.utils.StringHelper;
import com.toolsbox.contractor.view.activity.basic.BaseActivity;
import com.toolsbox.contractor.view.adapter.PlaceArrayAdapter;
import com.toolsbox.contractor.view.customUI.ConfirmDialog;
import com.toolsbox.contractor.view.customUI.IconEditText;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static com.toolsbox.contractor.common.Constant.FROM_JOB_EDIT;
import static com.toolsbox.contractor.common.Constant.FROM_JOB_HIRE;
import static com.toolsbox.contractor.common.Constant.FROM_JOB_POST;
import static com.toolsbox.contractor.common.Constant.gArrSpecialization;

public class PostJobActivity extends BaseActivity implements View.OnClickListener, ConfirmDialog.OnPositiveClickListener {
    private static String TAG = "PostJobActivity";
    private static final int REQUEST_CODE_INDUSTRY_SELECT = 1;
    private static final int REQUEST_CODE_CALENDAR_SELECT = 2;

    private Toolbar toolbar;
    private IconEditText etJobName, etIndustry, etAvailability;
    private EditText etDetails;
    private Spinner spUrgency;
    private ArrayAdapter<String> projectAdapter;
    private PlaceArrayAdapter placeArrayAdapter;
    private AutoCompleteTextView etArea;
    private RelativeLayout rlAvailability, rlIndustry, rlIndustryContainer;
    private Button btnSubmit;

    private int selectedIndustry;
    private ArrayList<Date> arrAvailability;
    private LatLng selectedLocation;

    // from activity
    private int from;
    private ContractorInfo contractor;
    private GeneralJobHistoryInfo job;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_job);
        initVariable();
        initUI();
    }

    void initVariable(){
        selectedIndustry = 0;
        arrAvailability = new ArrayList<>();
        selectedLocation = null;
        etArea = findViewById(R.id.et_area);
        etArea.setThreshold(3);
        etArea.setOnItemClickListener(mAutocompleteClickListener);
        placeArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1);
        etArea.setAdapter(placeArrayAdapter);
        projectAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_dropdown_item , gArrSpecialization);
        projectAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        from = getIntent().getIntExtra("from", Constant.FROM_JOB_POST);
        switch (from){
            case FROM_JOB_POST:
                break;
            case FROM_JOB_EDIT:
                job = (GeneralJobHistoryInfo) getIntent().getSerializableExtra("job");
                break;
            case FROM_JOB_HIRE:
                contractor = (ContractorInfo) getIntent().getSerializableExtra("contractor");
                break;
        }
    }

    void initUI(){
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorWhite), 0);
        setupToolbar();
        btnSubmit = findViewById(R.id.btn_submit);
        etJobName = findViewById(R.id.et_job_name);
        etIndustry = findViewById(R.id.et_industry);
        etAvailability = findViewById(R.id.et_availability);
        etDetails = findViewById(R.id.et_details);
        etIndustry.setOnClickListener(this);
        spUrgency = findViewById(R.id.sp_urgency);
        rlAvailability = findViewById(R.id.rl_availability);
        rlIndustry = findViewById(R.id.rl_industry);
        rlIndustryContainer = findViewById(R.id.rl_industry_container);
        btnSubmit.setOnClickListener(this);
        rlAvailability.setOnClickListener(this);
        rlIndustry.setOnClickListener(this);
        etAvailability.setEnabled(false);
        etIndustry.setEnabled(false);
        List<String> dataset = new LinkedList<>(Arrays.asList("High (Within 24 hours)", "Medium (2-3 business days)", "Low (Within 5 business days)"));
        ArrayAdapter<String> spinnerUrgency = new ArrayAdapter<>(this, R.layout.simple_spinner_item2, dataset);
        spinnerUrgency.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spUrgency.setAdapter(spinnerUrgency);

        if (from == FROM_JOB_HIRE){
            rlIndustryContainer.setVisibility(View.GONE);
            selectedIndustry = getIntent().getIntExtra("industry", 1);
            btnSubmit.setText(R.string.award_job);
        } else if (from == FROM_JOB_EDIT){
            etJobName.setText(job.jobName);
            etArea.setText(job.area);
            selectedLocation = new LatLng(Double.valueOf(job.area_lati), Double.valueOf(job.area_longi));
            List<CalendarDay> arrDates = GlobalUtils.getArrayCalendarDateFromString(job.jobAvailability, Constant.DATE_FORMAT_DEFAULT);
            for (CalendarDay item : arrDates){
                arrAvailability.add(item.getDate());
            }
            selectedIndustry = job.industry;
            etIndustry.setText(Constant.gArrSpecialization[selectedIndustry - 1]);
            etAvailability.setText(GlobalUtils.getFormatedStringFromDates(arrAvailability, Constant.DATE_FORMAT_SHORT));
            spUrgency.setSelection(job.urgency);
            etDetails.setText(job.detail);
        }
    }

    void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            switch (from){
                case FROM_JOB_POST:
                    actionBar.setTitle(R.string.post_job);
                    break;
                case FROM_JOB_EDIT:
                    actionBar.setTitle(R.string.edit_a_job);
                    break;
                case FROM_JOB_HIRE:
                    actionBar.setTitle("Hire " + contractor.business_name);
                    break;
            }

        } catch (Exception e) {
        }
    }

    boolean requiredFieldNotEmpty(){
        return !(StringHelper.isEmpty(etJobName.getText().toString()) || selectedIndustry == 0
                || selectedLocation == null || arrAvailability.size() == 0 || StringHelper.isEmpty(etDetails.getText().toString()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_industry:
                Intent intent = new Intent(this, IndustrySelectActivity.class);
                intent.putExtra("isMultiple", false);
                startActivityForResult(intent, REQUEST_CODE_INDUSTRY_SELECT);
                break;
            case R.id.rl_availability:
                Intent intent1 = new Intent(this, CalendarActivity.class);
                intent1.putExtra("isEditable", true);
                startActivityForResult(intent1, REQUEST_CODE_CALENDAR_SELECT);
                break;
            case R.id.btn_submit:
                if (requiredFieldNotEmpty()){
                    switch (from){
                        case FROM_JOB_POST:
                            postJob();
                            break;
                        case FROM_JOB_EDIT:
                            editJob();
                            break;
                        case FROM_JOB_HIRE:
                            hireJob();
                            break;
                    }
                } else {
                    Toast.makeText(PostJobActivity.this, R.string.please_fill_all_the_required_info, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    void postJob(){
        showProgressDialog();
        int posterId = AppPreferenceManager.getInt(Constant.PRE_ID, 0);
        String posterName = AppPreferenceManager.getString(Constant.PRE_BUSINESS_NAME, "");
        String strAvailability = GlobalUtils.getFormatedStringFromDates(arrAvailability, Constant.DATE_FORMAT_DEFAULT);

        ApiUtils.postJob(this, etJobName.getText().toString(), selectedIndustry, etArea.getText().toString(),
                String.valueOf(selectedLocation.latitude), String.valueOf(selectedLocation.longitude),
                etDetails.getText().toString(), 1, posterId, posterName, spUrgency.getSelectedItemPosition(), strAvailability, new Notify() {
                    @Override
                    public void onSuccess(Object object) {
                        hideProgressDialog();
                        GeneralData data = (GeneralData) object;
                        if (data != null){
                            if (data.status == 0){
                                showConfirmAlertDialog(R.string.your_job_has_been_posted_successfully);
                            } else {
                                Toast.makeText(PostJobActivity.this, R.string.failure, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PostJobActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFail() {
                        hideProgressDialog();
                        Toast.makeText(PostJobActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void hireJob(){
        showProgressDialog();
        int posterId = AppPreferenceManager.getInt(Constant.PRE_ID, 0);
        String posterName = AppPreferenceManager.getString(Constant.PRE_BUSINESS_NAME, "");
        String strAvailability = GlobalUtils.getFormatedStringFromDates(arrAvailability, Constant.DATE_FORMAT_DEFAULT);
        int contractorId = contractor.id;

        ApiUtils.hireJob(this, etJobName.getText().toString(), selectedIndustry, etArea.getText().toString(),
                String.valueOf(selectedLocation.latitude), String.valueOf(selectedLocation.longitude),
                etDetails.getText().toString(), 1, posterId, posterName, spUrgency.getSelectedItemPosition(), strAvailability, contractorId, new Notify() {
                    @Override
                    public void onSuccess(Object object) {
                        hideProgressDialog();
                        GeneralData data = (GeneralData) object;
                        if (data != null){
                            if (data.status == 0){
                                showConfirmAlertDialog(R.string.your_hiring_proposal_has_been);
                            } else {
                                Toast.makeText(PostJobActivity.this, R.string.failure, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PostJobActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFail() {
                        hideProgressDialog();
                        Toast.makeText(PostJobActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void editJob(){
        showProgressDialog();
        String token = AppPreferenceManager.getString(Constant.PRE_TOKEN, "");
        String strAvailability = GlobalUtils.getFormatedStringFromDates(arrAvailability, Constant.DATE_FORMAT_DEFAULT);
        int jobId = job.jobId;

        ApiUtils.editJob(this, jobId,  etJobName.getText().toString(), selectedIndustry, etArea.getText().toString(),
                String.valueOf(selectedLocation.latitude), String.valueOf(selectedLocation.longitude),
                etDetails.getText().toString(), spUrgency.getSelectedItemPosition(), strAvailability, token, new Notify() {
                    @Override
                    public void onSuccess(Object object) {
                        hideProgressDialog();
                        GeneralData data = (GeneralData) object;
                        if (data != null){
                            if (data.status == 0){
                                showConfirmAlertDialog(R.string.your_proposal_has_been_updated);
                            } else {
                                Toast.makeText(PostJobActivity.this, R.string.failure, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PostJobActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFail() {
                        hideProgressDialog();
                        Toast.makeText(PostJobActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = placeArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();
            PlacesClient placesClient = com.google.android.libraries.places.api.Places.createClient(PostJobActivity.this);
            placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                @Override
                public void onSuccess(FetchPlaceResponse response) {
                    Place place = response.getPlace();
                    selectedLocation = place.getLatLng();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    if (exception instanceof ApiException) {
                        Toast.makeText(PostJobActivity.this, exception.getMessage() + "", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_INDUSTRY_SELECT:
                    String temp = data.getStringExtra("selectedIndustry");
                    Gson gson = new Gson();
                    ArrayList<Integer> arrIndustry = new ArrayList<>();
                    arrIndustry = gson.fromJson(temp, new TypeToken<ArrayList<Integer>>(){}.getType());
                    if (arrIndustry.size() > 0){
                        selectedIndustry = arrIndustry.get(0) + 1;
                        etIndustry.setText(Constant.gArrSpecialization[arrIndustry.get(0)]);
                    }
                    break;

                case REQUEST_CODE_CALENDAR_SELECT:
                    String temp1 = data.getStringExtra("selectedDates");
                    Gson gson1 = new Gson();
                    List<CalendarDay> arr = new ArrayList<>();
                    arr = gson1.fromJson(temp1, new TypeToken<List<CalendarDay>>(){}.getType());
                    arrAvailability.clear();
                    for (CalendarDay item : arr){
                        arrAvailability.add(item.getDate());
                    }
                    etAvailability.setText(GlobalUtils.getFormatedStringFromDates(arrAvailability, Constant.DATE_FORMAT_SHORT));
                    Log.e(TAG, "Selected Date:" + temp1);
                    break;
            }
        }
    }

    void showConfirmAlertDialog(int strId){
        ConfirmDialog dlg = new ConfirmDialog(this, getResources().getString(strId), this);
        dlg.show();
    }


    // Confirm Dialog button Listener
    @Override
    public void onClickPositive() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
