package info.tangchen.slidebutton;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SlideButton extends View implements View.OnTouchListener {
    private Slide2TheEndListener listener;
    private float startX, startY;

    public SlideButton(Context context) {
        super(context);
        initButton(context);
    }

    public SlideButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initButton(context);
    }

    public SlideButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initButton(context);
    }

    public SlideButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initButton(context);
    }

    public void bindListener(Slide2TheEndListener listener) {
        this.listener = listener;
    }

    private void initButton(Context context) {
        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        float offsetX, offsetY;
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = motionEvent.getX();
                startY = motionEvent.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                offsetX = motionEvent.getX() - startX;
                offsetY = motionEvent.getY() - startY;

                if(offsetX > 0) {
                    offset = offsetX;
                    Message message = new Message();
                    message.obj = "refresh";
                    myHandler.sendMessage(message);
                }

                //new updateUI().run();
                break;
            case MotionEvent.ACTION_UP:
                offset = 0;

                Message message0 = new Message();
                message0.obj = "refresh";
                myHandler.sendMessage(message0);
                break;
        }
        return true;
    }

    class updateUI implements Runnable{

        @Override
        public void run() {
            Message message = new Message();
            message.obj = "refresh";
            myHandler.sendMessage(message);
        }
    }

    @SuppressLint("HandlerLeak")
    Handler myHandler = new Handler() {
        //接收到消息后处理
        public void handleMessage(Message msg) {
            switch ((String) msg.obj) {
                case "refresh":
                    invalidate();//刷新界面
                    break;
            }
            super.handleMessage(msg);
        }
    };

    float offset = 0.0f;
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        RectF background = new RectF(50, 20, getWidth() - 50, getHeight() - 20);
        canvas.drawRoundRect(background, 0xfff, 0xfff, paint);
        paint.setColor(Color.WHITE);
        float r = (getHeight() - 40) / 2;
        if(offset >= getWidth() - 200)
            offset = getWidth() - 200;
        canvas.drawCircle(r + 50 + offset, r + 20, r, paint);
    }
}