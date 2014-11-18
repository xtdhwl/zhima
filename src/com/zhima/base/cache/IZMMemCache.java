package com.zhima.base.cache;

import java.util.ArrayList;
/**
* @ClassName: IZMMemCache
* @Description: 内存缓存接口
* @author liubingsr
* @date 2012-6-14 下午4:40:09
*
* @param <K>
* @param <V>
*/
public interface IZMMemCache<K, V> {   
    public void add(K key, V value);
    public void remove(K key);    
    public V get(K key);
    public int getSize();
    public ArrayList<K> getKeys();
    public void clear();
}
