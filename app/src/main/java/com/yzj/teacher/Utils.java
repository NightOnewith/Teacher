package com.yzj.teacher;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class Utils {

    public static void popupKeyboard(final EditText editText) {
        editText.postDelayed(new Runnable() {
            @Override
            public void run() {

                InputMethodManager inputManager =
                        (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editText, 0);

            }
        }, 400);
    }
}
