package mobi.ttg.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;

import mobi.ttg.arduinowificontrol.ClientSocket;

/**
 * Created by duong on 2/23/2016.
 */
public class JoyStickView extends View implements View.OnTouchListener{
    private Context context;
    private Paint paint;
    private int cX, cY, stickPosX, stickPosY, stickRadius, bgRadius, minDistance, offSet;
    private boolean touchState = false;
    private int stickDirection = -1;
    private static final int STICK_UP = 0;
    private static final int STICK_DOWN = 1;
    private static final int STICK_RIGHT = 2;
    private static final int STICK_LEFT = 3;
    private static final int STICK_NONE = 4;
    private ProgressBar speedProgress;
    private ClientSocket clientSocket;
    private int speed = 0;
    public JoyStickView(Context context) {
        super(context);
        init(context);
    }

    public JoyStickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public JoyStickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        setOnTouchListener(this);
    }
    public void setSpeedProgress(ProgressBar pr){
        this.speedProgress = pr;
    }
    public void setClientSocket(ClientSocket client){
        this.clientSocket = client;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBg(canvas);
        drawStick(canvas);

    }

    private void drawStick(Canvas canvas) {
        paint.setColor(Color.parseColor("#005962"));
        paint.setStyle(Paint.Style.FILL);
        if(touchState){
            paint.setAlpha(128);
        }
        canvas.drawCircle(stickPosX, stickPosY, stickRadius, paint);
    }

    private void drawBg(Canvas canvas) {
        paint.setColor(Color.parseColor("#76c2af"));
        paint.setStrokeWidth(8);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(cX, cY, bgRadius, paint);
        paint.setStrokeWidth(4);
        canvas.drawCircle(cX, cY, offSet, paint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.cX = w/2;
        this.cY = h/2;
        this.stickRadius = (int)(cX*0.3f);
        this.bgRadius = (int)(cX*0.9f);
        this.stickPosX = cX;
        this.stickPosY = cY;
        this.offSet = stickRadius;
        if(speedProgress!=null){
            this.speedProgress.setMax(cX-offSet);
        }
        postInvalidate();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int tX = (int)event.getX();
        int tY = (int)event.getY();
        double distance = calDistance(tX, tY);
        double angle = calAngle(tX, tY);
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            touchState = true;
            if(distance <= cX-offSet){
                postInvalidate();
            }
        }else if(event.getAction() == MotionEvent.ACTION_MOVE && touchState){
            if(distance <= cX-offSet){
                stickPosX = tX;
                stickPosY = tY;
                invalidate();
            }else if(distance > cX-offSet){
                stickPosX = (int) (Math.cos(Math.toRadians(angle)) * (cX - offSet));
                stickPosY = (int) (Math.sin(Math.toRadians(angle)) * (cY - offSet));
                stickPosX += cX;
                stickPosY += cY;
                postInvalidate();
            }

        }else if(event.getAction() == MotionEvent.ACTION_UP){
            touchState = false;
            stickPosX = cX;
            stickPosY = cY;
            distance = 0;
            postInvalidate();
        }
        stickUpdate(distance,angle);
        return true;
    }
    private void stickUpdate(double distance, double angle){
        if(distance<offSet&&touchState){
            return;
        }
        int directionUpdate = calDirection(angle);
        if(stickDirection != directionUpdate){
            stickDirection = directionUpdate;
            if(clientSocket!=null){
                clientSocket.sendMessenge("DDI="+stickDirection+";");
            }
        }
        if(speedProgress!=null){
            speedProgress.setProgress((int)distance);
        }

    }

    private int calDirection(double angle) {
        if(!touchState) {
            return STICK_NONE;
        }
        if(angle >= 225 && angle < 315 ) {
            return STICK_UP;
        } else if(angle >= 315 || angle < 45 ) {
            return STICK_RIGHT;
        } else if(angle >= 45 && angle < 135 ) {
            return STICK_DOWN;
        } else if(angle >= 135 && angle < 225 ) {
            return STICK_LEFT;
        }
        return 0;
    }

    private double calDistance(int stickPosX, int stickPosY) {
        return Math.sqrt(Math.pow(stickPosX-cX, 2)+Math.pow(stickPosY-cY, 2));
    }
    private double calAngle(int stickPosX, int stickPosY){
        float x = stickPosX-cX;
        float y = stickPosY-cY;
        if(x==0){
            x=0.001f;
        }
        if(stickPosX >= cX && stickPosY >= cY)
            return Math.toDegrees(Math.atan(y/x));
        else if(stickPosX < cX && stickPosY >= cY)
            return Math.toDegrees(Math.atan(y/x)) + 180;
        else if(stickPosX < cX && stickPosY < cY)
            return Math.toDegrees(Math.atan(y/x)) + 180;
        else if(stickPosX >= cX && stickPosY < cY)
            return Math.toDegrees(Math.atan(y/x)) + 360;
        return 0;
    }
}
