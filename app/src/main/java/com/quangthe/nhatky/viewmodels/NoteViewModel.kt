package com.quangthe.nhatky.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.quangthe.nhatky.models.NoteFolder
import com.quangthe.nhatky.models.SimpleNote
import com.quangthe.nhatky.repositories.NoteRepository

class NoteViewModel : ViewModel() {
    private val noteRepository = NoteRepository()
    private val _note = MutableStateFlow<SimpleNote?>(null)
    val note: StateFlow<SimpleNote?> = _note

    private val _currentFolderId = MutableStateFlow(0)
    val currentFolderId: StateFlow<Int> = _currentFolderId.asStateFlow()

    private val _folders = MutableStateFlow<List<NoteFolder>>(emptyList())
    val folders: StateFlow<List<NoteFolder>> = _folders.asStateFlow()

    private val _folderStack = MutableStateFlow<List<NoteFolder>>(emptyList())
    val folderStack: StateFlow<List<NoteFolder>> = _folderStack.asStateFlow()

    fun loadNote(sequence: Int) {
        viewModelScope.launch {
            if (sequence > 0) {
                _note.value = noteRepository.findNoteBy(sequence)
            } else {
                _note.value = SimpleNote()
            }
        }
    }

    fun loadFolders(parentId: Int = 0) {
        viewModelScope.launch {
            _folders.value = noteRepository.findNoteFoldersByParent(parentId)
        }
    }

    fun loadRootNotes() {
        viewModelScope.launch {
            _note.value = null
        }
    }

    fun loadNotesByFolder(folderId: Int) {
        viewModelScope.launch {
            _note.value = noteRepository.findNotesByFolder(folderId).firstOrNull()
        }
    }

    fun enterFolder(folder: NoteFolder) {
        _folderStack.value = _folderStack.value + folder
        _currentFolderId.value = folder.id
        loadFolders(folder.id)
    }

    fun leaveFolder() {
        val stack = _folderStack.value.toMutableList()
        if (stack.isNotEmpty()) {
            stack.removeAt(stack.lastIndex)
            _folderStack.value = stack
            val parentId = if (stack.isNotEmpty()) stack.last().id else 0
            _currentFolderId.value = parentId
            loadFolders(parentId)
        }
    }

    fun isInRootFolder(): Boolean = _folderStack.value.isEmpty()

    fun getCurrentPathName(rootName: String): String {
        val stack = _folderStack.value
        if (stack.isEmpty()) return rootName
        return stack.joinToString(" > ") { it.name }
    }

    fun saveNote(title: String, contents: String, color: Int = 0) {
        val currentNote = _note.value ?: SimpleNote()
        currentNote.title = title
        currentNote.contents = contents
        currentNote.color = color
        currentNote.folderId = _currentFolderId.value
        viewModelScope.launch {
            noteRepository.saveNote(currentNote)
        }
    }

    fun createFolder(name: String, color: Int, parentId: Int = _currentFolderId.value) {
        val folder = NoteFolder().apply {
            this.name = name
            this.color = color
            this.parentId = parentId
        }
        viewModelScope.launch {
            noteRepository.saveFolder(folder)
            loadFolders(parentId)
        }
    }

    fun deleteFolder(id: Int) {
        viewModelScope.launch {
            noteRepository.deleteFolder(id)
            loadFolders(_currentFolderId.value)
        }
    }
}
