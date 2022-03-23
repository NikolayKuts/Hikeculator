package com.example.hikeculator.presentation.product_search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.annotation.IdRes
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.hikeculator.R
import com.example.hikeculator.databinding.FragmentProductSearchBinding
import com.example.hikeculator.presentation.common.collectWhenStarted
import com.google.android.material.snackbar.Snackbar

class ProductSearchFragment : Fragment(R.layout.fragment_product_search) {

    private val viewModel by viewModels<ProductSearchViewModel>()

    private val viewBinding by viewBinding(FragmentProductSearchBinding::bind)

    private val searchedProductsAdapter = ProductSearchAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeSearchRecyclerView()
        initializeFlowCollectors()
        initializeSearchEditTextListeners()
    }

    private fun searchProducts(searchExpression: String) {
        viewBinding.progressBarSearch.visibility = View.VISIBLE
        viewModel.search(searchExpression)
    }

    private fun initializeFlowCollectors() {
        viewModel.productSearchResult.collectWhenStarted(lifecycleScope = lifecycleScope) { products ->
            searchedProductsAdapter.submitList(products)
            viewBinding.recyclerViewListOfProducts.smoothScrollToPosition(0)
            viewBinding.progressBarSearch.visibility = View.GONE
        }
        viewModel.searchError.collectWhenStarted(lifecycleScope = lifecycleScope) { stringResId ->
            showSnackBar(stringResId)
        }
    }

    private fun initializeSearchEditTextListeners() {
        viewBinding.editTextSearch.addTextChangedListener { text ->
            searchProducts(text.toString())
        }

        viewBinding.editTextSearch.setOnEditorActionListener { textView, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    searchProducts(textView.text.toString())
                    true
                }
                else -> false
            }
        }
    }

    private fun initializeSearchRecyclerView() {
        viewBinding.recyclerViewListOfProducts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchedProductsAdapter
        }
    }

    private fun showSnackBar(resId: Int) =
        Snackbar.make(viewBinding.root, getString(resId), Snackbar.LENGTH_SHORT).show()
}