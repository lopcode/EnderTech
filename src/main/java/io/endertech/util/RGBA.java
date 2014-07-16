package io.endertech.util;

public class RGBA
{
    public static final RGBA Red = new RGBA(1.0f, 0.0f, 0.0f, 1.0f);
    public static final RGBA Green = new RGBA(0.0f, 1.0f, 0.0f, 1.0f);
    public static final RGBA Blue = new RGBA(0.0f, 0.0f, 1.0f, 1.0f);
    public static final RGBA White = new RGBA(1.0f, 1.0f, 1.0f, 1.0f);
    public static final RGBA Black = new RGBA(0.0f, 0.0f, 0.0f, 1.0f);

    public float red = 0.0f;
    public float green = 0.0f;
    public float blue = 0.0f;
    public float alpha = 0.0f;

    public RGBA(float red, float green, float blue, float alpha)
    {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public RGBA copy()
    {
        return new RGBA(this.red, this.green, this.blue, this.alpha);
    }

    public RGBA setAlpha(float alpha)
    {
        RGBA copy = this.copy();
        copy.alpha = alpha;
        return copy;
    }
}
