package com.tieto.incubator2017.notificationapp.screentextsender;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tieto.incubator2017.notificationapp.R;
import com.tieto.incubator2017.notificationapp.boardcontroller.ScreenControllerJni;


public class SendTextFragment extends Fragment {

    public static final int SEND_MESSAGE_FAILED = R.string.send_message_failed;
    private EditText mEditText;
    private Button mButtonSend;
    private Button mButtonClear;
    private ScreenControllerJni mScreenController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_send_text, container, false);
        mScreenController = new ScreenControllerJni();
        mEditText = (EditText)view.findViewById(R.id.user_text);
        mButtonSend = (Button)view.findViewById(R.id.button_send);
        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = mEditText.getText().toString();
                if (mScreenController.sendMessage(string) != 0) {
                    displayToastMessage(SEND_MESSAGE_FAILED);
                }
                mEditText.setText("");
            }
        });
        mButtonClear = (Button)view.findViewById(R.id.button_clear);
        mButtonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScreenController.clearScreen();
            }
        });
        return view;
    }

    private void displayToastMessage(int message){
        Toast.makeText(getContext(), getString(message), Toast.LENGTH_SHORT).show();
    }
}
