package com.carrombot;
import android.graphics.Bitmap;
import android.graphics.Color;
import java.util.ArrayList;
import java.util.List;
public class BallDetector {
    private final Bitmap bitmap;
    private final int screenW, screenH;
    private static final float BOARD_LEFT=0.05f,BOARD_RIGHT=0.95f,BOARD_TOP=0.10f,BOARD_BOTTOM=0.75f;
    private static final float STRIKER_TOP=0.75f,STRIKER_BOTTOM=0.90f;
    private static final int SAMPLE_STEP=4;
    public BallDetector(Bitmap bitmap, int screenW, int screenH) {
        this.bitmap=bitmap; this.screenW=screenW; this.screenH=screenH;
    }
    public List<int[]> findBalls() {
        List<int[]> candidates = new ArrayList<>();
        int startX=(int)(screenW*BOARD_LEFT),endX=(int)(screenW*BOARD_RIGHT);
        int startY=(int)(screenH*BOARD_TOP),endY=(int)(screenH*BOARD_BOTTOM);
        for (int x=startX;x<endX;x+=SAMPLE_STEP) {
            for (int y=startY;y<endY;y+=SAMPLE_STEP) {
                if (x>=bitmap.getWidth()||y>=bitmap.getHeight()) continue;
                int pixel=bitmap.getPixel(x,y);
                if ((isBlackBall(pixel)||isWhiteBall(pixel))&&isCircularRegion(x,y,pixel))
                    if (!hasCandidateNear(candidates,x,y,30))
                        candidates.add(new int[]{x,y});
            }
        }
        return candidates;
    }
    public int[] findStriker() {
        int startX=(int)(screenW*0.2f),endX=(int)(screenW*0.8f);
        int startY=(int)(screenH*STRIKER_TOP),endY=(int)(screenH*STRIKER_BOTTOM);
        for (int x=startX;x<endX;x+=SAMPLE_STEP) {
            for (int y=startY;y<endY;y+=SAMPLE_STEP) {
                if (x>=bitmap.getWidth()||y>=bitmap.getHeight()) continue;
                int pixel=bitmap.getPixel(x,y);
                if (isStriker(pixel)&&isCircularRegion(x,y,pixel))
                    return new int[]{x,y};
            }
        }
        return new int[]{screenW/2,(int)(screenH*0.82f)};
    }
    private boolean isBlackBall(int p){return Color.red(p)<60&&Color.green(p)<60&&Color.blue(p)<60;}
    private boolean isWhiteBall(int p){int r=Color.red(p),g=Color.green(p),b=Color.blue(p);return r>190&&g>185&&b>170&&Math.abs(r-g)<25;}
    private boolean isStriker(int p){return Color.red(p)>180&&Color.green(p)<80&&Color.blue(p)<80;}
    private boolean isCircularRegion(int cx,int cy,int tp) {
        int match=0,total=0,radius=10;
        for (int dx=-radius;dx<=radius;dx+=3) {
            for (int dy=-radius;dy<=radius;dy+=3) {
                if (dx*dx+dy*dy>radius*radius) continue;
                int nx=cx+dx,ny=cy+dy;
                if (nx<0||ny<0||nx>=bitmap.getWidth()||ny>=bitmap.getHeight()) continue;
                total++;
                if (colorMatch(bitmap.getPixel(nx,ny),tp,40)) match++;
            }
        }
        return total>0&&(float)match/total>0.55f;
    }
    private boolean colorMatch(int p1,int p2,int t){
        return Math.abs(Color.red(p1)-Color.red(p2))<t&&
               Math.abs(Color.green(p1)-Color.green(p2))<t&&
               Math.abs(Color.blue(p1)-Color.blue(p2))<t;
    }
    private boolean hasCandidateNear(List<int[]> list,int x,int y,int dist){
        for (int[] c:list){int dx=c[0]-x,dy=c[1]-y;if(dx*dx+dy*dy<dist*dist)return true;}
        return false;
    }
}
