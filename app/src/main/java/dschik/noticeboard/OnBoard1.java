package dschik.noticeboard;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class OnBoard1 extends Activity {

    private ViewPager mSlideViewpager;
    private LinearLayout mDotLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.on_board1);

        mSlideViewpager=(ViewPager)findViewById(R.id.slideViewPager);
        mDotLayout=(LinearLayout) findViewById(R.id.dotsLayout);
    }
}
