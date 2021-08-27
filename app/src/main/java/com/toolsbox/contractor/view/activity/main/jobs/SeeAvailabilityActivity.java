package com.toolsbox.contractor.view.activity.main.jobs;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaeger.library.StatusBarUtil;
import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.interFace.Notify;
import com.toolsbox.contractor.common.model.AvailabilityDateInfo;
import com.toolsbox.contractor.common.model.FromToDateInfo;
import com.toolsbox.contractor.common.model.TimePreferItemInfo;
import com.toolsbox.contractor.common.utils.GlobalUtils;
import com.toolsbox.contractor.common.utils.MessageUtils;
import com.toolsbox.contractor.common.utils.TimeHelper;
import com.toolsbox.contractor.view.activity.basic.BaseActivity;
import com.toolsbox.contractor.view.activity.main.home.CalendarActivity;
import com.toolsbox.contractor.view.adapter.AvailabilityAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SeeAvailabilityActivity extends BaseActivity {
    private static String TAG = "SeeAvailabilityActivity";
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private AvailabilityAdapter adapter;
    private Boolean isEditable;
    private ArrayList<AvailabilityDateInfo> arrDates = new ArrayList<>();
    private FromToDateInfo scheduledDateRange;
    private TextView tvDescription;
    private static final int REQUEST_CODE_CALENDAR_SELECT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_availability);
        initVariable();
        initUI();
        checkScheduledTime();
    }

    void initVariable(){
        isEditable = getIntent().getBooleanExtra("isEditable", true);
        String strArrDate = getIntent().getStringExtra("arrDates");
        Gson gson = new Gson();
        arrDates = gson.fromJson(strArrDate, new TypeToken<ArrayList<AvailabilityDateInfo>>(){}.getType());
        scheduledDateRange = (FromToDateInfo) getIntent().getSerializableExtra("scheduled");
    }

    void initUI(){
        StatusBarUtil.setTranslucent(this, 0);
        setupToolbar();
        tvDescription = findViewById(R.id.tv_description);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new AvailabilityAdapter(this, arrDates, isEditable);
        adapter.addListener(new AvailabilityAdapter.OnItemClickListener() {
            @Override
            public void onItemSelectClick(AvailabilityDateInfo item) {
                ArrayList<TimePreferItemInfo> arrTimePrefer = new ArrayList<>();
                ArrayList<Date> arrSelectedDate = new ArrayList<>();
                for (FromToDateInfo fItem : item.timeStamp) {
                    String strFromTo = GlobalUtils.getStringStamp(fItem);
                    arrTimePrefer.add(new TimePreferItemInfo(strFromTo, false, fItem));
                }
                arrSelectedDate.add(item.timeStamp.get(0).fromDate);
                if (new Date().after(item.timeStamp.get(0).toDate)) {
                    MessageUtils.showCustomAlertDialogNoCancel(SeeAvailabilityActivity.this, "Note", "Please select a future time availability", new Notify() {
                        @Override
                        public void onSuccess(Object object) {

                        }

                        @Override
                        public void onFail() {

                        }
                    });
                } else {
                    Intent intent = new Intent(SeeAvailabilityActivity.this, CalendarActivity.class);
                    intent.putExtra("isEditable", true);
                    Gson gson = new Gson();
                    String strTimePrefer = gson.toJson(arrTimePrefer);
                    String strSelectedDate = gson.toJson(arrSelectedDate);
                    intent.putExtra("arrTimePrefer", strTimePrefer);
                    intent.putExtra("arrSelectedDates", strSelectedDate);
                    startActivityForResult(intent, REQUEST_CODE_CALENDAR_SELECT);
                }
            }

        });
        recyclerView.setAdapter(adapter);
        if (!isEditable) {
            tvDescription.setVisibility(View.GONE);
        }
    }

    void checkScheduledTime(){
        for (AvailabilityDateInfo item : arrDates) {
            if (scheduledDateRange == null) {
                item.isSelected = false;
            } else {
                item.isSelected = isIncludeScheduledTime(item, scheduledDateRange);
            }
        }
        adapter.notifyDataSetChanged();
    }

    boolean isIncludeScheduledTime(AvailabilityDateInfo model, FromToDateInfo scheduledTime){
        boolean isInclude = false;
        for (FromToDateInfo timeStamp : model.timeStamp) {
            if (TimeHelper.checkSameDate(scheduledDateRange.fromDate, timeStamp.fromDate)) {
                isInclude = true;
            }
        }
        return isInclude;
    }

    void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        TextView tvToolbar = toolbar.findViewById(R.id.toolbar_title);
        tvToolbar.setText(R.string.availability);
        setSupportActionBar(toolbar);
        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        } catch (Exception e) {
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_save:
                if (scheduledDateRange != null && isEditable) {
                    Intent intent = new Intent();
                    intent.putExtra("selectedDate", scheduledDateRange);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else {
                    finish();
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_availability, menu);
        if (!isEditable){
            final MenuItem saveItemView = menu.findItem(R.id.action_save);
            saveItemView.setVisible(false);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_CALENDAR_SELECT:
                    if (data != null){
                        scheduledDateRange = (FromToDateInfo) data.getSerializableExtra("selectedDate");
                        checkScheduledTime();
                    }
                    break;
            }
        }
    }
}
