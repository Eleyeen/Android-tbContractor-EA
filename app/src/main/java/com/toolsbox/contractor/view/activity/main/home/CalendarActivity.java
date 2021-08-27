package com.toolsbox.contractor.view.activity.main.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaeger.library.StatusBarUtil;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.interFace.Notify;
import com.toolsbox.contractor.common.model.FromToDateInfo;
import com.toolsbox.contractor.common.model.TimePreferItemInfo;
import com.toolsbox.contractor.common.utils.MessageUtils;
import com.toolsbox.contractor.common.utils.StringHelper;
import com.toolsbox.contractor.view.activity.basic.BaseActivity;
import com.toolsbox.contractor.view.adapter.TimeSlotAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.prolificinteractive.materialcalendarview.MaterialCalendarView.SELECTION_MODE_MULTIPLE;
import static com.prolificinteractive.materialcalendarview.MaterialCalendarView.SELECTION_MODE_NONE;

public class CalendarActivity extends BaseActivity {
    private static String TAG = "CalendarActivity";

    private Toolbar toolbar;
    private MaterialCalendarView calendarView;
    private GridLayoutManager layoutManager;
    private TimeSlotAdapter adapter;
    private RecyclerView recyclerView;
    private List<CalendarDay> selectedDates = new ArrayList<>();
    private ArrayList<TimePreferItemInfo> arrTimePrefer = new ArrayList<>();
    private boolean isEditable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        initVariable();
        initUI();
    }

    void initVariable(){
        isEditable = getIntent().getBooleanExtra("isEditable", true);
        String strTimePrefer = getIntent().getStringExtra("arrTimePrefer");
        if (StringHelper.isEmpty(strTimePrefer)){
            arrTimePrefer = new ArrayList<>();
        } else {
            Gson gson = new Gson();
            arrTimePrefer = gson.fromJson(strTimePrefer, new TypeToken<ArrayList<TimePreferItemInfo>>(){}.getType());
        }
        String strSelectedDate = getIntent().getStringExtra("arrSelectedDates");
        ArrayList<Date> arrTempDate = new ArrayList<>();
        if (!StringHelper.isEmpty(strSelectedDate)) {
            Gson gson = new Gson();
            arrTempDate =  gson.fromJson(strSelectedDate, new TypeToken<ArrayList<Date>>(){}.getType());
            for (Date item : arrTempDate) {
                selectedDates.add(CalendarDay.from(item));
            }
        }

    }

    void initUI(){
        StatusBarUtil.setTranslucent(this, 0);
        setupToolbar();
        calendarView = findViewById(R.id.calendar_view);
        calendarView.setSelectionMode(SELECTION_MODE_NONE);

        // customize calendar
        if (isEditable){
            calendarView.state().edit()
                    .setMinimumDate(new Date())
                    .commit();
        }
        for (CalendarDay item : selectedDates){
            calendarView.setDateSelected(item, true);
        }
        recyclerView = findViewById(R.id.recycler);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new TimeSlotAdapter(this, arrTimePrefer);
        recyclerView.setAdapter(adapter);
    }

    void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        TextView tvToolbar = toolbar.findViewById(R.id.toolbar_title);
        tvToolbar.setText(R.string.customer_availability);
        setSupportActionBar(toolbar);
        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        } catch (Exception e) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_industry, menu);
        if (!isEditable){
            final MenuItem saveItemView = menu.findItem(R.id.action_done);
            saveItemView.setVisible(false);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_done:
                selectedDates = calendarView.getSelectedDates();
                if (selectedDates.size() < 1 || getSelectedTime() == null) {
                    MessageUtils.showCustomAlertDialogNoCancel(CalendarActivity.this, "Note", "Please select an availability date and a time availability", new Notify() {
                        @Override
                        public void onSuccess(Object object) {

                        }

                        @Override
                        public void onFail() {

                        }
                    });
                } else if (isEditable) {
                    Intent intent = new Intent();
                    intent.putExtra("selectedDate", getSelectedTime());
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    FromToDateInfo getSelectedTime(){
        for (TimePreferItemInfo item : arrTimePrefer) {
            if (item.isSelected) {
                return item.timeStamp;
            }
        }
        return null;
    }



}
