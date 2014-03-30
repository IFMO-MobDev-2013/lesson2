package local.firespace.pic;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new ImageView(this));
	}
	class Image{
		private int[] pixels;
		private int width;
		private int height;

		public Image(int[] pixels, int width, int height) {
			this.pixels = pixels;
			this.width = width;
			this.height = height;

		}

		public int getWidth() {
			return this.width;
		}

		public int getHeight() {
			return this.height;
		}

		public Image fastScale(int newWidth, int newHeight) {
			int[] temp = new int[newWidth * newHeight];
			int xScale = (width << 16) / newWidth;
			int yScale = (height << 16) / newHeight;
			int x,y;
			for (int i = 0; i < newWidth; i++) {
				for (int j = 0; j < newHeight; j++) {
					x = (i*xScale) >> 16;
					y = (j*yScale) >> 16;
					temp[j*newWidth + i] = pixels[y*width + x];
				}
			}
			return new Image(temp, newWidth, newHeight);
		}
		private int currentBrightness() {
			int curr = 0;
			for (int i = 0; i < width * height; i++) {
				curr += Color.red(pixels[i]) + Color.blue(pixels[i]) + Color.green(pixels[i]);
			}
			return curr / (width*height*3);
		}

		public void changeBrightness() {
			int red, green, blue, alpha;
			int curr = currentBrightness();
			for (int i = 0; i < height * width; i++) {
				red = Color.red(pixels[i]);
				green = Color.green(pixels[i]);
				blue = Color.blue(pixels[i]);
				alpha = Color.alpha(pixels[i]);

				if (red + curr > 255) red = 255;
				else red += curr;
				if (blue + curr > 255) blue = 255;
				else blue += curr;
				if (green + curr > 255) green = 255;
				else green += curr;

				pixels[i] = Color.argb(alpha, red, green, blue);
			}
		}

		public Image clockwise() {
			int[] temp = new int[this.width * this.height];

			for (int i = 0; i < this.height; i++) {
				for (int j = 0; j < this.width; j++) {
					temp[(this.height) * j + (this.height - i - 1)] = this.pixels[i * this.width + j];
				}
			}
			return new Image(temp, this.height, this.width);
		}

		public Image bilinearInterpolation(int newWidth, int newHeight) {
			int[] temp = new int[newHeight*newWidth];
			int a,b,c,d, x ,y,iter,index;
			float xRatio = (width) / newWidth;
			float yRatio = (height) / newHeight;
			int xScale = (width << 16) / newWidth;
			int yScale = (height << 16) / newHeight;
			float xDiff, yDiff, red, blue, green;
			iter = 0;
			for (int i = 0; i < newHeight; i++) {
				for (int j = 0; j < newWidth; j++) {
					x = (j*xScale) >> 16;
					y = (i*yScale) >> 16;
					xDiff = (xRatio*j) - (int)(xRatio*j);
					yDiff = (yRatio*i) - (int)(yRatio*i);
					index = (y * width + x);
					a = pixels[index];
					if (index + height < height*width) b = pixels[index + height];
					else b = a;
					if (index + width < height*width) c = pixels[index + width];
					else c = a;
					if (index + height + width < height*width) d = pixels[index + height + width];
					else d = a;

					blue = (a&0xff)*(1-xDiff)*(1-yDiff) + (b&0xff)*(xDiff)*(1-yDiff) +
							(c&0xff)*(yDiff)*(1-xDiff) + (d&0xff)*(xDiff*yDiff);

					green = ((a>>8)&0xff)*(1-xDiff)*(1-yDiff) + ((b>>8)&0xff)*(xDiff)*(1-yDiff) +
							((c>>8)&0xff)*(yDiff)*(1-xDiff) + ((d>>8)&0xff)*(xDiff*yDiff);

					red = ((a>>16)&0xff)*(1-xDiff)*(1-yDiff) + ((b>>16)&0xff)*(xDiff)*(1-yDiff) +
							((c>>16)&0xff)*(yDiff)*(1-xDiff) + ((d>>16)&0xff)*(xDiff*yDiff);

					temp[iter++] = 0xff000000 | (((int)red)<<16)&0xff0000 | (((int)green)<<8)&0xff00 | (int)blue;
				}
			}
			return new Image(temp, newWidth, newHeight);
		}


	}
	class ImageView extends View {
		private Bitmap IMAGE;
		private int[] pixels;
		private int pictureIndex;
		private Image[] pictures = new Image[3];
		public ImageView(Context context) {
			super(context);
			IMAGE = BitmapFactory.decodeResource(getResources(), R.drawable.source);
			pixels = new int[IMAGE.getWidth() * IMAGE.getHeight()];
			IMAGE.getPixels(pixels, 0, IMAGE.getWidth(), 0, 0, IMAGE.getWidth(), IMAGE.getHeight());

			pictures[0] = new Image(pixels, IMAGE.getWidth(), IMAGE.getHeight());

			pictures[0] = pictures[0].clockwise();
			pictures[0].changeBrightness();
			pictures[1] = pictures[0].fastScale(405, 434);
			pictures[2] = pictures[0].bilinearInterpolation(405, 434);


			setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (pictureIndex == 1) pictureIndex = 2;
					else pictureIndex = 1;
					invalidate();
				}
			});
		}

		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawBitmap(pictures[pictureIndex].pixels, 0, pictures[pictureIndex].getWidth(), 0, 0, pictures[pictureIndex].getWidth(), pictures[pictureIndex].getHeight(), true, null);
		}
	}
}