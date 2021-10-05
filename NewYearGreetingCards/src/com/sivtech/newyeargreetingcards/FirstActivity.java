package com.sivtech.newyeargreetingcards;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class FirstActivity extends Activity{
	 private AdView mAdView;
 	 private InterstitialAd mInterstitial;
	protected void onCreate(Bundle b)
	{
		super.onCreate(b);
		setContentView(R.layout.main);
		
		mInterstitial = new InterstitialAd(this);
		mInterstitial.setAdUnitId(getResources().getString(R.string.ad_unit_id));
		mInterstitial.loadAd(new AdRequest.Builder().build());
		mInterstitial.setAdListener(new ToastAdListener(FirstActivity.this) {
			@Override
			public void onAdLoaded() {
				super.onAdLoaded();

				if (mInterstitial.isLoaded()) {
					mInterstitial.show();
				}
				//System.out.println("ad show in block");
			}

			@Override
			public void onAdFailedToLoad(int errorCode) {
				super.onAdFailedToLoad(errorCode);
			//	System.out.println("ad failed");
			}
		});

		//AdView
		mAdView = (AdView) findViewById(R.id.adView);
		mAdView.setAdListener(new ToastAdListener(this));
		mAdView.loadAd(new AdRequest.Builder().build());
		
		ImageButton bt=(ImageButton)findViewById(R.id.start);
		ImageButton moreapps=(ImageButton)findViewById(R.id.moreapps);
		bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i=new Intent(getApplicationContext(),MainActivity.class);
				startActivity(i);
			}
		});
         moreapps.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i=new Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/developer?id=SIV+TECH"));
				startActivity(i);
				
			}
		});
		
		
	}

}
