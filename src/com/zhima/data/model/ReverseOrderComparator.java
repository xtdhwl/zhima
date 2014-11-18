/* 
* @Title: ReverseOrderComparator.java
* Created by liubingsr on 2012-8-11 上午11:27:26 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.data.model;

import java.util.Comparator;

/**
 * @ClassName: ReverseOrderComparator
 * @Description: 倒序比较器
 * @author liubingsr
 * @date 2012-8-11 上午11:27:26
 *
 */
public class ReverseOrderComparator<T extends BaseData> implements Comparator<T> {
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
//		long difference = left.getId() - right.getId();
//		if (difference > 0) {
//			return -1;
//		} else if (difference < 0) {
//			return 1;
//		} else {
//			return 0;
//		}
		Long leftId = left.getId(), rightId = right.getId();
		return rightId.compareTo(leftId);
	}
}
