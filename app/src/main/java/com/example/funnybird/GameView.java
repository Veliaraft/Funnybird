package com.example.funnybird;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View {
    private final Sprite playerBird;
    private final Sprite enemyBird;
    private final Sprite background;
    private int viewWidth;
    private int viewHeight;
    private int points = 0;

    private final int timerInterval = 30;
    public GameView(Context context) {
        super(context);
        Bitmap bgbg = BitmapFactory.decodeResource(getResources(), R.drawable.bg);
        Bitmap bg = Bitmap.createScaledBitmap(bgbg, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT, false);
        int bgw = bg.getWidth(); int bgh = bg.getHeight(); Rect bgr = new Rect(0, 0, bgw, bgh);
        background = new Sprite(0, 0, 0, 0, bgr, bg);
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.friend);
        Bitmap n = BitmapFactory.decodeResource(getResources(), R.drawable.notfriend);
        int bw = b.getWidth()/8; int bh = b.getHeight();
        int nw = n.getWidth()/8; int nh = n.getHeight();
        Rect frame = new Rect(0, 0, bw, bh);
        Rect frame2 = new Rect(0, 0, nw, nh);
        playerBird = new Sprite(10, 0, 0, 100, frame, b);
        enemyBird = new Sprite(2000, 250, -300, 0, frame2, n);
        for(int i = 0; i < 8; i++){
            playerBird.addFrame(new Rect(i*bw, 0, i*bw+bw, bh));
            enemyBird.addFrame(new Rect(i*nw, 0, i*nw+nw, nh));
        }
        Timer t = new Timer();
        t.start();
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        background.draw(canvas);
        playerBird.draw(canvas);
        enemyBird.draw(canvas);

        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setTextSize(55.0f);
        p.setColor(Color.WHITE);
        canvas.drawText(points + "", viewWidth - 200, 70, p);
    }

    protected void update () {
        playerBird.update(timerInterval);
        enemyBird.update(timerInterval);

        if (playerBird.getY() + playerBird.getFrameHeight() > viewHeight) {
            playerBird.setY(viewHeight - playerBird.getFrameHeight());
            playerBird.setVy(-playerBird.getVy());
            points--;
        }
        else if (playerBird.getY() < 0) {
            playerBird.setY(0);
            playerBird.setVy(-playerBird.getVy());
            points--;
        }

        if (enemyBird.getX() < - enemyBird.getFrameWidth()) {
            teleportEnemy();
            points +=10;
        }

        if (enemyBird.intersect(playerBird)) {
            teleportEnemy ();
            points -= 40;
        }
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventAction = event.getAction();
        if (eventAction == MotionEvent.ACTION_DOWN)  {
            if (event.getY() < playerBird.getBoundingBoxRect().top) {
                playerBird.setVy(-200);
                points--;
            }
            else if (event.getY() > (playerBird.getBoundingBoxRect().bottom)) {
                playerBird.setVy(200);
                points--;
            }
        }
        return true;
    }

    private void teleportEnemy () {
        enemyBird.setX(viewWidth + Math.random() * 500);
        enemyBird.setY(Math.random() * (viewHeight - enemyBird.getFrameHeight()));
    }

    class Timer extends CountDownTimer {
        public Timer() { super(Integer.MAX_VALUE, timerInterval); }
        @Override
        public void onTick(long millisUntilFinished) { update (); }
        @Override
        public void onFinish() { }
    }
}
