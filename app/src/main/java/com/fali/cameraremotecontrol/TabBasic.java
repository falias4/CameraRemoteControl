package com.fali.cameraremotecontrol;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

/**
 * Created by Toby on 06.06.2016.
 */
public class TabBasic extends Fragment {
    ToggleButton btnFocus;
    Button btnShutter;
    MainActivity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_basic, container, false);

        mActivity = ((MainActivity)getActivity());

        btnFocus = (ToggleButton) v.findViewById(R.id.toggleFocus);
        btnShutter = (Button) v.findViewById(R.id.btnShutter);

        btnFocus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                int serialRes = isChecked ? R.string.kFOCUS : R.string.kFOCUS_END;
                mActivity.btController.sendString(getString(serialRes));
            }
        });

        btnShutter.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                int serialRes = 0;

                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        serialRes = R.string.kSHUTTER;
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        serialRes = R.string.kSHUTTER_END;
                        break;
                }

                if(serialRes != 0) {
                    mActivity.btController.sendString(getString(serialRes));
                    Log.d("faliLogs", "onTouch: " + getString(serialRes));
                }

                return true;
            }
        });

        return v;
    }
}
