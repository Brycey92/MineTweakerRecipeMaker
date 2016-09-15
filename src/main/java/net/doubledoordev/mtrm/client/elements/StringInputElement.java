package net.doubledoordev.mtrm.client.elements;

import com.google.common.collect.ImmutableList;
import net.doubledoordev.mtrm.Helper;
import net.doubledoordev.mtrm.client.ClientHelper;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.lwjgl.input.Keyboard;

import java.util.List;

/**
 * @author Dries007
 */
public class StringInputElement extends ButtonElement
{
    private final String emptyDisplayText;
    protected String text = "";
    protected int blinkTimer = 0;

    public StringInputElement(GuiElementCallback callback, boolean optional, String displayText)
    {
        super(callback, optional, displayText);
        this.emptyDisplayText = displayText;
    }

    private void calcHeight()
    {
        if (!isFocused())
        {
            height = defaultHeight;
            if (text.isEmpty()) displayText = emptyDisplayText;
            else displayText = Helper.truncate(text.replace('\n', '↲'), 30);
        }
        else height = (Math.max(ClientHelper.split(fontRendererObj, text, width).size(), 1) * fontRendererObj.FONT_HEIGHT + 6);
        resizeCallback();
    }

    @Override
    public void setFocus(boolean focus)
    {
        boolean oldFocus = this.isFocused();
        super.setFocus(focus);
        if (oldFocus != focus) calcHeight();
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        if (!isFocused()) super.draw(mouseX, mouseY, partialTicks);
        else
        {
            int right = posX + width;
            int bottom = posY + height;
            drawRect(posX, posY, right, bottom, 0xFF000000);

            ClientHelper.drawSplit(fontRendererObj, text, width, 0xFFFFFF, posX, posY);

            if (blinkTimer / 6 % 2 == 0) drawVerticalLine((int)fontRendererObj.posX, (int)fontRendererObj.posY - 1, (int)fontRendererObj.posY + fontRendererObj.FONT_HEIGHT - 1, 0xFFFFFFFF);
        }
    }

    @Override
    public void update()
    {
        if (!isFocused())
        {
            super.update();
            blinkTimer = 0;
        }
        else
        {
            blinkTimer++;
        }
    }

    @Override
    protected void onClickOn(int mouseX, int mouseY, int mouseButton)
    {
        super.onClickOn(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawHover(int mouseX, int mouseY, int maxWidth, int maxHeight)
    {
        if (isFocused()) return;
        super.drawHover(mouseX, mouseY, maxWidth, maxHeight);
        if (text.length() > 30)
        {
            List<String> lines = ImmutableList.<String>builder().add(enabled ? "Current value:" : "Disabled!").add(text.split("\n")).build();
            GuiUtils.drawHoveringText(lines, mouseX, mouseY, maxWidth, maxHeight, this.maxWidth, fontRendererObj);
        }
    }

    @Override
    public boolean keyTyped(char typedChar, int keyCode)
    {
        if (super.keyTyped(typedChar, keyCode)) return true;
        if (GuiScreen.isKeyComboCtrlV(keyCode) || typedChar == 22) text += GuiScreen.getClipboardString();
        else if (GuiScreen.isCtrlKeyDown()) return false;
        else
        {
            switch (keyCode)
            {
                case Keyboard.KEY_ESCAPE:
                    return false;
                case Keyboard.KEY_BACK:
                    int len = text.length();
                    if (len > 0) text = text.substring(0, len - 1);
                    break;
                case Keyboard.KEY_NUMPADENTER: //fallthrough
                case Keyboard.KEY_RETURN:
                    text += '\n'; break;
                case Keyboard.KEY_DELETE:
                    text = ""; break;
                default:
                    if (typedChar != 0)
                        text += typedChar;
                    else
                        return false;
                    break;
            }
        }
        calcHeight();
        return true;
    }

    @Override
    public String save()
    {
        return enabled ? '"' + text + '"' : null;
    }
}
