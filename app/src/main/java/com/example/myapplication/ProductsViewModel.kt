package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductsViewModel : ViewModel() {

    private val _uiState: MutableStateFlow<UiState<List<String>>> =
        MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState<List<String>>> = _uiState.asStateFlow()

    init {
        fetchProducts()
    }

    fun refresh() {
        fetchProducts()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                delay(1500)
                if (System.currentTimeMillis() % 2L == 0L) {
                    throw IllegalStateException("Falha ao carregar produtos")
                }
                val products = listOf(
                    "Camiseta Madara",
                    "Action Figure Naruto",
                    "Moletom Akatsuki"
                )
                _uiState.value = UiState.Success(products)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }
}


