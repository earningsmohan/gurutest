package com.sivtech.newyeargreetingcards;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceView;

public class CounterClockView extends SurfaceView {

	Paint paint = new Paint();
	Paint paint2 = new Paint();

	final RectF rect = new RectF();
	final RectF rect2 = new RectF();

	int counterArcAngle = 15;
	//constructor
	public CounterClockView(Context context, AttributeSet attributeSet) {
	    super(context);

	    //setting some paint properties...

	    this.setBackgroundColor(Color.TRANSPARENT);

	}

	@Override
	public void onDraw(Canvas canvas) {

	    rect.set(50, 50, 150, 150);
	    rect2.set(50, 50, 150, 150);

	    this.layout(0, 0, 200, 200);

	    canvas.drawArc(rect, -90, 360, false, paint);
	    canvas.drawArc(rect2, -90, counterArcAngle, false, paint2);

	}
}