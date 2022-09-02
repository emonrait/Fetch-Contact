package com.emon.raihan.contactfeatch

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

open class CustomAppCompatActivity : AppCompatActivity(), View.OnTouchListener {
    //begin keyboard hide
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return false
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val v = currentFocus
        if (v != null && (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE)
            && v is EditText
            && !v.javaClass.name.startsWith("android.webkit.")
        ) {
            val scrcoords = IntArray(2)
            v.getLocationOnScreen(scrcoords)
            val x = ev.rawX + v.getLeft() - scrcoords[0]
            val y = ev.rawY + v.getTop() - scrcoords[1]
            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()
            ) hideKeyboard(this)
        }
        return super.dispatchTouchEvent(ev)
    }

    fun hideKeyboard(activity: Activity?) {
        if (activity != null && activity.window != null
        ) {
            val imm = activity
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(
                activity.window.decorView
                    .windowToken, 0
            )
        }
    }
    //end keyboard hide
}