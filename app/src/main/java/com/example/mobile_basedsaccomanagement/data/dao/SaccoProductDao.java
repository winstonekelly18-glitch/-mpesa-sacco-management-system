package com.example.mobile_basedsaccomanagement.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mobile_basedsaccomanagement.data.entity.SaccoProductEntity;

import java.util.List;

@Dao
public interface SaccoProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertProduct(SaccoProductEntity product);

    @Update
    int updateProduct(SaccoProductEntity product);

    @Delete
    int deleteProduct(SaccoProductEntity product);

    @Query("SELECT * FROM sacco_products ORDER BY id ASC")
    LiveData<List<SaccoProductEntity>> getAllProductsLiveData();

    @Query("SELECT * FROM sacco_products ORDER BY id ASC")
    List<SaccoProductEntity> getAllProducts();

    @Query("SELECT * FROM sacco_products WHERE id = :id LIMIT 1")
    SaccoProductEntity getProductById(long id);
}
