package com.company.PCBuilder.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.company.PCBuilder.R;
import com.company.PCBuilder.activities.MainActivity;
import com.company.PCBuilder.adapters.AccountTabListAdapter;
import com.google.firebase.auth.FirebaseAuth;

public class AccountFragment extends Fragment {
    private static final String[] functions = {"Setting", "About Us", "Current Version"};
    private static final int[] functionsPic = {R.drawable.setting, R.drawable.aboutus, R.drawable.version};
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private Context context;
    private TextView accountName, status, deposit;
    private ImageView picture;
    private Button logoutButton;
    private ListView listView;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.account_layout, container, false);

        // Connect with ID
        accountName = layout.findViewById(R.id.username);
        status = layout.findViewById(R.id.status);
        deposit = layout.findViewById(R.id.deposit);
        logoutButton = layout.findViewById(R.id.log_out_button);
        listView = layout.findViewById(R.id.account_list);
        picture = layout.findViewById(R.id.picture);

        // Logout Button pressed
        logoutButton.setOnClickListener(logoutListener);

        AccountTabListAdapter adapter = new AccountTabListAdapter(context, functions, functionsPic);
        listView.setAdapter(adapter);
        setListViewScrollAndHeight(adapter, listView);

        picture.setImageResource(R.drawable.account_icon);

        String email = ((MainActivity) context).getEmail();
        String accountType = ((MainActivity) context).getAccountType();

        accountName.setText(email);
        status.setText("Status: " + accountType);

        listView.setOnItemClickListener(itemClickListener);

        return layout;
    }

    // Action of logout button
    private View.OnClickListener logoutListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Sig nout the Firebase auth
            auth.signOut();

            getActivity().finish();
        }
    };

    private void setListViewScrollAndHeight(AccountTabListAdapter adapter, ListView listView) {
        // Set the ListView not move/scroll
        listView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });

        // Set the Height
        if (adapter != null) {
            int totalHeight = 0;
            for (int i = 0; i < adapter.getCount(); i++) {
                View listItem = adapter.getView(i, null, listView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
            listView.setLayoutParams(params);
            // listView.requestLayout();
        }
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) {
                Toast.makeText(context,"Please check in next version",Toast.LENGTH_LONG).show();
            } else if (position == 1) {
                Toast.makeText(context,"We are GROUP 14 in CS 440:\n\n" +
                        " Samuel Kajah, Peiqi Wang,\n Kevin Balcerzak, Mohammed Sami",Toast.LENGTH_LONG).show();
            } else if (position == 2){
                Toast.makeText(context,"Version: 1.0.0.3",Toast.LENGTH_SHORT).show();
            }

        }
    };

}