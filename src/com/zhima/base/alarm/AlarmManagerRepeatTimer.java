package com.zhima.base.alarm;

import android.content.Context;
import android.content.Intent;

/**
* @ClassName: AlarmManagerRepeatTimer
* @Description: 周期性闹铃
* @author liubingsr
* @date 2012-6-25 上午11:41:30
*
*/
public class AlarmManagerRepeatTimer extends AlarmManagerTimer {

    public AlarmManagerRepeatTimer(Context context) {
        super(context);
    }

    @Override
    public long register(long interval, Intent intent) {
        return register(interval, intent, false);
    }
}
