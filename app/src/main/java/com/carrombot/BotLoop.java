package com.carrombot;
import android.os.SystemClock;
import java.util.List;
public class BotLoop implements Runnable {
    private final CarromAccessibilityService service;
    private final int screenW, screenH;
    private static final int BOT_LOOP_DELAY_MS=800;
    public BotLoop(CarromAccessibilityService service,int screenW,int screenH){
        this.service=service;this.screenW=screenW;this.screenH=screenH;
    }
    @Override public void run(){
        while(true){
            try{
                android.graphics.Bitmap screen=ScreenCaptureHelper.getLatestFrame();
                if(screen==null){SystemClock.sleep(BOT_LOOP_DELAY_MS);continue;}
                BallDetector detector=new BallDetector(screen,screenW,screenH);
                List<int[]> balls=detector.findBalls();
                int[] striker=detector.findStriker();
                if(balls.isEmpty()||striker==null){SystemClock.sleep(BOT_LOOP_DELAY_MS);continue;}
                int[] target=findNearest(striker,balls);
                if(target==null){SystemClock.sleep(BOT_LOOP_DELAY_MS);continue;}
                AimResult aim=AimCalculator.calculate(striker[0],striker[1],target[0],target[1],screenW,screenH);
                executeStrike(aim);
                SystemClock.sleep(BOT_LOOP_DELAY_MS);
            }catch(Exception e){SystemClock.sleep(BOT_LOOP_DELAY_MS);}
        }
    }
    private int[] findNearest(int[] striker,List<int[]> balls){
        int[] best=null;double minD=Double.MAX_VALUE;
        for(int[] b:balls){double d=Math.sqrt(Math.pow(b[0]-striker[0],2)+Math.pow(b[1]-striker[1],2));if(d>50&&d<minD){minD=d;best=b;}}
        return best;
    }
    private void executeStrike(AimResult aim){
        float sx=aim.strikerX*screenW,sy=aim.strikerY*screenH;
        float px=(float)(sx-aim.force*Math.cos(aim.angle)*80);
        float py=(float)(sy-aim.force*Math.sin(aim.angle)*80);
        service.performTap(sx,sy);
        SystemClock.sleep(200);
        service.performStrikerSwipe(sx,sy,px,py,150);
    }
}
