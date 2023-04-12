package com.grzegorz.room.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Nota")
public class Nota {
    @PrimaryKey(autoGenerate = true)
    public int noteId;
//    public int sid;
    @ColumnInfo
    public String name;
    @ColumnInfo(name = "titulo")
    public String titulo;
    @ColumnInfo(name = "cuerpo")
    public String cuerpo;
}