package np.com.bimalkafle.quizonline

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import np.com.bimalkafle.quizonline.databinding.ActivityQuizBinding
import np.com.bimalkafle.quizonline.databinding.ScoreDialogBinding
import kotlin.math.min

class QuizActivity : AppCompatActivity(),View.OnClickListener {

    companion object {
        var questionModelList : List<QuestionModel> = listOf()
        var time : String = ""
    }

    lateinit var binding: ActivityQuizBinding

    var currentQuestionIndex = 0;
    var selectedAnswer = ""
    var score = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            btn0.setOnClickListener(this@QuizActivity)
            btn1.setOnClickListener(this@QuizActivity)
            btn2.setOnClickListener(this@QuizActivity)
            btn3.setOnClickListener(this@QuizActivity)
            nextBtn.setOnClickListener(this@QuizActivity)

            // Set the editor action listener for the answerEditText
            answerEdittext.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Simulate a click on the next button when Enter is pressed
                    onClick(nextBtn)
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }
        }

        loadQuestions()
        startTimer()
    }


    private fun startTimer(){
        val totalTimeInMillis = time.toInt() * 60 * 1000L
        object : CountDownTimer(totalTimeInMillis,1000L){
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished /1000
                val minutes = seconds/60
                val remainingSeconds = seconds % 60
                binding.timerIndicatorTextview.text = String.format("%02d:%02d", minutes,remainingSeconds)

            }

            override fun onFinish() {
                //Finish the quiz
            }

        }.start()
    }

    @SuppressLint("SetTextI18n")
    private fun loadQuestions(){
        selectedAnswer = ""
        if(currentQuestionIndex == questionModelList.size){
            finishQuiz()
            return
        }

        binding.apply {
            questionIndicatorTextview.text = "Question ${currentQuestionIndex+1}/ ${questionModelList.size} "
            questionProgressIndicator.progress =
                ( currentQuestionIndex.toFloat() / questionModelList.size.toFloat() * 100 ).toInt()
            questionTextview.text = questionModelList[currentQuestionIndex].question
            // Initialize all buttons with the text "Click to Display"
            btn0.text = "Hint : 1"
            btn1.text = "Hint : 2"
            btn2.text = "Hint : 3"
            btn3.text = "Hint : 4"
        }
    }

    override fun onClick(view: View?) {
        // Retrieve the text from the EditText and assign it to selectedAnswer
        selectedAnswer = binding.answerEdittext.text.toString()

        // Reset button backgrounds
        binding.apply {
            btn0.setBackgroundColor(getColor(R.color.gray))
            btn1.setBackgroundColor(getColor(R.color.gray))
            btn2.setBackgroundColor(getColor(R.color.gray))
            btn3.setBackgroundColor(getColor(R.color.gray))
        }

        // Check if next button is clicked or Enter is pressed
        if (view?.id == R.id.next_btn || view?.id == R.id.answer_edittext) {
            // Next button is clicked or Enter is pressed
            if (selectedAnswer.isEmpty()) {
                // Show toast if no answer is selected
                Toast.makeText(applicationContext, "Please select answer to continue", Toast.LENGTH_SHORT).show()
                return;
            }
            // Check if selected answer is correct and update score
            if (selectedAnswer == questionModelList[currentQuestionIndex].correct) {
                score++
                Log.i("Score of quiz", score.toString())
            }
            // Move to the next question
            currentQuestionIndex++
            loadQuestions()
        } else {
            // Options button is clicked
            // Highlight the clicked button and update selectedAnswer
            selectedAnswer = (view as Button).text.toString()
            view.setBackgroundColor(getColor(R.color.orange))

            // Display the options on the clicked button
            val options = questionModelList[currentQuestionIndex].options
            when (view.id) {
                R.id.btn0 -> binding.btn0.text = options[0]
                R.id.btn1 -> binding.btn1.text = options[1]
                R.id.btn2 -> binding.btn2.text = options[2]
                R.id.btn3 -> binding.btn3.text = options[3]
            }

            // Hide the other buttons' text
//            val allButtons = listOf(binding.btn0, binding.btn1, binding.btn2, binding.btn3)
//            val clickedButtonIndex = allButtons.indexOf(view)
//            allButtons.forEachIndexed { index, button ->
//                if (index != clickedButtonIndex) {
//                    button.text = "Hint"
//                }
//            }

            // Display the text of the clicked button in the answer EditText
//            binding.answerEdittext.setText(selectedAnswer)
        }
    }


    private fun finishQuiz(){
        val totalQuestions = questionModelList.size
        val percentage = ((score.toFloat() / totalQuestions.toFloat() ) *100 ).toInt()

        val dialogBinding  = ScoreDialogBinding.inflate(layoutInflater)
        dialogBinding.apply {
            scoreProgressIndicator.progress = percentage
            scoreProgressText.text = "$percentage %"
            if(percentage>60){
                scoreTitle.text = "Congrats! You have passed"
                scoreTitle.setTextColor(Color.BLUE)
            }else{
                scoreTitle.text = "Oops! You have failed"
                scoreTitle.setTextColor(Color.RED)
            }
            scoreSubtitle.text = "$score out of $totalQuestions are correct"
            finishBtn.setOnClickListener {
                finish()
            }
        }

        AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .show()

    }
}


















