package com.cyno.reminder.ui;

import com.cyno.reminder.constants.StringConstants;
import com.cyno.reminder.ui.IReminder.TrackerName;
import com.cyno.reminder.R;


//import com.cynozer.reminder.utils.LoginActivity;
//import com.google.android.gms.analytics.HitBuilders;
//import com.google.android.gms.analytics.Tracker;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class SplashActivity extends Activity implements OnClickListener {

	private Animation fadeInAnimation ;
	private Animation moveAnimation ;
	private Animation moveUpAnimation;
//	private View loginButton;
//	private Button skipButton;
//	private boolean isSignedIn;
	private Animation moveInstantlyAnimation;
//	private boolean isSignedInSkipped;
//	private boolean isLogin;




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences pref = getSharedPreferences(StringConstants.PREFERENCE_PROFILE, MODE_PRIVATE);
//		isSignedIn = pref.getBoolean(StringConstants.KEY_USER_SIGNED_GPLUS, false);
//		isSignedInSkipped = pref.getBoolean(StringConstants.SIGN_IN_SKIPPED, false);

		setContentView(R.layout.activity_splash);
//		Intent mIntent = getIntent();
//		if(mIntent != null){
//			String action = mIntent.getAction();
//			if(action != null){
//				isLogin = action.equals(LoginActivity.ACTION_SIGNIN);
//			}
//				
//		}
	

		TextView tv = (TextView) findViewById(R.id.splashscreen_text);
//		loginButton = findViewById(R.id.bt_splash_loginbutton);
//		skipButton = (Button)findViewById(R.id.bt_splash_skip);
//		loginButton.setOnClickListener(this);
//		skipButton.setOnClickListener(this);
		fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
		moveAnimation = AnimationUtils.loadAnimation(this, R.anim.move);
		moveInstantlyAnimation = AnimationUtils.loadAnimation(this, R.anim.move_instantly);
		moveUpAnimation = AnimationUtils.loadAnimation(this, R.anim.moveup);
		fadeInAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {
				if(animation == fadeInAnimation){
//					if(!isSignedIn & ! isSignedInSkipped | isLogin){
//						skipButton.setVisibility(View.VISIBLE);
//						loginButton.setVisibility(View.VISIBLE);
//						skipButton.startAnimation(moveUpAnimation); 
//						skipButton.startAnimation(moveUpAnimation);
//					}else{
						startActivity(new Intent(SplashActivity.this , MainActivity.class));
						finish();
					}
//				}
			}
		});


		tv.startAnimation(fadeInAnimation);
		ImageView iv = (ImageView) findViewById(R.id.splash_imageview);
		iv.setBackgroundResource(R.anim.splash);

		
//		if(!isSignedIn && ! isSignedInSkipped || isLogin){
//			iv.startAnimation(moveAnimation);
//		}else{
			iv.startAnimation(moveInstantlyAnimation);
//			skipButton.setVisibility(View.INVISIBLE);
//			loginButton.setVisibility(View.INVISIBLE);
//		}

		// Get tracker.
//		Tracker t = ((IReminder)getApplication()).getTracker(TrackerName.APP_TRACKER);
//		t.setScreenName("Splash activity");
//		t.send(new HitBuilders.AppViewBuilder().build());
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_signed_out, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/*	@Override
    protected Dialog onCreateDialog(int id) {
        if (id != DIALOG_GET_GOOGLE_PLAY_SERVICES) {
            return super.onCreateDialog(id);
        }

        int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (available == ConnectionResult.SUCCESS) {
            return null;
        }
        if (GooglePlayServicesUtil.isUserRecoverableError(available)) {
            return GooglePlayServicesUtil.getErrorDialog(
                    available, this, REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES);
        }
        return new AlertDialog.Builder(this)
                .setMessage(R.string.plus_generic_error)
                .setCancelable(true)
                .create();
    }*///TODO


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
//		case R.id.bt_splash_loginbutton:
//
//			Intent mIntent = new Intent(this , LoginActivity.class);
//			mIntent.setAction(LoginActivity.ACTION_SIGNIN);
//			startActivity(mIntent);
//			finish();
//			break;

//		case R.id.bt_splash_skip:
//			SharedPreferences.Editor edit = getSharedPreferences(StringConstants.PREFERENCE_PROFILE , MODE_PRIVATE).edit();
//			edit.putBoolean(StringConstants.SIGN_IN_SKIPPED, true);
//			edit.commit();
//			startActivity(new Intent(this , MainActivity.class));
//			finish();
//			break;

		default:
			break;
		}
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

}
