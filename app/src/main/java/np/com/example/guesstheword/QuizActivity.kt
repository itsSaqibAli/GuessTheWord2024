package np.com.example.guesstheword

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.content.Context
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.Toast
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import np.com.example.guesstheword.databinding.ActivityQuizBinding
import np.com.example.guesstheword.databinding.ScoreDialogBinding
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
    var hintClicked = 0;

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
            answerEdittext.setOnEditorActionListener { _, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                    // Hide the keyboard
                    hideKeyboard(answerEdittext)
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }
        }

        loadQuestions()
        startTimer()
    }


    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
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
            // Initialize all buttons with the text "Hint : Hint no"
            btn0.text = "Hint : 1"
            btn1.text = "Hint : 2"
            btn2.text = "Hint : 3"
            btn3.text = "Hint : 4"
        }
    }

    override fun onClick(view: View?) {

        if (view is Button) {
            hintClicked++
        }
        // Retrieve the text from the EditText and assign it to selectedAnswer
        selectedAnswer = binding.answerEdittext.text.toString()

        // Reset button backgrounds
        binding.apply {
            btn0.setBackgroundColor(getColor(R.color.blue))
            btn1.setBackgroundColor(getColor(R.color.blue))
            btn2.setBackgroundColor(getColor(R.color.blue))
            btn3.setBackgroundColor(getColor(R.color.blue))
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
            view.setBackgroundColor(getColor(androidx.appcompat.R.color.abc_background_cache_hint_selector_material_dark))

            // Display the options on the clicked button
            val options = questionModelList[currentQuestionIndex].options
            when (view.id) {
                R.id.btn0 -> binding.btn0.text = options[0]
                R.id.btn1 -> binding.btn1.text = options[1]
                R.id.btn2 -> binding.btn2.text = options[2]
                R.id.btn3 -> binding.btn3.text = options[3]
            }

        }
    }


    @SuppressLint("SetTextI18n")
    private fun finishQuiz(){
        val totalQuestions = questionModelList.size
        val finalScore = ((score.toFloat() * 5)-hintClicked.toFloat() + totalQuestions.toFloat() ).toInt()

        val dialogBinding  = ScoreDialogBinding.inflate(layoutInflater)
        dialogBinding.apply {
            scoreProgressIndicator.progress = finalScore
            scoreProgressText.text = "Score : $finalScore"
            if(finalScore >= ((totalQuestions*5)/2)){
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


















