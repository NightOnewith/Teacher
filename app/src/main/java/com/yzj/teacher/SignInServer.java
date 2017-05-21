package com.yzj.teacher;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SignInServer {
    private Handler handler;
    private Context context;
    private ServerSocket serverSocket;
    private static final String JSON_STU_ID = "ID";
    private static final String JSON_STU_NAME = "NAME";

    public SignInServer(Handler handler, Context context) {
        this.handler = handler;
        this.context = context;
    }

    void startSignInServe() {
        new SignInSocketServe().start();

    }

    private class SignInSocketServe extends Thread {

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(12369);
                while (true) {
                    new ProcessSocket(serverSocket.accept()).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    ServerSocket getServerSocket() {
        return serverSocket;
    }

    private class ProcessSocket extends Thread {
        private Socket socket;

        ProcessSocket(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {

            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                while (true) {
                    int b = socket.getInputStream().read();
                    if (b == -1)
                        break;
                    byteArrayOutputStream.write(b);
                }
                JSONObject resultJson = new JSONObject(new String(byteArrayOutputStream.toByteArray(), "UTF-8"));
                Message msg = new Message();
                msg.what = 3;
                Bundle bundle = new Bundle();
                if (!StudentManager.getManager(context).isExist(resultJson.getString(JSON_STU_ID))) {
                    bundle.putString("ID", resultJson.getString(JSON_STU_ID));
                    bundle.putString("NAME", resultJson.getString(JSON_STU_NAME));
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                    JSONObject result = new JSONObject();
                    result.put("RESULT", true);
                    socket.getOutputStream().write(result.toString().getBytes("UTF-8"));
                    socket.getOutputStream().flush();
                    socket.getOutputStream().close();
                } else {
                    JSONObject result = new JSONObject();
                    result.put("RESULT", false);
                    result.put("EXTRA_INFO", "你已签到");
                    socket.getOutputStream().write(result.toString().getBytes("UTF-8"));
                    socket.getOutputStream().flush();
                    socket.getOutputStream().close();
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
