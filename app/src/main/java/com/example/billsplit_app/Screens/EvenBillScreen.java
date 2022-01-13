package com.example.billsplit_app.Screens;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.billsplit_app.Adapters.ProfileAdapter;
import com.example.billsplit_app.Adapters.TipAdapter;
import com.example.billsplit_app.MainActivity;
import com.example.billsplit_app.R;
import com.example.billsplit_app.User;

public class EvenBillScreen extends AppCompatActivity {

    ProfileAdapter ProfileViewAdapter;
    TipAdapter TipViewAdapter;
    RecyclerView ProfileRecyclerView;
    RecyclerView EvenTipRecyclerView;
    Boolean popupShown = false;
    Boolean same_tip = false;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.even_bill_screen);

        ImageButton backButton = findViewById(R.id.back_button);
        ImageButton addUserButton = findViewById(R.id.add_user_button);
        Button submitButton = findViewById(R.id.submit_button);

        CheckBox sameTipButton = findViewById(R.id.same_tip_button);
        View same_tip_selection = findViewById(R.id.same_tip_selection);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_welcome_screen();
            }
        });

        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowPopup(v);
                CheckPopup();
            }
        });

        sameTipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sameTipButton.isChecked()) {
                    same_tip_selection.setVisibility(View.VISIBLE);
                }
                else{
                    same_tip_selection.setVisibility(View.INVISIBLE);
                }

            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_even_final_screen();


            }
        });

        setupRecyclerView();
    }

    void setupRecyclerView() {
        ProfileViewAdapter = new ProfileAdapter(this, MainActivity.usersList);
        TipViewAdapter = new TipAdapter();

        ProfileRecyclerView = findViewById(R.id.profile_list_view);
        ProfileRecyclerView.setAdapter(ProfileViewAdapter);
        ProfileRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        EvenTipRecyclerView = findViewById(R.id.even_tip_list_view);
        EvenTipRecyclerView.setAdapter(TipViewAdapter);
        EvenTipRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }

    public void ShowPopup(View view) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.add_profile_popup, null,false);
        popupShown = true;
        CheckPopup();

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                popupShown = false;
                CheckPopup();
                return true;
            }
        });

        EditText addProfileNameEditText = popupView.findViewById(R.id.add_profile_name_edit_text);
        Button addProfileNameSubmitButton = popupView.findViewById(R.id.add_profile_name_submit_button);

        addProfileNameSubmitButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                MainActivity.usersList.add(new User( addProfileNameEditText.getText().toString()));
                ProfileViewAdapter.notifyDataSetChanged();
                TipViewAdapter.notifyDataSetChanged();
                popupWindow.dismiss();
                popupShown = false;
                CheckPopup();
            }
        });
    }

    private void open_welcome_screen() {
        Intent open_welcome_screen = new Intent(this, WelcomeScreen.class);
        startActivity(open_welcome_screen);
    }

    private void open_even_final_screen() {
        Intent open_even_final_screen = new Intent(this, EvenFinalScreen.class);
        startActivity(open_even_final_screen);
    }

    public void CheckPopup() {
    }
}