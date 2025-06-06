package com.example.inventorycotrol.ui.fragments.orders.manage.addProductToOrder

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventorycotrol.common.Resource
import com.example.inventorycotrol.data.remote.mappers.mapToSelection
import com.example.inventorycotrol.di.IoDispatcher
import com.example.inventorycotrol.domain.model.validator.InputValidator
import com.example.inventorycotrol.domain.usecase.barcodeScanner.BarcodeScannerUseCase
import com.example.inventorycotrol.domain.usecase.product.ProductUseCases
import com.example.inventorycotrol.ui.model.order.product.OrderAddProductFormUiState
import com.example.inventorycotrol.ui.model.order.product.OrderAddProductUiState
import com.example.inventorycotrol.ui.model.order.product.OrderManageProductFormEvent
import com.example.inventorycotrol.ui.navigation.AppNavigator
import com.example.inventorycotrol.ui.navigation.Destination
import com.example.inventorycotrol.ui.snackbar.SnackbarController.sendSnackbarEvent
import com.example.inventorycotrol.ui.snackbar.SnackbarEvent
import com.example.inventorycotrol.ui.utils.extensions.handleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderAddProductViewModel @Inject constructor(
    private val productUseCase: ProductUseCases,
    private val navigator: AppNavigator,
    private val barcodeScannerUseCase: BarcodeScannerUseCase,
    private val savedStateHandle: SavedStateHandle,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val destArg = Destination.from<Destination.OrderProductSelector>(savedStateHandle)

    private val _uiState = MutableStateFlow(OrderAddProductUiState(currency = destArg.currency))
    val uiState = _uiState.asStateFlow()

    private val _uiFormState = MutableStateFlow(OrderAddProductFormUiState())
    val uiFormState = _uiFormState.asStateFlow()


    @OptIn(FlowPreview::class)
    private val searchQueryFlow = MutableStateFlow("").apply {
        debounce(1000).onEach { query ->
            if (query.isNotEmpty()) {
                searchProducts(query)
            } else {
                getProducts()
            }
        }.launchIn(viewModelScope)
    }

    init { _uiState.update { it.copy(isLoading = true) } }

    private fun searchProducts(query: String) = viewModelScope.launch(dispatcher) {
        productUseCase.getProducts.getAll().onEach { response ->
            when (response) {
                Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    sendSnackbarEvent(SnackbarEvent(response.errorMessage))
                }
                is Resource.Success -> {
                    val filteredProducts = response.data.filter { it.name.contains(query, ignoreCase = true) }.map { it.mapToSelection() }

                    _uiState.update { state ->
                        state.copy(
                            products = filteredProducts,
                            pinnedProduct = null,
                            isLoading = false
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getProducts() = viewModelScope.launch {
        productUseCase.getProducts.getAll().onEach { response ->
            when (response) {
                Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    sendSnackbarEvent(SnackbarEvent(response.errorMessage))
                }
                is Resource.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            products = response.data.map { it.mapToSelection() },
                            pinnedProduct = null,
                            isLoading = false,
                            isBarcode = false
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun scanBarcodeScanner() = viewModelScope.launch {
        handleResult(barcodeScannerUseCase(),
            onSuccess = { barcode ->
                val foundProduct =
                    _uiState.value.products.find { it.barcode == barcode.displayValue }

                if (foundProduct == null) {
                    return@handleResult
                }


                _uiState.update {
                    it.copy(
                        pinnedProduct = foundProduct,
                        products = listOf(foundProduct),
                        isBarcode = true
                    )
                }

                selectItem(foundProduct.id)
            }
        )
    }

    fun onEvent(event: OrderManageProductFormEvent) {
        when (event) {
            is OrderManageProductFormEvent.Quantity -> _uiFormState.update {
                it.copy(
                    quantity = event.quantity,
                    quantityError = null
                )
            }

            is OrderManageProductFormEvent.Price -> _uiFormState.update {
                it.copy(
                    rate = event.price,
                    rateError = null
                )
            }

            is OrderManageProductFormEvent.Search -> {
                _uiState.update {
                    it.copy(
                        isLoading = true
                    )
                }
                searchQueryFlow.update { event.query }
            }
        }
        if (isValidateInputs()) {
            _uiState.update { state ->
                state.copy(
                    canAddProduct = true
                )
            }
        } else {
            _uiState.update { state ->
                state.copy(
                    canAddProduct = false
                )
            }
        }
    }

    fun selectItem(productId: String) {
        val foundProduct = _uiState.value.products.find { it.id == productId}

        _uiState.update { state ->
            state.copy(
                pinnedProduct = foundProduct,
                products = state.products.map {
                    it.copy(isSelected = it.id == productId)
                }
            )
        }
    }

    private fun isValidateInputs(): Boolean {
        val inputValidator = InputValidator
            .create()
            .withNotEmpty()
            .withGreaterThenZero()

        val quantityResult = inputValidator.build().invoke(_uiFormState.value.quantity)
        val rateResult = inputValidator.build().invoke(_uiFormState.value.rate)

        return !(quantityResult.hasError || rateResult.hasError)
    }

    fun showAllProducts() {
        getProducts()
    }
}
