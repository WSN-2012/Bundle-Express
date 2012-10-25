package se.kth.ssvl.tslab.wsn.apps;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class DaemonConfig extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daemon_config);
       /* TextView tv=new TextView(this);
        tv.setText("Daemon Configuration");
        setContentView(tv);*/
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_daemon_config, menu);
        return true;
    }*/
    
    
    /** Called when the user clicks the Send button */
    public void UpdateEID(View view) {
        // Do something in response to button
    	//Intent intent = new Intent(this, ConfigEditor.class);
    	//EditText editText = (EditText) findViewById(R.id.UpdateServerEID);
    	//String message = editText.getText().toString();
    	//intent.putExtra(EXTRA_MESSAGE, message);
    	//startActivity(intent);
    	}
}
