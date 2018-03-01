package com.fysq.signapp2.Base.BaseMVP

interface BaseView<T> {
    fun setPresenter(presenter: T)
}