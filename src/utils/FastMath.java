package utils;

public class FastMath {
	public static final float DEG2RAD, RAD2DEG;
	private static final float[] sin, cos;
	private static final float[] asin, acos;
	private static final int SIN_COUNT = 360;
	public static final float ONEQTR_PI = (float) (Math.PI / 4.0f);
	public static final float THRQTR_PI = 3.0f * (float) (Math.PI / 4.0f);
	public static final float PI = 3.14159f;

	static {
		DEG2RAD = (float) Math.PI / 180.0f;
		RAD2DEG = 180.0f / (float) Math.PI;

		sin = new float[SIN_COUNT];
		cos = new float[SIN_COUNT];
		asin = new float[SIN_COUNT];
		acos = new float[SIN_COUNT];

		double arg = 0.0;
		for (int i = 0; i < SIN_COUNT; i++) {
			arg = (i + 0.5) * DEG2RAD;
			sin[i] = (float) Math.sin(arg);
			cos[i] = (float) Math.cos(arg);
			asin[i] = (float) Math.asin(sin[i]);
			acos[i] = (float) Math.acos(cos[i]);
		}

		// Four cardinal directions (credits: Nate)
		for (int i = 0; i < 360; i += 90) {
			arg = i * DEG2RAD;
			sin[i] = (float) Math.sin(arg);
			cos[i] = (float) Math.cos(arg);
			asin[i] = (float) Math.asin(sin[i]);
			acos[i] = (float) Math.acos(cos[i]);
		}
	}

	// Don't pass 0,0 to params (incorrect result from 0 division prevention)
	public static float atan2(float x, float y) {
		//http://pubs.opengroup.org/onlinepubs/009695399/functions/atan2.html
		//Volkan SALMA
		float r, angle;
		float abs_y = Math.abs(y) + 1e-10f; // kludge to prevent 0/0 condition
		if (x < 0.0f) {
			r = (x + abs_y) / (abs_y - x);
			angle = THRQTR_PI;
		}
		else {
			r = (x - abs_y) / (x + abs_y);
			angle = ONEQTR_PI;
		}
		angle += (0.1963f * r * r - 0.9817f) * r;
		/*
		 * if ( y < 0.0f ) return( -angle ); // negate if in quad III or IV else return( angle );
		 */
		return angle;
	}

	public static final float asin(float rad) {
		return asin[(int) (rad * RAD2DEG)];
	}

	public static final float acos(float rad) {
		return acos[(int) (rad * RAD2DEG)];
	}

	public static final float sin(float rad) {
		return sin[(int) (rad * RAD2DEG)];
	}

	public static final float cos(float rad) {
		return cos[(int) (rad * RAD2DEG)];
	}

	public static final float asinDeg(float deg) {
		return asin[(int) (deg + 0.5f)];
	}

	public static final float acosDeg(float deg) {
		return acos[(int) (deg + 0.5f)];
	}

	public static final float sinDeg(float deg) {
		return sin[(int) (deg + 0.5f)];
	}

	public static final float cosDeg(float deg) {
		return cos[(int) (deg + 0.5f)];
	}

	public static double fastSqrt(double num) {
		// Magic
		return Double.longBitsToDouble(((Double.doubleToLongBits(num) - (1l << 52)) >> 1) + (1l << 61));
	}

	public static float normalizeAngle(float angle) {
		float a = angle % 360;
		a = a < 0 ? a + 360.0f : a;
		return a;
	}
	
	public static int nextPowerOfTwo(int num){
		return Integer.highestOneBit(num) << 1;
	}
}
