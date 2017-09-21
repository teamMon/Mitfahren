package com.example.jpar4.mitfahren.tmap_test;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.jpar4.mitfahren.R;

public class NewCarpoolList extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    //private ListViewAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    // private Places mPlaces;
  //  private AddressModel mAddressModel;
    private int mPageNumber = 1;
    private boolean mHasMore = true;
   // private List<AddressModel> mAddressList = new ArrayList<AddressModel>();
   private SwipeRefreshLayout mSwipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_carpool_list_activity);

        getSupportActionBar().setTitle("List View");
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_lisiting);

/*        mLoadingMoreDataProgress = (ProgressBar) findViewById(R.id.loading_progress);
        mLoadingMoreDataProgress.getIndeterminateDrawable().setColorFilter(0xff00b1c7, PorterDuff.Mode.MULTIPLY);
        mLoadingMoreDataProgress.setVisibility(View.GONE);*/


        mRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
      //  mAdapter = new ListViewAdapter(mAddressList, this);
      //  mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.requestLayout();

       // mSwipeLayout.setColorSchemeResources(R.color.gomalan_bule_bg);
    }
}
