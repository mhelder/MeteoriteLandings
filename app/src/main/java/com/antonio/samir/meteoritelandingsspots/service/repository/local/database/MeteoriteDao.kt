package com.antonio.samir.meteoritelandingsspots.service.repository.local.database

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite


@Dao
interface MeteoriteDao {

    @Query("SELECT * from meteorites ORDER BY name")
    fun meteoriteOrdered(): DataSource.Factory<Int, Meteorite>

    @Query("SELECT * from meteorites ORDER BY ((reclat-:lat)*(reclat-:lat)) + ((reclong - :lng)*(reclong - :lng)) ASC")
    fun meteoriteOrderedByLocation(lat: Double, lng: Double): DataSource.Factory<Int, Meteorite>

    @Query("SELECT * from meteorites WHERE (LOWER(address) GLOB '*' || :filter|| '*') or (LOWER(name) GLOB '*' || :filter|| '*') ORDER BY ((reclat-:lat)*(reclat-:lat)) + ((reclong - :lng)*(reclong - :lng)) ASC")
    fun meteoriteOrderedByLocationFiltered(lat: Double, lng: Double, filter: String): DataSource.Factory<Int, Meteorite>

    @Query("SELECT * from meteorites WHERE (LOWER(address) GLOB '*' || :filter|| '*') or (LOWER(name) GLOB '*' || :filter|| '*') ORDER BY name ASC")
    fun meteoriteFiltered(filter: String): DataSource.Factory<Int, Meteorite>

    @Query("SELECT * from meteorites WHERE address IS NULL OR LENGTH(address) = 0 ORDER BY id")
    suspend fun meteoritesWithOutAddress(): List<Meteorite>

    @Insert
    suspend fun insertAll(items: List<Meteorite>)

    @Update
    suspend fun update(meteorite: Meteorite)

    @Query("SELECT * from meteorites where id = :meteoriteId LIMIT 1")
    fun getMeteoriteById(meteoriteId: String): LiveData<Meteorite>

    @Query("SELECT count(id) from meteorites")
    suspend fun getMeteoritesCount(): Int

}
