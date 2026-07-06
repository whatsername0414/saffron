package com.saffron.cook.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.saffron.cook.core.domain.model.Category

@Entity(tableName = "cached_categories")
data class CachedCategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
)

fun Category.toEntity() = CachedCategoryEntity(id = id, name = name)

fun CachedCategoryEntity.toCategory() = Category(id = id, name = name)
