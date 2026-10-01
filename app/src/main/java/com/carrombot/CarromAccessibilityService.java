package com.carrombot;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.DisplayMetrics;
import android.view.accessibility.AccessibilityEvent;
public class CarromAccessibilityService extends AccessibilityService {
    private static boolean botEnabled=false;
    private Handler botHandler;
    private HandlerThread botThread;
    private boolean isRunning=false;
    private static final String CARROM_PACKAGE="com.miniclip.carrom";
    public static void setBotEnabled(boolean e){botEnabled=e;}
    @Override public void onAccessibilityEvent(AccessibilityEvent event){
        if(!botEnabled)return;
        String pkg=event.getPackageName()!=null?event.getPackageName().toString():"";
        if(pkg.equals(CARROM_PACKAGE)&&!isRunning)startBot();
    }
    @Override public void onInterrupt(){stopBot();}
    @Override public void onDestroy(){stopBot();super.onDestroy();}
    private void startBot(){
        isRunning=true;
        botThread=new HandlerThread("BotThread");
        botThread.start();
        botHandler=new Handler(botThread.getLooper());
        DisplayMetrics m=getResources().getDisplayMetrics();
        botHandler.post(new BotLoop(this,m.widthPixels,m.heightPixels));
    }
    private void stopBot(){isRunning=false;if(botThread!=null)botThread.quitSafely();}
    public void performStrikerSwipe(float x1,float y1,float x2,float y2,int ms){
        Path p=new Path();p.moveTo(x1,y1);p.lineTo(x2,y2);
        GestureDescription g=new GestureDescription.Builder()
            .addStroke(new GestureDescription.StrokeDescription(p,0,ms)).build();
        dispatchGesture(g,null,null);
    }
    public void performTap(float x,float y){
        Path p=new Path();p.moveTo(x,y);
        GestureDescription g=new GestureDescription.Builder()
            .addStroke(new GestureDescription.StrokeDescription(p,0,50)).build();
        dispatchGesture(g,null,null);
    }
}
