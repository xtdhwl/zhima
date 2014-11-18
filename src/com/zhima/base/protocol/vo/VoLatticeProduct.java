/* 
 * @Title: VoLatticeshopProduct.java
 * Created by liubingsr on 2012-6-28 下午2:12:33 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.base.protocol.vo;

/**
* @ClassName: VoLatticeshopProduct
* @Description: 格子铺物品
* @author liubingsr
* @date 2012-8-18 下午1:54:30
*
*/
public final class VoLatticeProduct {
	public final static class ProductKind {
		private long id;

		public long getId() {
			return id;
		}
		public void setId(long id) {
			this.id = id;
		}
	}
	private long id = 0;
	/**
	 * 物品名称
	 */
	private String name = "";
	/**
	 * 物品描述
	 */
	private String description = "";
	/**
	 * 价格
	 */
	private Double price = 0.0;
	/**
	 * 数量
	 */
	private int count = 0;
	/**
	 * 交易方式描述
	 */
	private String dealType = "";
	/**
	 * 物品类别
	 */
//	private ProductKind kind;
	
	public VoLatticeProduct() {
//		kind = new ProductKind();
//		kind.setId(1);
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

//	public ProductKind getKind() {
//		return kind;
//	}
//	public void setKind(ProductKind kind) {
//		this.kind = kind;
//	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getDealType() {
		return dealType;
	}
	public void setDealType(String dealType) {
		this.dealType = dealType;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("name:" + name + ",");
		sb.append("description:" + description + ",");		
//		sb.append("kind:" + kind.id + ",");
		sb.append("price:" + price);
		return sb.toString();
	}
}
