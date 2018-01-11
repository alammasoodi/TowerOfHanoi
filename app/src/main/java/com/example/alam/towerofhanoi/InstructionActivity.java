package com.example.alam.towerofhanoi;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;
import java.util.Stack;

import static java.security.AccessController.getContext;

public class InstructionActivity extends Activity {
    Draw drawingView;
    TextView steps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
        steps = (TextView) findViewById(R.id.steps);
        LinearLayout ll =(LinearLayout) findViewById(R.id.main_layout);

        drawingView = new Draw(this,1000,800,4);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
        ll.addView(drawingView);
    }
    public class Draw extends View {
        float bottomLimit;
        Context context;
        boolean isValidTouch = true;
        float leftLimitMiddleRod;
        private Stack<DiskShape> leftRod;
        private Stack<DiskShape> middleRod;
        int moves = 0;
        int no_of_disks;
        float rightLimitMiddleRod;
        private Stack<DiskShape> rightRod;
        private Stack<DiskShape> rodWithDiskSelected = null;
        float topLimit;
        float f1x;
        float xRatio;
        float f2y;
        float yRatio;
        Paint paint = new Paint(Paint.DITHER_FLAG);

        public Draw(Context context, float width, float height, int _no_of_disks) {
            super(context);
            this.context = context;
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);
            paint.setStrokeWidth(5);
            setBackgroundDrawable(new BitmapDrawable(BitmapFactory.decodeResource(getResources(), 1)));
            this.leftRod = new Stack();
            this.middleRod = new Stack();
            this.rightRod = new Stack();
            this.xRatio = width / 480.0f;
            this.yRatio = height / 320.0f;
            this.no_of_disks = _no_of_disks;
            this.bottomLimit = 10.0f * this.xRatio;
            this.topLimit = 450.0f * this.xRatio;
            this.leftLimitMiddleRod = 150.0f * this.yRatio;
            this.rightLimitMiddleRod = 300.0f * this.yRatio;
            for (int i = _no_of_disks; i >= 1; i--) {
                this.leftRod.push(new DiskShape(i, this.xRatio, this.yRatio));
            }
        }

        public void onDraw(Canvas canvas) {
            canvas.drawLine(200, 290, 700, 290, paint);
            canvas.drawLine(200, 660, 700, 660, paint);
            canvas.drawLine(200, 1040, 700, 1040, paint);
            canvas.translate(this.xRatio * 220.0f, this.yRatio * 90.0f);
            canvas.save();
            drawDisks(canvas, this.leftRod);
            canvas.restore();
            canvas.translate(this.xRatio * 0.0f, this.yRatio * 150.0f);
            canvas.save();
            drawDisks(canvas, this.middleRod);
            canvas.restore();
            canvas.translate(this.xRatio * 0.0f, this.yRatio * 150.0f);
            canvas.save();
            drawDisks(canvas, this.rightRod);
            canvas.restore();
        }

        private void drawDisks(Canvas canvas, Stack<DiskShape> rod) {
            Iterator it = rod.iterator();
            while (it.hasNext()) {
                ((DiskShape) it.next()).draw(canvas);
                canvas.translate(0.0f, -25.0f * this.yRatio);
            }
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == 0) {
                this.f1x = event.getX();
                this.f2y = event.getY();
                if (this.f1x <= this.bottomLimit || this.f1x >= this.topLimit) {
                    this.isValidTouch = false;
                } else {
                    this.isValidTouch = true;
                    if (this.f2y < this.leftLimitMiddleRod) {
                        this.rodWithDiskSelected = this.leftRod;
                    } else if ( this.f2y > this.rightLimitMiddleRod) {
                        this.rodWithDiskSelected = this.rightRod;
                    } else {
                        this.rodWithDiskSelected = this.middleRod;
                    }
                    if (this.rodWithDiskSelected.size() != 0) {
                        ((DiskShape) this.rodWithDiskSelected.lastElement()).select();
                    }
                }
                invalidate();
            } else if (event.getAction() == 2) {
                if (this.isValidTouch && this.rodWithDiskSelected.size() != 0) {
                    int mX = (int) (250.0f * this.xRatio);
                    int mY = (int) (100.0f * this.yRatio);
                    if (this.rodWithDiskSelected == this.middleRod) {
                        mY = (int) (((float) mY) + (150.0f * this.yRatio));
                    } else if (this.rodWithDiskSelected == this.rightRod) {
                        mY = (int) (((float) mY) + (300.0f * this.yRatio));
                    }
                    int mm = (int) (((float) (this.rodWithDiskSelected.size() * 25)) * this.xRatio);
                    this.f1x = event.getX() - ((float) mX) + ((float) mm);
                    this.f2y = (event.getY() - ((float) mY));
                    ((DiskShape) this.rodWithDiskSelected.lastElement()).setBound();
                    ((DiskShape) this.rodWithDiskSelected.lastElement()).getBounds().inset((int) this.f1x, (int) this.f2y);
                    invalidate();
                }
            } else if (event.getAction() == 1 && this.isValidTouch && this.rodWithDiskSelected.size() != 0) {
                this.f1x = event.getX();
                this.f2y = event.getY();
                ((DiskShape) this.rodWithDiskSelected.lastElement()).setBound();
                if (this.f1x <= this.bottomLimit || this.f1x >= this.topLimit) {
                    ((DiskShape) this.rodWithDiskSelected.lastElement()).unSelect();
                } else {
                    ((DiskShape) this.rodWithDiskSelected.lastElement()).getBounds().inset((int) this.f1x, (int) this.f2y);
                    if (this.f2y < this.leftLimitMiddleRod) {
                        actionOnTouch(this.leftRod);
                    } else if (this.f2y < this.leftLimitMiddleRod || this.f2y > this.rightLimitMiddleRod) {
                        actionOnTouch(this.rightRod);
                    } else {
                        actionOnTouch(this.middleRod);
                    }
                }
                invalidate();
            }
            return true;
        }

        private void actionOnTouch(Stack<DiskShape> touchedRod) {
            ((DiskShape) this.rodWithDiskSelected.lastElement()).unSelect();
            ((DiskShape) this.rodWithDiskSelected.lastElement()).setBound();
            if (isValidMove(touchedRod)) {
                touchedRod.push((DiskShape) this.rodWithDiskSelected.pop());
                this.moves++;
                steps.setText(String.valueOf(this.moves));
            }
            this.rodWithDiskSelected = null;
            invalidate();
            if (this.rightRod.size() == this.no_of_disks || this.middleRod.size() == this.no_of_disks) {
//                ((Play) getContext()).gameOver(this.moves);
                Toast.makeText(context,"You win",Toast.LENGTH_SHORT).show();
            }
        }

        private boolean isValidMove(Stack<DiskShape> touchedRod) {
            return touchedRod.size() == 0 || ((DiskShape) this.rodWithDiskSelected.lastElement()).size < ((DiskShape) touchedRod.lastElement()).size;
        }
    }
    public static class DiskShape extends ShapeDrawable {
        public static float[] diskOuterRadius = new float[]{25.0f, 25.0f, 25.0f, 25.0f, 25.0f, 25.0f, 25.0f, 25.0f};
        int size;
        float yRatio;

        public DiskShape(int _size, float xRatio, float yRatio) {
            super(new RoundRectShape(diskOuterRadius, null, null));
            this.yRatio = yRatio;
            this.size = (int) (((float) (_size * 44)) * xRatio);
            unSelect();
            setBound();
        }

        public void setBound() {
            setBounds(0, 0, this.size, (int) (22.0f * this.yRatio));
        }

        public void select() {
            getPaint().setColor(-1996519356);
        }

        public void unSelect() {
            getPaint().setColor(Color.BLUE);
        }

        protected void onDraw(Shape shape, Canvas canvas, Paint paint) {
            canvas.save();
            canvas.translate((float) ((-this.size) / 2), 0.0f);
            shape.draw(canvas, paint);
            canvas.restore();
        }
    }
//    public static final class C0034R {
//
//        public static final class attr {
//        }
//
//        public static final class drawable {
//            public static final int easy = 2130837504;
//            public static final int exit = 2130837505;
//            public static final int gam = 2130837506;
//            public static final int hanoi_background = 2130837507;
//            public static final int hard = 2130837508;
//            public static final int ic_launcher = 2130837509;
//            public static final int ins = 2130837510;
//            public static final int med = 2130837511;
//        }
//
//        public static final class id {
//            public static final int dArea = 2131099649;
//            public static final int easy = 2131099651;
//            public static final int hard = 2131099652;
//            public static final int imageView1 = 2131099648;
//            public static final int medium = 2131099650;
//            public static final int textView1 = 2131099653;
//            public static final int textView2 = 2131099655;
//            public static final int textView3 = 2131099656;
//            public static final int textView4 = 2131099658;
//            public static final int textView5 = 2131099659;
//            public static final int textView6 = 2131099654;
//            public static final int textView7 = 2131099657;
//        }
//
//        public static final class layout {
//            public static final int game = 2130903040;
//            public static final int home = 2130903041;
//            public static final int instruct = 2130903042;
//            public static final int levels = 2130903043;
//        }
//
//        public static final class string {
//            public static final int app_name = 2130968576;
//            public static final int author = 2130968577;
//        }
//
//        public static final class style {
//            public static final int AppBaseTheme = 2131034112;
//            public static final int AppTheme = 2131034113;
//        }
//    }
}
