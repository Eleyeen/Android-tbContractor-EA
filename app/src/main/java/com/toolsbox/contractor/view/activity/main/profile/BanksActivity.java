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
import com.toolsbox.contractor.common.model.BankInfo;
import com.toolsbox.contractor.common.model.CreditCardInfo;
import com.toolsbox.contractor.common.model.api.GeneralData;
import com.toolsbox.contractor.common.utils.ApiUtils;
import com.toolsbox.contractor.common.utils.AppPreferenceManager;
import com.toolsbox.contractor.view.activity.basic.BaseActivity;
import com.toolsbox.contractor.view.adapter.BankAdapter;
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

public class BanksActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, BankAdapter.OnItemClickListener{
    private static String TAG = "BanksActivity";

    private Toolbar toolbar;
    private BankAdapter adapter;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private CardView cvAddBank;
    private static final int REQUEST_CODE_ADD_BANK = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banks);
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
        cvAddBank = findViewById(R.id.cv_add_card);
        cvAddBank.setOnClickListener(this);
        swipeRefresh = findViewById(R.id.refresh_swipe);
        swipeRefresh.setOnRefreshListener(this);
        recyclerView =  findViewById(R.id.recycler_card);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new BankAdapter(this, new ArrayList<>());
        adapter.addListener(this);
        recyclerView.setAdapter(adapter);
    }


    void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.bank_accounts);
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
                Intent intent = new Intent(BanksActivity.this, AddBankActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD_BANK);
                break;
        }
    }

    void fetchCard(){
        String token = AppPreferenceManager.getString(Constant.PRE_TOKEN, "");
        ApiUtils.fetchBankCards(this, token, new Notify() {
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
                                JSONArray jsonArrayCard = jsonData.getJSONArray("data");
                                List<BankInfo> arrCards = new ArrayList<>();
                                for (int i = 0; i < jsonArrayCard.length(); i ++){
                                    JSONObject jsonCard = jsonArrayCard.getJSONObject(i);
                                    String id = jsonCard.getString("id");
                                    String last4 = jsonCard.getString("last4");
                                    String routingNumber = jsonCard.getString("routing_number");
                                    boolean defaultCurrency = jsonCard.getBoolean("default_for_currency");
                                    String bankName = "";
                                    BankInfo cc = new BankInfo(id, last4, routingNumber, defaultCurrency, bankName);
                                    arrCards.add(cc);
                                }
                                adapter.addAll(arrCards);
                            } else {
                                Toast.makeText(BanksActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(BanksActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(BanksActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_ADD_BANK:
                    onRefresh();
                    break;

            }
        }
    }


    // onTap item

    @Override
    public void onTapItem(BankInfo item) {
        showProgressDialog();
        String token = AppPreferenceManager.getString(Constant.PRE_TOKEN, "");
        ApiUtils.updateDefaultBank(this, item.id, token, new Notify() {
            @Override
            public void onSuccess(Object object) {
                hideProgressDialog();
                GeneralData data = (GeneralData)object;
                if (data != null){
                    if (data.status == 0){
                       adapter.updateItem(item);
                    } else {
                        Toast.makeText(BanksActivity.this, R.string.failure, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(BanksActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {
                hideProgressDialog();
                Toast.makeText(BanksActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
