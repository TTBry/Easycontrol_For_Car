package com.autonavi.amapauto.helper;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import com.autonavi.amapauto.entity.AppData;
import com.autonavi.amapauto.entity.Device;
import com.autonavi.amapauto.entity.MonitorEvent;

public class DbHelper extends SQLiteOpenHelper {

  private static final String dataBaseName = "app.db";
  private static final int version = 19;
  private final String tableName = "DevicesDb";
  private final String monitorTableName = "MonitorEventsDb";

  public DbHelper(Context context) {
    super(context, dataBaseName, null, version);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE " + tableName + " (\n" + "\t uuid text PRIMARY KEY,\n" + "\t type integer,\n" + "\t name text,\n" + "\t address text,\n" + "\t specified_app text,\n" + "\t isAudio integer,\n" + "\t maxSize integer,\n" + "\t maxFps integer,\n" + "\t maxVideoBit integer,\n" + "\t setResolution integer,\n" + "\t defaultFull integer,\n" + "\t useH265 integer,\n" + "\t useOpus integer,\n" + "\t connectOnStart integer,\n" + "\t clipboardSync integer,\n" + "\t nightModeSync integer,\n" + "\t small_p_p_x integer,\n" + "\t small_p_p_y integer,\n" + "\t small_p_p_width integer,\n" + "\t small_p_p_height integer,\n" + "\t small_p_l_x integer,\n" + "\t small_p_l_y integer,\n" + "\t small_p_l_width integer,\n" + "\t small_p_l_height integer,\n" + "\t small_l_p_x integer,\n" + "\t small_l_p_y integer,\n" + "\t small_l_p_width integer,\n" + "\t small_l_p_height integer,\n" + "\t small_l_l_x integer,\n" + "\t small_l_l_y integer,\n" + "\t small_l_l_width integer,\n" + "\t small_l_l_height integer,\n" + "\t small_free_x integer,\n" + "\t small_free_y integer,\n" + "\t small_free_width integer,\n" + "\t small_free_height integer,\n" + "\t mini_y integer\n" + ");");
    db.execSQL("CREATE TABLE IF NOT EXISTS " + monitorTableName + " (\n" + "\t uuid text PRIMARY KEY,\n" + " packageName text,\n" + " className text,\n" + " eventType integer,\n" + " responseType" + " integer\n" + ");");
  }

  @SuppressLint("Range")
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    if (oldVersion < version) {
      // 获取旧数据
      ArrayList<Device> devices = getAll(db);
      // 修改表名
      db.execSQL("alter table " + tableName + " rename to tempTable");
      // 新建新表
      onCreate(db);
      // 将数据搬移至新表
      for (Device device : devices) db.insert(tableName, null, getValues(device));
      // 删除旧表
      db.execSQL("drop table tempTable");
    }
  }

  public ArrayList<MonitorEvent> getAllMonitorEvents() {
    return getAllMonitorEvents(getReadableDatabase());
  }

  @SuppressLint("Range")
  private ArrayList<MonitorEvent> getAllMonitorEvents(SQLiteDatabase db) {
    ArrayList<MonitorEvent> events = new ArrayList<>();
    try (Cursor cursor = db.query(monitorTableName, null, null, null, null, null, null)) {
      if (cursor.moveToFirst()) {
        do {
          String uuid = cursor.getString(cursor.getColumnIndex("uuid"));
          String packageName = cursor.getString(cursor.getColumnIndex("packageName"));
          String className = cursor.getString(cursor.getColumnIndex("className"));
          int eventType = cursor.getInt(cursor.getColumnIndex("eventType"));
          int responseType = cursor.getInt(cursor.getColumnIndex("responseType"));
          events.add(new MonitorEvent(uuid, packageName, className, eventType, responseType));
        } while (cursor.moveToNext());
      }
    }
    return events;
  }

  private ContentValues getValues(MonitorEvent event) {
    ContentValues values = new ContentValues();
    values.put("uuid", event.uuid);
    values.put("packageName", event.packageName);
    values.put("className", event.className);
    values.put("eventType", event.eventType);
    values.put("responseType", event.responseType);
    return values;
  }

  public void insert(MonitorEvent event) {
    getWritableDatabase().insert(monitorTableName, null, getValues(event));
  }

  public void delete(MonitorEvent event) {
    getWritableDatabase().delete(monitorTableName, "uuid=?", new String[]{String.valueOf(event.uuid)});
  }


  // 读取数据库设备列表
  @SuppressLint("Range")
  public ArrayList<Device> getAll() {
    return getAll(getReadableDatabase());
  }

  private ArrayList<Device> getAll(SQLiteDatabase db) {
    ArrayList<Device> devices = new ArrayList<>();
    try (Cursor cursor = db.query(tableName, null, null, null, null, null, null)) {
      if (cursor.moveToFirst()) do devices.add(getDeviceFormCursor(cursor)); while (cursor.moveToNext());
    }
    return devices;
  }

  // 查找
  @SuppressLint("Range")
  public Device getByUUID(String uuid) {
    Device device = null;
    try (Cursor cursor = getReadableDatabase().query(tableName, null, "uuid=?", new String[]{uuid}, null, null, null)) {
      if (cursor.moveToFirst()) device = getDeviceFormCursor(cursor);
    }
    return device;
  }

  // 更新
  public void insert(Device device) {
    getWritableDatabase().insert(tableName, null, getValues(device));
  }

  // 更新
  public void update(Device device) {
    getWritableDatabase().update(tableName, getValues(device), "uuid=?", new String[]{String.valueOf(device.uuid)});
  }

  // 删除
  public void delete(Device device) {
    getWritableDatabase().delete(tableName, "uuid=?", new String[]{String.valueOf(device.uuid)});
  }

  private ContentValues getValues(Device device) {
    ContentValues values = new ContentValues();
    values.put("uuid", device.uuid);
    values.put("type", device.type);
    values.put("name", device.name);
    values.put("isAudio", device.isAudio);
    values.put("address", device.address);
    values.put("specified_app", device.specified_app);
    values.put("maxSize", device.maxSize);
    values.put("maxFps", device.maxFps);
    values.put("maxVideoBit", device.maxVideoBit);
    values.put("setResolution", device.setResolution);
    values.put("defaultFull", device.defaultFull);
    values.put("useH265", device.useH265);
    values.put("useOpus", device.useOpus);
    values.put("connectOnStart", device.connectOnStart);
    values.put("clipboardSync", device.clipboardSync);
    values.put("nightModeSync", device.nightModeSync);
    values.put("small_p_p_x", device.small_p_p_x);
    values.put("small_p_p_y", device.small_p_p_y);
    values.put("small_p_p_width", device.small_p_p_width);
    values.put("small_p_p_height", device.small_p_p_height);
    values.put("small_p_l_x", device.small_p_l_x);
    values.put("small_p_l_y", device.small_p_l_y);
    values.put("small_p_l_width", device.small_p_l_width);
    values.put("small_p_l_height", device.small_p_l_height);
    values.put("small_l_p_x", device.small_l_p_x);
    values.put("small_l_p_y", device.small_l_p_y);
    values.put("small_l_p_width", device.small_l_p_width);
    values.put("small_l_p_height", device.small_l_p_height);
    values.put("small_l_l_x", device.small_l_l_x);
    values.put("small_l_l_y", device.small_l_l_y);
    values.put("small_l_l_width", device.small_l_l_width);
    values.put("small_l_l_height", device.small_l_l_height);
    values.put("small_free_x", device.small_free_x);
    values.put("small_free_y", device.small_free_y);
    values.put("small_free_width", device.small_free_width);
    values.put("small_free_height", device.small_free_height);
    values.put("mini_y", device.mini_y);
    return values;
  }

  @SuppressLint("Range")
  private Device getDeviceFormCursor(Cursor cursor) {
    return new Device(
      cursor.getString(cursor.getColumnIndex("uuid")),
      cursor.getInt(cursor.getColumnIndex("type")),
      cursor.getString(cursor.getColumnIndex("name")),
      cursor.getString(cursor.getColumnIndex("address")),
      cursor.getColumnIndex("specified_app") == -1 ? "" : cursor.getString(cursor.getColumnIndex("specified_app")),
      cursor.getInt(cursor.getColumnIndex("isAudio")) == 1,
      cursor.getInt(cursor.getColumnIndex("maxSize")),
      cursor.getInt(cursor.getColumnIndex("maxFps")),
      cursor.getInt(cursor.getColumnIndex("maxVideoBit")),
      cursor.getInt(cursor.getColumnIndex("setResolution")) == 1,
      cursor.getInt(cursor.getColumnIndex("defaultFull")) == 1,
      cursor.getColumnIndex("useH265") == -1 ? AppData.setting.getDefaultUseH265() : cursor.getInt(cursor.getColumnIndex("useH265")) == 1,
      cursor.getColumnIndex("useOpus") == -1 ? AppData.setting.getDefaultUseOpus() : cursor.getInt(cursor.getColumnIndex("useOpus")) == 1,
      cursor.getColumnIndex("connectOnStart") != -1 && cursor.getInt(cursor.getColumnIndex("connectOnStart")) == 1,
      cursor.getColumnIndex("clipboardSync") == -1 ? AppData.setting.getDefaultClipboardSync() : cursor.getInt(cursor.getColumnIndex("clipboardSync")) == 1,
      cursor.getColumnIndex("nightModeSync") == -1 ? AppData.setting.getDefaultNightModeSync() : cursor.getInt(cursor.getColumnIndex("nightModeSync")) == 1,
      cursor.getColumnIndex("small_p_p_x") == -1 ? Device.SMALL_X : cursor.getInt(cursor.getColumnIndex("small_p_p_x")),
      cursor.getColumnIndex("small_p_p_y") == -1 ? Device.SMALL_Y : cursor.getInt(cursor.getColumnIndex("small_p_p_y")),
      cursor.getColumnIndex("small_p_p_width") == -1 ? Device.SMALL_WIDTH : cursor.getInt(cursor.getColumnIndex("small_p_p_width")),
      cursor.getColumnIndex("small_p_p_height") == -1 ? Device.SMALL_HEIGHT : cursor.getInt(cursor.getColumnIndex("small_p_p_height")),
      cursor.getColumnIndex("small_p_l_x") == -1 ? Device.SMALL_X : cursor.getInt(cursor.getColumnIndex("small_p_l_x")),
      cursor.getColumnIndex("small_p_l_y") == -1 ? Device.SMALL_Y : cursor.getInt(cursor.getColumnIndex("small_p_l_y")),
      cursor.getColumnIndex("small_p_l_width") == -1 ? Device.SMALL_WIDTH : cursor.getInt(cursor.getColumnIndex("small_p_l_width")),
      cursor.getColumnIndex("small_p_l_height") == -1 ? Device.SMALL_HEIGHT : cursor.getInt(cursor.getColumnIndex("small_p_l_height")),
      cursor.getColumnIndex("small_l_p_x") == -1 ? Device.SMALL_X : cursor.getInt(cursor.getColumnIndex("small_l_p_x")),
      cursor.getColumnIndex("small_l_p_y") == -1 ? Device.SMALL_Y : cursor.getInt(cursor.getColumnIndex("small_l_p_y")),
      cursor.getColumnIndex("small_l_p_width") == -1 ? Device.SMALL_WIDTH : cursor.getInt(cursor.getColumnIndex("small_l_p_width")),
      cursor.getColumnIndex("small_l_p_height") == -1 ? Device.SMALL_HEIGHT : cursor.getInt(cursor.getColumnIndex("small_l_p_height")),
      cursor.getColumnIndex("small_l_l_x") == -1 ? Device.SMALL_X : cursor.getInt(cursor.getColumnIndex("small_l_l_x")),
      cursor.getColumnIndex("small_l_l_y") == -1 ? Device.SMALL_Y : cursor.getInt(cursor.getColumnIndex("small_l_l_y")),
      cursor.getColumnIndex("small_l_l_width") == -1 ? Device.SMALL_WIDTH : cursor.getInt(cursor.getColumnIndex("small_l_l_width")),
      cursor.getColumnIndex("small_l_l_height") == -1 ? Device.SMALL_HEIGHT : cursor.getInt(cursor.getColumnIndex("small_l_l_height")),
      cursor.getColumnIndex("small_free_x") == -1 ? Device.SMALL_X : cursor.getInt(cursor.getColumnIndex("small_free_x")),
      cursor.getColumnIndex("small_free_y") == -1 ? Device.SMALL_Y : cursor.getInt(cursor.getColumnIndex("small_free_y")),
      cursor.getColumnIndex("small_free_width") == -1 ? Device.SMALL_WIDTH : cursor.getInt(cursor.getColumnIndex("small_free_width")),
      cursor.getColumnIndex("small_free_height") == -1 ? Device.SMALL_HEIGHT : cursor.getInt(cursor.getColumnIndex("small_free_height")),
      cursor.getColumnIndex("mini_y") == -1 ? Device.MINI_Y : cursor.getInt(cursor.getColumnIndex("mini_y"))
    );
  }
}
