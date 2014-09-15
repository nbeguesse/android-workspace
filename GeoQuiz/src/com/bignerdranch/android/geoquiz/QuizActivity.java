package com.bignerdranch.android.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends Activity {
	private static final String TAG = "QuizActivity";
	public static final String KEY_INDEX = "index";
	private Button mTrueButton;
	private Button mFalseButton;
	private ImageButton mNextButton;
	private ImageButton mPrevButton;
	private TextView mQuestionTextView;
	private int mCurrentIndex = 0;
	private Button mCheatButton;
	private boolean mIsCheater;
	
	private TrueFalse[] mQuestionBank = new TrueFalse[] {
			new TrueFalse(R.string.question_oceans, true),
			new TrueFalse(R.string.question_africa, false),
			new TrueFalse(R.string.question_americas, true),
			new TrueFalse(R.string.question_asia, true),
			new TrueFalse(R.string.question_mideast, false),
	};
	
	private void updateQuestion(){
		int stringId = mQuestionBank[mCurrentIndex].getQuestion();
		mQuestionTextView.setText(stringId);
		mIsCheater = false;
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState){
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
	}
	private void checkAnswer(boolean userPressedTrue){
		boolean answerIsTrue = mQuestionBank[mCurrentIndex].isTrueQuestion();
		int messageResId = 0;
		if(mIsCheater){
			messageResId = R.string.judgement_toast;
		} else {
			if(userPressedTrue == answerIsTrue){
				messageResId = R.string.correct_toast;
			} else {
				messageResId = R.string.incorrect_toast;
			}
		}
		Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(data==null){
			return;
		}
		mIsCheater = data.getBooleanExtra(CheatActivity.EXTRA_ANSWER_SHOWN, false);
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz); //this inflates the layout
        
        mQuestionTextView = (TextView)findViewById(R.id.question_text_view);
        if(savedInstanceState != null){
        	mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
        }
        updateQuestion();
        
        mTrueButton =(Button)findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				checkAnswer(true);
			}
		});
        mFalseButton=(Button)findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				checkAnswer(false);
			}
		});
        mNextButton = (ImageButton)findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCurrentIndex = (mCurrentIndex+1)%mQuestionBank.length;
				updateQuestion();
			}
		});
        mPrevButton = (ImageButton)findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCurrentIndex -= 1;
				if(mCurrentIndex < 0){ mCurrentIndex = mQuestionBank.length-1; }
				mCurrentIndex = (mCurrentIndex)%mQuestionBank.length;
				updateQuestion();
			}
		});
        
        mCheatButton = (Button)findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(QuizActivity.this, CheatActivity.class);
				boolean answerIsTrue = mQuestionBank[mCurrentIndex].isTrueQuestion();
				i.putExtra(CheatActivity.EXTRA_ANSWER_IS_TRUE, answerIsTrue);
				startActivityForResult(i, 0);
				
			}
		});
       
    }
    
    @Override
    public void onStart(){
    	super.onStart();
    	Log.d(TAG, "onStart() called");
    }

    @Override
    public void onPause(){
    	super.onPause();
    	Log.d(TAG, "onPause() called");
    }    
    
    @Override
    public void onResume(){
    	super.onResume();
    	Log.d(TAG, "onResume() called");
    }

    @Override
    public void onStop(){
    	super.onStop();
    	Log.d(TAG, "onStop() called");
    }  
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	Log.d(TAG, "onDestroy() called");
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.quiz, menu);
        return true;
    }
    
}
