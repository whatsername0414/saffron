package com.saffron.cook.feature.note.di

import com.saffron.cook.feature.note.detail.NoteDetailViewModel
import com.saffron.cook.feature.note.editor.NoteEditorViewModel
import com.saffron.cook.feature.note.main.NoteListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val noteModule = module {
    viewModelOf(::NoteListViewModel)
    viewModelOf(::NoteDetailViewModel)
    viewModelOf(::NoteEditorViewModel)
}
