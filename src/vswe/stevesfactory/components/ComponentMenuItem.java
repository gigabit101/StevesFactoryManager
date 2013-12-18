package vswe.stevesfactory.components;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatAllowedCharacters;
import vswe.stevesfactory.CollisionHelper;
import vswe.stevesfactory.interfaces.ContainerManager;
import vswe.stevesfactory.interfaces.GuiManager;
import vswe.stevesfactory.network.DataBitHelper;
import vswe.stevesfactory.network.DataReader;
import vswe.stevesfactory.network.DataWriter;
import vswe.stevesfactory.network.PacketHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ComponentMenuItem extends ComponentMenu {



    public ComponentMenuItem(FlowComponent parent) {
        super(parent);

        text = "";
        result = new ArrayList<ItemStack>();
        settings = new ArrayList<ItemSetting>();
        for (int i = 0; i < 30; i++) {
            settings.add(new ItemSetting(i));
        }
        numberTextBoxes = new TextBoxNumberList();
        numberTextBoxes.addTextBox(amountTextBox = new TextBoxNumber(80 ,24, 3, true) {
            @Override
            public boolean isVisible() {
                return selectedSetting.isLimitedByAmount();
            }

            @Override
            public void onNumberChanged() {
                selectedSetting.getItem().stackSize = getNumber();
                writeServerData(DataTypeHeader.AMOUNT);
            }
        });
        numberTextBoxes.addTextBox(damageValueTextBox = new TextBoxNumber(70 ,52, 5, true) {
            @Override
            public boolean isVisible() {
                return !selectedSetting.isFuzzy();
            }

            @Override
            public void onNumberChanged() {
                selectedSetting.getItem().setItemDamage(getNumber());
                writeServerData(DataTypeHeader.META);
            }
        });

        radioButtons = new RadioButtonList() {
            @Override
            public void updateSelectedOption(int selectedOption) {
                DataWriter dw = getWriterForServerComponentPacket();
                dw.writeBoolean(false); //no specific item
                dw.writeBoolean(selectedOption == 0);
                PacketHandler.sendDataToServer(dw);
            }
        };

        initRadioButtons();

        checkBoxes = new CheckBoxList();

        checkBoxes.addCheckBox(new CheckBox("Specify amount?", 5, 25) {
            @Override
            public void setValue(boolean val) {
                selectedSetting.setLimitedByAmount(val);
            }

            @Override
            public boolean getValue() {
                return selectedSetting.isLimitedByAmount();
            }

            @Override
            public void onUpdate() {
                writeServerData(DataTypeHeader.USE_AMOUNT);
            }
        });

        checkBoxes.addCheckBox(new CheckBox("Is detection fuzzy?", 5, 40) {
                @Override
                public void setValue(boolean val) {
                    selectedSetting.setFuzzy(val);
                }

                @Override
                public boolean getValue() {
                    return selectedSetting.isFuzzy();
                }

                @Override
                public void onUpdate() {
                    writeServerData(DataTypeHeader.USE_FUZZY);
                }
         });

        updateScrolling();
    }

    protected void initRadioButtons() {
        radioButtons.add(new RadioButton(RADIO_BUTTON_X_LEFT, RADIO_BUTTON_Y, "White list"));
        radioButtons.add(new RadioButton(RADIO_BUTTON_X_RIGHT, RADIO_BUTTON_Y, "Black list"));
    }

    protected static final int RADIO_BUTTON_X_LEFT = 5;
    protected static final int RADIO_BUTTON_X_RIGHT = 65;
    protected static final int RADIO_BUTTON_Y = 5;

    private static final int TEXT_BOX_SIZE_W = 64;
    private static final int TEXT_BOX_SIZE_H = 12;
    private static final int TEXT_BOX_SRC_X = 0;
    private static final int TEXT_BOX_SRC_Y = 165;
    private static final int TEXT_BOX_X = 5;
    private static final int TEXT_BOX_Y = 5;
    private static final int TEXT_BOX_TEXT_X = 3;
    private static final int TEXT_BOX_TEXT_Y = 3;
    private static final int CURSOR_X = 2;
    private static final int CURSOR_Y = 0;
    private static final int CURSOR_Z = 5;
    private static final int AMOUNT_TEXT_X = 75;
    private static final int AMOUNT_TEXT_Y = 9;

    private static final int ARROW_SIZE_W = 10;
    private static final int ARROW_SIZE_H = 6;
    private static final int ARROW_SRC_X = 64;
    private static final int ARROW_SRC_Y = 165;
    private static final int ARROW_X = 105;
    private static final int ARROW_Y_UP = 32;
    private static final int ARROW_Y_DOWN = 42;


    private static final int ITEMS_PER_ROW = 5;
    private static final int VISIBLE_ROWS = 2;
    private static final int ITEM_SIZE = 16;
    private static final int ITEM_SIZE_WITH_MARGIN = 20;
    private static final int ITEM_X = 5;
    private static final int ITEM_Y = 20;

    private static final int SETTING_SRC_X = 0;
    private static final int SETTING_SRC_Y = 189;

    private static final int EDIT_ITEM_X = 5;
    private static final int EDIT_ITEM_Y = 5;



    private static final int DMG_VAL_TEXT_X = 15;
    private static final int DMG_VAL_TEXT_Y = 55;

    private static final int BACK_SRC_X = 46;
    private static final int BACK_SRC_Y = 52;
    private static final int BACK_SIZE_W = 9;
    private static final int BACK_SIZE_H = 9;
    private static final int BACK_X = 108;
    private static final int BACK_Y = 57;

    private static final int DELETE_SRC_X = 0;
    private static final int DELETE_SRC_Y = 130;
    private static final int DELETE_SIZE_W = 32;
    private static final int DELETE_SIZE_H = 11;
    private static final int DELETE_X = 85;
    private static final int DELETE_Y = 3;
    private static final int DELETE_TEXT_Y = 3;

    private int offsetItems;
    private int offsetSettings;
    private boolean canScroll;
    private int dir;
    private boolean clicked;
    private boolean selected;
    private String text;
    private int cursor;
    private int cursorPosition;
    private List<ItemStack> result;
    private List<ItemSetting> settings;
    private ItemSetting selectedSetting;
    private boolean editSetting;
    private TextBoxNumberList numberTextBoxes;
    private TextBoxNumber amountTextBox;
    private TextBoxNumber damageValueTextBox;
    protected RadioButtonList radioButtons;
    private CheckBoxList checkBoxes;

    @Override
    public String getName() {
        return "Items";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY) {
        if (isEditing()) {
            gui.drawItemStack(selectedSetting.getItem(), EDIT_ITEM_X, EDIT_ITEM_Y);

            checkBoxes.draw(gui, mX, mY);

            if (!selectedSetting.isFuzzy()) {
                gui.drawString("Damage value", DMG_VAL_TEXT_X, DMG_VAL_TEXT_Y, 0.7F, 0x404040);
            }
            numberTextBoxes.draw(gui, mX, mY);


            int srcDeleteY = inDeleteBounds(mX, mY) ? 1 : 0;
            gui.drawTexture(DELETE_X, DELETE_Y, DELETE_SRC_X, DELETE_SRC_Y + srcDeleteY * DELETE_SIZE_H, DELETE_SIZE_W, DELETE_SIZE_H);
            gui.drawCenteredString("Delete", DELETE_X, DELETE_Y + DELETE_TEXT_Y, 0.7F, DELETE_SIZE_W, 0xBB4040);
        }else if (isSearching()) {
            int srcBoxY = selected ? 1 : 0;

            gui.drawTexture(TEXT_BOX_X, TEXT_BOX_Y, TEXT_BOX_SRC_X, TEXT_BOX_SRC_Y + srcBoxY * TEXT_BOX_SIZE_H, TEXT_BOX_SIZE_W, TEXT_BOX_SIZE_H);
            gui.drawString(text, TEXT_BOX_X + TEXT_BOX_TEXT_X, TEXT_BOX_Y + TEXT_BOX_TEXT_Y, 0xFFFFFF);

            if (selected) {
                gui.drawCursor(TEXT_BOX_X + cursorPosition + CURSOR_X, TEXT_BOX_Y + CURSOR_Y, CURSOR_Z, 0xFFFFFFFF);
            }

            if (isScrollingVisible()) {
                gui.drawString("Found " + result.size(), AMOUNT_TEXT_X, AMOUNT_TEXT_Y, 0.7F, 0x404040);

                List<Point> points = getItemCoordinates();
                for (Point point : points) {
                    gui.drawItemStack(result.get(point.id), point.x, point.y);
                }
            }
        }else{

            radioButtons.draw(gui, mX, mY);

            List<Point> points = getItemCoordinates();
            for (Point point : points) {
                ItemSetting setting = settings.get(point.id);

                int srcSettingX = setting.getItem() == null ? 1 : 0;
                int srcSettingY = CollisionHelper.inBounds(point.x, point.y, ITEM_SIZE, ITEM_SIZE, mX, mY) ? 1 : 0;

                gui.drawTexture(point.x, point.y, SETTING_SRC_X + srcSettingX * ITEM_SIZE, SETTING_SRC_Y + srcSettingY * ITEM_SIZE, ITEM_SIZE, ITEM_SIZE);
                if (setting.getItem() != null) {
                    gui.drawItemStack(setting.getItem(), point.x, point.y);
                }
            }

        }

        if (isScrollingVisible()) {
            drawArrow(gui, true, mX, mY);
            drawArrow(gui, false, mX, mY);

            if (clicked && canScroll) {
                setOffset(getOffset() + dir);
                int min = 0;
                int max = ((int)(Math.ceil(((float)getScrollingList().size() / ITEMS_PER_ROW)) - VISIBLE_ROWS)) * ITEM_SIZE_WITH_MARGIN - (ITEM_SIZE_WITH_MARGIN - ITEM_SIZE);
                if (getOffset() < min) {
                    setOffset(min);
                }else if(getOffset() > max) {
                    setOffset(max);
                }

            }
        }

        if (selectedSetting != null) {
            int srcBackX = inBackBounds(mX, mY) ? 1 : 0;

            gui.drawTexture(BACK_X, BACK_Y, BACK_SRC_X + srcBackX * BACK_SIZE_W, BACK_SRC_Y, BACK_SIZE_W, BACK_SIZE_H);
        }
    }

    private boolean inBackBounds(int mX, int mY) {
        return CollisionHelper.inBounds(BACK_X, BACK_Y, BACK_SIZE_W, BACK_SIZE_H, mX, mY);
    }

    private boolean inDeleteBounds(int mX, int mY) {
        return CollisionHelper.inBounds(DELETE_X, DELETE_Y, DELETE_SIZE_W, DELETE_SIZE_H, mX, mY);
    }

    private boolean isScrollingVisible() {
        return !isEditing() && getScrollingList().size() > 0;
    }

    private List getScrollingList() {
        return isSearching() ? result : settings;
    }

    private int getOffset() {
        return isSearching() ? offsetItems : offsetSettings;
    }

    private void setOffset(int val) {
        if (isSearching()) {
            offsetItems = val;
        }else{
            offsetSettings = val;
        }
    }

    private void updateScrolling() {
        canScroll = getScrollingList().size() > ITEMS_PER_ROW * VISIBLE_ROWS;
        if (!canScroll) {
            setOffset(0);
        }
    }

    private int getFirstRow() {
        return (TEXT_BOX_Y + TEXT_BOX_SIZE_H + getOffset() - ITEM_Y) / ITEM_SIZE_WITH_MARGIN;
    }

    @SideOnly(Side.CLIENT)
    private void drawArrow(GuiManager gui, boolean down, int mX, int mY) {
        int srcArrowX = canScroll ? clicked && down == (dir == 1) ? 2 : inArrowBounds(down, mX, mY) ? 1 : 0 : 3;
        int srcArrowY = down ? 1 : 0;

        gui.drawTexture(ARROW_X, down ? ARROW_Y_DOWN : ARROW_Y_UP, ARROW_SRC_X + srcArrowX * ARROW_SIZE_W, ARROW_SRC_Y + srcArrowY * ARROW_SIZE_H, ARROW_SIZE_W, ARROW_SIZE_H);
    }

    private boolean inArrowBounds(boolean down, int mX, int mY) {
        return CollisionHelper.inBounds(ARROW_X, down ? ARROW_Y_DOWN : ARROW_Y_UP, ARROW_SIZE_W, ARROW_SIZE_H, mX, mY);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawMouseOver(GuiManager gui, int mX, int mY) {
        if (isEditing()) {
            if (CollisionHelper.inBounds(EDIT_ITEM_X, EDIT_ITEM_Y, ITEM_SIZE, ITEM_SIZE, mX, mY)) {
                gui.drawMouseOver(getToolTip(selectedSetting.getItem()), mX, mY);
            }else if(inDeleteBounds(mX, mY)) {
                gui.drawMouseOver("Delete this item selection", mX, mY);
            }
        }

        if (isScrollingVisible()) {
            List<Point> points = getItemCoordinates();
            for (Point point : points) {
                if (CollisionHelper.inBounds(point.x, point.y, ITEM_SIZE, ITEM_SIZE, mX, mY)) {
                    if (isSearching()) {
                        gui.drawMouseOver(getToolTip(result.get(point.id)), mX, mY);
                    }else{
                        gui.drawMouseOver(settings.get(point.id).getMouseOver(), mX, mY);
                    }


                }
            }
        }

        if (selectedSetting != null && inBackBounds(mX, mY)) {
            gui.drawMouseOver(isEditing() ? "Go back" : "Cancel", mX, mY);
        }
    }


    private List<Point> getItemCoordinates() {
        List<Point> points = new ArrayList<Point>();

        int start = getFirstRow();
        for (int row = start; row < start + VISIBLE_ROWS + 1; row++) {
            for (int col = 0; col < ITEMS_PER_ROW; col++) {
                int id = row * ITEMS_PER_ROW + col;
                if (id >= 0 && id < getScrollingList().size())  {
                    int y = ITEM_Y + row * ITEM_SIZE_WITH_MARGIN - getOffset();
                    if (y > TEXT_BOX_Y + TEXT_BOX_SIZE_H && y + ITEM_SIZE < FlowComponent.getMenuOpenSize()) {
                        points.add(new Point(id, ITEM_X + ITEM_SIZE_WITH_MARGIN * col, y));
                    }
                }
            }
        }

        return points;
    }

    @Override
    public void onClick(int mX, int mY, int button) {
        if (isEditing()) {
            checkBoxes.onClick(mX, mY);

            numberTextBoxes.onClick(mX, mY, button);

            if (inDeleteBounds(mX, mY)) {
                selectedSetting.clear();
                writeServerData(DataTypeHeader.CLEAR);
                selectedSetting = null;
                updateScrolling();
            }
        }else if (isSearching()) {
            if (CollisionHelper.inBounds(TEXT_BOX_X, TEXT_BOX_Y, TEXT_BOX_SIZE_W, TEXT_BOX_SIZE_H, mX, mY)) {
                selected = !selected;
            }

            List<Point> points = getItemCoordinates();
            for (Point point : points) {
                if (CollisionHelper.inBounds(point.x, point.y, ITEM_SIZE, ITEM_SIZE, mX, mY)) {
                    selectedSetting.setItem(result.get(point.id).copy());
                    writeServerData(DataTypeHeader.SET_ITEM);
                    selectedSetting = null;
                    updateScrolling();
                    break;
                }
            }
        }else{
            radioButtons.onClick(mX, mY, button);

            List<Point> points = getItemCoordinates();
            for (Point point : points) {
                if (CollisionHelper.inBounds(point.x, point.y, ITEM_SIZE, ITEM_SIZE, mX, mY)) {
                    selectedSetting = settings.get(point.id);
                    editSetting = button == 1;


                    if (editSetting && selectedSetting.getItem() == null) {
                        selectedSetting = null;
                        editSetting = false;
                    }else{
                        if (editSetting) {
                            amountTextBox.setNumber(selectedSetting.getItem().stackSize);
                            damageValueTextBox.setNumber(selectedSetting.getItem().getItemDamage());
                        }
                        updateScrolling();
                    }

                    break;
                }
            }
        }

        if (isScrollingVisible() && canScroll) {
            if(inArrowBounds(true, mX, mY)) {
                clicked = true;
                dir = 1;
            }else if (inArrowBounds(false, mX, mY)){
                clicked = true;
                dir = -1;
            }
        }
        if (selectedSetting != null && inBackBounds(mX, mY)) {
            selectedSetting = null;
            updateScrolling();
        }
    }

    private boolean isEditing() {
        return selectedSetting != null && editSetting;
    }

    private boolean isSearching() {
        return selectedSetting != null && !editSetting;
    }

    @Override
    public void onDrag(int mX, int mY) {

    }

    @Override
    public void onRelease(int mX, int mY) {
        clicked = false;
    }
    @SideOnly(Side.CLIENT)
    @Override
    public boolean onKeyStroke(GuiManager gui, char c, int k) {
        if (selected && isSearching()) {
            if (k == 203) {
                moveCursor(gui, -1);
            }else if(k == 205) {
                moveCursor(gui, 1);
            }else if (k == 14) {
                deleteText(gui, -1);
            }else if (k == 211) {
                deleteText(gui, 1);
            }else if (ChatAllowedCharacters.isAllowedCharacter(c)) {
                addText(gui, Character.toString(c));
            }

            return true;
        }else if (isEditing()){
            return numberTextBoxes.onKeyStroke(gui, c, k);
        }else{
            return false;
        }
    }

    @Override
    public void writeData(DataWriter dw) {
        dw.writeBoolean(isFirstRadioButtonSelected());
        for (ItemSetting setting : settings) {
            dw.writeBoolean(setting.getItem() != null);
            if (setting.getItem() != null) {
                dw.writeData(setting.getItem().itemID, DataBitHelper.MENU_ITEM_ID);
                dw.writeBoolean(setting.isFuzzy());
                dw.writeData(setting.getItem().getItemDamage(), DataBitHelper.MENU_ITEM_META);
                dw.writeBoolean(setting.isLimitedByAmount());
                if (setting.isLimitedByAmount()) {
                    dw.writeData(setting.getItem().stackSize, DataBitHelper.MENU_ITEM_AMOUNT);
                }
            }
        }
    }

    @Override
    public void readData(DataReader dr) {
        setFirstRadioButtonSelected(dr.readBoolean());
        for (ItemSetting setting : settings) {
            if (!dr.readBoolean()) {
                setting.clear();
            }else{
                int id = dr.readData(DataBitHelper.MENU_ITEM_ID);
                setting.setFuzzy(dr.readBoolean());
                int meta = dr.readData(DataBitHelper.MENU_ITEM_META);
                setting.setLimitedByAmount(dr.readBoolean());
                int amount;
                if (setting.isLimitedByAmount()) {
                    amount = dr.readData(DataBitHelper.MENU_ITEM_AMOUNT);
                }else{
                    amount = 1;
                }

                setting.setItem(new ItemStack(id, amount, meta));
            }
        }
    }

    @Override
    public void copyFrom(ComponentMenu menu) {
        ComponentMenuItem menuItem = (ComponentMenuItem)menu;

        setFirstRadioButtonSelected(menuItem.isFirstRadioButtonSelected());

        for (int i = 0; i < settings.size(); i++) {
            if (menuItem.settings.get(i).getItem() == null) {
                settings.get(i).clear();
            }else{
                settings.get(i).setItem(menuItem.settings.get(i).getItem().copy());
                settings.get(i).setFuzzy(menuItem.settings.get(i).isFuzzy());
                settings.get(i).setLimitedByAmount(menuItem.settings.get(i).isLimitedByAmount());
            }
        }
    }

    @Override
    public void refreshData(ContainerManager container, ComponentMenu newData) {
        if (((ComponentMenuItem)newData).isFirstRadioButtonSelected() != isFirstRadioButtonSelected()) {
            setFirstRadioButtonSelected(((ComponentMenuItem) newData).isFirstRadioButtonSelected());

            DataWriter dw = getWriterForClientComponentPacket(container);
            dw.writeBoolean(false); //no specific setting
            dw.writeBoolean(isFirstRadioButtonSelected());
            PacketHandler.sendDataToListeningClients(container, dw);
        }

        for (int i = 0; i < settings.size(); i++) {
            ItemSetting setting = settings.get(i);
            ItemSetting newSetting = ((ComponentMenuItem)newData).settings.get(i);

            if (newSetting.getItem() == null && setting.getItem() != null) {
                setting.setItem(null);
                writeClientData(container, DataTypeHeader.CLEAR, setting);
            }

            if (newSetting.getItem() != null && (setting.getItem() == null || setting.getItem().itemID != newSetting.getItem().itemID)) {
                setting.setItem(newSetting.getItem().copy());
                writeClientData(container, DataTypeHeader.SET_ITEM, setting);
            }

            if (newSetting.isLimitedByAmount() != setting.isLimitedByAmount()) {
                setting.setLimitedByAmount(newSetting.isLimitedByAmount());
                writeClientData(container, DataTypeHeader.USE_AMOUNT, setting);
            }

            if (newSetting.isFuzzy() != setting.isFuzzy()) {
                setting.setFuzzy(newSetting.isFuzzy());
                writeClientData(container, DataTypeHeader.USE_FUZZY, setting);
            }

            if (newSetting.getItem() != null && setting.getItem() != null) {
                if (newSetting.getItem().stackSize != setting.getItem().stackSize) {
                    setting.getItem().stackSize = newSetting.getItem().stackSize;
                    writeClientData(container, DataTypeHeader.AMOUNT, setting);
                }

                if (newSetting.getItem().getItemDamage() != setting.getItem().getItemDamage()) {
                    setting.getItem().setItemDamage(newSetting.getItem().getItemDamage());
                    writeClientData(container, DataTypeHeader.META, setting);
                }
            }
        }
    }

    @Override
    public void readNetworkComponent(DataReader dr) {
        boolean useSetting = dr.readBoolean();

        if (useSetting) {
            int settingId = dr.readData(DataBitHelper.MENU_ITEM_SETTING_ID);
            ItemSetting setting = settings.get(settingId);
            int headerId = dr.readData(DataBitHelper.MENU_ITEM_TYPE_HEADER);
            DataTypeHeader header = getHeaderFromId(headerId);

            switch (header) {
                case CLEAR:
                    setting.clear();
                    selectedSetting = null;
                    break;
                case SET_ITEM:
                    int id = dr.readData(DataBitHelper.MENU_ITEM_ID);
                    int dmg =  dr.readData(DataBitHelper.MENU_ITEM_META);

                    setting.setItem(new ItemStack(id, 1, dmg));

                    if (isEditing()) {
                        damageValueTextBox.setNumber(setting.getItem().getItemDamage());
                    }

                    break;
                case USE_AMOUNT:
                    setting.setLimitedByAmount(dr.readBoolean());
                    if (!setting.isLimitedByAmount() && setting.getItem() != null) {
                        setting.getItem().stackSize = 1;
                    }
                    break;
                case USE_FUZZY:
                    setting.setFuzzy(dr.readBoolean());
                    break;
                case AMOUNT:
                    if (setting.getItem() != null) {
                        setting.getItem().stackSize = dr.readData(DataBitHelper.MENU_ITEM_AMOUNT);
                        if (isEditing()) {
                            amountTextBox.setNumber(setting.getItem().stackSize);
                        }
                    }
                    break;
                case META:
                    if (setting.getItem() != null) {
                        setting.getItem().setItemDamage(dr.readData(DataBitHelper.MENU_ITEM_META));
                        if (isEditing()) {
                            damageValueTextBox.setNumber(setting.getItem().getItemDamage());
                        }
                    }

            }
        }else{
            setFirstRadioButtonSelected(dr.readBoolean());
        }
    }

    private void writeClientData(ContainerManager container, DataTypeHeader header, ItemSetting setting) {
        DataWriter dw = getWriterForClientComponentPacket(container);
        writeData(dw, header, setting);
        PacketHandler.sendDataToListeningClients(container, dw);
    }

    private void writeServerData(DataTypeHeader header) {
        DataWriter dw = getWriterForServerComponentPacket();
        writeData(dw, header, selectedSetting);
        PacketHandler.sendDataToServer(dw);
    }

    private void writeData(DataWriter dw, DataTypeHeader header, ItemSetting setting) {
        dw.writeBoolean(true); //specific setting is being used
        dw.writeData(setting.getId(), DataBitHelper.MENU_ITEM_SETTING_ID);
        dw.writeData(header.id, DataBitHelper.MENU_ITEM_TYPE_HEADER);

        switch (header) {
            case CLEAR:
                break;
            case SET_ITEM:
                dw.writeData(setting.getItem().itemID, DataBitHelper.MENU_ITEM_ID);
                dw.writeData(setting.getItem().getItemDamage(), DataBitHelper.MENU_ITEM_META);
                break;
            case USE_AMOUNT:
                dw.writeBoolean(setting.isLimitedByAmount());
                break;
            case USE_FUZZY:
                dw.writeBoolean(setting.isFuzzy());
                break;
            case AMOUNT:
                dw.writeData(setting.getItem().stackSize, DataBitHelper.MENU_ITEM_AMOUNT);
                break;
            case META:
                dw.writeData(setting.getItem().getItemDamage(), DataBitHelper.MENU_ITEM_META);

        }

    }

    public List<ItemSetting> getSettings() {
        return settings;
    }

    private enum DataTypeHeader {
        CLEAR(0),
        SET_ITEM(1),
        USE_AMOUNT(2),
        USE_FUZZY(3),
        AMOUNT(4),
        META(5);

        private int id;
        private DataTypeHeader(int header) {
            this.id = header;
        }
    }

    private DataTypeHeader getHeaderFromId(int id) {
        for (DataTypeHeader header : DataTypeHeader.values()) {
            if (id == header.id) {
                return header;
            }
        }
        return  null;
    }

    @SideOnly(Side.CLIENT)
    private void addText(GuiManager gui, String str) {
        text = text.substring(0, cursor) + str + text.substring(cursor);

        moveCursor(gui, str.length());
        textChanged();
    }

    @SideOnly(Side.CLIENT)
    private void deleteText(GuiManager gui, int direction) {
        if (cursor + direction >= 0 && cursor + direction <= text.length()) {
            if (direction > 0) {
                text = text.substring(0, cursor) + text.substring(cursor + 1);
            }else{
                text = text.substring(0, cursor - 1) + text.substring(cursor);
            }
            moveCursor(gui, direction);
            textChanged();
        }
    }

    @SideOnly(Side.CLIENT)
    private void moveCursor(GuiManager gui, int steps) {
        cursor += steps;

        if (cursor < 0) {
            cursor = 0;
        }else if (cursor > text.length()) {
            cursor = text.length();
        }

        cursorPosition = gui.getStringWidth(text.substring(0, cursor));
    }

    @SideOnly(Side.CLIENT)
    private void textChanged() {
        if (text.length() > 0) {
            updateSearch();
        }else{
            result.clear();
            updateScrolling();
        }
    }

    @SideOnly(Side.CLIENT)
    private void updateSearch() {
        result.clear();
        Item[] items = Item.itemsList;
        int itemLength = items.length;

        for (int i = 0; i < itemLength; ++i) {
            Item item = items[i];

            if (item != null && item.getCreativeTab() != null) {
                item.getSubItems(item.itemID, null, result);
            }
        }

        Iterator<ItemStack> itemIterator = result.iterator();
        String searchString = text.toLowerCase();

        while (itemIterator.hasNext()) {

            ItemStack itemStack = itemIterator.next();
            Iterator<String> descriptionIterator = itemStack.getTooltip(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().gameSettings.advancedItemTooltips).iterator();

            boolean foundSequence = false;

            while (descriptionIterator.hasNext()) {
                String line = descriptionIterator.next().toLowerCase();
                if (line.contains(searchString)) {
                    foundSequence = true;
                    break;
                }
            }

            if (!foundSequence) {
                itemIterator.remove();
            }
        }

        updateScrolling();
    }




    private class Point {
        int id, x, y;

        private Point(int id, int x, int y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }
    }



    @SideOnly(Side.CLIENT)
    public static List<String> getToolTip(ItemStack itemStack) {
        try {
            return itemStack.getTooltip(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
        }catch (Exception ex) {
            if (itemStack.getItemDamage() == 0) {
                return new ArrayList<String>();
            }else{
                ItemStack newItem = itemStack.copy();
                newItem.setItemDamage(0);
                return getToolTip(newItem);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static String getDisplayName(ItemStack itemStack) {
        try {
            return itemStack.getDisplayName();
        }catch (Exception ex) {
            if (itemStack.getItemDamage() == 0) {
                return "";
            }else{
                ItemStack newItem = itemStack.copy();
                newItem.setItemDamage(0);
                return getDisplayName(newItem);
            }
        }
    }

    protected boolean isFirstRadioButtonSelected() {
        return radioButtons.getSelectedOption() == 0;
    }

    protected void setFirstRadioButtonSelected(boolean value) {
        radioButtons.setSelectedOption(value ? 0 : 1);
    }

    public boolean useWhiteList() {
        return isFirstRadioButtonSelected();
    }

    private static final String NBT_RADIO_SELECTION = "FirstSelected";
    private static final String NBT_SETTINGS = "Settings";
    private static final String NBT_SETTING_ID = "Id";
    private static final String NBT_SETTING_ITEM_ID = "ItemId";
    private static final String NBT_SETTING_ITEM_DMG = "ItemDamage";
    private static final String NBT_SETTING_ITEM_COUNT = "ItemCount";
    private static final String NBT_SETTING_FUZZY = "Fuzzy";
    private static final String NBT_SETTING_USE_SIZE = "SizeLimit";

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
       setFirstRadioButtonSelected(nbtTagCompound.getBoolean(NBT_RADIO_SELECTION));

        NBTTagList settingTagList = nbtTagCompound.getTagList(NBT_SETTINGS);
        for (int i = 0; i < settingTagList.tagCount(); i++) {
            NBTTagCompound settingTag = (NBTTagCompound)settingTagList.tagAt(i);
            ItemSetting setting = settings.get(settingTag.getByte(NBT_SETTING_ID));
            setting.setItem(new ItemStack(settingTag.getShort(NBT_SETTING_ITEM_ID), settingTag.getShort(NBT_SETTING_ITEM_COUNT), settingTag.getShort(NBT_SETTING_ITEM_DMG)));
            setting.setFuzzy(settingTag.getBoolean(NBT_SETTING_FUZZY));
            setting.setLimitedByAmount(settingTag.getBoolean(NBT_SETTING_USE_SIZE));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        nbtTagCompound.setBoolean(NBT_RADIO_SELECTION, isFirstRadioButtonSelected());

        NBTTagList settingTagList = new NBTTagList();
        for (int i = 0; i < settings.size(); i++) {
            ItemSetting setting = settings.get(i);
            if (setting.getItem() != null) {
                NBTTagCompound settingTag = new NBTTagCompound();
                settingTag.setByte(NBT_SETTING_ID, (byte)setting.getId());
                settingTag.setShort(NBT_SETTING_ITEM_ID, (short)setting.getItem().itemID);
                settingTag.setShort(NBT_SETTING_ITEM_DMG, (short)setting.getItem().getItemDamage());
                settingTag.setShort(NBT_SETTING_ITEM_COUNT, (short)setting.getItem().stackSize);
                settingTag.setBoolean(NBT_SETTING_FUZZY, setting.isFuzzy());
                settingTag.setBoolean(NBT_SETTING_USE_SIZE, setting.isLimitedByAmount());
                settingTagList.appendTag(settingTag);
            }
        }
        nbtTagCompound.setTag(NBT_SETTINGS, settingTagList);
    }

    @Override
    public void addErrors(List<String> errors) {
        if (useWhiteList()) {
            for (ItemSetting setting : settings) {
                if (setting.getItem() != null) {
                    return;
                }
            }
            errors.add("The whitelist is empty");
        }
    }
}

