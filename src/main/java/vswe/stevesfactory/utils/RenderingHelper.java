package vswe.stevesfactory.utils;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public final class RenderingHelper {

    private RenderingHelper() {
    }

    public static BufferBuilder getRenderer() {
        return Tessellator.getInstance().getBuffer();
    }

    public static void bindTexture(ResourceLocation texture) {
        Minecraft.getInstance().getTextureManager().bindTexture(texture);
    }

    public static void useGradientGLStates() {
        GlStateManager.disableTexture();
        GlStateManager.disableAlphaTest();
        GlStateManager.enableBlend();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
    }

    public static void usePlainColorGLStates() {
        GlStateManager.disableTexture();
        GlStateManager.disableBlend();
    }

    public static void useTextureGLStates() {
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
        GlStateManager.color3f(1.0F, 1.0F, 1.0F);
    }

    public static void drawRect(int x, int y, int x2, int y2, int red, int green, int blue, int alpha) {
        BufferBuilder renderer = getRenderer();
        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        renderer.pos(x, y, 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.pos(x, y2, 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.pos(x2, y2, 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.pos(x2, y, 0.0D).color(red, green, blue, alpha).endVertex();
        Tessellator.getInstance().draw();
    }

    public static void drawRect(int x, int y, int x2, int y2, int color) {
        int alpha = (color >> 24) & 255;
        int red = (color >> 16) & 255;
        int green = (color >> 8) & 255;
        int blue = color & 255;
        drawRect(x, y, x2, y2, red, green, blue, alpha);
    }

    public static void drawColorLogic(int x, int y, int width, int height, int red, int green, int blue, GlStateManager.LogicOp logicOp) {
        GlStateManager.disableTexture();
        GlStateManager.enableColorLogicOp();
        GlStateManager.logicOp(logicOp);

        drawRect(x, y, width, height, red, green, blue, 255);

        GlStateManager.disableColorLogicOp();
        GlStateManager.enableTexture();
    }

    public static void drawThickBeveledBox(int x1, int y1, int x2, int y2, int thickness, int topLeftColor, int bottomRightColor, int fillColor) {
        if (fillColor != -1) {
            drawRect(x1 + thickness, y1 + thickness, x2 - thickness, y2 - thickness, fillColor);
        }
        drawRect(x1, y1, x2, y2, bottomRightColor);
        drawRect(x1, y1, x2 - thickness, y2 - thickness, topLeftColor);
    }

    public static void drawVerticalGradientRect(int x1, int y1, int x2, int y2, int color1, int color2) {
        float f = color1 >> 24 & 255;
        float f1 = color1 >> 16 & 255;
        float f2 = color1 >> 8 & 255;
        float f3 = color1 & 255;
        float f4 = color2 >> 24 & 255;
        float f5 = color2 >> 16 & 255;
        float f6 = color2 >> 8 & 255;
        float f7 = color2 & 255;

        GlStateManager.disableTexture();
        GlStateManager.enableBlend();
        GlStateManager.disableAlphaTest();
        GlStateManager.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(x2, y1, 0.0F).color(f1, f2, f3, f).endVertex();
        buffer.pos(x1, y1, 0.0F).color(f1, f2, f3, f).endVertex();
        buffer.pos(x1, y2, 0.0F).color(f5, f6, f7, f4).endVertex();
        buffer.pos(x2, y2, 0.0F).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlphaTest();
        GlStateManager.enableTexture();
    }

    public static void drawHorizontalGradientRect(int x1, int y1, int x2, int y2, int color1, int color2) {
        float f = color1 >> 24 & 255;
        float f1 = color1 >> 16 & 255;
        float f2 = color1 >> 8 & 255;
        float f3 = color1 & 255;
        float f4 = color2 >> 24 & 255;
        float f5 = color2 >> 16 & 255;
        float f6 = color2 >> 8 & 255;
        float f7 = color2 & 255;

        GlStateManager.disableTexture();
        GlStateManager.enableBlend();
        GlStateManager.disableAlphaTest();
        GlStateManager.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(x1, y1, 0.0F).color(f1, f2, f3, f).endVertex();
        buffer.pos(x1, y2, 0.0F).color(f1, f2, f3, f).endVertex();
        buffer.pos(x2, y2, 0.0F).color(f5, f6, f7, f4).endVertex();
        buffer.pos(x2, y1, 0.0F).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlphaTest();
        GlStateManager.enableTexture();
    }

    public static void drawTexture(int x1, int y1, int x2, int y2, ResourceLocation texture, float u1, float v1, float u2, float v2) {
        GlStateManager.enableTexture();
        GlStateManager.disableLighting();
        bindTexture(texture);

        BufferBuilder buffer = getRenderer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x1, y1, 0.0F).tex(u1, v1).endVertex();
        buffer.pos(x1, y2, 0.0F).tex(u1, v2).endVertex();
        buffer.pos(x2, y2, 0.0F).tex(u2, v2).endVertex();
        buffer.pos(x2, y1, 0.0F).tex(u2, v1).endVertex();
        Tessellator.getInstance().draw();
    }

    public static void drawCompleteTexture(int x1, int y1, int x2, int y2, ResourceLocation texture) {
        drawTexture(x1, y1, x2, y2, texture, 0.0F, 0.0F, 1.0F, 1.0F);
    }

    public static void drawTexturePortion(int x1, int y1, int x2, int y2, ResourceLocation texture, int textureWidth, int textureHeight, int tx, int ty, int portionWidth, int portionHeight) {
        float uFactor = 1 / textureWidth;
        float vFactor = 1 / textureHeight;
        int tx2 = tx + portionWidth;
        int ty2 = ty + portionHeight;
        drawTexture(x1, y1, x2, y2, texture, tx * uFactor, ty * vFactor, tx2 * uFactor, ty2 * vFactor);
    }

    public static void drawTexture256(int x1, int y1, int x2, int y2, ResourceLocation texture, int tx, int ty, int portionWidth, int portionHeight) {
        drawTexturePortion(x1, y1, x2, y2, texture, 256, 256, tx, ty, portionWidth, portionHeight);
    }

    public static void drawTransparentRect(int x1, int y1, int x2, int y2, int color) {
        float a = color >> 24 & 255;
        float r = color >> 16 & 255;
        float g = color >> 8 & 255;
        float b = color & 255;

        GlStateManager.disableTexture();
        GlStateManager.disableAlphaTest();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(x1, y1, 0.0F).color(r, g, b, a).endVertex();
        buffer.pos(x1, y2, 0.0F).color(r, g, b, a).endVertex();
        buffer.pos(x2, y2, 0.0F).color(r, g, b, a).endVertex();
        buffer.pos(x2, y1, 0.0F).color(r, g, b, a).endVertex();
        tessellator.draw();

        GlStateManager.disableBlend();
        GlStateManager.enableAlphaTest();
        GlStateManager.enableTexture();
        GlStateManager.clearCurrentColor();
    }

}
