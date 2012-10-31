package se.kth.ssvl.tslab.wsn.apps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class WebDataContent extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_data_content);
       
        TextView gatewayName = (TextView) findViewById(R.id.textView1);
        
        Intent i = getIntent();
        // getting attached intent data
        String data = i.getStringExtra("DataContent");
        // displaying selected product name
        gatewayName.setText(data);
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_web_data_content, menu);
        return true;
    }*/
}
