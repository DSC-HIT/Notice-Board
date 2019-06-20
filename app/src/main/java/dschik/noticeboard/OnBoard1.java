package dschik.noticeboard;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class OnBoard1 extends Activity {

    private ViewPager mSlideViewpager;
    private LinearLayout mDotLayout;
    private TextView[] mDots;

    private Button mNextBtn;
    private Button mBackBtn;
    private int mCurrentPage;

    public int j=0;
    private SliderAdapter sliderAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.on_board1);

        mSlideViewpager = (ViewPager) findViewById(R.id.slideViewPager);
        mDotLayout = (LinearLayout) findViewById(R.id.dotsLayout);

        mNextBtn=(Button)findViewById(R.id.nextButton);
        mBackBtn=(Button)findViewById(R.id.prevButton);

        sliderAdapter = new SliderAdapter(this);
        mSlideViewpager.setAdapter(sliderAdapter);

        addDotsIndicator(0);

        mSlideViewpager.addOnPageChangeListener(viewListener);

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mSlideViewpager.setCurrentItem(mCurrentPage-1);
            }
        });
            mNextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(j == 1) {
                        Log.d("aa","if entry"+j);
                        Intent intent = new Intent(OnBoard1.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        Log.d("aa","else entry"+j);
                        mSlideViewpager.setCurrentItem(mCurrentPage+1);
                    }

                }
            });


    }

    public void addDotsIndicator(int position) {
        mDots = new TextView[3];
        mDotLayout.removeAllViews();
        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.transparentwhite));

            mDotLayout.addView(mDots[i]);
        }

        if (mDots.length > 0) {
            mDots[position].setTextColor(getResources().getColor(R.color.colorwhite));
        }

    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);

            mCurrentPage=position;
            if(position==0){
                mNextBtn.setEnabled(true);
                mBackBtn.setEnabled(false);
                mBackBtn.setVisibility(View.INVISIBLE);
                mNextBtn.setText("SKIP");
                mBackBtn.setText("");
                j=0;
            }
            else if(position==mDots.length-1){
                mBackBtn.setEnabled(true);
                mNextBtn.setEnabled(true);
                mBackBtn.setVisibility(View.VISIBLE);
                mNextBtn.setText("LOGIN");
                mBackBtn.setText("BACK");
                j=1;

            }
            else{
                mBackBtn.setEnabled(true);
                mNextBtn.setEnabled(true);
                mBackBtn.setVisibility(View.VISIBLE);
                mNextBtn.setText("SKIP");
                mBackBtn.setText("BACK");
                j=0;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };
}
