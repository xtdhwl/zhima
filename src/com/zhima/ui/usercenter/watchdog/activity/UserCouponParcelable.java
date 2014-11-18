package com.zhima.ui.usercenter.watchdog.activity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Parcel类:http://developer.android.com/reference/android/os/Parcel.html <br>
 * 封装数据的容器，封装后的数据可以通过Intent或IPC传递 <br>
 * 
 * Parcelable接口：http://developer.android.com/reference/android/os/Parcelable.html <br>
 * 自定义类继承该接口后，其实例化后能够被写入Parcel或从Parcel中恢复。 <br>
 * 
 * 如果某个类实现了这个接口，那么它的对象实例可以写入到 Parcel 中，并且能够从中恢复，
 * 并且这个类必须要有一个 static 的 field ，并且名称要为 CREATOR ，这个 field 是某个实现了 Parcelable.Creator 接口的类的对象实例。
 */
public class UserCouponParcelable implements Parcelable{
        public String name;
        public long mId;
        public long beginTime;
        public long deadlineTime;
        public String description;
        public String imageUrl;
        
        //静态的Parcelable.Creator接口
        public static final Parcelable.Creator<UserCouponParcelable> CREATOR = new Creator<UserCouponParcelable>() {
                
                //创建出类的实例，并从Parcel中获取数据进行实例化
                public UserCouponParcelable createFromParcel(Parcel source) {
                        UserCouponParcelable userCoupon = new UserCouponParcelable();
                        userCoupon.name = source.readString();
                        userCoupon.description = source.readString();
                        userCoupon.imageUrl = source.readString();
                        userCoupon.mId = source.readLong();
                        userCoupon.beginTime = source.readLong();
                        userCoupon.deadlineTime = source.readLong();

                        return userCoupon;
                }

                public UserCouponParcelable[] newArray(int size) {
                        // TODO Auto-generated method stub
                        return new UserCouponParcelable[size];
                }

        };
        
        //
        @Override
        public int describeContents() {
                // TODO Auto-generated method stub
                return 0;
        }
        
        //将数据写入外部提供的Parcel中
        @Override
        public void writeToParcel(Parcel dest, int flags) {
                // TODO Auto-generated method stub
                dest.writeString(name);
                dest.writeString(description);
                dest.writeString(imageUrl);
                dest.writeLong(mId);
                dest.writeLong(beginTime);
                dest.writeLong(deadlineTime);
        }
}