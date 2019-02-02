package com.yossisegev.domain.common

import io.reactivex.SingleTransformer

/**
 * Created by Yossi Segev on 20/02/2018.
 */
abstract class Transformer<T> : SingleTransformer<T, T>