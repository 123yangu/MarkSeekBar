package video.com.myseekbar;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import video.com.myseekbar.view.MarkSeekBar;


public class MainActivity extends ActionBarActivity {
    String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MarkSeekBar markSeekBar = (MarkSeekBar)findViewById(R.id.markSeekBar);
        markSeekBar.setStartProgressText("0");
        markSeekBar.setMaxProgressText("100");
        markSeekBar.setMax(100);
        markSeekBar.setProgress(50);

    }

}
