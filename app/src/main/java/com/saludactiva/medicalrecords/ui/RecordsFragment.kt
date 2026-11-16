package com.saludactiva.medicalrecords.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.saludactiva.medicalrecords.R
import com.saludactiva.medicalrecords.data.MedicalStudy
import com.saludactiva.medicalrecords.databinding.DialogAddRecordBinding
import com.saludactiva.medicalrecords.databinding.FragmentRecordsBinding

class RecordsFragment : Fragment() {
    private var _binding: FragmentRecordsBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val viewModel: RecordsViewModel by viewModels()
    private lateinit var recordsAdapter: RecordsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecordsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        binding.toolbar.setOnMenuItemClickListener(::onMenuItemSelected)
        recordsAdapter = RecordsAdapter(::confirmDelete, ::openDocument)
        binding.recordsList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recordsAdapter
        }
        binding.searchInput.doAfterTextChanged { viewModel.search(it?.toString().orEmpty()) }
        binding.addRecordButton.setOnClickListener { showAddStudyDialog() }
        binding.swipeRefresh.setOnRefreshListener { viewModel.loadStudies() }

        viewModel.studies.observe(viewLifecycleOwner) { studies ->
            recordsAdapter.submitList(studies)
            binding.emptyMessage.setText(
                if (binding.searchInput.text.isNullOrBlank()) {
                    R.string.empty_records
                } else {
                    R.string.no_search_results
                }
            )
            binding.emptyState.visibility =
                if (studies.isEmpty() && binding.swipeRefresh.isRefreshing.not()) View.VISIBLE
                else View.GONE
        }
        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            binding.swipeRefresh.isRefreshing = loading
            if (loading) binding.emptyState.visibility = View.GONE
        }
        viewModel.message.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                viewModel.clearMessage()
            }
        }
        viewModel.sessionExpired.observe(viewLifecycleOwner) { expired ->
            if (expired) findNavController().navigate(R.id.action_recordsFragment_to_loginFragment)
        }
        viewModel.loadStudies()
    }

    private fun onMenuItemSelected(item: MenuItem): Boolean {
        if (item.itemId != R.id.action_logout) return false
        viewModel.signOut()
        return true
    }

    private fun showAddStudyDialog() {
        val form = DialogAddRecordBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.add_record)
            .setView(form.root)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.save, null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val title = form.titleInput.text?.toString().orEmpty()
                val type = form.typeInput.text?.toString().orEmpty()
                val date = form.dateInput.text?.toString().orEmpty()
                val result = form.resultInput.text?.toString().orEmpty()
                if (listOf(title, type, date, result).any { it.isBlank() }) {
                    Snackbar.make(form.root, R.string.validation_required, Snackbar.LENGTH_LONG).show()
                } else {
                    viewModel.createStudy(
                        title,
                        type,
                        date,
                        result,
                        form.documentUrlInput.text?.toString().orEmpty()
                    )
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    private fun confirmDelete(study: MedicalStudy) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(study.titulo)
            .setMessage(R.string.delete_confirmation)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.delete) { _, _ -> viewModel.deleteStudy(study.id) }
            .show()
    }

    private fun openDocument(url: String) {
        val uri = Uri.parse(url)
        if (uri.scheme != "https" && uri.scheme != "http") {
            Snackbar.make(binding.root, "El enlace del documento no es válido.", Snackbar.LENGTH_LONG)
                .show()
            return
        }
        try {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (_: ActivityNotFoundException) {
            Snackbar.make(binding.root, "No hay una aplicación para abrir este enlace.", Snackbar.LENGTH_LONG)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
