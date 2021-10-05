package com.sivtech.newyeargreetingcards;



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.FloatMath;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {

	float[] lastEvent = null;
	private static final int RESULT_LOAD_TEXT=1;
	float d = 0f;
	float newRot = 0f;
	private PointF start = new PointF();
	private PointF mid = new PointF();
	float oldDist = 1f;
	MotionEvent event;
	public static final int COLOR_MIN = 0x00;
	public static final int COLOR_MAX = 0xFF;
	Matrix matrix = new Matrix();
    Matrix savedMatrix= new Matrix();
	 PointF startPoint = new PointF();
	 PointF midPoint = new PointF();
	 //float oldDist = 1f;
	 static final int NONE = 0;
	 static final int DRAG = 1;
     static final int ZOOM =2;
     int mode = NONE;
	
	
	ImageView iv,textimage;
	int width,height;
	ImageButton pevious,share,next,save,text,apply;
	private int currentImage = 0;
    int[] images = {
    		R.drawable.n1,R.drawable.n2,R.drawable.n3,R.drawable.n4,R.drawable.n5,R.drawable.n6,R.drawable.n7,R.drawable.n8,R.drawable.n9,R.drawable.n10,
    		R.drawable.n11,R.drawable.n12,R.drawable.n13,R.drawable.n14,R.drawable.n15,R.drawable.n16,R.drawable.n17,R.drawable.n18,R.drawable.n19,R.drawable.n20,
    		R.drawable.n21,R.drawable.n22,R.drawable.n23,R.drawable.n24,R.drawable.n25,R.drawable.n26,R.drawable.n27,R.drawable.n28,R.drawable.n29,R.drawable.n30,
    		R.drawable.n31,R.drawable.n32,R.drawable.n33,R.drawable.n34,R.drawable.n35,R.drawable.n36,R.drawable.n37,R.drawable.n38,R.drawable.n39,R.drawable.n40,
    		R.drawable.n41,R.drawable.n42,R.drawable.n43,R.drawable.n44,R.drawable.n45,R.drawable.n46,R.drawable.n47,R.drawable.n48,R.drawable.n49,R.drawable.n50,
    		R.drawable.n51,R.drawable.n52,R.drawable.n53,R.drawable.n54,R.drawable.n55,R.drawable.n56,R.drawable.n57,R.drawable.n58,R.drawable.n59,R.drawable.n60,
    		R.drawable.n61,R.drawable.n62,R.drawable.n63,R.drawable.n64,R.drawable.n65,R.drawable.n66,R.drawable.n67,R.drawable.n68,R.drawable.n69,R.drawable.n70,
    		R.drawable.n71,R.drawable.n72,R.drawable.n73,R.drawable.n74,R.drawable.n75,R.drawable.n76,R.drawable.n77,R.drawable.n78,R.drawable.n79,R.drawable.n80,
    		R.drawable.n81,R.drawable.n82,R.drawable.n83,R.drawable.n84,R.drawable.n85,R.drawable.n86,R.drawable.n87,R.drawable.n88,R.drawable.n89,R.drawable.n90,
    		R.drawable.n91,R.drawable.n92,R.drawable.n93,R.drawable.n94,R.drawable.n95,R.drawable.n96,R.drawable.n97,R.drawable.n98,R.drawable.n99,R.drawable.n100,
    		
    		
    		
    }; 
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		iv=(ImageView)findViewById(R.id.imageView1);
		textimage=(ImageView)findViewById(R.id.textimage);
		text=(ImageButton)findViewById(R.id.text);
		apply=(ImageButton)findViewById(R.id.apply);
		//iv.setImageDrawable(getResources().getDrawable(R.drawable.saibaba));
		iv.setImageResource(images[currentImage]);
		pevious=(ImageButton)findViewById(R.id.pevious);
		save=(ImageButton)findViewById(R.id.save);
		share=(ImageButton)findViewById(R.id.share);
		next=(ImageButton)findViewById(R.id.next);
		pevious.setOnClickListener(peviousChangeImageListener);
		next.setOnClickListener(nextChangeImageListener);
	
	  save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				apply.setVisibility(View.INVISIBLE);
				String root = Environment.getExternalStorageDirectory().toString();
    			File myDir = new File(root + "/New year greetingcards");    
    		    myDir.mkdirs();
    	        Random generator = new Random();
    	        int n = 10000;
    	        n = generator.nextInt(n);
    	        String fname = "Image-"+ n +".jpg";
    	        File file = new File (myDir, fname);
    	        sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
    	        if (file.exists ()) file.delete (); 
    	        try {
    	        	
    	               FileOutputStream out = new FileOutputStream(file);
    	               //bpWidth=b2.getWidth();
    	     	      //bpHeight=b2.getHeight();
    	               Bitmap resultbitmap = Bitmap.createBitmap(iv.getWidth(),iv.getHeight(),Bitmap.Config.ARGB_8888);
    	               iv.setDrawingCacheEnabled(true);
    	               Bitmap p = Bitmap.createBitmap(iv.getDrawingCache());
    	               iv.setDrawingCacheEnabled(false);
    	               
    	               textimage.setDrawingCacheEnabled(true);
    	               Bitmap p1 = Bitmap.createBitmap(textimage.getDrawingCache());
    	               textimage.setDrawingCacheEnabled(false);
    	              
    	                Canvas canvas = new Canvas(resultbitmap);
    	               canvas.drawBitmap(p, 0, 0, null);
    	               canvas.drawBitmap(p1, 0, 0, null);
    	               resultbitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
    	              Toast.makeText(getApplicationContext(), "Your Image:"+fname+"is stored in New year greetingcards Folder in sd card", Toast.LENGTH_LONG).show();
    	               out.flush();
    	               out.close();
    	          } catch (Exception e) {
    	               e.printStackTrace();
    	        }
			}
		});
		share.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				apply.setVisibility(View.INVISIBLE);
				 Bitmap  resultbitmap = Bitmap.createBitmap(iv.getWidth(),iv.getHeight(),Bitmap.Config.ARGB_8888);
		         iv.setDrawingCacheEnabled(true);
		         Bitmap p = Bitmap.createBitmap(iv.getDrawingCache());
		         iv.setDrawingCacheEnabled(false);
		         
		         textimage.setDrawingCacheEnabled(true);
	               Bitmap p1 = Bitmap.createBitmap(textimage.getDrawingCache());
	               textimage.setDrawingCacheEnabled(false);
		         
		         Canvas canvas = new Canvas(resultbitmap);
		         canvas.drawBitmap(p, 0, 0, null);
		         canvas.drawBitmap(p1, 0, 0, null);
				Intent share = new Intent(Intent.ACTION_SEND);
				share.setType("image/jpeg");
				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				resultbitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
				File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
				try {
				    f.createNewFile();
				    FileOutputStream fo = new FileOutputStream(f);
				    fo.write(bytes.toByteArray());
				} catch (IOException e) {                       
				        e.printStackTrace();
				}
				 
				share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temporary_file.jpg"));
				//setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				startActivity(Intent.createChooser(share, "Share Image"));
				
				//Envinormental.getExternalStorageDirectory.getPath()
			
			}
		});
		text.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i=new Intent(getApplicationContext(), SecondActivity.class);
				startActivityForResult(i, RESULT_LOAD_TEXT);
				
			}
		});
		apply.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Bitmap resultbitmap = Bitmap.createBitmap(iv.getWidth(),iv.getHeight(),Bitmap.Config.ARGB_8888);
	               iv.setDrawingCacheEnabled(true);
	               Bitmap p = Bitmap.createBitmap(iv.getDrawingCache());
	               iv.setDrawingCacheEnabled(false);
	               
	               textimage.setDrawingCacheEnabled(true);
	               Bitmap p1 = Bitmap.createBitmap(textimage.getDrawingCache());
	               textimage.setDrawingCacheEnabled(false);
	              
	                Canvas canvas = new Canvas(resultbitmap);
	               canvas.drawBitmap(p, 0, 0, null);
	               canvas.drawBitmap(p1, 0, 0, null);
	               iv.setImageBitmap(resultbitmap);
	               textimage.setImageBitmap(null);
	               apply.setVisibility(View.INVISIBLE);
				
			}
		});
		
	}
	View.OnClickListener nextChangeImageListener = new OnClickListener() {

        public void onClick(View v) {
            //Increase Counter to move to next Image
        	apply.setVisibility(View.INVISIBLE);
            currentImage++;
            currentImage = currentImage % images.length;

            iv.setImageResource(images[currentImage]);

        }
    };
    View.OnClickListener peviousChangeImageListener = new OnClickListener() {

        public void onClick(View v) {
            //Increase Counter to move to next Image
        	apply.setVisibility(View.INVISIBLE);
            currentImage--;
            currentImage = (currentImage + images.length) % images.length;

            iv.setImageResource(images[currentImage]);

        }
    };
    
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK){
		switch (requestCode) {
		 
		case RESULT_LOAD_TEXT:
			apply.setVisibility(View.VISIBLE);
			Toast.makeText(getApplicationContext(), "click apply button to add one more text", Toast.LENGTH_LONG).show();
			//Toast.makeText(getApplicationContext(), "else block"+RESULT_OK + resultCode, Toast.LENGTH_LONG).show();
			textimage.setImageBitmap(CommonUtillities.bitmap);
		    
			 textimage.setOnTouchListener(new View.OnTouchListener() {
		  		public boolean onTouch(View v, MotionEvent event) {
		              ImageView view = (ImageView) v;
		            // Handle touch events here...
		              switch (event.getAction() & MotionEvent.ACTION_MASK) {
		              case MotionEvent.ACTION_DOWN:
		                  savedMatrix.set(matrix);
		                  start.set(event.getX(), event.getY());
		                  mode = DRAG;
		                  lastEvent = null;
		                  break;
		              case MotionEvent.ACTION_POINTER_DOWN:
		                  oldDist = spacing(event);
		                  if (oldDist > 10f) {
		                      savedMatrix.set(matrix);
		                      midPoint(mid, event);
		                      mode = ZOOM;
		                  }
		                  lastEvent = new float[4];
		                  lastEvent[0] = event.getX(0);
		                  lastEvent[1] = event.getX(1);
		                  lastEvent[2] = event.getY(0);
		                  lastEvent[3] = event.getY(1);
		                  d = rotation(event);
		                  break;
		              case MotionEvent.ACTION_UP:
		              case MotionEvent.ACTION_POINTER_UP:
		                  mode = NONE;
		                  lastEvent = null;
		                  break;
		              case MotionEvent.ACTION_MOVE:
		                  if (mode == DRAG) {
		                      // ...
		                      matrix.set(savedMatrix);
		                      matrix.postTranslate(event.getX() - start.x, event.getY()
		                              - start.y);
		                  } else if (mode == ZOOM && event.getPointerCount() == 2) {
		                      float newDist = spacing(event);
		                      matrix.set(savedMatrix);
		                      if (newDist > 10f) {
		                          float scale = newDist / oldDist;
		                          matrix.postScale(scale, scale, mid.x, mid.y);
		                      }
		                      if (lastEvent != null) {
		                          newRot = rotation(event);
		                          float r = newRot - d;
		                          matrix.postRotate(r, view.getMeasuredWidth() / 2,
		                                  view.getMeasuredHeight() / 2);
		                      }
		                  }
		                  break;
		              }

		              view.setImageMatrix(matrix);

		              return true;
		          
		  		 
		  	}private float rotation(MotionEvent event) {
		  	    double delta_x = (event.getX(0) - event.getX(1));
		  	    double delta_y = (event.getY(0) - event.getY(1));
		  	    double radians = Math.atan2(delta_y, delta_x);

		  	    return (float) Math.toDegrees(radians);
		  	}
		  	private float spacing(MotionEvent event) {
		  	        float x = event.getX(0) - event.getX(1);
		  	        float y = event.getY(0) - event.getY(1);
		  	        return FloatMath.sqrt(x * x + y * y);
		  	    }

		  	private void midPoint(PointF point, MotionEvent event) {
		  	        float x = event.getX(0) + event.getX(1);
		  	        float y = event.getY(0) + event.getY(1);
		  	        point.set(x / 2, y / 2);
		  	}
		});
			
		
		}
		}
 }	  
   

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
