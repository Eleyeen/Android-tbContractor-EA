package com.toolsbox.contractor.view.activity.main.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.jaeger.library.StatusBarUtil;
import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.Constant;
import com.toolsbox.contractor.common.interFace.Notify;
import com.toolsbox.contractor.common.model.CreditCardInfo;
import com.toolsbox.contractor.common.model.api.GeneralData;
import com.toolsbox.contractor.common.utils.ApiUtils;
import com.toolsbox.contractor.common.utils.AppPreferenceManager;
import com.toolsbox.contractor.view.activity.basic.BaseActivity;
import com.toolsbox.contractor.view.adapter.CreditCardAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class CreditCardActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, CreditCardAdapter.OnItemClickListener{
    private static String TAG = "CreditCardActivity";

    private Toolbar toolbar;
    private CreditCardAdapter adapter;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private CardView cvAddCreditCard;
    private static final int REQUEST_CODE_INPUT_CC = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card);
        initVariable();
        initUI();
        swipeRefresh.setRefreshing(true);
        fetchCard();
    }

    void initVariable(){

    }

    void initUI(){
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorWhite), 0);
        setupToolbar();
        cvAddCreditCard = findViewById(R.id.cv_add_card);
        cvAddCreditCard.setOnClickListener(this);
        swipeRefresh = findViewById(R.id.refresh_swipe);
        swipeRefresh.setOnRefreshListener(this);
        recyclerView =  findViewById(R.id.recycler_card);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new CreditCardAdapter(this, new ArrayList<>());
        adapter.addListener(this);
        recyclerView.setAdapter(adapter);
    }


    void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.payments);
        } catch (Exception e) {
        }
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
    public void onRefresh() {
        adapter.clear();
        fetchCard();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cv_add_card:
                Intent intent = new Intent(CreditCardActivity.this, InputCCActivity.class);
                startActivityForResult(intent, REQUEST_CODE_INPUT_CC);
                break;
        }
    }

    void fetchCard(){
        String token = AppPreferenceManager.getString(Constant.PRE_TOKEN, "");
        ApiUtils.fetchCards(this, token, new Notify() {
            @Override
            public void onSuccess(Object object) {
                swipeRefresh.setRefreshing(false);
                JsonObject jsonObject = (JsonObject) object;
                if (jsonObject != null){
                    try {
                        JSONObject jsonData = new JSONObject(jsonObject.toString());
                        Log.e(TAG, "-->" + jsonData.toString());
                        if (jsonData.has("status")){
                            int status = jsonData.getInt("status");
                            String message = jsonData.getString("message");
                            if (status == 0){
                                String defaultCard = jsonData.getString("default");
                                JSONArray jsonArrayCard = jsonData.getJSONArray("data");
                                List<CreditCardInfo> arrCards = new ArrayList<>();
                                for (int i = 0; i < jsonArrayCard.length(); i ++){
                                    JSONObject jsonCard = jsonArrayCard.getJSONObject(i);
                                    String id = jsonCard.getString("id");
                                    String last4 = jsonCard.getString("last4");
                                    String brand = jsonCard.getString("brand");
                                    boolean isDefault = false;
                                    if (defaultCard.equals(id))
                                        isDefault = true;
                                    CreditCardInfo cc = new CreditCardInfo(id, last4, brand, isDefault);
                                    arrCards.add(cc);
                                }
                                adapter.addAll(arrCards);
                            } else {
                                Log.e(TAG, message);
                            }
                        }
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(CreditCardActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(CreditCardActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_INPUT_CC:
                    onRefresh();
                    break;

            }
        }
    }


    // onTap item

    @Override
    public void onTapItem(CreditCardInfo item) {
        showProgressDialog();
        String token = AppPreferenceManager.getString(Constant.PRE_TOKEN, "");
        ApiUtils.updateDefaultCreditCard(this, item.id, token, new Notify() {
            @Override
            public void onSuccess(Object object) {
                hideProgressDialog();
                GeneralData data = (GeneralData)object;
                if (data != null){
                    if (data.status == 0){
                       adapter.updateItem(item);
                    } else {
                        Toast.makeText(CreditCardActivity.this, R.string.failure, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CreditCardActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {
                hideProgressDialog();
                Toast.makeText(CreditCardActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
