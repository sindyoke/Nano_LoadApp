package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.withStyledAttributes
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

    private var pointPosition: Int = 0

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private var valueAnimator = ValueAnimator()

    private var btnState: ButtonState by Delegates.observable(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Clicked -> {
                Log.d(TAG, "download started")
            }

            ButtonState.Loading -> {
                Log.d(TAG, "downloading")
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
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = notDownloadedColor
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        paint.color = Color.WHITE
        paint.textSize = 40f
        canvas.drawText(btnState.state, width / 2 - 20f, height / 2 + 15f, paint)

        if (btnState == ButtonState.Loading) {
            paint.color = downloadedColor
            canvas.drawRect(0f, 0f, width * pointPosition / 360f, height.toFloat(), paint)
        }
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

    override fun performClick(): Boolean {
        btnState = ButtonState.Loading
        invalidate()
        return super.performClick()
    }

    fun setButtonState(buttonState: ButtonState) {
        btnState = buttonState
    }
}