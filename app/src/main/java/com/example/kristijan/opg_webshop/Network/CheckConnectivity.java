package com.example.kristijan.opg_webshop.Network;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CheckConnectivity {
    Context context;

    public CheckConnectivity(Context context) {
        this.context = context;
    }

    public void checkNetworkConnection(){

        AlertDialog.Builder builder =new AlertDialog.Builder(context);
        builder.setTitle("No internet Connection");
        builder.setMessage("Please turn on internet connection to continue");
        builder.setNegativeButton("Reload", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                isNetworkConnectionAvailable();

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public boolean isNetworkConnectionAvailable(){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        if(isConnected) {
            Log.d("Network", "Connected");
            return true;
        }
        else{

            checkNetworkConnection();
            Log.d("Network","Not Connected");

            return false;
        }
    }


    public static class TestInternet extends AsyncTask<Void, Void, Boolean> {

        Context context;

        public TestInternet(Context context) {
            this.context=context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                URL url = new URL("http://www.google.com");
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setConnectTimeout(3000);
                urlc.connect();
                if (urlc.getResponseCode() == 200) {
                    return true;
                }
            } catch (MalformedURLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return false;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) { // code if not connected
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("An internet connection is required.");
                builder.setCancelable(false);

                builder.setPositiveButton(
                        "TRY AGAIN",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                new TestInternet(context).execute();
                            }
                        });


                AlertDialog alert11 = builder.create();
                alert11.show();
            } else { // code if connected
            }
        }
    }
}
