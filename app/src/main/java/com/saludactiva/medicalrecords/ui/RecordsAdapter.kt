package com.saludactiva.medicalrecords.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.saludactiva.medicalrecords.data.MedicalStudy
import com.saludactiva.medicalrecords.databinding.ItemRecordBinding

class RecordsAdapter(
    private val onDelete: (MedicalStudy) -> Unit,
    private val onOpenDocument: (String) -> Unit
) : ListAdapter<MedicalStudy, RecordsAdapter.RecordViewHolder>(StudyDiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val binding = ItemRecordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RecordViewHolder(
        private val binding: ItemRecordBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(study: MedicalStudy) {
            binding.recordTitle.text = study.titulo
            binding.recordType.text = study.tipo
            binding.recordDate.text = study.fecha
            binding.recordResult.text = study.resultadoBreve
            binding.deleteButton.setOnClickListener { onDelete(study) }
            val documentUrl = study.urlDocumento
            binding.documentButton.isEnabled = !documentUrl.isNullOrBlank()
            binding.documentButton.visibility =
                if (documentUrl.isNullOrBlank()) android.view.View.GONE else android.view.View.VISIBLE
            binding.documentButton.setOnClickListener {
                documentUrl?.let(onOpenDocument)
            }
        }
    }

    private object StudyDiffCallback : DiffUtil.ItemCallback<MedicalStudy>() {
        override fun areItemsTheSame(oldItem: MedicalStudy, newItem: MedicalStudy) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: MedicalStudy, newItem: MedicalStudy) =
            oldItem == newItem
    }
}
