package com.nobodysapps.greentastic.error_handling

open class LocationError(message: String?): Error(message)

class NoLocationPermissionError: LocationError("Location permission not granted")

class NoGPSError: LocationError("No GPS")