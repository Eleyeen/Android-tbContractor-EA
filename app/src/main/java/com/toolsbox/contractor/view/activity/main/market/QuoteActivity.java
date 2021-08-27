package com.toolsbox.contractor.view.activity.main.market;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaeger.library.StatusBarUtil;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.Constant;
import com.toolsbox.contractor.common.interFace.Notify;
import com.toolsbox.contractor.common.model.AttachFileInfo;
import com.toolsbox.contractor.common.model.AvailabilityDateInfo;
import com.toolsbox.contractor.common.model.FromToDateInfo;
import com.toolsbox.contractor.common.model.GeneralJobHistoryInfo;
import com.toolsbox.contractor.common.model.JobDetailInfo;
import com.toolsbox.contractor.common.model.JobInfo;
import com.toolsbox.contractor.common.model.QuoteInfo;
import com.toolsbox.contractor.common.model.api.AttachData;
import com.toolsbox.contractor.common.model.api.GeneralData;
import com.toolsbox.contractor.common.model.api.JobDetailData;
import com.toolsbox.contractor.common.utils.ApiUtils;
import com.toolsbox.contractor.common.utils.AppPreferenceManager;
import com.toolsbox.contractor.common.utils.FileUtils;
import com.toolsbox.contractor.common.utils.GlobalUtils;
import com.toolsbox.contractor.common.utils.MessageUtils;
import com.toolsbox.contractor.common.utils.PreferenceHelper;
import com.toolsbox.contractor.common.utils.StringHelper;
import com.toolsbox.contractor.common.utils.TimeHelper;
import com.toolsbox.contractor.view.activity.basic.BaseActivity;
import com.toolsbox.contractor.view.activity.main.HomeActivity;
import com.toolsbox.contractor.view.activity.main.home.CalendarActivity;
import com.toolsbox.contractor.view.activity.main.home.PostJobActivity;
import com.toolsbox.contractor.view.activity.main.jobs.ImagePreviewActivity;
import com.toolsbox.contractor.view.activity.main.jobs.JobDetail2Activity;
import com.toolsbox.contractor.view.activity.main.jobs.SeeAvailabilityActivity;
import com.toolsbox.contractor.view.adapter.QuoteAttachAdapter;
import com.toolsbox.contractor.view.customUI.ConfirmDialog;
import com.toolsbox.contractor.view.customUI.IconEditText;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;

import static com.toolsbox.contractor.common.Constant.DATE_FORMAT_AVAILABILITY;
import static com.toolsbox.contractor.common.Constant.FRAG_JOBS;
import static com.toolsbox.contractor.common.Constant.FROM_QUOTE_SEND;
import static com.toolsbox.contractor.common.Constant.FROM_QUOTE_UPDATE;
import static com.toolsbox.contractor.common.Constant.PRE_TOKEN;

public class QuoteActivity extends BaseActivity implements View.OnClickListener{
    private static String TAG = "QuoteActivity";
    private static final int REQUEST_CODE_CALENDAR_SELECT = 1;
    private static final int REQUEST_CODE_SELECT_PICTURE = 3;
    private Toolbar toolbar;
    private EditText etValue, etDetails;
    private IconEditText etStartDate;
    private RelativeLayout rlStartDate;
    private Button btnSubmit;
    private SegmentedControl segmentedControl;
    private FromToDateInfo originScheduledDateRange, scheduledDateRange;
    private ArrayList<AvailabilityDateInfo> arrSelectedDate = new ArrayList<>();
    private ArrayList<Date> arrAvailability;
    private JobDetailInfo currentContractorJob;
    private RecyclerView recyclerView;
    private QuoteAttachAdapter adapter;
    private ArrayList<AttachFileInfo> dataAttach = new ArrayList<>();

    private File rawImageFile;
    // Previous intent data
    private int from;
    private JobInfo currentJob;
    private int jobId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote);
        initVariable();
        initUI();
    }

    void initVariable(){
        arrAvailability = new ArrayList<>();
        from = getIntent().getIntExtra("from", FROM_QUOTE_SEND);
        if (from == FROM_QUOTE_UPDATE){
            jobId = getIntent().getIntExtra("jobId", 0);
        } else {
            currentJob = (JobInfo) getIntent().getSerializableExtra("currentJob");
            arrSelectedDate = GlobalUtils.convertAvailabilityDateModel(currentJob.availability_dates);
        }
    }

    void initUI(){
        StatusBarUtil.setTranslucent(this, 0);
        setupToolbar();
        segmentedControl = findViewById(R.id.segmented_control);
        etValue = findViewById(R.id.et_value);
        etDetails = findViewById(R.id.et_details);
        etStartDate = findViewById(R.id.et_start_date);
        rlStartDate = findViewById(R.id.rl_start_date);
        rlStartDate.setOnClickListener(this);
        btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);
        segmentedControl.setSelectedSegment(0);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new QuoteAttachAdapter(this, dataAttach);
        adapter.addListener(new QuoteAttachAdapter.OnItemClickListener() {
            @Override
            public void onItemAddClick() {
                chooseAction();
            }

            @Override
            public void onItemRemoveClick(AttachFileInfo item) {
                MessageUtils.showCustomAlertDialog(QuoteActivity.this, "Remove", "Are you sure you want to remove the image?", new Notify() {
                    @Override
                    public void onSuccess(Object object) {
                        dataAttach.remove(item);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFail() {

                    }
                });
            }

            @Override
            public void onItemOpen(AttachFileInfo item) {
//                Intent intent = new Intent(QuoteActivity.this, ImagePreviewActivity.class);
//                intent.putExtra("isLocal", true);
//                intent.putExtra("file", item.localUrl);
//                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        if (from == FROM_QUOTE_UPDATE){
            fetchContractorQuoteDetails();
        }
    }

    void chooseAction() {
        File dir = FileUtils.getDiskCacheDir(this, "temp");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String name = StringHelper.getDateRandomString() + ".png";
        rawImageFile = new File(dir, name);
        Intent captureImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        captureImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(rawImageFile));

        Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(pickIntent, getString(R.string.profile_photo));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {captureImageIntent});

        startActivityForResult(chooserIntent, REQUEST_CODE_SELECT_PICTURE);
    }

    void updateView(){
        arrSelectedDate = GlobalUtils.convertAvailabilityDateModel(currentContractorJob.job.availability_dates);
        scheduledDateRange = GlobalUtils.convertStringToAvailabilityDateInfo(currentContractorJob.bid.availability_start_dates);
        originScheduledDateRange = GlobalUtils.convertStringToAvailabilityDateInfo(currentContractorJob.bid.availability_start_dates);

        // initialize bid info
        if (currentContractorJob.bid.type == 0) {
            etValue.setText(currentContractorJob.bid.budget);
            segmentedControl.setSelectedSegment(0);
        } else {
            etValue.setText(currentContractorJob.bid.hourly_rate);
            segmentedControl.setSelectedSegment(1);
        }
        etDetails.setText(currentContractorJob.bid.description);
        if (currentContractorJob.bid.status == Constant.QUOTE_AWARDED) {
            btnSubmit.setText("Apply");
        } else {
            etStartDate.setText("View Availability");
            btnSubmit.setText("Update");
        }
        etDetails.setEnabled(false);
        etValue.setEnabled(false);
        segmentedControl.setEnabled(false);
        btnSubmit.setEnabled(false);
    }

    void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        TextView tvToolbar = toolbar.findViewById(R.id.toolbar_title);
        tvToolbar.setText(R.string.proposal);
        setSupportActionBar(toolbar);
        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        } catch (Exception e) {
        }
    }

    boolean requiredFieldNotEmpty(){
        return !(StringHelper.isEmpty(etValue.getText().toString()) || etValue.getText().toString().equals("0")
        || StringHelper.isEmpty(etDetails.getText().toString()) || scheduledDateRange == null);
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
            case R.id.rl_start_date:
                Intent intent1 = new Intent(this, SeeAvailabilityActivity.class);
                intent1.putExtra("isEditable", true);
                Gson gson = new Gson();
                String strArrDate = gson.toJson(arrSelectedDate);
                intent1.putExtra("arrDates", strArrDate);
                intent1.putExtra("scheduled", scheduledDateRange);
                startActivityForResult(intent1, REQUEST_CODE_CALENDAR_SELECT);
                break;
            case R.id.btn_submit:
                int jobType = segmentedControl.getLastSelectedAbsolutePosition();
                if (requiredFieldNotEmpty()){
                    if (from == Constant.FROM_QUOTE_SEND)
                        sendQuote();
                    else
                        updateQuote();
                } else {
                    Toast.makeText(QuoteActivity.this, R.string.please_fill_all_the_required_info, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    boolean isUpdatedScheduledDate(){
        return !TimeHelper.checkSameDate(scheduledDateRange.fromDate, originScheduledDateRange.fromDate) || !TimeHelper.checkSameDate(scheduledDateRange.toDate, originScheduledDateRange.toDate
        );
    }

    boolean checkActionType(Intent data) {
        boolean isCamera = true;
        if (data != null ) {
            String action = data.getAction();
            if ((data.getData() == null) && (data.getClipData() == null)) {
                isCamera = true;
            } else {
                isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
            }
        }
        return isCamera;
    }

    public Uri getPickImageResultUri(Intent  data) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null  && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ?  Uri.fromFile(rawImageFile) : data.getData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_CALENDAR_SELECT:
                    if (data != null) {
                        scheduledDateRange = (FromToDateInfo) data.getSerializableExtra("selectedDate");
                        etStartDate.setText("View Availability");
                        if (from == FROM_QUOTE_UPDATE) {
                            if (isUpdatedScheduledDate()) {
                                btnSubmit.setEnabled(true);
                            } else {
                                btnSubmit.setEnabled(false);
                            }
                        }
                    }
                    break;
                case REQUEST_CODE_SELECT_PICTURE:
                    if (checkActionType(data)) { // Camera
                        Uri imageUri =  getPickImageResultUri(data);
                        File originFile = new File(imageUri.getPath());
                        File compressFile = FileUtils.compressImage(this, originFile);
                        uploadAttachment(compressFile);
                    } else {  // Gallery
                        if (data.getData() != null){
                            Uri uri = data.getData();
                            File originFile = FileUtils.getFile(this, uri);
                            File compressFile = FileUtils.compressImage(this, originFile);
                            uploadAttachment(compressFile);
                        }
                    }
                    break;
            }
        }
    }

    void uploadAttachment(File file){
        showProgressDialog();
        ApiUtils.uploadAttachment(this, file, new Notify() {
            @Override
            public void onSuccess(Object object) {
                hideProgressDialog();
                AttachData data = (AttachData) object;
                if (data != null){
                    if (data.status == 0){
                        dataAttach.add(new AttachFileInfo(file.getName(), data.info, file.getAbsolutePath(), false));
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(QuoteActivity.this, data.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(QuoteActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {
                hideProgressDialog();
                Toast.makeText(QuoteActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }

    void fetchContractorQuoteDetails(){
        showProgressDialog();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("job_id", jobId);
            jsonObject.put("contractor_id", PreferenceHelper.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiUtils.jobContractorDetail(this, jsonObject, new Notify() {
            @Override
            public void onSuccess(Object object) {
                hideProgressDialog();
                JobDetailData data = (JobDetailData) object;
                if (data != null) {
                    if (data.status == 0) {
                        currentContractorJob = data.info;
                        if (!StringHelper.isEmpty(currentContractorJob.bid.attachment)) {
                            for (String item : Arrays.asList(currentContractorJob.bid.attachment.split(","))) {
                                dataAttach.add(new AttachFileInfo(GlobalUtils.fetchDownloadFileName(item), item, "", false));
                            }
                            adapter.notifyDataSetChanged();
                        }
                        updateView();
                    } else {
                        Toast.makeText(QuoteActivity.this, data.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(QuoteActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {
                hideProgressDialog();
                Toast.makeText(QuoteActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }

    void sendQuote(){
        showProgressDialog();
        int jobType = segmentedControl.getLastSelectedAbsolutePosition();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", jobType);
            jsonObject.put("job_id", currentJob.id);
            if (jobType == 0) {  // Fixed
                jsonObject.put("budget", etValue.getText().toString());
                jsonObject.put("hourly_rate", "");
            } else {             // Hourly
                jsonObject.put("budget", "");
                jsonObject.put("hourly_rate", etValue.getText().toString());
            }
            jsonObject.put("description", etDetails.getText().toString());
            jsonObject.put("start_dates", fetchAvailableDate());
            jsonObject.put("attachment", fetchAttachment());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "Params" + jsonObject.toString());
        ApiUtils.sendQuote(this, jsonObject, PreferenceHelper.getToken(), new Notify() {
            @Override
            public void onSuccess(Object object) {
                hideProgressDialog();
                GeneralData data = (GeneralData) object;
                if (data != null){
                    if (data.status == 0){
                        MessageUtils.showConfirmAlertDialog(QuoteActivity.this, getResources().getString(R.string.your_quote_has_been_placed), new Notify() {
                            @Override
                            public void onSuccess(Object object) {
                                goToJobs();
                            }

                            @Override
                            public void onFail() {

                            }
                        });
                    } else {
                        Toast.makeText(QuoteActivity.this, data.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(QuoteActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {
                hideProgressDialog();
                Toast.makeText(QuoteActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }

    String fetchAttachment(){
        List<String> arrString = new ArrayList<>();
        for (AttachFileInfo item : dataAttach) {
            arrString.add(item.url);
        }
        return TextUtils.join(",", arrString);
    }

    String fetchAvailableDate(){
        String strFrom = GlobalUtils.convertDateToString(scheduledDateRange.fromDate, DATE_FORMAT_AVAILABILITY, TimeZone.getTimeZone("UTC"));
        String strTo = GlobalUtils.convertDateToString(scheduledDateRange.toDate, DATE_FORMAT_AVAILABILITY, TimeZone.getTimeZone("UTC"));
        return strFrom + "-" + strTo;
    }

    void updateQuote(){
        showProgressDialog();
        int jobType = segmentedControl.getLastSelectedAbsolutePosition();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", jobType);
            jsonObject.put("bid_id", currentContractorJob.bid.id);
            if (jobType == 0) {  // Fixed
                jsonObject.put("budget", etValue.getText().toString());
                jsonObject.put("hourly_rate", "");
            } else {             // Hourly
                jsonObject.put("budget", "");
                jsonObject.put("hourly_rate", etValue.getText().toString());
            }
            jsonObject.put("description", etDetails.getText().toString());
            jsonObject.put("start_dates", fetchAvailableDate());
            jsonObject.put("attachment", fetchAttachment());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiUtils.updateQuote(this, jsonObject, PreferenceHelper.getToken(), new Notify() {
            @Override
            public void onSuccess(Object object) {
                hideProgressDialog();
                GeneralData data = (GeneralData) object;
                if (data != null){
                    if (data.status == 0){
                        MessageUtils.showConfirmAlertDialog(QuoteActivity.this, getResources().getString(R.string.your_quote_has_been_updated), new Notify() {
                            @Override
                            public void onSuccess(Object object) {
                                goToJobs();
                            }

                            @Override
                            public void onFail() {

                            }
                        });
                    } else {
                        Toast.makeText(QuoteActivity.this, R.string.failure, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(QuoteActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {
                hideProgressDialog();
                Toast.makeText(QuoteActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void goToJobs() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("indexFrag", FRAG_JOBS);
        startActivity(intent);
    }

}
