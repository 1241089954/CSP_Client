package com.zjicm.csp;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ObjectOutputStream;
import java.util.Random;

public class GameDialog extends Dialog implements View.OnClickListener {

    private Button mStart;
    private Button mEnd;
    private TextView mPlayer1;
    private TextView mPlayer2;
    private ObjectOutputStream stream;
    private boolean isPlayer1Roll = false;
    private boolean isPlayer2Roll = false;
    private static boolean isPlayer2OK = false;
    private String[] strings = new String[]{
            "剪刀",
            "石头",
            "布"
    };

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0){
                mPlayer1.setText((String)msg.obj);
            }else {
                mPlayer2.setText((String)msg.obj);
            }
        }
    };

    private int p1;
    private int p2;

    private Handler handler;


    public GameDialog(Context context, int style, ObjectOutputStream stream,Handler handler) {
        super(context,style);
        this.stream = stream;
        this.handler = handler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setGravity(Gravity.CENTER);//设置dialog显示居中
        setContentView(R.layout.dialog_game);
//        MainActivity.listener = this;
        initView();
    }

    private void initView() {
        mStart = (Button) findViewById(R.id.start);
        mEnd = (Button) findViewById(R.id.end);
        mPlayer1 = (TextView) findViewById(R.id.player1);
        mPlayer2 = (TextView) findViewById(R.id.player2);

        mStart.setOnClickListener(this);
        mEnd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                if (!isPlayer1Roll && !isPlayer2Roll) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            isPlayer2Roll = true;
                            Random random = new Random();
                            p1 = random.nextInt(100);
                            for (int i = 0; i < p1; i++) {
//                            Log.d("WYL", "当前是:" + i);
                                Message message00 = new Message();
                                message00.what = 1;
                                message00.obj = strings[i % 3];
                                mHandler.sendMessage(message00);
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            p1 = p1 % 3;
                            try {
                                stream.writeObject(new PlayerAction(PlayerAction.ACTION.CSP, p1));
                                Message message00 = new Message();
                                message00.what = 1;
                                message00.obj = strings[p1];
                                mHandler.sendMessage(message00);
                                Thread.sleep(1000);
                                p2 = p1;
                                isPlayer2Roll = false;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
//                            Log.d("WYL", "线程结束1");
                        }
                    }).start();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            isPlayer1Roll = true;
                            while (isPlayer2Roll) {
                                try {
                                    Random random = new Random();
                                    Message message10 = new Message();
                                    message10.what = 0;
                                    message10.obj = strings[random.nextInt(3) % 3];
                                    mHandler.sendMessage(message10);
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            Message message11 = new Message();
                            message11.what = 0;
                            message11.obj = strings[(p2)];
                            mHandler.sendMessage(message11);
                            judgement(p1, p2);
                            isPlayer1Roll = false;
//                            Log.d("WYL", "线程结束2");
                        }
                    }).start();
                }
                break;

            case R.id.end:
                final PlayerAction a = new PlayerAction(PlayerAction.ACTION.InOrOut,
                        false, MainActivity.action.getPosition(), MainActivity.action.getSeat());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            stream.writeObject(a);
                            stream.writeObject(null);
                            dis();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.d("WYL", "线程结束");
                    }
                }).start();
                break;

            default:
                break;
        }
    }

    private void dis() {
        this.dismiss();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

//    @Override
//    public void onSendCSP(int csp) {
//        isPlayer2OK = true;
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        mPlayer1.setText(strings[csp % 3]);
//    }

    private void judgement(int csp1, int csp2) {
        String res = "";
        Log.d("WYL", "csp1 = " + csp1 + "  csp2 = " + csp2);
        if (csp1 == csp2) {
            res = "平局";
        } else {
            if (csp1 == 1 && csp2 == 2) {
                res = "很遗憾,你输了!";
            } else if (csp1 == 1 && csp2 == 3) {
                res = "恭喜你,你赢了!";
            } else if (csp1 == 2 && csp2 == 3) {
                res = "很遗憾,你输了!";
            } else if (csp1 == 2 && csp2 == 1) {
                res = "恭喜你,你赢了!";
            } else if (csp1 == 3 && csp2 == 1) {
                res = "很遗憾,你输了!";
            } else {
                res = "恭喜你,你输了!";
            }
        }
        Message message = new Message();
        message.what = 1;
        message.obj = res;
        handler.sendMessage(message);
    }
}
