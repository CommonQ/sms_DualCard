package edu.bupt.mms.numberlocate;

import android.os.Handler;
import android.util.Log;

public class MoveAnimation extends Thread {
    private Handler mHandler;

    public MoveAnimation(Handler handler) {
        mHandler = handler;
    }

    private static boolean RUNNING = false;
    private long time = 0;
    @Override
    public synchronized void start() {
        if (RUNNING) {
            Log.w("MoveAnimation","MoveAnimation alreay start");
            return;
        }
        RUNNING = true;
        time = System.currentTimeMillis();
        super.start();
    }

    @Override
    public void run() {
        while (RUNNING) {
            long deltaTime = System.currentTimeMillis() - time;
            if (deltaTime > 450) {
                Log.w("MoveAnimation","MoveAnimation time reached");
                mHandler.removeMessages(PhoneStatusRecevier.ANIMATION_PROCESS);
                RUNNING = false;
                break;
            }
            mHandler.sendEmptyMessageDelayed(PhoneStatusRecevier.ANIMATION_PROCESS, 40 + deltaTime*10/450);
        }
        RUNNING = false;
        mHandler.sendEmptyMessage(PhoneStatusRecevier.ANIMATION_FINISH);
        Log.w("MoveAnimation","MoveAnimation thread finish");
    }
}
