package com.zhima.ui.common.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.View;

/**
 * @ClassName: CircleScaleView
 * @Description: 自定义,在一个圆形中根据不同的比例绘制出不同颜色不同大小的扇形块
 * @author yusonglin
 * @date 2012-7-12 
 */
public class CircleScaleView extends View {

	private Paint mSectorPaint;
	private RadialGradient mRadialGradient;
	/**
	 * 扇形颜色数组
	 */
	private int[] mColors;
	//比例数组
	private float[] mScales;
	//角度数组
	private float[] mAngles;
	//绘制文本颜色
	private int mTextColor;
	//绘制文本大小
	private int mTextSize;
	
	public CircleScaleView(Context context) {
		this(context, null);
	}
	
	public CircleScaleView(Context context,
			AttributeSet paramAttributeSet) {
		super(context, paramAttributeSet);
		this.mSectorPaint = new Paint();
		this.mSectorPaint.setAntiAlias(true); //抗锯齿    
	}

	protected void onDraw(Canvas paramCanvas) {
		drawCircle(paramCanvas);
	}
	
	/**
	 * 设置绘制文本颜色
	 * @param color 要设置的文本颜色
	 */
	public void setTextColor(int color){
		this.mTextColor = color;
	}
	
	/**
	 * 设置绘制文本大小
	 * @param textSize 
	 */
	public void setTextSize(int textSize){
		this.mTextColor = textSize;
	}
	
	/**
	 * 设置扇形颜色资源数组
	 * @param colors 
	 */
	public void setColors(int[] colors){
		this.mColors = colors;
	}
	
	/**
	* 利用传过来的参数数组 ,算出数组每个元素相对于总大小的比例 和 在圆形中所占的角度大小.
	* @param params 需要在在饼型中显示的参数数组
	*/
	public void setParams(float[] params){
		float sweepAngle;
		float totalNum = 0;
		
		for(float i:params){
			totalNum += i;
		}
		
		mScales = new float[params.length];
		mAngles = new float[params.length];
		for(int i=0;i<mScales.length;i++){
			mScales[i] = ((int)((params[i]/totalNum)*100))/100.0f;
			sweepAngle = (float) (mScales[i] * 360.0D);
			mAngles[i] = sweepAngle;
		}
	}
	
	/** 
	 * 绘制全部扇形和文本
	 * @param paramCanvas  画布
	 */
	private void drawCircle(Canvas paramCanvas){
		float sweepAngle; // 绘制度数
		float startAngle = 0 ;  //起始角度
		float rightCoordinate = getWidth() ; //view右边坐标
		float bottomCoordinate = getHeight() ;//view下边坐标
		
		//画文本的画笔
		Paint textPaint = new Paint();
		textPaint.setColor(mTextColor);
		textPaint.setTextSize(mTextSize);
		
		float radius = rightCoordinate<bottomCoordinate?rightCoordinate/2:bottomCoordinate/2;
		float distance = radius*2/3;
		//绘制扇形
		for(int i=0;i<mScales.length;i++){
			RectF localRectF = new RectF(0.0F, 0.0F, radius*2,radius*2);
			
			sweepAngle = mAngles[i];

			int colorFlag = i % (mColors.length);
			mRadialGradient = new RadialGradient(1, 1, 1, mColors[colorFlag],mColors[colorFlag], TileMode.MIRROR);
			
			this.mSectorPaint.setShader(mRadialGradient);
			if (i == (mScales.length - 1)) {
				sweepAngle = 360 - startAngle;
				startAngle = 360 - sweepAngle;
			}
			
			paramCanvas.drawArc(localRectF, startAngle, sweepAngle, true,mSectorPaint);// 顺时针效果
			startAngle += sweepAngle;
		}
		
		//绘制文本
		for(int i=0;i<mScales.length;i++){
			sweepAngle = mAngles[i];
			float angle = startAngle+(sweepAngle/2);
			Point point = getPoint(distance, angle);
			paramCanvas.drawText((mScales[i] * 100) + "%", point.x+radius-radius*4/15, point.y+radius,textPaint);
			startAngle += sweepAngle;
		}
		
	}
	
	/**
	 * 计算出相对于圆心的坐标
	 * @param distance  离圆心的距离
	 * @param angle  角度
	 * @return 封装了绘制文本坐标的对象
	 */
	private Point getPoint(float distance,float angle){
		
		float x = (float) (distance*Math.cos(getCircleAngle(angle)));
		float y = (float) (distance*Math.sin(getCircleAngle(angle)));
		
		Point point = new Point();
		point.x = x;
		point.y = y;
		
		return point;
	}
	
	private float getCircleAngle(float angle){
		return (float) ((angle/180)*Math.PI);
	}
	
	/**
	 * @ClassName: Point
	 * @Description: 内部类。封装绘制文本的坐标
	*/
	static class Point{
		float x;
		float y;
	}
}