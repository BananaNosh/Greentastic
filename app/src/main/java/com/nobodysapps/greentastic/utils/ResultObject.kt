package com.nobodysapps.greentastic.utils


class ResultObject<T> {
    val value: T?
    val error: Throwable?
    val state: ResultState

    constructor() {
        this.state = ResultState.LOADING
        this.value = null
        this.error = null
    }

    constructor(value: T) {
        this.value = value
        this.state = ResultState.LOADED
        this.error = null
    }

    constructor(error: Throwable) {
        this.error = error
        this.state = ResultState.ERROR
        this.value = null
    }
}

enum class ResultState {
    LOADING,
    LOADED,
    ERROR
}