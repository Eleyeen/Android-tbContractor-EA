package com.toolsbox.contractor.view.activity.main.market;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.Constant;
import com.toolsbox.contractor.common.interFace.Notify;
import com.toolsbox.contractor.common.model.ContractorInfo;
import com.toolsbox.contractor.common.model.ReviewInfo;
import com.toolsbox.contractor.common.model.api.ReviewArrayData;
import com.toolsbox.contractor.common.model.api.ReviewData;
import com.toolsbox.contractor.common.utils.ApiUtils;
import com.toolsbox.contractor.common.utils.AppPreferenceManager;
import com.toolsbox.contractor.view.activity.basic.BaseActivity;
import com.toolsbox.contractor.view.adapter.ReviewsAdapter;
import com.toolsbox.contractor.view.adapter.listener.PaginationScrollListener;

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

public class ReviewsActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener{
    private static String TAG = "ReviewActivity";

    private Toolbar toolbar;
    private ReviewsAdapter adapter;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private RelativeLayout rlNodata, rlLoadingData;
    private CardView cvAddReview;
    private int from;
    private ContractorInfo currentContractor;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int CURRENT_PAGE = 1;
    private static final int REQUEST_CODE_ADD_REVIEW = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        initVariable();
        initUI();
        fetchReviews();
    }

    void initVariable(){
        from = getIntent().getIntExtra("from", Constant.FROM_OTHER_REVIEW);
        if (from == Constant.FROM_OTHER_REVIEW)
            currentContractor = (ContractorInfo) getIntent().getSerializableExtra("contractor");
    }

    void initUI(){
        StatusBarUtil.setTranslucent(this, 0);
        setupToolbar();
        rlNodata = findViewById(R.id.rl_no_data);
        rlLoadingData = findViewById(R.id.rl_loading_data);
        cvAddReview = findViewById(R.id.cv_add_review);
        cvAddReview.setOnClickListener(this);
        recyclerView =  findViewById(R.id.recycler_reviews);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ReviewsAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                CURRENT_PAGE ++;
                fetchReviews();
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        if (from == Constant.FROM_MY_REVIEW)
            cvAddReview.setVisibility(View.GONE);
    }

    void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        TextView tvToolbar = toolbar.findViewById(R.id.toolbar_title);
        if (from == Constant.FROM_OTHER_REVIEW)
            tvToolbar.setText(currentContractor.business_name +  " Reviews");
        else
            tvToolbar.setText(R.string.my_reviews);
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        CURRENT_PAGE = 1;
        isLastPage = false;
        adapter.clear();
        fetchReviews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cv_add_review:
                Intent intent = new Intent(ReviewsActivity.this, AddReviewActivity.class);
                intent.putExtra("contractor", currentContractor);
                startActivityForResult(intent, REQUEST_CODE_ADD_REVIEW);
                break;
        }
    }

    void fetchReviews(){
        isLoading = true;
        updateBackground();
        int contractorId;
        if (from == Constant.FROM_OTHER_REVIEW)
            contractorId = currentContractor.id;
        else
            contractorId = AppPreferenceManager.getInt(Constant.PRE_ID, 0);
        ApiUtils.fetchReiew(this, CURRENT_PAGE, Constant.PER_PAGE, contractorId, new Notify() {
            @Override
            public void onSuccess(Object object) {
                isLoading = false;
                ReviewData data = (ReviewData)object;
                if (data != null){
                    if (data.status == 0){
                        if (CURRENT_PAGE != 1) adapter.removeLoading();
                        List<ReviewArrayData> arrReviews = data.info;
                        int totalReviews = data.total;
                        adapter.addAll(arrReviews);
                        // Check if need more loading
                        int totalPage = (totalReviews + Constant.PER_PAGE -1) / Constant.PER_PAGE;
                        if (CURRENT_PAGE < totalPage)
                            adapter.addLoading();
                        else
                            isLastPage = true;
                    } else {
                        Toast.makeText(ReviewsActivity.this, data.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ReviewsActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
                updateBackground();
            }

            @Override
            public void onFail() {
                isLoading = false;
                Toast.makeText(ReviewsActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
                updateBackground();
            }
        });
    }

    void updateBackground(){
        if (adapter.getItemCount() == 0) {
            if (isLoading) {
                rlNodata.setVisibility(View.GONE);
                rlLoadingData.setVisibility(View.VISIBLE);
            } else {
                rlNodata.setVisibility(View.VISIBLE);
                rlLoadingData.setVisibility(View.GONE);
            }
        } else {
            rlNodata.setVisibility(View.GONE);
            rlLoadingData.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_ADD_REVIEW:
                    onRefresh();
                    break;

            }
        }
    }
}
