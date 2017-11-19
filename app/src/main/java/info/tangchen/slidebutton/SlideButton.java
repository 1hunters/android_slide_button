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

                if (startX > this.spaceX + 2 * r)
                    return false;

                break;
            case MotionEvent.ACTION_MOVE: //手指按下，滑动的过程
                offsetX = motionEvent.getX() - startX;

                if(offsetX > 0) {
                    offset = offsetX;
                    refreshUI();
                }

                break;
            case MotionEvent.ACTION_UP:
                if(offset + 25 >= maxOffsetX)
                    listener.changeState();

                for(; offset > 0; offset--)
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

    float offset = 0.0f, maxOffsetX, r;
    float width, height, spaceX, spaceY;
    Canvas canvas;
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;

        if(width == 0) {
            width = getWidth();
            height = getHeight();
            spaceY = height / 8; //y轴留空距离
            spaceX = width / 21; //x轴留空距离，我这里把宽度分割成了21份，在下面取2份为左右两边的留空距离
            r = (getHeight() - spaceY * 2) / 2; //圆形按钮的半径，即为整个view的高度减去上下两部分留空区域的一半
            maxOffsetX = getWidth() - 2 * (spaceX + r); //圆形按钮圆心最远移动距离，即为整个控件的宽度减去 2 * 左右两侧留空 ＋ 2 * 半径
        }

        paint();
    }

    private void paint() {
        printBackground();
        printTextContent();
        printCircle(r);
    }

    float bottom, top, centerX;
    private void printTextContent() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
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
        paint.setAntiAlias(true);
        paint.setColor(backgroundColor);
        RectF background = new RectF(spaceX, spaceY, getWidth() - spaceX, getHeight() - spaceY);
        canvas.drawRoundRect(background, 0xfff, 0xfff, paint);
        bottom = background.bottom;
        top = background.top;
        centerX = background.centerX();
    }

    private int getTextAlpha() {
        //描述文字半透明为 （当前移动距离：最远移动距离）* 255
        int base = (int) (255 - (255 * (offset >= maxOffsetX ? maxOffsetX : offset) / maxOffsetX));
        return base <= 235 ? base + 20 : base; //若半透明度<235时候
    }

    private void printCircle(float r) {
        Paint paint = new Paint();
        paint.setColor(circleColor);
        paint.setAntiAlias(true);

        //给偏移量设置一个阈值，即不能超过圆形的最大移动距离，否则会出现圆形控件移出整个滑动部件空间的情况
        if(offset >= maxOffsetX)
            offset = maxOffsetX;

        //cx：圆心x坐标，为r + x轴留空距离 + 移动的距离，cy：圆心y点坐标，为r＋y轴留空距离
        //整个圆形的滑动过程即为圆形在x轴上的运动，因此只改变圆心的x坐标即可
        canvas.drawCircle(r + spaceX + offset, r + spaceY, r, paint);
    }
}