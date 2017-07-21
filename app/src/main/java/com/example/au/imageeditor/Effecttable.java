package com.example.au.imageeditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;


/**
 * Created by Abhishek upadhyay on 18-09-2016.
 */
public class Effecttable {
    private Context context;
    private Bitmap bitmap;
    Utils utils = new Utils();
    Mat src;

    public Effecttable(Context mContext) {
        this.context = mContext;
        // this.bitmap=bitmaps;
        //this.src=scr;
    }
/*
    public static Bitmap doGreyscale(Bitmap src) {
        // constant factors
        final double GS_RED = 0.299;
        final double GS_GREEN = 0.587;
        final double GS_BLUE = 0.114;


        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        // pixel information
        int A, R, G, B;
        int pixel;

        // get image size
        int width = src.getWidth();
        int height = src.getHeight();

        // scan through every single pixel
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get one pixel color
                pixel = src.getPixel(x, y);
                // retrieve color of all channels
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                // take conversion up to one single value
                R = G = B = (int) (GS_RED * R + GS_GREEN * G + GS_BLUE * B);
                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        // return final image
        return bmOut;
    }
*/
    public Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public Bitmap sharpen(Bitmap bitmap) {
        /*
        final double weight = 15;

        double[][] SharpConfig = new double[][]{
                {0, -2, 0},
                {-2, weight, -2},
                {0, -2, 0}
        };
        ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
        convMatrix.applyConfig(SharpConfig);
        convMatrix.Factor = weight - 8;
        return ConvolutionMatrix.computeConvolution3x3(bitmap, convMatrix);
*/

        src = new Mat(bitmap.getHeight(),bitmap.getWidth(), CvType.CV_8UC1);
        Utils.bitmapToMat(bitmap,src);
        // src.convertTo(src,-1,1,50);
        // Mat kernel =new Mat(3,3,CvType.CV_32F);

        Imgproc.GaussianBlur(src, src, new Size(0,0), 10);
        Core.addWeighted(src, 1.5, src, -0.5, 0, src);

        //Core.transform(src,src,mmeankernel);
       // Imgproc.filter2D(src, src, -1, mmeankernel);
        Bitmap result = Bitmap.createBitmap(src.cols(),src.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(src,result);
        return result;
}

    public  Bitmap doHighlightImage(Bitmap src) {
        // create new bitmap, which will be painted and becomes result image
        Bitmap bmOut = Bitmap.createBitmap(src.getWidth() + 96, src.getHeight() + 96, Bitmap.Config.ARGB_8888);
        // setup canvas for painting
        Canvas canvas = new Canvas(bmOut);
        // setup default color
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);

        // create a blur paint for capturing alpha
        Paint ptBlur = new Paint();
        ptBlur.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL));
        int[] offsetXY = new int[2];
        // capture alpha into a bitmap
        Bitmap bmAlpha = src.extractAlpha(ptBlur, offsetXY);
        // create a color paint
        Paint ptAlphaColor = new Paint();
        ptAlphaColor.setColor(0xFFFFFFFF);
        // paint color for captured alpha region (bitmap)
        canvas.drawBitmap(bmAlpha, offsetXY[0], offsetXY[1], ptAlphaColor);
        // free memory
        bmAlpha.recycle();

        // paint the image source
        canvas.drawBitmap(src, 0, 0, null);

        // return out final image
        return bmOut;
    }

    public Bitmap applyMeanRemoval(Bitmap bitmap) {

       /* double[][] MeanRemovalConfig = new double[][] {
                { -1 , -1, -1 },
                { -1 ,  9, -1 },
                { -1 , -1, -1 }
        };
        ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
        convMatrix.applyConfig(MeanRemovalConfig);
        convMatrix.Factor = 1;
        convMatrix.Offset = 0;
        return ConvolutionMatrix.computeConvolution3x3(bitmap, convMatrix);
         */
        src = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC1);
        Utils.bitmapToMat(bitmap, src);
        // src.convertTo(src,-1,1,50);
        // Mat kernel =new Mat(3,3,CvType.CV_32F);

        Mat mmeankernel = new Mat(3, 3, CvType.CV_32F) {
            {
                put(0, 0, -1);
                put(0, 1, -1);
                put(0, 2, -1);

                put(1, 0, -1);
                put(1, 1, 9);
                put(1, 2, -1);

                put(2, 0, -1);
                put(2, 1, -1);
                put(2, 2, -1);
            }
        };

        //Core.transform(src,src,mmeankernel);
        Imgproc.filter2D(src, src, -1, mmeankernel);
        Bitmap result = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(src, result);
        return result;


    }

    public Bitmap bright(Bitmap bitmap) {
        // Mat  rgbaInnerWindow = new Mat();
        // utils.bitmapToMat(src,rgbaInnerWindow);
        //Mat rgba =new Mat();

        src = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC1);
        Utils.bitmapToMat(bitmap, src);
        src.convertTo(src, -1, 1, 50);
        Bitmap result = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(src, result);
        return result;


    }

    public Bitmap sepia(Bitmap bitmap) {
        // Mat  rgbaInnerWindow = new Mat();
        // utils.bitmapToMat(src,rgbaInnerWindow);
        //Mat rgba =new Mat();

        src = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC1);
        Utils.bitmapToMat(bitmap, src);
        // src.convertTo(src,-1,1,50);
        // Mat kernel =new Mat(3,3,CvType.CV_32F);
        Mat mSepiaKernel = new Mat(4, 4, CvType.CV_32F);
        mSepiaKernel.put(0, 0, /* R */0.189f, 0.769f, 0.393f, 0f);
        mSepiaKernel.put(1, 0, /* G */0.168f, 0.686f, 0.349f, 0f);
        mSepiaKernel.put(3, 0, /* A */0.000f, 0.000f, 0.000f, 1f);
        Core.transform(src, src, mSepiaKernel);
        Bitmap result = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(src, result);
        return result;


    }

    public Bitmap Sobelv(Bitmap bitmap) {
        src = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC1);
        Utils.bitmapToMat(bitmap, src);
        Imgproc.cvtColor(src, src, Imgproc.COLOR_RGB2GRAY);
        Mat msobelkernel = new Mat(3, 3, CvType.CV_32F) {
            {
                put(0, 0, -1);
                put(0, 1, 0);
                put(0, 2, 1);

                put(1, 0 - 2);
                put(1, 1, 0);
                put(1, 2, 2);

                put(2, 0, -1);
                put(2, 1, 0);
                put(2, 2, 1);
            }
        };
        Imgproc.filter2D(src, src, -1, msobelkernel);
        // Core.transform(src,src,msobelkernel);
        Bitmap result = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(src, result);
        return result;


    }

    public Bitmap Sobelh(Bitmap bitmap) {
        src = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC1);
        Utils.bitmapToMat(bitmap, src);
        Imgproc.cvtColor(src, src, Imgproc.COLOR_RGB2GRAY);
        Mat msobelkernel = new Mat(3, 3, CvType.CV_32F) {
            {
                put(0, 0, -1);
                put(0, 1, -2);
                put(0, 2, -2);

                put(1, 0, 0);
                put(1, 1, 0);
                put(1, 2, 0);

                put(2, 0, 1);
                put(2, 1, 2);
                put(2, 2, 1);
            }
        };
        Imgproc.filter2D(src, src, -1, msobelkernel);
        //  Core.transform(src, src, msobelkernel);
        Bitmap result = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(src, result);
        return result;
    }
   public Bitmap flip(Bitmap bitmap){
       src = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC1);
       Utils.bitmapToMat(bitmap, src);
       Core.flip(src, src, -1);
       Imgproc.cvtColor(src,src,Imgproc.COLOR_RGB2HSV);

       Bitmap result = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888);
       Utils.matToBitmap(src, result);
       return result;
   }
    public Bitmap Invert(Bitmap bitmap){
        src = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC1);
        Utils.bitmapToMat(bitmap, src);

       // Mat invertcolormatrix= new Mat(src.rows(),src.cols(), src.type(), new Scalar(255,255,255));

        //Core.subtract(invertcolormatrix, src, src);
        Core.bitwise_not ( src, src );
        Bitmap result = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(src, result);
        return result;
    }

}
