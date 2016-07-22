package cropimageview;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;
import android.widget.TextView;

import com.example.androidserversocket.ClientSocketThread;
import com.example.androidserversocket.ClientText;
import com.example.androidserversocket.ServerText;

import static com.example.androidserversocket.Appconfig.socketArray;

/**
 * Created by multidots on 4/14/2016.
 */
public class ScrollTextViewClient extends TextView {

    // scrolling feature
    public Scroller mSlr;
    int scrollingLen;

    DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
    int width = displayMetrics.widthPixels;
    int height = displayMetrics.heightPixels;

    // milliseconds for a round of scrolling
    private int mRndDuration = 5000;

    // the X offset when paused
    private int mXPaused = 0;

    // whether it's being paused
    private boolean mPaused = true;
    public static boolean isCalled=false;

    /*
    * constructor
    */
    public ScrollTextViewClient(Context context) {
        this(context, null);
        // customize the TextView
        setSingleLine();
        setEllipsize(null);
        setVisibility(INVISIBLE);
    }

    /*
    * constructor
    */
    public ScrollTextViewClient(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
        // customize the TextView
        setSingleLine();
        setEllipsize(null);
        setVisibility(INVISIBLE);
    }


    /*
    * constructor
    */
    public ScrollTextViewClient(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // customize the TextView
        setSingleLine();
        setEllipsize(null);
        setVisibility(INVISIBLE);
    }

    /**
     * begin to scroll the text from the original position
     */
    public void startScroll() {
        // begin from the very right side
        mXPaused = -width;

        // assume it's paused
        mPaused = true;
        System.out.println("111 start :" + width);
        resumeScroll();
    }

    /**
     * resume the scroll from the pausing point
     */
    public void resumeScroll() {

        if (!mPaused)
            return;

        System.out.println("111 resume");

        // Do not know why it would not scroll sometimes
        // if setHorizontallyScrolling is called in constructor.
        setHorizontallyScrolling(true);

        // use LinearInterpolator for steady scrolling
        mSlr = new Scroller(this.getContext(), new LinearInterpolator());
        setScroller(mSlr);

        scrollingLen = calculateScrollingLen();
        System.out.println("111 resume new" + getWidth() + "paused" + mXPaused);
        int distance = scrollingLen - (getWidth() + mXPaused);
        int duration = (new Double(mRndDuration * distance * 1.00000
                / scrollingLen)).intValue();
        System.out.println("111 resume 11 : " + mXPaused + " distance" + distance + "duration :" + duration + "scrolling length :" + scrollingLen);
        setVisibility(VISIBLE);
//        mSlr.startScroll(mXPaused, 0, distance, 0, duration);//Actual remove comment
        mSlr.startScroll(mXPaused, 0, distance, 0, 4000);
        invalidate();
        mPaused = false;
    }

    /**
     * calculate the scrolling length of the text in pixel
     *
     * @return the scrolling length in pixels
     */
    private int calculateScrollingLen() {


        TextPaint tp = getPaint();
        Rect rect = new Rect();
        String strTxt = getText().toString();
        tp.getTextBounds(strTxt, 0, strTxt.length(), rect);
        int scrollingLen = rect.width() + getWidth();
        rect = null;
        System.out.println("111 calculate :" + scrollingLen);
        return scrollingLen+30;
    }

    /**
     * pause scrolling the text
     */
    public void pauseScroll() {

        System.out.println("111 pause");
        if (null == mSlr)
            return;

        if (mPaused)
            return;

        mPaused = true;

        // abortAnimation sets the current X to be the final X,
        // and sets isFinished to be true
        // so current position shall be saved
        mXPaused = mSlr.getCurrX();

        mSlr.abortAnimation();
    }

    @Override
     /*
     * override the computeScroll to restart scrolling when finished so as that
     * the text is scrolled forever
     */
    public void computeScroll() {
        super.computeScroll();

        System.out.println("111 compute" + mSlr.getCurrX());
        if (mSlr.getCurrX()>=scrollingLen){
//            new ClientText().refresh();
        }

        if (!isCalled) {
            if (mSlr.getCurrX() == 0 || mSlr.getCurrX() == 1 || mSlr.getCurrX() == 2 || mSlr.getCurrX() == 3 || mSlr.getCurrX() == 4 || mSlr.getCurrX() == -1 || mSlr.getCurrX() == -2 || mSlr.getCurrX() == -3 || mSlr.getCurrX() == -4) {
                isCalled = true;
                Thread thread = new Thread(new ClientSocketThread("192.168.43.1", 9000));
                thread.start();
            }
        }
        if (mSlr.getCurrX() == scrollingLen) {
            //pauseScroll(); // Not required as of now it puase automatically.
            this.startScroll();
        }

        if (null == mSlr) return;

        if (mSlr.isFinished() && (!mPaused)) {
        }
    }

    public int getRndDuration() {
        return mRndDuration;
    }

    public void setRndDuration(int duration) {
        this.mRndDuration = duration;
    }

    public boolean isPaused() {
        return mPaused;
    }
}
