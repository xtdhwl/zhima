/* 
* @Title: ReverseOrderComparator.java
* Created by liubingsr on 2012-8-11 上午11:27:26 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.data.model;

import java.util.Comparator;

/**
 * @ClassName PositiveOrderComparator
 * @Description 正序比较器
 * @param <T>
 * @author jiang
 * @date 2013-1-26 下午02:33:13
 */
public class PositiveOrderComparator<T extends BaseData> implements Comparator<T> {
	@Override
	public int compare(T left, T right) {
		if (left == null && right == null) {
			return 0;
		}
		if (left != null && right == null) {
			return -1;
		}
		if (left == null && right != null) {
			return 1;
		}
		Long leftId = left.getId(), rightId = right.getId();
		return leftId.compareTo(rightId);
	}
}
