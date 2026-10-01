package com.carrombot;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.os.Handler;
import android.os.Looper;
import java.nio.ByteBuffer;
public class ScreenCaptureHelper {
    private static MediaProjection projection;
    private static ImageReader imageReader;
    private static VirtualDisplay virtualDisplay;
    private static int screenW, screenH, dpi;
    private static volatile Bitmap latestFrame;
    public static void init(MediaProjection mp, int w, int h, int d) {
        projection = mp; screenW = w; screenH = h; dpi = d;
        imageReader = ImageReader.newInstance(w, h, PixelFormat.RGBA_8888, 2);
        virtualDisplay = projection.createVirtualDisplay("BotCapture",
            w, h, dpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader.getSurface(), null, null);
        imageReader.setOnImageAvailableListener(reader -> {
            Image image = reader.acquireLatestImage();
            if (image == null) return;
            Image.Plane[] planes = image.getPlanes();
            ByteBuffer buffer = planes[0].getBuffer();
            int rowStride = planes[0].getRowStride();
            int pixelStride = planes[0].getPixelStride();
            int bitmapW = rowStride/pixelStride;
            Bitmap bmp = Bitmap.createBitmap(bitmapW, h, Bitmap.Config.ARGB_8888);
            bmp.copyPixelsFromBuffer(buffer);
            if (bitmapW != w) {
                latestFrame = Bitmap.createBitmap(bmp, 0, 0, w, h);
                bmp.recycle();
            } else {
                if (latestFrame != null) latestFrame.recycle();
                latestFrame = bmp;
            }
            image.close();
        }, new Handler(Looper.getMainLooper()));
    }
    public static Bitmap getLatestFrame() { return latestFrame; }
    public static void release() {
        if (virtualDisplay != null) virtualDisplay.release();
        if (projection != null) projection.stop();
    }
}
