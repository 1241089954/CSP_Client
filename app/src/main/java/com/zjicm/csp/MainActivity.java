package com.zjicm.csp;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.zjicm.csp.PlayerAction.ACTION;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class MainActivity extends AppCompatActivity implements TableAdapter.onPlayerClickListener {
    private final static String HOST = "115.198.213.105";
    private final static int PORT = 12345;

    private TextView mOnline;
    private RecyclerView mTable;
    private TableAdapter mAdapter;
    public static PlayerAction action;
    public static OnSendCSPListener listener;
    private TextView mScore;
    private int score = 0;
    private Socket mSocket = null;
    private ObjectInputStream mObjectInputStream = null;
    private ObjectOutputStream mObjectOutputStream = null;

    private long time = System.currentTimeMillis();
    private Result<ArrayList<Table>> result;

    //定义一个handler对象,用来刷新界面
    public Handler handler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                Result<ArrayList<Table>> result = (Result<ArrayList<Table>>) msg.obj;
                Log.d("WYL", "接受到的消息类型:" + result.getResultType());
                if (result.getResultType() == Result.TYPE.TABLE_INFO) {
                    mAdapter = new TableAdapter(result.getValue(), MainActivity.this);
                    mAdapter.setOnPlayerClickListener(MainActivity.this);
                    mTable.setAdapter(mAdapter);

                    if (!result.getMessage().equals("")) {
                        Toast.makeText(MainActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    mOnline.setText(result.getOnNumber());
                } else if (result.getResultType() == Result.TYPE.TABLE_UPDATE) {
                    Table table = result.getValue().get(0);
                    mAdapter.update(table);

                    if (!result.getMessage().equals("")) {
                        Toast.makeText(MainActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    mOnline.setText(result.getOnNumber());

                } else if (result.getResultType() == Result.TYPE.Game_START) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startGame();
                } else if (result.getResultType() == Result.TYPE.CSP_INFO) {
                    Log.d("WYL", "接受到对手的消息:" + result.getMessage());
                }
            } else if (msg.what == 1) {
                if (((String) msg.obj).equals("恭喜你,你赢了!")) {
                    mScore.setText(score++ + "");
                }
                Toast.makeText(MainActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化视图
        initView();

        //创建Socket,并建立连接
        getConnection();
    }

    private void initView() {
        TextView mUsername = (TextView) findViewById(R.id.username);
        mOnline = (TextView) findViewById(R.id.online);
        //定义相关变量,完成初始化
        mScore = (TextView) findViewById(R.id.score);
        mTable = (RecyclerView) findViewById(R.id.table);

        StaggeredGridLayoutManager sglm = new StaggeredGridLayoutManager(1, 1);
        mTable.setLayoutManager(sglm);

        mUsername.setText(IPGetUtil.getIPAddress(this));
        mScore.setText("得分:" + score);
        Log.d("WYL", "本机IP地址:" + IPGetUtil.getIPAddress(MainActivity.this));
    }

    private void getConnection() {
        //创建线程，与服务端建立连接
        new Thread() {
            public void run() {
                try {
                    //创建套接字
                    mSocket = new Socket(HOST, PORT);
                    //创建对象输入流
                    mObjectInputStream = new ObjectInputStream(mSocket.getInputStream());
                    //创建对象输出流
                    mObjectOutputStream = new ObjectOutputStream(mSocket.getOutputStream());
                    Log.d("WYL", "建立连接成功");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

        //创建线程，实时获取服务端返回的消息
        new Thread() {
            public void run() {
                try {
                    while (true) {
                        if (mSocket == null || mObjectInputStream == null)
                            continue;
                        if (!mSocket.isClosed() && mSocket.isConnected() && !mSocket.isInputShutdown()) {
                            if ((result = (Result<ArrayList<Table>>) mObjectInputStream.readObject()) != null) {
                                Message message = new Message();
                                message.what = 0;
                                message.obj = result;
                                handler.sendMessage(message);
                                Log.d("WYL", "接受消息成功");
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        if (!mSocket.isClosed() && mSocket.isConnected() && !mSocket.isInputShutdown()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mObjectOutputStream.writeObject("bye");
                        mObjectOutputStream.writeObject(null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - time > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                time = System.currentTimeMillis();
            } else {
                finish();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onPlayer1Click(final int position, boolean isPositionAvailable) {
        if (isPositionAvailable) {
            Toast.makeText(this, "该座位已有玩家", Toast.LENGTH_SHORT).show();
        } else {
            final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
            View view = getLayoutInflater().inflate(R.layout.dialog_tip, null, false);
            dialog.setView(view);
            dialog.setCancelable(false);
            Window window = dialog.getWindow();
            window.setBackgroundDrawableResource(R.color.transparent);
            dialog.show();

            view.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSocket == null) {
                        return;
                    }
                    if (mSocket.isConnected() && !mSocket.isOutputShutdown()) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    action = new PlayerAction(ACTION.InOrOut, true, position, 1);
                                    mObjectOutputStream.writeObject(action);
                                    mObjectOutputStream.writeObject(null);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                    dialog.dismiss();
                }
            });

            view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
    }

    @Override
    public void onPlayer2Click(final int position, boolean isPositionAvailable) {
        if (isPositionAvailable) {
            Toast.makeText(this, "该座位已有玩家", Toast.LENGTH_SHORT).show();
        } else {
            final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
            View view = getLayoutInflater().inflate(R.layout.dialog_tip, null, false);
            dialog.setView(view);
            dialog.setCancelable(false);
            Window window = dialog.getWindow();
            window.setBackgroundDrawableResource(R.color.transparent);
            dialog.show();

            view.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSocket == null) {
                        return;
                    }
                    if (mSocket.isConnected() && !mSocket.isOutputShutdown()) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    action = new PlayerAction(ACTION.InOrOut, true, position, 2);
                                    mObjectOutputStream.writeObject(action);
                                    mObjectOutputStream.writeObject(null);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                    dialog.dismiss();
                }
            });

            view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
    }

    private void startGame() {
        Log.d("WYL", "开始游戏");
        GameDialog dialog = new GameDialog(this, R.style.style_game, mObjectOutputStream, handler);
        dialog.show();
    }

    public void setOnSendCSPListener(OnSendCSPListener ls) {
        listener = ls;
    }


    interface OnSendCSPListener {
        void onSendCSP(int csp);
    }
}
