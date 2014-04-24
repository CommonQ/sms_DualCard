/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.bupt.mms.ui;

import android.content.Context;
import android.text.ClipboardManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ListView;

public final class MessageListView extends ListView {
    private OnSizeChangedListener mOnSizeChangedListener;

    public MessageListView(Context context) {
        super(context);
    }

    
    public MessageListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_C:
        	
        	
        	 Log.v("MessageList", "触发了！！！");
            MessageListItem view = (MessageListItem)getSelectedView();
            if (view == null) {
                break;
            }
            MessageItem item = view.getMessageItem();
           
            if (item != null && item.isSms()) {
                ClipboardManager clip =
                    (ClipboardManager)getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                clip.setText(item.mBody);
                return true;
            }
            break;
        }

        return super.onKeyShortcut(keyCode, event);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        Log.v("MessageList", "触发了！！！");
        if (mOnSizeChangedListener != null) {
            mOnSizeChangedListener.onSizeChanged(w, h, oldw, oldh);
            Log.v("MessageList", "触发了！！！");
        }
    }

    /**
     * Set the listener which will be triggered when the size of
     * the view is changed.
     */
    void setOnSizeChangedListener(OnSizeChangedListener l) {
        mOnSizeChangedListener = l;
        
        
        Log.v("MessageList", "触发了！！！");
    }

    public interface OnSizeChangedListener {
        void onSizeChanged(int width, int height, int oldWidth, int oldHeight);
    }
}

