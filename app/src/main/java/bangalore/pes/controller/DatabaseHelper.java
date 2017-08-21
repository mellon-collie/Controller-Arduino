package bangalore.pes.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nisha on 04-07-2017.
 */
// sqlite database for storing materials and their parameter values
// CRUD operations can also be performed
class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Materials_database";
    private static final String TABLE_NAME = "MaterialsParameters";
    private static final String KEY_NAME = "name";
    private static final String KEY_ID_2 = "parameters_1";
    private static final String KEY_ID_3 = "parameters_2";
    private static final String CREATE_MATERIALS_TABLE="create table " + TABLE_NAME + " (" + KEY_NAME + " TEXT PRIMARY KEY not null," + KEY_ID_2 + " TEXT,"
            + KEY_ID_3 + " TEXT);";
    public DatabaseHelper(Context context){
        super(context,  DATABASE_NAME,null,DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try{
            sqLiteDatabase.execSQL(CREATE_MATERIALS_TABLE);
        }catch (SQLiteException e)
        {
            e.printStackTrace();
        }}

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void insert(Materials materials) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_NAME, materials.getMaterial());
        contentValues.put(KEY_ID_2, materials.getParameter_1());
        contentValues.put(KEY_ID_3, materials.getParameter_2());
        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);

    }

    public int updateMaterials(Materials materials)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();


        contentValues.put(KEY_NAME,materials.getMaterial());
        contentValues.put(KEY_ID_2,materials.getParameter_1());
        contentValues.put(KEY_ID_3,materials.getParameter_2());

        return sqLiteDatabase.update(TABLE_NAME,contentValues,KEY_NAME+" = ? ",new String[]{materials.getMaterial()});
    }

    public void deleteMaterials(String materialName)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        sqLiteDatabase.delete(TABLE_NAME,KEY_NAME+" = ?",new String[]{materialName});
    }

    public List<Materials> getAllMaterials()
    {
        List<Materials> materials_list=new ArrayList<Materials>();
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        String selectQuery="SELECT * FROM "+TABLE_NAME;
        Cursor c=sqLiteDatabase.rawQuery(selectQuery,null);

        if(c!=null)
        {
            c.moveToFirst();
        }
        else
        {
        }
        do{
            Materials materials=new Materials();
            materials.setMaterial(c.getString(c.getColumnIndex(KEY_NAME)));
            materials.setParameter_1(c.getString(c.getColumnIndex(KEY_ID_2)));
            materials.setParameter_2(c.getString(c.getColumnIndex(KEY_ID_3)));
            materials_list.add(materials);
        }while(c.moveToNext()!=false);
        return materials_list;}


    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }


}
