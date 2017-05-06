package com.xueba.quting.high;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


public class AboutUsActivity extends Activity {
    ImageView ivAboutUs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ivAboutUs=(ImageView)findViewById(R.id.ivAboutUs);
        ivAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                AboutUsActivity.this.finish();
            }
        });
    }
}
