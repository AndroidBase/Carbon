package pl.zielony.carbon.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;

import pl.zielony.carbon.GestureDetector;
import pl.zielony.carbon.OnGestureListener;
import pl.zielony.carbon.R;
import pl.zielony.carbon.animation.AnimUtils;
import pl.zielony.carbon.animation.DefaultAnimatorListener;
import pl.zielony.carbon.shadow.ShadowView;

/**
 * Created by Marcin on 2015-01-22.
 */
public class ImageView extends android.widget.ImageView implements ShadowView,OnGestureListener {
    private float elevation = 0;
    private float translationZ = 0;
    private boolean isRect = true;

    GestureDetector gestureDetector = new GestureDetector(this);
    private AnimUtils.Style inAnim, outAnim;
    private Rect touchMargin;
    private Bitmap texture;
    private Paint paint = new Paint();
    private Canvas textureCanvas;
    private float cornerRadius;

    public ImageView(Context context) {
        super(context);
        init(null, R.attr.carbon_imageViewStyle);
    }

    public ImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, R.attr.carbon_imageViewStyle);
    }

    public ImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }


    private void init(AttributeSet attrs, int defStyle) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ImageView, defStyle, 0);
        //int color = a.getColor(R.styleable.ImageView_carbon_rippleColor, 0);
        //if (color != 0)
          //  setBackgroundDrawable(new RippleDrawable(color, getBackground()));
        setElevation(a.getDimension(R.styleable.ImageView_carbon_elevation, 0));
        inAnim = AnimUtils.Style.values()[a.getInt(R.styleable.ImageView_carbon_inAnimation, 0)];
        outAnim = AnimUtils.Style.values()[a.getInt(R.styleable.ImageView_carbon_outAnimation, 0)];

        int touchMarginAll = (int) a.getDimension(R.styleable.ImageView_carbon_touchMargin, 0);
        if (touchMarginAll > 0) {
            touchMargin = new Rect(touchMarginAll, touchMarginAll, touchMarginAll, touchMarginAll);
        } else {
            touchMargin = new Rect();
            int top = (int) a.getDimension(R.styleable.ImageView_carbon_touchMarginTop, 0);
            int left = (int) a.getDimension(R.styleable.ImageView_carbon_touchMarginLeft, 0);
            int right = (int) a.getDimension(R.styleable.ImageView_carbon_touchMarginRight, 0);
            int bottom = (int) a.getDimension(R.styleable.ImageView_carbon_touchMarginBottom, 0);
            if (top > 0 || left > 0 || right > 0 || bottom > 0)
                touchMargin = new Rect(left, top, right, bottom);
        }

        cornerRadius = (int) a.getDimension(R.styleable.ImageView_carbon_cornerRadius, 0);

        a.recycle();

        paint.setAntiAlias(true);
    }

    public void setVisibility(final int visibility) {
        if (getVisibility() != View.VISIBLE && visibility == View.VISIBLE && inAnim != null) {
            super.setVisibility(visibility);
            AnimUtils.animateIn(this, inAnim, null);
        } else if (getVisibility() == View.VISIBLE && visibility != View.VISIBLE) {
            AnimUtils.animateOut(this, outAnim, new DefaultAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    ImageView.super.setVisibility(visibility);
                }
            });
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled())
            return false;

        gestureDetector.onTouchEvent(event);
        super.onTouchEvent(event);
        return true;
    }

    @Override
    public float getElevation() {
        return elevation;
    }

    public synchronized void setElevation(float elevation) {
        if (elevation == this.elevation)
            return;
        this.elevation = elevation;
        if (getParent() != null)
            ((View) getParent()).postInvalidate();
    }

    @Override
    public float getTranslationZ() {
        return translationZ;
    }

    private synchronized void setTranslationZInternal(float translationZ) {
        if (translationZ == this.translationZ)
            return;
        this.translationZ = translationZ;
        if (getParent() != null)
            ((View) getParent()).postInvalidate();
    }

    @Override
    public void setTranslationZ(float translationZ) {
        ValueAnimator animator = ValueAnimator.ofFloat(this.translationZ, translationZ);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setTranslationZInternal((Float) animation.getAnimatedValue());
            }
        });
        animator.start();
    }

    @Override
    public boolean isRect() {
        return isRect;
    }

    @Override
    public void setRect(boolean rect) {
        this.isRect = rect;
    }

    @Override
    public void onPress(MotionEvent motionEvent) {
        if (getBackground() instanceof RippleDrawable)
            ((RippleDrawable) getBackground()).onPress(motionEvent);
    }

    @Override
    public void onTap(MotionEvent motionEvent) {

    }

    @Override
    public void onDrag(MotionEvent motionEvent) {

    }

    @Override
    public void onMove(MotionEvent motionEvent) {

    }

    @Override
    public void onRelease(MotionEvent motionEvent) {
        if (getBackground() instanceof RippleDrawable)
            ((RippleDrawable) getBackground()).onRelease(motionEvent);
        setTranslationZ(0);
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public void onMultiTap(MotionEvent motionEvent, int clicks) {

    }

    @Override
    public void onCancel(MotionEvent motionEvent) {
        if (getBackground() instanceof RippleDrawable)
            ((RippleDrawable) getBackground()).onCancel(motionEvent);
        setTranslationZ(0);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if(!changed || getWidth() == 0 || getHeight() == 0)
            return;

        if (cornerRadius > 0) {
            texture = null;
            texture = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            textureCanvas = new Canvas(texture);
            paint.setShader(new BitmapShader(texture, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (cornerRadius > 0) {
            textureCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
            super.draw(textureCanvas);

            RectF rect = new RectF();
            rect.bottom = getHeight();
            rect.right = getWidth();
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint);
        } else {
            super.draw(canvas);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setTranslationZ(enabled ? 0 : -elevation);
    }

    public void getHitRect(Rect outRect) {
        if(touchMargin==null) {
            super.getHitRect(outRect);
            return;
        }
        outRect.set(getLeft() - touchMargin.left, getTop() - touchMargin.top, getRight() + touchMargin.right, getBottom() + touchMargin.bottom);
    }
}
