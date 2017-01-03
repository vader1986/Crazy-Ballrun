/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.gui.controls;

import crazyballrun.game.GameConstants;
import crazyballrun.game.controls.ControlEngine;
import crazyballrun.game.controls.IListener;
import crazyballrun.game.graphics.GraphicsEngine;
import crazyballrun.game.gui.GUIControl;
import crazyballrun.game.gui.interfaces.IControlObserver;
import crazyballrun.game.gui.interfaces.IControlSubscriber;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.util.ArrayList;

/**
 * Simple ListBox GUI-element which can be used for game settings and file-
 * and folder-selection. 
 * @author Timm Hoffmeister
 */
public class CBListBox extends GUIControl implements IListener, IControlObserver, IControlSubscriber {
    
    /**
     * Offset between the listbox' left/right frame and its items.
     */
    private static int LISTBOX_OFFSET_LEFT = 20;

    /**
     * Offset between the listbox' top/bottom frame and its items.
     */
    private static int LISTBOX_OFFSET_TOP = 20;

    /**
     * Height used to render the listbox' title.
     */
    private static int LISTBOX_TITLE_HEIGHT = 20;
        
    /**
     * Offset of the elements' string from their border.
     */
    private static int LISTBOX_ITEM_STRING_OFFSET = 5;

    /**
     * Width of the scrollbar (up/down) for the listbox. 
     */
    private int LISTBOX_SCROLLBAR_WIDTH = 20;
    
    /**
     * Listbox entries.
     */
    private ArrayList <CBListEntry> mEntries = null;
    
    /**
     * Title of the listbox. 
     */
    private String mTitle = null;
    
    /**
     * Specifies the type of the list.
     */
    private String mListType = null;
    
    /**
     * Scroll bar available?
     */
    private boolean mUseReference = false;
    
    /**
     * Optional reference to another listbox.
     */
    private String mReference = null;
    
    /**
     * Allows multiple selections in the listbox. 
     */
    private boolean mMultiSelection = false;
    
    /**
     * If multi selection is not allowed, this is a reference to the selected
     * list entry.
     */
    private CBListEntry mSelectedEntry = null;
    
    /**
     * Path to the list's file content.
     */
    private String mListContentPath = null;

    /**
     * Files in 'mListContentPath' will be added to the list content.
     */
    private boolean mContentFiles = true;

    /**
     * Folders in 'mListContentPath' will be added to the list content.
     */
    private boolean mContentFolders = false;
    
    /**
     * Specifies the file-ending which should be used for list content.
     */
    private String mFileEnding = null;
    
    /**
     * Rectangle for painting listbox-frame's background.
     */
    private RoundRectangle2D mFrameRecBG = null;
    
    /**
     * Rectangle for painting listbox frames.
     */
    private RoundRectangle2D mFrameRec = null;

    /**
     * Color gradient for painting listbox frames.
     */
    private GradientPaint mFrameRecGradient = null;

    /**
     * Rectangles of list elements. 
     */
    private Rectangle2D [] mElementRec = null;

    /**
     * Color gradients for painting one non-selected list elements.
     */
    private GradientPaint [] mElementRecGradient = null;

    /**
     * Color gradients for painting one selected list elements.
     */
    private GradientPaint [] mElementRecGradientSelected = null;
    
    /**
     * Rectangle for the scrollbar. 
     */
    private Rectangle2D mScrollBarRec = null;

    /**
     * Color gradient for the scroll bar. 
     */
    private GradientPaint mScrollBarRecGradient = null;
    
    /**
     * Number of elements rendered without scrolling. 
     */
    private int mNumberElements = 10;

    /**
     * Index of the first element shown in the list (NOT the first element, if
     * the scrollbar has been used).
     */
    private int mCurrentElement = 0;
    
    /**
     * Height of the element box in pixels.
     */
    private int mElementBoxHeight = 0;
    
    /**
     * Height of one element in pixels. 
     */
    private int mElementHeight = 0;

    /**
     * Offset of the scrollbar scroller.
     */
    private int mScrollBarOffset = 0;
    
    /**
     * ListBox listeners (to changes in selection/scrollbar).
     */
    private ArrayList <IControlSubscriber> mListeners = null;
    
    /**
     * Constructor of CBListBoxSimple.
     */
    public CBListBox () {
        mEntries = new ArrayList<CBListEntry>();
        mListeners = new ArrayList<IControlSubscriber>();
    }

    @Override
    public void register (IControlSubscriber listener) {
        mListeners.add(listener);
    }
    
    @Override
    public boolean notify (Object event, IControlObserver sender) {
        if (event instanceof CBListBoxSimpleEvent) {
            CBListBoxSimpleEvent vEvent = (CBListBoxSimpleEvent) event;
            switch (vEvent.mType) {
                case CBListBoxSimpleEvent.EVENT_SCROLLBAR:
                    setCurrentElement(vEvent.mIndex);
                    break;
                case CBListBoxSimpleEvent.EVENT_SELECTION_TRUE:
                    select(vEvent.mIndex, true);
                    break;
                case CBListBoxSimpleEvent.EVENT_SELECTION_FALSE:
                    select(vEvent.mIndex, false);
                    break;
                case CBListBoxSimpleEvent.EVENT_REMOVED:
                    removeEntry(vEvent.mIndex);
                    break;
            }
        }
        return false;
    }
    
    @Override
    public void setAttribute(String attr, String value) {
        if (attr.equals("title")) {
            mTitle = value;
        } else if (attr.equals("type")) {
            mListType = value;
        } else if (attr.equals("item")) {
            mEntries.add( new CBListEntry(value.split(",")[0], value.split(",")[1]));
        } else if (attr.equals("font")) {
            setFont(value);
        } else if (attr.equals("font color")) {
            String [] vRGB = value.split(",");
            setColor(vRGB[0], vRGB[1], vRGB[2]);
        } else if (attr.equals("reference")) {
            mReference = value;
            mUseReference = true;
        } else if (attr.equals("path")) {
            mListContentPath = value;
        } else if (attr.equals("files")) {
            mContentFiles = Boolean.parseBoolean(value);
        } else if (attr.equals("folders")) {
            mContentFolders = Boolean.parseBoolean(value);
        } else if (attr.equals("extension")) {
            mFileEnding = value;
        } else if (attr.equals("multiselections")) {
            mMultiSelection = Boolean.parseBoolean(value);
        } else if (attr.equals("elements")) {
            mNumberElements = Integer.parseInt(value);
        }
    }

    /** 
     * Creates and adds entries for the listbox: takes files from the given
     * path.
     * @param filepath where to find the file/folders to add to the listbox
     */
    private void fillFromFile (String filepath) {
        File f = new File(filepath);
        if (f.isDirectory()) {
            for (File vFile : f.listFiles()) {
                if (mContentFolders && vFile.isDirectory()) {
                    mEntries.add(new CBListEntry(vFile.getName(), vFile.getName()));
                }
                if (mContentFiles && vFile.isFile()) {
                    String vName = vFile.getName();
                    if (mFileEnding == null ||
                            vName.substring(vName.lastIndexOf(".")).compareTo(mFileEnding) == 0) {
                        mEntries.add(new CBListEntry(vName, vName));
                    }
                }
            }
        }
    }
    
    /**
     * Fills the listbox with appropriate content.
     */
    private void fill () {
        mListType = mListType.toUpperCase();
        
        if (mListType.compareTo("ITEMS") == 0) {
            // nothing
        } else if (mListType.compareTo("FILES") == 0) {
            fillFromFile(GameConstants.MAIN_PATH + File.separator + mListContentPath);
        } else if (mListType.compareTo("LEVELS") == 0) {
            mMultiSelection = false;
            mContentFolders = false;
            mContentFiles = true;
            mFileEnding = GameConstants.LEVEL_FILE_EXTENSION;
            fillFromFile(GameConstants.LEVEL_RESOURCE_PATH);
        } else if (mListType.compareTo("SERVER") == 0) {
            // TODO: show a list of available servers
        }

        // initialize list entries
        for (CBListEntry vEntry : mEntries) {
            vEntry.initialize();
        }
    }
    
    @Override
    public void initialize() {

        // element render properties
        int vElementBoxHeight = mRectangle[5] - LISTBOX_OFFSET_TOP * 2 - LISTBOX_TITLE_HEIGHT;
        int vElementWidth = mRectangle[4] - LISTBOX_OFFSET_LEFT * 2 - (mUseReference ? 0 : LISTBOX_SCROLLBAR_WIDTH);
        mElementHeight = vElementBoxHeight / mNumberElements;
        mElementRec = new Rectangle2D[mNumberElements];
        mElementRecGradient = new GradientPaint[mNumberElements];
        mElementRecGradientSelected = new GradientPaint[mNumberElements];
        Color c1es = new Color (255, 50, 0, 255);
        Color c2es = new Color (255, 50, 0, 255);
        for (int i = 0; i < mElementRec.length; i++) {
            int x1 = mRectangle[0] + LISTBOX_OFFSET_LEFT;
            int y1 = mRectangle[1] + LISTBOX_OFFSET_TOP + LISTBOX_TITLE_HEIGHT + i * mElementHeight;
            mElementRec[i] = new Rectangle2D.Double (x1, y1, vElementWidth, mElementHeight);
            mElementRecGradient[i] = new GradientPaint(x1, y1, Color.BLUE, x1, y1 + mElementHeight, Color.BLUE);
            mElementRecGradientSelected[i] = new GradientPaint(x1, y1, c1es, x1, y1 + mElementHeight, c2es);
        }
        
        // render properties
        Color c1 = new Color (Color.GRAY.getRed(), Color.GRAY.getGreen(), Color.GRAY.getBlue(), 255);
        Color c2 = new Color (Color.LIGHT_GRAY.getRed(), Color.LIGHT_GRAY.getGreen(), Color.LIGHT_GRAY.getBlue(), 255);
        mFrameRecBG = new RoundRectangle2D.Double(mRectangle[0]-1,mRectangle[1]-1,mRectangle[4]+2,mRectangle[5]+2,21,21);
        mFrameRec = new RoundRectangle2D.Double(mRectangle[0],mRectangle[1],mRectangle[4],mRectangle[5],20,20);
        mFrameRecGradient = new GradientPaint(mRectangle[0],mRectangle[1], c1, mRectangle[2] , mRectangle[3], c2);

        // scroll bar render properties
        int vScrollbarX = mRectangle[2] - LISTBOX_OFFSET_LEFT - LISTBOX_SCROLLBAR_WIDTH;
        int vScrollbarY = mRectangle[1] + LISTBOX_OFFSET_TOP + LISTBOX_TITLE_HEIGHT;
        Color c1sb = new Color (255, 50, 0, 255);
        Color c2sb = new Color (0, 0, 0, 255);
        mElementBoxHeight = mRectangle[5] - LISTBOX_OFFSET_TOP * 2 - LISTBOX_TITLE_HEIGHT;
        mScrollBarRec = new Rectangle2D.Double(vScrollbarX, vScrollbarY, LISTBOX_SCROLLBAR_WIDTH, mElementBoxHeight);
        mScrollBarRecGradient = new GradientPaint(vScrollbarX, vScrollbarY, c1sb, vScrollbarX, mRectangle[3] - LISTBOX_OFFSET_TOP, c2sb);
        setCurrentElement(0);
        setScrollbarOffset(0);
        if (mUseReference) 
            LISTBOX_SCROLLBAR_WIDTH = 0;
        
        // fills the listbox with entries
        fill();
        
        // event registration
        if (mUseReference)
            mMyFrame.register(mReference, this);
    }

    @Override
    public void activate () {
        super.activate();
        if (!mUseReference) {
            ControlEngine.getInstance().register(this, "Mouse", "mouse.press.left");
        }
    }

    @Override
    public void deactivate() {
        super.deactivate();
        if (!mUseReference) {
            ControlEngine.getInstance().deregister(this, "Mouse", "mouse.press.left");
        }
    }
    
    /**
     * Creates an event, dependent on the type of listener and notfies them. 
     * @param sync if 'true' the listener is a listbox which wants synchronization
     * @param scrollbar scrollbar event?
     * @param index index of the entry
     * @param selected the entry is selected or not?
     * @return a reference to the generated event.
     */
    private void notifyListeners (boolean sync, int index, int mode)
    {
        // create event
        Object vEvent = null;
        if (sync)
            vEvent = new CBListBoxSimpleEvent(mode, index);
        else
            vEvent = mEntries.get(index).data;
        
        // notify listeners
        for (IControlSubscriber vSubs : mListeners) {
            if ((vSubs instanceof CBListBox && sync) ||
                (!(vSubs instanceof CBListBox) && !sync))
            {
                vSubs.notify(vEvent, this);
            }
        }
    }
    
    /**
     * Savely returns one list entry. 
     * @param index index of the entry
     * @return a reference to the list entry-object.
     */
    private synchronized CBListEntry getEntry (int index) {
        if (mEntries.size() > index)
            return mEntries.get(index);
        else 
            return null;
    }

    /**
     * Gets the currently selected entry of the list.
     * @return selected list entry
     */
    public synchronized String getSelectedEntry () 
    {
        String vReturn = null;
        if (mSelectedEntry != null) 
        {
            vReturn = mSelectedEntry.data;
        }
        return vReturn;
    }
    
    /**
     * Removes the currently selected list entry.
     * @return content of the removed line
     */
    public synchronized String removeSelectedEntry () {
        String vReturn = null;
        if (mSelectedEntry != null) {
            int index = mEntries.indexOf(mSelectedEntry);
            vReturn = mSelectedEntry.data;
            mEntries.remove(mSelectedEntry);
            mSelectedEntry = null;
            notifyListeners(true, index, CBListBoxSimpleEvent.EVENT_REMOVED);
        }
        return vReturn;
    }
    
    /**
     * Removes an entry from the list.
     * @param index index of the entry
     */
    public synchronized void removeEntry (int index) {
        if (mEntries.get(index) == mSelectedEntry) {
            mSelectedEntry = null;
        }
        mEntries.remove(index);
    }

    /**
     * Gets the index of the first shown entry in the listbox. 
     * @return the index of the first shown item.
     */
    private synchronized int getCurrentElement () {
        return mCurrentElement; 
    }

    /**
     * Sets the index of the first shown entry in the listbox. 
     * @param index index in the entry-list
     */
    private synchronized void setCurrentElement (int index) {
        mCurrentElement = index;
        notifyListeners(true, index, CBListBoxSimpleEvent.EVENT_SCROLLBAR);
    }
    
    /**
     * Gets the current offset of the scroller in the scrollbar.
     * @return the scrollbar offset.
     */
    private synchronized int getScrollbarOffset () {
        return mScrollBarOffset;
    }
    
    /**
     * Sets the scrollbar offset (offset of the scroller-icon). 
     * @param offset current scrollbar offset
     */
    private synchronized void setScrollbarOffset (int offset) {
        mScrollBarOffset = offset;
    }
    
    /**
     * Calculates the index of the selected element. 
     * @param x position of the mouse click (x-coordinate)
     * @param y position of the mouse click (y-coordinate)
     * @return '-1' if no element has been selected, otherwise the index of the element.
     */
    private int getElementIndex (int x, int y) {
        int vReturn = -1;
        if (x > mRectangle[0] + LISTBOX_OFFSET_LEFT &&
            x < mRectangle[2] - LISTBOX_OFFSET_LEFT - LISTBOX_SCROLLBAR_WIDTH &&
            y > mRectangle[1] + LISTBOX_OFFSET_TOP + LISTBOX_TITLE_HEIGHT &&
            y < mRectangle[3] - LISTBOX_OFFSET_TOP) 
        {
            vReturn = (y - mRectangle[1] - LISTBOX_OFFSET_TOP - LISTBOX_TITLE_HEIGHT) / mElementHeight;
        }
        return vReturn;
    }

    /**
     * Selects or unselect a list-entry. 
     * @param index index of the list entry
     * @param selected select or unselect
     */
    private synchronized void select (int index, boolean selected) {
        mEntries.get(index).selected = selected;
    }
    
    /**
     * Finds out if a list entry is selected or not. 
     * @param index index of the list entry
     * @return 'true' if the entry is selected.
     */
    private synchronized boolean isSelected (int index) {
        return mEntries.get(index).isSelected();
    }
    
    /**
     * Inverts the "selection" of a list entry.
     * @param index index of the entry
     * @return 'true' if the selection has changed to 'true'.
     */
    private synchronized boolean invertSelection (int index) {
        boolean result = mEntries.get(index).invertSelection();
        notifyListeners(true, index,
                (result) ? CBListBoxSimpleEvent.EVENT_SELECTION_TRUE :
                           CBListBoxSimpleEvent.EVENT_SELECTION_FALSE); 
        return result;
    }
    
    /**
     * Changes the index of the currently selected item for non-multiselection
     * lists. 
     * @param index index of the new selected entry
     */
    private synchronized void setSelectedEntry (int index) {
        boolean result = mEntries.get(index).invertSelection();
        notifyListeners(true, index,
                (result) ? CBListBoxSimpleEvent.EVENT_SELECTION_TRUE :
                           CBListBoxSimpleEvent.EVENT_SELECTION_FALSE); 
        if (result)
        {
            if (mSelectedEntry != null && mSelectedEntry != mEntries.get(index)) {
                mSelectedEntry.unSelect();
                notifyListeners(true, mEntries.indexOf(mSelectedEntry), CBListBoxSimpleEvent.EVENT_SELECTION_FALSE); 
            }
            mSelectedEntry = mEntries.get(index);
            notifyListeners(false, index, -1);
        }
    }
    
    @Override
    public void notify(Object event, String type, String sender) {
        MouseEvent vEvent = (MouseEvent) event;

        // Select/Unselect List-Items
        if (type.compareTo("mouse.press.left") == 0) {
            Integer vIndex = getElementIndex(vEvent.getX(), vEvent.getY());
            if (vIndex > -1)
                vIndex += getCurrentElement(); 
            if (vIndex > -1 && vIndex < mEntries.size())
            {
                if (!mMultiSelection) {
                    setSelectedEntry(vIndex);
                } else {
                    invertSelection(vIndex);
                }
                return;
            }
        } 
        
        // Apply Scrollbar
        if (type.compareTo("mouse.press.left") == 0) {
            if (vEvent.getX() > mRectangle[2] - LISTBOX_OFFSET_LEFT - LISTBOX_SCROLLBAR_WIDTH &&
                vEvent.getX() < mRectangle[2] - LISTBOX_OFFSET_LEFT &&
                vEvent.getY() > mRectangle[1] + LISTBOX_OFFSET_TOP + LISTBOX_TITLE_HEIGHT &&
                vEvent.getY() < mRectangle[3] - LISTBOX_OFFSET_TOP) 
            {
                double vAbs = (double)(vEvent.getY() - (mRectangle[1] + LISTBOX_OFFSET_TOP + LISTBOX_TITLE_HEIGHT)) / (double)( mElementBoxHeight );
                int vCurrent = (int)((double) mEntries.size() * vAbs);
                int vScrollbarOffset = 0;
                if (mEntries.size() > 0)
                    vScrollbarOffset = (int) ((double) vCurrent / 
                                  (double)(mEntries.size()) * (double) mElementBoxHeight);
                vScrollbarOffset = Math.min(vScrollbarOffset, mElementBoxHeight - LISTBOX_SCROLLBAR_WIDTH);
                setScrollbarOffset(vScrollbarOffset);
                setCurrentElement(vCurrent);
            }
            
        }
        
    }

    @Override
    public void paint(Graphics g, Frame frame) {
        
        // Render Listbox-Frame
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(Color.lightGray);
        g2d.fill( mFrameRecBG );
        g2d.setPaint( mFrameRecGradient ); 
        g2d.fill( mFrameRec );         
        
        // Render Listbox-Title
        if (mTitle != null) {
            g.setFont(mTitleFont);
            if (mDoubleFont) {
                g.setColor(Color.GRAY);
                g.drawString(mTitle, 
                    mRectangle[0] + mRectangle[4] / 2 - g.getFontMetrics().stringWidth(mTitle) / 2 + 1, 
                    mRectangle[1] + LISTBOX_OFFSET_TOP + 1);
            }
            g.setColor(mFontColor);
            g.drawString(mTitle, 
                mRectangle[0] + mRectangle[4] / 2 - g.getFontMetrics().stringWidth(mTitle) / 2, 
                mRectangle[1] + LISTBOX_OFFSET_TOP);
        }

        // Render Scroll-Bar
        if (!mUseReference)
        {
            g2d.setPaint( mScrollBarRecGradient );
            g2d.fill( mScrollBarRec );
            g2d.setPaint( Color.CYAN );
            g2d.drawOval( 
                    mRectangle[2] - LISTBOX_OFFSET_LEFT - LISTBOX_SCROLLBAR_WIDTH + 1,
                    mRectangle[1] + LISTBOX_OFFSET_TOP + LISTBOX_TITLE_HEIGHT + 1 + getScrollbarOffset(),
                    LISTBOX_SCROLLBAR_WIDTH-2, LISTBOX_SCROLLBAR_WIDTH-2);       
            g.setColor(Color.BLACK);
            g.drawLine( mRectangle[2] - LISTBOX_OFFSET_LEFT - LISTBOX_SCROLLBAR_WIDTH,
                        mRectangle[1] + LISTBOX_OFFSET_TOP + LISTBOX_TITLE_HEIGHT-1,
                        mRectangle[2] - LISTBOX_OFFSET_LEFT - LISTBOX_SCROLLBAR_WIDTH,
                        mRectangle[3] - LISTBOX_OFFSET_TOP );
        }

        // Render Element-Frame
        g.setColor(Color.BLACK);
        g.drawLine( mRectangle[0] + LISTBOX_OFFSET_LEFT-1,
                    mRectangle[1] + LISTBOX_OFFSET_TOP + LISTBOX_TITLE_HEIGHT-1,
                    mRectangle[0] + LISTBOX_OFFSET_LEFT-1,
                    mRectangle[3] - LISTBOX_OFFSET_TOP );
        g.drawLine( mRectangle[0] + LISTBOX_OFFSET_LEFT-1,
                    mRectangle[1] + LISTBOX_OFFSET_TOP + LISTBOX_TITLE_HEIGHT-1,
                    mRectangle[2] - LISTBOX_OFFSET_LEFT,
                    mRectangle[1] + LISTBOX_OFFSET_TOP + LISTBOX_TITLE_HEIGHT-1);
        g.drawLine( mRectangle[0] + LISTBOX_OFFSET_LEFT,
                    mRectangle[3] - LISTBOX_OFFSET_TOP,
                    mRectangle[2] - LISTBOX_OFFSET_LEFT,
                    mRectangle[3] - LISTBOX_OFFSET_TOP);
        g.drawLine( mRectangle[2] - LISTBOX_OFFSET_LEFT,
                    mRectangle[1] + LISTBOX_OFFSET_TOP + LISTBOX_TITLE_HEIGHT-1,
                    mRectangle[2] - LISTBOX_OFFSET_LEFT,
                    mRectangle[3] - LISTBOX_OFFSET_TOP );
        
        // Render Elements
        int vCurrent = getCurrentElement();
        for (int i = vCurrent; i < vCurrent + mNumberElements; i++) 
        {
            if (mEntries.size() > i && isSelected(i)) {
                g2d.setPaint( mElementRecGradientSelected[i - vCurrent] ); 
            } else {
                g2d.setPaint( mElementRecGradient[i - vCurrent] ); 
            }
            g2d.fill( mElementRec[i - vCurrent] );

            g.setFont(mTitleFont);
            g.setColor(Color.BLACK);
            CBListEntry vEntry = getEntry(i);
            if (vEntry != null) {
                g.drawString( vEntry.show, 
                        (int)(mElementRec[i - vCurrent].getX()) + LISTBOX_ITEM_STRING_OFFSET, 
                        (int) mElementRec[i-vCurrent].getCenterY() + g.getFontMetrics().getHeight() / 4 );
            }
        }
        
    }
    
    /**
     * Inner representation of a CBListBoxSimple-event. It stores the event type
     * (scrollbar/selection change) and the index of the list element (if the
     * event type is scrollbar change, then this is the index of the first shown
     * entry in the listbox).
     */
    public class CBListBoxSimpleEvent {

        /**
         * Index of scrollbar-event.
         */
        public final static int EVENT_SCROLLBAR = 0;

        /**
         * Index of selection-event.
         */
        public final static int EVENT_SELECTION_TRUE = 1;

        /**
         * Index of un-selection-event.
         */
        public final static int EVENT_SELECTION_FALSE = 2;

        /**
         * Index of remove-entry-event.
         */
        public final static int EVENT_REMOVED = 3;

        /**
         * Event type. 
         */
        public int mType = EVENT_SELECTION_FALSE;

        /**
         * Index of the list entry which is affected by the event. 
         */
        public int mIndex = -1;

        /**
         * Constructor of CBListBoxSimpleEvent.
         * @param mode event type
         * @param index list entry index
         */
        public CBListBoxSimpleEvent (int mode, int index) 
        {
            mIndex = index;
            mType = mode;
        }
    }
    
    /**
     * Inner representation of list entries of CBListBox-instances.
     */
    private class CBListEntry {
        
        /**
         * Data connected with the list entry. 
         */
        public String data = null;
        
        /**
         * String representation of the whole entry. 
         */
        public String entry = null;
        
        /**
         * How the entry should be displayed (occationally cut, if too long).
         */
        public String show = null;

        /**
         * 'true' if the entry has been selected. 
         */
        private boolean selected = false;
        
        /**
         * Constructor of CBListEntriy. Creates a new list entry. 
         * @param entry string representation of the list entry
         * @param data data connected with the entry 
         */
        public CBListEntry (String entry, String data) {
            this.entry = entry;
            this.data = data;
        }
        
        /**
         * Gets if the entry is selected.
         * @return the selection of the entry.
         */
        public boolean isSelected () {
            return selected;
        }
        
        /**
         * Unselects the entry.
         */
        public void unSelect () {
            selected = false;
        }
        
        /**
         * Inverts the selection of the element.
         * @return the selection of the element.
         */
        public boolean invertSelection () {
            selected = !selected;
            return selected;
        }
        
        /**
         * Initializes the list entry by calculating the "show" attribute.
         */
        public void initialize () {

            // element initially not selected
            selected = false;

            // left and right border of the (rendered) elements
            int left = mRectangle[0] + LISTBOX_OFFSET_LEFT;
            int right = mRectangle[2] - LISTBOX_OFFSET_LEFT - LISTBOX_SCROLLBAR_WIDTH;
            
            // cut element string if too long
            show = entry;
            boolean changed = false;
            FontMetrics fm = GraphicsEngine.getInstance().getFrame().getFontMetrics(mTitleFont);
            while (fm.stringWidth(show) > right - left - LISTBOX_ITEM_STRING_OFFSET - 10)
            {
                show = show.substring(0, show.length()-1);
                changed = true;
            }
            
            if (changed) {
                show = show + "...";
            }
        }
    }
    
    
    
}
