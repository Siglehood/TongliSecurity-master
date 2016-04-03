package com.sig.tonglisecurity.widget;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Selection;
import android.text.Spannable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import com.sig.tonglisecurity.R;


public class EditTextWithClearButton extends EditText {

    public EditTextWithClearButton(Context context) {
        super(context);
    }

    public EditTextWithClearButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextWithClearButton(Context context, AttributeSet attrs,
                                   int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start,
                                 int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);

        Drawable[] drawable = getCompoundDrawables();
        if (!getText().toString().equals("") && hasFocus()) {
            setCompoundDrawablesWithIntrinsicBounds(
                    drawable[0],
                    drawable[1],
                    getContext().getResources().getDrawable(
                            R.drawable.ic_clear), drawable[3]);
        } else {
            setCompoundDrawablesWithIntrinsicBounds(drawable[0], drawable[1],
                    null, drawable[3]);
        }
        invalidate();
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        Drawable[] drawable = getCompoundDrawables();
        if (focused && !getText().toString().equals("")) {
            setCompoundDrawablesWithIntrinsicBounds(
                    drawable[0],
                    drawable[1],
                    getContext().getResources().getDrawable(
                            R.drawable.ic_clear), drawable[3]);
        } else {
            setCompoundDrawablesWithIntrinsicBounds(drawable[0], drawable[1],
                    null, drawable[3]);
        }
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean re = super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                Drawable[] drawable = getCompoundDrawables();
                if (drawable[2] != null) {
                    int left = getWidth() - getPaddingRight() - drawable[2].getIntrinsicWidth();
                    int right = getWidth() - getPaddingRight();
                    int top = getPaddingTop();
                    int bottom = getHeight() - getPaddingBottom();
                    if (event.getX() < right && event.getX() > left
                            && event.getY() > top && event.getY() < bottom) {
                        setText("");
                        invalidate();
                    }
                }
                break;
        }
        return re;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        //把光标定位到文本末尾
        CharSequence text2 = getText();
        if (text2 instanceof Spannable) {
            Spannable spanText = (Spannable) text2;
            Selection.setSelection(spanText, text2.length());
        }
    }
}
