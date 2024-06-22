package com.autonavi.amapauto;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import com.autonavi.amapauto.databinding.ActivityLogBinding;
import com.autonavi.amapauto.entity.Device;
import com.autonavi.amapauto.helper.DeviceListAdapter;
import com.autonavi.amapauto.helper.L;
import com.autonavi.amapauto.helper.PublicTools;

public class LogActivity extends Activity {
  private ActivityLogBinding logActivity;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    PublicTools.setStatusAndNavBar(this);
    PublicTools.setLocale(this);
    logActivity = ActivityLogBinding.inflate(this.getLayoutInflater());
    setContentView(logActivity.getRoot());
    ArrayAdapter<String> devices = new ArrayAdapter<>(this, R.layout.item_spinner_item);
    devices.add(getString(R.string.log_other_devices));
    for (Device device : DeviceListAdapter.devicesList) {
      devices.add(device.name);
    }
    logActivity.backButton.setOnClickListener(v -> finish());
    logActivity.logDevice.setAdapter(devices);
    logActivity.logDevice.setSelection(0);
    logActivity.logDevice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
          logActivity.logText.setText(L.getLogs());
          return;
        }
        for (Device device : DeviceListAdapter.devicesList) {
          if (device.name.equals(logActivity.logDevice.getSelectedItem().toString())) {
            logActivity.logText.setText(L.getLogs(device.uuid));
            break;
          }
        }
      }
      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });
    super.onCreate(savedInstanceState);
  }
}