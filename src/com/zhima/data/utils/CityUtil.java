package com.zhima.data.utils;


/**
 * 	  默认城市id的组成是 省行政区字段(2位十六进制)+市行政区字段(2位十六进制)+区县行政区字段(2位十六进制)+区域字段(2位十六进制)+子区域字段(2位十六进制)+ 地区深度字段(1位)
 *	  比如 
 *    北京市朝阳区 0x01010500004  
 *    丰台区       0x01010600004 
 *    五指山市     0x14020000003
 *    取出朝阳区下面所有区域的条件:
 *	  where city_id > 0x01010500004 and city < 0x01010600004
 *	  取出朝阳区下所有区块的条件是
 *    where city_id > 0x01010500004 and city < 0x01010600004 and city_id&0xFF0 = 0
 *    取出北京市下所有的区县的条件是
 *    where id > 0x01010000002 and id < 0x01020000002 and id&0xFF000 = 0
 *    0xFFFF0 这个数字的值可以通过subrangeCond函数计算出来
 *    如果通过常量来
 *    取省的集合 province 使用条件 where id&0x0FF0000000 = 0
 *    取城市的集合 city 使用条件 where id&0x00FFFF0 = 0 AND id&0x00FF000000>0
 *    取区县的集合 di+
 
 *    
 *    strict 使用条件 where id&0x0FF000 = 0
 *    取区域的集合 block 使用 where id&0x0FF0 = 0
 *    
 *    地区深度字段(1位) 表示的是这个city_id代表的是一个省,还是一个市,一个区县.
 *    如果代表全国,则深度是1
 *    如果代表省,则深度是2
 *    代表市,深度是3 比如石家庄市
 *    代表区县,深度是4 比如朝阳区
 *    代表区域,深度是5 比如望京
 *    代表子区域,深度是6 比如大山子
 *    
 *    查询city的SQL是:
 *    select HEX(id) as id, HEX(parent_id) as parent_id,t.cityName as name ,t.ename	from city t	where 1=1
 *    SELECT HEX(t1.id) as id, HEX(t1.parent_id) as parent_id,t1.cityName as name ,t1.ename FROM city t1 LEFT JOIN city t2 ON t1.parent_id=t2.id WHERE t2.cityName LIKE '%河北%'
 *    
 *    查询所有区下面的商业区的SQL是(比如朝阳区下面的亚运村、燕莎、望京):
 *    select * from city t where t.id & 0xF = 5
 *    
 *    查询所有区的SQL是(比如海淀、朝阳):
 *    select * from city t where t.id & 0xF = 4
 *    
 *    查询所有的市的SQL是(比如唐山市):
 *    select * from city t where t.id & 0xF = 3
 *    
 *    查询所有的省的SQL是(比如河北省):
 *    select * from city t where t.id & 0xF = 2
 * @author yindj
 *
 */
public class CityUtil {
	
	public static final long CONTRY_ID = 0x01L;
	
	public static final long MIN_PROVINCE_ID_ONLY = 0x1000000000L;
	public static final long MIN_CITY_ID_ONLY =     0x10000000L;
	public static final long MIN_DISTRICT_ID_ONLY = 0x100000L;
	public static final long MIN_BLOCK_ID_ONLY =    0x1000L;
	public static final long MIN_SUBBLOCK_ID_ONLY = 0x10L;
	
	public static final int DEPTH_COUNTRY_ID = 0x01;
	public static final int DEPTH_PROVINCE_ID = 0x02;
	public static final int DEPTH_CITY_ID = 0x03;
	public static final int DEPTH_DISTRICT_ID = 0x04;
	public static final int DEPTH_BLOCK_ID = 0x05;
	public static final int DEPTH_SUBBLOCK_ID = 0x06;
	
	public static final long MIN_PROVINCE_ID =      0x100000000FL;
	public static final long MIN_CITY_ID =          0xFF1000000FL;
	public static final long MIN_DISTRICT_ID =      0xFFFF10000FL;
	public static final long MIN_BLOCK_ID =         0xFFFFFF1000L;
	public static final long MIN_SUBBLOCK_ID =      0xFFFFFFFFFFL;
	
	public static final long MAX_PROVINCE_ID_ONLY = 0xFF000000000L;
	public static final long MAX_CITY_ID_ONLY =     0xFF0000000L;
	public static final long MAX_DISTRICT_ID_ONLY = 0xFF00000L;
	public static final long MAX_BLOCK_ID_ONLY =    0xFF000L;
	public static final long MAX_SUBBLOCK_ID_ONLY = 0xFF0L;
	
	public static final long MAX_PROVINCE_ID =      0xFF00000000FL;
	public static final long MAX_CITY_ID =          0xFFFF000000FL;
	public static final long MAX_DISTRICT_ID =      0xFFFFFF0000FL;
	public static final long MAX_BLOCK_ID =         0xFFFFFFFF00FL;
	public static final long MAX_SUBBLOCK_ID =      0xFFFFFFFFFFFL;
	
	/**
	 * 按位分拆一个10进制的数字
	 * @param source
	 * @param begin
	 * @param end
	 * @return
	 */
	public static Long getDecimalPart(Long source,int begin,int end){
		long t = source % (long)Math.pow(10, begin);
		t = t / (long)Math.pow(10, end);
		return t;
	}

	public static void printCityId(Long cityid) {
		System.out.print("depth="+Long.toHexString(getDepth(cityid)));
		System.out.print(", real_id="+Long.toHexString(getRealId(cityid)));
		System.out.print(", province_id="+Long.toHexString(getRealProvinceId(cityid)));
		System.out.print(", city_id="+Long.toHexString(getRealCityId(cityid)));
		System.out.print(", district_id="+Long.toHexString(getRealDistrictId(cityid)));
		System.out.print(", block_id="+Long.toHexString(getRealBlockId(cityid)));
		System.out.println(", sub_block_id="+Long.toHexString(getRealSubBlockId(cityid)));
	}
	//test
	public static void main(String[] args){
		//printCityId(0x1010050004L);
		System.out.println("expect:");
		System.out.println("depth=6, real_id=89, province_id=1, city_id=23, district_id=45, block_id=67, sub_block_id=89");
		System.out.println("actual:");
		printCityId(0x01234567896L);
		long[] subrs = subrange(0x01234567894L);
		System.out.println("expect:");
		System.out.println("sub start at 0x01234567894L, sub stop at 0x01234667894L");
		System.out.println("actual:");
		System.out.print("sub start at 0x0"+Long.toHexString(subrs[0])+"L");
		System.out.println(", sub stop at 0x0"+Long.toHexString(subrs[1])+"L");
		long pid = upid(0x01234567896L);
		System.out.println("expect:");
		System.out.println("upid = 0x01234567005L");
		System.out.println("actual:");
		System.out.println("upid = 0x0"+Long.toHexString(pid)+"L");
		long bid = mkBrotherId(0x01234567005L);
		System.out.println("expect:");
		System.out.println("bid = 0x01234568005L");
		System.out.println("actual:");
		System.out.println("bid = 0x0"+Long.toHexString(bid)+"L");
		bid = mkChildId(0x01234567005L);
		System.out.println("expect:");
		System.out.println("childId = 0x01234567016L");
		System.out.println("actual:");
		System.out.println("childId = 0x0"+Long.toHexString(bid)+"L");
		long[] ids = fullUpid(0x01234567016L);
		System.out.println("");
		System.out.print("fullpath=");
		for (int i = 0; i < ids.length; i++) {
			System.out.print(Long.toHexString(ids[i])+"L,");
		}
		System.out.println("");
		System.out.println("except=  1L,1000000002L,1230000003L,1234500004L,1234567005L,");
		ids = fullUpidAndSelf(0x01234567016L);
		System.out.print("fullpathWithSelf=");
		for (int i = 0; i < ids.length; i++) {
			System.out.print(Long.toHexString(ids[i])+"L,");
		}
		System.out.println("");
		
		System.out.println("except=          1L,1000000002L,1230000003L,1234500004L,1234567005L,1234567016L,");
	}

	

	/**
	 * @param cityid
	 * @return  表示省行政区的那段id.
	 */
	public static long getRealProvinceId(long cityId){
		//depth = 2
		return (cityId & MAX_PROVINCE_ID_ONLY)/MIN_PROVINCE_ID_ONLY;// getDecimalPart(cityid,15,12);
	}
	
	/**
	 * 
	 * @param cityId
	 * @return 从cityid中获取出真正表示城市行政区的那段id
	 */
	public static long getRealCityId(long cityId){
		//depth = 3
		return (cityId & MAX_CITY_ID_ONLY)/MIN_CITY_ID_ONLY;//getDecimalPart(cityid,12,9);
	}
	
	/**
	 * @param cityid
	 * @return 表示区县行政区的那段id.
	 */
	public static long getRealDistrictId(long cityId){
		//depth = 4
		return (cityId & MAX_DISTRICT_ID_ONLY)/MIN_DISTRICT_ID_ONLY;//getDecimalPart(cityid,9,6);
	}
	
	/**
	 * @param cityid
	 * @return  表示地块的那段id.
	 */
	public static long getRealBlockId(long cityId){
		//depth = 5
		return (cityId & MAX_BLOCK_ID_ONLY)/MIN_BLOCK_ID_ONLY;//getDecimalPart(cityid,3,0);
	}
	/**
	 * @param cityid
	 * @return  表示子区域的那段id..
	 */
	public static long getRealSubBlockId(long cityId){
		//depth = 6
		return (cityId & MAX_SUBBLOCK_ID_ONLY)/MIN_SUBBLOCK_ID_ONLY; //getDecimalPart(cityid,12,9);
	}
	
	/**
	 * @param cityid
	 * @return cityid的实际有效长度,返回 如果 1是全国, 2是省, 3是市, 4是区县, 5是区域, 6是子区域
	 */
	public static int getDepth(long cityId){
		return (int)cityId & 0xF;
	}
	/**
	 * 真正其作用的最后一级id,比如北京市朝阳区 0x40101050000 ,返回代表朝阳区的5
	 * @param cityId
	 * @return 真正有效的id
	 */
	public static int getRealId(long cityId){
		int depth = getDepth(cityId);
		long d = (long)Math.pow(0x100, 6-depth);
		long w = d * 0xFF0;
		return (int)((cityId & w)/(d * 0x10));
	}
	

	/**
	 * 查询一个区域子区域的id范围
	 * @param cityId 父区域的id
	 * @return long[],其中long[0]表示cityid的最小值,long[1]表示cityid的最大值
	 */
	public static long[] subrange(Long cityId) {
		int pd = getDepth(cityId);
		long[] ret = new long[2];
		//使用switch优化这里的性能
		ret[0] = cityId;
		switch (pd) {
		case 1: // 全国
			ret[1] = MAX_SUBBLOCK_ID;
			break;
		case 2:// 省
			ret[1] = cityId + MIN_PROVINCE_ID_ONLY;
			break;
		case 3: //市
			ret[1] = cityId + MIN_CITY_ID_ONLY;
			break;
		case 4: //区县
			ret[1] = cityId + MIN_DISTRICT_ID_ONLY;
			break;
		case 5: //区域
			ret[1] = cityId + MIN_BLOCK_ID_ONLY;
			break;
		case 6: //子区域
			ret[1] = cityId + MIN_SUBBLOCK_ID_ONLY;
			break;
		}
		
		//ret[1] = cityId + (long)Math.pow(0xFF, 6-pd)*0x10L; 
		return ret;

	}
	
	/**
	 * 查询一个城市id的父id
	 * @param cityId 目标城市id
	 * @return 父id,注意,全国的父id是0x0L,省的父id是全国0x01L.
	 */
	public static long upid(long cityId) {
		int dp = (int) getDepth(cityId);
		long pid = 0x0L;
		switch (dp) {
		case 1: // 全国的父是0x0L
			pid = 0x0L;
			break;
		case 2:// 省的父是全国0x01L
			pid = 0x01L;
			break;
		case 3:
			pid = (cityId & MAX_PROVINCE_ID) - 1;
			break;
		case 4:
			pid = (cityId & MAX_CITY_ID) - 1;
			break;
		case 5:
			pid = (cityId & MAX_DISTRICT_ID) - 1;
			break;
		case 6:
			pid = (cityId & MAX_BLOCK_ID) - 1;
			break;
		}
		return pid;
	}
	/**
	 * 返回父id和其他上级id
	 * @param id
	 * @return id的数组,长度是上级的长度.其中数组[0]是全国的id,数组[1]是省的id,数组[2]是市的id....,不包含自己的id
	 */
	public static long[] fullUpid(long id) {
		if(id<1){
			return new long[]{};
		}
		int dp = getDepth(id);
		long[] pids = new long[dp - 1];
		long pid = id;
		for (int i = 0; i < dp-1; i++) {
			pid = upid(pid);
			pids[dp-i-2] = pid;
		}
		return pids;
	}
	/**
	 * 返回父id和其他上级id
	 * @param id
	 * @return id的数组,长度是上级的长度.其中数组[0]是全国的id,数组[1]是省的id,数组[2]是市的id....,包含自己的id
	 */
	public static long[] fullUpidAndSelf(long id){
		
		int dp = getDepth(id);
		long[] pids = new long[dp];
		long pid = id;
		for (int i = 0; i < dp-1; i++) {
			pid = upid(pid);
			pids[dp-i-2] = pid;
		}
		pids[dp-1] = id;
		return pids;
	}
	/**
	 * 创建一个brother的id,当兄弟id为空时使用
	 * @param brotherId 兄弟id
	 * @return
	 */
	public static long mkChildId(long parentId) {
		int dp = getDepth(parentId);
		long nbrotherId = 0;
		switch (dp) {
		case 1: // 全国
			nbrotherId = MIN_PROVINCE_ID_ONLY;
			break;
		case 2:// 省
			nbrotherId = parentId + MIN_CITY_ID_ONLY + 1;
			break;
		case 3:
			nbrotherId = parentId + MIN_DISTRICT_ID_ONLY + 1;
			break;
		case 4:
			nbrotherId = parentId + MIN_BLOCK_ID_ONLY + 1;
			break;
		case 5:
			nbrotherId = parentId + MIN_SUBBLOCK_ID_ONLY + 1;
			break;
		}
		return nbrotherId;
	}
	/**
	 * 创建一个brother的id
	 * @param brotherId 兄弟id
	 * @return
	 */
	public static long mkBrotherId(long brotherId) {
		int dp = getDepth(brotherId);
		long nbrotherId = 0;
		switch (dp) {
		case 1: // 全国的是0x0L
			nbrotherId = 0x0L;
			break;
		case 2:// 省的父是全国0x01L
			nbrotherId = brotherId + MIN_PROVINCE_ID_ONLY;
			break;
		case 3:
			nbrotherId = brotherId + MIN_CITY_ID_ONLY;
			break;
		case 4:
			nbrotherId = brotherId + MIN_DISTRICT_ID_ONLY;
			break;
		case 5:
			nbrotherId = brotherId + MIN_BLOCK_ID_ONLY;
			break;
		case 6:
			nbrotherId = brotherId + MIN_SUBBLOCK_ID_ONLY;
			break;
		}
		return nbrotherId;
	}
}
