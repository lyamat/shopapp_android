package com.example.kelineyt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kelineyt.data.Product
import com.example.kelineyt.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _specialProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val specialProducts: StateFlow<Resource<List<Product>>> = _specialProducts

    private val _bestDealsProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestDealsProducts: StateFlow<Resource<List<Product>>> = _bestDealsProducts

    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts: StateFlow<Resource<List<Product>>> = _bestProducts

    private val pagingInfo = PagingInfo()

    init {
        fetchSpecialProducts()
        fetchBestDeals()
        fetchBestProducts()
    }

    fun fetchSpecialProducts() {
        if(!pagingInfo.isPagingSpecialProductsEnd) {
            viewModelScope.launch {
                _specialProducts.emit(Resource.Loading())
            }
            firestore.collection("Products")
                .limit(pagingInfo.specialProductsPage * 2)
                .whereEqualTo("category", "Special Products").get().addOnSuccessListener { result ->
                    val specialProductsList = result.toObjects(Product::class.java)
                    pagingInfo.isPagingSpecialProductsEnd =
                        specialProductsList == pagingInfo.oldSpecialProducts
                    pagingInfo.oldSpecialProducts = specialProductsList
                    viewModelScope.launch {
                        _specialProducts.emit(Resource.Success(specialProductsList))
                    }
                    pagingInfo.specialProductsPage++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _specialProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }


    fun fetchBestDeals() {
        if(!pagingInfo.isPagingBestDealsEnd) {
            viewModelScope.launch {
                _bestDealsProducts.emit(Resource.Loading())
            }
            firestore.collection("Products")
                .limit(pagingInfo.bestDealsPage * 2)
                .whereEqualTo("category", "Best Deals").get().addOnSuccessListener { result ->
                    val bestDealsProducts = result.toObjects(Product::class.java)
                    pagingInfo.isPagingBestDealsEnd = bestDealsProducts == pagingInfo.oldBestDeals
                    pagingInfo.oldBestDeals = bestDealsProducts
                    viewModelScope.launch {
                        _bestDealsProducts.emit(Resource.Success(bestDealsProducts))
                    }
                    pagingInfo.bestDealsPage++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _bestDealsProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }

    fun fetchBestProducts() {
        if (!pagingInfo.isPagingBestProductsEnd) {
            viewModelScope.launch {
                _bestProducts.emit(Resource.Loading())
            }
            firestore.collection("Products")
                .limit(pagingInfo.bestProductsPage * 10).get()
                .addOnSuccessListener { result ->
                    val bestProducts = result.toObjects(Product::class.java)
                    pagingInfo.isPagingBestProductsEnd = bestProducts == pagingInfo.oldBestProducts
                    pagingInfo.oldBestProducts = bestProducts
                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Success(bestProducts))
                    }
                    pagingInfo.bestProductsPage++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }
}

internal data class PagingInfo(
    var bestProductsPage: Long = 1,
    var oldBestProducts: List<Product> = emptyList(),
    var isPagingBestProductsEnd: Boolean = false,

    var bestDealsPage: Long = 1,
    var oldBestDeals: List<Product> = emptyList(),
    var isPagingBestDealsEnd: Boolean = false,

    var specialProductsPage: Long = 1,
    var oldSpecialProducts: List<Product> = emptyList(),
    var isPagingSpecialProductsEnd: Boolean = false
)












