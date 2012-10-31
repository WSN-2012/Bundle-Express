package se.kth.ssvl.tslab.wsn.apps;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class WebData extends ListActivity {
	
	String [] gatewaylist= {
		"Gateway1",
		"Gateway2",
		"Gateway3",
		"Gateway4",
		"Gateway5",
		"Gateway6",
		"Gateway7",
		"Gateway8",
		"Gateway9",
		"Gateway10"
		};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_web_data);
        
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, gatewaylist));
        
        
       /* TextView tv=new TextView(this);
        tv.setText("Web Data");
        setContentView(tv);*/
    

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_web_data, menu);
        return true;
    }*/
    
        ListView lv = getListView();

        // listening to single list item on click
        lv.setOnItemClickListener(new OnItemClickListener() {
          public void onItemClick(AdapterView<?> parent, View view,
              int position, long id) {
        	  
        // selected item 
        String data = ((TextView) view).getText().toString();
        Intent intent1 = new Intent(getApplicationContext(), WebDataContent.class);
        intent1.putExtra("DataContent", data);
        startActivity(intent1);
        		}
	        
        });
        
  }
   
    // List Item selection test
    
 /*public void onListItemClick(ListView parent, View v, int position, long id)
 	
 {
	 Toast.makeText(this, "You have selected" + gatewaylist[position] , Toast.LENGTH_LONG).show();
 }*/
    
}
