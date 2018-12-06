package com.port.apps.epg;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.port.api.util.Constant;
import com.port.api.util.SystemLog;
import com.port.api.widget.TwoWayView;
import com.port.api.widget.TwoWayView.ChoiceMode;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HomeFragment extends Fragment{

	private String TAG = "HomeFragment";
	private View view;
	public TwoWayView rListView;
	OnSelectedListener mListener;
	
	public int rpos=0;
	public String direction = "";
	RelativeLayout currentRecommendedlayout;
	
	private ArrayList<HashMap<String,String>> recommendList = new ArrayList<HashMap<String,String>>();

	private ProgressDialog progressDialog;
	private boolean runningtask;
	
	public interface OnSelectedListener {
        public void onRecommendation(int id);
        public void goFeature(int id);
    }
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnSelectedListener");
        }
    }
	
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        new HomeData().execute();
        runningtask = true;
    }

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		if(Constant.DEBUG)  Log.d(TAG ,"onCreateView()");
		
		view = inflater.inflate(R.layout.homefragment, container, false);
		getHomeLayout();
        return view;
    }
	
	private void showHomeFragment(){
        
        rListView.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_DOWN){
					switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_DOWN:
						if(Constant.DEBUG)  Log.d(TAG,"rListView KEYCODE_DPAD_DOWN "+keyCode);
						return true;
						
					case KeyEvent.KEYCODE_DPAD_UP:
						if(Constant.DEBUG)  Log.d(TAG,"rListView KEYCODE_DPAD_UP "+keyCode);
						rListView.getChildAt(rpos-rListView.getFirstVisiblePosition()).setBackgroundColor(getResources().getColor(R.color.black));
						rListView.setFocusable(false);
						mListener.goFeature(rpos);
						return true;
						
					case KeyEvent.KEYCODE_DPAD_LEFT:
						if(Constant.DEBUG)  Log.d(TAG,"rListView KEYCODE_DPAD_LEFT "+keyCode);
						if(Constant.DEBUG)  Log.d(TAG,"Before decrementing rpos "+rpos);
						direction = "left";
						if(rpos >= 0){
							if(rpos > rListView.getFirstVisiblePosition()){
								rListView.getChildAt(rpos-rListView.getFirstVisiblePosition()).setBackgroundColor(getResources().getColor(R.color.black));
								if(Constant.DEBUG)  Log.d(TAG,"first loop, rpos " +rpos + " rListView first visible position " + rListView.getFirstVisiblePosition());
								rpos = rpos-1;
								rListView.getChildAt(rpos-rListView.getFirstVisiblePosition()).setBackgroundColor(getResources().getColor(R.color.pink));
							}else{
								if(Constant.DEBUG)  Log.d(TAG,"second loop, rpos " +rpos + " rListView first visible position " + rListView.getFirstVisiblePosition());
								if(rpos>0){
									rListView.getChildAt(rpos-rListView.getFirstVisiblePosition()).setBackgroundColor(getResources().getColor(R.color.black));
									rListView.setSelection(rpos-1);
									if(Constant.DEBUG)  Log.d(TAG, "After decrementing rpos, setting selection : "+rpos);
								}
							}
							return true;
						}
						return false;
						
					case KeyEvent.KEYCODE_DPAD_RIGHT:
						if(Constant.DEBUG)  Log.d(TAG,"rListView KEYCODE_DPAD_RIGHT "+keyCode);
						if(Constant.DEBUG)  Log.d(TAG,"Before incrementing rpos "+rpos);
						direction = "right";
						if(rpos < recommendList.size()){
							if(rpos < rListView.getLastVisiblePosition()){
								rListView.getChildAt(rpos-rListView.getFirstVisiblePosition()).setBackgroundColor(getResources().getColor(R.color.black));
								if(Constant.DEBUG)  Log.d(TAG, "first loop, rListView.getLastVisiblePosition(): "+rListView.getLastVisiblePosition());
								if(Constant.DEBUG)  Log.d(TAG, "first loop, rListView.getFirstVisiblePosition(): "+rListView.getFirstVisiblePosition());
								rpos=rpos+1;
								rListView.getChildAt(rpos-rListView.getFirstVisiblePosition()).setBackgroundColor(getResources().getColor(R.color.pink));
								if(Constant.DEBUG)  Log.d(TAG, "After incrementing rpos on regular scroll : "+rpos);
							}else{
								if(Constant.DEBUG)  Log.d(TAG, "second loop, rListView.getLastVisiblePosition(): "+rListView.getLastVisiblePosition());
								if(Constant.DEBUG)  Log.d(TAG, "second loop, rListView.getFirstVisiblePosition(): "+rListView.getFirstVisiblePosition());
								if(rpos+1 < recommendList.size()){
									rListView.getChildAt(rpos-rListView.getFirstVisiblePosition()).setBackgroundColor(getResources().getColor(R.color.black));
									rListView.setSelection(rListView.getFirstVisiblePosition()+1);
									if(Constant.DEBUG)  Log.d(TAG, "After incrementing rpos on set selection : "+rpos);
								}
							}
							return true;
						}
						
						if(Constant.DEBUG)  Log.d(TAG, "After incrementing rpos false: "+rpos);
						return false;
						
					case KeyEvent.KEYCODE_ENTER :
						if(Constant.DEBUG)  Log.d(TAG,"rListView KEYCODE_ENTER "+rListView.getSelectedItemPosition());
						String Id="";
						HashMap<String, String> map = recommendList.get(rpos);
						if(map.containsKey("imageId")){
							Id= map.get("imageId");
						}
						if(Constant.DEBUG)  Log.d(TAG,"rListView KEYCODE_ENTER  .Id: "+Id+", rpos: "+rpos);
						mListener.onRecommendation(Integer.parseInt(Id));
						return true;

					default:
						if(Constant.DEBUG)  Log.d(TAG,"rListView Key Code "+keyCode);
	                return true;
					}
				}
				return false;
			}
		});
        
	}
	
	@Override
	public void onResume(){
		super.onResume();

	}
	
	@Override
	public void onPause(){
		super.onPause();
	}
	
	@Override
    public void onDetach()
    {
        super.onDetach();
        view = null;
    }
	
	public class HomeData extends AsyncTask<Void, Void, Void>{
		
		@Override
		protected void onPreExecute() {

		}
		
		@Override
		protected Void doInBackground(Void... params) {
			getHomeData();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void v) {
			runningtask = false; 

	        if(getActivity()!=null){
	              getHomeLayout();
	        }
		}		
	}
	
	private void getHomeData(){
		
		recommendList = Catalogue.featuredImageList;
		
	}
	
	private void getHomeLayout(){
		try{
			if(runningtask){
				progressDialog = new ProgressDialog(getActivity(),R.style.MyTheme);
				progressDialog.setCancelable(true);
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
				progressDialog.show();
			}else{
				if(progressDialog != null && progressDialog.isShowing()){
					progressDialog.dismiss();
				}
		        direction = "";
				rListView = (TwoWayView) view.findViewById(R.id.rList);  
				rListView.setAdapter(new RecommendedAdapter(recommendList));
			    rListView.setChoiceMode(ChoiceMode.SINGLE);
			    rListView.setFocusable(false);
			    rListView.setFocusableInTouchMode(true);
			    rListView.requestFocus();
			    showHomeFragment();
			}
		}catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
	}
	
	
	public class RecommendedAdapter extends BaseAdapter{
		ArrayList<HashMap<String,String>> Data = new ArrayList<HashMap<String,String>>();
		
		public RecommendedAdapter(ArrayList<HashMap<String,String>> data){
			Data = data;
		}

		@Override
		public int getCount() {
			return Data.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)throws IndexOutOfBoundsException {
			if(Constant.DEBUG)  Log.d(TAG,"RecommendedAdapter Pos "+position);
				View v = null;
				if (convertView == null) {
		            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		            v = inflater.inflate(R.layout.recommendview, parent, false);
			    }else{
		        	v=convertView;
		        }
				if(Data!=null && Data.size() > 0){
					currentRecommendedlayout = (RelativeLayout) v.findViewById(R.id.rlayout);
					TextView textView = (TextView) v.findViewById(R.id.textView1); 
					if(Data.get(position).get("name").equalsIgnoreCase("")||Data.get(position).get("name") != null)
						textView.setText(Data.get(position).get("name"));
					
					ImageView imageView = (ImageView) v.findViewById(R.id.imageView1); 
					if(Data.get(position).get("image").equalsIgnoreCase("")||Data.get(position).get("image") != null){
						String tvImage = Data.get(position).get("image");
						Bitmap b = com.port.api.util.CommonUtil.StringToBitMap(tvImage, 0);
						if(b!=null){
							imageView.setImageBitmap(b);
						}
					}
					
				}
			
				if(rListView.isFocusable()){
					if(direction.equalsIgnoreCase("left")){
						rpos = position;
					}else if(direction.equalsIgnoreCase("right")){
						rpos = rpos+1;
					}
					
					if(rListView.getFirstVisiblePosition() == (rpos-position)+rListView.getFirstVisiblePosition()){
						currentRecommendedlayout.setBackgroundColor(getResources().getColor(R.color.pink));	
					}else{
						currentRecommendedlayout.setBackgroundColor(getResources().getColor(R.color.black));
					}
					if(Constant.DEBUG)  Log.d(TAG, "if getView rpos: "+rpos+", LastVisiblePosition: "+rListView.getLastVisiblePosition());
				}
				return v;
		}
	}
	
	
}
