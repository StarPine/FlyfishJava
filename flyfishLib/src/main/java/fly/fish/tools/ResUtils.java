package fly.fish.tools;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ResUtils {

    public static final String RESOURCE_TYPE_ID = "id";
    public static final String RESOURCE_TYPE_LAYOUT = "layout";
    public static final String RESOURCE_TYPE_DRAWABLE = "drawable";
    public static final String RESOURCE_TYPE_STRING = "string";
    public static final String RESOURCE_TYPE_STYLE = "style";
    public static final String RESOURCE_TYPE_COLOR = "color";
    public static final String RESOURCE_TYPE_ANIM = "anim";

    public static int getResourceID(Context context, String name, String type) {
        Resources localResources = context.getResources();
        String pkg = context.getPackageName();
        int id;
        try {
            id = localResources.getIdentifier(name, type, pkg);
        } catch (Exception e) {
            id = 0;
        }
        return id;
    }

    public static String getString(Context context, String resName) {
        String result = "";
        try {
            result = context.getResources().getString(getResourceID(context, resName, RESOURCE_TYPE_STRING));
        } catch (NotFoundException ignore) {
        }
        return result;
    }

    public static String getString(Context context, String resName, Object... formatArgs) {
        String result;
        try {
            result = context.getResources().getString(getResourceID(context, resName, RESOURCE_TYPE_STRING), formatArgs);
        } catch (NotFoundException e) {
            result = "";
        }
        return result;
    }

    public static int getStringID(Context context, String resName) {
        int result;
        try {
            result = getResourceID(context, resName, RESOURCE_TYPE_STRING);
        } catch (NotFoundException e) {
            result = 0;
        }
        return result;
    }

    @SuppressWarnings("deprecation")
    public static Drawable getDrawable(Context context, String resName) {
        Drawable result;
        try {
            result = context.getResources().getDrawable(getDrawableID(context, resName));
        } catch (NotFoundException e) {
            result = null;
        }
        return result;
    }

    public static int getDrawableID(Context context, String resName) {
        int result;
        try {
            result = getResourceID(context, resName, RESOURCE_TYPE_DRAWABLE);
        } catch (NotFoundException e) {
            result = 0;
        }
        return result;
    }

    public static Bitmap getBitmap(Context ctx, String resName) {
        return BitmapFactory.decodeResource(ctx.getResources(), getDrawableID(ctx, resName));
    }

    public static <T extends View> T getView(Activity act, String resName) {
        View result;
        try {
            result = act.findViewById(getResourceID(act, resName, RESOURCE_TYPE_ID));
        } catch (NotFoundException e) {
            result = null;
        }
        return (T) result;
    }


    public static <T extends View> T getRootView(Context ctx, String resName, ViewGroup viewGroup) {
        return getRootView(ctx, resName, viewGroup, false);
    }

    public static <T extends View> T getRootView(Context ctx, String resName, ViewGroup viewGroup, boolean attachToRoot) {
        View result;
        try {
            result = LayoutInflater.from(ctx).inflate(getResourceID(ctx, resName, RESOURCE_TYPE_LAYOUT), viewGroup, attachToRoot);
        } catch (NotFoundException e) {
            result = null;
        }
        return (T) result;
    }

    public static <T extends View> T getView(View rootView, String resName) {
        View result;
        try {
            result = rootView.findViewById(getResourceID(rootView.getContext(), resName, RESOURCE_TYPE_ID));
        } catch (Exception e) {
            result = null;
        }
        return (T) result;
    }

    public static int getColor(Context context, String resName) {
        return getColor(context, getColorID(context, resName));
    }

    public static int getColor(Context context, int colorID) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(colorID);
        } else {
            return context.getResources().getColor(colorID);
        }
    }

    public static ColorStateList getColorStateList(Context context, String resName) {
        int colorID = getColorID(context, resName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColorStateList(colorID);
        } else {
            return context.getResources().getColorStateList(colorID);
        }
    }

    public static int getViewID(Context context, String resName) {
        int result;
        try {
            result = getResourceID(context, resName, RESOURCE_TYPE_ID);
        } catch (NotFoundException e) {
            result = 0;
        }
        return result;
    }

    public static int getLayoutID(Context context, String resName) {
        int result;
        try {
            result = getResourceID(context, resName, RESOURCE_TYPE_LAYOUT);
        } catch (NotFoundException e) {
            result = 0;
        }
        return result;
    }

    public static int getStyleID(Context context, String resName) {
        int result;
        try {
            result = getResourceID(context, resName, RESOURCE_TYPE_STYLE);
        } catch (NotFoundException e) {
            result = 0;
        }
        return result;
    }

    public static int getAnimID(Context context, String resName) {
        int result;
        try {
            result = getResourceID(context, resName, RESOURCE_TYPE_ANIM);
        } catch (NotFoundException e) {
            result = 0;
        }
        return result;
    }

    public static int getColorID(Context context, String resName) {
        int result;
        try {
            result = getResourceID(context, resName, RESOURCE_TYPE_COLOR);
        } catch (NotFoundException e) {
            result = 0;
        }
        return result;
    }

    public static void showView(View view) {
        if (view == null) return;
        view.setVisibility(View.VISIBLE);
    }

    public static void hideView(View view, boolean gone) {
        if (view == null) return;
        view.setVisibility(gone ? View.GONE : View.INVISIBLE);
    }
}
