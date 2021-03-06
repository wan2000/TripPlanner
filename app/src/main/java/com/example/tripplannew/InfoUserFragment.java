package com.example.tripplannew;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.tripplannew.data.webservice.Account;
import com.example.tripplannew.viewmodels.SettingViewModel;
import com.example.tripplannew.viewmodels.TripListViewModel;

import java.util.Calendar;

public class InfoUserFragment extends Fragment {

    private TextView mBtnBack;
    private EditText mShowName;
    private Button mEtDateOfBirth;
    private EditText mEtPlaceOfBirth;
    private Button mBtnSave;

    private TripListViewModel mTripListViewModel;
    private SettingViewModel mSettingViewModel;

    private Account mAccount;

    private DatePickerDialog.OnDateSetListener tvDateOfBirthSetListener;
    private String mDateOfBirth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mTripListViewModel = new ViewModelProvider(getActivity()).get(TripListViewModel.class);
        mSettingViewModel = new ViewModelProvider(getActivity()).get(SettingViewModel.class);

        return inflater.inflate(R.layout.fragment_info_user, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mBtnBack=getActivity().findViewById(R.id.btnBack_infouser);
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_infoUserFragment_to_settingFragment);
            }
        });

        mShowName = getActivity().findViewById(R.id.show_name_infouser);

        mEtDateOfBirth = getActivity().findViewById(R.id.etDateOfBirth_infouser);
        mEtDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendar(tvDateOfBirthSetListener);
            }
        });

        tvDateOfBirthSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month= month+1;
                mDateOfBirth = day+"/"+month+"/"+year;
                mEtDateOfBirth.setText(mDateOfBirth);
            }
        };


        mEtPlaceOfBirth = getActivity().findViewById(R.id.etPlaceOfBirth_infouser);
        Log.i("check", mTripListViewModel.getUserId());
        mSettingViewModel.getAccount(mTripListViewModel.getUserId()).observe(
                getActivity(), account -> {
                    mAccount = account;
                    mShowName.setText(account.getName());
                    if(account.getDateOfBirth() != null)
                    {
                        mEtDateOfBirth.setText(account.getDateOfBirth());
                    }
                    if(account.getPlaceOfBirth() != null)
                    {
                        mEtPlaceOfBirth.setText(account.getPlaceOfBirth());
                    }
                });

        mBtnSave = getActivity().findViewById(R.id.btnSave_infouser);
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mShowName.getText().toString();
                String dateOfBirth = mEtDateOfBirth.getText().toString();
                String placeOfBirth = mEtPlaceOfBirth.getText().toString();
//                mAccount.update(name, dateOfBirth, placeOfBirth);
                Account updatedAccount = new Account(mTripListViewModel.getUserId(), name, dateOfBirth, placeOfBirth);

                mSettingViewModel.updateProfile(updatedAccount).observe(getActivity(), status -> {
                    if(status == null)
                    {
                        Toast toast = Toast.makeText(getActivity(), "Lỗi đường truyền", Toast.LENGTH_LONG);
                        toast.show();
                    }
                    else if(status)
                    {
                        Toast toast = Toast.makeText(getActivity(), "Cập nhật thành công", Toast.LENGTH_LONG);
                        toast.show();
                        Navigation.findNavController(v).navigate(R.id.action_infoUserFragment_to_settingFragment);
                    }
                });
            }
        });
    }

    private void showCalendar(DatePickerDialog.OnDateSetListener tv){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(getActivity(),
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                tv,year,month,day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}
