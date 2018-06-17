package maksymilianrozanski.github.io.medicinesbox

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_edit.*
import maksymilianrozanski.github.io.medicinesbox.component.AddEditActivityComponent
import maksymilianrozanski.github.io.medicinesbox.data.MedicinesDatabaseHandler
import maksymilianrozanski.github.io.medicinesbox.data.TimeProvider
import maksymilianrozanski.github.io.medicinesbox.model.KEY_ID
import maksymilianrozanski.github.io.medicinesbox.model.Medicine
import maksymilianrozanski.github.io.medicinesbox.utilities.QuantityCalculator
import javax.inject.Inject

class AddEditActivity : AppCompatActivity() {

    @Inject
    lateinit var databaseHandler: MedicinesDatabaseHandler

    @Inject
    lateinit var timeProvider: TimeProvider

    @Inject
    lateinit var quantityCalculator: QuantityCalculator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)

        (application as MyApp).appComponent
                .plusAddEditActivity(AddEditActivityComponent.Module())
                .inject(this)

        var launchIntent: Intent = intent
        var medicineFromIntent = launchIntent.getParcelableExtra<Medicine>(KEY_ID)

        if (medicineFromIntent != null
                && medicineFromIntent.name != null
                && medicineFromIntent.id != null
                && medicineFromIntent.quantity != null
                && medicineFromIntent.dailyUsage != null) {
            medicineNameEditText.setText(medicineFromIntent.name)
            medicineQuantityEditText.setText(String.format("%.2f", quantityCalculator.calculateQuantityToday(medicineFromIntent)))
            medicineDailyUsageEditText.setText(String.format("%.2f", medicineFromIntent.dailyUsage))
        }

        saveButton.setOnClickListener {
            if (medicineFromIntent == null) {
                saveNewMedicine()
            } else {
                saveEditedMedicine(medicineFromIntent)
            }
        }

        cancelButton.setOnClickListener{
            NavUtils.navigateUpFromSameTask(this)
        }
    }

    private fun saveNewMedicine() {
        var medicineToSave = Medicine()
        medicineToSave.name = medicineNameEditText.text.toString()
        medicineToSave.quantity = medicineQuantityEditText.text.toString().toDouble()
        medicineToSave.dailyUsage = medicineDailyUsageEditText.text.toString().toDouble()

        if (medicineToSave.name.toString().isNotBlank()) {
            medicineToSave.savedTime = timeProvider.getCurrentTimeInMillis()
            databaseHandler.createMedicine(medicineToSave)
            Toast.makeText(this, "Saving new medicine.", Toast.LENGTH_SHORT).show()
            NavUtils.navigateUpFromSameTask(this)
        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveEditedMedicine(medicine: Medicine) {
        if (medicine.id != null) {
            var medicineToUpdate = Medicine()

            medicineToUpdate.name = medicineNameEditText.text.toString()
            if (medicineToUpdate.name.toString().isNotBlank()) {
                medicineToUpdate.quantity = medicineQuantityEditText.text.toString().toDouble()
                medicineToUpdate.dailyUsage = medicineDailyUsageEditText.text.toString().toDouble()
                medicineToUpdate.savedTime = timeProvider.getCurrentTimeInMillis()
                medicineToUpdate.id = medicine.id

                databaseHandler.updateMedicine(medicineToUpdate)
                Toast.makeText(this, "Updating medicine, id: ${medicineToUpdate.id}", Toast.LENGTH_SHORT).show()
                NavUtils.navigateUpFromSameTask(this)
            }
        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }
}
