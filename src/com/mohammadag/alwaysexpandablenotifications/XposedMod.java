package com.mohammadag.alwaysexpandablenotifications;

import android.app.Notification;
import android.app.Notification.Builder;
import android.os.Parcelable;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class XposedMod implements IXposedHookZygoteInit {
	protected CharSequence mContentText;

	@Override
	public void initZygote(StartupParam startupParam) throws Throwable {		
		try {
			XposedHelpers.findAndHookMethod(Notification.Builder.class, "build", new XC_MethodReplacement() {
				@Override
				protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
					Notification.Builder builder = (Builder) param.thisObject;
					Notification n = (Notification) XposedBridge.invokeOriginalMethod(param.method,
							param.thisObject, null);
					try {
						if (n.bigContentView == null) {
							CharSequence seq = mContentText;
							if (seq == null) {
								seq = (CharSequence) XposedHelpers.getAdditionalInstanceField(param.thisObject, "mContentText1");
							}

							if (seq == null) {
								seq = "MohammadAG messed up his module";
							}
							Notification.BigTextStyle bigBuilder = new Notification.BigTextStyle(builder);
							bigBuilder.bigText(seq);
							n = bigBuilder.build();
						}
					} catch (Throwable t) {
						t.printStackTrace();
					}

					return n;
				}
			});
		} catch (Throwable t) {
			t.printStackTrace();
		}

		try {
			XposedHelpers.findAndHookMethod(Notification.Builder.class, "setContentText", CharSequence.class, new XC_MethodHook() {
				protected void afterHookedMethod(MethodHookParam param) throws Throwable {
					mContentText = safeCharSequence((CharSequence) param.args[0]);
					XposedHelpers.setAdditionalInstanceField(param.thisObject, "mContentText1", mContentText);
				};
			});
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public static CharSequence safeCharSequence(CharSequence cs) {
		if (cs instanceof Parcelable) {
			return cs.toString();
		}

		return cs;
	}

}
