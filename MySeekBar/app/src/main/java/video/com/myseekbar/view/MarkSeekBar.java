package video.com.myseekbar.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import video.com.myseekbar.R;

/**
 * Created by fangzhu on 2015/8/18.
 */
public class MarkSeekBar extends View {

    String TAG = "MarkSeekBar";

    boolean drawBg = false;

    /*背景颜色*/
    int colorBg = Color.GRAY;
    /*进度条颜色*/
    int colorProgress = Color.RED;
    /*mark字体的颜色*/
    int markTextColor = Color.RED;
    /*mark字体的大小*/
    int markTextSize;
    /*真实的进度*/
    int progress = 0;
    /*最大真实值*/
    int max = 100;
    /*最后停留的位置*/
    int mLastMotionX;

    /*X轴起始位置*/
    int mOffestX;
    /*进度条高度*/
    int progressHeight;
    /*进度条高度边距*/
    int progressMargin;
    /*下方滑块图片*/
    Bitmap thumBitMap = null;
    /*下方滑块图片宽*/
    int thumBitMapWidth;
    /*下方滑块图片高*/
    int thumBitMapHeight;
    /*上方标示图片*/
    Bitmap markBitMap = null;
    /*上方图片宽*/
    int markBitMapWidth;
    /*上方图片高*/
    int markBitMapHeight;
    /*thum与progressBar的距离*/
    int progressToThumemargin;
    /*起始显示值*/
    String startProgressText;
    /*最大显示值*/
    String maxProgressText;
    /*起始字体颜色*/
    int startProgressTextColor = Color.BLACK;
    /*起始字体大小*/
    int startProgressTextSize;
    /*是否显示起始字体*/
    boolean isDrawStartProgressText = true;
    /*上方显示的进度文字*/
    String markString = null;
    /**
     * A callback that notifies clients when the progress level has been
     * changed. This includes changes that were initiated by the user through a
     * touch gesture or arrow key/trackball as well as changes that were initiated
     * programmatically.
     */
    public interface OnSeekBarChangeListener {

        /**
         * Notification that the progress level has changed. Clients can use the fromUser parameter
         * to distinguish user-initiated changes from those that occurred programmatically.
         *
         * @param seekBar  The SeekBar whose progress has changed
         * @param progress The current progress level. This will be in the range 0..
         *                 (The default value for max is 100.)
         * @param fromUser True if the progress change was initiated by the user.
         */
        void onProgressChanged(MarkSeekBar seekBar, int progress, boolean fromUser);

        /**
         * Notification that the user has started a touch gesture. Clients may want to use this
         * to disable advancing the seekbar.
         *
         * @param seekBar The SeekBar in which the touch gesture began
         */
        void onStartTrackingTouch(MarkSeekBar seekBar);

        /**
         * Notification that the user has finished a touch gesture. Clients may want to use this
         * to re-enable advancing the seekbar.
         *
         * @param seekBar The SeekBar in which the touch gesture began
         */
        void onStopTrackingTouch(MarkSeekBar seekBar);
    }

    OnSeekBarChangeListener onSeekBarChangeListener;

    public MarkSeekBar(Context context) {
        super(context);
        init();
    }

    public MarkSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MarkSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    void init() {
        mOffestX = 10;
        markTextSize = dip2px(getContext(), 12);
        startProgressTextSize = dip2px(getContext(), 16);
        progressToThumemargin = dip2px(getContext(), 5);

        thumBitMapWidth = dip2px(getContext(), 23);
        thumBitMapHeight = dip2px(getContext(), 28);
        markBitMapWidth = dip2px(getContext(), 23);
        markBitMapHeight = dip2px(getContext(), 28);

//        66 X 80 23
        thumBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_progress_thum);
//        thumBitMap = thumBitMap.createScaledBitmap(thumBitMap, thumBitMapWidth, thumBitMapHeight, true);//修改高宽超过原来的大小会失真

        markBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_progress_mark);
//        markBitMap = markBitMap.createScaledBitmap(markBitMap, markBitMapWidth, markBitMapHeight, true);//修改高宽超过原来的大小会失真


        progressMargin = thumBitMap.getWidth() / 2 + dip2px(getContext(), 10);
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    int getProgressHeight() {
        if (progressHeight > 0)
            return progressHeight;
        return dip2px(getContext(), 15);
    }

    int getProgressMargin() {
        if (progressMargin > 0)
            return progressMargin;
        return dip2px(getContext(), 25);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //根据progress得到滑动的位置
        int bgW = getWidth() - getProgressMargin() * 2;
        mLastMotionX = bgW * progress / max + getProgressMargin();

        if (mLastMotionX <= getProgressMargin())
            mLastMotionX = getProgressMargin() + mOffestX;


        drawProgressBg(canvas);
        drawProgressBar(canvas);
        drawBitMap(canvas);
        drawMarkText(canvas);

        if (isDrawStartProgressText)
            drawStartProgressText(canvas);

        if (isDrawStartProgressText)
            drawMaxProgressText(canvas);


    }

    void drawProgressBg (Canvas canvas) {
        //画圆角矩形
        Paint p = new Paint();
        p.setStyle(Paint.Style.FILL);//充满
        p.setColor(colorBg);
        p.setAntiAlias(true);// 设置画笔的锯齿效果
        int y = getHeight() / 2 - getProgressHeight() / 2;
        RectF bgRectF = new RectF(getProgressMargin(), y, getWidth() - getProgressMargin(), getProgressHeight() + y);// 设置个新的长方形
        canvas.drawRoundRect(bgRectF, getProgressHeight() / 2, getProgressHeight() / 2, p);//第二个参数是x半径，第三个参数是y半径
    }

    void drawProgressBar (Canvas canvas) {
        int y = getHeight() / 2 - getProgressHeight() / 2;
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);//充满
        paint.setColor(colorProgress);
        paint.setAntiAlias(true);// 设置画笔的锯齿效果
        RectF progressRectF = new RectF(getProgressMargin(), y, mLastMotionX, getProgressHeight() + y);
        canvas.drawRoundRect(progressRectF, getProgressHeight() / 2, getProgressHeight() / 2, paint);
    }
    void drawBitMap (Canvas canvas) {
        //drawBitmap
        Paint drawPaint = new Paint();
//        drawPaint.setStyle(Paint.Style.FILL);//充满
        drawPaint.setAntiAlias(true);// 设置画笔的锯齿效果

        float leftThun = mLastMotionX - thumBitMap.getWidth() / 2;
        float topThum = getHeight() / 2 + getProgressHeight() / 2 + progressToThumemargin;
        canvas.drawBitmap(thumBitMap, leftThun, topThum, drawPaint);

        float leftMark = mLastMotionX - markBitMap.getWidth() / 2;
        float topMark = getHeight() / 2 - getProgressHeight() / 2 - markBitMap.getHeight() - progressToThumemargin;
        canvas.drawBitmap(markBitMap, leftMark, topMark, drawPaint);

    }

    void drawMarkText (Canvas canvas) {
        String content = getMarkString();
        if (content == null)
            content = progress + "";

        float leftMark = mLastMotionX - markBitMap.getWidth() / 2;
        float topMark = getHeight() / 2 - getProgressHeight() / 2 - markBitMap.getHeight() - progressToThumemargin;
        //drawtext
        Paint paintText = new Paint();
        paintText.setAntiAlias(true);
        paintText.setStyle(Paint.Style.FILL);
        paintText.setColor(markTextColor);
        paintText.setTextSize(markTextSize);

        int w = (int) paintText.measureText(content);
        Paint.FontMetricsInt fontMetrics = paintText.getFontMetricsInt();
        int h = fontMetrics.bottom - fontMetrics.top;

        Rect targetRect = new Rect((int) leftMark, (int) topMark, (int) leftMark + thumBitMap.getWidth(), (int) topMark + thumBitMap.getHeight() - dip2px(getContext(), 2));
        int baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        // 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
        paintText.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(content, targetRect.centerX(), baseline, paintText);
    }

    void drawStartProgressText (Canvas canvas) {
        if (startProgressText == null)
            return;
        float left = getProgressMargin();
        float top = getHeight() / 2 - getProgressHeight() / 2;
        //drawtext
        Paint paintText = new Paint();
        paintText.setAntiAlias(true);
        paintText.setStyle(Paint.Style.FILL);
        paintText.setColor(startProgressTextColor);
        paintText.setTextSize(startProgressTextSize);

        String content = startProgressText;
        int w = (int) paintText.measureText(content);
        Paint.FontMetricsInt fontMetrics = paintText.getFontMetricsInt();
        int h = fontMetrics.bottom - fontMetrics.top;

        Rect targetRect = new Rect((int)left - w /2, (int)top - h, (int)left + w /2, (int)top);
        int baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        // 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
        paintText.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(content, targetRect.centerX(), baseline, paintText);
    }

    void drawMaxProgressText (Canvas canvas) {
        if (maxProgressText == null)
            return;
        float left = getWidth() - getProgressMargin();
        float top = getHeight() / 2 - getProgressHeight() / 2;
        //drawtext
        Paint paintText = new Paint();
        paintText.setAntiAlias(true);
        paintText.setStyle(Paint.Style.FILL);
        paintText.setColor(startProgressTextColor);//这里简单复用了
        paintText.setTextSize(startProgressTextSize);

        String content = maxProgressText;
        int w = (int) paintText.measureText(content);
        Paint.FontMetricsInt fontMetrics = paintText.getFontMetricsInt();
        int h = fontMetrics.bottom - fontMetrics.top;

        Rect targetRect = new Rect((int)left - w /2, (int)top - h, (int)left + w /2, (int)top);
        int baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        // 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
        paintText.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(content, targetRect.centerX(), baseline, paintText);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (onSeekBarChangeListener != null)
                    onSeekBarChangeListener.onStartTrackingTouch(this);

                mLastMotionX = (int) event.getX();

                break;
            case MotionEvent.ACTION_UP:
                if (onSeekBarChangeListener != null)
                    onSeekBarChangeListener.onStopTrackingTouch(this);

                break;
            case MotionEvent.ACTION_POINTER_UP:

                break;
            case MotionEvent.ACTION_POINTER_DOWN:

                break;
            case MotionEvent.ACTION_MOVE:

                float topMark = getHeight() / 2 - getProgressHeight() / 2;

                Log.v(TAG, "topMark=" + topMark + "  event.getY()=" + event.getY());
                //只允许手势在下方滑动
                if (topMark > event.getY())
                    break;

                mLastMotionX = (int) event.getX();


                Log.v(TAG, "mLastMotionX=" + mLastMotionX);
                if (mLastMotionX < getProgressMargin())
                    mLastMotionX = getProgressMargin();
                if (mLastMotionX > getWidth() - getProgressMargin())
                    mLastMotionX = getWidth() - getProgressMargin();
                Log.v(TAG, "mLastMotionX 222 =" + mLastMotionX);
                int progressW = mLastMotionX - getProgressMargin();
                int bgW = getWidth() - getProgressMargin() * 2;
                progress = (int) ((float) progressW / (float) bgW * (float) max);
                Log.v(TAG, "progress =" + progress);

                if (onSeekBarChangeListener != null)
                    onSeekBarChangeListener.onProgressChanged(this, progress, true);
                if (mLastMotionX <= getProgressMargin())
                    mLastMotionX = getProgressMargin() + mOffestX;
                postInvalidate();
                break;
        }

        return true;

    }

    public boolean isDrawBg() {
        return drawBg;
    }

    public void setDrawBg(boolean drawBg) {
        this.drawBg = drawBg;
    }

    public int getColorBg() {
        return colorBg;
    }

    public void setColorBg(int colorBg) {
        this.colorBg = colorBg;
    }

    public int getColorProgress() {
        return colorProgress;
    }

    public void setColorProgress(int colorProgress) {
        this.colorProgress = colorProgress;
    }

    public int getMarkTextColor() {
        return markTextColor;
    }

    public void setMarkTextColor(int markTextColor) {
        this.markTextColor = markTextColor;
    }

    public int getMarkTextSize() {
        return markTextSize;
    }

    public void setMarkTextSize(int markTextSize) {
        this.markTextSize = markTextSize;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        postInvalidate();
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
        postInvalidate();
    }

    public int getmLastMotionX() {
        return mLastMotionX;
    }

    public void setmLastMotionX(int mLastMotionX) {
        this.mLastMotionX = mLastMotionX;
    }

    public int getmOffestX() {
        return mOffestX;
    }

    public void setmOffestX(int mOffestX) {
        this.mOffestX = mOffestX;
    }

    public void setProgressHeight(int progressHeight) {
        this.progressHeight = progressHeight;
    }

    public void setProgressMargin(int progressMargin) {
        this.progressMargin = progressMargin;
    }

    public Bitmap getThumBitMap() {
        return thumBitMap;
    }

    public void setThumBitMap(Bitmap thumBitMap) {
        this.thumBitMap = thumBitMap;
    }

    public int getThumBitMapWidth() {
        return thumBitMapWidth;
    }

    public void setThumBitMapWidth(int thumBitMapWidth) {
        this.thumBitMapWidth = thumBitMapWidth;
    }

    public int getThumBitMapHeight() {
        return thumBitMapHeight;
    }

    public void setThumBitMapHeight(int thumBitMapHeight) {
        this.thumBitMapHeight = thumBitMapHeight;
    }

    public Bitmap getMarkBitMap() {
        return markBitMap;
    }

    public void setMarkBitMap(Bitmap markBitMap) {
        this.markBitMap = markBitMap;
    }

    public int getMarkBitMapWidth() {
        return markBitMapWidth;
    }

    public void setMarkBitMapWidth(int markBitMapWidth) {
        this.markBitMapWidth = markBitMapWidth;
    }

    public int getMarkBitMapHeight() {
        return markBitMapHeight;
    }

    public void setMarkBitMapHeight(int markBitMapHeight) {
        this.markBitMapHeight = markBitMapHeight;
    }

    public int getProgressToThumemargin() {
        return progressToThumemargin;
    }

    public void setProgressToThumemargin(int progressToThumemargin) {
        this.progressToThumemargin = progressToThumemargin;
    }

    public String getStartProgressText() {
        return startProgressText;
    }

    public void setStartProgressText(String startProgressText) {
        this.startProgressText = startProgressText;
    }

    public String getMaxProgressText() {
        return maxProgressText;
    }

    public void setMaxProgressText(String maxProgressText) {
        this.maxProgressText = maxProgressText;
    }

    public int getStartProgressTextColor() {
        return startProgressTextColor;
    }

    public void setStartProgressTextColor(int startProgressTextColor) {
        this.startProgressTextColor = startProgressTextColor;
    }

    public int getStartProgressTextSize() {
        return startProgressTextSize;
    }

    public void setStartProgressTextSize(int startProgressTextSize) {
        this.startProgressTextSize = startProgressTextSize;
    }

    public boolean isDrawStartProgressText() {
        return isDrawStartProgressText;
    }

    public void setIsDrawStartProgressText(boolean isDrawStartProgressText) {
        this.isDrawStartProgressText = isDrawStartProgressText;
    }

    public OnSeekBarChangeListener getOnSeekBarChangeListener() {
        return onSeekBarChangeListener;
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener onSeekBarChangeListener) {
        this.onSeekBarChangeListener = onSeekBarChangeListener;
    }

    public String getMarkString() {
        return markString;
    }

    public void setMarkString(String markString) {
        this.markString = markString;
    }
}
