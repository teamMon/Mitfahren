package com.example.jpar4.mitfahren.func;
import android.content.Context;

import java.io.File;

public final class PicassoClear {

    public static boolean clearImageDiskCache(Context context) {
        File cache = new File(context.getApplicationContext().getCacheDir(), "picasso-cache");
        if (cache.exists() && cache.isDirectory()) {
            return deleteDir(cache);
        }
        return false;
    }

    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it
        return dir.delete();
    }

}