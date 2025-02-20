package com.baseflow.geolocator.location;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.baseflow.geolocator.errors.ErrorCallback;
import com.baseflow.geolocator.errors.ErrorCodes;
import com.baseflow.geolocator.errors.PermissionUndefinedException;
import com.baseflow.geolocator.permission.LocationPermission;
import com.baseflow.geolocator.permission.PermissionManager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings("deprecation")
public class GeolocationManager
    implements io.flutter.plugin.common.PluginRegistry.ActivityResultListener {

  private final List<LocationClient> locationClients;

  public GeolocationManager() {
    this.locationClients = new CopyOnWriteArrayList<>();
  }

  public void getLastKnownPosition(
      Context context,
      boolean forceLocationManager,
      PositionChangedCallback positionChangedCallback,
      ErrorCallback errorCallback) {

    LocationClient locationClient = createLocationClient(context, forceLocationManager, null);
    locationClient.getLastKnownPosition(positionChangedCallback, errorCallback);
  }

  public void isLocationServiceEnabled(
      @Nullable Context context, LocationServiceListener listener) {
    if (context == null) {
      listener.onLocationServiceError(ErrorCodes.locationServicesDisabled);
    }

    LocationClient locationClient = createLocationClient(context, false, null);
    locationClient.isLocationServiceEnabled(listener);
  }

  public void startPositionUpdates(
      @NonNull LocationClient locationClient,
      @Nullable Activity activity,
      @NonNull PositionChangedCallback positionChangedCallback,
      @NonNull ErrorCallback errorCallback) {

    this.locationClients.add(locationClient);
    locationClient.startPositionUpdates(activity, positionChangedCallback, errorCallback);
  }

  public void stopPositionUpdates(@NonNull LocationClient locationClient) {
    locationClients.remove(locationClient);
    locationClient.stopPositionUpdates();
  }

  public LocationClient createLocationClient(
      Context context,
      boolean forceAndroidLocationManager,
      @Nullable LocationOptions locationOptions) {
    return new LocationManagerClient(context, locationOptions);
  }

  @Override
  public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
    for (LocationClient client : this.locationClients) {
      if (client.onActivityResult(requestCode, resultCode)) {
        return true;
      }
    }

    return false;
  }
}
