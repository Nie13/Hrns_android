package com.hrns.l.bluetooth_hrns.BLUE;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hrns.l.bluetooth_hrns.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;

/**
 * Created by l on 5/3/2017.
 */

public class actRealTime extends BaseCommActivity{
    private final Handler mHandler = new Handler();
    private Runnable mTimer;
    private double graphLastXValue = 5d;
    private LineGraphSeries<DataPoint> mSeries;
    private GraphView graph;
    private ScrollView msvCtl = null;
    private TextView mtvReceive = null;


    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_realtime);
        this.graph = (GraphView)this.findViewById(R.id.graph);
        this.mtvReceive = (TextView)this.findViewById(R.id.act_realtime_tv_receive);
        this.msvCtl = (ScrollView)this.findViewById(R.id.act_realtime_sv_scroll);
        this.enabledBack();
        this.initIO_Mode();
        //GraphView graph = (GraphView) activity.findViewById(R.id.graph);
        initGraph(graph);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        this.saveAutoComplateCmdHistory(this.getLocalClassName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        MenuItem miClear = menu.add(0, MENU_CLEAR, 0,getString(R.string.menu_clear));
        miClear.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        MenuItem miIoMode = menu.add(0, MENU_IO_MODE, 0, getString(R.string.menu_io_mode));
        miIoMode.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        MenuItem miSaveFile = menu.add(0, MENU_SAVE_TO_FILE, 0, getString(R.string.menu_save_to_file));
        miSaveFile.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        MenuItem miClearHistory = menu.add(0, MENU_CLEAR_CMD_HISTORY, 0, getString(R.string.menu_clear_history));
        miClearHistory.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                this.mbThreadStop = true;
                this.setResult(Activity.RESULT_CANCELED);
                this.finish();
                return true;
            case MENU_CLEAR:
                this.mtvReceive.setText("");
                return true;
            case MENU_IO_MODE:
                this.setIOModeDialog();
                return true;
            case MENU_SAVE_TO_FILE:
                this.saveData2File();
                return true;
            default:
                return super.onMenuItemSelected(featureId, item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(KeyEvent.KEYCODE_BACK == keyCode){
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
        if(iOffset > 0)
            this.msvCtl.scrollTo(0, iOffset);
    }

    private void saveData2File(){
        if(this.mtvReceive.length() > 0)
            this.save2SD(this.mtvReceive.getText().toString().trim());
    }

    

    public void initGraph(GraphView graph){
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMinX(8);

        graph.getGridLabelRenderer().setLabelVerticalWidth(100);

        mSeries = new LineGraphSeries<>();
        mSeries.setDrawDataPoints(true);
        mSeries.setDrawBackground(true);
        graph.addSeries(mSeries);
    }

    public void onResume(){
        mTimer = new Runnable() {
            @Override
            public void run() {
                graphLastXValue += 0.25d;
                mSeries.appendData(new DataPoint(graphLastXValue, getRandom()), true, 22);
                mHandler.postDelayed(this, 330);
            }
        };
        mHandler.postDelayed(mTimer, 1500);
    }

    public void onPause(){
        mHandler.removeCallbacks(mTimer);
    }

    double mLastRandom = 2;
    Random mRand = new Random();

    private double getRandom(){
        return mLastRandom += mRand.nextDouble() * 0.5 -0.25;
    }
}
