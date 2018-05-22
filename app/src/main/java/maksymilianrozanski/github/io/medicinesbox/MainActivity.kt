package maksymilianrozanski.github.io.medicinesbox

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import maksymilianrozanski.github.io.medicinesbox.data.MedicinesAdapter
import maksymilianrozanski.github.io.medicinesbox.data.MedicinesDatabaseHandler
import maksymilianrozanski.github.io.medicinesbox.model.Medicine
import java.util.*

class MainActivity : AppCompatActivity() {

    private var adapter: MedicinesAdapter? = null
    private var medicineListFromDb: ArrayList<Medicine>? = null
    private var medicineListForAdapter: ArrayList<Medicine>? = null

    private var layoutManger: RecyclerView.LayoutManager? = null
    var databaseHandler: MedicinesDatabaseHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        databaseHandler = MedicinesDatabaseHandler(this)

        medicineListFromDb = ArrayList()
        medicineListForAdapter = ArrayList()
        layoutManger = LinearLayoutManager(this)
        adapter = MedicinesAdapter(medicineListForAdapter!!, this)

        recyclerViewId.layoutManager = layoutManger
        recyclerViewId.adapter = adapter

        reloadAdapterDataFromDb()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.addExampleItem -> {
                addExampleItem()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun reloadAdapterDataFromDb() {
        medicineListFromDb = databaseHandler!!.readMedicines()
        adapter!!.setList(medicineListFromDb!!)
    }

    private fun addExampleItem() {
        var exampleMedicine = Medicine()
        exampleMedicine.name = "Example name"
        var random = Random()
        var randomInt = random.nextInt(10)
        exampleMedicine.quantity = randomInt
        exampleMedicine.dailyUsage = 1
        exampleMedicine.savedTime = System.currentTimeMillis()
        databaseHandler!!.createMedicine(exampleMedicine)

        reloadAdapterDataFromDb()

        Toast.makeText(this, "Adding new example item", Toast.LENGTH_SHORT).show()
    }
}
