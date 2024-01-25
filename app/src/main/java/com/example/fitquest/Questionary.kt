import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.fitquest.AuthManager
import com.example.fitquest.R
import com.example.fitquest.databinding.ActivityQuestionaryBinding

class Questionary : AppCompatActivity() {

    private lateinit var binding: ActivityQuestionaryBinding

    private lateinit var radioGroupGoal: RadioGroup
    private lateinit var radioGroupMotivation: RadioGroup
    private lateinit var radioGroupPushUps: RadioGroup
    private lateinit var radioGroupActivityLevel: RadioGroup
    private lateinit var spinnerFirstDay: Spinner
    private lateinit var numberPickerTrainingDays: NumberPicker
    private lateinit var buttonSubmit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        radioGroupGoal = findViewById(R.id.radioGroupGoal)
        radioGroupMotivation = findViewById(R.id.radioGroupMotivation)
        radioGroupPushUps = findViewById(R.id.radioGroupPushUps)
        radioGroupActivityLevel = findViewById(R.id.radioGroupActivityLevel)
        spinnerFirstDay = findViewById(R.id.spinnerFirstDay)
        numberPickerTrainingDays = findViewById(R.id.numberPickerTrainingDays)
        buttonSubmit = findViewById(R.id.buttonSubmit)

        numberPickerTrainingDays.minValue = 0
        numberPickerTrainingDays.maxValue = 7
        numberPickerTrainingDays.wrapSelectorWheel = true

        val adapter = ArrayAdapter.createFromResource(
            this, R.array.days_of_week, android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFirstDay.adapter = adapter

        buttonSubmit.setOnClickListener {
            val selectedGoal = findViewById<RadioButton>(radioGroupGoal.checkedRadioButtonId)?.text.toString()
            val selectedMotivation = findViewById<RadioButton>(radioGroupMotivation.checkedRadioButtonId)?.text.toString()
            val selectedPushUps = findViewById<RadioButton>(radioGroupPushUps.checkedRadioButtonId)?.text.toString()
            val selectedActivityLevel = findViewById<RadioButton>(radioGroupActivityLevel.checkedRadioButtonId)?.text.toString()
            val selectedFirstDay = spinnerFirstDay.selectedItem.toString()
            val trainingDays = numberPickerTrainingDays.value

        }
    }
}
