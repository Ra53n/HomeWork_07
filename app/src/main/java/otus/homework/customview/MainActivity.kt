package otus.homework.customview

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    private var showPieChart: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pieChartView = findViewById<PieChartView>(R.id.pie_chart_view)
        val chartView = findViewById<ChartView>(R.id.chart_view)
        val buttonSwitchChart = findViewById<Button>(R.id.button_switch_chart)

        val jsonString =
            resources.openRawResource(R.raw.payload).bufferedReader().use { it.readText() }
        val gson = Gson()
        val payloads = mutableListOf<Payload>()
        payloads.addAll(gson.fromJson(jsonString, Array<Payload>::class.java))

        pieChartView.setPayloads(payloads)
        pieChartView.setSectorClickListener {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        chartView.setPayloads(payloads)

        buttonSwitchChart.setOnClickListener {
            showPieChart = !showPieChart
            if (showPieChart) {
                pieChartView.visibility = View.VISIBLE
                chartView.visibility = View.GONE
            } else {
                pieChartView.visibility = View.GONE
                chartView.visibility = View.VISIBLE
            }
        }
    }
}