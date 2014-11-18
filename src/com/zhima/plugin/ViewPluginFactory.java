package com.zhima.plugin;

import com.zhima.base.error.ViewPluginException;
import com.zhima.base.logger.Logger;
import com.zhima.plugin.space.DefalutTopbarPluginController;
import com.zhima.plugin.space.NoticePluginController;
import com.zhima.plugin.space.NoticeViewPlugin;
import com.zhima.plugin.space.common.controller.RecommendedPluginController;
import com.zhima.plugin.space.common.viewplugin.DefaultLayoutViewPlugin;
import com.zhima.plugin.space.common.viewplugin.RecommendedViewPlugin;
import com.zhima.plugin.space.common.viewplugin.SpaceTopbarViewPlugin;
import com.zhima.plugin.space.couples.CouplesHeaderViewPlugin;
import com.zhima.plugin.space.couples.CouplesSpaceViewPlugin;
import com.zhima.plugin.space.couples.controller.CouplesHeaderPluginController;
import com.zhima.plugin.space.couples.controller.CouplesSapcePluginController;
import com.zhima.plugin.space.couples.controller.CouplesTopbarPluginController;
import com.zhima.plugin.space.reputationseal.ReputationAcqierementViewPlugin;
import com.zhima.plugin.space.reputationseal.ReputationHeaderViewPlugin;
import com.zhima.plugin.space.reputationseal.ReputationSpaceViewPlugin;
import com.zhima.plugin.space.reputationseal.controller.RepPluginDiaryController;
import com.zhima.plugin.space.reputationseal.controller.ReputationHeaderPluginController;
import com.zhima.plugin.space.reputationseal.controller.ReputationSapcePluginController;
import com.zhima.plugin.space.reputationseal.controller.ReputationTopbarPluginController;
import com.zhima.plugin.space.zmcard.activity.ZMCardHeaderViewPlugin;
import com.zhima.plugin.space.zmcard.activity.ZMCardSpaceViewPlugin;
import com.zhima.plugin.space.zmcard.controller.ZMCardHeaderPluginController;
import com.zhima.plugin.space.zmcard.controller.ZMCardSapcePluginController;
import com.zhima.plugin.space.zmcard.controller.ZMCardTopbarPluginController;
import com.zhima.ui.activity.BaseActivity;

/**
 * @ClassName: ViewPluginFactory
 * @Description: 创建ViewPlugin实例
 * @author luqilong
 * @date 2013-1-3 上午10:00:41
 */

public class ViewPluginFactory {

	private static final String TAG = ViewPluginFactory.class.getSimpleName();

	public static String createCoupleConfigJson() {
		String couple_json = "{style:\"couples_style\",\"items\":[{name:\"couples_topbar_plugin\",position:1,description:喜印空间},"
				+ "{name:\"couples_album_plugin\",position:20,description:喜印相册},"
				+ "{name:\"couples_space_plugin\",position:25,description:空间推荐},"
				+ "{name:\"couples_diary_plugin\",position:30,description:誉玺日志},"
				+ "{name:\"couples_recommended_plugin\",position:50,description:周围推荐}]}";
		return couple_json;
	}

	public static String createFameConfigJson() {
		String reputation_json = "{style:\"reputation_style\",\"items\":[{name:\"reputation_topbar_plugin\",position:1,description:誉玺空间},"
				+ "{name:\"reputation_album_plugin\",position:2,description:誉玺相册},"
				+ "{name:\"reputation_space_plugin\",position:3,description:誉玺空间},"
				+ "{name:\"fame_diary_plugin\",position:4,description:誉玺日志},"
				+ "{name:\"reputation_recommended_plugin\",position:5,description:周围推荐}]}";

		return reputation_json;
	}

	public static String createZMCardConfigJson() {
		String zmcard_json = "{style:\"zmcard_style\",\"items\":[{name:\"zmcard_topbar_plugin\",position:1,description:名玺空间},"
				+ "{name:\"zmcard_album_plugin\",position:2,description:名玺相册},"
				+ "{name:\"zmcard_space_plugin\",position:3,description:名玺空间},"
				+ "{name:\"zmcard_diary_plugin\",position:4,description:名玺日志},"
				+ "{name:\"zmcard_recommended_plugin\",position:5,description:周围推荐}]}";

		return zmcard_json;
	}

//	public static String createIdolConfigJson() {
//		String idol_json = "{style:\"idol_style\",\"items\":[{name:\"idol_topbar_plugin\",position:1,description:知天使空间},"
//				+ "{name:\"notice_plugin\",position:10,description:公告},"
//				+ "{name:\"idol_album_plugin\",position:20,description:知天使相册},"
//				+ "{name:\"idol_multimedia_plugin\",position:30,description:知天使影像},"
//				+ "{name:\"idol_acqierement_plugin\",position:40,description:知天使频道},"
//				+ "{name:\"idol_recommended_plugin\",position:50,description:周边推荐}]}";
//
//		return idol_json;
//	}

//	//注意使用模块.xml.id 问题  在多个模块中避免出现重复id
//	String json = null;
//	String business_json = "{style:\"business_style\",\"items\":[{name:\"business_topbar_plugin\",position:1,description:商业空间,visibility:visibility},"
//			+ " {name:\"business_product_plugin\",position:2,description:商品列表,visibility:visibility},"
//			+ "{name:\"business_promotion_plugin\",position:3,description:商品活动,visibility:visibility},"
//			+ "{name:\"business_recommended_plugin\",position:4,description:周边推荐,visibility:visibility},"
//			+ "{name:\"idol_recommended_plugin\",position:5,description:周边推荐,visibility:invisibility}]}";
//
//	//使用插件实现这个知天使空间
//	//知天使acqierement_plugin
//	String idol_json = "{style:\"idol_style\",\"items\":[{name:\"idol_topbar_plugin\",position:1,description:知天使空间},"
//			+ "{name:\"notice_plugin\",position:10,description:公告},"
//			+ "{name:\"idol_album_plugin\",position:20,description:知天使相册},"
//			+ "{name:\"idol_multimedia_plugin\",position:30,description:知天使影像},"
//			+ "{name:\"idol_acqierement_plugin\",position:40,description:知天使频道},"
//			+ "{name:\"idol_recommended_plugin\",position:50,description:周边推荐}]}";
//	//知趣elevator_plugin
//	String elevator_json = "{style:\"elevator_style\",\"items\":[{name:\"elevator_topbar_plugin\",position:1,description:公共空间},"
//			+ "{name:\"idol_recommended_plugin\",position:2,description:周边推荐},"
//			+ "{name:\"elevator_joke_plugin\",position:3,description:知趣}]}";

	/**
	 * @Title: createViewPlugin
	 * @Description: 创建ViewPlugin对象
	 * @param activity
	 * @param pluginVo
	 * @return BaseViewPlugin
	 */
	public static BaseViewPlugin createViewPlugin(BaseActivity activity, ViewPluginDefine pluginVo) {
		//在模块中每个模块ViewPlugin都有一个控制器.
		//如模块控制器无逻辑可以使用默认控制器

		BaseViewPlugin commonViewPlugin = createCommonViewPlugin(activity, pluginVo);
		if (commonViewPlugin != null) {
			Logger.getInstance(TAG).debug("创建公共插件成功");
			return commonViewPlugin;
		}

		BaseViewPlugin coupleViewPlugin = createCoupleViewPlugin(activity, pluginVo);
		if (coupleViewPlugin != null) {
			Logger.getInstance(TAG).debug("创建喜迎插件成功");
			return coupleViewPlugin;
		}

		BaseViewPlugin famePlugin = createFameViewPlugin(activity, pluginVo);
		if (famePlugin != null) {
			Logger.getInstance(TAG).debug("创建誉玺日志插件成功");
			return famePlugin;
		}

		BaseViewPlugin zmcardPlugin = createZMCardViewPlugin(activity, pluginVo);
		if (zmcardPlugin != null) {
			Logger.getInstance(TAG).debug("创建名印插件成功");
			return zmcardPlugin;
		}

//		BaseViewPlugin idolViewPlugin = createIdolViewPlugin(activity, pluginVo);
//		if (idolViewPlugin != null) {
//			Logger.getInstance(TAG).debug("创建知天使插件成功");
//			return idolViewPlugin;
//		}

//		BaseViewPlugin businessPlugin = createBusinessViewPlugin(activity, pluginVo);
//		if (businessPlugin != null) {
//			Logger.getInstance(TAG).debug("创建商业插件成功");
//			return businessPlugin;
//		}
//
//		BaseViewPlugin elevatorPlugin = createElevatorViewPlugin(activity, pluginVo);
//		if (elevatorPlugin != null) {
//			Logger.getInstance(TAG).debug("创建公共空间插件成功");
//			return elevatorPlugin;
//		}
		throw new ViewPluginException("创建插件失败，插件类型未实现。" + pluginVo);
		//		return null;
	}

	//名印
	private static BaseViewPlugin createZMCardViewPlugin(BaseActivity activity, ViewPluginDefine pluginVo) {
		switch (pluginVo.getType()) {
		case zmcard_topbar:
			//默认topbar
			SpaceTopbarViewPlugin zmcardTopbarViewPlugin = new SpaceTopbarViewPlugin(activity);
			zmcardTopbarViewPlugin.init(new ZMCardTopbarPluginController(activity, zmcardTopbarViewPlugin));
			zmcardTopbarViewPlugin.setTitle(pluginVo.getTitle());
			Logger.getInstance(TAG).debug("创建名印-topbar成功");
			return zmcardTopbarViewPlugin;

		case zmcard_album:
			//相册
			ZMCardHeaderViewPlugin zmcardHeaderViewPlugin = new ZMCardHeaderViewPlugin(activity);
			zmcardHeaderViewPlugin.init(new ZMCardHeaderPluginController(activity, zmcardHeaderViewPlugin));
			//			prodectViewPlugin.setTitle(pluginVo.getTitle());
			return zmcardHeaderViewPlugin;
		case zmcard_space:
			//空间
			ZMCardSpaceViewPlugin zmcardSpaceViewPlugin = new ZMCardSpaceViewPlugin(activity);
			zmcardSpaceViewPlugin.init(new ZMCardSapcePluginController(activity, zmcardSpaceViewPlugin));
			//			prodectViewPlugin.setTitle(pluginVo.getTitle());
			return zmcardSpaceViewPlugin;
		case zmcard_diary:
			//日志
			ReputationAcqierementViewPlugin defalutLayoutViewPlugin = new ReputationAcqierementViewPlugin(activity);
			defalutLayoutViewPlugin.init(new RepPluginDiaryController(activity, defalutLayoutViewPlugin));
			//			prodectViewPlugin.setTitle(pluginVo.getTitle());
			return defalutLayoutViewPlugin;
		case zmcard_recommended:
			//空间
			RecommendedViewPlugin recommendedViewPlugin = new RecommendedViewPlugin(activity);
			recommendedViewPlugin.init(new RecommendedPluginController(activity, recommendedViewPlugin));
			recommendedViewPlugin.setTitle(pluginVo.getTitle());
			return recommendedViewPlugin;
		}
		return null;
	}

	//玉印
	private static BaseViewPlugin createFameViewPlugin(BaseActivity activity, ViewPluginDefine pluginVo) {
		switch (pluginVo.getType()) {
		case reputation_topbar:
			//默认topbar
			SpaceTopbarViewPlugin reputationTopbarViewPlugin = new SpaceTopbarViewPlugin(activity);
			reputationTopbarViewPlugin.init(new ReputationTopbarPluginController(activity, reputationTopbarViewPlugin));
			reputationTopbarViewPlugin.setTitle(pluginVo.getTitle());
			return reputationTopbarViewPlugin;
		case fame_diary:
			//默认模块布局
			ReputationAcqierementViewPlugin defalutLayoutViewPlugin = new ReputationAcqierementViewPlugin(activity);
			defalutLayoutViewPlugin.init(new RepPluginDiaryController(activity, defalutLayoutViewPlugin));
			//			prodectViewPlugin.setTitle(pluginVo.getTitle()y);
			return defalutLayoutViewPlugin;
		case reputation_album:
			//相册
			ReputationHeaderViewPlugin reputationHeaderViewPlugin = new ReputationHeaderViewPlugin(activity);
			reputationHeaderViewPlugin.init(new ReputationHeaderPluginController(activity, reputationHeaderViewPlugin));
			//			prodectViewPlugin.setTitle(pluginVo.getTitle());
			return reputationHeaderViewPlugin;
		case reputation_space:
			//空间
			ReputationSpaceViewPlugin reputationSpaceViewPlugin = new ReputationSpaceViewPlugin(activity);
			reputationSpaceViewPlugin.init(new ReputationSapcePluginController(activity, reputationSpaceViewPlugin));
			//			prodectViewPlugin.setTitle(pluginVo.getTitle());
			return reputationSpaceViewPlugin;
		case reputation_recommended:
			//空间
			RecommendedViewPlugin recommendedViewPlugin = new RecommendedViewPlugin(activity);
			recommendedViewPlugin.init(new RecommendedPluginController(activity, recommendedViewPlugin));
			recommendedViewPlugin.setTitle(pluginVo.getTitle());
			return recommendedViewPlugin;
		}
		return null;
	}

	//喜印
	private static BaseViewPlugin createCoupleViewPlugin(BaseActivity activity, ViewPluginDefine pluginVo) {
		switch (pluginVo.getType()) {
		case couples_topbar:
			//喜印标题栏
			SpaceTopbarViewPlugin coupleTopbarViewPlugin = new SpaceTopbarViewPlugin(activity);
			coupleTopbarViewPlugin.init(new CouplesTopbarPluginController(activity, coupleTopbarViewPlugin));
			coupleTopbarViewPlugin.setTitle(pluginVo.getTitle());
			return coupleTopbarViewPlugin;
		case couples_album:
			//喜印相册
			CouplesHeaderViewPlugin couplesHeaderViewPlugin = new CouplesHeaderViewPlugin(activity);
			couplesHeaderViewPlugin.init(new CouplesHeaderPluginController(activity, couplesHeaderViewPlugin));
//			couplesHeaderViewPlugin.setTitle(pluginVo.getTitle());
			return couplesHeaderViewPlugin;
		case couples_space:
			//喜印相册
			CouplesSpaceViewPlugin spaceViewPlugin = new CouplesSpaceViewPlugin(activity);
			spaceViewPlugin.init(new CouplesSapcePluginController(activity, spaceViewPlugin));
//			couplesHeaderViewPlugin.setTitle(pluginVo.getTitle());
			return spaceViewPlugin;
		case couples_diary:
			//默认模块布局
			ReputationAcqierementViewPlugin defalutLayoutViewPlugin = new ReputationAcqierementViewPlugin(activity);
			defalutLayoutViewPlugin.init(new RepPluginDiaryController(activity, defalutLayoutViewPlugin));
			//			prodectViewPlugin.setTitle(pluginVo.getTitle());
			return defalutLayoutViewPlugin;
		case couples_recommended:
			RecommendedViewPlugin recommendedViewPlugin = new RecommendedViewPlugin(activity);
			recommendedViewPlugin.init(new RecommendedPluginController(activity, recommendedViewPlugin));
			recommendedViewPlugin.setTitle(pluginVo.getTitle());
			return recommendedViewPlugin;
		}
		return null;
	}

	//---------------------------------------------------------------------------------------------------------------
	//知天使
//	private static BaseViewPlugin createIdolViewPlugin(BaseActivity activity, ViewPluginDefine pluginVo) {
//		switch (pluginVo.getType()) {
//		case idol_topbar:
//			//知天使标题栏
//			SpaceTopbarViewPlugin idolTopbarViewPlugin = new SpaceTopbarViewPlugin(activity);
//			idolTopbarViewPlugin.init(new IdolTopbarPluginController(activity, idolTopbarViewPlugin));
//			idolTopbarViewPlugin.setTitle(pluginVo.getTitle());
//			return idolTopbarViewPlugin;
//		case idol_album:
//			//知天使空间
//			SpaceHeaderViewPlugin spaceHeaderViewPlugin = new SpaceHeaderViewPlugin(activity);
//			//使用知天使控制器
//			spaceHeaderViewPlugin.init(new IdolHeaderPluginController(activity, spaceHeaderViewPlugin));
//			//			spaceHeaderViewPlugin.setTitle(pluginVo.getTitle());
//			return spaceHeaderViewPlugin;
//		case idol_multimedia:
//			//知天使影像
//			IdolMultimediaViewPlugin multimediaViewPlugin = new IdolMultimediaViewPlugin(activity);
//			multimediaViewPlugin.init(new IdolMultimediaPluginController(activity, multimediaViewPlugin));
//			multimediaViewPlugin.setTitle(pluginVo.getTitle());
//			return multimediaViewPlugin;
//		case idol_acqierement:
//			//知天使频道
//			IdolAcqierementViewPlugin acqierementViewPlugin = new IdolAcqierementViewPlugin(activity);
//			acqierementViewPlugin.init(new IdolAcqierementPluginController(activity, acqierementViewPlugin));
//			acqierementViewPlugin.setTitle(pluginVo.getTitle());
//			return acqierementViewPlugin;
//		case idol_recommended:
//			//知天使频道
//			IdolRecommendedViewPlugin idolRecommendedViewPlugin = new IdolRecommendedViewPlugin(activity);
//			idolRecommendedViewPlugin.init(new IdolRecommendedPluginController(activity, idolRecommendedViewPlugin));
//			idolRecommendedViewPlugin.setTitle(pluginVo.getTitle());
//			return idolRecommendedViewPlugin;
//		}
//		return null;
//	}

	//公共空间
//	private static BaseViewPlugin createElevatorViewPlugin(BaseActivity activity, ViewPluginDefine pluginVo) {
//		switch (pluginVo.getType()) {
//		case elevator_topbar:
//			//公共空间topbar
//			SpaceTopbarViewPlugin elevatorTopbarViewPlugin = new SpaceTopbarViewPlugin(activity);
//			elevatorTopbarViewPlugin.init(new ElevatorTopbarPluginController(activity, elevatorTopbarViewPlugin));
//			elevatorTopbarViewPlugin.setTitle(pluginVo.getTitle());
//			return elevatorTopbarViewPlugin;
//		case elevator_joke:
//			//公共频道
//			ElevatorJokeLayoutViewPlugin elevatorJokeLayoutViewPlugin = new ElevatorJokeLayoutViewPlugin(activity);
//			elevatorJokeLayoutViewPlugin.init(new ElevatorJokeViewPluginController(activity,
//					elevatorJokeLayoutViewPlugin));
//			elevatorJokeLayoutViewPlugin.setTitle(pluginVo.getTitle());
//			return elevatorJokeLayoutViewPlugin;
//		}
//		return null;
//	}

	//商业
//	private static BaseViewPlugin createBusinessViewPlugin(BaseActivity activity, ViewPluginDefine pluginVo) {
//		switch (pluginVo.getType()) {
//		case business_topbar:
//			//商业空间topbar
//			SpaceTopbarViewPlugin businessTopbarViewPlugin = new SpaceTopbarViewPlugin(activity);
//			businessTopbarViewPlugin.init(new BusinessTopbarPluginController(activity, businessTopbarViewPlugin));
//			businessTopbarViewPlugin.setTitle(pluginVo.getTitle());
//			return businessTopbarViewPlugin;
//		case business_product:
//			//商业空间活动模块
//			BusinessProductViewPlugin prodectViewPlugin = new BusinessProductViewPlugin(activity);
//			prodectViewPlugin.init(new BusinessProductPluginController(activity, prodectViewPlugin));
//			prodectViewPlugin.setTitle(pluginVo.getTitle());
//			return prodectViewPlugin;
//		case business_promotion:
//			//商业空间商品模块
//			BusinessPromotionViewPlugin promotionViewPlugin = new BusinessPromotionViewPlugin(activity);
//			promotionViewPlugin.init(new BusinessPromotionPluginController(activity, promotionViewPlugin));
//			promotionViewPlugin.setTitle(pluginVo.getTitle());
//			return promotionViewPlugin;
//		case business_recommended:
//			//商业空间商品模块
//			BusinessRecommendedViewPlugin businessRecommendedViewPlugin = new BusinessRecommendedViewPlugin(activity);
//			businessRecommendedViewPlugin.init(new BusinessRecommendedPluginController(activity,
//					businessRecommendedViewPlugin));
//			businessRecommendedViewPlugin.setTitle(pluginVo.getTitle());
//			return businessRecommendedViewPlugin;
//		}
//		return null;
//	}

	//公用布局
	private static BaseViewPlugin createCommonViewPlugin(BaseActivity activity, ViewPluginDefine pluginVo) {
		switch (pluginVo.getType()) {
		case default_layout:
			//默认模块布局
			DefaultLayoutViewPlugin defalutLayoutViewPlugin = new DefaultLayoutViewPlugin(activity);
			defalutLayoutViewPlugin.init(new DefalutLayoutPluginController(activity, defalutLayoutViewPlugin));
			//			prodectViewPlugin.setTitle(pluginVo.getTitle());
			return defalutLayoutViewPlugin;
		case default_topbar:
			//默认模块布局
			SpaceTopbarViewPlugin defalutTopbarViewPlugin = new SpaceTopbarViewPlugin(activity);
			defalutTopbarViewPlugin.init(new DefalutTopbarPluginController(activity, defalutTopbarViewPlugin));
			defalutTopbarViewPlugin.setTitle(pluginVo.getTitle());
			return defalutTopbarViewPlugin;
		case notice:
			//默认模块布局
			NoticeViewPlugin noticeViewPlugin = new NoticeViewPlugin(activity);
			noticeViewPlugin.init(new NoticePluginController(activity, noticeViewPlugin));
			noticeViewPlugin.setTitle(pluginVo.getTitle());
			return noticeViewPlugin;
		}
		return null;
	}
	//
}
