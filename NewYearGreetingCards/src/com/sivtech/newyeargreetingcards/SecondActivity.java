package com.sivtech.newyeargreetingcards;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ShortBuffer;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.provider.MediaStore.Images;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.FloatMath;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class SecondActivity extends Activity implements OnEditorActionListener,
OnClickListener, OnSeekBarChangeListener,OnTouchListener{

	
    Typeface fontface;
	private static SharedPreferences prefs;
	private static ImageView img;
	private static EditText edt;
	private TextView textLength;
	private static ImageButton saveButton, cancelButton,textf;
	private static ColorPanelView colorText;
	private static ColorPanelView colorBorder;
	private static ColorPanelView colorShadow;
	ColorPickerDialog textPicker;

	private static Button colorTextButton;
	private static Button colorBorderbButton;
	private static Button colorShadowButton;

	private static TextView border_txt, shadow_txt, shadowH_txt, shadowV_txt;
	private static SeekBar borderSBar, shadowSBar, shadowHSBar, shadowVSBar;

	private static PreferenceActivity pa;
	private int font = 90;
	private int[] length = new int[8];
	private int counter = -1;
	private static boolean isDicrease = false;
	private static int textColor, borderColor, shadowColor;
	private static int borderSize, shadowSize, shadowH, shadowV;
	private static int textWidth, textHeight;
	private int imgWidth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.text_to_bitmap);

		int pixel=this.getWindowManager().getDefaultDisplay().getWidth();

		if (pixel >= 600) {
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}else{
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		textColor = prefs.getInt("text_color", 0xffffffff);

		colorText = (ColorPanelView) findViewById(R.id.text_color);
		colorTextButton = (Button) findViewById(R.id.text_color_btn);
		colorTextButton.setOnClickListener(this);
		colorText.setColor(textColor);

		borderColor = prefs.getInt("border_color", Color.BLACK);
		colorBorder = (ColorPanelView) findViewById(R.id.border_color);
		colorBorderbButton = (Button) findViewById(R.id.border_color_btn);
		colorBorderbButton.setOnClickListener(this);
		colorBorder.setColor(borderColor);

		shadowColor = prefs.getInt("shadow_color", Color.BLACK);
		colorShadow = (ColorPanelView) findViewById(R.id.shadow_color);
		colorShadowButton = (Button) findViewById(R.id.shadow_color_btn);
		colorShadowButton.setOnClickListener(this);
		colorShadow.setColor(shadowColor);

		borderSize = prefs.getInt("border_size", 2);
		border_txt = (TextView) findViewById(R.id.border_size);
		borderSBar = (SeekBar) findViewById(R.id.border_seek);
		border_txt.setText("" + borderSize);
		borderSBar.setProgress(borderSize);
		borderSBar.setOnSeekBarChangeListener(this);

		shadowSize = prefs.getInt("shadow_size", 2);
		shadow_txt = (TextView) findViewById(R.id.shadow_size);
		shadowSBar = (SeekBar) findViewById(R.id.shadow_seek);
		shadow_txt.setText("" + shadowSize);
		shadowSBar.setProgress(shadowSize);
		shadowSBar.setOnSeekBarChangeListener(this);

		shadowH = prefs.getInt("shadowH", 0);
		shadowH_txt = (TextView) findViewById(R.id.hori_txt);
		shadowHSBar = (SeekBar) findViewById(R.id.hori_seek);
		shadowH_txt.setText("" + (shadowH));
		shadowHSBar.setProgress(shadowH +25);
		shadowHSBar.setOnSeekBarChangeListener(this);

		shadowV = prefs.getInt("shadowV", 0);
		shadowV_txt = (TextView) findViewById(R.id.ver_txt);
		shadowVSBar = (SeekBar) findViewById(R.id.ver_seek);
		shadowV_txt.setText("" + (shadowV));
		shadowVSBar.setProgress(shadowV + 25);
		shadowVSBar.setOnSeekBarChangeListener(this);

		textLength = (TextView) findViewById(R.id.text_length);
		textf=(ImageButton)findViewById(R.id.font);
		textf.setOnClickListener(this);
		saveButton = (ImageButton) findViewById(R.id.save_btn);
		saveButton.setOnClickListener(this);
		cancelButton = (ImageButton) findViewById(R.id.cancel_btn);
		cancelButton.setOnClickListener(this);
		edt = (EditText) findViewById(R.id.edt_txt);
		edt.addTextChangedListener(inputTextWatcher);

		img = (ImageView) findViewById(R.id.txt_img);

		imgWidth = img.getLayoutParams().width;
		if (imgWidth == -1) {
			imgWidth = getResources().getDisplayMetrics().widthPixels;
		}

		Log.e("textSize:...^^^", "width: " + imgWidth + " height: " + img.getLayoutParams().height);
		Bitmap bmp = drawTextToBitmap(this, R.drawable.ic_launcher, "");
		img.setImageBitmap(bmp);

		Log.e("", "" + textColor);
		colorText.setOnClickListener(this);
		BitmapFactory.Options options= new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;

		img.setOnTouchListener(this);

	}

	TextWatcher inputTextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

			Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());

			img.setImageBitmap(bmp);

			textLength.setText("" + edt.length());
			//Toast.makeText(getApplicationContext(), "" + edt.length(), Toast.LENGTH_SHORT).show();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}
	};

	private Bitmap combainImage(Context gContext, int bottom, int top){
		Resources resources = gContext.getResources();
		//float scale = resources.getDisplayMetrics().density;
		Bitmap bottomImage = BitmapFactory.decodeResource(resources, top);
		Bitmap topImage = BitmapFactory.decodeResource(resources, bottom);
		android.graphics.Bitmap.Config bitmapConfig = bottomImage.getConfig();
		
		// set default bitmap config if none
		if (bitmapConfig == null) {
			bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
		}
		// resource bitmaps are imutable,
		// so we need to convert it to mutable one
		bottomImage = bottomImage.copy(bitmapConfig, true);
		Log.e("$$$$", "Width: " + bottomImage.getWidth() + " Height: " + bottomImage.getHeight());
		int imgWidth = topImage.getWidth();
		int imgHeight = topImage.getHeight();

		// Constrain to given size but keep aspect ratio
		float scaleFactor = Math.min(((float) bottomImage.getWidth()) / imgWidth, ((float) bottomImage.getHeight()) / imgHeight);

		Matrix scale = new Matrix();
		scale.postScale(scaleFactor, scaleFactor);
		final Bitmap scaledImage = Bitmap.createBitmap(topImage, 0, 0, imgWidth, imgHeight, scale, false);

		//topImage = Bitmap.createBitmap(topImage,0,0,100, 100);
		Canvas comboImage = new Canvas(bottomImage);
		// Then draw the second on top of that
		comboImage.drawBitmap(scaledImage, 0f, 0f, null);

		return bottomImage;
	}

	public Bitmap drawTextToBitmap(Context gContext, int gResId, String gText) {
		Resources resources = gContext.getResources();
		int width = resources.getDisplayMetrics().widthPixels;
		width = imgWidth - 90;
		float scale = resources.getDisplayMetrics().density;
		scale = 1.0f;
		Log.e("scale", "" + scale);
		Bitmap bitmap = BitmapFactory.decodeResource(resources, gResId);

		android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();

		// set default bitmap config if none
		if (bitmapConfig == null) {
			bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
		}
		// resource bitmaps are imutable,
		// so we need to convert it to mutable one
		//bitmap = bitmap.copy(bitmapConfig, true);
		bitmap = Bitmap.createBitmap(resources.getDisplayMetrics().widthPixels + 80, 250, bitmapConfig);

		Canvas canvas = new Canvas(bitmap);
		// new antialised Paint
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		// text color - #3D3D3D
		paint.setTypeface(fontface);
		paint.setColor(textColor);
		Rect bounds = new Rect();

		// text size in pixels
		paint.setTextSize((int) (font * scale));

		// text shadow
		Log.e("SHADOW.....", "shadowH: " + shadowH + " shadowV: " + shadowV);
		paint.setShadowLayer(shadowSize, shadowH, shadowV, shadowColor);
		Paint strokePaint = new Paint();
		if (borderSize > 0) {
			strokePaint.setAntiAlias(true);
			strokePaint.setColor(borderColor);
			strokePaint.setTextSize(font * scale);
			strokePaint.setTypeface(fontface);
			//strokePaint.setTypeface(Typeface.DEFAULT_BOLD);
			strokePaint.setStyle(Paint.Style.STROKE);
			strokePaint.setStrokeWidth(borderSize);
		}

		// draw text to the Canvas center
		paint.getTextBounds(gText, 0, gText.length(), bounds);
		Log.e("", "" + paint.getShader());
		Log.e("gText>>>>>>>>>>>>>", "length: " + gText.length());
		float mt = paint.measureText(gText);
		int bw = bounds.width();
		Log.e("bw", "" + bw);
		if (bw > width) {
			font = font -10;
			counter++;
			length[counter] = gText.length();
			isDicrease = true;
		}else if (counter >= 0) {
			if (length[counter] == gText.length()){
				if (font < 90) {
					font = font + 10;
					length[counter]=0;
					counter--;
				}
			}
		}

		Log.e("LCG", String.format(
				"measureText %f, getTextBounds %d (%s)",
				mt,
				bw, bounds.toShortString())
				);
		int x = (bitmap.getWidth() - bounds.width()) / 2;
		int y = (bitmap.getHeight() + bounds.height()) / 2;

		textWidth = (bounds.width() + (shadowSize * 2)) + 10;
		textHeight = bounds.height() + (shadowSize * 2);
		//textHeight = (int) (textHeight + (textHeight / 3));
		Bitmap bit = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		Log.e("......", "X: " + x + " Y: " + y);
		canvas.drawText(gText, x * scale, y * scale, paint);
		if (borderSize > 0) {
			canvas.drawText(gText, x * scale, y * scale, strokePaint);
		}

		return bitmap;
	}

	private void downloadImageInPhone(Bitmap b) {
		String filename = "tempImage";
		ContentValues values = new ContentValues();
		values.put(Images.Media.TITLE, filename);
		values.put(Images.Media.DATE_ADDED, System.currentTimeMillis());
		values.put(Images.Media.MIME_TYPE, "image/png");
		/*uri = getContentResolver().insert(Images.Media.EXTERNAL_CONTENT_URI,
				values);*/
		Uri uri = Uri.fromFile(new File("/sdcard/textImage.png"));

		try {
			OutputStream outStream = getContentResolver().openOutputStream(uri);
			b.compress(Bitmap.CompressFormat.PNG, 100, outStream);
			outStream.flush();
			outStream.close();
			Log.d("done", "done");
			/*Toast.makeText(TextToBitmapActivity.this,
					"Photo is Successfully Downloaded in your Android Phone.",
					Toast.LENGTH_LONG).show();*/
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

		return false;
	}


	@Override
	public void onClick( final View v) {
		switch (v.getId()) {
		case R.id.text_color_btn:

			Toast.makeText(getApplicationContext(), "text" +0xffffffff, Toast.LENGTH_LONG).show();
			textPicker = new ColorPickerDialog(SecondActivity.this, prefs
					.getInt("text_color", 0xffffffff));

			textPicker.setAlphaSliderVisible(true);

			textPicker.setButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					textColor = textPicker.getColor();
					Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
					img.setImageBitmap(bmp);
					SharedPreferences.Editor editor = prefs.edit();
					editor.putInt("text_color", textPicker.getColor());
					editor.commit();
					colorText.setColor(textColor);
				}
			});

			textPicker.setButton2("Cancel", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			});
			textPicker.show();
			break;
		case R.id.border_color_btn:
			final ColorPickerDialog borderPicker = new ColorPickerDialog(SecondActivity.this, prefs.getInt("border_color", Color.BLACK));
			borderPicker.setAlphaSliderVisible(true);
			borderPicker.setButton("Ok", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					borderColor = borderPicker.getColor();
					Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
					img.setImageBitmap(bmp);
					SharedPreferences.Editor editor = prefs.edit();
					editor.putInt("border_color", borderPicker.getColor());
					editor.commit();
					colorBorder.setColor(borderColor);
				}
			});
			borderPicker.setButton2("Cancel", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			});
			borderPicker.show();
			break;
		case R.id.shadow_color_btn:
			final ColorPickerDialog shadowPicker = new ColorPickerDialog(SecondActivity.this, prefs
					.getInt("shadow_color", Color.BLACK));
			shadowPicker.setAlphaSliderVisible(true);
			shadowPicker.setButton("Ok", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					shadowColor = shadowPicker.getColor();
					Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
					img.setImageBitmap(bmp);
					SharedPreferences.Editor editor = prefs.edit();
					editor.putInt("shadow_color", shadowPicker.getColor());
					editor.commit();
					colorShadow.setColor(shadowColor);
				}
			});
			shadowPicker.setButton2("Cancel", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			});
			shadowPicker.show();
			break;
		case R.id.font:
			LayoutInflater layoutInflater = (LayoutInflater)getBaseContext()
			.getSystemService(LAYOUT_INFLATER_SERVICE); 
			View popupView = layoutInflater.inflate(R.layout.popfont, null);
			final PopupWindow popupWindow = new PopupWindow(popupView,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			
			TextView face1 = (TextView)popupView.findViewById(R.id.font1);
			TextView face2= (TextView)popupView.findViewById(R.id.font2);
			TextView face3 = (TextView)popupView.findViewById(R.id.font3);
			TextView face4= (TextView)popupView.findViewById(R.id.font4);
			TextView face5=(TextView)popupView.findViewById(R.id.font5);
			TextView face6=(TextView)popupView.findViewById(R.id.font6);
			TextView face7=(TextView)popupView.findViewById(R.id.font7);
			TextView face8=(TextView)popupView.findViewById(R.id.font8);
			TextView face9=(TextView)popupView.findViewById(R.id.font9);
			TextView face10=(TextView)popupView.findViewById(R.id.font10);
			TextView face11=(TextView)popupView.findViewById(R.id.font11);
			TextView face12=(TextView)popupView.findViewById(R.id.font12);
			TextView face13=(TextView)popupView.findViewById(R.id.font13);
			TextView face14=(TextView)popupView.findViewById(R.id.font14);
			TextView face15=(TextView)popupView.findViewById(R.id.font15);
			TextView face16 = (TextView)popupView.findViewById(R.id.font16);
			TextView face17= (TextView)popupView.findViewById(R.id.font17);
			TextView face18 = (TextView)popupView.findViewById(R.id.font18);
			TextView face19= (TextView)popupView.findViewById(R.id.font19);
			TextView face20=(TextView)popupView.findViewById(R.id.font20);
			TextView face21=(TextView)popupView.findViewById(R.id.font21);
			TextView face22=(TextView)popupView.findViewById(R.id.font22);
			TextView face23=(TextView)popupView.findViewById(R.id.font23);
			TextView face24=(TextView)popupView.findViewById(R.id.font24);
			TextView face25=(TextView)popupView.findViewById(R.id.font25);
			TextView face26= (TextView)popupView.findViewById(R.id.font26);
			TextView face27= (TextView)popupView.findViewById(R.id.font27);
			TextView face28 = (TextView)popupView.findViewById(R.id.font28);
			TextView face29= (TextView)popupView.findViewById(R.id.font29);
			TextView face30=(TextView)popupView.findViewById(R.id.font30);
			TextView face31=(TextView)popupView.findViewById(R.id.font31);
			TextView face32=(TextView)popupView.findViewById(R.id.font32);
			TextView face33=(TextView)popupView.findViewById(R.id.font33);
			TextView face34=(TextView)popupView.findViewById(R.id.font34);
			TextView face35=(TextView)popupView.findViewById(R.id.font35);

			face1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					fontface= Typeface.createFromAsset(
							v.getContext().getAssets(), 
							"fonts/today.ttf");
					Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
					img.setImageBitmap(bmp);

					popupWindow.dismiss();
				}
			});
			face2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					fontface = Typeface.createFromAsset(
							v.getContext().getAssets(), 
							"fonts/bleeding_cowboys.ttf");
					Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
					img.setImageBitmap(bmp);
					popupWindow.dismiss();
				}
			});
			face3.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					fontface = Typeface.createFromAsset(
							v.getContext().getAssets(), 
							"fonts/panhead.ttf");
					Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
					img.setImageBitmap(bmp);
					popupWindow.dismiss();
				}
			});
			face4.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					 fontface = Typeface.createFromAsset(
							v.getContext().getAssets(), 
							"fonts/swinsbrg.ttf");
					Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
					img.setImageBitmap(bmp);
					popupWindow.dismiss();
				}
			});
			face5.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					 fontface = Typeface.createFromAsset(
							v.getContext().getAssets(), 
							"fonts/chunq_dipped.ttf");
					Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
					img.setImageBitmap(bmp);
					popupWindow.dismiss();
				}
			});
			face6.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					 fontface = Typeface.createFromAsset(
							v.getContext().getAssets(), 
							"fonts/sealed_with_a_kiss.ttf");
					Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
					img.setImageBitmap(bmp);
					popupWindow.dismiss();
				}
			});
			face7.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					 fontface = Typeface.createFromAsset(
							v.getContext().getAssets(), 
							"fonts/iloversyou.ttf");
					Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
					img.setImageBitmap(bmp);
					popupWindow.dismiss();
				}
			});
			face8.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					 fontface = Typeface.createFromAsset(
							v.getContext().getAssets(), 
							"fonts/loveness.ttf");
					Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
					img.setImageBitmap(bmp);
					popupWindow.dismiss();
				}
			});
			face9.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					 fontface = Typeface.createFromAsset(
							v.getContext().getAssets(), 
							"fonts/Sevillana-Regular.ttf");
					Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
					img.setImageBitmap(bmp);
					popupWindow.dismiss();
				}
			});
			face10.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					 fontface = Typeface.createFromAsset(
							v.getContext().getAssets(), 
							"fonts/Wedgie Regular.ttf");
					Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
					img.setImageBitmap(bmp);
					popupWindow.dismiss();
				}
			});
			face11.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					 fontface = Typeface.createFromAsset(
							v.getContext().getAssets(), 
							"fonts/look-up.ttf");
					Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
					img.setImageBitmap(bmp);
					popupWindow.dismiss();
				}
			});
			face12.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					 fontface = Typeface.createFromAsset(
							v.getContext().getAssets(), 
							"fonts/holyuni.ttf");
					Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
					img.setImageBitmap(bmp);
					popupWindow.dismiss();
				}
			});
			face13.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					 fontface = Typeface.createFromAsset(
							v.getContext().getAssets(), 
							"fonts/Yank.ttf");
					Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
					img.setImageBitmap(bmp);
					popupWindow.dismiss();
				}
			});
			face14.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					 fontface = Typeface.createFromAsset(
							v.getContext().getAssets(), 
							"fonts/at_sign_regular.ttf");
					Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
					img.setImageBitmap(bmp);
					popupWindow.dismiss();
				}
			});
			face15.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					 fontface = Typeface.createFromAsset(
							v.getContext().getAssets(), 
							"fonts/Barbarian.ttf");
					Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
					img.setImageBitmap(bmp);
					popupWindow.dismiss();
				}
			});
			face16.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					fontface= Typeface.createFromAsset(
							v.getContext().getAssets(), 
							"fonts/BRICK.TTF");
					Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
					img.setImageBitmap(bmp);

					popupWindow.dismiss();
				}
			});
			face17.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					fontface = Typeface.createFromAsset(
							v.getContext().getAssets(), 
							"fonts/christmaseve.ttf");
					Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
					img.setImageBitmap(bmp);
					popupWindow.dismiss();
				}
			});
			face18.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					fontface = Typeface.createFromAsset(
							v.getContext().getAssets(), 
							"fonts/ChristmasLigtness.ttf");
					Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
					img.setImageBitmap(bmp);
					popupWindow.dismiss();
				}
			});
			face19.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					 fontface = Typeface.createFromAsset(
							v.getContext().getAssets(), 
							"fonts/Fancy-Tattoo-Script.ttf");
					Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
					img.setImageBitmap(bmp);
					popupWindow.dismiss();
				}
			});
			face20.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					 fontface = Typeface.createFromAsset(
							v.getContext().getAssets(), 
							"fonts/ka_blamo.ttf");
					Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
					img.setImageBitmap(bmp);
					popupWindow.dismiss();
				}
			});
			face21.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					 fontface = Typeface.createFromAsset(
							v.getContext().getAssets(), 
							"fonts/Kingthings Needles.ttf");
					Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
					img.setImageBitmap(bmp);
					popupWindow.dismiss();
				}
			});
			face22.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					 fontface = Typeface.createFromAsset(
							v.getContext().getAssets(), 
							"fonts/One Wild Line.ttf");
					Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
					img.setImageBitmap(bmp);
					popupWindow.dismiss();
				}
			});
			face23.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					 fontface = Typeface.createFromAsset(
							v.getContext().getAssets(), 
							"fonts/Overhaul.ttf");
					Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
					img.setImageBitmap(bmp);
					popupWindow.dismiss();
				}
			});
			face24.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					 fontface = Typeface.createFromAsset(
							v.getContext().getAssets(), 
							"fonts/Power.ttf");
					Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
					img.setImageBitmap(bmp);
					popupWindow.dismiss();
				}
			});
			face25.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					 fontface = Typeface.createFromAsset(
							v.getContext().getAssets(), 
							"fonts/Raven's Claws.ttf");
					Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
					img.setImageBitmap(bmp);
					popupWindow.dismiss();
				}
			});
			face26.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					fontface= Typeface.createFromAsset(
							v.getContext().getAssets(), 
							"fonts/Regal Demise.ttf");
					Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
					img.setImageBitmap(bmp);

					popupWindow.dismiss();
				}
			});
			face27.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					fontface = Typeface.createFromAsset(
							v.getContext().getAssets(), 
							"fonts/ReynoldsCaps.ttf");
					Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
					img.setImageBitmap(bmp);
					popupWindow.dismiss();
				}
			});
			face28.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					fontface = Typeface.createFromAsset(
							v.getContext().getAssets(), 
							"fonts/ShineOn.ttf");
					Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
					img.setImageBitmap(bmp);
					popupWindow.dismiss();
				}
			});
			face29.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					 fontface = Typeface.createFromAsset(
							v.getContext().getAssets(), 
							"fonts/slopeness.ttf");
					Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
					img.setImageBitmap(bmp);
					popupWindow.dismiss();
				}
			});
			face30.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					 fontface = Typeface.createFromAsset(
							v.getContext().getAssets(), 
							"fonts/StickLetterMedium.ttf");
					Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
					img.setImageBitmap(bmp);
					popupWindow.dismiss();
				}
			});
			face31.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					 fontface = Typeface.createFromAsset(
							v.getContext().getAssets(), 
							"fonts/Uechi Gothic.ttf");
					Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
					img.setImageBitmap(bmp);
					popupWindow.dismiss();
				}
			});
			face32.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					 fontface = Typeface.createFromAsset(
							v.getContext().getAssets(), 
							"fonts/Uechi-Italic.ttf");
					Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
					img.setImageBitmap(bmp);
					popupWindow.dismiss();
				}
			});
			face33.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					 fontface = Typeface.createFromAsset(
							v.getContext().getAssets(), 
							"fonts/Urban_slick.ttf");
					Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
					img.setImageBitmap(bmp);
					popupWindow.dismiss();
				}
			});
			face34.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					 fontface = Typeface.createFromAsset(
							v.getContext().getAssets(), 
							"fonts/vnyltle.ttf");
					Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
					img.setImageBitmap(bmp);
					popupWindow.dismiss();
				}
			});
			face35.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					 fontface = Typeface.createFromAsset(
							v.getContext().getAssets(), 
							"fonts/VoodooVampire.ttf");
					Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
					img.setImageBitmap(bmp);
					popupWindow.dismiss();
				}
			});

			popupWindow.showAtLocation(textf, Gravity.CENTER, 0, 0);
            break;
			
			
			
		case R.id.save_btn:
			if (!edt.getText().toString().equals("")) {
				Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
				Log.e(">>>>", "Width: " + bmp.getWidth() + " Height: " + bmp.getHeight());
				Log.e("Text", "Width: " + textWidth + " Height: " + textHeight);
				Log.e("Compare", "Width: " + (bmp.getWidth() - textWidth) + " Height: " + (bmp.getHeight() - textHeight));

				int startX = (bmp.getWidth() - textWidth) / 2;
				int startY = (bmp.getHeight() - textHeight) / 2;
				Bitmap  cropedBitmap = Bitmap.createBitmap(bmp, startX, startY, textWidth, textHeight + (textHeight / 3));
				if (shadowH < 0) {
					cropedBitmap = Bitmap.createBitmap(bmp, startX + shadowH, startY, textWidth - shadowH, textHeight  + (textHeight / 3));
					if (shadowV < 0) {
						cropedBitmap = Bitmap.createBitmap(bmp, startX + shadowH, startY + shadowV, textWidth - shadowH, (textHeight - shadowV)  + (textHeight / 3));
					}else if (shadowV > 0) {
						cropedBitmap = Bitmap.createBitmap(bmp, startX + shadowH, startY, textWidth - shadowH, (textHeight + shadowV)  + (textHeight / 3));
					}
				}else if (shadowH > 0) {
					cropedBitmap = Bitmap.createBitmap(bmp, startX, startY, textWidth + shadowH, textHeight  + (textHeight / 3));
					if (shadowV < 0) {
						cropedBitmap = Bitmap.createBitmap(bmp, startX, startY + shadowV, textWidth + shadowH, (textHeight - shadowV) + (textHeight / 3));
					}else if (shadowV > 0) {
						cropedBitmap = Bitmap.createBitmap(bmp, startX, startY, textWidth + shadowH, (textHeight + shadowV) + (textHeight / 3));
					}
				}else{
					if (shadowV < 0) {
						cropedBitmap = Bitmap.createBitmap(bmp, startX, startY + shadowV, textWidth, (textHeight - shadowV) + (textHeight / 3));
					}else if (shadowV > 0) {
						cropedBitmap = Bitmap.createBitmap(bmp, startX, startY, textWidth, (textHeight + shadowV) + (textHeight / 3));
					}
				}
				//Util u = new Util();
				CommonUtillities.bitmap=cropedBitmap;

				saveBitmap(cropedBitmap, "PhotoCollage/Text", "text");
				//downloadImageInPhone(cropedBitmap);
				setResult(RESULT_OK);
				SecondActivity.this.finish();
			}else{
				Toast.makeText(getApplicationContext(), "You must write some text here, if you don't please press cancel.", Toast.LENGTH_LONG).show();
			}

			break;
		case R.id.cancel_btn:
			setResult(RESULT_CANCELED);
			SecondActivity.this.finish();
			break;
		default:
			break;
		}

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (seekBar == borderSBar) {
			borderSize = progress;
			border_txt.setText("" + borderSize);
			Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
			img.setImageBitmap(bmp);
		}
		if (seekBar == shadowSBar) {
			shadowSize = progress;
			shadow_txt.setText("" + shadowSize);
			Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
			img.setImageBitmap(bmp);
		}
		if (seekBar == shadowHSBar) {
			shadowH = progress - 25;
			shadowH_txt.setText("" + shadowH);
			Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
			img.setImageBitmap(bmp);
		}
		if (seekBar == shadowVSBar) {
			shadowV = progress - 25;
			shadowV_txt.setText("" + shadowV);
			Bitmap bmp = drawTextToBitmap(SecondActivity.this, R.drawable.ic_launcher, edt.getText().toString());
			img.setImageBitmap(bmp);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		if (seekBar == borderSBar) {
			SharedPreferences.Editor editor = prefs.edit();
			editor.putInt("border_size", borderSize);
			editor.commit();
		}
		if (seekBar == shadowSBar) {
			SharedPreferences.Editor editor = prefs.edit();
			editor.putInt("shadow_size", shadowSize);
			editor.commit();
		}
		if (seekBar == shadowHSBar) {
			SharedPreferences.Editor editor = prefs.edit();
			editor.putInt("shadowH", shadowH);
			editor.commit();
		}
		if (seekBar == shadowVSBar) {
			SharedPreferences.Editor editor = prefs.edit();
			editor.putInt("shadowV", shadowV);
			editor.commit();
		}
	}

	private void saveBitmap(Bitmap bitmap, String dir, String baseName) {
		try {
			File sdcard = Environment.getExternalStorageDirectory();
			File pictureDir = new File(sdcard, dir);
			pictureDir.mkdirs();
			File f = null;

			String name = baseName + ".png";
			f = new File(pictureDir, name);
			/*if (!f.exists()) {
                    break;
                }*/
			String name1 = f.getAbsolutePath();
			FileOutputStream fos = new FileOutputStream(name1);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();

		} catch (Exception e) {
		} finally {
			/*
            if (fos != null) {
                fos.close();
            }
			 */
		}

	}

	class Util {
		public final int SDK_VERSION = getSdkVersion();

		private int getSdkVersion() {
			try {
				return Integer.parseInt(Build.VERSION.SDK);
			} catch (NumberFormatException e) {
				return 3;
			}
		}

		String saveBitmap(Bitmap bitmap, String dir, String baseName) {
			try {
				File sdcard = Environment.getExternalStorageDirectory();
				File pictureDir = new File(sdcard, dir);
				pictureDir.mkdirs();
				File f = null;
				for (int i = 1; i < 200; ++i) {
					String name = baseName + i + ".png";
					f = new File(pictureDir, name);
					if (!f.exists()) {
						break;
					}
				}
				if (!f.exists()) {
					String name = f.getAbsolutePath();
					FileOutputStream fos = new FileOutputStream(name);
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
					fos.flush();
					fos.close();
					return name;
				}
			} catch (Exception e) {
			} finally {
				/*
	            if (fos != null) {
	                fos.close();
	            }
				 */
			}
			return null;
		}

		void bitmapBGRtoRGB(Bitmap bitmap, int width, int height) {
			int size = width * height;
			short data[] = new short[size];
			ShortBuffer buf = ShortBuffer.wrap(data);
			bitmap.copyPixelsToBuffer(buf);
			for (int i = 0; i < size; ++i) {
				//BGR-565 to RGB-565
				short v = data[i];
				data[i] = (short) (((v&0x1f) << 11) | (v&0x7e0) | ((v&0xf800) >> 11));
			}
			buf.rewind();
			bitmap.copyPixelsFromBuffer(buf);
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub


		ImageView view = (ImageView) v; 
		
		System.out.println("matrix=" + savedMatrix.toString()); 
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			savedMatrix.set(matrix); 
			startPoint.set(event.getX(), event.getY()); mode = DRAG;
			break; 
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event); 
			if (oldDist > 10f) { 
				savedMatrix.set(matrix);
				midPoint(midPoint, event);
				mode = ZOOM;
			} 
			break; 
		case MotionEvent.ACTION_UP: 
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE; break; 
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				matrix.set(savedMatrix);
				matrix.postTranslate(event.getX() - startPoint.x, event.getY() - startPoint.y);
			} else if (mode == ZOOM) { 
				float newDist = spacing(event);
				if (newDist > 10f) {
					matrix.set(savedMatrix); 
					float scale = newDist / oldDist; 
					matrix.postScale(scale, scale, midPoint.x, midPoint.y);
				} 
			}
			break; 
		} 
		view.setImageMatrix(matrix); 
		return true; 
	} 

	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y); 
	}
	private void midPoint(PointF point, MotionEvent event) {

		float x = event.getX(0) + event.getX(1); float y = event.getY(0) + event.getY(1); 
		point.set(x / 2, y / 2); }
	ImageView imageDetail; 
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix(); 
	PointF startPoint = new PointF();
	PointF midPoint = new PointF(); 
	float oldDist = 1f; 
	static final int NONE = 0; 
	static final int DRAG = 1; 
	static final int ZOOM = 2;
	int mode = NONE;

}
