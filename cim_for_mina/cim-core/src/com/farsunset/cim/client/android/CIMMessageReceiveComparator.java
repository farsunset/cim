package com.farsunset.cim.client.android;

import java.util.Comparator;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;

import com.farsunset.cim.nio.constant.CIMConstant;


/**
 * 消息接收activity的接收顺序排序，CIM_RECEIVE_ORDER倒序
 * @author 3979434
 *
 */
public class CIMMessageReceiveComparator  implements Comparator<OnCIMMessageListener>{

	Context mcontext;
	public CIMMessageReceiveComparator(Context ctx)
	{
		mcontext = ctx;
	}
	
	@Override
	public int compare(OnCIMMessageListener arg1, OnCIMMessageListener arg2) {
		 
		Integer order1  = CIMConstant.CIM_DEFAULT_MESSAGE_ORDER;
		Integer order2  = CIMConstant.CIM_DEFAULT_MESSAGE_ORDER;
		ActivityInfo info;
		if (arg1 instanceof Activity ) {
			
			try {
				 info = mcontext.getPackageManager() .getActivityInfo(((Activity)(arg1)).getComponentName(), PackageManager.GET_META_DATA);
				 if(info.metaData!=null)
				 {
					 order1 = info.metaData.getInt("CIM_RECEIVE_ORDER");
				 }
				 
		     } catch (Exception e) {}
		}
		
		if (arg1 instanceof Activity ) {
			try {
				 info = mcontext.getPackageManager() .getActivityInfo(((Activity)(arg2)).getComponentName(), PackageManager.GET_META_DATA);
				 if(info.metaData!=null)
				 {
					 order2 = info.metaData.getInt("CIM_RECEIVE_ORDER");
				 }
				 
		     } catch (Exception e) {}
		}
		
		return order2.compareTo(order1);
	}
	 

}
