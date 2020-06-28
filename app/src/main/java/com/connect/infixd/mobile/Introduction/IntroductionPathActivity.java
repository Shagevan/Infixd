package com.connect.infixd.mobile.Introduction;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.connect.infixd.mobile.Application.InfixdBaseActivity;
import com.connect.infixd.mobile.Dialogs.DialogFactory;
import com.connect.infixd.mobile.R;
import com.connect.infixd.mobile.intentservices.FriendIntentService;
import com.connect.infixd.mobile.intentservices.InfixdIntentService;
import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.infixd.client.model.IntroductionPathsResponse;
import com.infixd.client.model.ShortestPathResponse;

import java.util.List;

public class IntroductionPathActivity extends InfixdBaseActivity implements InfixdIntentService.Receiver{
    private List<ShortestPathResponse> shortestPaths;
    private HorizontalInfiniteCycleViewPager infiniteCycleViewPager;
    private Button getInfixedBtn;
    private InfixdIntentService.BroadcastReceiver mBroadCastReciever;
    private String targetName;
    private ConstraintLayout empty;
    private ConstraintLayout sPath;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction_path);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FriendIntentService.ACTION_SEND_INTRODUCTION_REQUEST_SUCCESS);
        intentFilter.addAction(FriendIntentService.ACTION_SEND_INTRODUCTION_REQUEST_FAIL);
        mBroadCastReciever = new InfixdIntentService.BroadcastReceiver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadCastReciever, intentFilter);

        getInfixedBtn = (Button) findViewById(R.id.getInfixedBtn);

        IntroductionPathsResponse response = (IntroductionPathsResponse) getIntent().getSerializableExtra("introductionPathSet");
        shortestPaths = response.getShortestPaths();
        targetName=getIntent().getStringExtra("targetName");

        empty=(ConstraintLayout) findViewById(R.id.empty);
        sPath=(ConstraintLayout) findViewById(R.id.shortPath);

        if(shortestPaths.size()==0){
            empty.setVisibility(View.VISIBLE);
            sPath.setVisibility(View.GONE);
            tv=(TextView) findViewById(R.id.targetName);
            tv.setText(targetName);
        }

        else{
            infiniteCycleViewPager = (HorizontalInfiniteCycleViewPager)findViewById(R.id.hicvp);
            infiniteCycleViewPager.setAdapter(new HorizontalPagerAdapter(this,shortestPaths));

            getInfixedBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = infiniteCycleViewPager.getRealItem();
                    String senderId = shortestPaths.get(position).getStartUserId();
                    String receiverId = shortestPaths.get(position).getShortestPathNodes().get(1).getUserId().replaceAll("\"", "");
                    String targetId = shortestPaths.get(position).getEndUserId();

                    Intent irIntent = new Intent(IntroductionPathActivity.this, FriendIntentService.class);
                    irIntent.setAction(FriendIntentService.ACTION_SEND_INTRODUCTION_REQUEST);
                    irIntent.putExtra(FriendIntentService.DATA_SENDER_ID, senderId);
                    irIntent.putExtra(FriendIntentService.DATA_RECEIVER_ID, receiverId);
                    irIntent.putExtra(FriendIntentService.DATA_TARGET_ID, targetId);
                    startService(irIntent);
                }
            });

        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) switch (intent.getAction()) {
            case FriendIntentService.ACTION_SEND_INTRODUCTION_REQUEST_SUCCESS:
                finish();
                break;
            case FriendIntentService.ACTION_SEND_INTRODUCTION_REQUEST_FAIL:
                DialogFactory.getInstance().make("Server Error", findViewById(R.id.introductionPathActivity)).show();
                break;
        }
    }
}
