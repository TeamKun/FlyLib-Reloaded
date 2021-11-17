/*
 * Copyright (c) 2021 kotx__
 */

package dev.kotx.flylib.command.elements

import dev.kotx.flylib.command.ConfigElement

class DoubleElement(override val key: String, override var value: Double?, val min: Double, val max: Double) :
    ConfigElement<Double>