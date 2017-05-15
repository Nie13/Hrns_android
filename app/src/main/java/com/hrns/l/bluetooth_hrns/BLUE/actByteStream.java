package com.hrns.l.bluetooth_hrns.BLUE;


import com.hrns.l.bluetooth_hrns2.bluetooth.BluetoothSppClient;
import com.hrns.l.bluetooth_hrns2.util.CHexConver;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;



public class actByteStream extends BaseCommActivity{
	private ImageButton mibtnSend = null;
	private AutoCompleteTextView mactvInput = null;
	private TextView mtvReceive = null;
	private ScrollView msvCtl = null;

	private GraphView graph;
	private LineGraphSeries<DataPoint> mSeries;
	private LineGraphSeries<DataPoint> mnSeries;
	private double graphLastXValue = 0.1d;
	private TextView mtvHr = null;
	private TextView mtvSr = null;
    private TextView mtvEn = null;

	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_byte_stream);
		
		this.mibtnSend = (ImageButton)this.findViewById(R.id.actByteStream_btn_send);
		this.mactvInput = (AutoCompleteTextView)this.findViewById(R.id.actByteStream_actv_input);
		this.mtvReceive = (TextView)this.findViewById(R.id.actByteStream_tv_receive);
		this.msvCtl = (ScrollView)this.findViewById(R.id.actByteStream_sv_Scroll);
		this.graph = (GraphView)this.findViewById(R.id.graph);
		this.mtvHr = (TextView)this.findViewById(R.id.actByteStream_tv_hr);
		this.mtvSr = (TextView)this.findViewById(R.id.actByteStream_tv_sr);
        this.mtvEn = (TextView)this.findViewById(R.id.actByteStream_tv_energy);

		this.initCtl(); 
		this.loadAutoComplateCmdHistory(this.getLocalClassName(), this.mactvInput);
		
		this.enabledBack();
		this.initIO_Mode(); 
		//this.usedDataCount(); 
		initGraph(graph);

		new receiveTask()
			.executeOnExecutor(FULL_TASK_EXECUTOR);
	}
	
	
	
	
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	
    	this.saveAutoComplateCmdHistory(this.getLocalClassName());
    }
    
	
	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
		this.mactvInput.setInputType(InputType.TYPE_NULL); //Close soft keyboard
	}
	
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        MenuItem miClear = menu.add(0, MEMU_CLEAR, 0, getString(R.string.menu_clear));
        miClear.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        MenuItem miIoMode = menu.add(0, MEMU_IO_MODE, 0, getString(R.string.menu_io_mode));
        miIoMode.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER); 
        MenuItem miSaveFile = menu.add(0, MEMU_SAVE_TO_FILE, 0, getString(R.string.menu_save_to_file));
        miSaveFile.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER); 
        MenuItem miClearHistory = menu.add(0, MEMU_CLEAR_CMD_HISTORY, 0, getString(R.string.menu_clear_cmd_history));
        miClearHistory.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER); 
        MenuItem miHelper = menu.add(0, MEMU_HELPER, 0, getString(R.string.menu_helper));
        miHelper.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER); 
        return super.onCreateOptionsMenu(menu);
    }
	
	
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {  
        switch(item.getItemId())  {
	        case android.R.id.home:
	            
	        	this.mbThreadStop = true; 
	        	this.setResult(Activity.RESULT_CANCELED); 
	        	this.finish();
	        	return true;
	        case MEMU_CLEAR: 
	        	this.mtvReceive.setText("");
	        	return true;
	        case MEMU_IO_MODE: 
	        	this.setIOModeDialog();
	        	return true;
	        case MEMU_SAVE_TO_FILE: 
	        	this.saveData2File();
	        	return true;
	        case MEMU_CLEAR_CMD_HISTORY: 
	        	this.clearAutoComplate(this.mactvInput);
	        	return true;
	        /*case MEMU_HELPER: //Display using the wizard
	        	if (this.getString(R.string.language).toString().equals("zh-rCN"))
	        		this.mtvReceive.setText(this.getStringFormRawFile(R.raw.byte_stream_cn) +"\n\n");
	        	else if (this.getString(R.string.language).toString().equals("zh-rTW"))
	        		this.mtvReceive.setText(this.getStringFormRawFile(R.raw.byte_stream_tw) +"\n\n");
	        	else
	        		this.mtvReceive.setText(this.getStringFormRawFile(R.raw.byte_stream_en) +"\n");
	        	return true;*/
	        default:
	        	return super.onMenuItemSelected(featureId, item);
        }
    }
	
	
	private void initCtl(){
		this.mibtnSend.setEnabled(false);
		//this.refreshRxdCount();
		//this.refreshTxdCount();
		
		
		this.mactvInput.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable arg0){
				if (arg0.length() > 0)
					mibtnSend.setEnabled(true);
				else
					mibtnSend.setEnabled(false);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3){
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3){
			}
			
		});
	}
	
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (KeyEvent.KEYCODE_BACK == keyCode){
        	this.mbThreadStop = true; 
        	this.setResult(Activity.RESULT_CANCELED, null);
        	this.finish();
        	return true;
        }
        else
        	return super.onKeyDown(keyCode, event);
    }
    
   
    private void autoScroll(){
		int iOffset = this.mtvReceive.getMeasuredHeight() - this.msvCtl.getHeight();     
        if (iOffset > 0)
        	this.msvCtl.scrollTo(0, iOffset);
    }

    private void saveData2File(){
    	if (this.mtvReceive.length() > 0)
    		this.save2SD(this.mtvReceive.getText().toString().trim());
    }

    public void onClickBtnSend(View c){
    	String sSend = this.mactvInput.getText().toString().trim();
    	if (BluetoothSppClient.IO_MODE_HEX == this.mbtOutputMode){	
    		if (!CHexConver.checkHexStr(sSend)){
    			Toast.makeText(this, 
				   getString(R.string.msg_not_hex_string),
				   Toast.LENGTH_SHORT).show();
    			return;
    		}
    	}
    	
    	this.mibtnSend.setEnabled(false);
//    	sSend += "\r\n"; 
    	if (this.mBSC.Send(sSend) >= 0){
    		//this.refreshTxdCount(); 
    		this.mibtnSend.setEnabled(true); 
    		this.addAutoComplateVal(sSend, this.mactvInput); 
    	}else{
			Toast.makeText(this, getString(R.string.msg_msg_bt_connect_lost), Toast.LENGTH_LONG).show();
			this.mactvInput.setEnabled(false); 
    	}
    }
    

    private class receiveTask extends AsyncTask<String, String, Integer>
    {
    	
    	private final static byte CONNECT_LOST = 0x01;
    	private final static byte THREAD_END = 0x02;
		
		@Override
		public void onPreExecute()
		{
			mtvReceive.setText(getString(R.string.msg_receive_data_wating));
			mbThreadStop = false;
		}
		

		@Override
		protected Integer doInBackground(String... arg0){
			mBSC.Receive();
			while(!mbThreadStop){
				if (!mBSC.isConnect())
					return (int)CONNECT_LOST; 
				
				if (mBSC.getReceiveBufLen() > 0){
					SystemClock.sleep(20);
					this.publishProgress(mBSC.Receive());
				}
			}
			return (int)THREAD_END;
		}
		

		@Override
		public void onProgressUpdate(String... progress){
			StringBuffer pg = new StringBuffer("");
			if (null != progress[0]){
				mtvReceive.append(progress[0]);
				autoScroll();
				//refreshRxdCount();
				String[] parts = progress[0].split("(:)|(;)");
				//mtvReceive.append("{}" + parts.length + "{}");
				try{
					if(parts.length == 6){
						String numb1 = parts[1];
						String numb2 = parts[3];
						String energy = parts[5];
						mtvHr.setText(numb1);
						mtvSr.setText(numb2);
						mtvEn.setText(energy);
						//numb = (parts[1] + parts[2]).toString();
						double newpoint = Double.parseDouble(numb1);
						//double neoPoint = Double.parseDouble(numb2);
						double neonPoint = Double.parseDouble(energy) / 60.0;
						graphLastXValue += 0.2d;
						mSeries.appendData(new DataPoint(graphLastXValue, newpoint), true, 310);
						mnSeries.appendData(new DataPoint(graphLastXValue, neonPoint), true, 310);
						//mtvReceive.append("{}" + numb1 + "{}");
					}
				}catch (Exception e){
					mtvReceive.append("EXCEPTION");
				}
			}
		}
		

		@Override
		public void onPostExecute(Integer result){
			if (CONNECT_LOST == result) 
				mtvReceive.append(getString(R.string.msg_msg_bt_connect_lost));
			else
				mtvReceive.append(getString(R.string.msg_receive_data_stop));
			mibtnSend.setEnabled(false); 
			//refreshHoldTime();
		}
    }

    public void initGraph(GraphView graph){
		graph.getViewport().setXAxisBoundsManual(true);
		graph.getViewport().setMinX(0);
		graph.getViewport().setMaxX(60);
		graph.getGridLabelRenderer().setLabelVerticalWidth(100);
		mSeries = new LineGraphSeries<>();
		mSeries.setDrawDataPoints(false);
		//mSeries.setDrawBackground(true);
		//mSeries.setBackgroundColor(Color.RED);
		mSeries.setColor(Color.RED);
		mnSeries = new LineGraphSeries<>();
		mnSeries.setDrawDataPoints(false);
		mnSeries.setDrawBackground(false);
		graph.addSeries(mnSeries);
		graph.addSeries(mSeries);
	}
}
