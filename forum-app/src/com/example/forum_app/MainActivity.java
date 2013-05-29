package com.example.forum_app;


import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnChildClickListener, OnItemClickListener{
	
	// Define here how many "Newest Posts" should be displayed:
	final static int NUMBER_NEWEST_POSTS = 3;
	

	private ExpandableListView list_newest_posts;
	private ListView list_categories;
	private Button login;
	private Button register;

	private List<JSONObject> json_categories;
	private List<JSONObject> json_newest_posts;
	
	
	/** Getter **/
	public List<JSONObject> getJsonNewestPosts() { return json_newest_posts; };
	public List<JSONObject> getJsonCategories() { return json_categories; };
	public int getMaxNumberNewesPosts() { return NUMBER_NEWEST_POSTS; };
	
	
	
    /**
    * onCreate(Bundle)
    * Starts up the Activity
    */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Resources res = getResources();

		
		/* ******************************************* *
		 * The expandable list for the newest posts: 
		 * ******************************************* */
		list_newest_posts = (ExpandableListView) findViewById(R.id.list_newest_posts);
	
		String query_newest_posts = "SELECT Subject FROM Thread WHERE ThreadID IN ";
		query_newest_posts += "(SELECT DISTINCT ThreadID FROM Post WHERE ThreadID IN ";
		query_newest_posts += "(SELECT ThreadID FROM Post ORDER BY CreateDate DESC FETCH FIRST ";
		query_newest_posts += NUMBER_NEWEST_POSTS; 
		query_newest_posts += " ROWS ONLY))";
		
		// Send the query to the Database:
		try {
			json_newest_posts = this.sendQuery(query_newest_posts);
			if(json_newest_posts == null)
				throw new NullPointerException("The DB query returned null");
		} catch (NullPointerException e) {
			Log.d("MainActivity", "JSON Parser : Zugriff auf Datenbank fehlgeschlagen!");
			e.printStackTrace();
		}

		// This is needed for the Custom Adapter to handle the expandable list:
		ArrayList<Parent> arrayParents = new ArrayList<Parent>();
		ArrayList<TextView> arrayChildren = new ArrayList<TextView>();
		Parent parent = new Parent();
		parent.setTitle(res.getString(R.string.new_posts));

		// If there are less newer posts than in NUMBER_NEWEST_POSTS, display the
		// ones that are available anyway:
		int max = ((json_newest_posts.size() <= NUMBER_NEWEST_POSTS)
						? json_newest_posts.size() : NUMBER_NEWEST_POSTS);
		
		// From each row of the JSON Table with the newest posts we extract the subject
		// and insert it into the child-array. Then we set the parent correctly:
		for (int i = 0; i < max; i++) {
			try {
				TextView child = new TextView(getApplicationContext());
				child.setText(json_newest_posts.get(i).getString("subject"));		
				arrayChildren.add(child);
			} catch (JSONException e) {
				Log.d("MainActivity", "JSON Parser : Zugriff auf neueste Posts fehlgeschlagen!");
				e.printStackTrace();
			}
		}
		parent.setArrayChildren(arrayChildren);
		arrayParents.add(parent);
		
		// Now we can set the adapter:
		MyCustomAdapter adapter = new MyCustomAdapter(MainActivity.this, arrayParents);
		list_newest_posts.setAdapter(adapter);
		
		list_newest_posts.setOnChildClickListener(this);
		
		// the next line is there so the list starts expanded: 
		list_newest_posts.expandGroup(0);
		
		

		/* ******************************************* *
		 * The normal list for the categories: 
		 * ******************************************* */
		list_categories = (ListView) findViewById(R.id.list_categories);
		
		String query_categories = "SELECT * FROM Category";
		// Send the query to the Database:
		try {
			json_categories = this.sendQuery(query_categories);
			if(json_categories == null)
				throw new NullPointerException("The DB query returned null");
		} catch (NullPointerException e) {
			Log.d("MainActivity", "JSON Parser : Zugriff auf Datenbank fehlgeschlagen!");
			e.printStackTrace();
		}
		
		final ArrayList<String> list = new ArrayList<String>();
		
		// From each row of the JSON Table with the categories we extract the name
		// and insert it into the list
		for (int i = 0; i < json_categories.size(); i++) {
			try {
				list.add(json_categories.get(i).getString("name"));
			} catch (JSONException e) {
				Log.d("MainActivity", "JSON Parser : Zugriff auf Categories fehlgeschlagen!");
				e.printStackTrace();
			}
		}
		
		// Now we can set the adapter:
		list_categories.setAdapter(new ArrayAdapter<String>(this, R.layout.list_view, list));
		
		list_categories.setOnItemClickListener(this);

		

		/* ******************************************* *
		 * The The bottom Bar with Login and Register: 
		 * ******************************************* */
		login = (Button) findViewById(R.id.login);
		register = (Button) findViewById(R.id.register);
		
		final Intent login_intent = new Intent(this, LoginActivity.class);
		final Intent register_intent = new Intent(this, RegisterActivity.class);
		
		login.setOnClickListener(new OnClickListener() {
			public void onClick(View v) 
	        {   
				Log.d("MainActivity", "Login OnClickListener Fired");
	            startActivity(login_intent);      
	            finish();
	        }
	    });
		
		register.setOnClickListener(new OnClickListener() {
			public void onClick(View v) 
	        {   
				Log.d("MainActivity", "Register OnClickListener Fired");
	            startActivity(register_intent);      
	            finish();
	        }
	    });
	
	}
	
	
    /**
    * onCreateOptionsMenu(Menu)
    * Inflate the menu; this adds items to the action bar if it is present.
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
    * sendQuery(String)
    * @param query holds string with full SQL query
    * @return is a list where every item equals one row from query answer
    */
    public List<JSONObject> sendQuery(String query){
    	
    	DBOperator dboperator = DBOperator.getInstance();
    	return dboperator.sendQuery(query);
    	
    }
    
    /**
    * onChildClick(ExpandableListView, View, int, int, long)
    * This is the OnClickListener for the Newest Posts List
    */
	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		//TODO: Start the Activity with the new Thread here!
		String test = new String();
		try {
			test = json_newest_posts.get(childPosition).getString("subject");
		} catch (JSONException e) {
			Log.d("MainActivity", "JSON Parser : Zugriff auf neueste Posts fehlgeschlagen!");
			e.printStackTrace();
		}
		Log.d("MainActivity", "Newest Posts OnChildClickListener Fired with " + test);
		return false; // set to true (95% sure ;-) )
	}
	
	
    /**
    * onItemClick(AdapterView<?>, View, int, long)
    * This is the OnClickListener for the Categories List
    */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		//TODO: Start the Activity with the Categorie here!
		TextView test = (TextView) arg1;
		Log.d("MainActivity", "Categories OnChildClickListener Fired with " + test.getText());
	}
	
}
