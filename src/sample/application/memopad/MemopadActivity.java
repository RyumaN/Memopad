package sample.application.memopad;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Selection;
import android.widget.EditText;
import java.text.DateFormat;
import java.util.Date;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.content.ContentValues;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class MemopadActivity extends Activity{
	Paint paint;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState){
    	
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main);
        
        EditText et = (EditText) this.findViewById(R.id.editText1);
        SharedPreferences pref = this.getSharedPreferences("MemoPrefs", MODE_PRIVATE);
        et.setText(pref.getString("memo",""));
        et.setSelection(pref.getInt("cursor", 0));
    }
    
    @Override
    public void onStop(){
    	super.onStop();
    	EditText et = (EditText) this.findViewById(R.id.editText1);
    	SharedPreferences pref = this.getSharedPreferences("MemoPrefs", MODE_PRIVATE);
    	SharedPreferences.Editor editor = pref.edit();
    	editor.putString("memo", et.getText().toString());
    	editor.putInt("cursor",Selection.getSelectionStart(et.getText()));
    	editor.commit();
    	}
    
public void saveMemo(){
    	
		EditText et = (EditText)this.findViewById(R.id.editText1);
    	String title;
    	String memo = et.getText().toString();
    	
    	if(memo.trim().length()>0){
    		
    		if(memo.indexOf("\n") == -1){
    			title = memo.substring(0, Math.min(memo.length(), 20));
    		}
    		else{
    			title = memo.substring(0, Math.min(memo.indexOf("\n"), 20));
    		}
    		
    		String ts = DateFormat.getDateTimeInstance().format(new Date());
    		MemoDBHelper memos = new MemoDBHelper(this);
    		SQLiteDatabase db = memos.getWritableDatabase();
    		ContentValues values = new ContentValues();
    		values.put("title", title + "\n" + ts);
    		values.put("memo", memo);
    		db.insertOrThrow("memoDB", null, values);
    		memos.close();
    		}
    	}

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	// TODO 自動生成されたメソッド・スタブ
	super.onActivityResult(requestCode, resultCode, data);
	if(resultCode == RESULT_OK){
		EditText et = (EditText) findViewById(R.id.editText1);
		
		switch(requestCode){
		case 0:
			et.setText(data.getStringExtra("text"));
			break;
		}
	}
}

@Override
public boolean onCreateOptionsMenu(Menu menu) {
	// TODO 自動生成されたメソッド・スタブ
	MenuInflater mi = this.getMenuInflater();
	mi.inflate(R.menu.menu, menu);
	return super.onCreateOptionsMenu(menu);
}

@Override
public boolean onOptionsItemSelected(MenuItem item) {
	
	EditText et = (EditText) this.findViewById(R.id.editText1);
	switch(item.getItemId()){
	case R.id.menu_save:
		this.saveMemo();
		break;
	case R.id.menu_open:
		Intent i = new Intent(this,MemoList.class);
		this.startActivityForResult(i,0);
		break;
	case R.id.menu_new:
		et.setText("");
		break;
	case R.id.menu_color_change:
		final String[] items = getResources().getStringArray(R.array.ColorName);
		final int[] colors = getResources().getIntArray(R.array.Color);
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setTitle(R.string.menu_color_change);
		ab.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				paint.setColor(colors[item]);
				}
		});
		ab.show();
		break;
	}
	return super.onOptionsItemSelected(item);
}
}