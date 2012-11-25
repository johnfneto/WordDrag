package com.appsolve.worddrag;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.appsolve.worddrag.R;

public class DataBaseHelper extends SQLiteOpenHelper{
	
    public static final String DEBUG_TAG = "GrabWordLog";
	
    int id = 0;
    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/com.appsolve.worddrag/databases/";
 
    private static String DB_NAME = "Synonyms.db";
 
    private SQLiteDatabase myDataBase; 
 
    private final Context myContext;
 
    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DataBaseHelper(Context context) {
 
    	super(context, DB_NAME, null, 1);
        this.myContext = context;
    }	
 
  /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException{
 
    	boolean dbExist = checkDataBase();
    	SQLiteDatabase db_Read = null;
    	
    	if(dbExist){
    		Log.d(DEBUG_TAG, "Database already exist--------------------");
    		//do nothing - database already exist
    	}else{
 
    		//By calling this method and empty database will be created into the default system path
               //of your application so we are gonna be able to overwrite that database with our database.
    		Log.d(DEBUG_TAG, "Database doesnt exist--------------------");
        	db_Read = this.getReadableDatabase();
        	db_Read.close();
 
        	try {
 
    			copyDataBase();
    			Log.d(DEBUG_TAG, "Copying Database--------------------");
 
    		} catch (IOException e) {
 
        		throw new Error("Error copying database");
 
        	}
    	}
 
    }
 
    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){
 
    	SQLiteDatabase checkDB = null;
 
    	try{
    		String myPath = DB_PATH + DB_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
 
    	}catch(SQLiteException e){
 
    		//database does't exist yet.
 
    	}
 
    	if(checkDB != null){
 
    		checkDB.close();
 
    	}
 
    	return checkDB != null ? true : false;
    }
 
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{
 
    	//Open your local db as the input stream
    	InputStream myInput = myContext.getAssets().open(DB_NAME);
 
    	// Path to the just created empty db
    	String outFileName = DB_PATH + DB_NAME;
 
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
 
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
 
    }
 
    public void openDataBase() throws SQLException{
 
    	//Open the database
        String myPath = DB_PATH + DB_NAME;
    	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
		//Log.d(DEBUG_TAG, "Open Database--------------------");
    }
 
    @Override
	public synchronized void close() {
 
    	    if(myDataBase != null)
    		    myDataBase.close();
 
    	    super.close();
    	    //Log.d(DEBUG_TAG, "Close Database--------------------");
	}
 
	@Override
	public void onCreate(SQLiteDatabase db) {
 
	}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
 
	}
 
	public int getAllEntries()	{
		int totalEntries;
	    Cursor cursor = myDataBase.rawQuery("SELECT COUNT(Word) FROM WordMaster", null);
	            if(cursor.moveToFirst()) {
	            	totalEntries = cursor.getInt(0);
	            	cursor.close();
	            	return totalEntries;
	                //return cursor.getInt(0);
	            }
            	totalEntries = cursor.getInt(0);
            	cursor.close();
            	return totalEntries;
	            //return cursor.getInt(0);
	}

	public int getSynCount()	{
		int totalEntries;
	    Cursor cursor = myDataBase.rawQuery("SELECT COUNT(Word) FROM WordMaster", null);
	            if(cursor.moveToFirst()) {
	            	totalEntries = cursor.getInt(0);
	            	cursor.close();
	            	return totalEntries;
	                //return cursor.getInt(0);
	            }
            	totalEntries = cursor.getInt(0);
            	cursor.close();
            	return totalEntries;
	            //return cursor.getInt(0);
	}
	
	public String getRandomEntry()	{
		String word;
		id = getAllEntries();
    	//Log.d(DEBUG_TAG, "id = " + id);
		Random random = new Random();
		int rand = random.nextInt(getAllEntries());
		if(rand == 0)
			++rand;
	    Cursor cursor = myDataBase.rawQuery("SELECT Word FROM WordMaster WHERE _id = " + rand, null);
            if(cursor.moveToFirst()) {
            	word = cursor.getString(0);
            	cursor.close();
            	return word;
                //return cursor.getString(0);
            }
    	word = cursor.getString(0);
    	cursor.close();
    	return word;
        //return cursor.getString(0);
	}

	public String getWordDef(String word)	{
		String wordNo;
		//Log.d(DEBUG_TAG, "word = " + word);
	    Cursor cursor = myDataBase.rawQuery("SELECT Meaning FROM WordMaster WHERE Word = '" + word + "'", null);
	            if(cursor.moveToFirst()) {
	            	wordNo = cursor.getString(0);
	            	cursor.close();
	            	return wordNo;
	                //return cursor.getString(0);
	            }
	            wordNo = cursor.getString(0);
            	cursor.close();
            	return wordNo;
	            //return cursor.getString(0);	
	}

	
	public String getWord(String id)	{
		String word;
	
    	Log.d(DEBUG_TAG, "id = " + id);

	    Cursor cursor = myDataBase.rawQuery("SELECT Word FROM WordMaster WHERE _id = " + id, null);
            if(cursor.moveToFirst()) {
            	word = cursor.getString(0);
            	cursor.close();
            	return word;
                //return cursor.getString(0);
            }
    	word = cursor.getString(0);
    	cursor.close();
    	Log.d(DEBUG_TAG, "word = " + word);
    	return word;
	}
	
	public ArrayList<LevelDetails> getLevels() {
		Cursor cursor;
		Integer flag;
		
    	ArrayList<LevelDetails> results = new ArrayList<LevelDetails>();

    	cursor = myDataBase.rawQuery("SELECT * FROM WordMaster", null);
    	   
    	   if(cursor.moveToFirst())
    	   {
    	       do
    	       {
    	    	   LevelDetails levelDetails = new LevelDetails();
    	    	   
    	    	   
 
    	    	   
    	    	   
    	    	   levelDetails.setWord(cursor.getString(1));
    	    	   flag = cursor.getInt(2);
    	    	   levelDetails.setLocked((flag == 1)? true : false);
    	    	   levelDetails.setSecs(cursor.getInt(3));
    	    	   levelDetails.setStars(cursor.getInt(4));    	    	   
    	    	   //Log.d(DEBUG_TAG, "getWord= " + levelDetails.getWord());
    	    	   //Log.d(DEBUG_TAG, "getLocked= " + levelDetails.getLocked());
    	    	   //Log.d(DEBUG_TAG, "getSecs= " + levelDetails.getSecs());
    	    	   //Log.d(DEBUG_TAG, "getStars= " + levelDetails.getStars());
    	           results.add(levelDetails);
    	       }while(cursor.moveToNext());
    	       if(cursor != null && !cursor.isClosed())
    	          cursor.close();
    	   }
	       if(cursor != null && !cursor.isClosed())
 	          cursor.close();
    	  
	    /*   
   		for (int i=0; i<results.size();i++)
			Log.d(DEBUG_TAG, "results= " + results.get(i).getWord());
   		*/
    	return results;		
	}
	
	
	
	public ArrayList<ArrayList<String>> getScores(String sortType) {
		int totalScores;
		String[] columnNames;
		Cursor cursor2;
		
		//Log.d(DEBUG_TAG, "getScores in");
		Cursor cursor1 = myDataBase.rawQuery("SELECT COUNT(Score) FROM Scores", null);
        cursor1.moveToFirst();
        totalScores = cursor1.getInt(0);
        cursor1.close();

    	//Log.d(DEBUG_TAG, "totalScores = " + Integer.toString(totalScores));
    	
    	
    	   ArrayList<ArrayList<String>> results = new ArrayList<ArrayList<String>>();
    	   //if (sortType.equals("rank"))
    		   cursor2 = myDataBase.rawQuery("SELECT * FROM Scores ORDER BY Score DESC", null);
    	   //else
    		   //cursor2 = myDataBase.rawQuery("SELECT * FROM Scores ORDER BY Date DESC", null);
    	   
    	   if(cursor2.moveToFirst())
    	   {
    	       do
    	       {
    	           ArrayList<String> recipe = new ArrayList<String>();
    	           //recipe.add(cursor2.getString(1));
    	           recipe.add(cursor2.getString(2));
    	           recipe.add(Integer.toString(cursor2.getInt(3)));
    	           if (sortType.equals("date")) 
    	        	   recipe.add("0");
    	           results.add(recipe);
    	       }while(cursor2.moveToNext());
    	       if(cursor2 != null && !cursor2.isClosed())
    	          cursor2.close();
    	   }
	       if(cursor2 != null && !cursor2.isClosed())
 	          cursor2.close();
    	   
   		//for (int i=0; i<results.size();i++)
			//Log.d(DEBUG_TAG, "results= " + results.get(i));
   		
    	return results;
		

	}
	
	
	
	
	public void addScore(String currentDateandTime, String noCorrectAnswers ) {
		Log.d(DEBUG_TAG, "add: " + currentDateandTime+" ; " + noCorrectAnswers);
		myDataBase.execSQL("INSERT INTO Scores (Date, Score) VALUES ('"+currentDateandTime+"', '"+noCorrectAnswers+"') ");
		
	}
	
	public void deleteScores() {
		
		myDataBase.delete("Scores", null, null);
	}

	public String[] getSyn(String wordID) {
		//String wordID =  "";
		String syn[] = new String[4];
		Cursor cursor;
		int i = 0;
		
	   cursor = myDataBase.rawQuery("SELECT Word FROM Synonyms WHERE WordMaster_id = '" + wordID + "'", null);

        	   
        	   if(cursor.moveToFirst()) {
        	       do
        	       {
        	           syn[i] = cursor.getString(0); 
        	           i++;
        	       }while(cursor.moveToNext());
        	       if(cursor != null && !cursor.isClosed())
        	          cursor.close();
        	   }
    	       if(cursor != null && !cursor.isClosed())
     	          cursor.close();
		return syn;
	}

	
	public String getRandomSyn()	{
		String word;
		id = getSynCount();
    	//Log.d(DEBUG_TAG, "id = " + id);
		Random random = new Random();
		int rand = random.nextInt(getSynCount());
		if(rand == 0)
			++rand;
	    Cursor cursor = myDataBase.rawQuery("SELECT Word FROM Synonyms WHERE _id = " + rand, null);
            if(cursor.moveToFirst()) {
            	word = cursor.getString(0);
            	cursor.close();
            	return word;
                //return cursor.getString(0);
            }
    	word = cursor.getString(0);
    	cursor.close();
    	return word;
        //return cursor.getString(0);
	}	
	
	
	public String[] getExtra(String[] syn) {
		String extra[] = new String[4];
		
		do {
			extra[0] = getRandomSyn();
		} while (extra[0].equals(syn[0]) || extra[0].equals(syn[1]) || 
				 extra[0].equals(syn[2]) || extra[0].equals(syn[3]));
		
		for (int i=1;i<4;i++) {
			do {
				extra[i] = getRandomSyn();
			} while (extra[i].equals(syn[0]) || extra[i].equals(syn[1]) || 
					 extra[i].equals(syn[2]) || extra[i].equals(syn[3]) ||
					 extra[i].equals(extra[0]) || extra[i].equals(extra[((i+1)%3)==0?3:(i+1)%3]) || 
					 extra[i].equals(extra[((i+2)%3)==0?3:(i+2)%3]));
				
				
			//for (int j=0;j<=i;j++) {
				//if (!tempWord.equals(extra[j])) 
					
			//}
		}
		
		
		
		return extra;
	}

}
