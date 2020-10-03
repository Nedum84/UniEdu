package com.uniedu.room


import androidx.lifecycle.LiveData
import androidx.room.*
import com.uniedu.model.EBooks
import com.uniedu.room.TableNames.Companion.TABLE_EBOOKS

@Dao
interface EBooksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upSert(list: List<EBooks>)

    @Query("SELECT * from $TABLE_EBOOKS WHERE book_id  = :id")
    suspend fun getBookById(id: Long): EBooks?


    @Query("SELECT * FROM $TABLE_EBOOKS ORDER BY book_id DESC")
    fun getAllBooks(): LiveData<List<EBooks>>

    @Query("SELECT * FROM $TABLE_EBOOKS WHERE book_title LIKE :id OR course_id LIKE :id  ORDER BY book_title DESC")
    fun getAllBooks(id: String): LiveData<List<EBooks>>


    @Query("DELETE FROM $TABLE_EBOOKS")
    fun delete()
}

