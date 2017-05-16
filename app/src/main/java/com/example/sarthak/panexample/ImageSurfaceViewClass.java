package com.example.sarthak.panexample;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Sarthak on 10/5/17.
 */

public class ImageSurfaceViewClass extends SurfaceView implements ScaleGestureDetector.OnScaleGestureListener, Runnable{

    private Canvas canvas;

    private Thread renderThread = null;

    private SurfaceHolder holder;

    volatile boolean running = false;
    private boolean isValid;

    private Bitmap drawingBitmap = null;
    private Canvas drawingBoard = null;
    private Matrix drawingMatrix;

    private static float MIN_ZOOM = 1f;

    private static float MAX_ZOOM = 5f;

    private float scaleFactor = 1.f;

    private int mode;

    private boolean dragged = true;

    private int displayWidth, displayHeight;

    private static int NONE = 0, DRAG = 1, ZOOM = 2;
    private float startX = 0f,startY = 0f, translateX = 0f, translateY = 0f, previousTranslateX = 0, previousTranslateY = 0;



    private ScaleGestureDetector scaleGestureDetector;


    public ImageSurfaceViewClass(Context context) {
        super(context);
        init(context);
    }

    public ImageSurfaceViewClass(Context context, AttributeSet attr) {
        super(context, attr);
        init(context);
    }

    private void init(Context context) {
        holder = getHolder();
        scaleGestureDetector = new ScaleGestureDetector(context, this);
        running = true;
        drawingBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.courseimage);

        renderThread = new Thread(this);
        renderThread.start();

        displayWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        displayHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

//        if (event.getPointerCount() > 1) {
//            scaleGestureDetector.onTouchEvent(event);
//        }

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                startX = event.getX() - previousTranslateX;
                startY = event.getY() - previousTranslateY;
                break;

            case MotionEvent.ACTION_MOVE:
                translateX = event.getX() - startX;
                translateY = event.getY() - startY;

                double distance = Math.sqrt( Math.pow( (event.getX() - (startX + previousTranslateX)), 2) + Math.pow( (event.getY() - (startY + previousTranslateY)), 2) );
                if (distance > 0) {
                    dragged = true;
                }
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                mode = ZOOM;
                break;

            case MotionEvent.ACTION_UP:
                mode = NONE;
                dragged = false;
                previousTranslateX = translateX;
                previousTranslateY = translateY;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                mode = DRAG;
                previousTranslateX = translateX;
                previousTranslateY = translateY;
                break;

        }

        scaleGestureDetector.onTouchEvent(event);

        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        scaleFactor *= detector.getScaleFactor();
        scaleFactor = Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    @Override
    public void run() {
        while (running) {

            if (!holder.getSurface().isValid()) {
                //isValid = false;
                continue;
            }

            /*
            if (!isValid) {
                int myCanvas_w = getWidth();
                int myCanvas_h = getHeight();
                if(drawingBitmap != null) {
                    drawingBitmap.recycle();
                }
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = true;
                //drawingBitmap = Bitmap.createBitmap(myCanvas_w, myCanvas_h, Bitmap.Config.RGB_565);
                drawingBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.courseimage);
                //drawingBoard = new Canvas();
                //drawingBoard.setBitmap(drawingBitmap);
                canvas.setBitmap(drawingBitmap);
                drawingMatrix = new Matrix();
                isValid = true;
            }
            */
            if ( !((mode == DRAG && scaleFactor != 1f) || mode == ZOOM)) {
                continue;
            }

            canvas = holder.lockCanvas();
           // drawingBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.courseimage);

            //canvas.scale(this.scaleFactor, this.scaleFactor, this.scaleGestureDetector.getFocusX(), this.scaleGestureDetector.getFocusY());
            canvas.scale(scaleFactor, scaleFactor);

            //translateX = (translateX * -1) < 0 ? 0 : translateX;
            //translateY = (translateY * -1) < 0 ? 0 : translateY;

            if ( (translateX * -1) < 0) {
                translateX = 0;
            }
            else if ( (translateX * -1) > (scaleFactor - 1) * displayWidth) {
                translateX = (1 - scaleFactor) * displayWidth;
            }


            if ( (translateY * -1) < 0) {
                translateY = 0;
            }
            else if ( (translateY * -1) > (scaleFactor - 1) * displayHeight ) {
                translateY = (1 - scaleFactor) * displayHeight;
            }


            canvas.translate(translateX / scaleFactor, translateY / scaleFactor);
            canvas.drawBitmap(drawingBitmap, 0, 0, null);

            //render();
            holder.unlockCanvasAndPost(canvas);
        }
    }


    private void render() {
        canvas.drawBitmap(drawingBitmap, drawingMatrix, null);
    }


}
