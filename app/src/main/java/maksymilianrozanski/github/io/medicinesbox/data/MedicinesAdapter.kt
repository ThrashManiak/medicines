package maksymilianrozanski.github.io.medicinesbox.data

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import maksymilianrozanski.github.io.medicinesbox.AddEditActivity
import maksymilianrozanski.github.io.medicinesbox.MyApp
import maksymilianrozanski.github.io.medicinesbox.R
import maksymilianrozanski.github.io.medicinesbox.model.KEY_ID
import maksymilianrozanski.github.io.medicinesbox.model.Medicine

class MedicinesAdapter(private var list: ArrayList<Medicine>, private val context: Context) :
        RecyclerView.Adapter<MedicinesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
                .inflate(R.layout.list_row, parent, false)
        return ViewHolder(view, context, list)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindViews(list[position])
    }

    fun setList( newList: ArrayList<Medicine>){
        this.list = newList
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View, context: Context, list: ArrayList<Medicine>)
        : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var quantityCalculator = (context.applicationContext as MyApp).appComponent.getQuantityCalculator()

        var medicineName = itemView.findViewById(R.id.medicineName) as TextView
        var expectedQuantity = itemView.findViewById(R.id.expectedQuantity) as TextView
        var medicineDailyUsage = itemView.findViewById(R.id.medicineDailyUsage) as TextView
        var medicineSavedTime = itemView.findViewById(R.id.medicineSaveDate) as TextView
        var medicineEnoughUntil = itemView.findViewById(R.id.enoughUntil) as TextView
        var deleteButton  = itemView.findViewById(R.id.deleteButton) as Button
        var editButton = itemView.findViewById(R.id.editButton) as Button

        fun bindViews(medicine: Medicine) {
            medicineName.text = medicine.name
            expectedQuantity.text = "Expected quantity today: ${String.format("%.2f",quantityCalculator.calculateQuantityToday(medicine))}"
            medicineDailyUsage.text = "Daily usage: ${String.format("%.2f", medicine.dailyUsage)}"
            medicineSavedTime.text = medicine.showFormattedDate()
            medicineEnoughUntil.text = "Enough until: ${medicine.enoughUntilDate()}"

            medicineName.setOnClickListener(this)
            deleteButton.setOnClickListener(this)
            editButton.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            var medicine = list[adapterPosition]

            when {
                view?.id == deleteButton.id -> {
                    Toast.makeText(context, "Clicked delete button, Deleting! : ${medicine.id}", Toast.LENGTH_SHORT).show()
                    list.removeAt(adapterPosition)
                    deleteItem(medicine.id!!)
                    notifyItemRemoved(adapterPosition)
                }
                view?.id == editButton.id -> {
                    Toast.makeText(context, "Clicked edit button, medicine id: ${medicine.id}",Toast.LENGTH_SHORT).show()
                    var intent = Intent(context, AddEditActivity::class.java)
                    intent.putExtra(KEY_ID, medicine)
                    context.startActivity(intent)
                }
                else -> Toast.makeText(context, "Clicked something else", Toast.LENGTH_SHORT).show()
            }
        }

        private fun deleteItem(id: Int){
            var databaseHandler = MedicinesDatabaseHandler(context)
            databaseHandler.deleteMedicine(id)
        }
    }
}