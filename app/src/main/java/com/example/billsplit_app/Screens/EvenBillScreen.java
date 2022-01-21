package com.example.billsplit_app.Screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.billsplit_app.Adapters.ProfileAdapter;
import com.example.billsplit_app.Adapters.TipAdapter;
import com.example.billsplit_app.InternalFiles;
import com.example.billsplit_app.MainActivity;
import com.example.billsplit_app.R;
import com.example.billsplit_app.User;

import org.json.JSONException;

public class EvenBillScreen extends AppCompatActivity {

    ProfileAdapter ProfileViewAdapter;
    TipAdapter TipViewAdapter;
    RecyclerView ProfileRecyclerView;
    RecyclerView EvenTipRecyclerView;
    TextView current_total;
    int allTip = 0;
    String customTip = "";

    @SuppressLint({"ResourceType", "SetTextI18n", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.even_bill_screen);

        MainActivity.allTipsSelected = false;
        MainActivity.usersList.add(new User("Me"));
        if (MainActivity.nOfUsers > 1) {
            for (int i = 1; i < MainActivity.nOfUsers; i++) {
                MainActivity.usersList.add(new User("Person " + i));
            }
        }

        ImageButton backButton = findViewById(R.id.back_button);
        ImageButton addUserButton = findViewById(R.id.add_user_button);
        Button submitButton = findViewById(R.id.submit_button);
        CheckBox sameTipButton = findViewById(R.id.same_tip_button);

        View same_tip_selection = findViewById(R.id.same_tip_selection);
        current_total = findViewById(R.id.even_current_total);
        EditText sameTipEditText = findViewById(R.id.even_tip_edit_text);
        ImageButton sameTipPopupButton = findViewById(R.id.even_transit_enter_exit);

        try {
            current_total.setText("$ " + String.format("%.2f",InternalFiles.getSavedCost()));
        } catch (JSONException e) {
            e.printStackTrace();
        }


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
                TipViewAdapter.notifyDataSetChanged();
            }
        });

        sameTipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sameTipEditText.getText().clear();
                if (sameTipButton.isChecked()) {
                    same_tip_selection.setVisibility(View.VISIBLE);

                    MainActivity.allTipsSelected = true;
                } else {
                    same_tip_selection.setVisibility(View.INVISIBLE);
                    MainActivity.allTipsSelected = false;
                }
                allTip = 0;
                try {
                    refreshPrereqs();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    MainActivity.finalTipTotal = InternalFiles.getSavedCost();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                current_total.setText("$ " + String.format("%.2f",MainActivity.finalTipTotal));
                TipViewAdapter.notifyDataSetChanged();
            }
        });

        sameTipEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    allTip = Integer.parseInt(s.toString());
                } else {
                    allTip = 0;
                }

                try {
                    refreshPrereqs();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    MainActivity.finalTipTotal = InternalFiles.getSavedCost() + updateTipsPercentage();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                current_total.setText("$ " + String.format("%.2f",MainActivity.finalTipTotal));
                TipViewAdapter.notifyDataSetChanged();
            }
        });

        sameTipPopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowTipAllPopup(v,sameTipEditText);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (User user : MainActivity.usersList) {
                    try {
                        user.refreshTotalEven();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    open_final_screen();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        setupRecyclerView();
    }

    void setupRecyclerView() {
        ProfileViewAdapter = new ProfileAdapter(this, MainActivity.usersList);
        TipViewAdapter = new TipAdapter(this,current_total);

        ProfileRecyclerView = findViewById(R.id.profile_list_view);
        ProfileRecyclerView.setAdapter(ProfileViewAdapter);
        ProfileRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        EvenTipRecyclerView = findViewById(R.id.even_tip_list_view);
        EvenTipRecyclerView.setAdapter(TipViewAdapter);
        EvenTipRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }

    public void refreshPrereqs() throws JSONException {
        for (User u : MainActivity.usersList) {
            u.setTipsPercentage(allTip);
            u.refreshTotalEven();
        }
    }

    public double updateTipsPercentage() {
        double total = 0.0;
        for (User u : MainActivity.usersList) {
            total += u.getTips_times_total();
        }
        return total;
    }

    public void ShowPopup(View view) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.add_profile_popup, null, false);
        CheckPopup();

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
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
                MainActivity.usersList.add(new User(addProfileNameEditText.getText().toString()));
                ProfileViewAdapter.notifyDataSetChanged();
                TipViewAdapter.notifyDataSetChanged();
                popupWindow.dismiss();
                CheckPopup();
            }
        });
    }

    private void open_welcome_screen() {
        Intent open_welcome_screen = new Intent(this, WelcomeScreen.class);
        startActivity(open_welcome_screen);
    }

    private void open_final_screen() throws JSONException {
        Intent open_even_final_screen = new Intent(this, FinalScreen.class);
        startActivity(open_even_final_screen);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void ShowTipAllPopup(View view, EditText tipText) {
        View popupView = LayoutInflater.from(view.getContext()).inflate(R.layout.open_tip_popup, null, false);
        CheckPopup();
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                CheckPopup();
                return true;
            }
        });

        ImageView popupBackground = popupView.findViewById(R.id.popup_background);
        ImageButton popupCloseButton = popupView.findViewById(R.id.back_button);
        ImageView zeroButton = popupView.findViewById(R.id.tip_button1);
        ImageView tenButton = popupView.findViewById(R.id.tip_button2);
        ImageView twelveButton = popupView.findViewById(R.id.tip_button3);
        ImageView fifteenButton = popupView.findViewById(R.id.tip_button4);
        EditText popupTipText = popupView.findViewById(R.id.popup_tip_edit_text);
        Button popupSubmitButton = popupView.findViewById(R.id.popup_submit_button);

        popupBackground.setOnClickListener(v -> {
            // This is just here to prevent popup from closing when clicking the background
        });

        popupCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        zeroButton.setOnClickListener(v -> {
            tipText.setText("0");
            TipViewAdapter.notifyDataSetChanged();
            popupWindow.dismiss();
            CheckPopup();
        });

        tenButton.setOnClickListener(v -> {
            tipText.setText("10");
            TipViewAdapter.notifyDataSetChanged();
            popupWindow.dismiss();
            CheckPopup();
        });

        twelveButton.setOnClickListener(v -> {
            tipText.setText("12");
            TipViewAdapter.notifyDataSetChanged();
            popupWindow.dismiss();
            CheckPopup();
        });

        fifteenButton.setOnClickListener(v -> {
            tipText.setText("15");
            TipViewAdapter.notifyDataSetChanged();
            popupWindow.dismiss();
            CheckPopup();
        });

        popupSubmitButton.setOnClickListener(v -> {
            tipText.setText(customTip);
            TipViewAdapter.notifyDataSetChanged();
            popupWindow.dismiss();
            CheckPopup();
        });

        popupTipText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    customTip = s.toString();
                }
                else {
                    customTip = "";
                }
            }
        });
    }

    public void CheckPopup() {
    }
}
