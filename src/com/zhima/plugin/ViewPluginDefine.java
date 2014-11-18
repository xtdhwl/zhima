package com.zhima.plugin;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.consts.ZMConsts.ViewPluginKind;
import com.zhima.base.logger.Logger;

/**
 * @ClassName: ViewPluginDefine
 * @Description: 插件模型
 * @author liubingsr
 * @date 2013-1-5 下午2:44:45
 * 
 */
public class ViewPluginDefine {
	private static final String TAG = ViewPluginDefine.class.getSimpleName();

	/** plugin name */
	private String mName;
	/** 控件的位置 */
	private int mPosition;

	/** 类型 */
	private PluginType mType;
	/** 描述。插件标题栏 */
	private String mTitle;

	/**
	 * @ClassName: PluginType
	 * @Description: 插件类型
	 * @author liubingsr
	 * @date 2013-1-5 下午2:45:01
	 * 
	 */
	public enum PluginType {
		/** 默认的layoutViewPlugin */
		default_topbar,
		/** 默认的layoutViewPlugin */
		default_layout,
		/** 公告 */
		notice,
//		/** 商品活动模块 */
//		business_promotion,
//		/** 商品列表模块 */
//		business_product,
//		/** 空间顶部标题栏模块 */
//		business_topbar,
//		/** 商业空间周边推荐模块 */
//		business_recommended,
//		/** 知天使标题栏模块 */
//		idol_topbar,
//		/** 知天使展示 */
//		idol_album,
//		/** 知天使频道 */
//		idol_multimedia,
//		/** 知天使频道 */
//		idol_acqierement,
//		/** 知天使推荐 */
//		idol_recommended,
		//喜印
		/** 喜印标题栏模块 */
		couples_topbar,
		/** 喜印展示 */
		couples_album,
		/** 空间推荐 */
		couples_space,
		couples_diary,
		/** 喜印推荐 */
		couples_recommended,
		/** 公共空间标题栏 */
		elevator_topbar,
		/** 公共空间知趣 */
		elevator_joke,

		/** 誉玺标题栏 */
		reputation_topbar,
		/** 誉玺日志 */
		fame_diary,
		/** 誉玺空间 */
		reputation_space,
		/** 誉玺相册 */
		reputation_album,
		/** 誉玺周边 */
		reputation_recommended,

		//名玺
		zmcard_topbar,
		zmcard_album,
		zmcard_space,
		zmcard_diary,
		zmcard_recommended,
		/** 默认模块 */
		unknown;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		this.mTitle = title;
	}

	public PluginType getType() {
		return mType;
	}

	public void setType(PluginType type) {
		this.mType = type;
	}

	public int getPosition() {
		return mPosition;
	}

	public void setPosition(int mPosition) {
		this.mPosition = mPosition;
	}

	/**
	 * @Title: create
	 * @Description: 创建对象
	 * @param json
	 * @return ViewPluginDefine
	 */
	public static ViewPluginDefine create(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			return create(jsonObject);
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug(e.toString());
		}
		return null;
	}

	/**
	 * @Title: create
	 * @Description: 创建对象
	 * @param jsonObject
	 * @return ViewPluginDefine
	 */
	public static ViewPluginDefine create(JSONObject jsonObject) {
		try {
			//模块名称
			String pluginName = jsonObject.getString("name");
			//模块名称描述
			String description = jsonObject.getString("description");
			//模块位置
			int position = jsonObject.getInt("position");

			ViewPluginDefine plugin = new ViewPluginDefine();
			plugin.setName(pluginName);
			plugin.setTitle(description);
			plugin.setType(ViewPluginKind.getViewPluginType(pluginName));
			plugin.setPosition(position);
			return plugin;
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug(e.toString());
		}
		return null;
	}

	@Override
	public String toString() {
		return "ViewPluginDefine [mName=" + mName + ", mPosition=" + mPosition + ", mType=" + mType + ", mTitle="
				+ mTitle + "]";
	}
}
