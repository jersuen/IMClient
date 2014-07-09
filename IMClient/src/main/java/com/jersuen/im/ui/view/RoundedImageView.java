package com.jersuen.im.ui.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import com.jersuen.im.R;

public class RoundedImageView extends ImageView {

	public static final String TAG = "RoundedImageView";
	public static final int DEFAULT_RADIUS = 0;
	public static final int DEFAULT_BORDER_WIDTH = 0;
	private static final ScaleType[] SCALE_TYPES = { ScaleType.MATRIX, ScaleType.FIT_XY, ScaleType.FIT_START, ScaleType.FIT_CENTER, ScaleType.FIT_END, ScaleType.CENTER, ScaleType.CENTER_CROP,
			ScaleType.CENTER_INSIDE };

	private int mCornerRadius = DEFAULT_RADIUS;
	private int mBorderWidth = DEFAULT_BORDER_WIDTH;
	private ColorStateList mBorderColor = ColorStateList.valueOf(RoundedDrawable.DEFAULT_BORDER_COLOR);
	private boolean mOval = false;
	private boolean mMutateBackground = false;

	private int mResource;
	private Drawable mDrawable;
	private Drawable mBackgroundDrawable;

	private ScaleType mScaleType;

	public RoundedImageView(Context context) {
		super(context);
	}

	public RoundedImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundedImageView, defStyle, 0);

		int index = a.getInt(R.styleable.RoundedImageView_android_scaleType, -1);
		if (index >= 0) {
			setScaleType(SCALE_TYPES[index]);
		} else {
			// default scaletype to FIT_CENTER
			setScaleType(ScaleType.FIT_CENTER);
		}

		mCornerRadius = a.getDimensionPixelSize(R.styleable.RoundedImageView_corner_radius, -1);
		mBorderWidth = a.getDimensionPixelSize(R.styleable.RoundedImageView_border_width, -1);

		// don't allow negative values for radius and border
		if (mCornerRadius < 0) {
			mCornerRadius = DEFAULT_RADIUS;
		}
		if (mBorderWidth < 0) {
			mBorderWidth = DEFAULT_BORDER_WIDTH;
		}

		mBorderColor = a.getColorStateList(R.styleable.RoundedImageView_border_color);
		if (mBorderColor == null) {
			mBorderColor = ColorStateList.valueOf(RoundedDrawable.DEFAULT_BORDER_COLOR);
		}

		mMutateBackground = a.getBoolean(R.styleable.RoundedImageView_mutate_background, false);
		mOval = a.getBoolean(R.styleable.RoundedImageView_oval, false);

		updateDrawableAttrs();
		updateBackgroundDrawableAttrs(true);

		a.recycle();
	}

	@Override
	protected void drawableStateChanged() {
		super.drawableStateChanged();
		invalidate();
	}

	/**
	 * Return the current scale type in use by this ImageView.
	 * 
	 * @attr ref android.R.styleable#ImageView_scaleType
	 * @see android.widget.ImageView.ScaleType
	 */
	@Override
	public ScaleType getScaleType() {
		return mScaleType;
	}

	/**
	 * Controls how the image should be resized or moved to match the size of
	 * this ImageView.
	 * 
	 * @param scaleType
	 *            The desired scaling mode.
	 * @attr ref android.R.styleable#ImageView_scaleType
	 */
	@Override
	public void setScaleType(ScaleType scaleType) {
		assert scaleType != null;

		if (mScaleType != scaleType) {
			mScaleType = scaleType;

			switch (scaleType) {
			case CENTER:
			case CENTER_CROP:
			case CENTER_INSIDE:
			case FIT_CENTER:
			case FIT_START:
			case FIT_END:
			case FIT_XY:
				super.setScaleType(ScaleType.FIT_XY);
				break;
			default:
				super.setScaleType(scaleType);
				break;
			}

			updateDrawableAttrs();
			updateBackgroundDrawableAttrs(false);
			invalidate();
		}
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		mResource = 0;
		mDrawable = RoundedDrawable.fromDrawable(drawable);
		updateDrawableAttrs();
		super.setImageDrawable(mDrawable);
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		mResource = 0;
		mDrawable = RoundedDrawable.fromBitmap(bm);
		updateDrawableAttrs();
		super.setImageDrawable(mDrawable);
	}

	@Override
	public void setImageResource(int resId) {
		if (mResource != resId) {
			mResource = resId;
			mDrawable = resolveResource();
			updateDrawableAttrs();
			super.setImageDrawable(mDrawable);
		}
	}

	@Override
	public void setImageURI(Uri uri) {
		super.setImageURI(uri);
		setImageDrawable(getDrawable());
	}

	private Drawable resolveResource() {
		Resources rsrc = getResources();
		if (rsrc == null) {
			return null;
		}

		Drawable d = null;

		if (mResource != 0) {
			try {
				d = rsrc.getDrawable(mResource);
			} catch (Exception e) {
				Log.w(TAG, "Unable to find resource: " + mResource, e);
				// Don't try again.
				mResource = 0;
			}
		}
		return RoundedDrawable.fromDrawable(d);
	}

	@Override
	public void setBackground(Drawable background) {
		setBackgroundDrawable(background);
	}

	private void updateDrawableAttrs() {
		updateAttrs(mDrawable);
	}

	private void updateBackgroundDrawableAttrs(boolean convert) {
		if (mMutateBackground) {
			if (convert) {
				mBackgroundDrawable = RoundedDrawable.fromDrawable(mBackgroundDrawable);
			}
			updateAttrs(mBackgroundDrawable);
		}
	}

	private void updateAttrs(Drawable drawable) {
		if (drawable == null) {
			return;
		}

		if (drawable instanceof RoundedDrawable) {
			((RoundedDrawable) drawable).setScaleType(mScaleType).setCornerRadius(mCornerRadius).setBorderWidth(mBorderWidth).setBorderColors(mBorderColor).setOval(mOval);
		} else if (drawable instanceof LayerDrawable) {
			// loop through layers to and set drawable attrs
			LayerDrawable ld = ((LayerDrawable) drawable);
			for (int i = 0, layers = ld.getNumberOfLayers(); i < layers; i++) {
				updateAttrs(ld.getDrawable(i));
			}
		}
	}

	@Override
	@Deprecated
	public void setBackgroundDrawable(Drawable background) {
		mBackgroundDrawable = background;
		updateBackgroundDrawableAttrs(true);
		super.setBackgroundDrawable(mBackgroundDrawable);
	}

	public int getCornerRadius() {
		return mCornerRadius;
	}

	public void setCornerRadius(int radius) {
		if (mCornerRadius == radius) {
			return;
		}

		mCornerRadius = radius;
		updateDrawableAttrs();
		updateBackgroundDrawableAttrs(false);
	}

	public int getBorderWidth() {
		return mBorderWidth;
	}

	public void setBorderWidth(int width) {
		if (mBorderWidth == width) {
			return;
		}

		mBorderWidth = width;
		updateDrawableAttrs();
		updateBackgroundDrawableAttrs(false);
		invalidate();
	}

	public int getBorderColor() {
		return mBorderColor.getDefaultColor();
	}

	public void setBorderColor(int color) {
		setBorderColors(ColorStateList.valueOf(color));
	}

	public ColorStateList getBorderColors() {
		return mBorderColor;
	}

	public void setBorderColors(ColorStateList colors) {
		if (mBorderColor.equals(colors)) {
			return;
		}

		mBorderColor = (colors != null) ? colors : ColorStateList.valueOf(RoundedDrawable.DEFAULT_BORDER_COLOR);
		updateDrawableAttrs();
		updateBackgroundDrawableAttrs(false);
		if (mBorderWidth > 0) {
			invalidate();
		}
	}

	public boolean isOval() {
		return mOval;
	}

	public void setOval(boolean oval) {
		mOval = oval;
		updateDrawableAttrs();
		updateBackgroundDrawableAttrs(false);
		invalidate();
	}

	public boolean isMutateBackground() {
		return mMutateBackground;
	}

	public void setMutateBackground(boolean mutate) {
		if (mMutateBackground == mutate) {
			return;
		}

		mMutateBackground = mutate;
		updateBackgroundDrawableAttrs(true);
		invalidate();
	}

    @SuppressWarnings("UnusedDeclaration")
    public static class RoundedDrawable extends Drawable {

        public static final String TAG = "RoundedDrawable";
        public static final int DEFAULT_BORDER_COLOR = Color.BLACK;

        private final RectF mBounds = new RectF();
        private final RectF mDrawableRect = new RectF();
        private final RectF mBitmapRect = new RectF();
        private final BitmapShader mBitmapShader;
        private final Paint mBitmapPaint;
        private final int mBitmapWidth;
        private final int mBitmapHeight;
        private final RectF mBorderRect = new RectF();
        private final Paint mBorderPaint;
        private final Matrix mShaderMatrix = new Matrix();

        private float mCornerRadius = 0;
        private boolean mOval = false;
        private float mBorderWidth = 0;
        private ColorStateList mBorderColor = ColorStateList.valueOf(DEFAULT_BORDER_COLOR);
        private ScaleType mScaleType = ScaleType.FIT_CENTER;

        public RoundedDrawable(Bitmap bitmap) {

            mBitmapWidth = bitmap.getWidth();
            mBitmapHeight = bitmap.getHeight();
            mBitmapRect.set(0, 0, mBitmapWidth, mBitmapHeight);

            mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mBitmapShader.setLocalMatrix(mShaderMatrix);

            mBitmapPaint = new Paint();
            mBitmapPaint.setStyle(Paint.Style.FILL);
            mBitmapPaint.setAntiAlias(true);
            mBitmapPaint.setShader(mBitmapShader);

            mBorderPaint = new Paint();
            mBorderPaint.setStyle(Paint.Style.STROKE);
            mBorderPaint.setAntiAlias(true);
            mBorderPaint.setColor(mBorderColor.getColorForState(getState(), DEFAULT_BORDER_COLOR));
            mBorderPaint.setStrokeWidth(mBorderWidth);
        }

        public static RoundedDrawable fromBitmap(Bitmap bitmap) {
            if (bitmap != null) {
                return new RoundedDrawable(bitmap);
            } else {
                return null;
            }
        }

        public static Drawable fromDrawable(Drawable drawable) {
            if (drawable != null) {
                if (drawable instanceof RoundedDrawable) {
                    // just return if it's already a RoundedDrawable
                    return drawable;
                } else if (drawable instanceof LayerDrawable) {
                    LayerDrawable ld = (LayerDrawable) drawable;
                    int num = ld.getNumberOfLayers();

                    // loop through layers to and change to RoundedDrawables if possible
                    for (int i = 0; i < num; i++) {
                        Drawable d = ld.getDrawable(i);
                        ld.setDrawableByLayerId(ld.getId(i), fromDrawable(d));
                    }
                    return ld;
                }

                // try to get a bitmap from the drawable and
                Bitmap bm = drawableToBitmap(drawable);
                if (bm != null) {
                    return new RoundedDrawable(bm);
                } else {
                    Log.w(TAG, "Failed to create bitmap from drawable!");
                }
            }
            return drawable;
        }

        public static Bitmap drawableToBitmap(Drawable drawable) {
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            }

            Bitmap bitmap;
            int width = Math.max(drawable.getIntrinsicWidth(), 1);
            int height = Math.max(drawable.getIntrinsicHeight(), 1);
            try {
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
            } catch (Exception e) {
                e.printStackTrace();
                bitmap = null;
            }

            return bitmap;
        }

        @Override
        public boolean isStateful() {
            return mBorderColor.isStateful();
        }

        @Override
        protected boolean onStateChange(int[] state) {
            int newColor = mBorderColor.getColorForState(state, 0);
            if (mBorderPaint.getColor() != newColor) {
                mBorderPaint.setColor(newColor);
                return true;
            } else {
                return super.onStateChange(state);
            }
        }

        private void updateShaderMatrix() {
            float scale;
            float dx;
            float dy;

            switch (mScaleType) {
                case CENTER:
                    mBorderRect.set(mBounds);
                    mBorderRect.inset((mBorderWidth) / 2, (mBorderWidth) / 2);

                    mShaderMatrix.set(null);
                    mShaderMatrix.setTranslate((int) ((mBorderRect.width() - mBitmapWidth) * 0.5f + 0.5f),
                            (int) ((mBorderRect.height() - mBitmapHeight) * 0.5f + 0.5f));
                    break;

                case CENTER_CROP:
                    mBorderRect.set(mBounds);
                    mBorderRect.inset((mBorderWidth) / 2, (mBorderWidth) / 2);

                    mShaderMatrix.set(null);

                    dx = 0;
                    dy = 0;

                    if (mBitmapWidth * mBorderRect.height() > mBorderRect.width() * mBitmapHeight) {
                        scale = mBorderRect.height() / (float) mBitmapHeight;
                        dx = (mBorderRect.width() - mBitmapWidth * scale) * 0.5f;
                    } else {
                        scale = mBorderRect.width() / (float) mBitmapWidth;
                        dy = (mBorderRect.height() - mBitmapHeight * scale) * 0.5f;
                    }

                    mShaderMatrix.setScale(scale, scale);
                    mShaderMatrix.postTranslate((int) (dx + 0.5f) + mBorderWidth,
                            (int) (dy + 0.5f) + mBorderWidth);
                    break;

                case CENTER_INSIDE:
                    mShaderMatrix.set(null);

                    if (mBitmapWidth <= mBounds.width() && mBitmapHeight <= mBounds.height()) {
                        scale = 1.0f;
                    } else {
                        scale = Math.min(mBounds.width() / (float) mBitmapWidth,
                                mBounds.height() / (float) mBitmapHeight);
                    }

                    dx = (int) ((mBounds.width() - mBitmapWidth * scale) * 0.5f + 0.5f);
                    dy = (int) ((mBounds.height() - mBitmapHeight * scale) * 0.5f + 0.5f);

                    mShaderMatrix.setScale(scale, scale);
                    mShaderMatrix.postTranslate(dx, dy);

                    mBorderRect.set(mBitmapRect);
                    mShaderMatrix.mapRect(mBorderRect);
                    mBorderRect.inset((mBorderWidth) / 2, (mBorderWidth) / 2);
                    mShaderMatrix.setRectToRect(mBitmapRect, mBorderRect, Matrix.ScaleToFit.FILL);
                    break;

                default:
                case FIT_CENTER:
                    mBorderRect.set(mBitmapRect);
                    mShaderMatrix.setRectToRect(mBitmapRect, mBounds, Matrix.ScaleToFit.CENTER);
                    mShaderMatrix.mapRect(mBorderRect);
                    mBorderRect.inset((mBorderWidth) / 2, (mBorderWidth) / 2);
                    mShaderMatrix.setRectToRect(mBitmapRect, mBorderRect, Matrix.ScaleToFit.FILL);
                    break;

                case FIT_END:
                    mBorderRect.set(mBitmapRect);
                    mShaderMatrix.setRectToRect(mBitmapRect, mBounds, Matrix.ScaleToFit.END);
                    mShaderMatrix.mapRect(mBorderRect);
                    mBorderRect.inset((mBorderWidth) / 2, (mBorderWidth) / 2);
                    mShaderMatrix.setRectToRect(mBitmapRect, mBorderRect, Matrix.ScaleToFit.FILL);
                    break;

                case FIT_START:
                    mBorderRect.set(mBitmapRect);
                    mShaderMatrix.setRectToRect(mBitmapRect, mBounds, Matrix.ScaleToFit.START);
                    mShaderMatrix.mapRect(mBorderRect);
                    mBorderRect.inset((mBorderWidth) / 2, (mBorderWidth) / 2);
                    mShaderMatrix.setRectToRect(mBitmapRect, mBorderRect, Matrix.ScaleToFit.FILL);
                    break;

                case FIT_XY:
                    mBorderRect.set(mBounds);
                    mBorderRect.inset((mBorderWidth) / 2, (mBorderWidth) / 2);
                    mShaderMatrix.set(null);
                    mShaderMatrix.setRectToRect(mBitmapRect, mBorderRect, Matrix.ScaleToFit.FILL);
                    break;
            }

            mDrawableRect.set(mBorderRect);
            mBitmapShader.setLocalMatrix(mShaderMatrix);
        }

        @Override
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);

            mBounds.set(bounds);

            updateShaderMatrix();
        }

        @Override
        public void draw(Canvas canvas) {

            if (mOval) {
                if (mBorderWidth > 0) {
                    canvas.drawOval(mDrawableRect, mBitmapPaint);
                    canvas.drawOval(mBorderRect, mBorderPaint);
                } else {
                    canvas.drawOval(mDrawableRect, mBitmapPaint);
                }
            } else {
                if (mBorderWidth > 0) {
                    canvas.drawRoundRect(mDrawableRect, Math.max(mCornerRadius, 0),
                            Math.max(mCornerRadius, 0), mBitmapPaint);
                    canvas.drawRoundRect(mBorderRect, mCornerRadius, mCornerRadius, mBorderPaint);
                } else {
                    canvas.drawRoundRect(mDrawableRect, mCornerRadius, mCornerRadius, mBitmapPaint);
                }
            }
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }

        @Override
        public void setAlpha(int alpha) {
            mBitmapPaint.setAlpha(alpha);
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
            mBitmapPaint.setColorFilter(cf);
            invalidateSelf();
        }

        @Override public void setDither(boolean dither) {
            mBitmapPaint.setDither(dither);
            invalidateSelf();
        }

        @Override public void setFilterBitmap(boolean filter) {
            mBitmapPaint.setFilterBitmap(filter);
            invalidateSelf();
        }

        @Override
        public int getIntrinsicWidth() {
            return mBitmapWidth;
        }

        @Override
        public int getIntrinsicHeight() {
            return mBitmapHeight;
        }

        public float getCornerRadius() {
            return mCornerRadius;
        }

        public RoundedDrawable setCornerRadius(float radius) {
            mCornerRadius = radius;
            return this;
        }

        public float getBorderWidth() {
            return mBorderWidth;
        }

        public RoundedDrawable setBorderWidth(int width) {
            mBorderWidth = width;
            mBorderPaint.setStrokeWidth(mBorderWidth);
            return this;
        }

        public int getBorderColor() {
            return mBorderColor.getDefaultColor();
        }

        public RoundedDrawable setBorderColor(int color) {
            return setBorderColors(ColorStateList.valueOf(color));
        }

        public ColorStateList getBorderColors() {
            return mBorderColor;
        }

        public RoundedDrawable setBorderColors(ColorStateList colors) {
            mBorderColor = colors != null ? colors : ColorStateList.valueOf(0);
            mBorderPaint.setColor(mBorderColor.getColorForState(getState(), DEFAULT_BORDER_COLOR));
            return this;
        }

        public boolean isOval() {
            return mOval;
        }

        public RoundedDrawable setOval(boolean oval) {
            mOval = oval;
            return this;
        }

        public ScaleType getScaleType() {
            return mScaleType;
        }

        public RoundedDrawable setScaleType(ScaleType scaleType) {
            if (scaleType == null) {
                scaleType = ScaleType.FIT_CENTER;
            }
            if (mScaleType != scaleType) {
                mScaleType = scaleType;
                updateShaderMatrix();
            }
            return this;
        }

        public Bitmap toBitmap() {
            return drawableToBitmap(this);
        }
    }
}
