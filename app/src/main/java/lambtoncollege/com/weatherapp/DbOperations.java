package lambtoncollege.com.weatherapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



//this class is responsible for all the database operations it has methods for creating database putting values to it
// and getting them.
public class DbOperations extends SQLiteOpenHelper {


    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";

    public static final String PROFILE_TABLE = "profile_table";

    public static final String C_NUMBER = "card_number";
    public static final String CVV = "cvv";
    public static final String EXP_DATE = "exp_date";

    public static final String PAYMENT_TABLE = "payment_table";

    public static final String DATABASE_NAME = "dbWeather";

    public static final int db_version = 1;

    //just a constructor passing database name and version to the base class of database to let the know m here
    public DbOperations(Context context) {
        super(context, DATABASE_NAME, null, db_version);
    }



//this is the query for creating Profile database
    public String PROFILE_QUERY = "CREATE TABLE " + PROFILE_TABLE + "(" + FIRST_NAME + " TEXT," + LAST_NAME + " TEXT);";

//this is the query for creating Payment database
    public String PAYMENT_QUERY = "CREATE TABLE " + PAYMENT_TABLE + "(" + C_NUMBER + " TEXT," + CVV + " TEXT," + EXP_DATE + " TEXT);";

    @Override
    public void onCreate(SQLiteDatabase sdb) {
        //creating tables executing the queries
        sdb.execSQL(PROFILE_QUERY);
        sdb.execSQL(PAYMENT_QUERY);

    }




    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PROFILE_TABLE);
        onCreate(db);
    }






//this method is setting profile information into table
    public void putProfile(DbOperations dop, String fName, String lName){

            SQLiteDatabase sqLiteDatabase = dop.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(FIRST_NAME, fName);
            cv.put(LAST_NAME, lName);
            sqLiteDatabase.insert(PROFILE_TABLE, null, cv);

        }


//this method is setting payment information into table

    public void putPayment(DbOperations dop, String cardNumber, String cvv, String expDate){
        SQLiteDatabase sqLiteDatabase = dop.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(C_NUMBER, cardNumber);
        cv.put(CVV, cvv);
        cv.put(EXP_DATE, expDate);
        sqLiteDatabase.insert(PAYMENT_TABLE, null, cv);

    }

//this method retrieve  the profile information saved into database
    public Cursor getProfileInformation(DbOperations dop) {
        SQLiteDatabase SQ = dop.getReadableDatabase();
        String[] coloumns = {FIRST_NAME, LAST_NAME,};
        Cursor CR = SQ.query(PROFILE_TABLE, coloumns, null, null, null, null, null);
        return CR;

    }
//this method retrieve  the payment information saved into database
    public Cursor getPaymentInformation(DbOperations dop) {
        SQLiteDatabase SQ = dop.getReadableDatabase();
        String[] coloumns = {C_NUMBER, CVV,EXP_DATE,};
        Cursor CR = SQ.query(PAYMENT_TABLE, coloumns, null, null, null, null, null);
        return CR;

    }



//this just gets the count of rows in table
    public int getCount(String tname) {
        String countQuery = "SELECT  * FROM " + tname;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    









}
