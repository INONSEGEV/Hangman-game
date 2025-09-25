package com.shiftis.hangmangame;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class HangmanStepDrawView extends View {

    private Paint paint;
    private Path drawPath;
    private List<Path> steps; // 10 שלבים
    private int currentStep = 0;
    private float animatedValue = 0f;
    private PathMeasure pathMeasure;
    private float pathLength;

    private float width, height; // רוחב וגובה ה-View

    public HangmanStepDrawView(Context context) {
        super(context);
        init();
    }

    public HangmanStepDrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(0xFF000000); // שחור
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8f);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);

        drawPath = new Path();
        steps = new ArrayList<>();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;

        // יצירת השלבים בהתאם לגודל ה-View
        steps.clear();

        float baseY = height * 0.9f;
        float baseX = width * 0.2f;
        float poleHeight = height * 0.6f;
        float beamLength = width * 0.3f;

        // בסיס
        Path step1 = new Path();
        step1.moveTo(baseX, baseY);
        step1.lineTo(baseX + beamLength * 0.5f, baseY);
        steps.add(step1);

        // עמוד
        Path step2 = new Path();
        step2.moveTo(baseX + beamLength * 0.25f, baseY);
        step2.lineTo(baseX + beamLength * 0.25f, baseY - poleHeight);
        steps.add(step2);

        // קורה וחבל
        Path step3 = new Path();
        step3.moveTo(baseX + beamLength * 0.25f, baseY - poleHeight);
        step3.lineTo(baseX + beamLength * 0.75f, baseY - poleHeight);
        step3.lineTo(baseX + beamLength * 0.75f, baseY - poleHeight + height * 0.08f);
        steps.add(step3);

        // ראש
        Path step4 = new Path();
        float headRadius = width * 0.05f;
        step4.addCircle(baseX + beamLength * 0.75f, baseY - poleHeight + height * 0.15f, headRadius, Path.Direction.CW);
        steps.add(step4);

        // גוף
        Path step5 = new Path();
        float headBottom = baseY - poleHeight + height * 0.11f + headRadius;
        float bodyOffset = height * 0.05f;
        step5.moveTo(baseX + beamLength * 0.75f, headBottom + bodyOffset);
        step5.lineTo(baseX + beamLength * 0.75f, headBottom + bodyOffset + height * 0.15f);
        steps.add(step5);

        // יד שמאל
        Path step6 = new Path();
        step6.moveTo(baseX + beamLength * 0.75f, headBottom + bodyOffset + height * 0.03f);
        step6.lineTo(baseX + beamLength * 0.65f, headBottom + bodyOffset + height * 0.08f);
        steps.add(step6);

        // יד ימין
        Path step7 = new Path();
        step7.moveTo(baseX + beamLength * 0.75f, headBottom + bodyOffset + height * 0.03f);
        step7.lineTo(baseX + beamLength * 0.85f, headBottom + bodyOffset + height * 0.08f);
        steps.add(step7);

        // רגל שמאל
        Path step8 = new Path();
        step8.moveTo(baseX + beamLength * 0.75f, headBottom + bodyOffset + height * 0.15f);
        step8.lineTo(baseX + beamLength * 0.68f, headBottom + bodyOffset + height * 0.23f);
        steps.add(step8);

        // רגל ימין
        Path step9 = new Path();
        step9.moveTo(baseX + beamLength * 0.75f, headBottom + bodyOffset + height * 0.15f);
        step9.lineTo(baseX + beamLength * 0.82f, headBottom + bodyOffset + height * 0.23f);
        steps.add(step9);

        // שלב 10 - פנים "מת" (XX בעיניים ולשון בחוץ)
        Path step10 = new Path();
        float headCenterX = baseX + beamLength * 0.75f;
        float headCenterY = baseY - poleHeight + height * 0.15f;
        float eyeOffsetX = headRadius * 0.4f;
        float eyeOffsetY = headRadius * 0.3f;
        float eyeSize = headRadius * 0.2f;

        // עין שמאל XX
        step10.moveTo(headCenterX - eyeOffsetX - eyeSize, headCenterY - eyeOffsetY - eyeSize);
        step10.lineTo(headCenterX - eyeOffsetX + eyeSize, headCenterY - eyeOffsetY + eyeSize);
        step10.moveTo(headCenterX - eyeOffsetX - eyeSize, headCenterY - eyeOffsetY + eyeSize);
        step10.lineTo(headCenterX - eyeOffsetX + eyeSize, headCenterY - eyeOffsetY - eyeSize);

        // עין ימין XX
        step10.moveTo(headCenterX + eyeOffsetX - eyeSize, headCenterY - eyeOffsetY - eyeSize);
        step10.lineTo(headCenterX + eyeOffsetX + eyeSize, headCenterY - eyeOffsetY + eyeSize);
        step10.moveTo(headCenterX + eyeOffsetX - eyeSize, headCenterY - eyeOffsetY + eyeSize);
        step10.lineTo(headCenterX + eyeOffsetX + eyeSize, headCenterY - eyeOffsetY - eyeSize);

        // לשון "מת"
        step10.moveTo(headCenterX - headRadius * 0.3f, headCenterY + headRadius * 0.6f);
        step10.quadTo(headCenterX, headCenterY + headRadius * 0.8f, headCenterX + headRadius * 0.3f, headCenterY + headRadius * 0.6f);
        steps.add(step10);

        // --- תוספת חשובה ---
        // כדי לוודא שכל שלב נמדד לפני הציור
        for (Path p : steps) {
            PathMeasure pm = new PathMeasure(p, false);
            pm.getLength();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPath.reset();

        for (int i = 0; i < currentStep; i++) {
            drawPath.addPath(steps.get(i));
        }

        if (currentStep < steps.size() && steps.size() > 0) {
            Path current = steps.get(currentStep);
            pathMeasure = new PathMeasure(current, false);
            pathLength = pathMeasure.getLength();
            Path tempPath = new Path();
            pathMeasure.getSegment(0, pathLength * animatedValue, tempPath, true);
            drawPath.addPath(tempPath);
        }

        canvas.drawPath(drawPath, paint);
    }

    public void drawNextStep() {
        if (currentStep >= steps.size()) return;

        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(500); // חצי שניה לכל שלב
        animator.addUpdateListener(animation -> {
            animatedValue = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
        animator.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                currentStep++;
                animatedValue = 0f;
            }
        });
    }

    public void reset() {
        currentStep = 0;
        animatedValue = 0f;
        drawPath.reset();
        invalidate(); // גורם ל-onDraw להתעדכן ולהציג ציור ריק
    }

    public int getMaxSteps() {
        return steps.size();
    }
}
