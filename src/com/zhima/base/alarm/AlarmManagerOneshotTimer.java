package com.zhima.base.alarm;

import android.content.Context;
import android.content.Intent;

/**
* @ClassName: AlarmManagerOneshotTimer
* @Description: 一次性闹铃
* @author liubingsr
* @date 2012-6-25 上午11:40:58
*
*/
public class AlarmManagerOneshotTimer extends AlarmManagerTimer {

    public AlarmManagerOneshotTimer(Context context) {
        super(context);
    }

    @Override
    public long register(long interval, Intent intent) {
        return register(interval, intent, true);
    }
}
