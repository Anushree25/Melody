package myecho.com.melody

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class EchoDatabase:SQLiteOpenHelper{

    var songList:ArrayList<Songs>?=null


    val DB_Query = ("create table " + Staticated.TABLE_NAME + "("
            + Staticated.COLUMN_ID + " Integer, " + Staticated.COLUMN_TITLE + " text not null, " + Staticated.COLUMN_ARTIST + " text not null, "
            + Staticated.COLUMN_PATH + " integer)")

    object Staticated{

        val DB_NAME="FavoriteDatabase"
        val DB_VERSION=1

        val TABLE_NAME:String="FavoriteTable"
        val COLUMN_ID:String="SongID"
        val COLUMN_TITLE:String="SongTitle"
        val COLUMN_ARTIST:String="SongArtist"
        val COLUMN_PATH:String="SongPath"



    }

    override fun onCreate(db: SQLiteDatabase?) {

        db?.execSQL(DB_Query)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    //Add your favorite song into db
    fun storeAsFavorite(id:Int?,title:String?,artist:String?,path:String?){

        val db=this.writableDatabase
        var contentValues=ContentValues()
        contentValues.put(Staticated.COLUMN_ID,id)
        contentValues.put(Staticated.COLUMN_TITLE,title)
        contentValues.put(Staticated.COLUMN_ARTIST,artist)
        contentValues.put(Staticated.COLUMN_PATH,path)

        //Insert data to specific table
        db.insert(Staticated.TABLE_NAME,null,contentValues)
        //To prevent data leakage
        db.close()



    }
    //Get all data from the database
    fun fetchDbList():ArrayList<Songs>?{
        try {
            songList=ArrayList<Songs>()
            val db = this.readableDatabase
            val query = "Select * from " + Staticated.TABLE_NAME
            var cusror = db.rawQuery(query, null)

            if (cusror.moveToFirst()) {

                do {

                    var id = cusror.getInt(cusror.getColumnIndexOrThrow(Staticated.COLUMN_ID))
                    var title = cusror.getString(cusror.getColumnIndexOrThrow(Staticated.COLUMN_TITLE))
                    var artist = cusror.getString(cusror.getColumnIndexOrThrow(Staticated.COLUMN_ARTIST))
                    var path = cusror.getString(cusror.getColumnIndexOrThrow(Staticated.COLUMN_PATH))

                    songList?.add(Songs(id.toLong(), title, artist, path, 0,"null","null"))

                } while (cusror.moveToNext())

            } else {

                return null
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return  songList

    }

    fun checkIfIdExists(id:Int):Boolean?{

        var storeId=-1019
        val db=this.readableDatabase
        val query="Select * from "+Staticated.TABLE_NAME + " where SongID = $id"
        var cusror=db.rawQuery(query,null)

        if(cusror.moveToFirst()){

            do{

                storeId=cusror.getInt(cusror.getColumnIndexOrThrow(Staticated.COLUMN_ID))


            }while(cusror.moveToNext())

        }else{

            return false
        }

        return storeId!=-1019

    }

    //If user want to remove the song from his favorite list,below function is used
    fun deleteValueFromTheDatabase(id:Int){


        val db=this.writableDatabase
        db.delete(Staticated.TABLE_NAME,Staticated.COLUMN_ID+"="+id,null)
        //Close the db connection whenever we use writable option
        db.close()
    }


    fun checkSizeOfDb():Int{

        var counter=0
        val db=this.readableDatabase
        val query="Select * from "+ Staticated.TABLE_NAME
        var cusror=db.rawQuery(query,null)

        if(cusror.moveToFirst()){

            do{

               counter=counter+1

            }while(cusror.moveToNext())

        }else{

            return 0
        }

       return  1


    }

    constructor(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : super(context, name, factory, version)

    constructor(context: Context) : super(context, Staticated.DB_NAME, null, Staticated.DB_VERSION)
}
