package myecho.com.melody

import android.os.Parcel
import android.os.Parcelable

 class  Songs(songId:Long,songTitle:String,artist:String,details:String,dateIndex:Long,path : String, uri :String):Parcelable{
    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest?.writeValue(songId)
        dest?.writeString(songTitle)
        dest?.writeString(artist)
        dest?.writeString(details)
        dest?.writeValue(dateIndex)
        dest?.writeValue(path)
        dest?.writeValue(uri)
    }

    override fun describeContents(): Int {

        return  0
    }


     var songId:Long=0
     var songTitle:String?=null
     var artist:String?=null
     var details:String?=null
     var dateIndex:Long=0
     var path : String? = null
     var uri : String? = null



    init {
         this.songId=songId
         this.songTitle=songTitle
         this.artist=artist
         this.details=details
         this.dateIndex=dateIndex
         this.path = path
         this.uri = uri
     }

    //The comparators are used to compare two entities together with each other

    object Statified {
      //Sort the songs according to name
        var nameComparator: Comparator<Songs> = Comparator<Songs> { song1, song2 ->
            val songOne = song1.songTitle?.toUpperCase()
            val songTwo = song2.songTitle?.toUpperCase()
            songOne?.compareTo(songTwo as String) as Int
        }
        //Sort the songs according to list
        var dateComparator: Comparator<Songs> = Comparator<Songs> { song1, song2 ->
            val songOne = song1.dateIndex.toDouble()
            val songTwo = song2.dateIndex.toDouble()
            songTwo.compareTo(songOne)
        }
    }


}