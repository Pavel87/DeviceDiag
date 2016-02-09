package com.pacmac.devicediag;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by tqm837 on 9/22/2015.
 */
public class AsyncPingTask extends AsyncTask<String, Integer, String> {


    public InterfaceASTask asynResp = null;

    public AsyncPingTask() {
    }

    @Override
    protected void onPreExecute() {
        asynResp.startingPingCommand();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... ipAddress) {

        String result = null;

        try {
            Process process = Runtime.getRuntime().exec(
                    "/system/bin/ping -s 32 -c 4 " + ipAddress[0]);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            int i;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            while ((i = reader.read(buffer)) > 0) {
                output.append(buffer, 0, i);
                result = output.toString();
            }
            reader.close();

        } catch (IOException e) {
            return "IO Exception - Something went wrong";

        }
        return result;
    }


    @Override
    protected void onPostExecute(String result) {
        asynResp.showPingResponse(result);
        super.onPostExecute(result);
    }

    @Override
    protected void onCancelled() {
        asynResp.showPingResponse("PING error: 1");
        super.onCancelled();
    }

    @Override
    protected void onCancelled(String result) {
        asynResp.showPingResponse("Ping error: 2");
        super.onCancelled(result);
    }
}