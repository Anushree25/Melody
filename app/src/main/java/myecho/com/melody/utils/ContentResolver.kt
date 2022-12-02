package myecho.com.melody.utils

import android.app.Activity
import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import myecho.com.melody.MainActivity
import myecho.com.melody.Songs

class ContentResolver {

    companion object{

        fun getSongsFromThePhone(mActivity:Context): ArrayList<Songs> {

            var songs= ArrayList<Songs>()
            var contentResolver=mActivity?.contentResolver
            var songuri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

            var songcursor=contentResolver?.query(songuri,null,null,null,null)

            if(songcursor!=null && songcursor.moveToFirst()) {
                var songId = songcursor.getColumnIndex(MediaStore.Audio.Media._ID)
                var songtitle = songcursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
                var songArtist = songcursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
                var songDetails = songcursor.getColumnIndex(MediaStore.Audio.Media.DATA)
                var songeDate = songcursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
                var songPath = songcursor.getColumnIndex(MediaStore.Audio.Media.RELATIVE_PATH)

               // var uriSong = songcursor.getColumnIndex(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)


                while (songcursor.moveToNext()) {
                    var id = songcursor.getLong(songId)
                    var title = songcursor.getString(songtitle)
                    var artist = songcursor.getString(songArtist)
                    var details = songcursor.getString(songDetails)
                    var date = songcursor.getLong(songeDate)
                    var path = songcursor.getString(songPath)
                   // var uri = songcursor.getString(uriSong)


                    songs.add(Songs(id, title, artist, details, date, path,""))

                }


            }
            return songs
        }




        fun updateSong(context: Context, song: Songs){

            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
                var contentResolver= context.contentResolver
                val mediaId = MediaStore.Audio.Media._ID
                val selection = "${MediaStore.Audio.Media._ID} = ?"
                val selectionArgs = arrayOf(song.songId.toString())
                val projection = arrayOf(
                    MediaStore.Audio.Media.TITLE
                )


                val updatedSongDetails = ContentValues().apply {
                    put(MediaStore.Audio.Media.TITLE, song.songTitle)
                }
                  val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    song.songId)
                try {
                    var songUpdated = contentResolver?.update( MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, updatedSongDetails, selection,selectionArgs)

                    if (songUpdated != null) {
                        Log.d("Updated song","Content")
                        if(songUpdated != 0 ){
                          Toast.makeText(context,"Updated Song",Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(context,"Sorry song not updated.The implementation is in progress",Toast.LENGTH_SHORT).show()
                        }
                    }

                }catch (securityException: Exception) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        val collection = ArrayList<Uri>();
                        collection.add(contentUri);
                        val pendingIntent = MediaStore.createWriteRequest(contentResolver, collection);
                        (context as MainActivity).startIntentSenderForResult(
                            pendingIntent.intentSender,
                            8,
                            null,
                            0,
                            0,
                            0,
                            null
                        )

                    }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (securityException is RecoverableSecurityException) {
                            val exception = securityException
                            val pendingIntent = exception.getUserAction().getActionIntent();
                            (context as Activity).startIntentSenderForResult(
                                pendingIntent.intentSender,
                                8, null, 0,
                                0, 0, null
                            )
                        } else {
                            securityException.printStackTrace()
                        }
                    }

                }

        }
    }

    fun deleteSong(context: Context, song: Songs
        ){

                var contentResolver= context.contentResolver

                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    song.songId)

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                    try {
                        var songDeleted = contentResolver?.delete(contentUri,null,null)
                        // var getSong=contentResolver?.update(contentUri,updatedSongDetails,null,null)

                        if (songDeleted != null) {
                            if(songDeleted> 0){
                                Log.d("Update song","Content")
                            }else{
                                Log.d("Song not updated","Content")
                            }
                        }

                    }catch (securityException: Exception) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            if (securityException is RecoverableSecurityException) {
                                val exception = securityException
                                val pendingIntent = exception.getUserAction().getActionIntent();
                                (context as Activity).startIntentSenderForResult(
                                    pendingIntent.intentSender,
                                    9, null, 0,
                                    0, 0, null
                                )
                            } else {
                                securityException.printStackTrace()
                            }
                        }

                }}else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val collection = ArrayList<Uri>();
                    collection.add(contentUri);
                    val pendingIntent = MediaStore.createDeleteRequest(contentResolver, collection);
                    (context as MainActivity).startIntentSenderForResult(
                        pendingIntent.intentSender,
                        9,
                        null,
                        0,
                        0,
                        0,
                        null
                    )

                }


        }
        }

}





