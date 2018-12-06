package com.port.api.interactive;

import com.port.api.R;
import com.port.api.network.bt.BluetoothConnectionService;
import com.port.api.util.Constant;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;

public class iMouse extends Service{

//	OverlayView mView;
	private String TAG = "iMouse";
//	
//	public iMouse(Context c) {
//		
//        mView = new OverlayView(c);
//        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,//TYPE_SYSTEM_ALERT,//TYPE_SYSTEM_OVERLAY,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
//                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, //will cover status bar as well!!!
//                PixelFormat.TRANSLUCENT);
//        params.gravity = Gravity.LEFT | Gravity.TOP;
//        params.setTitle("Cursor");
//        WindowManager wm = (WindowManager) c.getSystemService(c.WINDOW_SERVICE);
//        wm.addView(mView, params);
//	}
//	
//    public void onDestroy(Context c) {
//        Log.d("CursorService", "Service destroyed");
//        if(mView != null) {
//            ((WindowManager) c.getSystemService(c.WINDOW_SERVICE)).removeView(mView);
//            mView = null;
//        }
//    }	
//	
    public int coordinates(String axis,float value, Context c){
		// get screen size
		int width = 0;
		int height = 0;
        DisplayMetrics metrics = new DisplayMetrics();
		try {
			WindowManager winMgr = (WindowManager)c.getSystemService(Context.WINDOW_SERVICE) ;
	       	winMgr.getDefaultDisplay().getMetrics(metrics);
	       	width = winMgr.getDefaultDisplay().getWidth();
	       	height = winMgr.getDefaultDisplay().getHeight();
	       	if(Constant.DEBUG) Log.d(TAG ,"TV Resolution width "+width +", hieght "+height);
		}
		catch (Exception e) { 
			e.printStackTrace();
		} 
		
		if(axis.equalsIgnoreCase("x")) {
			value = value * width;
		}else if(axis.equalsIgnoreCase("y")) {
			value = value * height;
		}
		if(Constant.DEBUG)  Log.d(TAG ,"coordinate().value "+value);
		return (int)value;
	}
	
    public void showCursor(final int ix, final int iy){
		if(Constant.DEBUG)  Log.d(TAG ,"showCursor: ix: "+ix+", iy: "+iy);
		ShowMouse(true);
		new Thread(new Runnable() {         
            @Override
            public void run() {   
            	try {
					Thread.sleep(300);
					Update(ix, iy, true);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            	
            }   
        }).start();
	}
//    
//    public void Update(final int x, final int y, final boolean autoenable) {
//		if(Constant.DEBUG)  Log.d(TAG ,"Update: x: "+x+", y: "+y);
//    	mView.Update(x,y);
//    	if ((x!=0 || y!= 0) && autoenable && !mView.isCursorShown() ) 
//    		ShowMouse(true); //will also post invalidate
//    	else
//    		mView.postInvalidate();
//    }
//    
//    public void ShowMouse(boolean status) {
//    	mView.ShowCursor(status);
//    	mView.postInvalidate();
//    }
    
	OverlayView mView;

    /**
     * @param x
     * @param y
     * @param autoenable if set, it will automatically show the cursor when movement is detected
     */
    public void Update(final int x, final int y, final boolean autoenable) {
    	mView.Update(x,y);
    	if ((x!=0 || y!= 0) && autoenable && !mView.isCursorShown() ) 
    		ShowMouse(true); //will also post invalidate
    	else
    		mView.postInvalidate();
    }
    public void ShowMouse(boolean status) {
    	mView.ShowCursor(status);
    	mView.postInvalidate();
    }
    
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
    
    public final IBinder mMouse = new mBinder();

	public class mBinder extends Binder {
		public iMouse getService() {
			return iMouse.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if(Constant.DEBUG)  Log.d(TAG, "Service created");
		
        mView = new OverlayView(this);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,//TYPE_SYSTEM_ALERT,//TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, //will cover status bar as well!!!
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.setTitle("Cursor");
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.addView(mView, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(Constant.DEBUG)  Log.d(TAG, "Service destroyed");
        if(mView != null) {
            ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(mView);
            mView = null;
        }
    }

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mMouse;
	}

}

class OverlayView extends ViewGroup{

	private Paint mLoadPaint;
    boolean mShowCursor;
    Bitmap	cursor;
    public int x = 0,y = 0;
	
	public OverlayView(Context context) {
		super(context);
		cursor = BitmapFactory.decodeResource(context.getResources(),R.drawable.cursor);
		mLoadPaint = new Paint();
		mLoadPaint.setAntiAlias(true);
		mLoadPaint.setTextSize(10);
		mLoadPaint.setARGB(255, 255, 0, 0);
	}
	
	@Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.drawText("Hello World", 0, 0, mLoadPaint);
        if (mShowCursor) canvas.drawBitmap(cursor,x,y,null);
    }
	
	public void Update(int nx, int ny) {
    	x = nx; y = ny;
    }
    public void ShowCursor(boolean status) {
    	mShowCursor = status;
	}
    public boolean isCursorShown() {
    	return mShowCursor;
    }

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
	}
	
}