package net.atos.zac.app.zaken.converter.historie

import net.atos.client.zgw.shared.util.JsonbUtil

fun Any?.asMapWithKeyOfString(): Map<String, *>? = (this as? Map<*, *>)
    ?.mapNotNull { (key, value) -> (key as? String)?.let { it to value } }
    ?.toMap()

fun <K> Map<K, *>.diff(other: Map<K, *>): Map<K, Pair<*, *>> = other
    .filterNot { (key, value) -> this.containsKey(key) && compare(value, this[key]) }
    .mapValues { (key, value) -> this[key] to value }

private fun compare(left: Any?, right: Any?): Boolean =
    when {
        left is Map<*, *> && right is Map<*, *> -> left.all {
            right.containsKey(it.key) && compare(it.value, right[it.key])
        }
        left is List<*> && right is List<*> -> left.size == right.size && left.withIndex().all {
            compare(it.value, right[it.index])
        }
        else -> left == right
    }

fun <T> Map<String, *>.getTypedValue(type: Class<T>): T? =
    JsonbUtil.JSONB.toJson(this)
        .let { JsonbUtil.JSONB.fromJson(it, type) }

fun Map<String, *>.stringProperty(propName: String): String? =
    this[propName] as? String
