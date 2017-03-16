package com.example.sergei.asynctasktesting;

import android.icu.util.TimeUnit;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class MainActivity extends AppCompatActivity {


    final String LOG_TAG = "myLogs";

    MyTask mt;
    TextView tvStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus = (TextView)findViewById(R.id.status);


    }


    public void onClick(View view) {

        switch (view.getId()){
            case R.id.start:
                mt = new MyTask();
                mt.execute("1", "2", "3");
                break;
            case R.id.get:
                showResult();
                break;
            case R.id.cancel:
               cancelTask();
            default:
                break;
        }

    }

    private void cancelTask() {

        if(mt == null) return;
        Log.d(LOG_TAG, "cancel result: " + mt.cancel(true));

    }

    private void showResult(){
        if(mt == null) return;
        int result = -1;

        try {
            Log.d(LOG_TAG, "Try to get result");
            result = mt.get(1, java.util.concurrent.TimeUnit.SECONDS);
            Log.d(LOG_TAG, "get returns " + result);
            Toast.makeText(this, "get returns " + result, Toast.LENGTH_LONG).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            Log.d(LOG_TAG, "get timeout, result = " + result);
            e.printStackTrace();
        }

    }

    private class MyTask extends AsyncTask<String, Integer, Integer>{


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tvStatus.setText("Begin");
            Log.d(LOG_TAG, "Begin");

        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                int i = 0;
                for (String x : params){
                    Thread.sleep(1000);
                    publishProgress(++i);
                    Log.d(LOG_TAG, "isCancelled: " + isCancelled());

                    if(isCancelled()) {
                        return -1;
                    }
                }
                Thread.sleep(1000);
                return i;
            } catch (InterruptedException e) {
                Log.d(LOG_TAG, "Interrupted");
                e.printStackTrace();
            }

            return -1;
        }

        @Override
        protected void onCancelled() {
            tvStatus.setText("Cancel");
            Log.d(LOG_TAG, "Cancel");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            tvStatus.setText("Result " + values[0] + " files downloaded");
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            tvStatus.setText("End. Result = " + result);
            Log.d(LOG_TAG, "End. Result = " + result);
        }
    }
}
