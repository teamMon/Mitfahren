package com.example.jpar4.mitfahren.func;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

/**
 * Created by jpar4 on 2017-09-12.
 */

public class PicassoTransformations {
    public static int targetWidth = 400;

    public static Transformation resizeTransformation = new Transformation() {
        @Override
        public Bitmap transform(Bitmap source) {
            double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
            int targetHeight = (int) (targetWidth * aspectRatio);
            Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
            if (result != source) {
                source.recycle();
            }
            return result;
        }

        @Override
        public String key() {
            return "resizeTransformation#" + System.currentTimeMillis();
        }
    };
}
