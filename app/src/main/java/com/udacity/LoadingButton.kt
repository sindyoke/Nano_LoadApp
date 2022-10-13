package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.toRectF
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "LoadingButton"
    }

    private var widthSize = 0
    private var heightSize = 0

    private var downloadedColor = 0
    private var notDownloadedColor = 0
    private var circleColor = 0
    private var textColor = 0

    private var pointPosition: Int = 0
    private var textRect = Rect()
    private var circleRect = Rect()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 40f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private var valueAnimator = ValueAnimator()

    private var btnState: ButtonState by Delegates.observable(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Clicked -> {
                Log.d(TAG, "button clicked")
            }

            ButtonState.Loading -> {
                Log.d(TAG, "downloading")
                valueAnimator.cancel()
                valueAnimator = ValueAnimator.ofInt(0, 360).setDuration(2000)
                    .apply {
                        addUpdateListener {
                            pointPosition = it.animatedValue as Int
                            invalidate()
                        }
                        repeatCount = ValueAnimator.INFINITE
                        repeatMode = ValueAnimator.RESTART
                        start()
                    }
            }

            ButtonState.Completed -> {
                Log.d(TAG, "download completed")
                valueAnimator.cancel()
            }
        }
    }

    init {
        isClickable = true

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            notDownloadedColor = getColor(R.styleable.LoadingButton_buttonColor1, 0)
            downloadedColor = getColor(R.styleable.LoadingButton_buttonColor2, 0)
            circleColor = getColor(R.styleable.LoadingButton_circleColor, 0)
            textColor = getColor(R.styleable.LoadingButton_textColor, 0)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = notDownloadedColor
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        if (btnState == ButtonState.Loading) {
            paint.color = downloadedColor
            canvas.drawRect(0f, 0f, width * pointPosition / 360f, height.toFloat(), paint)

            paint.color = circleColor
            canvas.drawArc(circleRect.toRectF(), -90f, pointPosition.toFloat(), true, paint)
        } else {
            paint.color = notDownloadedColor
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        }

        paint.color = textColor
        canvas.drawText(
            btnState.state,
            width.toFloat()/2,
            height.toFloat()/2 - (paint.descent() + paint.ascent())/2,
            paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        paint.getTextBounds(ButtonState.Loading.state, 0, ButtonState.Loading.state.length, textRect)
        circleRect = Rect(
            width/2 + textRect.width()/2 + 15,
            height/2 - textRect.height()/2,
            width/2 + textRect.width()/2 + 15 + textRect.height(),
            height/2 + textRect.height()/2
        )
    }

    override fun performClick(): Boolean {
        btnState = ButtonState.Loading
        invalidate()
        return super.performClick()
    }

    fun setButtonState(buttonState: ButtonState) {
        btnState = buttonState
        invalidate()
    }

    fun stopAnimation() {
        valueAnimator.cancel()
    }
}