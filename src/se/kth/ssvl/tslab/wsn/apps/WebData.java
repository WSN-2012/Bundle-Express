package se.kth.ssvl.tslab.wsn.apps;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class WebData extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_web_data);
        TextView tv=new TextView(this);
        tv.setText("I am in Tab A..");
        setContentView(tv);
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_web_data, menu);
        return true;
    }*/
}
