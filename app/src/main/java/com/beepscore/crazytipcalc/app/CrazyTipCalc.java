package com.beepscore.crazytipcalc.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;


public class CrazyTipCalc extends Activity {

    // Constants used when saving and restoring

    private static final String TOTAL_BILL = "TOTAL_BILL";
    private static final String CURRENT_TIP = "CURRENT_TIP";
    private static final String BILL_WITHOUT_TIP = "BILL_WITHOUT_TIP";

    private double billBeforeTip;
    private double tipAmount;
    private double finalBill;

    EditText billBeforeTipET;
    EditText tipAmountET;
    EditText finalBillET;

    private int[] checklistValues = new int[12];
    CheckBox friendlyCheckBox;
    CheckBox specialsCheckBox;
    CheckBox opinionCheckBox;

    RadioGroup availableRadioGroup;
    RadioButton availableBadRadio;
    RadioButton availableOKRadio;
    RadioButton availableGoodRadio;

    Spinner problemsSpinner;

    Button startChronometerButton;
    Button pauseChronometerButton;
    Button resetChronometerButton;

    Chronometer timeWaitingChronometer;

    long secondsYouWaited = 0;

    TextView timeWaitingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crazy_tip_calc);

        if (null == savedInstanceState) {
            // app has just started
            billBeforeTip = 0.0;
            tipAmount = 0.15;
            finalBill = 0.0;
        } else {
            // app is being restored
            billBeforeTip = savedInstanceState.getDouble(BILL_WITHOUT_TIP);
            tipAmount = savedInstanceState.getDouble(CURRENT_TIP);
            finalBill = savedInstanceState.getDouble(TOTAL_BILL);
        }

        billBeforeTipET = (EditText) findViewById(R.id.billEditText);
        tipAmountET = (EditText) findViewById(R.id.tipEditText);
        finalBillET = (EditText) findViewById(R.id.finalBillEditText);

        tipSeekBar = (SeekBar) findViewById(R.id.changeTipSeekBar);
        tipSeekBar.setOnSeekBarChangeListener(tipSeekBarListener);

        billBeforeTipET.addTextChangedListener(billBeforeTipListener);

        friendlyCheckBox = (CheckBox) findViewById(R.id.friendlyCheckBox);
        specialsCheckBox = (CheckBox) findViewById(R.id.specialsCheckBox);
        opinionCheckBox = (CheckBox) findViewById(R.id.opinionCheckBox);

        setUpIntroCheckBoxes();

        availableRadioGroup = (RadioGroup) findViewById(R.id.availableRadioGroup);
        availableBadRadio = (RadioButton) findViewById(R.id.availableBadRadio);
        availableOKRadio = (RadioButton) findViewById(R.id.availableOKRadio);
        availableGoodRadio = (RadioButton) findViewById(R.id.availableGoodRadio);

        addChangeListenerToRadios();

        problemsSpinner = (Spinner) findViewById(R.id.problemsSpinner);
        problemsSpinner.setPrompt("Problem Solving");

        addItemSelectedListenerToSpinner();

        startChronometerButton = (Button) findViewById(R.id.startChronometerButton);
        pauseChronometerButton = (Button) findViewById(R.id.pauseChronometerButton);
        resetChronometerButton = (Button) findViewById(R.id.resetChronometerButton);

        setButtonOnClickListeners();

        timeWaitingChronometer = (Chronometer) findViewById(R.id.timeWaitingChronometer);

        timeWaitingTextView = (TextView) findViewById(R.id.timeWaitingTextView);
    }

    private TextWatcher billBeforeTipListener = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            try {
                billBeforeTip = Double.parseDouble(charSequence.toString());
            } catch (NumberFormatException e) {
                billBeforeTip = 0.0;
            }
            updateTipAndFinalBill();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void updateTipAndFinalBill() {
        // Model
        double tipAmount = Double.parseDouble(tipAmountET.getText().toString());
        double finalBill = billBeforeTip + (tipAmount * billBeforeTip);
        // View
        finalBillET.setText(String.format("%.02f", finalBill));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putDouble(TOTAL_BILL, finalBill);
        outState.putDouble(CURRENT_TIP, tipAmount);
        outState.putDouble(BILL_WITHOUT_TIP, billBeforeTip);
    }

    private SeekBar tipSeekBar;
    private OnSeekBarChangeListener tipSeekBarListener = new OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            tipAmount = (tipSeekBar.getProgress() * 0.01);
            tipAmountET.setText(String.format("%.02f", tipAmount));
            updateTipAndFinalBill();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private void setUpIntroCheckBoxes() {

        friendlyCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            // https://developer.android.com/reference/android/widget/CompoundButton.html
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checklistValues[0] = (friendlyCheckBox.isChecked())?4:0;
                setTipFromWaitressChecklist();
                updateTipAndFinalBill();
            }
        });

        specialsCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checklistValues[1] = (specialsCheckBox.isChecked())?1:0;
                setTipFromWaitressChecklist();
                updateTipAndFinalBill();
            }
        });

        opinionCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checklistValues[2] = (opinionCheckBox.isChecked())?2:0;
                setTipFromWaitressChecklist();
                updateTipAndFinalBill();
            }
        });
    }

    private void setTipFromWaitressChecklist() {
        int checklistTotal = 0;

        for (int item : checklistValues) {
            checklistTotal += item;
        }
        tipAmountET.setText(String.format("%.02f", checklistTotal * 0.01));
    }

    private void addChangeListenerToRadios() {

        availableRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            public void onCheckedChanged(RadioGroup group, int checkedId) {
                checklistValues[3] = (availableBadRadio.isChecked())?-1:0;
                checklistValues[4] = (availableOKRadio.isChecked())?2:0;
                checklistValues[5] = (availableGoodRadio.isChecked())?4:0;

                setTipFromWaitressChecklist();
                updateTipAndFinalBill();
            }
        });

    }

    private void addItemSelectedListenerToSpinner(){
        problemsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView <?> arg0, View arg1,
                    int arg2, long arg3) {
                checklistValues[6] = (String.valueOf(problemsSpinner.getSelectedItem()).equals("Bad"))?-1:0;
                checklistValues[7] = (String.valueOf(problemsSpinner.getSelectedItem()).equals("OK"))?3:0;
                checklistValues[8] = (String.valueOf(problemsSpinner.getSelectedItem()).equals("Good"))?6:0;
                // Calculate tip using the waitress checklist options
                setTipFromWaitressChecklist();
                // Update all the other EditTexts
                updateTipAndFinalBill();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void setButtonOnClickListeners(){

        startChronometerButton.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View arg0) {
                int stoppedMilliseconds = 0;
                String chronoText = timeWaitingChronometer.getText().toString();
                String array[] = chronoText.split(":");
                if (array.length == 2) {
                    // Find the seconds
                  stoppedMilliseconds = Integer.parseInt(array[0]) * 60 * 1000
                        + Integer.parseInt(array[1]) * 1000;
                } else if (array.length == 3) {
                    // Find the minutes
                  stoppedMilliseconds = Integer.parseInt(array[0]) * 60 * 60 * 1000
                        + Integer.parseInt(array[1]) * 60 * 1000
                        + Integer.parseInt(array[2]) * 1000;
                }

                // Amount of time elapsed since the start button was
                // pressed, minus the time paused
                timeWaitingChronometer.setBase(SystemClock.elapsedRealtime() - stoppedMilliseconds);

                // Set the number of seconds you have waited
                // This would be set for minutes in the real world
                // obviously. That can be found in array[2]
                secondsYouWaited = Long.parseLong(array[1]);
                updateTipBasedOnTimeWaited(secondsYouWaited);

                timeWaitingChronometer.start();
            }
        });

        pauseChronometerButton.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View arg0) {
                timeWaitingChronometer.stop();
            }
        });

        resetChronometerButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                timeWaitingChronometer.setBase(SystemClock.elapsedRealtime());
                secondsYouWaited = 0;
            }
        });
    }

    private void updateTipBasedOnTimeWaited(long secondsYouWaited){
        checklistValues[9] = (secondsYouWaited > 10)?-2:2;
        setTipFromWaitressChecklist();
        updateTipAndFinalBill();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.crazy_tip_calc, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
