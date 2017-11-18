package info.tangchen.slidebutton;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
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
    private float startX;
    private int backgroundColor, circleColor;
    private String textContent;

    public SlideButton(Context context) {
        super(context);
    }

    public SlideButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initButton(context, attrs);
    }

    public SlideButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initButton(context, attrs);
    }

    public SlideButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initButton(context, attrs);
    }

    public void bindListener(Slide2TheEndListener listener) {
        this.listener = listener;
    }

    private void initButton(Context context, AttributeSet attrs) {
        setOnTouchListener(this);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SlideButton);
        backgroundColor = typedArray.getColor(R.styleable.SlideButton_bg, Color.GRAY);
        circleColor = typedArray.getColor(R.styleable.SlideButton_circleColor, Color.WHITE);
        textContent = typedArray.getString(R.styleable.SlideButton_text);
        if(textContent == null)
            textContent = "";
        typedArray.recycle();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        float offsetX;
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = motionEvent.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                offsetX = motionEvent.getX() - startX;
                if(offsetX > 0) {
                    offset = offsetX;
                    refreshUI();
                }
                break;
            case MotionEvent.ACTION_UP:
                if(offset + 25 >= maxOffsetX)
                    listener.changeState();

                offset = 0;

                refreshUI();
                break;
        }
        return true;
    }

    private void refreshUI() {
        Message message = new Message();
        message.obj = "refresh";
        myHandler.sendMessage(message);
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

    float offset = 0.0f, maxOffsetX;
    float width, height, offsetX, offsetY;
    Canvas canvas;
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;
        width = getWidth();
        height = getHeight();
        offsetY = height / 8;
        offsetX = width / 21;
        paint();
    }

    private void paint() {
        float r = (getHeight() - offsetY * 2) / 2;
        maxOffsetX = getWidth() - 2 * (offsetX + r);

        printBackground();
        printTextContent();
        printCircle(r);
    }

    float bottom, top, centerX;
    private void printTextContent() {
        Paint paint = new Paint();
        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        paint.setColor(Color.RED);
        int baseline = (int)(bottom + top - fontMetrics.bottom - fontMetrics.top) / 2;
        // 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setStrokeWidth(3);
        paint.setTextSize(55);
        paint.setAlpha(getTextAlpha());
        canvas.drawText(textContent, centerX, baseline + 20, paint);
    }

    private void printBackground() {
        Paint paint = new Paint();
        paint.setColor(backgroundColor);
        RectF background = new RectF(offsetX, offsetY, getWidth() - offsetX, getHeight() - offsetY);
        canvas.drawRoundRect(background, 0xfff, 0xfff, paint);
        bottom = background.bottom;
        top = background.top;
        centerX = background.centerX();
    }

    private int getTextAlpha() {
        int base = (int) (255 - (255 * (offset >= maxOffsetX ? maxOffsetX : offset) / maxOffsetX));
        return base <= 235 ? base + 20 : base;
    }

    private void printCircle(float r) {
        Paint paint = new Paint();
        paint.setColor(circleColor);
        if(offset >= maxOffsetX)
            offset = maxOffsetX;
        canvas.drawCircle(r + offsetX + offset, r + offsetY, r, paint);
    }
}