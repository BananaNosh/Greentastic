package com.nobodysapps.greentastic.errorHandling

open class LocationError(message: String?): Error(message)

class NoLocationPermissionError: LocationError("Location permission not granted")

class NoGPSError: LocationError("No GPS")